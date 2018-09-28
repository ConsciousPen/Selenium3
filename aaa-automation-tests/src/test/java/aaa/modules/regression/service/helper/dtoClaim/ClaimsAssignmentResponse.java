package aaa.modules.regression.service.helper.dtoClaim;

import java.util.List;

public class ClaimsAssignmentResponse {

    private List<Claim> unmatchedClaims;

    private List<Claim> matchedClaims;

    public List<Claim> getUnmatchedClaims() {
        return unmatchedClaims;
    }

    public void setUnmatchedClaims(List<Claim> unmatchedClaims) {
        this.unmatchedClaims = unmatchedClaims;
    }

    public List<Claim> getMatchedClaims() {
        return matchedClaims;
    }

    public void setMatchedClaims(List<Claim> matchedClaims) {
        this.matchedClaims = matchedClaims;
    }

    @Override
    public String toString() {
        return "ClaimsAssignmentResponse{" +
                "unmatchedClaims=" + unmatchedClaims +
                ", matchedClaims=" + matchedClaims +
                '}';
    }
}
