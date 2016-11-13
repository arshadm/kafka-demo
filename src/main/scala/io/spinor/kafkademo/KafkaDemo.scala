package io.spinor.kafkademo

import com.typesafe.config.ConfigFactory
import kafka.admin.AdminUtils
import kafka.utils.ZkUtils
import org.I0Itec.zkclient.exception.ZkMarshallingError
import org.I0Itec.zkclient.serialize.ZkSerializer
import org.I0Itec.zkclient.{ZkClient, ZkConnection}
import org.slf4j.LoggerFactory

/**
  * A simple kafka demo application.
  *
  * @author A. Mahmood (arshadm@spinor.io)
  */
object KafkaDemo extends App {
  /** The logger. */
  final val logger = LoggerFactory.getLogger("KafkaDemo")

  /** The application configuration settings. */
  val config = ConfigFactory.load()

  /** Check whether we are starting a producer or a consumer. */
  val isProducer = System.getenv("PRODUCER").equals("true")

  // sleep for 10 seconds before trying to create topics
  // and producers/consumers
  Thread.sleep(10000)

  // create the topic before running the producer or consumer
  createTopic()

  if (isProducer) {
    logger.info("Starting demo kafka producer")
    runProducer()
  } else {
    logger.info("Starting demo kafka consumer")
    runConsumer()
  }

//  while(true) {
//    Thread.sleep(1000)
//  }

  /**
    * Create the topic using zookeeper.
    */
  private def createTopic(): Unit = {
    val zookeeperHosts = System.getenv("ZOOKEEPER_CONNECT")
    val sessionTimeout = config.getInt("zookeeper.session.timeout.ms")
    val topic = config.getString("topic.name")
    val numPartitions = config.getInt("topic.partitions")
    val numReplicas = config.getInt("topic.replicas")

    System.out.println("ZOOKEEPER_CONNECT: " + zookeeperHosts)

    val zkClient = new ZkClient(zookeeperHosts, sessionTimeout, sessionTimeout, new ZKStringSerializer())
    val zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts, sessionTimeout), false)

    if (!AdminUtils.topicExists(zkUtils, topic)) {
      AdminUtils.createTopic(zkUtils, topic, numPartitions, numReplicas)
    }
  }

  /**
    * Run the kafka producer. We ensure the thread is not a daemon thread
    * so that the JVM does not terminate.
    */
  private def runProducer(): Unit = {
    val thread = new Thread(new DemoKafkaProducer(config), "kafkademo-producer")
    thread.setDaemon(false)
    thread.start()
  }

  /**
    * Run the kafka consumer. We ensure the thread is not a daemon thread
    * so that the JVM does not terminate.
    */
  private def runConsumer(): Unit = {
    val thread = new Thread(new DemoKafkaConsumer(config), "kafkademo-consumer")
    thread.setDaemon(false)
    thread.start()
  }
}

/**
  * A basic string serializer to be used with zookeeper.
  */
class ZKStringSerializer extends ZkSerializer {

  @throws(classOf[ZkMarshallingError])
  def serialize(data: Object): Array[Byte] = data.asInstanceOf[String].getBytes("UTF-8")

  @throws(classOf[ZkMarshallingError])
  def deserialize(bytes: Array[Byte]): Object = {
    if (bytes == null)
      null
    else
      new String(bytes, "UTF-8")
  }
}
