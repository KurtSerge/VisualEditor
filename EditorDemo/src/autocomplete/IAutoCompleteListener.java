package autocomplete;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import editor.InterfaceController;

public interface IAutoCompleteListener {
	public void onAutoCompleteCreateReplacement(InterfaceController controller, SimpleAutoCompleteEntry entry);
}
