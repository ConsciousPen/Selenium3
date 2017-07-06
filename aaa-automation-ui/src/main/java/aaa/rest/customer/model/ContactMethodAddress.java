package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMethodAddress extends ContactMethod {

    private String temporary;
    private String effectiveFrom;
    private String effectiveTo;
    private String duration;
    private String stateProvCd;
    private String postalCode;
    private String countryCd;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String county;
    private String inCareOf;
    private String subdivision;
    private String attention;
    private String validationIndicator;
    private String latitude;
    private String longitude;
    private String accuracy;
    private String referenceId;

    public ContactMethodAddress() {
    }

    public ContactMethodAddress(ContactMethodAddress contactMethod) {
        super(contactMethod);
        this.temporary = contactMethod.getTemporary();
        this.effectiveFrom = contactMethod.getEffectiveFrom();
        this.effectiveTo = contactMethod.getEffectiveTo();
        this.duration = contactMethod.getDuration();
        this.stateProvCd = contactMethod.getStateProvCd();
        this.postalCode = contactMethod.getPostalCode();
        this.countryCd = contactMethod.getCountryCd();
        this.addressLine1 = contactMethod.getAddressLine1();
        this.addressLine2 = contactMethod.getAddressLine2();
        this.addressLine3 = contactMethod.getAddressLine3();
        this.city = contactMethod.getCity();
        this.county = contactMethod.getCounty();
        this.inCareOf = contactMethod.getInCareOf();
        this.subdivision = contactMethod.getSubdivision();
        this.attention = contactMethod.getAttention();
        this.validationIndicator = contactMethod.getValidationIndicator();
        this.latitude = contactMethod.getLatitude();
        this.longitude = contactMethod.getLongitude();
        this.accuracy = contactMethod.getAccuracy();
        this.referenceId = contactMethod.getReferenceId();
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public String getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(String effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public String getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(String effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStateProvCd() {
        return stateProvCd;
    }

    public void setStateProvCd(String stateProvCd) {
        this.stateProvCd = stateProvCd;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getInCareOf() {
        return inCareOf;
    }

    public void setInCareOf(String inCareOf) {
        this.inCareOf = inCareOf;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getValidationIndicator() {
        return validationIndicator;
    }

    public void setValidationIndicator(String validationIndicator) {
        this.validationIndicator = validationIndicator;
    }

    public String getLatitude() {
        //Stick for usage in Address and Customer models simultaneously
        if (this.latitude != null && this.latitude.contains(".")) {
            this.latitude = latitude.split("\\.")[0];
        }
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        //Stick for usage in Address and Customer models simultaneously
        if (this.longitude != null && this.longitude.contains(".")) {
            this.longitude = longitude.split("\\.")[0];
        }
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        ContactMethodAddress that = (ContactMethodAddress) o;

        if (temporary != null ? !temporary.equals(that.temporary) : that.temporary != null)
            return false;
        if (effectiveFrom != null ? !effectiveFrom.equals(that.effectiveFrom) : that.effectiveFrom != null)
            return false;
        if (effectiveTo != null ? !effectiveTo.equals(that.effectiveTo) : that.effectiveTo != null)
            return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null)
            return false;
        if (stateProvCd != null ? !stateProvCd.equals(that.stateProvCd) : that.stateProvCd != null)
            return false;
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null)
            return false;
        if (countryCd != null ? !countryCd.equals(that.countryCd) : that.countryCd != null)
            return false;
        if (addressLine1 != null ? !addressLine1.equals(that.addressLine1) : that.addressLine1 != null)
            return false;
        if (addressLine2 != null ? !addressLine2.equals(that.addressLine2) : that.addressLine2 != null)
            return false;
        if (addressLine3 != null ? !addressLine3.equals(that.addressLine3) : that.addressLine3 != null)
            return false;
        if (city != null ? !city.equals(that.city) : that.city != null)
            return false;
        if (county != null ? !county.equals(that.county) : that.county != null)
            return false;
        if (inCareOf != null ? !inCareOf.equals(that.inCareOf) : that.inCareOf != null)
            return false;
        if (subdivision != null ? !subdivision.equals(that.subdivision) : that.subdivision != null)
            return false;
        if (attention != null ? !attention.equals(that.attention) : that.attention != null)
            return false;
        if (validationIndicator != null ? !validationIndicator.equals(that.validationIndicator) : that.validationIndicator != null)
            return false;
        if (latitude != null ? !getLatitude().equals(that.getLatitude()) : that.latitude != null)
            return false;
        if (longitude != null ? !getLongitude().equals(that.getLongitude()) : that.longitude != null)
            return false;
        if (accuracy != null ? !accuracy.equals(that.accuracy) : that.accuracy != null)
            return false;
        return referenceId != null ? referenceId.equals(that.referenceId) : that.referenceId == null;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (temporary != null ? temporary.hashCode() : 0);
        result = 31 * result + (effectiveFrom != null ? effectiveFrom.hashCode() : 0);
        result = 31 * result + (effectiveTo != null ? effectiveTo.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (stateProvCd != null ? stateProvCd.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (countryCd != null ? countryCd.hashCode() : 0);
        result = 31 * result + (addressLine1 != null ? addressLine1.hashCode() : 0);
        result = 31 * result + (addressLine2 != null ? addressLine2.hashCode() : 0);
        result = 31 * result + (addressLine3 != null ? addressLine3.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (county != null ? county.hashCode() : 0);
        result = 31 * result + (inCareOf != null ? inCareOf.hashCode() : 0);
        result = 31 * result + (subdivision != null ? subdivision.hashCode() : 0);
        result = 31 * result + (attention != null ? attention.hashCode() : 0);
        result = 31 * result + (validationIndicator != null ? validationIndicator.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (accuracy != null ? accuracy.hashCode() : 0);
        result = 31 * result + (referenceId != null ? referenceId.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        String parentToString = super.toString();
        return parentToString + "\nChildClass:\nContactMethodAddress{" +
                "temporary='" + temporary + '\'' +
                ", effectiveFrom='" + effectiveFrom + '\'' +
                ", effectiveTo='" + effectiveTo + '\'' +
                ", duration='" + duration + '\'' +
                ", stateProvCd='" + stateProvCd + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", countryCd='" + countryCd + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", addressLine3='" + addressLine3 + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", inCareOf='" + inCareOf + '\'' +
                ", subdivision='" + subdivision + '\'' +
                ", attention='" + attention + '\'' +
                ", validationIndicator='" + validationIndicator + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", accuracy='" + accuracy + '\'' +
                ", referenceId='" + referenceId + '\'' +
                '}';
    }
}
