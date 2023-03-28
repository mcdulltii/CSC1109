package com.atm.frontend;

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AdminTable extends JFrame {

    public void displayTable(String s, ArrayList<String[]> data) {
        // headers for the table
        String[] headers = {};
        if (s.equalsIgnoreCase("Accounts")) {
            headers = new String[] {
                    "Card Number", "Account Number", "Username", "Password", "First Name", "Last Name", "Password Salt",
                    "Available Balance", "Total Balance",
                    "Transfer Limit", "IsAdmin"
            };
        } else if (s.equalsIgnoreCase("Transactions")) {
            headers = new String[] {
                    "Transaction ID", "Account Number", "Transaction Date", "Transaction Details", "Cheque Number",
                    "Date", "Withdrawal", "Deposit", "Balance"
            };
        }

        // format data
        String[][] formattedData = new String[data.size()][];
        for (int i = 0; i<data.size(); i++) {
            formattedData[i] = data.get(i);
        }

        // create table with data
        TableModel model = new DefaultTableModel(formattedData, headers) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model) {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension(super.getPreferredSize().width,
                    getRowHeight() * data.size());
            }
            
            @Override
            public boolean getScrollableTracksViewportWidth()
            {
                return getPreferredSize().width < getParent().getWidth();
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // set frame size
        Dimension DimMax = Toolkit.getDefaultToolkit().getScreenSize();
        this.setMaximumSize(DimMax);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.add(new JScrollPane(table));
        this.setTitle(s);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}
