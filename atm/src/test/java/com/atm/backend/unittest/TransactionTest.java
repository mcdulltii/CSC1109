// package com.atm.backend.unittest;

// import static org.junit.Assert.assertEquals;
// import static org.mockito.Mockito.reset;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Method;
// import java.sql.SQLException;

// import org.junit.After;
// import org.junit.Before;
// import org.junit.Test;

// import com.atm.backend.Account;
// import com.atm.backend.Transaction;

// public class TransactionTest {
//     UnitTestSuite uts;
//     Transaction transaction;

//     // Account methods
//     Method getAccountNumber;
//     Method getAvailableBalance;
//     Method getTotalBalance;
//     Method getTransferLimit;

//     @Before
//     public void setUp() throws SQLException {
//         uts = new UnitTestSuite();
//         transaction = new Transaction(uts.acc, uts.mock.connection);

//         getAccountNumber = uts.protectedAccMethods.get("getAccountNumber");
//         getAvailableBalance = uts.protectedAccMethods.get("getAvailableBalance");
//         getTotalBalance = uts.protectedAccMethods.get("getTotalBalance");
//         getTransferLimit = uts.protectedAccMethods.get("getTransferLimit");
//     }

//     @After
//     public void cleanUp() {
//         reset(uts.mock.preStatement);
//     }

//     @Test
//     public void testTransferToAccount() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//             InvocationTargetException, SQLException, NoSuchFieldException {
//         // Set second account for test transfer
//         Account testTransferAcc = new Account("4444333322221111", 8000, 100000, 2000);

//         Method transfer = transaction.getClass().getDeclaredMethod("transferToAccount", Account.class, Account.class,
//                 double.class);
//         transfer.setAccessible(true);
//         transfer.invoke(transaction, uts.acc, testTransferAcc, 50);

//         // Verify Account update
//         verify(uts.mock.connection).prepareStatement(
//                 "UPDATE accounts SET TotalBalance = ?, AvailableBalance = ?, TransferLimit = ? WHERE AccountNumber = ?");
//         verify(uts.mock.preStatement).setDouble(1, (double) getTotalBalance.invoke(uts.acc));
//         verify(uts.mock.preStatement).setDouble(2, (double) getAvailableBalance.invoke(uts.acc));
//         verify(uts.mock.preStatement).setDouble(3, (double) getTransferLimit.invoke(uts.acc));
//         verify(uts.mock.preStatement).setLong(4, Long.parseLong((String) getAccountNumber.invoke(uts.acc)));
//         verify(uts.mock.preStatement).setDouble(1, (double) getTotalBalance.invoke(testTransferAcc));
//         verify(uts.mock.preStatement).setDouble(2, (double) getAvailableBalance.invoke(testTransferAcc));
//         verify(uts.mock.preStatement).setDouble(3, (double) getTransferLimit.invoke(testTransferAcc));
//         verify(uts.mock.preStatement).setLong(4, Long.parseLong((String) getAccountNumber.invoke(testTransferAcc)));
//         verify((uts.mock.preStatement), times(2)).executeUpdate();

//         // Verify Transaction update
//         when(uts.mock.resultSet.next()).thenReturn(true, true, false);

//         verify(uts.mock.statement, times(2))
//                 .executeQuery("SELECT transactionId FROM transactions ORDER BY transactionId desc limit 1");
//         verify(uts.mock.connection, times(2)).prepareStatement(
//                 "INSERT INTO transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
//         verify(uts.mock.preStatement).setString(2, (String) getAccountNumber.invoke(uts.acc));
//         verify(uts.mock.preStatement).setString(4, "TRF TO " + (String) getAccountNumber.invoke(testTransferAcc));
//         verify(uts.mock.preStatement).setDouble(7, 50);
//         verify(uts.mock.preStatement).setDouble(9, 149950);
//         verify(uts.mock.preStatement).setString(2, (String) getAccountNumber.invoke(testTransferAcc));
//         verify(uts.mock.preStatement).setString(4, "TRF FROM " + (String) getAccountNumber.invoke(uts.acc));
//         verify(uts.mock.preStatement).setDouble(8, 50);
//         verify(uts.mock.preStatement).setDouble(9, 100050);

