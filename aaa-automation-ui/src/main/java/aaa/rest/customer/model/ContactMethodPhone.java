package aaa.rest.customer.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMethodPhone extends ContactMethod implements IModel {

    private String phoneNumber;
    private String phoneExtension;
    private String consentStatus;
    private String consentDate;
    private List<String> preferredDaysToContact = new ArrayList<>();
    private List<String> preferredTimesToContact = new ArrayList<>();

    public ContactMethodPhone() {
    }

    public ContactMethodPhone(ContactMethodPhone contactMethod) {
        super(contactMethod);
        this.phoneNumber = contactMethod.getPhoneNumber();
        this.phoneExtension = contactMethod.getPhoneExtension();
        this.consentStatus = contactMethod.getConsentStatus();
        this.consentDate = contactMethod.getConsentDate();
        this.preferredDaysToContact = contactMethod.getPreferredDaysToContact();
        this.preferredTimesToContact = contactMethod.getPreferredTimesToContact();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneExtension() {
        return phoneExtension;
    }

    public void setPhoneExtension(String phoneExtension) {
        this.phoneExtension = phoneExtension;
    }

    public List<String> getPreferredDaysToContact() {
        return preferredDaysToContact;
    }

    public void addPreferredDaysToContact(String... preferredDaysToContact) {
        for (String dayToContact : preferredDaysToContact) {
            this.preferredDaysToContact.add(dayToContact);
        }
    }

    public List<String> getPreferredTimesToContact() {
        return preferredTimesToContact;
    }

    public void addPreferredTimesToContact(String... preferredTimesToContact) {
        for (String timeToContact : preferredTimesToContact) {
            this.preferredTimesToContact.add(timeToContact);
        }
    }

    public String getConsentStatus() {
        return consentStatus;
    }

    public void setConsentStatus(String consentStatus) {
        this.consentStatus = consentStatus;
    }

    public String getConsentDate() {
        return consentDate;
    }

    public void setConsentDate(String consentDate) {
        this.consentDate = consentDate;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        ContactMethodPhone that = (ContactMethodPhone) o;

        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null)
            return false;
        if (phoneExtension != null ? !phoneExtension.equals(that.phoneExtension) : that.phoneExtension != null)
            return false;
        if (consentDate != null ? !getConsentDate().equals(that.getConsentDate()) : that.consentDate != null)
            return false;
        if (consentStatus != null ? !consentStatus.equals(that.consentStatus) : that.consentStatus != null)
            return false;
        if (preferredDaysToContact != null ? !preferredDaysToContact.containsAll(that.preferredDaysToContact) : that.preferredDaysToContact != null)
            return false;
        return preferredTimesToContact != null ? preferredTimesToContact.containsAll(that.preferredTimesToContact) : that.preferredTimesToContact == null;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (phoneExtension != null ? phoneExtension.hashCode() : 0);
        result = 31 * result + (consentDate != null ? consentDate.hashCode() : 0);
        result = 31 * result + (consentStatus != null ? consentStatus.hashCode() : 0);
        result = 31 * result + (preferredDaysToContact != null ? preferredDaysToContact.hashCode() : 0);
        result = 31 * result + (preferredTimesToContact != null ? preferredTimesToContact.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        String parentToString = super.toString();
        return parentToString + "\nChildClass\nContactMethodPhone{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", phoneExtension='" + phoneExtension + '\'' +
                ", consentDate='" + consentDate + '\'' +
                ", consentStatus='" + consentStatus + '\'' +
                ", preferredDaysToContact=" + preferredDaysToContact +
                ", preferredTimesToContact=" + preferredTimesToContact +
                '}';
    }
}
