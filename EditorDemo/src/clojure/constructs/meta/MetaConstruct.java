package clojure.constructs.meta;

import construct.Construct;
import clojure.constructs.containers.ListConstruct;
import editor.InterfaceController.EInterfaceAction;
import editor.document.ConstructDocument;

public abstract class MetaConstruct extends ListConstruct
{
	public MetaConstruct(ConstructDocument document, String name, Construct parent) { 
		super(document, name, parent);
	}
	
	public boolean canPerformBinding(EInterfaceAction binding) {		
		switch(binding) { 
			case Bind_InsertPaste:
			case Bind_InsertReplace:
			case Bind_InsertChild:
			case Bind_InsertWrap:
			case Bind_InsertUsurp:
				return true;
				
			default:
				break;
		}
		
		return false;
	}	
}