//         assertEquals(149950, (double) getTotalBalance.invoke(uts.acc), 0);
//         assertEquals(1950, (double) getTransferLimit.invoke(uts.acc), 0);
//         assertEquals(100050, (double) getTotalBalance.invoke(testTransferAcc), 0);
//     }

//     @Test
//     public void testDeposit()
//             throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException,
//             SQLException, NoSuchFieldException {
//         Method deposit = transaction.getClass().getDeclaredMethod("deposit", Account.class, double.class);
//         deposit.setAccessible(true);
//         deposit.invoke(transaction, uts.acc, 50);

//         // Verify Account update
//         verify(uts.mock.connection).prepareStatement(
//                 "UPDATE accounts SET TotalBalance = ?, AvailableBalance = ?, TransferLimit = ? WHERE AccountNumber = ?");
//         verify(uts.mock.preStatement).setDouble(1, (double) getTotalBalance.invoke(uts.acc));
//         verify(uts.mock.preStatement).setDouble(2, (double) getAvailableBalance.invoke(uts.acc));
//         verify(uts.mock.preStatement).setDouble(3, (double) getTransferLimit.invoke(uts.acc));
//         verify(uts.mock.preStatement).setLong(4, Long.parseLong((String) getAccountNumber.invoke(uts.acc)));
//         verify((uts.mock.preStatement), times(1)).executeUpdate();

//         // Verify Transaction update
//         when(uts.mock.resultSet.next()).thenReturn(true, true, false);

//         verify(uts.mock.statement)
//                 .executeQuery("SELECT transactionId FROM transactions ORDER BY transactionId desc limit 1");
//         verify(uts.mock.resultSet, times(3)).next();
//         verify(uts.mock.connection).prepareStatement(
//                 "INSERT INTO transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
//         verify(uts.mock.preStatement).setString(2, (String) getAccountNumber.invoke(uts.acc));
//         verify(uts.mock.preStatement).setString(4, "ATM DEPOSIT");
//         verify(uts.mock.preStatement).setDouble(8, 50);
//         verify(uts.mock.preStatement).setDouble(9, 150050);

//         assertEquals(150050, (double) getTotalBalance.invoke(uts.acc), 0);
//     }

//     @Test
//     public void testWithdraw() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//             InvocationTargetException, SQLException {
//         Method withdraw = transaction.getClass().getDeclaredMethod("withdraw", Account.class, double.class);
//         withdraw.setAccessible(true);
//         withdraw.invoke(transaction, uts.acc, 50);

//         // Verify Account update
//         verify(uts.mock.connection).prepareStatement(
//                 "UPDATE accounts SET TotalBalance = ?, AvailableBalance = ?, TransferLimit = ? WHERE AccountNumber = ?");
//         verify(uts.mock.preStatement).setDouble(1, (double) getTotalBalance.invoke(uts.acc));
//         verify(uts.mock.preStatement).setDouble(2, (double) getAvailableBalance.invoke(uts.acc));
//         verify(uts.mock.preStatement).setDouble(3, (double) getTransferLimit.invoke(uts.acc));
//         verify(uts.mock.preStatement).setLong(4, Long.parseLong((String) getAccountNumber.invoke(uts.acc)));

//         // Verify Transaction update
//         when(uts.mock.resultSet.next()).thenReturn(true, true, false);

//         verify(uts.mock.statement)
//                 .executeQuery("SELECT transactionId FROM transactions ORDER BY transactionId desc limit 1");
//         verify(uts.mock.resultSet, times(3)).next();
//         verify(uts.mock.connection).prepareStatement(
//                 "INSERT INTO transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
//         verify(uts.mock.preStatement).setString(2, (String) getAccountNumber.invoke(uts.acc));
//         verify(uts.mock.preStatement).setString(4, "ATM WITHDRAWAL");
//         verify(uts.mock.preStatement).setDouble(7, 50);
//         verify(uts.mock.preStatement).setDouble(9, 149950);

//         assertEquals(149950, (double) getTotalBalance.invoke(uts.acc), 0);
//     }
// }
