package autocomplete;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import construct.Construct;
import editor.InterfaceController;

public interface IAutoCompleteListener {
	public void onAutoCompleteCreateReplacement(InterfaceController controller, SimpleAutoCompleteEntry entry);
}
