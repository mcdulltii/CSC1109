package com.atm;

import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.UIManager;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;
import com.atm.frontend.GUIButton;


public class ATMGUI extends JFrame {
	private Client client;
	private int loginRounds;
	private String username;
	private String password;
	private String[] credDisplay;

	public ATMGUI() {
		super("ATM");
		this.resetVariables();

		// Basic Constructor Setup
		setResizable(false);
		setLocationRelativeTo(null);
		initComponents();
		setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.client = this.connectClient();
	}

	private void resetVariables() {
		this.loginRounds = 0;
		this.username = "";
		this.password = "";
	}

	private Client connectClient() {
		// Connect to ATM server
        client = new Client("127.0.0.1", 7777, false);
        client.startConnection();
        client.sendMessage(null); // Receive server prompt
		ReceivedMessage recvMsg = client.receiveMessage();
		this.formatCredMessage(recvMsg.msg);
		this.updateCredDisplayArea();
		return client;
	}

	private void formatCredMessage(String msg) {
		this.credDisplay = msg.split("\n");
		this.credDisplay = Arrays.copyOf(this.credDisplay, this.credDisplay.length + 4);
		// Replace server reply
		int length = this.credDisplay.length;
		this.credDisplay[length-5] = "Enter Card number:";
		this.credDisplay[length-3] = "";
		this.credDisplay[length-2] = "Enter Pin number:";
	}

	private void updateCredDisplayArea() {
		int length = this.credDisplay.length;
		this.credDisplay[length-4] = this.username;
		this.credDisplay[length-1] = this.password;
		this.updateDisplayArea(String.join("\n", this.credDisplay));
	}

	private String sendUsernamePassword() {
		String authReply = null;
		int numTries = client.getNumTries();
		if (numTries < 2) {
			// Only allow 3 tries for user authentication
			if (this.username != null && this.username.length() > 0 &&
				this.password != null && this.password.length() > 0) {
				authReply = client.sendUsernamePassword(username, password);
			} else {
				// Popup invalid input window
				JOptionPane.showMessageDialog(null, "Failed to read input!");
				return null;
			}
		} else {
			// Exit when user hits 3 tries
			JOptionPane.showMessageDialog(null, "No attempts remaining!\n Terminating program.");
			this.exitWindow();
		}

		// Check server authentication
		if (authReply != null && authReply.contains("User authenticated")) {
			// Go to ATM app
			return authReply;
		} else {
			JOptionPane.showMessageDialog(null, "Username password combination is incorrect!\n" + (2 - numTries) + " attempts remaining!");
			return null;
		}
	}

