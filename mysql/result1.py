# 准备json文件，里面的内容为{"status":0,"hq":"2022-04-25","code":"cn_000002"}
import json
import pymysql

db = pymysql.connect(host='localhost',
                     user='root',
                     password='hadoop',
                     database='result1')
# 使用 cursor() 方法创建一个游标对象 cursor
cursor = db.cursor()
table_sql = "DROP TABLE IF EXISTS result_data1"
# 使用 execute() 方法执行 SQL，如果表存在则删除
cursor.execute(table_sql)
# 使用预处理语句创建表
table_sql = "CREATE TABLE result_data1(id bigint NOT NULL AUTO_INCREMENT,action  CHAR(6),count  CHAR(10), primary KEY (id) USING BTREE) "
# 执行建表与插入sql
cursor.execute(table_sql)
# 提交到数据库执行
db.commit()
# 打开json文件
with open("/usr/local/hadoop/IdeaProjects/UserBehavior_Analyze/web/static/result1.json", 'r',
          encoding='utf_8_sig') as f:
    # 读取json文件
    for line in f.readlines():
        if (len(line) <= 1):
            continue;
        # 读取的json文件为字典类型
        dic = json.loads(line)
        for i in dic:
            # 拼接key值
            keys = ','.join(i.keys())
            print(keys)
            # 将value值存为元组类型
            valuesTuple = tuple(i.values())

            # 拼接values
            values = ','.join(['%s'] * len(i))
            # 插入的表名
            table = 'result_data1'
            # 插入sql语句
            insertSql = 'INSERT INTO {table}({keys}) VALUES ({values})'.format(table=table, keys=keys, values=values)
            db.ping(reconnect=True)
            cursor.execute(insertSql, valuesTuple)
            db.commit()
cursor.close()  # 关闭游标
# 关闭数据库连接
db.close()
