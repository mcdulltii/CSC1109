package com.atm.backend;

// transfer from a1 to a2
public class Transfer{
    private Withdraw withdrawal;
    private Deposit deposit;

    protected String transferToAccount(Account a1, Account a2, double amount) throws InsufficientFundsException {
        if (amount > a1.getAvailableBalance()) {
            throw new InsufficientFundsException(-(a1.getAvailableBalance() - amount));
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive.");
        }
        
        // Update transactions
        withdrawal.execute(a1, amount);
        deposit.execute(a2, amount);

        return "Tranfer is Successful";
    }

}