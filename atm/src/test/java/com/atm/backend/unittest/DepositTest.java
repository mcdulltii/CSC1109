package com.atm.backend.unittest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atm.backend.Account;
import com.atm.backend.Deposit;

public class DepositTest {
    UnitTestSuite uts;
    Deposit depositTrans;

    // Account methods
    Method getAccountNumber;
    Method getAvailableBalance;
    Method getTotalBalance;
    Method getTransferLimit;
    Method getDeposit;

    @Before
    public void setUp() throws SQLException {
        uts = new UnitTestSuite();
        depositTrans = new Deposit(uts.acc, uts.mock.connection);

        getAccountNumber = uts.protectedAccMethods.get("getAccountNumber");
        getAvailableBalance = uts.protectedAccMethods.get("getAvailableBalance");
        getTotalBalance = uts.protectedAccMethods.get("getTotalBalance");
        getTransferLimit = uts.protectedAccMethods.get("getTransferLimit");
        getDeposit = uts.protectedAccMethods.get("getDeposit");
    }

    @After
    public void cleanUp() {
        reset(uts.mock.preStatement);
    }

    @Test
    public void testDeposit()
            throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException,
            SQLException, NoSuchFieldException {
        Method execute = depositTrans.getClass().getDeclaredMethod("execute", Account.class, double.class);
        execute.setAccessible(true);
        execute.invoke(depositTrans, uts.acc, 50);

        // Verify Account update made with right arguments
        verify(uts.mock.connection).prepareStatement(
                "UPDATE accounts SET TotalBalance = ?, AvailableBalance = ?, TransferLimit = ? WHERE AccountNumber = ?");
        verify(uts.mock.preStatement).setDouble(1, (double) getTotalBalance.invoke(uts.acc));
        verify(uts.mock.preStatement).setDouble(2, (double) getAvailableBalance.invoke(uts.acc));
        verify(uts.mock.preStatement).setDouble(3, (double) getTransferLimit.invoke(uts.acc));
        verify(uts.mock.preStatement).setLong(4, Long.parseLong((String) getAccountNumber.invoke(uts.acc)));
        verify((uts.mock.preStatement), times(1)).executeUpdate();

        // Verify Transaction update made with right arguments
        when(uts.mock.resultSet.next()).thenReturn(true, true, false);

        verify(uts.mock.statement)
                .executeQuery("SELECT transactionId FROM transactions ORDER BY transactionId desc limit 1");
        verify(uts.mock.resultSet, times(3)).next();
        verify(uts.mock.connection).prepareStatement(
                "INSERT INTO transactions (transactionId, accountNumber, transactionDate, transactionDetails, chqNumber, valueDate, withdrawal, deposit, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        verify(uts.mock.preStatement).setString(2, (String) getAccountNumber.invoke(uts.acc));
        verify(uts.mock.preStatement).setString(4, "ATM DEPOSIT/TRF");
        verify(uts.mock.preStatement).setDouble(8, 50);
        verify(uts.mock.preStatement).setDouble(9, 150050);

        assertEquals(150050, (double) getTotalBalance.invoke(uts.acc), 0);
    }
}
