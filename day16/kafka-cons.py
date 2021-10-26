from kafka import KafkaConsumer
from json import loads

# earliest and latest
consumer = KafkaConsumer('iot', bootstrap_servers=['localhost:9092'], auto_offset_reset='latest', value_deserializer=lambda m: loads(m.decode('utf-8')))

for message in consumer:
    message = message.value
    print('received : {}'.format(message))

