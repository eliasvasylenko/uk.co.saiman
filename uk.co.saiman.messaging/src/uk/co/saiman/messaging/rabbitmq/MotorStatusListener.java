
package uk.co.saiman.messaging.rabbitmq;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MotorStatusListener {
  private String myName;
  private Connection connection;
  private Channel channel;
  private Consumer consumer;

  private final Semaphore available = new Semaphore(0, true);

  public List<String> messages = new LinkedList<String>();

  public MotorStatusListener(String string) {
    myName = string;
  }

  public Boolean subscribe() {
    try {
      ConnectionFactory factory = new ConnectionFactory();

      factory.setVirtualHost("lt2");
      factory.setHost("192.168.192.196");
      factory.setPassword("LT2");
      factory.setUsername("SAI");
      // factory.setCredentialsProvider(creds);
      connection = factory.newConnection();
      channel = connection.createChannel();

      channel.exchangeDeclare("LaserToF", "fanout");
      String queueName = channel.queueDeclare().getQueue();
      channel.queueBind(queueName, "LaserToF", "");

      consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(
            String consumerTag,
            Envelope envelope,
            AMQP.BasicProperties properties,
            byte[] body)
            throws IOException {
          String message = new String(body, "UTF-8");
          messages.add(message);

          System.out.println(" Listener [" + myName + " ] Received '" + message + "'");

          available.release();
        }
      };
      channel.basicConsume(queueName, true, consumer);

      return true;
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

  public Boolean wait_for_message() {
    try {
      return available.tryAcquire(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }

  }
}