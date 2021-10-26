#!/usr/bin/python3

from pyhive import hive

host_name = 'localhost'
port = 10000
user = 'sunbeam'
password = ' '
db_name = 'edbda'

# get hive connection
conn = hive.Connection(host=host_name, port=port, username=user, password=password, database=db_name, auth='CUSTOM')

# get cursor object to execute the query on hive
cur = conn.cursor()

# execute sql (hive ql) query using cursor
id = input('enter id: ')
sql = 'select * from books where id = ' + id
print('sql : ' + sql)
cur.execute(sql)

# collect result of query and process it
result = cur.fetchall()
for row in result:
	print(row)

# close hive connection
conn.close()
