package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMethodEmail extends ContactMethod implements IModel {

    private String emailId;
    private String consentStatus;
    private String consentDate;

    public ContactMethodEmail() {
    }

    public ContactMethodEmail(ContactMethodEmail contactMethod) {
        super(contactMethod);
        this.emailId = contactMethod.getEmailId();
        this.consentStatus = contactMethod.getConsentStatus();
        this.consentDate = contactMethod.getConsentDate();
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
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

        ContactMethodEmail that = (ContactMethodEmail) o;

        if (emailId != null ? !emailId.equals(that.emailId) : that.emailId != null)
            return false;
        if (consentStatus != null ? !consentStatus.equals(that.consentStatus) : that.consentStatus != null)
            return false;
        return consentDate != null ? getConsentDate().equals(that.getConsentDate()) : that.consentDate == null;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (emailId != null ? emailId.hashCode() : 0);
        result = 31 * result + (consentStatus != null ? consentStatus.hashCode() : 0);
        result = 31 * result + (consentDate != null ? consentDate.hashCode() : 0);
        return result;
    }
}
