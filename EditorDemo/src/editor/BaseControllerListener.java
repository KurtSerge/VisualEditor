package editor;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import construct.Construct;
import editor.BaseController.EInterfaceAction;

public interface BaseControllerListener {
	public boolean onReceievedAction(BaseController baseController, EInterfaceAction action, SimpleAutoCompleteEntry entry);
}
