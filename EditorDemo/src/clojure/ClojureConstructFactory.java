package clojure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import construct.Construct;
import clojure.constructs.BooleanConstruct;
import clojure.constructs.CharacterConstruct;
import clojure.constructs.DoubleConstruct;
import clojure.constructs.IntegerConstruct;
import clojure.constructs.KeywordConstruct;
import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.ListConstruct;
import clojure.constructs.containers.MapConstruct;
import clojure.constructs.containers.VectorConstruct;
import clojure.constructs.meta.KeywordExpressionPairConstruct;
import editor.document.ConstructDocument;

public class ClojureConstructFactory {
	public static Construct duplicate(Class<?> constructClass, ConstructDocument document, Construct parent) {
		Construct newConstruct = null;
		
		// Primitives
		if(constructClass.equals(SymbolConstruct.class)) { 
			newConstruct = new SymbolConstruct(document, parent, "symbol");
		} else if(constructClass.equals(KeywordExpressionPairConstruct.class)) { 
			newConstruct = new KeywordExpressionPairConstruct(document, parent, null);
		} else if(constructClass.equals(BooleanConstruct.class)) { 
			newConstruct = new BooleanConstruct(document, parent, "true");
		} else if(constructClass.equals(IntegerConstruct.class)) {
			newConstruct = new IntegerConstruct(document, parent, "0");
		} else if(constructClass.equals(KeywordConstruct.class)) { 
			newConstruct = new KeywordConstruct(document, parent, "keyword");
		} else if(constructClass.equals(DoubleConstruct.class)) { 
			newConstruct = new DoubleConstruct(document, parent, "0.0");
		} else if(constructClass.equals(CharacterConstruct.class)) { 
			newConstruct = new CharacterConstruct(document, parent, "c");
		} else if(constructClass.equals(StringConstruct.class)) { 
			newConstruct = new StringConstruct(document, parent, "string");
		}

		// Collections
		if(constructClass.equals(VectorConstruct.class)) { 
			newConstruct = new VectorConstruct(document, parent, null);
		} else if(constructClass.equals(ListConstruct.class)) { 
			newConstruct = new ListConstruct(document, parent, null);
		} else if(constructClass.equals(MapConstruct.class)) { 
			newConstruct = new MapConstruct(document, parent, null);
		}
		
		if(newConstruct == null) { 
			try {
				// Attempt to generically instantiate the construct
				// This will catch all meta construct initializing
				Constructor<?> constructor = constructClass.getConstructor(editor.document.ConstructDocument.class, construct.Construct.class);
				if(constructor != null) { 
					newConstruct = (Construct) constructor.newInstance(document, parent);
				}
			} catch (Exception ex) {
			}
		}
		
		return newConstruct;
	}
}
