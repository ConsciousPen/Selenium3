package aaa.common.components;

public class OtherAAAProductsSearchTableElement {
    public String CUSTOMER_NAME_ADDRESS;
    public String CUSTOMER_DOB;
    public String POLICY_TYPE;
    public String OTHER_AAA_PRODUCTS_POLICY_ADDRESS;
    public String STATUS;

    public OtherAAAProductsSearchTableElement(){
        CUSTOMER_NAME_ADDRESS = "";
        POLICY_TYPE = "";
        CUSTOMER_DOB = "";
        OTHER_AAA_PRODUCTS_POLICY_ADDRESS = "";
        STATUS = "";
    }

    public OtherAAAProductsSearchTableElement(String customerNameAddress, String customerDOB, String policyType, String OtherAAAProductsPolicyAddress, String status){
        CUSTOMER_NAME_ADDRESS = customerNameAddress;
        CUSTOMER_DOB = customerDOB;
        POLICY_TYPE = policyType;
        OTHER_AAA_PRODUCTS_POLICY_ADDRESS = OtherAAAProductsPolicyAddress;
        STATUS = status;
    }
}