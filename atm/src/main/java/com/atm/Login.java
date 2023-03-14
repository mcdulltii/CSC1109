package com.atm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.*;
import net.miginfocom.swing.MigLayout;

public class Login extends JFrame {
	private Client client;

	public Login() {
		super("Login Screen");
		// Connect to ATM server
        client = new Client("127.0.0.1", 7777, false);
        client.startConnection();
        client.sendMessage(null); // Receive server prompt
		client.receiveMessage();

		// Basic Constructor Setup
		setResizable(false);
		setLocationRelativeTo(null);
		initComponents();
		setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private void sendUsernamePassword(AWTEvent e) {
		String authReply = null;
		int numTries = client.getNumTries();
		if (numTries < 2) {
			// Only allow 3 tries for user authentication
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			if (username != null && username.length() > 0 &&
				password != null && password.length() > 0) {
				authReply = client.sendUsernamePassword(username, password);
			} else {
				// Popup invalid input window
				JOptionPane.showMessageDialog(null, "Failed to read input!");
				return;
			}
		} else {
			// Exit when user hits 3 tries
			JOptionPane.showMessageDialog(null, "No attempts remaining!\n Terminating program.");
			this.exitWindow(null);
		}

		// Check server authentication
		if (authReply != null && authReply.contains("User authenticated")) {
			// Go to ATM app
			dispose();
			new ATMGUI();
		} else {
			JOptionPane.showMessageDialog(null, "Username password combination is incorrect!\n" + (2 - numTries) + " attempts remaining!");
		}
	}

	private void exitWindow(MouseEvent e) {
        client.close();
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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
			"[70,fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[70,fill]" +
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
		promptLabel.setText("Enter card and pin number:");
		contentPane.add(promptLabel, "cell 1 3 15 2");
		contentPane.add(usernameField, "cell 2 7 18 5");

		//---- cardLabel ----
		cardLabel.setText("Card number:");
		contentPane.add(cardLabel, "cell 1 9");
		contentPane.add(passwordField, "cell 2 12 18 4");

		//---- pinLabel ----
		pinLabel.setText("Pin number:");
		contentPane.add(pinLabel, "cell 1 13");

		//---- passwordField ----
		passwordField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				sendUsernamePassword(e);
			}
		});

		//---- okButton ----
		okButton.setText("Ok");
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendUsernamePassword(e);
			}
		});
		contentPane.add(okButton, "cell 2 20");

		//---- exitButton ----
		exitButton.setText("Exit");
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exitWindow(e);
			}
		});
		contentPane.add(exitButton, "cell 7 20");
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

    public static void main(String args[]) {
		// Set GUI theme
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("GTK+".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		// Instantiate login UI
		new Login();
    }
}
