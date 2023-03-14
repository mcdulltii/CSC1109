package com.atm;

import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;


public class ATMGUI extends JFrame {
	private Client client;

	public ATMGUI(Client client) {
		super("ATM");
		this.client = client;
		
		// Basic Constructor Setup
		setResizable(false);
		setLocationRelativeTo(null);
		initComponents();
		setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public ATMGUI(Client client, String username) {
		super("ATM - " + username);
		this.client = client;

		// Basic Constructor Setup
		setResizable(false);
		setLocationRelativeTo(null);
		initComponents();
		setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private void button1MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button2MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button3MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button4MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button5MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button6MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button7MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button8MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button9MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void button0MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void buttonClearMouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void buttonEnterMouseClicked(MouseEvent e) {
		// TODO add your code here
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
		buttonClear = new GUIButton();
		buttonEnter = new GUIButton();

		Color bgColor = new Color(0x39,0x30,0x53);
		displayArea.setOpaque(true);
		displayArea.setBackground(bgColor);
		inputArea.setOpaque(true);
		inputArea.setBackground(bgColor);
		button1.setBackground(Color.DARK_GRAY);
		button2.setBackground(Color.DARK_GRAY);
		button3.setBackground(Color.DARK_GRAY);
		button4.setBackground(Color.DARK_GRAY);
		button5.setBackground(Color.DARK_GRAY);
		button6.setBackground(Color.DARK_GRAY);
		button7.setBackground(Color.DARK_GRAY);
		button8.setBackground(Color.DARK_GRAY);
		button9.setBackground(Color.DARK_GRAY);
		button0.setBackground(Color.DARK_GRAY);
		Color DARK_RED = new Color(0xCC,0x36,0x36);
		buttonClear.setBackground(DARK_RED);
		Color DARK_GREEN = new Color(0x36,0x7E,0x18);
		buttonEnter.setBackground(DARK_GREEN);

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
			"[]"));
		Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 5);
		displayArea.setBorder(border);
		inputArea.setBorder(border);
		contentPane.add(displayArea, "cell 1 1 1 14,height 360:360:360");
		contentPane.add(inputArea, "cell 5 1 10 2,height 180:180:180");
		contentPane.add(separator, "cell 3 0 1 16");

		//---- button1 ----
		button1.setText("1");
		button1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button1MouseClicked(e);
			}
		});
		contentPane.add(button1, "cell 6 3 2 2");

		//---- button2 ----
		button2.setText("2");
		button2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button2MouseClicked(e);
			}
		});
		contentPane.add(button2, "cell 9 3 2 2");

		//---- button3 ----
		button3.setText("3");
		button3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button3MouseClicked(e);
			}
		});
		contentPane.add(button3, "cell 12 3 2 2");

		//---- button4 ----
		button4.setText("4");
		button4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button4MouseClicked(e);
			}
		});
		contentPane.add(button4, "cell 6 6 2 2");

		//---- button5 ----
		button5.setText("5");
		button5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button5MouseClicked(e);
			}
		});
		contentPane.add(button5, "cell 9 6 2 2");

		//---- button6 ----
		button6.setText("6");
		button6.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button6MouseClicked(e);
			}
		});
		contentPane.add(button6, "cell 12 6 2 2");

		//---- button7 ----
		button7.setText("7");
		button7.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button7MouseClicked(e);
			}
		});
		contentPane.add(button7, "cell 6 9 2 2");

		//---- button8 ----
		button8.setText("8");
		button8.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button8MouseClicked(e);
			}
		});
		contentPane.add(button8, "cell 9 9 2 2");

		//---- button9 ----
		button9.setText("9");
		button9.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button9MouseClicked(e);
			}
		});
		contentPane.add(button9, "cell 12 9 2 2");

		//---- button0 ----
		button0.setText("0");
		button0.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button0MouseClicked(e);
			}
		});
		contentPane.add(button0, "cell 6 12 2 2");

		//---- buttonClear ----
		buttonClear.setText("Clear");
		buttonClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buttonClearMouseClicked(e);
			}
		});
		contentPane.add(buttonClear, "cell 9 12 2 2");

		//---- buttonEnter ----
		buttonEnter.setText("Enter");
		buttonEnter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buttonEnterMouseClicked(e);
			}
		});
		contentPane.add(buttonEnter, "cell 12 12 2 2");
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
	private GUIButton buttonClear;
	private GUIButton buttonEnter;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
