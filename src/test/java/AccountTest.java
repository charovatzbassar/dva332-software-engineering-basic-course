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

        testAccount.withdraw(new BigDecimal(500));
        assertEquals(new BigDecimal(500), testAccount.getBalance());

        testAccount.withdraw(new BigDecimal(-5));
        assertEquals(new BigDecimal(500), testAccount.getBalance());

        Account testAccount2 = new Account(new BigDecimal(-1), "SEK", new BigDecimal(500));

        testAccount2.withdraw(new BigDecimal(499));
        assertEquals(new BigDecimal(-499), testAccount2.getBalance());

    }

    @Test
    void testDeposit() {
        Account testAccount = new Account(new BigDecimal(1000), "SEK", BigDecimal.ZERO);

        testAccount.deposit(new BigDecimal(1000));
        assertEquals(new BigDecimal(2000), testAccount.getBalance());

        testAccount.deposit(new BigDecimal(1234567890));
        assertEquals(new BigDecimal(1234569890), testAccount.getBalance());

        testAccount.deposit(new BigDecimal(-12));
        assertEquals(new BigDecimal(1234569890), testAccount.getBalance());

    }

    @Test
    void testConvertToCurrency() {
        Account account = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        account.convertToCurrency("BAM", 0.17);
        assertEquals(new BigDecimal(1200), account.getBalance());

        account.convertToCurrency("EUR", 0.09);
        assertEquals(new BigInteger("107"), account.getBalance().toBigInteger());

        account.convertToCurrency("BLA", -1);
        assertEquals(new BigInteger("107"), account.getBalance().toBigInteger());
    }

    @Test
    void testTransferToAccount() {
        Account account1 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        Account account2 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);

        account1.TransferToAccount(account2);

        assertEquals(BigDecimal.ZERO, account1.getBalance());
        assertEquals(new BigDecimal(2400), account2.getBalance());

        Account account3 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        Account account4 = new Account(new BigDecimal(1200), "EUR", BigDecimal.ZERO);

        account3.TransferToAccount(account4);

        assertEquals(new BigDecimal(1200), account3.getBalance());
        assertEquals(new BigDecimal(1200), account4.getBalance());

        Account account5 = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        Account account6 = new Account(new BigDecimal(12321), "SEK", BigDecimal.ZERO);

        account6.TransferToAccount(account5);

        assertEquals(new BigDecimal(13521), account5.getBalance());
        assertEquals(BigDecimal.ZERO, account6.getBalance());
    }

    @Test
    void testWithdrawAll() {
        Account account = new Account(new BigDecimal(1200), "SEK", BigDecimal.ZERO);
        account.withdrawAll();

        assertEquals(BigDecimal.ZERO, account.getBalance());

        Account account1 = new Account(new BigDecimal(1200), "EUR", new BigDecimal(250));
        account1.withdrawAll();

        assertEquals(BigDecimal.ZERO, account1.getBalance());

        Account account2 = new Account(new BigDecimal(1200), "USD", new BigDecimal(1234567890));
        account2.withdrawAll();

        assertEquals(BigDecimal.ZERO, account2.getBalance());
    }
}
