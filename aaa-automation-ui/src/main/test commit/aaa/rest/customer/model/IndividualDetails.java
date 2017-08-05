package aaa.rest.customer.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndividualDetails {

    private String firstName;
    private String lastName;
    private String middleName;
    private String title;
    private String suffix;
    private String nickname;
    private String birthDate;
    private String genderCd;
    private String maritalStatusCd;
    private String taxId;
    private String deceased;
    private String deathDate;
    private String deathNotificationReceived;
    private String occupationCd;
    private String occupationDescription;
    private String tobaccoCd;
    private String citizenshipCd;
    private List<String> interests;
    private List<String> disabilities;
    private String designationCd;
    private String designationDescription;
    private String associateEmployments;
    private String associateBusinessEntity;

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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGenderCd() {
        return genderCd;
    }

    public void setGenderCd(String genderCd) {
        this.genderCd = genderCd;
    }

    public String getMaritalStatusCd() {
        return maritalStatusCd;
    }

    public void setMaritalStatusCd(String maritalStatusCd) {
        this.maritalStatusCd = maritalStatusCd;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getDeceased() {
        return deceased;
    }

    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getDeathNotificationReceived() {
        //stick for difference between ui customer creation and through rest (1st case - value false, 2nd - value null)
        if (deathNotificationReceived == null) {
            this.deathNotificationReceived = "false";
        }
        return deathNotificationReceived;
    }

    public void setDeathNotificationReceived(String deathNotificationReceived) {
        this.deathNotificationReceived = deathNotificationReceived;
    }

    public String getOccupationCd() {
        return occupationCd;
    }

    public void setOccupationCd(String occupationCd) {
        this.occupationCd = occupationCd;
    }

    public String getOccupationDescription() {
        return occupationDescription;
    }

    public void setOccupationDescription(String occupationDescription) {
        this.occupationDescription = occupationDescription;
    }

    public String getTobaccoCd() {
        return tobaccoCd;
    }

    public void setTobaccoCd(String tobaccoCd) {
        this.tobaccoCd = tobaccoCd;
    }

    public String getCitizenshipCd() {
        return citizenshipCd;
    }

    public void setCitizenshipCd(String citizenshipCd) {
        this.citizenshipCd = citizenshipCd;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getDisabilities() {
        return disabilities;
    }

    public void setDisabilities(List<String> disabilities) {
        this.disabilities = disabilities;
    }

    public String getDesignationCd() {
        return designationCd;
    }

    public void setDesignationCd(String designationCd) {
        this.designationCd = designationCd;
    }

    public String getDesignationDescription() {
        return designationDescription;
    }

    public void setDesignationDescription(String designationDescription) {
        this.designationDescription = designationDescription;
    }

    public String getAssociateEmployments() {
        return associateEmployments;
    }

    public void setAssociateEmployments(String associateEmployments) {
        this.associateEmployments = associateEmployments;
    }

    public String getAssociateBusinessEntity() {
        return associateBusinessEntity;
    }

    public void setAssociateBusinessEntity(String associateBusinessEntity) {
        this.associateBusinessEntity = associateBusinessEntity;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IndividualDetails that = (IndividualDetails) o;

        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null)
            return false;
        if (suffix != null ? !suffix.equals(that.suffix) : that.suffix != null)
            return false;
        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null)
            return false;
        if (birthDate != null ? !birthDate.equals(that.birthDate) : that.birthDate != null)
            return false;
        if (genderCd != null ? !genderCd.equals(that.genderCd) : that.genderCd != null)
            return false;
        if (maritalStatusCd != null ? !maritalStatusCd.equals(that.maritalStatusCd) : that.maritalStatusCd != null)
            return false;
        if (taxId != null ? !taxId.equals(that.taxId) : that.taxId != null)
            return false;
        if (deceased != null ? !deceased.equals(that.deceased) : that.deceased != null)
            return false;
        if (deathDate != null ? !deathDate.equals(that.deathDate) : that.deathDate != null)
            return false;
        if (!getDeathNotificationReceived().equals(that.getDeathNotificationReceived()))
            return false;
        if (occupationCd != null ? !occupationCd.equals(that.occupationCd) : that.occupationCd != null)
            return false;
        if (occupationDescription != null ? !occupationDescription.equals(that.occupationDescription) : that.occupationDescription != null)
            return false;
        if (tobaccoCd != null ? !tobaccoCd.equals(that.tobaccoCd) : that.tobaccoCd != null)
            return false;
        if (citizenshipCd != null ? !citizenshipCd.equals(that.citizenshipCd) : that.citizenshipCd != null)
            return false;
        if (interests != null ? !interests.equals(that.interests) : that.interests != null)
            return false;
        if (disabilities != null ? !disabilities.equals(that.disabilities) : that.disabilities != null)
            return false;
        if (designationCd != null ? !designationCd.equals(that.designationCd) : that.designationCd != null)
            return false;
        if (designationDescription != null ? !designationDescription.equals(that.designationDescription) : that.designationDescription != null)
            return false;
        if (associateEmployments != null ? !associateEmployments.equals(that.associateEmployments) : that.associateEmployments != null)
            return false;
        return associateBusinessEntity != null ? associateBusinessEntity.equals(that.associateBusinessEntity) : that.associateBusinessEntity == null;
    }

    @Override public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (genderCd != null ? genderCd.hashCode() : 0);
        result = 31 * result + (maritalStatusCd != null ? maritalStatusCd.hashCode() : 0);
        result = 31 * result + (taxId != null ? taxId.hashCode() : 0);
        result = 31 * result + (deceased != null ? deceased.hashCode() : 0);
        result = 31 * result + (deathDate != null ? deathDate.hashCode() : 0);
        result = 31 * result + (deathNotificationReceived != null ? deathNotificationReceived.hashCode() : 0);
        result = 31 * result + (occupationCd != null ? occupationCd.hashCode() : 0);
        result = 31 * result + (occupationDescription != null ? occupationDescription.hashCode() : 0);
        result = 31 * result + (tobaccoCd != null ? tobaccoCd.hashCode() : 0);
        result = 31 * result + (citizenshipCd != null ? citizenshipCd.hashCode() : 0);
        result = 31 * result + (interests != null ? interests.hashCode() : 0);
        result = 31 * result + (disabilities != null ? disabilities.hashCode() : 0);
        result = 31 * result + (designationCd != null ? designationCd.hashCode() : 0);
        result = 31 * result + (designationDescription != null ? designationDescription.hashCode() : 0);
        result = 31 * result + (associateEmployments != null ? associateEmployments.hashCode() : 0);
        result = 31 * result + (associateBusinessEntity != null ? associateBusinessEntity.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "IndividualDetails{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", title='" + title + '\'' +
                ", suffix='" + suffix + '\'' +
                ", nickname='" + nickname + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", genderCd='" + genderCd + '\'' +
                ", maritalStatusCd='" + maritalStatusCd + '\'' +
                ", taxId='" + taxId + '\'' +
                ", deceased='" + deceased + '\'' +
                ", deathDate='" + deathDate + '\'' +
                ", deathNotificationReceived='" + deathNotificationReceived + '\'' +
                ", occupationCd='" + occupationCd + '\'' +
                ", occupationDescription='" + occupationDescription + '\'' +
                ", tobaccoCd='" + tobaccoCd + '\'' +
                ", citizenshipCd='" + citizenshipCd + '\'' +
                ", interests=" + interests +
                ", disabilities=" + disabilities +
                ", designationCd='" + designationCd + '\'' +
                ", designationDescription='" + designationDescription + '\'' +
                ", associateEmployments='" + associateEmployments + '\'' +
                ", associateBusinessEntity='" + associateBusinessEntity + '\'' +
                '}';
    }
}
