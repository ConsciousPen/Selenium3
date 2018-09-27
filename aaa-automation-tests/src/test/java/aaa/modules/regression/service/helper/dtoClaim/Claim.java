package aaa.modules.regression.service.helper.dtoClaim;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Claim {

    private String claimNumber;

    private String policyReferenceNumber;

    private String type;

    private Date dateOpened;

    private Date dateClosed;

    private Date dateOfLoss;

    private String status;

    private String matchCode;

    private DriverInformation driverInformation;

    private BigDecimal totalAmountPaid;

    private BigDecimal claimDeductible;

    private String liabilityCd;

    @SuppressWarnings("SpellCheckingInspection")
    private boolean subroFlag;

    private String lossSummary;

    private String driverOid;

    private List<Payment> payments;

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getPolicyReferenceNumber() {
        return policyReferenceNumber;
    }

    public void setPolicyReferenceNumber(String policyReferenceNumber) {
        this.policyReferenceNumber = policyReferenceNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }

    public Date getDateOfLoss() {
        return dateOfLoss;
    }

    public void setDateOfLoss(Date dateOfLoss) {
        this.dateOfLoss = dateOfLoss;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public DriverInformation getDriverInformation() {
        return driverInformation;
    }

    public void setDriverInformation(DriverInformation driverInformation) {
        this.driverInformation = driverInformation;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public BigDecimal getClaimDeductible() {
        return claimDeductible;
    }

    public void setClaimDeductible(BigDecimal claimDeductible) {
        this.claimDeductible = claimDeductible;
    }

    public String getLiabilityCd() {
        return liabilityCd;
    }

    public void setLiabilityCd(String liabilityCd) {
        this.liabilityCd = liabilityCd;
    }

    public boolean isSubroFlag() {
        return subroFlag;
    }

    public void setSubroFlag(boolean subroFlag) {
        this.subroFlag = subroFlag;
    }

    public String getLossSummary() {
        return lossSummary;
    }

    public void setLossSummary(String lossSummary) {
        this.lossSummary = lossSummary;
    }

    public String getDriverOid() {
        return driverOid;
    }

    public void setDriverOid(String driverOid) {
        this.driverOid = driverOid;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "claimNumber='" + claimNumber + '\'' +
                ", policyReferenceNumber='" + policyReferenceNumber + '\'' +
                ", type='" + type + '\'' +
                ", dateOpened=" + dateOpened +
                ", dateClosed=" + dateClosed +
                ", dateOfLoss=" + dateOfLoss +
                ", status='" + status + '\'' +
                ", matchCode='" + matchCode + '\'' +
                ", driverInformation=" + driverInformation +
                ", totalAmountPaid=" + totalAmountPaid +
                ", claimDeductible=" + claimDeductible +
                ", liabilityCd='" + liabilityCd + '\'' +
                ", subroFlag=" + subroFlag +
                ", lossSummary='" + lossSummary + '\'' +
                ", driverOid='" + driverOid + '\'' +
                ", payments=" + payments +
                '}';
    }
}
