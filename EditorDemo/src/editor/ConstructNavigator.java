package editor;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;

import construct.Construct;
import editor.InterfaceController.EInterfaceAction;
import editor.document.ConstructDocument;

public class ConstructNavigator implements IInterfaceActionListener {
	private final JFrame frame;
	private final ConstructDocument mDocument;
	private ConstructEditor selected = null;
	
	public ConstructEditor getSelected() { 
		return selected;
	}
	
	public ConstructNavigator(JFrame frame, ConstructDocument document) {
		this.frame = frame;
		mDocument = document;
	}
	
	public void SelectRandom() {
		ConstructEditorStore constructStore = mDocument.getConstructEditorStore();
		List<WeakReference<ConstructEditor>> constructEditors = constructStore.getEditors();
		
		int select = Math.abs((new Random()).nextInt()) % constructEditors.size();
		WeakReference<ConstructEditor> toSelect = constructEditors.get(select);
		Select(Construct.SelectionCause.SelectedRandomly, toSelect.get());
	}
	
	public void SelectParentConstruct() {
		if(selected == null)
			return;
		
		ConstructEditor parent = selected.getParent();
		if(parent == null)
			return;
		
		 Select(Construct.SelectionCause.SelectedParent, parent);
	}
	
	public void SelectFirstChildConstruct() {
		if(selected == null)
			return;
		if(selected.construct.children.size() == 0)
			return;
		
		Construct child = selected.construct.children.get(0);
		if(child == null)
			return;
		
		 Select(Construct.SelectionCause.SelectedFirstChild, mDocument.editorsFromConstruct(child));
	}
	
	public boolean SelectAdjacentConstruct(boolean next) {
		if(selected == null)
			return false;

		Construct parent = selected.construct.parent;
		if(parent == null)
			return false;

		int myIndex = parent.children.indexOf(selected.construct);
		int selectIndex = (next) ? ++myIndex : --myIndex;
		if(selectIndex >= parent.children.size()) {
			selectIndex = 0;
		} else if(selectIndex < 0) {
			selectIndex = parent.children.size()-1;
			if(selectIndex < 0)
				return false;
		}
		
		Construct newSelect = parent.children.get(selectIndex);
		if(newSelect == null)
			return false;
	
		ConstructEditor edit = mDocument.editorsFromConstruct(newSelect);
		if(edit == null) {
			return false;
		}
		
		Select(Construct.SelectionCause.SelectedAdjacentConstruct, edit);
		
		return true;
	}
	
	public void Select(Construct.SelectionCause selectionType, ConstructEditor newSel) {
		if(newSel == null)
			return;
		
		Application.resetError();
		Application.getApplication().hideAutoComplete(true);
		
		
		ConstructEditor lastSelected = selected;
		if(selected != null)   {
			selected.setSelected(selectionType, newSel, false);
		}
		
		if(selected != null)
			selected.update();
		
		Construct constructForSelection = newSel.construct.getConstructForSelection(selectionType);			
		ConstructEditor constructEditor = mDocument.editorsFromConstruct(constructForSelection);
		constructEditor.update();
		selected = constructEditor;
		selected.setSelected(selectionType, lastSelected, true);

		frame.invalidate();
		frame.repaint();
	}
	
	@Override
	public boolean onReceievedAction(InterfaceController baseController, EInterfaceAction binding, SimpleAutoCompleteEntry construct) {
		switch(binding) {
			case Bind_SelectParent:
    			SelectParentConstruct();
				break;
				
			case Bind_SelectChild:
    			SelectFirstChildConstruct();
				break;
				
			case Bind_SelectNextSibling:
    			SelectAdjacentConstruct(true);
				break;
				
			case Bind_SelectPrevSibling:
    			SelectAdjacentConstruct(false);
				break;
				
			case Bind_SelectRandom:
				SelectRandom();
				break;
				
			case Bind_DebugPrint:
				selected.construct.debugPrint();
				break;
				
			default:
				return false;
		}
		
		return true;
	}
}
