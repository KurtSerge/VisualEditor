package editor;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import construct.Construct;

public interface BaseControllerListener {
	public boolean onReceievedAction(BaseController baseController, BaseController.EKeyBinding binding, SimpleAutoCompleteEntry entry);
}
