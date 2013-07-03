import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class XMLTest {
	
	private static void printChildren(Element root) {
		printChildren(root, 0);
	}
	private static void printChildren(Element root, int depth) {
		NodeList children =	root.getChildNodes();
		
		// Print all direct children
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element current = (Element)node;
				System.out.print("\tNode Name: " + current.getNodeName());
				System.out.print(", Level: " + depth);
				System.out.print(", URI:" + current.getAttribute("URI"));
				
				String lit = current.getAttribute("literal");
				if(lit.length() != 0) {
					System.out.print("\tliteral: " + current.getAttribute("literal"));
				}
				System.out.println("");
				
				printChildren(current, depth+1);
			}
		}
	}
	
	private static Document getXMLDocument(String filename) {
		File fXmlFile = new File(filename);
		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	public static void main(String[] args)  {
			Document doc = getXMLDocument("C:/Development/VisualEditor/Editor/sample.xml");
			if(doc != null) {
				// Get number of constructs
				NodeList nList = doc.getElementsByTagName("Construct");
				int numberConstructs = nList.getLength();
				System.out.println("Construct Count: " + numberConstructs);
				
				// Get root
				Element root = doc.getDocumentElement();
				System.out.print("Root element: " + root.getNodeName());
				System.out.print(", URI: " + root.getAttribute("URI"));
				System.out.println(", literal: " + root.getAttribute("literal"));

				printChildren(root);
			}
	}
}
