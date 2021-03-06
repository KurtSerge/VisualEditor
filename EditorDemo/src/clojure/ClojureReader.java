package clojure;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import clojure.constructs.BigDecimalConstruct;
import clojure.constructs.BigIntConstruct;
import clojure.constructs.BooleanConstruct;
import clojure.constructs.CharacterConstruct;
import clojure.constructs.DoubleConstruct;
import clojure.constructs.IntegerConstruct;
import clojure.constructs.KeywordConstruct;
import clojure.constructs.RatioConstruct;
import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.ListConstruct;
import clojure.constructs.containers.MapConstruct;
import clojure.constructs.containers.VectorConstruct;
import clojure.constructs.meta.UnknownConstruct;
import clojure.constructs.special.EmptyConstruct;
import clojure.lang.RT;
import construct.Construct;
import editor.document.ConstructDocument;

public class ClojureReader {
	
	public ClojureReader() { 
		mClassToConstructClassMap = new HashMap<Class<?>, Class<?>>();
		
		// Clojure types
		mClassToConstructClassMap.put(clojure.lang.PersistentVector.class, VectorConstruct.class);
		mClassToConstructClassMap.put(clojure.lang.PersistentArrayMap.class, MapConstruct.class);
		mClassToConstructClassMap.put(clojure.lang.Symbol.class, SymbolConstruct.class);
		mClassToConstructClassMap.put(clojure.lang.Keyword.class, KeywordConstruct.class);
		mClassToConstructClassMap.put(clojure.lang.Ratio.class, RatioConstruct.class);
		mClassToConstructClassMap.put(clojure.lang.PersistentList.class, ListConstruct.class);
		
		// Java types
		mClassToConstructClassMap.put(java.lang.Integer.class, IntegerConstruct.class);
		mClassToConstructClassMap.put(java.lang.Double.class, DoubleConstruct.class);
		mClassToConstructClassMap.put(java.lang.Boolean.class, BooleanConstruct.class);
		mClassToConstructClassMap.put(java.lang.Character.class, CharacterConstruct.class);
		mClassToConstructClassMap.put(java.math.BigInteger.class, BigIntConstruct.class);
		mClassToConstructClassMap.put(java.math.BigDecimal.class, BigDecimalConstruct.class);
		mClassToConstructClassMap.put(java.lang.String.class, StringConstruct.class);		
	}

	private String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private Construct createConstructFromObject(ConstructDocument document, Construct parent, Object object) { 
		Class<?> objectClass = object.getClass();
		Class<?> constructClass = mClassToConstructClassMap.get(objectClass);
		String objectString = object.toString();
		
		if(constructClass != null) { 
			try {
				Constructor<?> constructor = constructClass.getConstructor(ConstructDocument.class, Construct.class, String.class);
				return (Construct) constructor.newInstance(document, parent, objectString);
			} catch (Exception ex) { 
				ex.printStackTrace();
				System.out.println("Constructor not available for " + objectClass);
			}
		} else { 
			// This is a special case object
			if(object == clojure.lang.PersistentList.EMPTY) { 
				return new ListConstruct(document, parent, objectString);
			}
		}
		
		return new UnknownConstruct(document, parent);
	}
	
	/**
	 * Recursively takes an object's children and appends them
	 * to the Construct parent passed in.  
	 * 
	 * @param parentConstruct The construct node to add children to.
	 * @param parentObject The object from which to find children.
	 * @return The next level construct
	 * @throws Exception
	 */
	public Construct recursiveCreateConstructFromObject(ConstructDocument document, Construct parentConstruct, Object parentObject)
		throws Exception
	{
		Construct thisConstruct = createConstructFromObject(document, parentConstruct, parentObject);		
	    String name = parentObject.getClass().getName();
	   
	    // Treat the parent as a vector type
	    if(name.equalsIgnoreCase("clojure.lang.PersistentVector")) {
	   	clojure.lang.PersistentVector vector = (clojure.lang.PersistentVector) parentObject;		
		   	for(int i = 0; i < vector.length(); i++) { 
		   		Object leafObject = vector.get(i);
		   		Construct leafConstruct = recursiveCreateConstructFromObject(document, thisConstruct, leafObject);
		   		if(leafConstruct != null) {
		   			thisConstruct.addChild(leafConstruct);
		   		}
		   	}
	    } else if(name.equalsIgnoreCase("clojure.lang.PersistentList")) {
	    	clojure.lang.PersistentList list = (clojure.lang.PersistentList) parentObject;		
		   	for(int i = 0; i < list.size(); i++) { 
		   		Construct leafConstruct = recursiveCreateConstructFromObject(document, thisConstruct, list.get(i));
		   		if(leafConstruct != null) {
		   			thisConstruct.addChild(leafConstruct);
		   		}
		   	}
	    } else if(name.equalsIgnoreCase("clojure.lang.PersistentArrayMap")) {
	    	clojure.lang.PersistentArrayMap map = (clojure.lang.PersistentArrayMap) parentObject;
	    	for(Object key : map.keySet()) { 
	    		Construct keyConstruct = recursiveCreateConstructFromObject(document, thisConstruct, key);
	    		Construct valueConstruct = recursiveCreateConstructFromObject(document, thisConstruct, map.get(key));
	    		if(keyConstruct != null && valueConstruct != null) {
	    			thisConstruct.addChild(keyConstruct);
	    			thisConstruct.addChild(valueConstruct);
	    		}
	    	}
	    }
	    
	  	return thisConstruct;
	}
	
	/**
	 * Reads the entire contents of the stream into a string, and then uses
	 * Clojure to parse the construct tree.
	 * 
	 * @param stream Stream from which to read the file from.
	 * @return The root level construct, may be null.
	 * @throws Exception Failure to load
	 */
	public Construct parseFromInputStream(ConstructDocument document, InputStream stream) throws Exception {
		String inputStreamAsString = convertStreamToString(stream);
		Object inputStreamAsSingleObject = RT.readString(inputStreamAsString);
		
		System.out.println(inputStreamAsString);
		System.out.println(inputStreamAsSingleObject);
		
		EmptyConstruct emptyConstruct = new EmptyConstruct(document);
		
		emptyConstruct.addChild(0, recursiveCreateConstructFromObject(document, emptyConstruct, inputStreamAsSingleObject));
		return emptyConstruct;
	}
	
	private Map<Class<?>, Class<?>> mClassToConstructClassMap;	
}
