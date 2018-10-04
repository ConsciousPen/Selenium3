package aaa.helpers.claim.datamodel.claim;

import java.util.List;

public class CASClaimResponse {

    private List<ClaimLineItem> claimLineItemList;

    public List<ClaimLineItem> getClaimLineItemList() {
        return claimLineItemList;
    }

    public void setClaimLineItemList(List<ClaimLineItem> claimLineItemList) {
        this.claimLineItemList = claimLineItemList;
    }
}
