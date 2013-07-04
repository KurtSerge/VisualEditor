package XML;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class URIConfig extends Loader {
	private final Document xmlDoc;
	private final Map<String, String> classNameMap; // Map of class name to URI's common name <URI, class>
	
	public URIConfig(String filename) {
		classNameMap = new HashMap<String, String>();
		xmlDoc = getXMLDocument(filename);
		if(xmlDoc != null) {
			NodeList list = xmlDoc.getElementsByTagName("URI");
			for(int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String common = element.getAttribute("common_name");
					String classname = element.getAttribute("class");
					classNameMap.put(common, classname);
				}
			}
		}
	}
	
	public String getClassName(String uri) {
		return classNameMap.get(uri);
	}
}
