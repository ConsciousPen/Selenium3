package aaa.helpers.claim.datamodel.claim;

import java.util.List;

@SuppressWarnings("unused")
public class Claim {
    private String claimPolicyReferenceNumber;
    private String claimNumber;
    private String claimPrefix;
    private String claimType;
    private String claimCause;
    private String claimOpenDate;
    private String claimCloseDate;
    private String claimDateOfLoss;
    private String claimStatusCode;
    private String accidentFault;
    private String lossSummary;
    private String claimDeductibleAmount;
    private String claimDeductibleCurrencyCode;
    @SuppressWarnings("SpellCheckingInspection")
    private String subroFlag;
    private String totalAmountPaid;
    private String totalAmountPaidCurrencyCode;
    private String driverName;
    private String drivingLicenseNumber;
    private String drivingLicenseState;
    private String driverAgeAsOfDateOfLoss;
    private String driverDateOfBirth;
    private String driverRelationToInsured;
    private String vehicleSerialNumber;
    private String vehicleBodyType;
    private String vehicleMake;
    private String vehicleManufacturedYear;
    private String vehicleDescription;

    private List<ClaimCoverage> claimCoverageList;

    public String getClaimPolicyReferenceNumber() {
        return claimPolicyReferenceNumber;
    }

    public void setClaimPolicyReferenceNumber(String claimPolicyReferenceNumber) {
        this.claimPolicyReferenceNumber = claimPolicyReferenceNumber;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public String getClaimPrefix() {
        return claimPrefix;
    }

    public void setClaimPrefix(String claimPrefix) {
        this.claimPrefix = claimPrefix;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getClaimCause() {
        return claimCause;
    }

    public void setClaimCause(String claimCause) {
        this.claimCause = claimCause;
    }

    public String getClaimOpenDate() {
        return claimOpenDate;
    }

    public void setClaimOpenDate(String claimOpenDate) {
        this.claimOpenDate = claimOpenDate;
    }

    public String getClaimCloseDate() {
        return claimCloseDate;
    }

    public void setClaimCloseDate(String claimCloseDate) {
        this.claimCloseDate = claimCloseDate;
    }

    public String getClaimDateOfLoss() {
        return claimDateOfLoss;
    }

    public void setClaimDateOfLoss(String claimDateOfLoss) {
        this.claimDateOfLoss = claimDateOfLoss;
    }

    public String getClaimStatusCode() {
        return claimStatusCode;
    }

    public void setClaimStatusCode(String claimStatusCode) {
        this.claimStatusCode = claimStatusCode;
    }

    public String getAccidentFault() {
        return accidentFault;
    }

    public void setAccidentFault(String accidentFault) {
        this.accidentFault = accidentFault;
    }

    public String getLossSummary() {
        return lossSummary;
    }

    public void setLossSummary(String lossSummary) {
        this.lossSummary = lossSummary;
    }

    public String getClaimDeductibleAmount() {
        return claimDeductibleAmount;
    }

    public void setClaimDeductibleAmount(String claimDeductibleAmount) {
        this.claimDeductibleAmount = claimDeductibleAmount;
    }

    public String getClaimDeductibleCurrencyCode() {
        return claimDeductibleCurrencyCode;
    }

    public void setClaimDeductibleCurrencyCode(String claimDeductibleCurrencyCode) {
        this.claimDeductibleCurrencyCode = claimDeductibleCurrencyCode;
    }

    public String getSubroFlag() {
        return subroFlag;
    }

    public void setSubroFlag(String subroFlag) {
        this.subroFlag = subroFlag;
    }

    public String getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(String totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public String getTotalAmountPaidCurrencyCode() {
        return totalAmountPaidCurrencyCode;
    }

    public void setTotalAmountPaidCurrencyCode(String totalAmountPaidCurrencyCode) {
        this.totalAmountPaidCurrencyCode = totalAmountPaidCurrencyCode;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getDrivingLicenseState() {
        return drivingLicenseState;
    }

    public void setDrivingLicenseState(String drivingLicenseState) {
        this.drivingLicenseState = drivingLicenseState;
    }

    public String getDriverAgeAsOfDateOfLoss() {
        return driverAgeAsOfDateOfLoss;
    }

    public void setDriverAgeAsOfDateOfLoss(String driverAgeAsOfDateOfLoss) {
        this.driverAgeAsOfDateOfLoss = driverAgeAsOfDateOfLoss;
    }

    public String getDriverDateOfBirth() {
        return driverDateOfBirth;
    }

    public void setDriverDateOfBirth(String driverDateOfBirth) {
        this.driverDateOfBirth = driverDateOfBirth;
    }

    public String getDriverRelationToInsured() {
        return driverRelationToInsured;
    }

    public void setDriverRelationToInsured(String driverRelationToInsured) {
        this.driverRelationToInsured = driverRelationToInsured;
    }

    public String getVehicleSerialNumber() {
        return vehicleSerialNumber;
    }

    public void setVehicleSerialNumber(String vehicleSerialNumber) {
        this.vehicleSerialNumber = vehicleSerialNumber;
    }

    public String getVehicleBodyType() {
        return vehicleBodyType;
    }

    public void setVehicleBodyType(String vehicleBodyType) {
        this.vehicleBodyType = vehicleBodyType;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleManufacturedYear() {
        return vehicleManufacturedYear;
    }

    public void setVehicleManufacturedYear(String vehicleManufacturedYear) {
        this.vehicleManufacturedYear = vehicleManufacturedYear;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }

    public List<ClaimCoverage> getClaimCoverageList() {
        return claimCoverageList;
    }

    public void setClaimCoverageList(List<ClaimCoverage> claimCoverageList) {
        this.claimCoverageList = claimCoverageList;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "claimPolicyReferenceNumber='" + claimPolicyReferenceNumber + '\'' +
                ", claimNumber='" + claimNumber + '\'' +
                ", claimPrefix='" + claimPrefix + '\'' +
                ", claimType='" + claimType + '\'' +
                ", claimCause='" + claimCause + '\'' +
                ", claimOpenDate='" + claimOpenDate + '\'' +
                ", claimCloseDate='" + claimCloseDate + '\'' +
                ", claimDateOfLoss='" + claimDateOfLoss + '\'' +
                ", claimStatusCode='" + claimStatusCode + '\'' +
                ", accidentFault='" + accidentFault + '\'' +
                ", lossSummary='" + lossSummary + '\'' +
                ", claimDeductibleAmount='" + claimDeductibleAmount + '\'' +
                ", claimDeductibleCurrencyCode='" + claimDeductibleCurrencyCode + '\'' +
                ", subroFlag='" + subroFlag + '\'' +
                ", totalAmountPaid='" + totalAmountPaid + '\'' +
                ", totalAmountPaidCurrencyCode='" + totalAmountPaidCurrencyCode + '\'' +
                ", driverName='" + driverName + '\'' +
                ", drivingLicenseNumber='" + drivingLicenseNumber + '\'' +
                ", drivingLicenseState='" + drivingLicenseState + '\'' +
                ", driverAgeAsOfDateOfLoss='" + driverAgeAsOfDateOfLoss + '\'' +
                ", driverDateOfBirth='" + driverDateOfBirth + '\'' +
                ", driverRelationToInsured='" + driverRelationToInsured + '\'' +
                ", vehicleSerialNumber='" + vehicleSerialNumber + '\'' +
                ", vehicleBodyType='" + vehicleBodyType + '\'' +
                ", vehicleMake='" + vehicleMake + '\'' +
                ", vehicleManufacturedYear='" + vehicleManufacturedYear + '\'' +
                ", vehicleDescription='" + vehicleDescription + '\'' +
                ", claimCoverageList=" + claimCoverageList +
                '}';
    }
}
