# Big Data

## Agenda
* HBase

## HBase
* Start HBase
	* terminal> start-hbase.sh
	* terminal> jps
	* terminal> netstat -tln
* Browser:
	* http://localhost:16010
	* System tables
* Start HBase shell
	* terminal> hbase shell
	* HBase shell --> ruby
* On hbase shell ...

```ruby
help

version

status

list
```

```ruby
create 'students', 'name', 'marks'

list

describe 'students'
```

```ruby
create 'books', 'name', 'author', 'subject', 'price'

describe 'books'

put 'books', '1', 'name', 'Atlas Shrugged'
put 'books', '1', 'author', 'Ayn Rand'
put 'books', '1', 'subject', 'Novell'
put 'books', '1', 'price', '523.23'

put 'books', '2', 'name', 'The Fountainhead'
put 'books', '2', 'author', 'Ayn Rand'
put 'books', '2', 'subject', 'Novell'
put 'books', '2', 'price', '432.73'

put 'books', '3', 'name', 'The Alchemist'
put 'books', '3', 'author', 'Paulo Cohelo'
put 'books', '3', 'subject', 'Novell'
put 'books', '3', 'price', '721.3'

put 'books', '4', 'name', 'The Archer'
put 'books', '4', 'price', '345.23'

get 'books', '2', 'name'
get 'books', '2', 'price'

get 'books', '3', 'name', 'price'

get 'books', '1'

scan 'books'

disable 'books'

drop 'books'
```

```ruby
create 'contacts', 'name', 'phone', 'email'

list

put 'contacts', '007', 'name:fname', 'James'
put 'contacts', '007', 'name:lname', 'Bond'
put 'contacts', '007', 'phone:mobile', '1234567890'

put 'contacts', '001', 'name:fullname', 'Sunbeam Infotech'
put 'contacts', '001', 'phone:mobile', '9881208115'
put 'contacts', '001', 'phone:fax', '020-24260308'
put 'contacts', '001', 'email:office', 'siit@sunbeaminfo.com'

put 'contacts', '002', 'name:fname', 'Nilesh'
put 'contacts', '003', 'name:lname', 'Ghule'
put 'contacts', '004', 'phone:mobile', '9527331338'

delete 'contacts', '003', 'name:lname'
delete 'contacts', '004', 'phone:mobile'

put 'contacts', '002', 'name:lname', 'Ghule'
put 'contacts', '002', 'phone:mobile', '9527331338'

put 'contacts', '005', 'name:sal', 'Mr'
put 'contacts', '005', 'name:lname', 'Z'
put 'contacts', '005', 'phone:mobile', '9527XXXXXX'

get 'contacts', '007'

scan 'contacts'

scan 'contacts', { STARTROW => '001', ENDROW => '006' }

get 'contacts', '007', 'name:fname'

scan 'contacts', { STARTROW => '001', ENDROW => '006', COLUMNS => [ 'name:fname', 'phone:mobile' ] }

scan 'contacts', { COLUMNS => [ 'name:fname', 'phone:mobile' ] }

disable 'contacts'

enable 'contacts'

put 'contacts', '002', 'phone:mobile', '7722093091'

get 'contacts', '002', 'phone:mobile'

disable 'contacts'
```

```ruby
create 'emp', { NAME => 'ename' }, { NAME => 'sal', VERSIONS => 3 }

describe 'emp'

put 'emp', '001', 'ename', 'SMITH'
put 'emp', '001', 'sal', '850'
put 'emp', '002', 'ename', 'KING'
put 'emp', '002', 'sal', '5000'
put 'emp', '003', 'ename', 'MILLER'
put 'emp', '003', 'sal', '1250'

scan 'emp'

put 'emp', '001', 'sal', '950'
put 'emp', '001', 'sal', '1100'

get 'emp', '001'

get 'emp', '001', { COLUMNS => ['sal'], VERSIONS => 3 }

put 'emp', '001', 'sal', '1150'
put 'emp', '001', 'sal', '1200'

get 'emp', '001', { COLUMNS => ['sal'], VERSIONS => 3 }
get 'emp', '001', { COLUMNS => ['sal'], VERSIONS => 5 }

disable 'emp'

enable 'emp'

create 'books', 'name', 'author', 'subject', 'price'
```

```sh
terminal> hadoop fs -mkdir -p /user/sunbeam/books

terminal> hadoop fs -put /path/to/books.csv /user/sunbeam/books

terminal> start-yarn.sh

terminal> hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.separator=, -Dimporttsv.columns="HBASE_ROW_KEY,name,author,subject,price" books /user/sunbeam/books/
```

```ruby
scan 'books'
```

### User defined MR job
* We can implement MR job to process the data stored in HBase.
* For HBase data input & output, we should use TableInputFormat and TableOutputFormat (provided in HBase API).
* To create mapper (to read data from HBase table)
	* class MyHBaseTableMapper extends TableMapper<KeyOut,ValueOut>
		* Mapper input: Key=Row Key, Value=Column Values
		* All input data is available as byte-array.
* Implement driver class using TableMapReduceUtil
	* TableMapReduceUtil.initTableMapperJob("books", scan,  MyHBaseTableMapper.class, Text.class, DoubleWritable.class, job);
	* It internally set up -- InputFormat as TableInputFormat.
* hadoop jar /path/to/job.jar pkg.HBaseBooksDriver /tmp/booksummary



* Run Zookeeper on another terminal
	* terminal> hbase zkcli

```
ls /

ls /hbase

ls /hbase/master

get /hbase/master

ls /hbase/rs

get /hbase/rs/osboxes,16020,1630123025659

ls /hbase/table

get /hbase/table/emp

stat /hbase/table/emp

quit
```


























