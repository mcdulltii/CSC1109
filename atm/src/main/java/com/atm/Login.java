package com.atm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class Login extends JFrame {
	public Login() {
		//Basic Constructor Setup
		super("Login Screen");
		setResizable(false);
		setLocationRelativeTo(null);
		initComponents();
		setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private void sendUsernamePassword(MouseEvent e) {
		// TODO add your code here
	}

	private void exitWindow(MouseEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		promptLabel = new JLabel();
		usernameField = new JTextField();
		cardLabel = new JLabel();
		passwordField = new JPasswordField();
		pinLabel = new JLabel();
		okButton = new JButton();
		exitButton = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new MigLayout(
			"hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- promptLabel ----
		promptLabel.setText("Enter card number and pin:");
		contentPane.add(promptLabel, "cell 1 3 15 2");
		contentPane.add(usernameField, "cell 2 7 18 5");

		//---- cardLabel ----
		cardLabel.setText("Card number:");
		contentPane.add(cardLabel, "cell 1 9");
		contentPane.add(passwordField, "cell 2 12 18 3");

		//---- pinLabel ----
		pinLabel.setText("Pin number:");
		contentPane.add(pinLabel, "cell 1 13");

		//---- okButton ----
		okButton.setText("Ok");
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendUsernamePassword(e);
			}
		});
		contentPane.add(okButton, "cell 2 19");

		//---- exitButton ----
		exitButton.setText("Exit");
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exitWindow(e);
			}
		});
		contentPane.add(exitButton, "cell 7 19");
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	private JLabel promptLabel;
	private JTextField usernameField;
	private JLabel cardLabel;
	private JPasswordField passwordField;
	private JLabel pinLabel;
	private JButton okButton;
	private JButton exitButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
