package aaa.helpers.freemaker.datamodel.claim;

import java.util.ArrayList;
import java.util.List;

public class CASClaimResponse {

    private List<ClaimLineItem> claimLineItemList;

    public List<ClaimLineItem> getClaimLineItemList() {
        return claimLineItemList;
    }

    public void setClaimLineItemList(List<ClaimLineItem> claimLineItemList) {
        this.claimLineItemList = claimLineItemList;
    }

    public static CASClaimResponse newInstance() {
        CASClaimResponse claimResponse = new CASClaimResponse();
        claimResponse.claimLineItemList = new ArrayList<>();
        return claimResponse;
    }
}
