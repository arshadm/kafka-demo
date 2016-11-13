package io.spinor.kafkademo

import java.util.concurrent.Executors
import java.util.{Properties, UUID}

import com.typesafe.config.Config
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.LoggerFactory

import scala.util.Random

/**
  * The Kafka message producer.
  *
  * @param config the application configuration settings
  * @author A. Mahmood (arshadm@spinor.io)
  */
class DemoKafkaProducer(config: Config) extends Runnable {
  /** The logger. */
  final val logger = LoggerFactory.getLogger(classOf[DemoKafkaProducer])

  /**
    * Continuously send messages to Kafka until terminated.
    */
  override def run(): Unit = {
    val properties = getProducerProperties()
    val producer = new KafkaProducer[String, String](properties)
    val topic = config.getString("topic.name")
    val numPartitionss = config.getInt("topic.partitions")
    val messageInterval = config.getLong("producer.message.interval.ms")
    val numThreads = config.getInt("producer.numthreads")
    val executor = Executors.newFixedThreadPool(numThreads)

    for(tid <- 1 until numThreads) {
      executor.submit(new KafkaMessageSender(producer, topic, numPartitionss, messageInterval))
    }
  }

  /**
    * Get the kafka producer properies.
    *
    * @return the kafka producer properties
    */
  private def getProducerProperties(): Properties = {
    val properties = new Properties()

    properties.put("bootstrap.servers", System.getenv("KAFKA_BROKERS"))
    properties.put("acks", config.getString("producer.acks"))
    properties.put("retries", config.getString("producer.retries"))
    properties.put("batch.size", config.getString("producer.batch.size"))
    properties.put("linger.ms", config.getString("producer.linger.ms"))
    properties.put("buffer.memory", config.getString("producer.buffer.memory"))

    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    properties
  }
}

/**
  * The Kafka message sender.
  *
  * @param producer the shared kafka instance
  * @param topic the topic
  * @param numPartitions the number of partitions
  * @param messageInterval the message interval
  */
class KafkaMessageSender(producer: KafkaProducer[String,String], topic: String, numPartitions: Int, messageInterval: Long) extends Runnable {
  /** The logger. */
  final val logger = LoggerFactory.getLogger(classOf[KafkaMessageSender])

  override def run(): Unit = {
    var messageCount = 0

    while (true) {
      val key = UUID.randomUUID().toString
      val tid = Thread.currentThread().getId()
      val value = s"Hello World $tid - $messageCount"
      val partition = Random.nextInt(numPartitions)
      val timestamp = System.currentTimeMillis()

      logger.info(s"Producing message: $key -> $value")
      val producerRecord = new ProducerRecord[String, String](topic, partition, timestamp, key, value)
      producer.send(producerRecord)

      Thread.sleep(messageInterval)

      messageCount = messageCount + 1
    }
  }
}
