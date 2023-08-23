import base64

from confluent_kafka import Consumer, KafkaError
from confluent_kafka import Producer


def delivery_report(err, msg):
    if err is not None:
        print('Message delivery failed: {}'.format(err))
    else:
        print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))


def main_producer(message, topic):
    p = Producer({'bootstrap.servers': '10.0.2.15:9092'})
    # test_data = ["test1", "test2"]
    p.produce(topic, message, callback=delivery_report)
    p.flush()


def main_consumer(did, topic):
    c = Consumer({
        'bootstrap.servers': '10.0.2.15:9092',
        'group.id': did,
        'auto.offset.reset': 'earliest'
    })

    c.subscribe([topic])
    while True:
        print("here")
        msg = c.poll(1.0)

        if msg is None:
            continue
        if msg.error():
            if msg.error().code() == KafkaError._PARTITION_EOF:
                continue
            else:
                print(msg.error())
                break
        res = handle_message_receive(did, msg)
        if res is not None:
            break
    c.close()
    return res


def handle_message_send(did, m):
    #print("m --> ", m)
    m = base64.b64encode(m).decode('utf-8')
    m = did + " ||| " + m
    #print("RES --> ", m)
    return m


def handle_message_receive(did_receiver, m):
    # print("m value dec --> ", m.value().decode('utf-8'))
    m = m.value().decode('utf-8')
    m = m.split(" ||| ")
    did_sender = m[0]
    if did_sender != did_receiver:
        return base64.b64decode(m[1])
    #print("view --> ",base64.b64decode(m[1]))
    #return base64.b64decode(m[1])
    return None
# def handle_message(did, )
