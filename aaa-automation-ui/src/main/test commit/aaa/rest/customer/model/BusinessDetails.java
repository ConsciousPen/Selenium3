package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessDetails {

    private String legalName;
    private String businessType;
    private String dbaName;
    private String sicCode;
    private String naicsCode;
    private String legalId;
    private String dateStarted;
    private String taxExemptInd;
    private String groupSponsorInd;
    private String numberOfContinuous;
    private String numberOfEmployees;

    public String getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(String numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public String getNumberOfContinuous() {
        return numberOfContinuous;
    }

    public void setNumberOfContinuous(String numberOfContinuous) {
        this.numberOfContinuous = numberOfContinuous;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getDbaName() {
        return dbaName;
    }

    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public String getNaicsCode() {
        return naicsCode;
    }

    public void setNaicsCode(String naicsCode) {
        this.naicsCode = naicsCode;
    }

    public String getLegalId() {
        return legalId;
    }

    public void setLegalId(String legalId) {
        this.legalId = legalId;
    }

    public String getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(String dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String getTaxExemptInd() {
        return taxExemptInd;
    }

    public void setTaxExemptInd(String taxExemptInd) {
        this.taxExemptInd = taxExemptInd;
    }

    public String getGroupSponsorInd() {
        return groupSponsorInd;
    }

    public void setGroupSponsorInd(String groupSponsorInd) {
        this.groupSponsorInd = groupSponsorInd;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BusinessDetails that = (BusinessDetails) o;

        if (legalName != null ? !legalName.equals(that.legalName) : that.legalName != null)
            return false;
        if (businessType != null ? !businessType.equals(that.businessType) : that.businessType != null)
            return false;
        if (dbaName != null ? !dbaName.equals(that.dbaName) : that.dbaName != null)
            return false;
        if (sicCode != null ? !sicCode.equals(that.sicCode) : that.sicCode != null)
            return false;
        if (naicsCode != null ? !naicsCode.equals(that.naicsCode) : that.naicsCode != null)
            return false;
        if (legalId != null ? !legalId.equals(that.legalId) : that.legalId != null)
            return false;
        if (dateStarted != null ? !dateStarted.equals(that.dateStarted) : that.dateStarted != null)
            return false;
        if (taxExemptInd != null ? !taxExemptInd.equals(that.taxExemptInd) : that.taxExemptInd != null)
            return false;
        if (groupSponsorInd != null ? !groupSponsorInd.equals(that.groupSponsorInd) : that.groupSponsorInd != null)
            return false;
        if (numberOfContinuous != null ? !numberOfContinuous.equals(that.numberOfContinuous) : that.numberOfContinuous != null)
            return false;
        return numberOfEmployees != null ? numberOfEmployees.equals(that.numberOfEmployees) : that.numberOfEmployees == null;
    }

    @Override public int hashCode() {
        int result = legalName != null ? legalName.hashCode() : 0;
        result = 31 * result + (businessType != null ? businessType.hashCode() : 0);
        result = 31 * result + (dbaName != null ? dbaName.hashCode() : 0);
        result = 31 * result + (sicCode != null ? sicCode.hashCode() : 0);
        result = 31 * result + (naicsCode != null ? naicsCode.hashCode() : 0);
        result = 31 * result + (legalId != null ? legalId.hashCode() : 0);
        result = 31 * result + (dateStarted != null ? dateStarted.hashCode() : 0);
        result = 31 * result + (taxExemptInd != null ? taxExemptInd.hashCode() : 0);
        result = 31 * result + (groupSponsorInd != null ? groupSponsorInd.hashCode() : 0);
        result = 31 * result + (numberOfContinuous != null ? numberOfContinuous.hashCode() : 0);
        result = 31 * result + (numberOfEmployees != null ? numberOfEmployees.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "BusinessDetails{" +
                "legalName='" + legalName + '\'' +
                ", businessType='" + businessType + '\'' +
                ", dbaName='" + dbaName + '\'' +
                ", sicCode='" + sicCode + '\'' +
                ", naicsCode='" + naicsCode + '\'' +
                ", legalId='" + legalId + '\'' +
                ", dateStarted='" + dateStarted + '\'' +
                ", taxExemptInd='" + taxExemptInd + '\'' +
                ", groupSponsorInd='" + groupSponsorInd + '\'' +
                ", numberOfContinuous='" + numberOfContinuous + '\'' +
                ", numberOfEmployees='" + numberOfEmployees + '\'' +
                '}';
    }
}
