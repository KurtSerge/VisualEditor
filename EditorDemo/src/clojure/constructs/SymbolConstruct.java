package clojure.constructs;

import java.awt.Color;

import clojure.ClojureConstruct;
import editor.Construct;

public class SymbolConstruct extends ClojureConstruct {
	/**
	 * Mutable.
	 * 
	 * @param parent
	 * @param literal
	 */
	public SymbolConstruct(Construct parent, String literal) { 
		this(parent, literal, true);
	}
	
	/**
	 * Support for immutable symbols.
	 * 
	 * @param parent
	 * @param symbol
	 * @param mutable
	 */
	public SymbolConstruct(Construct parent, String symbol, boolean mutable) { 
		super("symbol", parent);
		
		if(mutable == false) { 
			this.mMutable = false;
			this.literal = null;
			this.mImmutableSymbol = symbol;
		} else { 
			this.mMutable = true;
			this.literal = symbol;
			this.mImmutableSymbol = null;
		}
	}

	@Override
	public String screen_text() {
		return mImmutableSymbol;
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override
	public Color debug_getForegroundColor() { 
		if(mImmutableSymbol == null) { 
			return new Color(36, 26, 196);
		} else { 
			return new Color(0, 153, 0);
		}
	}
	
	private boolean mMutable;
	private String mImmutableSymbol;
	
	
	@Override
	public Construct deepCopy(Construct parent) {
		SymbolConstruct newCopy;
		
		if(mMutable) { 
			newCopy = new SymbolConstruct(parent, this.literal, true);
		} else { 
			newCopy = new SymbolConstruct(parent, mImmutableSymbol, false);
		}
		
		super.deepCopy(newCopy);
		return newCopy;
	}
}
