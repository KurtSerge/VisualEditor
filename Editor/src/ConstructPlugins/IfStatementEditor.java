package ConstructPlugins;

import java.awt.Component;
import java.awt.Dimension;

import EditorFramework.Construct;
import EditorFramework.MonospaceConstructEditor;

public class IfStatementEditor extends MonospaceConstructEditor {
	public IfStatementEditor(Construct construct) {
		super(construct);
	}

	@Override
	protected String toScreenText() {
		// #node# can be any editor
		return "if(#node#)\n{#node#}";
	}
}