	private void exitWindow() {
        client.close();
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void numberButtonMouseClicked(MouseEvent e, JButton[] numberButtons){
		for(int i=0; i<10; i++){
			if (e.getSource() == numberButtons[i]){
				String inputText = inputArea.getText();
				// Limit input character length
				if (inputText.length() <= 16)
					inputArea.setText(inputText.concat(String.valueOf(i)));
			}
		}
	}

	private void updateDisplayArea(String recvMsg) {
		String displayTextStart = "<html><p>";
		String displayTextEnd = "</p></html>";
		this.displayArea.setText(displayTextStart +
								 recvMsg.replaceAll("\n", "<br/>") +
								 displayTextEnd);
	}

	private void buttonBackMouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void buttonClearMouseClicked(MouseEvent e) {
		// TODO add your code here
		inputArea.setText("");
		// Need to reconfigure server to return to selection menu
		// ReceivedMessage recvMsg = client.sendMessage(null);
		// this.updateDisplayArea(recvMsg.msg);
	}

	private void buttonEnterMouseClicked(MouseEvent e) {
		switch (this.loginRounds) {
			case 0:
				// Save input as username
				this.username = inputArea.getText();
				inputArea.setText("");
				this.updateCredDisplayArea();
				this.loginRounds++;
				break;
			case 1:
				// Save input as password
				this.password = inputArea.getText();
				inputArea.setText("");
				this.updateCredDisplayArea();
				// Send username password to server
				String authReply = this.sendUsernamePassword();
				if (authReply == null) {
					// Username password combination fails
					this.resetVariables();
					this.updateCredDisplayArea();
				} else {
					// Username password combination passes
					this.loginRounds = 2;
					String[] reply = authReply.split("\n", 2);
					this.updateDisplayArea(reply[1]);
				}
				break;
			case 2:
			default:
				// Authentication loop passes, reached main input loop
				String input = inputArea.getText();
				inputArea.setText("");
				ReceivedMessage recvMsg = client.sendMessage(input);
				this.updateDisplayArea(recvMsg.msg);
				break;
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		displayArea = new JLabel();
		inputArea = new JLabel();
		separator = new JSeparator();
		button1 = new GUIButton();
		button2 = new GUIButton();
		button3 = new GUIButton();
		button4 = new GUIButton();
		button5 = new GUIButton();
		button6 = new GUIButton();
		button7 = new GUIButton();
		button8 = new GUIButton();
		button9 = new GUIButton();
		button0 = new GUIButton();
		buttonBack = new GUIButton();
		buttonClear = new GUIButton();
		buttonEnter = new GUIButton();

		JButton[] numberButtons = new JButton[10];
		numberButtons[0] = button0;
		numberButtons[1] = button1;
		numberButtons[2] = button2;
		numberButtons[3] = button3;
		numberButtons[4] = button4;
		numberButtons[5] = button5;
		numberButtons[6] = button6;
		numberButtons[7] = button7;
		numberButtons[8] = button8;
		numberButtons[9] = button9;

		Color bgColor = new Color(0x39,0x30,0x53);
		displayArea.setFont(new Font("Arial", Font.PLAIN, 18));
		displayArea.setOpaque(true);
		displayArea.setBackground(bgColor);
		displayArea.setForeground(Color.WHITE);

		inputArea.setFont(new Font("Arial", Font.PLAIN, 32));
		inputArea.setOpaque(true);
		inputArea.setBackground(bgColor);
		inputArea.setForeground(Color.WHITE);
		inputArea.setHorizontalAlignment(SwingConstants.CENTER);
		inputArea.setVerticalAlignment(SwingConstants.CENTER);

		for (int i=0; i<10; i++){
			numberButtons[i].setBackground(Color.DARK_GRAY);
			numberButtons[i].setForeground(Color.WHITE);
		}
		Color DARK_BROWN = new Color(0xF2, 0xD3, 0x88);
		buttonBack.setBackground(DARK_BROWN);
		buttonBack.setForeground(Color.BLACK);
		Color DARK_RED = new Color(0xCC,0x36,0x36);
		buttonClear.setBackground(DARK_RED);
		buttonClear.setForeground(Color.BLACK);
		Color DARK_GREEN = new Color(0x36,0x7E,0x18);
		buttonEnter.setBackground(DARK_GREEN);
		buttonEnter.setForeground(Color.WHITE);

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new MigLayout(
			"hidemode 3",
			// columns
			"[fill]" +
			"[300,fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[75,fill]" +
			"[fill]" +
			"[fill]" +
			"[75,fill]" +
			"[fill]" +
			"[fill]" +
			"[75,fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]",
			// rows
			"[]" +
			"[180,fill]" +
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
		Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 5);
		displayArea.setBorder(border);
		inputArea.setBorder(border);
		contentPane.add(displayArea, "cell 1 1 1 17,height 420:420:420");
		contentPane.add(inputArea, "cell 5 1 10 2,height 180:180:180");
		contentPane.add(separator, "cell 3 0 1 16");

		//---- number buttons ----
		for (int i=0; i<10; i++){
			numberButtons[i].setText(Integer.toString(i));
			numberButtons[i].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					numberButtonMouseClicked(e, numberButtons);
				}
			});
		}
		contentPane.add(numberButtons[0], "cell 9 12 2 2");
		contentPane.add(numberButtons[1], "cell 6 3 2 2");
		contentPane.add(numberButtons[2], "cell 9 3 2 2");
		contentPane.add(numberButtons[3], "cell 12 3 2 2");
		contentPane.add(numberButtons[4], "cell 6 6 2 2");
		contentPane.add(numberButtons[5], "cell 9 6 2 2");
		contentPane.add(numberButtons[6], "cell 12 6 2 2");
		contentPane.add(numberButtons[7], "cell 6 9 2 2");
		contentPane.add(numberButtons[8], "cell 9 9 2 2");
		contentPane.add(numberButtons[9], "cell 12 9 2 2");

		//---- buttonBack ----
		buttonBack.setText("Back");
		buttonClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buttonBackMouseClicked(e);
			}
		});
		contentPane.add(buttonBack, "cell 6 15 1 2");

		//---- buttonClear ----
		buttonClear.setText("Clear");
		buttonClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buttonClearMouseClicked(e);
			}
		});
		contentPane.add(buttonClear, "cell 9 15 2 2");

		//---- buttonEnter ----
		buttonEnter.setText("Enter");
		buttonEnter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buttonEnterMouseClicked(e);
			}
		});
		contentPane.add(buttonEnter, "cell 12 15 2 2");
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	private JLabel displayArea;
	private JLabel inputArea;
	private JSeparator separator;
	private GUIButton button1;
	private GUIButton button2;
	private GUIButton button3;
	private GUIButton button4;
	private GUIButton button5;
	private GUIButton button6;
	private GUIButton button7;
	private GUIButton button8;
	private GUIButton button9;
	private GUIButton button0;
	private GUIButton buttonBack;
	private GUIButton buttonClear;
	private GUIButton buttonEnter;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

	public static void main(String args[]) {
		// Set GUI theme
		UIManager.put( "control", new Color( 60, 60, 60) );
		UIManager.put( "info", new Color( 60,60,60) );
		UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
		UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
		UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
		UIManager.put( "nimbusFocus", new Color(115,164,209) );
		UIManager.put( "nimbusGreen", new Color(176,179,50) );
		UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
		UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
		UIManager.put( "nimbusOrange", new Color(191,98,4) );
		UIManager.put( "nimbusRed", new Color(169,46,34) );
		UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
		UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
		UIManager.put( "text", new Color( 230, 230, 230) );
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		// Instantiate ATM GUI
		new ATMGUI();
    }
}
