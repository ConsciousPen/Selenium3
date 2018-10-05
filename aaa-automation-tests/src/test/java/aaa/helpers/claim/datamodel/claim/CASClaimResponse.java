package aaa.helpers.claim.datamodel.claim;

import java.util.List;

@SuppressWarnings("unused")
public class CASClaimResponse {

    private List<ClaimLineItem> claimLineItemList;

    public List<ClaimLineItem> getClaimLineItemList() {
        return claimLineItemList;
    }

    public void setClaimLineItemList(List<ClaimLineItem> claimLineItemList) {
        this.claimLineItemList = claimLineItemList;
    }

    @Override
    public String toString() {
        return "CASClaimResponse{" +
                "claimLineItemList=" + claimLineItemList +
                '}';
    }
}
