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

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = "PAS-8871")
    public void pas8871_testViewRatingDetailsLink_HO4(@Optional("CA") String state) {

        pas8871_testViewRatingDetailsLink(getPolicyTD());

    }
}
