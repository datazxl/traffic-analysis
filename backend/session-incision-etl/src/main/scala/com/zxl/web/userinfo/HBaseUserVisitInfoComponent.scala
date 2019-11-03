package com.zxl.web.userinfo
import com.zxl.web.CombinedId
import org.apache.hadoop.hbase.{Cell, CellUtil, TableName}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes

object HBaseUserVisitInfoComponent extends UserVisitInfoComponent {

  private val tableName = TableName.valueOf(System.getProperty("web.etl.hbase.namespace", "default"), System.getProperty("web.etl.hbase.UserTableName", "web-user"))
  private val columnFamily = Bytes.toBytes("f")
  private val columnQualifier = Bytes.toBytes("v")

  /**
    * 根据访客唯一标识查询访客的历史访问信息
    *
    * @param ids
    * @return
    */
  override def findUserVisitInfo(ids: Seq[CombinedId]): Map[CombinedId, UserVisitInfo] = {
    // 1.获取连接
    val conn: Connection = HBaseConnectionFactory.getHbaseConn
    // 2.获取table,构建get,得到结果
    val table: Table = conn.getTable(tableName)
    val gets: Seq[Get] = ids.map(id => {
      new Get(Bytes.toBytes(id.encode)).addColumn(columnFamily, columnQualifier)
    })
    import scala.collection.JavaConversions._
    val results: Array[Result] = table.get(gets)
    // 3.将结果组成Map[CombinedId, UserVisitInfo]
    val resOpt: Seq[Option[(CombinedId, UserVisitInfo)]] = ids zip results map{ case (id, res) => {
      if (res.isEmpty) {
        None
      } else {
        val cell: Cell = res.getColumnLatestCell(columnFamily, columnQualifier)
        Some(id, UserVisitInfo(id, cell.getTimestamp, Bytes.toInt(CellUtil.cloneValue(cell))))
      }
    }}

    table.close()
    resOpt.flatten.toMap
  }

  /**
    * 更新用户的历史访问行为数据信息
    *
    * @param users
    */
  override def updateUserVisitInfo(users: Seq[UserVisitInfo]): Unit = {
    //1.获取连接
    val conn: Connection = HBaseConnectionFactory.getHbaseConn
    //2.获取table，构建put并执行
    val table: Table = conn.getTable(tableName)
    val puts: Seq[Put] = users.map(user => {
      new Put(Bytes.toBytes(user.id.encode)).addColumn(columnFamily, columnQualifier, user.lastVisitTime, Bytes.toBytes(user.lastVisitCount))
    })
    import scala.collection.JavaConversions._
    table.put(puts)

    table.close()
  }
}
