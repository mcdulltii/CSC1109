package com.atm.backend.unittest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class AccUserObjTest {
    UnitTestSuite uts;

    // Account methods
    Method getAccountNumber;
    Method getAvailableBalance;
    Method getTotalBalance;
    Method getTransferLimit;
    // User methods
    Method getFirstName;
    Method getLastName;
    Method getIsAdmin;

    @Before
    public void setUp() throws SQLException {
        uts = new UnitTestSuite();

        getAccountNumber = uts.protectedAccMethods.get("getAccountNumber");
        getAvailableBalance = uts.protectedAccMethods.get("getAvailableBalance");
        getTotalBalance = uts.protectedAccMethods.get("getTotalBalance");
        getTransferLimit = uts.protectedAccMethods.get("getTransferLimit");

        getFirstName = uts.protectedUserMethods.get("getFirstName");
        getLastName = uts.protectedUserMethods.get("getLastName");
        getIsAdmin = uts.protectedUserMethods.get("getIsAdmin");
    }

    @Test
    public void testGetAccountfromUsername() throws SQLException, IllegalAccessException, InvocationTargetException {
        // verify executeQuery method was called with correct SQL statement
        verify(uts.mock.statement).executeQuery("SELECT * FROM accounts WHERE UserName = \"tia\"");

        // verify the ResultSet.next method was called two times
        verify(uts.mock.resultSet, times(2)).next();

        // verify the ResultSet get methods was called with the right column label
        verify(uts.mock.resultSet).getLong("AccountNumber");
        verify(uts.mock.resultSet).getString("FirstName");
        verify(uts.mock.resultSet).getString("LastName");
        verify(uts.mock.resultSet).getDouble("AvailableBalance");
        verify(uts.mock.resultSet).getDouble("TotalBalance");
        verify(uts.mock.resultSet).getDouble("TransferLimit");
        verify(uts.mock.resultSet).getInt("IsAdmin");

        // verify the returned objects has the right properties
        assertEquals("409000438620", (String) getAccountNumber.invoke(uts.acc));
        assertEquals(10000.00, (double) getAvailableBalance.invoke(uts.acc), 0);
        assertEquals(150000.00, (double) getTotalBalance.invoke(uts.acc), 0);
        assertEquals(2000.00, (double) getTransferLimit.invoke(uts.acc), 0);
        assertEquals("Tia", (String) getFirstName.invoke(uts.user));
        assertEquals("Ackermann", (String) getLastName.invoke(uts.user));
        assertEquals(0, (int) getIsAdmin.invoke(uts.user));
    }
}
