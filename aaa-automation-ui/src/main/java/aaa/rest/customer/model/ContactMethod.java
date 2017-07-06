package aaa.rest.customer.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import aaa.rest.IModel;

// Change to parent class with common data and children with specific fields
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ContactMethod implements IModel {

    private String id; //for all
    private String contactMethod;// for all
    private String contactType; // for all
    private String preferredInd; // for all
    private String doNotSolicitInd; // for all
    private String comment; // for all

    public ContactMethod() {
    }

    public ContactMethod(ContactMethod contactMethod) {
        this.id = contactMethod.getId();
        this.contactMethod = contactMethod.getContactMethod();
        this.contactType = contactMethod.getContactType();
        this.preferredInd = contactMethod.getPreferredInd();
        this.doNotSolicitInd = contactMethod.getDoNotSolicitInd();
        this.comment = contactMethod.getComment();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactMethod() {
        return contactMethod;
    }

    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getPreferredInd() {
        return preferredInd;
    }

    public void setPreferredInd(String preferredInd) {
        this.preferredInd = preferredInd;
    }

    public String getDoNotSolicitInd() {
        return doNotSolicitInd;
    }

    public void setDoNotSolicitInd(String doNotSolicitInd) {
        this.doNotSolicitInd = doNotSolicitInd;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ContactMethod that = (ContactMethod) o;

        if (contactMethod != null ? !contactMethod.equals(that.contactMethod) : that.contactMethod != null)
            return false;
        if (contactType != null ? !contactType.equals(that.contactType) : that.contactType != null)
            return false;
        if (preferredInd != null ? !preferredInd.equals(that.preferredInd) : that.preferredInd != null)
            return false;
        if (doNotSolicitInd != null ? !doNotSolicitInd.equals(that.doNotSolicitInd) : that.doNotSolicitInd != null)
            return false;
        return comment != null ? comment.equals(that.comment) : that.comment == null;
    }

    @Override public int hashCode() {
        int result = contactMethod != null ? contactMethod.hashCode() : 0;
        result = 31 * result + (contactType != null ? contactType.hashCode() : 0);
        result = 31 * result + (preferredInd != null ? preferredInd.hashCode() : 0);
        result = 31 * result + (doNotSolicitInd != null ? doNotSolicitInd.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "ContactMethod{" +
                "id(Excluded from equals)='" + id + '\'' +
                ", contactMethod='" + contactMethod + '\'' +
                ", contactType='" + contactType + '\'' +
                ", preferredInd='" + preferredInd + '\'' +
                ", doNotSolicitInd='" + doNotSolicitInd + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
