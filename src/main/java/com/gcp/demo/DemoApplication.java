package com.gcp.demo;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;

import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import java.io.IOException;
import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcp.demo.adapter.StorageClientAdapter;
import com.gcp.demo.bean.MessageSub;

@SpringBootApplication
public class DemoApplication {
	private static final Logger logger = Logger.getLogger(DemoApplication.class.getName());
	private static final Random rand = new Random(2020);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		logger.info("Start Demo..... ");
	}
	
	  @Bean
	  public MessageChannel inputMessageChannel() {
	    return new PublishSubscribeChannel();
	  }

	  @Bean
	  public PubSubInboundChannelAdapter inboundChannelAdapter(
	      @Qualifier("inputMessageChannel") MessageChannel messageChannel,
	      PubSubTemplate pubSubTemplate) {
	    PubSubInboundChannelAdapter adapter =
	        new PubSubInboundChannelAdapter(pubSubTemplate, "first-sub");
	    adapter.setOutputChannel(messageChannel);
	    adapter.setAckMode(AckMode.MANUAL);
	    adapter.setPayloadType(String.class);
	    return adapter;
	  }

	  @ServiceActivator(inputChannel = "inputMessageChannel")
	  public void messageReceiver(
	      String payload,
	      @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		  logger.info("Message arrived via an inbound channel adapter from sub-one! Payload: " + payload);
		  
		  try {
			MessageSub msg = new ObjectMapper().readValue(payload, MessageSub.class);
			
			StorageClientAdapter.downloadObject("first-142020", "first-142020.appspot.com", msg.body, "/"+msg.body);
			
			StorageClientAdapter.uploadObject("first-142020", "output-files2", msg.body, "/"+msg.body);
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  
		  
	    message.ack();
	  }

}
