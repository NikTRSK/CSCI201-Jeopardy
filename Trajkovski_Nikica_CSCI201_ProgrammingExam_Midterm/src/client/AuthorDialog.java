package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AuthorDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public AuthorDialog(JFrame parent, String name, String aphoto) {
		super(parent, "View Author");
		setLayout(new FlowLayout());
//		getContentPane().setSize(new Dimension(400, 300));
//		Point p = new Point(400, 400);
//		setLocation(p.x, p.y);

		// Create a message
//		JPanel messagePane = new JPanel();
//		messagePane.add(new JLabel("Author of this factory: " + name));
		// get content pane, which is usually the
		// Container of all the dialog's components.
//		getContentPane().add(messagePane);
		

		// Create a button
//		JPanel photoPane = new JPanel();
		ImageIcon photo = new ImageIcon(aphoto);
//		photoPane.add(new JLabel(photo, SwingConstants.CENTER));
		// set action listener on the button\
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(new JLabel("Author of this factory: " + name, SwingConstants.CENTER));
		add(new JLabel(photo, SwingConstants.CENTER));
		pack();
		setVisible(true);
	}
}
