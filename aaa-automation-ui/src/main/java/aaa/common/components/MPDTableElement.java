package aaa.common.components;

public class MPDTableElement {
    public String POLICY_NUMBER_ADDRESS;
    public String POLICY_TYPE;
    public String CUSTOMER_DOB;
    public String EXPIRATION_DATE;
    public String STATUS;
    public String MPD;

    public MPDTableElement(){
        POLICY_NUMBER_ADDRESS = "";
        POLICY_TYPE = "";
        CUSTOMER_DOB = "";
        EXPIRATION_DATE = "";
        STATUS = "";
        MPD = "";
    }

    public MPDTableElement(String policyNumberAddress, String policyType, String customerDOB, String expirationDate, String status, String mpd){
        POLICY_NUMBER_ADDRESS = policyNumberAddress;
        POLICY_TYPE = policyType;
        CUSTOMER_DOB = customerDOB;
        EXPIRATION_DATE = expirationDate;
        STATUS = status;
        MPD = mpd;
    }
}