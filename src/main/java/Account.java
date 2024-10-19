import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Account implements IAccount {

    /**
     * Current balance this account holds
     */
    private BigDecimal balance;
    /**
     * Currency used in this account, can be "SEK", "EUR", or "USD"
     */
    private String currency;
    /**
     * max_overdrawn is a non-negative number indicating how much the account can be "in the red"
     * The minimum balance of the account is -1 * max_overdrawn
     */
    private BigDecimal max_overdrawn;

    // The list of possible currencies
    private final List<String> currencies = Arrays.asList("SEK", "EUR", "USD");

    public BigDecimal getMaxOverdrawn() {
        return this.max_overdrawn;
    }

    public void setMaxOverdrawn(BigDecimal max_overdrawn) {
        if(max_overdrawn.compareTo(BigDecimal.ZERO) <= 0) {
            this.max_overdrawn = BigDecimal.ZERO;
        } else {
            this.max_overdrawn = max_overdrawn;
        }
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        /*
        It needs to be checked if the user has chosen USD, EUR or SEK. If not, do not take further action.
         */
        if (!this.currencies.contains(currency)) return;
        this.currency = currency;
    }

    public void setBalance(BigDecimal balance) {
        /*
        Here, the bug was that you could not set the balance to be zero. Additionally, I have
        modified the expression using the negation rule in tautology to remove the extra negation
        */
        if (balance.compareTo(this.max_overdrawn.multiply(new BigDecimal(-1))) >= 0) {
            this.balance = balance;
        }
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account() {
        this.balance = BigDecimal.ZERO;
        this.currency = "SEK";
        this.max_overdrawn = BigDecimal.ZERO;
    }

    public Account(BigDecimal starting_balance, String currency, BigDecimal max_overdrawn) {
        // checks if the balance is greater than 0
        this.balance = starting_balance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : starting_balance;
        // checks if the currency is valid
        this.currency = this.currencies.contains(currency) ? currency : "SEK";
        // Rewritten it using the ternary operator
        this.max_overdrawn = max_overdrawn.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : max_overdrawn;
    }

    @Override
    public BigDecimal withdraw(BigDecimal requestedAmount) {
        /*
        The first implementation just removed the funds without any checks.
        Firstly, it has to be checked whether the projected balance exceeds the max_overdrawn value, and if the requestedAmount is positive.
        Then, the subtraction did not reflect on the class attribute balance.
        If the money can be withdrawn, the class attribute is updated, and the new balance is returned.
        */
        if (requestedAmount.compareTo(BigDecimal.ZERO) < 0) return this.balance;

        BigDecimal projBalance = this.balance.subtract(requestedAmount);

        if (projBalance.compareTo(this.max_overdrawn.multiply(new BigDecimal(-1))) < 0) return this.balance;

        BigDecimal newBalance = this.balance.subtract(requestedAmount);
        this.balance = newBalance;
        return newBalance;
    }

    @Override
    public BigDecimal deposit(BigDecimal amount_to_deposit) {
        /*
          Here, it firstly needs to be checked whether amount_to_deposit is negative.
          Then, the new balance will be set as the value of the balance attribute, and the new balance will be returned.
         */
        if (amount_to_deposit.compareTo(BigDecimal.ZERO) < 0) return this.balance;

        BigDecimal newBalance = this.balance.add(amount_to_deposit);
        this.balance = newBalance;
        return newBalance;
    }

    @Override
    public boolean convertToCurrency(String currencyCode, double rate) {
        /*
        Before the conversion, the currency code must be valid, and the rate must be greater than 0.
        Also, the balance attribute was not updated, so that was also implemented.
         */
        if (rate <= 0 || !this.currencies.contains(currencyCode)) return false;

        this.currency = currencyCode;
        this.balance = this.balance.multiply(new BigDecimal(rate));
        return true;
    }

    @Override
    public void TransferToAccount(Account to_account) {
        /*
        Because the IAccount interface did not define getters and setters for the fields, I have changed the signature
        of this method, so that it accepts the implemented class, not the class which implements that interface.
        In order for the transfer to happen, the currencies must be the same. Here, if they are different,
        no further action will be taken. Also, the account on which this method is called on must be updated with the new balance, i.e. 0.
         */
        if (!this.currency.equals(to_account.getCurrency())) return;

        to_account.deposit(this.balance);
        this.setBalance(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal withdrawAll() {
        /*
        In order to withdraw all positive funds, it needs to be checked if there are any positive funds, i.e. if the balance is positive.
        The current balance will be returned if the balance is negative.
         */
        return this.balance.compareTo(BigDecimal.ZERO) > 0 ? this.withdraw(this.balance) : this.balance;
    }
}
