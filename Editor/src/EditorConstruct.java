
public abstract class EditorConstruct {
	private SyntaxTree theTree;
	
	public abstract String toScreenText();
	public abstract String toPlainText();
	public boolean ValidateSyntaxTree(SyntaxTreeElement top) { return false; }
}
