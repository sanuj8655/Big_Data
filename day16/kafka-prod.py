from time import sleep
from json import dumps
from kafka import KafkaProducer
import random
from datetime import datetime

producer = KafkaProducer(bootstrap_servers=['localhost:9092'], value_serializer=lambda x:dumps(x).encode('utf-8'))

while True:
	data = dict()
	now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
	sensor = 'LDR'
	reading = random.randint(0,80)
	data['time'] = now
	data['sensor'] = sensor
	data['reading'] = reading
	producer.send('iot', value=data)
	sleep(1)

# pip3 install kafka-python
# kafka-topics.sh --zookeeper localhost:2181 --create --topic iot --replication-factor 1 --partitions 2

# kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic iot

# python3 kafka-prod.py

