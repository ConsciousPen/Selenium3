package aaa.rest.billing.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingAccount implements IModel{

    private String accountNumber;

    public BillingAccount() {
    }

    public BillingAccount(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BillingAccount that = (BillingAccount) o;

        return accountNumber != null ? accountNumber.equals(that.accountNumber) : that.accountNumber == null;
    }

    @Override public int hashCode() {
        return accountNumber != null ? accountNumber.hashCode() : 0;
    }

    @Override public String toString() {
        return "BillingAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
