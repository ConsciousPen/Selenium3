package aaa.rest.customer.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import aaa.rest.IModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements IModel {

    private String customerNumber;
    private String customerType;
    private String customerStatus;
    private String sourceCd;
    private String ratingCd;
    private String displayValue;
    private String brandCd;
    private String preferredSpokenLanguageCd;
    private String preferredWrittenLanguageCd;
    private String paperless;
    private List<String> segments;
    private String registeredOnline;
    private String archived;
    @JsonProperty("individualDetails")
    private IndividualDetails individualDetails;
    @JsonProperty("businessDetails")
    private BusinessDetails businessDetails;
    //private String preferredContactMethod;
    private List<ContactMethodAddress> addresses;
    private List<ContactMethodChat> chats;
    private List<ContactMethodEmail> emails;
    private List<ContactMethodPhone> phones;
    private List<ContactMethodSocialNet> socialNets;
    private List<ContactMethodSocialWebAddress> webAddresses;
    private List<Agency> agencies;
    private List<AdditionalName> indCustomerAdditionalNames;
    private List<AdditionalName> businessCustomerAdditionalNames;
    private String customerEmployments;
    private List<Relationship> genericRelationships;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }

    public String getSourceCd() {
        return sourceCd;
    }

    public void setSourceCd(String sourceCd) {
        this.sourceCd = sourceCd;
    }

    public String getRatingCd() {
        return ratingCd;
    }

    public void setRatingCd(String ratingCd) {
        this.ratingCd = ratingCd;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getBrandCd() {
        return brandCd;
    }

    public void setBrandCd(String brandCd) {
        this.brandCd = brandCd;
    }

    public String getPreferredSpokenLanguageCd() {
        return preferredSpokenLanguageCd;
    }

    public void setPreferredSpokenLanguageCd(String preferredSpokenLanguageCd) {
        this.preferredSpokenLanguageCd = preferredSpokenLanguageCd;
    }

    public String getPreferredWrittenLanguageCd() {
        return preferredWrittenLanguageCd;
    }

    public void setPreferredWrittenLanguageCd(String preferredWrittenLanguageCd) {
        this.preferredWrittenLanguageCd = preferredWrittenLanguageCd;
    }

    public String getPaperless() {
        return paperless;
    }

    public void setPaperless(String paperless) {
        this.paperless = paperless;
    }

    public List<String> getSegments() {
        return segments;
    }

    public void setSegments(List<String> segments) {
        this.segments = segments;
    }

    public String getRegisteredOnline() {
        return registeredOnline;
    }

    public void setRegisteredOnline(String registeredOnline) {
        this.registeredOnline = registeredOnline;
    }

    public String getArchived() {
        return archived;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }

    public IndividualDetails getIndividualDetails() {
        return individualDetails;
    }

    public void setIndividualDetails(IndividualDetails individualDetails) {
        this.individualDetails = individualDetails;
    }

    public BusinessDetails getBusinessDetails() {
        return businessDetails;
    }

    public void setBusinessDetails(BusinessDetails businessDetails) {
        this.businessDetails = businessDetails;
    }

    public List<ContactMethodAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ContactMethodAddress> addresses) {
        this.addresses = addresses;
    }

    public List<ContactMethodChat> getChats() {
        return chats;
    }

    public void setChats(List<ContactMethodChat> chats) {
        this.chats = chats;
    }

    public List<ContactMethodEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<ContactMethodEmail> emails) {
        this.emails = emails;
    }

    public List<ContactMethodPhone> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactMethodPhone> phones) {
        this.phones = phones;
    }

    public List<ContactMethodSocialNet> getSocialNets() {
        return socialNets;
    }

    public void setSocialNets(List<ContactMethodSocialNet> socialNets) {
        this.socialNets = socialNets;
    }

    public List<ContactMethodSocialWebAddress> getWebAddresses() {
        return webAddresses;
    }

    public void setWebAddresses(List<ContactMethodSocialWebAddress> webAddresses) {
        this.webAddresses = webAddresses;
    }

    public List<Agency> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<Agency> agencies) {
        this.agencies = agencies;
    }

    public List<AdditionalName> getIndCustomerAdditionalNames() {
        return indCustomerAdditionalNames;
    }

    public void setIndCustomerAdditionalNames(List<AdditionalName> indCustomerAdditionalNames) {
        this.indCustomerAdditionalNames = indCustomerAdditionalNames;
    }

    public List<AdditionalName> getBusinessCustomerAdditionalNames() {
        return businessCustomerAdditionalNames;
    }

    public void setBusinessCustomerAdditionalNames(List<AdditionalName> businessCustomerAdditionalNames) {
        this.businessCustomerAdditionalNames = businessCustomerAdditionalNames;
    }

    public String getCustomerEmployments() {
        return customerEmployments;
    }

    public void setCustomerEmployments(String customerEmployments) {
        this.customerEmployments = customerEmployments;
    }

    public List<Relationship> getGenericRelationships() {
        return genericRelationships;
    }

    public void setGenericRelationships(List<Relationship> genericRelationships) {
        this.genericRelationships = genericRelationships;
    }

    public void resetAllCustomersIds(){
        setCustomerNumber(null);
        resetContactMethodsIds(getAddresses());
        resetContactMethodsIds(getPhones());
        resetContactMethodsIds(getEmails());
        resetContactMethodsIds(getChats());
        resetContactMethodsIds(getSocialNets());
        resetContactMethodsIds(getWebAddresses());
    }

    private void resetContactMethodsIds(List<? extends ContactMethod> contactMethods) {
        for (ContactMethod contactMethod : contactMethods) {
            contactMethod.setId(null);
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Customer customer = (Customer) o;

        if (customerType != null ? !customerType.equals(customer.customerType) : customer.customerType != null)
            return false;
        if (customerStatus != null ? !customerStatus.equals(customer.customerStatus) : customer.customerStatus != null)
            return false;
        if (sourceCd != null ? !sourceCd.equals(customer.sourceCd) : customer.sourceCd != null)
            return false;
        if (ratingCd != null ? !ratingCd.equals(customer.ratingCd) : customer.ratingCd != null)
            return false;
        if (displayValue != null ? !displayValue.equals(customer.displayValue) : customer.displayValue != null)
            return false;
        if (brandCd != null ? !brandCd.equals(customer.brandCd) : customer.brandCd != null)
            return false;
        if (preferredSpokenLanguageCd != null ? !preferredSpokenLanguageCd.equals(customer.preferredSpokenLanguageCd) : customer.preferredSpokenLanguageCd != null)
            return false;
        if (preferredWrittenLanguageCd != null ? !preferredWrittenLanguageCd.equals(customer.preferredWrittenLanguageCd) : customer.preferredWrittenLanguageCd != null)
            return false;
        if (paperless != null ? !paperless.equals(customer.paperless) : customer.paperless != null)
            return false;
        if (segments != null ? !segments.equals(customer.segments) : customer.segments != null)
            return false;
        if (registeredOnline != null ? !registeredOnline.equals(customer.registeredOnline) : customer.registeredOnline != null)
            return false;
        if (archived != null ? !archived.equals(customer.archived) : customer.archived != null)
            return false;
        if (individualDetails != null ? !individualDetails.equals(customer.individualDetails) : customer.individualDetails != null)
            return false;
        if (businessDetails != null ? !businessDetails.equals(customer.businessDetails) : customer.businessDetails != null)
            return false;
        if (addresses != null ? !addresses.equals(customer.addresses) : customer.addresses != null)
            return false;
        if (chats != null ? !chats.equals(customer.chats) : customer.chats != null)
            return false;
        if (emails != null ? !emails.equals(customer.emails) : customer.emails != null)
            return false;
        if (phones != null ? !phones.equals(customer.phones) : customer.phones != null)
            return false;
        if (socialNets != null ? !socialNets.equals(customer.socialNets) : customer.socialNets != null)
            return false;
        if (webAddresses != null ? !webAddresses.equals(customer.webAddresses) : customer.webAddresses != null)
            return false;
        if (agencies != null ? !agencies.equals(customer.agencies) : customer.agencies != null)
            return false;
        if (indCustomerAdditionalNames != null ? !indCustomerAdditionalNames.equals(customer.indCustomerAdditionalNames) : customer.indCustomerAdditionalNames != null)
            return false;
        if (businessCustomerAdditionalNames != null ? !businessCustomerAdditionalNames.equals(customer.businessCustomerAdditionalNames) : customer.businessCustomerAdditionalNames != null)
            return false;
        if (customerEmployments != null ? !customerEmployments.equals(customer.customerEmployments) : customer.customerEmployments != null)
            return false;
        return genericRelationships != null ? genericRelationships.equals(customer.genericRelationships) : customer.genericRelationships == null;
    }

    @Override public int hashCode() {
        int result = customerType != null ? customerType.hashCode() : 0;
        result = 31 * result + (customerStatus != null ? customerStatus.hashCode() : 0);
        result = 31 * result + (sourceCd != null ? sourceCd.hashCode() : 0);
        result = 31 * result + (ratingCd != null ? ratingCd.hashCode() : 0);
        result = 31 * result + (displayValue != null ? displayValue.hashCode() : 0);
        result = 31 * result + (brandCd != null ? brandCd.hashCode() : 0);
        result = 31 * result + (preferredSpokenLanguageCd != null ? preferredSpokenLanguageCd.hashCode() : 0);
        result = 31 * result + (preferredWrittenLanguageCd != null ? preferredWrittenLanguageCd.hashCode() : 0);
        result = 31 * result + (paperless != null ? paperless.hashCode() : 0);
        result = 31 * result + (segments != null ? segments.hashCode() : 0);
        result = 31 * result + (registeredOnline != null ? registeredOnline.hashCode() : 0);
        result = 31 * result + (archived != null ? archived.hashCode() : 0);
        result = 31 * result + (individualDetails != null ? individualDetails.hashCode() : 0);
        result = 31 * result + (businessDetails != null ? businessDetails.hashCode() : 0);
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        result = 31 * result + (chats != null ? chats.hashCode() : 0);
        result = 31 * result + (emails != null ? emails.hashCode() : 0);
        result = 31 * result + (phones != null ? phones.hashCode() : 0);
        result = 31 * result + (socialNets != null ? socialNets.hashCode() : 0);
        result = 31 * result + (webAddresses != null ? webAddresses.hashCode() : 0);
        result = 31 * result + (agencies != null ? agencies.hashCode() : 0);
        result = 31 * result + (indCustomerAdditionalNames != null ? indCustomerAdditionalNames.hashCode() : 0);
        result = 31 * result + (businessCustomerAdditionalNames != null ? businessCustomerAdditionalNames.hashCode() : 0);
        result = 31 * result + (customerEmployments != null ? customerEmployments.hashCode() : 0);
        result = 31 * result + (genericRelationships != null ? genericRelationships.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Customer{" +
                "customerNumber(Excluded from equals)='" + customerNumber + '\'' +
                ", customerType='" + customerType + '\'' +
                ", customerStatus='" + customerStatus + '\'' +
                ", sourceCd='" + sourceCd + '\'' +
                ", ratingCd='" + ratingCd + '\'' +
                ", displayValue='" + displayValue + '\'' +
                ", brandCd='" + brandCd + '\'' +
                ", preferredSpokenLanguageCd='" + preferredSpokenLanguageCd + '\'' +
                ", preferredWrittenLanguageCd='" + preferredWrittenLanguageCd + '\'' +
                ", paperless='" + paperless + '\'' +
                ", segments=" + segments +
                ", registeredOnline='" + registeredOnline + '\'' +
                ", archived='" + archived + '\'' +
                ", individualDetails=" + individualDetails +
                ", businessDetails=" + businessDetails +
                ", addresses=" + addresses +
                ", chats=" + chats +
                ", emails=" + emails +
                ", phones=" + phones +
                ", socialNets=" + socialNets +
                ", webAddresses=" + webAddresses +
                ", agencies=" + agencies +
                ", indCustomerAdditionalNames=" + indCustomerAdditionalNames +
                ", businessCustomerAdditionalNames=" + businessCustomerAdditionalNames +
                ", customerEmployments='" + customerEmployments + '\'' +
                ", genericRelationships=" + genericRelationships +
                '}';
    }
}




