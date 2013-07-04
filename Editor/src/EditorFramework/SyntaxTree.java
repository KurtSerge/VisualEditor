package EditorFramework;
import GenericTree.GenericTree;
import GenericTree.GenericTreeNode;

public class SyntaxTree extends GenericTree<SyntaxTreeElement> {
	public SyntaxTree() { }
	
	
	public GenericTreeNode<SyntaxTreeElement> findURI(String URI) {
		/*
        GenericTreeNode<T> returnNode = null;
        int i = 0;

        if (currentNode.getData().equals(dataToFind)) {
            returnNode = currentNode;
        }

        else if(currentNode.hasChildren()) {
            i = 0;
            while(returnNode == null && i < currentNode.getNumberOfChildren()) {
                returnNode = auxiliaryFind(currentNode.getChildAt(i), dataToFind);
                i++;
            }
        }

        return returnNode;*/
        
		return null;
	}
}
