package aaa.modules.regression.sales.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.template.functional.RevisedHomeTierPATemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.PA)
public class TestDisableReorderReport extends HomeSSHO3BaseTest {

    /**
     * @author Igor Garkusha
     * @name PA Revised Home Tier : Disable Reorder Report at Mid Term
     * @scenario
     * 1.  Create quote
     * 2.  check that user able to Override credit score and re-order report
     * 3.  complete policy creation
     * 4.  create new Endorsement
     * 5.  check that override credit score is disabled
     * 6.  check that user can't re-order report
     * 7.  Verify policy can be bound
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO3, testCaseId = "PAS-6795")
    public void pas6795_disableReorderReportEndorsement(@Optional("PA") String state) {

        new RevisedHomeTierPATemplate().pas6795_disableReorderReportEndorsement(getPolicyType());

    }

    /**
     * @author Josh Carpenter
     * @name PA Revised Home Tier : Disable Reorder Report at Renewal
     * @scenario
     * 1.  Create PA HO3 quote
     * 2.  check that user able to Override credit score and re-order report
     * 3.  complete policy creation
     * 4.  Initiate Renewal
     * 5.  Navigate to Reports tab
     * 5.  check that override credit score is disabled
     * 6.  check that user can't re-order report
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Renewal.HOME_SS_HO3, testCaseId = "PAS-6827, PAS-12770")
    public void pas6827_disableReorderReportRenewal(@Optional("PA") String state) {

        new RevisedHomeTierPATemplate().pas6827_disableReorderReportRenewal(getPolicyType());

    }

}
