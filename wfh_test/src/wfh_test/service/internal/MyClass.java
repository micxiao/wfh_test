package wfh_test.service.internal;

import wfh_test.service.IMyClass;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MyClass implements IMyClass, MessageListener {
	private MyConsumer cons;
	private MyProducer prod;
	private String outputFilePath;
	
	public MyClass() {
		outputFilePath = "";
		cons = new MyConsumer();
		prod = new MyProducer();
		cons.addListener(this);
	}
	
	public void startConsumerPolling(String topicName) {
		cons.startPollingRecords(topicName);
	}
	
	public void sendProducerMessage(String topicName, String message) {
		prod.sendMessage(topicName, message);
	}
	
	public void enableOutputFile(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	public static String readInputFile(String filePath) 
	{
	    String content = "";
	    try {
	    	content = new String(Files.readAllBytes(Paths.get(filePath)));
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    return content;
	}
	
	@Override
	public void newMessageReceived(String message) {	
		if (message.trim().substring(0, 5).equals("<?xml")) {
			try {
				if (!outputFilePath.equals("")) {
					FileWriter fileWriter = new FileWriter(outputFilePath);
				    fileWriter.write(message);
				    fileWriter.close();
				}
				parseXml(message);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(message);
		}
	}
	
	private void parseXml(String xmlMessage) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlMessage)));
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                String msgType = elem.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue();
                String msgCont = elem.getElementsByTagName("content").item(0).getChildNodes().item(0).getNodeValue();
                System.out.printf("Message type is: <%s>\nMessage content is: <%s>\n", msgType, msgCont);
            }
        }
	}
}
