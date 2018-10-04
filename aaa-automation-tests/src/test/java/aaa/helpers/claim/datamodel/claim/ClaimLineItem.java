package aaa.helpers.claim.datamodel.claim;

import java.util.ArrayList;
import java.util.List;

public class ClaimLineItem {

    private String product;
    private String agreementNumber;
    private List<Claim> claimList;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public List<Claim> getClaimList() {
        return claimList;
    }

    public void setClaimList(List<Claim> claimList) {
        this.claimList = claimList;
    }

    public static ClaimLineItem newInstance(String product, String agreementNumber) {
        ClaimLineItem claimLineItem = new ClaimLineItem();
        claimLineItem.product = product;
        claimLineItem.agreementNumber = agreementNumber;
        claimLineItem.claimList = new ArrayList<>();
        return claimLineItem;
    }
}
