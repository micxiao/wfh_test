package wfh_test.service.internal;

@SuppressWarnings("unused")
public class TestClass {
	static String inputFilePath = "C:\\Users\\micxiao\\Desktop\\testInput.xml";
	static String outputFilePath = "C:\\Users\\micxiao\\Desktop\\testOutput.xml";
	
	public static void main(String[] args) {
		//kafkaTest();
		udpTest();
	}
	
	private static void kafkaTest() {
		MyClass mc = new MyClass();
		mc.enableOutputFile(outputFilePath);
		mc.startConsumerPolling("test");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mc.sendProducerMessage("test", MyClass.fileToString(inputFilePath));
	}
	
	private static void udpTest() {
		MyUdpClass muc = new MyUdpClass();
		muc.enableOutputFile(outputFilePath);
		muc.startUdpListener();
		muc.sendUdpMessage(MyClass.fileToString(inputFilePath));
	}
	
}
