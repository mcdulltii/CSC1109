package com.atm.backend.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atm.backend.Authenticate;
import com.atm.backend.SQLQueries;

public class AuthenticateTest {
    static UnitTestSuite uts;
    static Authenticate auth;
    static SQLQueries q;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        uts = new UnitTestSuite();
        auth = new Authenticate(uts.mock.connection);
        q = mock(SQLQueries.class);
    }

    @Test
    public void testCheckPassword() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // Set to use Mock instance
        Field qField = auth.getClass().getDeclaredField("q");
        qField.setAccessible(true);
        qField.set(auth, q);

        // Get protected method, and control behaviour
        byte[] salt = new byte[] {1, 2, 3};
        String hashPassword = "6b1875248b597596875c8c89064d261d07dd71bb38281dd2d207824838edb6ce";

        Method method = q.getClass().getDeclaredMethod("getPasswordSaltfromCardNumber", String.class);
        method.setAccessible(true);
        when(method.invoke(q, anyString())).thenReturn(salt);

        method = q.getClass().getDeclaredMethod("getPasswordfromCardNumber", String.class);
        method.setAccessible(true);
        when(method.invoke(q, anyString())).thenReturn(hashPassword);

        // If salt exists
        assertTrue(auth.checkPassword("4556220772249664", "123456"));
        // If salt does not exists
        when(method.invoke(q, anyString())).thenReturn(null);
        assertFalse(auth.checkPassword("4556220772249664", "123456"));
    }
}
