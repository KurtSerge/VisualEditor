package autocomplete;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import construct.Construct;
import editor.BaseController;

public interface IAutoCompleteListener {
	public void onAutoCompleteCreateReplacement(BaseController controller, SimpleAutoCompleteEntry entry);
}
