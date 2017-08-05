package aaa.rest.partysearch.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import aaa.rest.IModel;
import toolkit.datax.TestData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Party implements IModel {

    private String lastName;
    private String firstName;
    private String legalName;
    private String customerNumber;

    public Party() {
    }

    public Party(String lastName, String firstName, String customerNumber) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.customerNumber = customerNumber;
    }

    public Party(String legalName, String customerNumber) {
        this.legalName = legalName;
        this.customerNumber = customerNumber;
    }

    public Party(TestData testData, Map<String, String> mapper) {
        this.customerNumber = mapper.get(testData.getValue("customerNumber"));
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Party party = (Party) o;
        //checking only by ids match(unique values) no ability to check by names due to it generates randomly each time to avoid duplicate issues
        return customerNumber != null ? customerNumber.equals(party.customerNumber) : party.customerNumber == null;
    }

    @Override public int hashCode() {
        return customerNumber != null ? customerNumber.hashCode() : 0;
    }

    @Override public String toString() {
        return "Party{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", legalName='" + legalName + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                '}';
    }
}
