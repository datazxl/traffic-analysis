#!/bin/bash
yesterday=$1
if [ -z ${yesterday} ]; then
    yesterday=`date -d"1 days ago" +%Y%m%d`
fi
year=${yesterday:0:4}
month=${yesterday:0:6}
day=${yesterday}

rm transform_${day}.sql

for table_name in Session PageView Conversion Heartbeat MouseClick; do
    lower_table_name=`echo ${table_name} | tr "A-Z" "a-z"`
    echo "starting  ${lower_table_name} transformed"
    #1.创建临时表
    create_temp_table_sql="CREATE TEMPORARY EXTERNAL TABLE IF NOT EXISTS web_temp.${lower_table_name}_avro_tmp
    STORED AS avro
    LOCATION 'hdfs://master:9000/user/traffic-analysis/web/${lower_table_name}/year=${year}/month=${month}/day=${day}'
    TBLPROPERTIES (
        'avro.schema.url'='hdfs://master:9000/user/traffic-analysis/avro/${table_name}.avsc'
    );"
    #2.从临时表读取并插入数据到最终表
    insert_overwrite_table_sql="INSERT OVERWRITE TABLE web.${lower_table_name} PARTITION(year=${year}, month=${month}, day=${day}) SELECT tmp.* FROM web_temp.${lower_table_name}_avro_tmp tmp;"

    echo ${create_temp_table_sql} >> transform_${day}.sql
    echo ${insert_overwrite_table_sql} >> transform_${day}.sql
done

echo "exit;" >> transform_${day}.sql
hive -f transform_${day}.sql
