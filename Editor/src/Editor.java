
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class Editor  {
    public static void main(String[] args) {

	    // Text area
	    JTextArea textArea = new JTextArea("toScreenText");
	    Font monofont = new Font("Courier New", Font.PLAIN, 12);
	    textArea.setFont(monofont);
	    textArea.setEditable(false);
	    textArea.setLineWrap(false);
	    // Scroll
	    JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    // Frame
		JFrame.setDefaultLookAndFeelDecorated(true);
	    JFrame frame = new JFrame("Editor");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(scrollPane);
	    frame.add(textArea);
	    frame.pack();
	    frame.setVisible(true);
    }
}
