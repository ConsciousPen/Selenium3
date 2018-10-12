package aaa.helpers.rest.dtoClaim;

import java.math.BigDecimal;

public class Payment {

    private String disposition;

    private String paymentType;

    private BigDecimal amountPaid;

    private String coverageName;

    private String coverageId;

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

    public String getCoverageName() {
        return coverageName;
    }

    public void setCoverageName(String coverageName) {
        this.coverageName = coverageName;
    }

    public String getCoverageId() {
        return coverageId;
    }

    public void setCoverageId(String coverageId) {
        this.coverageId = coverageId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "disposition='" + disposition + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", amountPaid=" + amountPaid +
                ", coverageName=" + coverageName +
                ", coverageId=" + coverageId +
                '}';
    }
}
