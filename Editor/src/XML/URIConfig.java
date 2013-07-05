package XML;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class URIConfig {
	private final Document xmlDoc;
	private final Map<String, String> classEditorMap; // Map of class name to URI's common name <URI, editor class>
	private final Map<String, String> classConstructMap; // <URI, construct class>
	
	public URIConfig(String filename) {
		classEditorMap = new HashMap<String, String>();
		classConstructMap = new HashMap<String, String>();
		xmlDoc = Loader.getXMLDocument(filename);
		if(xmlDoc != null) {
			NodeList list = xmlDoc.getElementsByTagName("URI");
			for(int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String common = element.getAttribute("common_name");
					String editName = element.getAttribute("editor_class");
					String conName = element.getAttribute("lang_class");
					classEditorMap.put(common, editName);
					classConstructMap.put(common, conName);
				}
			}
		}
	}
	
	public String getEditorClassName(String uri) {
		return classEditorMap.get(uri);
	}
	
	public String getConstructClassName(String uri) {
		return classConstructMap.get(uri);
	}
}
