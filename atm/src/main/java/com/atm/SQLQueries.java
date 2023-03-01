package com.atm;
import java.sql.*;
public class SQLQueries {

    public void executeQueryTransactions(String transactionId, String accountNumber, Date transactionDate, String transactionDetails, String chqNumber, Date valueDate, Double withdrawal, Double deposit, Double balance){
        final String db_url="jdbc:mysql://localhost:3306/oopasgdb";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn=DriverManager.getConnection(db_url, "testAdmin", "password1");
            //Statement statement=conn.createStatement();
            // ResultSet rs=statement.executeQuery(query);
            // while (rs.next()){
            //     System.out.println(rs.getString(1));
            // }
            String sql = " insert into transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, transactionId);
            preparedStmt.setString(2, accountNumber);
            preparedStmt.setDate(3, transactionDate);
            preparedStmt.setString(4, transactionDetails);
            preparedStmt.setString(5, chqNumber);
            preparedStmt.setDate(6, valueDate);
            preparedStmt.setDouble(7, withdrawal);
            preparedStmt.setDouble(8, deposit);
            preparedStmt.setDouble(9, balance);
            preparedStmt.execute();
            conn.close();
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
