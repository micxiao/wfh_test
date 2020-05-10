package wfh_test.service.internal;

public class TestClass {

	public static void main(String[] args) {
		String inputFilePath = "C:\\Users\\micxiao\\Desktop\\testInput.xml";
		String outputFilePath = "C:\\Users\\micxiao\\Desktop\\testOutput.xml";
		MyClass mc = new MyClass();
		mc.enableOutputFile(outputFilePath);
		mc.startConsumerPolling("test");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mc.sendProducerMessage("test", MyClass.readInputFile(inputFilePath));
	}
	
}
