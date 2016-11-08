package GUI;

import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class dialogBox extends JOptionPane {
	private static final long serialVersionUID = 1L;
	public dialogBox() {
		UIManager.put("OptionPane.background", new Color(0,150,136));
		UIManager.put("Panel.background", new Color(0,150,136));
		UIManager.put("Panel.foreground", Color.WHITE);
		UIManager.put("Button.background", new Color(39,40,34));
		UIManager.put("Button.foreground", Color.WHITE);
	}
}