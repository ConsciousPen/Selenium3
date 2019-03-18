package aaa.common.components;

public class OtherAAAProductsTableElement {
    public String POLICY_NUMBER_ADDRESS;
    public String POLICY_TYPE;
    public String CUSTOMER_DOB;
    public String EXPIRATION_DATE;
    public String STATUS;
    public String MPD;

    public OtherAAAProductsTableElement(){
        POLICY_NUMBER_ADDRESS = "";
        POLICY_TYPE = "";
        CUSTOMER_DOB = "";
        EXPIRATION_DATE = "";
        STATUS = "";
        MPD = "";
    }

    public OtherAAAProductsTableElement(String policyNumberAddress, String policyType, String customerDOB, String expirationDate, String status, String mpd){
        POLICY_NUMBER_ADDRESS = policyNumberAddress;
        POLICY_TYPE = policyType;
        CUSTOMER_DOB = customerDOB;
        EXPIRATION_DATE = expirationDate;
        STATUS = status;
        MPD = mpd;
    }
}