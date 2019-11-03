package com.zxl.web.userinfo

import com.zxl.web.CombinedId
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client.{Admin, Connection}
import org.apache.hadoop.hbase.util.Bytes
import org.scalatest.FunSuite

class HBaseUserVisitInfoComponentTest extends FunSuite {

  System.setProperty("web.etl.hbase.zk.quorums", "master,slave1,slave2")
  System.setProperty("web.etl.hbase.UserTableName", "web-user-test")
  private val conn: Connection = HBaseConnectionFactory.getHbaseConn
  private val admin: Admin = conn.getAdmin

  private val hTableDescriptor = new HTableDescriptor(TableName.valueOf("web-user-test"))
  hTableDescriptor.addFamily(new HColumnDescriptor(Bytes.toBytes("f")))

  if (admin.tableExists(TableName.valueOf("web-user-test"))){
    admin.disableTable(TableName.valueOf("web-user-test"))
    admin.deleteTable(TableName.valueOf("web-user-test"))
  }
  admin.createTable(hTableDescriptor)


  test("testFindUserVisitInfo") {
    val infos = Seq(UserVisitInfo(CombinedId(1, "user1"), 666, 1), UserVisitInfo(CombinedId(2, "user2"), 777, 2))
    HBaseUserVisitInfoComponent.updateUserVisitInfo(infos)

    val ids = Seq(CombinedId(1,"user1"), CombinedId(2, "user2"))
    val userInfos: Map[CombinedId, UserVisitInfo] = HBaseUserVisitInfoComponent.findUserVisitInfo(ids)

    assert(userInfos.size == 2)
    val userVisitInfo1: UserVisitInfo = userInfos.get(CombinedId(1,"user1")).get
    assert(userVisitInfo1.lastVisitTime == 666)
    assert(userVisitInfo1.lastVisitCount == 1)
    val userVisitInfo2: UserVisitInfo = userInfos.get(CombinedId(2,"user2")).get
    assert(userVisitInfo2.lastVisitTime == 777)
    assert(userVisitInfo2.lastVisitCount == 2)
  }

}
