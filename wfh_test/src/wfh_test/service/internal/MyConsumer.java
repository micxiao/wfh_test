package wfh_test.service.internal;

import java.util.*;
import java.util.concurrent.*;
import java.time.Duration;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.WakeupException;

class MyConsumer {
	private List<Future<?>> futures = new ArrayList<>();
	private List<MessageListener> listeners = new ArrayList<>();
	
	void addListener(MessageListener ml) {
		listeners.add(ml);
	}
	
	void startPollingRecords(String topicName) {
		if (futures.size() > 0) {
			if (futures.get(0).isDone()) {
				futures.clear();
				ExecutorService executor = Executors.newSingleThreadExecutor();
			    Future<?> f = executor.submit(() -> receiveMessage(topicName));
			    futures.add(f);
			} else {
				System.out.println("Kafka Consumer Polling is already running.");
			}
		} else {
			ExecutorService executor = Executors.newSingleThreadExecutor();
		    Future<?> f = executor.submit(() -> receiveMessage(topicName));
		    futures.add(f);
		}  
	}
	
	private void receiveMessage(String topicName) {
			
		// Consumer settings
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "test");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");       
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
					      
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
					            
		// Consumer subscribes to a list of topics
	    consumer.subscribe(Arrays.asList(topicName));
		    
	    try {
	    	while (true) {
		         ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
		         for (ConsumerRecord<String, String> record : records) {
		        	 for (MessageListener ml : listeners) {
		        		 ml.newMessageReceived(record.value());
		        	 }
		        	 //System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
		         }        
		    }
	    } 
	    catch (WakeupException e) {
	    }
	    finally {
	    	consumer.close();
	    }
	}
}
