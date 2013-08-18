package autocomplete;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class CellRenderer extends JTextArea implements ListCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static UIDefaults defaults = UIManager.getLookAndFeelDefaults();

	public CellRenderer()
	{
		super();
		setEditable(false);
		setCursor(null);
		setOpaque(true);
		setFocusable(false);
		setLineWrap(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
	      int index, boolean isSelected, boolean cellHasFocus)
	{
		this.setMaximumSize(new Dimension(list.getWidth(), Integer.MAX_VALUE));
		if (isSelected || cellHasFocus)
		{
		    this.setBackground(defaults.getColor("TextArea.selectionBackground"));
		    this.setForeground(defaults.getColor("TextArea.selectionForeground"));
		}
		else
		{
		    this.setBackground(defaults.getColor("TextArea.background"));
		    this.setForeground(defaults.getColor("TextArea.foreground"));
		}
		revalidate();
		return this;
	}

}