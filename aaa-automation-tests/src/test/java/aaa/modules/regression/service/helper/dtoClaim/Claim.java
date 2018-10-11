package aaa.modules.regression.service.helper.dtoClaim;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Claim {

    private String claimNumber;

    private String policyReferenceNumber;

    private String type;

    private String dateOpened;

    private String dateClosed;

    private String dateOfLoss;

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

    private Boolean isCompClaim;

    private String cause;

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

    public String getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
    }

    public String getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(String dateClosed) {
        this.dateClosed = dateClosed;
    }

    public String getDateOfLoss() {
        return dateOfLoss;
    }

    public void setDateOfLoss(String dateOfLoss) {
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


    public Boolean getIsCompClaim() {
        return isCompClaim;
    }

    public void setIsCompClaim(Boolean isCompClaim) {
        this.isCompClaim = isCompClaim;
    }

     public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
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
                ", isCompClaim=" + isCompClaim +
                ", cause=" + cause +
                '}';
    }
}
