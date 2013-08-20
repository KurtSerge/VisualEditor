package clojure.constructs.meta;

import construct.Construct;
import construct.Placeholder;
import construct.PlaceholderConstruct;
import clojure.constructs.containers.ListConstruct;
import editor.InterfaceController.EInterfaceAction;
import editor.document.ConstructDocument;

public abstract class MetaConstruct extends ListConstruct
{
	public MetaConstruct(ConstructDocument document, String name, Construct parent) { 
		super(document, name, parent);
	}
}
