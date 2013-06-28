import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Editor  {
    public static void main(String[] args) {
	    // Text area
	    JTextArea textArea = new JTextArea("toScreenText", 30, 50);
	    Font monofont = new Font("Courier New", Font.PLAIN, 12);
	    textArea.setFont(monofont);
	    textArea.setEditable(false);
	    textArea.setPreferredSize(new Dimension(100, 100));
	    // Scroll
	    JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    textArea.setLineWrap(true);
	    // Frame
		JFrame.setDefaultLookAndFeelDecorated(true);
	    JFrame frame = new JFrame("Editor");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(scrollPane);
	    frame.pack();
	    frame.setVisible(true);
    }
}
