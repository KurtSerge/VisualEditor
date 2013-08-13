package editor;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import construct.Construct;
import editor.InterfaceController.EInterfaceAction;

public interface IInterfaceActionListener {
	public boolean onReceievedAction(InterfaceController baseController, EInterfaceAction action, SimpleAutoCompleteEntry entry);
}
