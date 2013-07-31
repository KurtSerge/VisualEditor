package editor;

import java.awt.Dimension;

import editor.document.ConstructDocument;

public class LayoutController {
	
	private float mConstructRatioWidth;
	private ConstructDocument mDocument;
	private Dimension mScreen;	
	
	public LayoutController(ConstructDocument document, Dimension screen, float initialMaxRatio) {
		setConstructDisplayWidth(initialMaxRatio);		
		setDimensions(screen);
		
		mDocument = document;		
	}

	public void setConstructDisplayWidth(float ratio) { 
		mConstructRatioWidth = Math.max(0, Math.min(1, ratio));
		relayout();
	}
	
	public void setDimensions(Dimension dimensions) { 
		mScreen = dimensions;
		relayout();
	}
	
	public void relayout() { 
		if(mScreen == null || mDocument == null) { 
			return ;
		}
		
		restoreFlags(mDocument.getRootConstruct(), false);
		
		Dimension size = getSize(mDocument.getRootConstruct());
		float ratio = (float)size.width / (float)mScreen.width;
		while(ratio > mConstructRatioWidth) {
			System.out.println("Width " + ratio + " is still bigger than " + mConstructRatioWidth);
			

			
			
			if(!layout(mDocument.getRootConstruct())) { 
				System.out.println("No more nodes to set..");
				break;
			}
			
			size = getSize(mDocument.getRootConstruct());
			ratio = (float) size.width / (float) mScreen.width;			
		}
	}
	
	protected Dimension getSize(Construct construct) { 
		ConstructEditor editor = ConstructEditor.editorsByConstructs.get(construct).get();
		return editor.get_size();
	}

	protected void restoreFlags(Construct construct, boolean flagState) { 
		construct.setMultilined(flagState);
		for(Construct child : construct.children) { 
			restoreFlags(child, flagState);
		}
	}
	
	protected boolean needsCompacting(Construct construct) { 
		float constructDisplaySizeInPixels = getSize(construct).width;
		float constructDisplaySizePercentage = (float) constructDisplaySizeInPixels / (float) mScreen.width;
		return constructDisplaySizePercentage > mConstructRatioWidth; 
	}
	
	protected boolean layout(Construct construct) {
		if(construct == null || mConstructRatioWidth == 0.0f) {
			System.err.println("Not setup to layout.");
			return false;
		}

		if(construct.getIsMultilined() == false) {
			construct.setMultilined(true);
			
			ConstructEditor editor = ConstructEditor.editorsByConstructs.get(construct).get();
			mDocument.getRootComponent().repaint();			
			editor.update();			
			
			return true;
		} else { 
			for(Construct child : construct.children) { 
				boolean result = layout(child);
				if(result) { 
					return true;
				}
			}
		}
		
		return false;
	}
}
