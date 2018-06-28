package aaa.modules.regression.sales.home_ca.ho4.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.modules.regression.sales.template.functional.TestViewRatingDetailsLinkTemplate ;
import java.time.LocalDateTime;


@StateList(states = Constants.States.CA)
public class TestViewRatingDetailsLink extends TestViewRatingDetailsLinkTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO4;
    }

    protected LocalDateTime policyExpirationDate;

    /**
     * @author Dakota Berg
     * @name View Rating Details Link Unavailable for CA HO policy in pending renewal status
     * @scenario
     * 1. Create a policy
     * 2. Set the system date to R - 35
     * 3. Run jobs: 'renewalOfferGenerationPart1' and 'renewalOfferGenerationPart2'
     * 4. Create a renewal
     * 5. Pay the minimum due payment for the renewal
     * 6. Select the pending policy
     * 7. Go to the Premium and Coverages tab and verify that the View Rating Details Link is enabled
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "PAS-8871")
    public void pas8871_testViewRatingDetailsLink_HO4(@Optional("CA") String state) {

        pas8871_testViewRatingDetailsLink(getPolicyTD());

    }
}
