package EditorFramework;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;

import org.w3c.dom.css.Rect;

public abstract class CodeEditorConstruct extends EditorConstruct {
	// Each element in the SyntaxTree should correspond to a ScreenTextElement
	public class ScreenTextElement {
		String plainText;	// Plain text to be printed to monospaced editor
		Rect BoundingBox;
		SyntaxTreeElement syntaxElement;
	}
	private List<ScreenTextElement> screenElements;
	
	
	
	public SyntaxTreeElement getElementAt(int row, int col) {
		 return null;
	}
	
	@Override
	public JPanel getPanel() {
		return null;
	}
	
	public abstract String toScreenText();
}
