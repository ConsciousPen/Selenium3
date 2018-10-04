package aaa.helpers.rest.dtoClaim;

import java.util.Date;

public class DriverInformation {

    private String firstName;

    private String lastName;

    private String licenseNumber;

    private String issuedState;

    private String relationToNamedInsured;

    private Date dateOfBirth;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getIssuedState() {
        return issuedState;
    }

    public void setIssuedState(String issuedState) {
        this.issuedState = issuedState;
    }

    public String getRelationToNamedInsured() {
        return relationToNamedInsured;
    }

    public void setRelationToNamedInsured(String relationToNamedInsured) {
        this.relationToNamedInsured = relationToNamedInsured;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "DriverInformation{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", issuedState='" + issuedState + '\'' +
                ", relationToNamedInsured='" + relationToNamedInsured + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
