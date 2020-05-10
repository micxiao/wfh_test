package wfh_test.service;

public interface IMyClass {
	public void startConsumerPolling(String topicName);
	public void sendProducerMessage(String topicName, String message);
}
