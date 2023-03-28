package com.atm.backend.unittest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atm.backend.Authenticate;
import com.atm.backend.Settings;

public class SettingsTest {
    static UnitTestSuite uts;
    static Settings accSettings;
    static Settings usrSettings;

    // Account methods
    static Method getAccountNumber;
    static Method getTransferLimit;
    // User methods
    static Method getPin;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        uts = new UnitTestSuite();
        usrSettings = new Settings(uts.user, uts.mock.connection);
        accSettings = new Settings(uts.acc, uts.mock.connection);

        getAccountNumber = uts.protectedAccMethods.get("getAccountNumber");
        getTransferLimit = uts.protectedAccMethods.get("getTransferLimit");

        getPin = uts.protectedUserMethods.get("getPin");
    }

    @After
    public void cleanUp() {
        reset(uts.mock.preStatement);
    }

    @Test
    public void testSetPinNumber() throws SQLException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Authenticate auth = mock(Authenticate.class);

        // Set to use Mock instance
        Field authField = usrSettings.getClass().getDeclaredField("auth");
        authField.setAccessible(true);
        authField.set(usrSettings, auth);

        // Control the behaviour
        byte[] mockNonce = new byte[] { 1, 2, 3 };
        Method getRandomNonce = auth.getClass().getDeclaredMethod("getRandomNonce");
        getRandomNonce.setAccessible(true);
        when(getRandomNonce.invoke(auth)).thenReturn(mockNonce);

        String pinNum = "234567";
        usrSettings.setPinNumber(pinNum);

        // Verify queries statement is made with right arguments
        verify(uts.mock.connection).prepareStatement("UPDATE accounts SET Password = ? WHERE AccountNumber = ?");
        verify(uts.mock.preStatement).setString(1, (String) getPin.invoke(uts.user));
        verify(uts.mock.connection).prepareStatement("UPDATE accounts SET PasswordSalt = ? WHERE AccountNumber = ?");
        verify(uts.mock.preStatement, times(1)).setBytes(1, mockNonce);
        verify(uts.mock.preStatement, times(2)).setLong(2, Long.parseLong((String) getAccountNumber.invoke(uts.acc)));
        verify((uts.mock.preStatement), times(2)).executeUpdate();

        assertEquals(auth.hashString(pinNum, mockNonce), getPin.invoke(uts.user));
    }

    @Test
    public void testSetTransferLimit() throws SQLException, IllegalAccessException, InvocationTargetException {
        accSettings.setTransferLimit(5000);

        // Verify queries statement is made with right arguments
        verify(uts.mock.connection).prepareStatement("UPDATE accounts SET TransferLimit = ? WHERE AccountNumber = ?");
        verify(uts.mock.preStatement).setDouble(1, (double) getTransferLimit.invoke(uts.acc));
        verify(uts.mock.preStatement).setLong(2, Long.parseLong((String) getAccountNumber.invoke(uts.acc)));
        verify(uts.mock.preStatement).executeUpdate();

        assertEquals(5000, (double) getTransferLimit.invoke(uts.acc), 0);
    }
}
