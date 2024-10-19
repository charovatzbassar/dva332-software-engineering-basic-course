import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;


class AccountTest {

    @Test
    void testGetMaxOverdrawn() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, myTestAccount.getMaxOverdrawn());

        Account myTestAccount2 = new Account(BigDecimal.ZERO, "SEK", new BigDecimal(-1));
        assertEquals(BigDecimal.ZERO, myTestAccount2.getMaxOverdrawn()); //max_overdrawn must be non-negative

        Account myTestAccount3 = new Account(BigDecimal.ZERO, "SEK", new BigDecimal(1000));
        assertEquals(new BigDecimal(1000), myTestAccount3.getMaxOverdrawn());
    }

    @Test
    void testSetMaxOverdrawn() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);

        myTestAccount.setMaxOverdrawn(new BigDecimal(-1));
        assertEquals(BigDecimal.ZERO, myTestAccount.getMaxOverdrawn()); //max_overdrawn must be non-negative

        myTestAccount.setMaxOverdrawn(new BigDecimal(100));
        assertEquals(new BigDecimal(100), myTestAccount.getMaxOverdrawn()); //max_overdrawn must be non-negative

        myTestAccount.setMaxOverdrawn(new BigDecimal(1234567890));
        assertEquals(new BigDecimal(1234567890), myTestAccount.getMaxOverdrawn()); //max_overdrawn must be non-negative

    }

    @Test
    void testGetCurrency() {
        Account myTestAccount = new Account(BigDecimal.ZERO,  "SEK", BigDecimal.ZERO);
        assertEquals("SEK", myTestAccount.getCurrency());

        myTestAccount = new Account(BigDecimal.ZERO,  "EUR", BigDecimal.ZERO);
        assertEquals("EUR", myTestAccount.getCurrency());

        myTestAccount = new Account(BigDecimal.ZERO,  "USD", BigDecimal.ZERO);
        assertEquals("USD", myTestAccount.getCurrency());
    }

    @Test
    void testSetCurrency() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);
        myTestAccount.setCurrency("EUR");
        assertEquals("EUR", myTestAccount.getCurrency());

        myTestAccount.setCurrency("SEK");
        assertEquals("SEK", myTestAccount.getCurrency());

        myTestAccount.setCurrency("USD");
        assertEquals("USD", myTestAccount.getCurrency());

        myTestAccount.setCurrency("BAM");
        assertEquals("USD", myTestAccount.getCurrency());
    }

    @Test
    void testGetBalance() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, myTestAccount.getBalance());

        myTestAccount = new Account(new BigDecimal(100), "SEK", BigDecimal.ZERO);
        assertEquals(new BigDecimal(100), myTestAccount.getBalance());

        myTestAccount = new Account(new BigDecimal(1234567890), "SEK", BigDecimal.ZERO);
        assertEquals(new BigDecimal(1234567890), myTestAccount.getBalance());
    }

    @Test
    void testSetBalance() {
        Account myTestAccount = new Account(BigDecimal.ZERO, "SEK", BigDecimal.ONE);

        //should not be allowed to set balance to lower that -1 * maxOverdrawn
        myTestAccount.setBalance(new BigDecimal(-2));
        assertEquals(BigDecimal.ZERO, myTestAccount.getBalance());

        myTestAccount.setBalance(new BigDecimal(42));
        assertEquals(new BigDecimal(42), myTestAccount.getBalance());

        myTestAccount.setBalance(new BigDecimal(1234567890));
        assertEquals(new BigDecimal(1234567890), myTestAccount.getBalance());
    }

    @Test
    void testWithdraw() {
        Account testAccount = new Account(new BigDecimal(1000), "SEK", BigDecimal.ZERO);
        /*
        In this test case, I want to withdraw funds to my account. This code initialises a new Account with 1000 SEK as the starting balance, and the max. overdrawn of 0.
        500 SEK is withdrawn from the account, and there should be 500 SEK remaining. I have opted for this to test the most basic functionality.
         */
        testAccount.withdraw(new BigDecimal(500));
        assertEquals(new BigDecimal(500), testAccount.getBalance());

        /*
        In this test case, I want to try to withdraw a negative amount from the system. It is not possible, therefore the balance will remain 500 SEK. This code
        "withdraws" -5 SEK from the account, and it is not successful, therefore the balance does not change.
         */
        testAccount.withdraw(new BigDecimal(-5));
        assertEquals(new BigDecimal(500), testAccount.getBalance());

        Account testAccount2 = new Account(BigDecimal.ZERO, "SEK", new BigDecimal(500));

        /*
        In this test case, I want to withdraw funds, so that I go over the max_overdrawn value. A new Account is instantiated with no funds, and a max overdrawn value of -500 SEK.
        This code tries to withdraw 501 SEK, but it cannot, because it is over the limit. Therefore, the balance stays 0. Here, I want to test if I can withdraw more money than it is allowed.
         */
        testAccount2.withdraw(new BigDecimal(501));
        assertEquals(BigDecimal.ZERO, testAccount2.getBalance());

        /*
        In this test case, I want to withdraw 499 SEK from the same account. The money is withdrawn, and the account has 499 SEK.
        I have chosen this value, for the reason that I want to test when the account's balance is negative.
         */
        testAccount2.withdraw(new BigDecimal(499));
        assertEquals(new BigDecimal(-499), testAccount2.getBalance());

    }

    @Test
    void testDeposit() {
        Account testAccount = new Account(new BigDecimal(1000), "SEK", BigDecimal.ZERO);

        /*
        In this test case, I want to deposit 1000 SEK to the testAccount instance. This code deposits the funds, and checks if the balance is 2000 SEK.
        I have opted for this value to test the core functionality.
         */
        testAccount.deposit(new BigDecimal(1000));
        assertEquals(new BigDecimal(2000), testAccount.getBalance());

        /*
        In this test case, I want to deposit 1234567890 SEK to the testAccount instance. This code deposits the funds, and checks if the balance is 1234569890 SEK.
        I have opted for this value to test the behaviour with larger values. I could not use either the max. integer or double values, because of the way BigDecimal behaves.
        The maximum value of it is dependent on the available memory.
         */
        testAccount.deposit(new BigDecimal(1234567890));
        assertEquals(new BigDecimal(1234569890), testAccount.getBalance());

        /*
        In this test case, I want to try to deposit -12 SEK to the testAccount instance. This code "deposits" the funds, and checks if the balance is the same as before.
        I have opted for this value to test if I could deposit negative amount of money.
         */
        testAccount.deposit(new BigDecimal(-12));
        assertEquals(new BigDecimal(1234569890), testAccount.getBalance());

    }

    @Test
    void testConvertToCurrency() {
        Account account = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        /*
        In this test case, I want to convert the currency of the account instance to the Bosnian convertible mark (BAM). The code tries to change the value,
        and checks if the balance and the currency had remained the same. I have opted for this value in order to test the behaviour when I pass an unsupported currency.
         */
        account.convertToCurrency("BAM", 0.17);
        assertEquals(new BigDecimal(1200), account.getBalance());
        assertEquals("SEK", account.getCurrency());

        /*
        In this test case, I want to convert the currency of the account instance to the euro. The code tries to change the value,
        and checks if the balance and the currency have changed. 1200 SEK is around €107. I have opted for this value in order to test the basic functionality.
         */
        account.convertToCurrency("EUR", 0.09);
        assertEquals(new BigInteger("107"), account.getBalance().toBigInteger());
        assertEquals("EUR", account.getCurrency());

        /*
        In this test case, I want to convert the currency of the account instance to some obscure currency, with the exchange rate of -1. The code tries to change the value,
        and checks if the balance and the currency have changed. Everything should remain the same. I have opted for this value in order to test the behaviour when a negative exchange rate is passed.
        It is not possible, so the everything remains the same.
         */
        account.convertToCurrency("BLA", -1);
        assertEquals(new BigInteger("107"), account.getBalance().toBigInteger());
        assertEquals("EUR", account.getCurrency());
    }

    @Test
    void testTransferToAccount() {
        Account account1 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        Account account2 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);

        /*
        In this test case, I want to test fund transferring from  account1 instance to account2 instance, which have 1200 SEK each. The TranferToAccount() method is called on account1, and the code checks
        the balances of both accounts. Instance account1 should have no money, while account2 instance should have 2400 SEK. I have opted for these values to test the basic functionality.
         */
        account1.TransferToAccount(account2);

        assertEquals(BigDecimal.ZERO, account1.getBalance());
        assertEquals(new BigDecimal(2400), account2.getBalance());

        Account account3 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        Account account4 = new Account(new BigDecimal(1200), "EUR", BigDecimal.ZERO);

        /*
        In this test case, I want to test fund transferring from  account3 instance to account4 instance, which have different currencies. The TranferToAccount() method is called on account3, and the code checks
        the balances of both accounts. There should be no chance to the balances. Instance account3 should have 1200 SEK, while account4 instance should have €1200. I have opted for these values to test the behaviour
        when I try to pass an account with a different currency to the TransferToAccount() method.
         */
        account3.TransferToAccount(account4);

        assertEquals(new BigDecimal(1200), account3.getBalance());
        assertEquals(new BigDecimal(1200), account4.getBalance());

        Account account5 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        Account account6 = new Account(new BigDecimal(1234567890), "SEK", BigDecimal.ZERO);

        /*
        In this test case, I want to test fund transferring from  account6 instance to account5 instance, which have 1234567890 SEK and 1200 SEK respectively. The TranferToAccount() method is called on account6, and the code checks
        the balances of both accounts. Instance account6 should have no money, while account5 instance should have 1234569090 SEK. I have opted for these values to test the basic functionality with larger values.
         */
        account6.TransferToAccount(account5);

        assertEquals(new BigDecimal(1234569090), account5.getBalance());
        assertEquals(BigDecimal.ZERO, account6.getBalance());
    }

    @Test
    void testWithdrawAll() {
        /*
        In this test case, I want to withdraw all funds from the account instance with 1200 SEK at disposal. The withdrawAll() method is called, and it is checked whether the balance is 0 SEK.
        I have opted for these values for attributes to check the basic functionality.
         */
        Account account = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        account.withdrawAll();

        assertEquals(BigDecimal.ZERO, account.getBalance());

        /*
        In this test case, I want to withdraw all funds from the account1 instance with 1200 euros at disposal, and a max_overdrawn value of 250. The withdrawAll() method is called, and it is checked whether the balance is 0 SEK.
        I have opted for these values for attributes to check if the balance will be 0 after, and that it will not withdraw money until the -1 * max_overdrawn value.
         */
        Account account1 = new Account(new BigDecimal(1200), "EUR", new BigDecimal(250));
        account1.withdrawAll();

        assertEquals(BigDecimal.ZERO, account1.getBalance());

        /*
        In this test case, I want to withdraw all funds from the account2 instance with $1200 at disposal, and a max_overdrawn value of 1234567890. The withdrawAll() method is called, and it is checked whether the balance is 0 SEK.
        I have opted for these values for attributes to check if the balance will be 0 after, and that it will not withdraw money until the -1 * max_overdrawn value. Also, I wanted to see the behaviour
        when there is a larger value of max_overdrawn.
         */
        Account account2 = new Account(new BigDecimal(1200), "USD", new BigDecimal(1234567890));
        account2.withdrawAll();

        assertEquals(BigDecimal.ZERO, account2.getBalance());
    }
}
