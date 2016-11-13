package io.spinor.kafkademo

import java.util.Properties
import java.util.concurrent.Executors

import com.google.common.collect.ImmutableMap
import com.typesafe.config.Config
import kafka.consumer.{Consumer, ConsumerConfig}
import kafka.serializer.StringDecoder
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * The Kafka message consumer.
  *
  * @param config the application configuration settings
  * @author A. Mahmood (arshadm@spinor.io)
  */
class DemoKafkaConsumer(config: Config) extends Runnable {
  /** The logger. */
  final val logger = LoggerFactory.getLogger(classOf[DemoKafkaConsumer])

  /**
    * Consume messages from Kafka until terminated.
    */
  override def run(): Unit = {
    val consumerConfiguration = getConsumerConfiguration()
    val numThreads = config.getInt("consumer.numthreads")
    val consumer = Consumer.createJavaConsumerConnector(getConsumerConfiguration())
    val topic = config.getString("topic.name")

    val consumerMap = consumer.createMessageStreams(ImmutableMap.of(topic, numThreads), new StringDecoder(), new StringDecoder())
    val topicStreams = consumerMap.get(topic)

    val executor = Executors.newFixedThreadPool(numThreads)

    for(stream <- topicStreams.asScala) {
      executor.submit(new Runnable {
        override def run(): Unit = {
          val iterator = stream.iterator()
          while (iterator.hasNext()) {
            logger.info("Message:" + iterator.next().message().toString)
          }
        }
      })
    }
  }

  /**
    * Get the consumer properties.
    *
    * @return the consumer properties
    */
  private def getConsumerConfiguration(): ConsumerConfig = {
    val properties = new Properties()

    logger.info("ZOOKEEPER_CONNECT: " + System.getenv("ZOOKEEPER_CONNECT"))

    properties.put("group.id", config.getString("consumer.group.id"))
    properties.put("zookeeper.connect", System.getenv("ZOOKEEPER_CONNECT"))
    properties.put("zookeeper.session.timeout.ms", config.getString("consumer.zookeeper.session.timeout.ms"))
    properties.put("zookeeper.sync.time.ms", config.getString("consumer.zookeeper.sync.time.ms"))
    properties.put("auto.commit.interval.ms", config.getString("consumer.auto.commit.interval.ms"))

    new ConsumerConfig(properties)
  }
}
