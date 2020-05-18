package wfh_test.service.internal;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException; 

// This class uses UDP to send, receive and parse XML documents
public class MyUdpClass {
	private List<Future<?>> futures = new ArrayList<>();
	private String outputFilePath;
	
	public MyUdpClass() {
		outputFilePath = "";
	}
	
	public void enableOutputFile(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	public void sendUdpMessage(String message) {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			InetAddress ip = InetAddress.getLocalHost();
			byte buf[] = message.getBytes();
			DatagramPacket dataPacket = new DatagramPacket(buf, buf.length, ip, 1234);
			ds.send(dataPacket);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!ds.isClosed()) {
				ds.close();
			}
		}
	}
	public void startUdpListener() {
		if (futures.size() > 0) {
			if (futures.get(0).isDone()) {
				futures.clear();
				ExecutorService executor = Executors.newSingleThreadExecutor();
			    Future<?> f = executor.submit(() -> receiveUdpMessage());
			    futures.add(f);
			} else {
				System.out.println("UDP Listener is already running.");
			}
		} else {
			ExecutorService executor = Executors.newSingleThreadExecutor();
		    Future<?> f = executor.submit(() -> receiveUdpMessage());
		    futures.add(f);
		}
	}
	private void receiveUdpMessage() {
		DatagramSocket ds = null;
		try {
			// Step 1 : Create a socket to listen at port 1234 
			ds = new DatagramSocket(1234);
			byte[] data = new byte[65535];
			DatagramPacket dataPacket = null;
			while (true) {
				// Step 2 : create a DatagramPacket to receive the data.
				dataPacket = new DatagramPacket(data, data.length); 
				// Step 3 : receive the data in byte buffer. 
				ds.receive(dataPacket);
				String message = byteToString(data);		
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
				// Clear the buffer after every message. 
				data = new byte[65535];
			}
		} catch (IOException e) {
			e.printStackTrace();
        } finally {
        	if (!ds.isClosed()) {
        		ds.close();
        	}
        }
	}
	private String byteToString(byte[] a) { 
        if (a == null) {
        	return null;
        }
        StringBuilder ret = new StringBuilder(); 
        int i = 0;
        while (a[i] != 0) { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret.toString();
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
