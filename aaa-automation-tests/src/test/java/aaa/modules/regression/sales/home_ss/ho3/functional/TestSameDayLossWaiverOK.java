package aaa.modules.regression.sales.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestSameDayLossWaiverOKTemplate;
import toolkit.utils.TestInfo;

public class TestSameDayLossWaiverOK extends TestSameDayLossWaiverOKTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

    /**
     * @author Josh Carpenter
     * @name Test First Clue loss within experience period is waived during new business
     * @scenario
     * 1. Initiate OK property quote with customer that returns multiple Clue claims within experience period
     * 2. Fill up to Premium & Coverages Tab
     * 3. Calculate premium
     * 4. Navigate back to Property Info tab
     * 5. Validate first claim is waived
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-25467")
    public void pas25467_testFirstLossWaivedClueNB(@Optional("OK") String state) {
        testFirstLossWaivedClueNB();
    }

    /**
     * @author Josh Carpenter
     * @name Test First Clue loss within experience period is waived during mid-term endorsement to add NI
     * @scenario
     * 1. Create OK property policy with customer that has clean claim history
     * 2. Initiate mid-term endorsement
     * 3. Add second named insured that returns multiple Clue claims within experience period
     * 4. Calculate premium
     * 5. Navigate back to Property Info tab
     * 6. Validate first claim is waived
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-25467")
    public void pas25467_testFirstLossWaivedClueEndorsement(@Optional("OK") String state) {
        testFirstLossWaivedClueEndorsement();
    }

    /**
     * @author Josh Carpenter
     * @name Test same day waiver is applied for multiple Clue claims with the same loss date during new business
     * @scenario
     * 1. Initiate OK property quote with customer that returns multiple Clue claims within experience period with same loss date
     * 2. Fill up to Premium & Coverages Tab
     * 3. Calculate premium
     * 4. Navigate back to Property Info tab
     * 5. Validate that loss with the least severity in terms of premium (claim points, type of loss, etc.) is given same day waiver
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-25467")
    public void pas25467_testSameDayWaiverClueNB(@Optional("OK") String state) {
        testSameDayWaiverClueNB();
    }

    /**
     * @author Josh Carpenter
     * @name Test same day waiver is applied for multiple Clue claims with the same loss date during endorsement to add NI
     * @scenario
     * 1. Create OK property policy with customer that has clean claim history
     * 2. Initiate mid-term endorsement
     * 3. Fill up to Premium & Coverages Tab
     * 4. Calculate premium
     * 5. Navigate back to Property Info tab
     * 6. Validate that loss with the least severity in terms of premium (claim points, type of loss, etc.) is given same day waiver
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-25467")
    public void pas25467_testSameDayWaiverClueEndorsement(@Optional("OK") String state) {
        testSameDayWaiverClueEndorsement();
    }

}
