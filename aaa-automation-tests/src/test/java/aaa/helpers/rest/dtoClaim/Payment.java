package aaa.helpers.rest.dtoClaim;

import java.math.BigDecimal;

public class Payment {

    private String disposition;

    private String paymentType;

    private BigDecimal amountPaid;


    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "disposition='" + disposition + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", amountPaid=" + amountPaid +
                '}';
    }
}
