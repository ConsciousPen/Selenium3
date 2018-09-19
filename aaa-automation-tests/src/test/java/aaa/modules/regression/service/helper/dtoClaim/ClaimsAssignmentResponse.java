package aaa.modules.regression.service.helper.dtoClaim;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ClaimsAssignmentResponse {

    @Setter
    @Getter
    private List<Claim> unmatchedClaims;

    @Setter
    @Getter
    private List<Claim> matchedClaims;
}
