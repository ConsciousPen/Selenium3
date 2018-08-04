package aaa.modules.regression.sales.auto_ca.choice.functional;

import java.time.LocalDateTime;

import aaa.common.enums.Constants;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.utils.StateList;
import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestMemberSinceDate extends AutoCaChoiceBaseTest {

    /**
     * @author Brian Bond
     * @name MemberSinceDate in database matches stub response - PAS-17193
     * @scenario
     * Precondition: Have an active valid membership response from the Stub
     * 1. Create Customer.
     * 2. Create Auto CA Quote up to Membership tab.
     * 3. Validate that the Member Since Date in the DB is null.
     * 4. Order report in the UI.
     * 5. Validate that the Member Since Date in the DB now matches the Stub response.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-17193")
    public void pas17193_MemberSinceDate_DB_Matches_Stub_Response(@Optional("CA") String state) {
        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_RMS_MembershipValid").resolveLinks());

        Log.info("Step 1: Create Customer.");
        mainApp().open();
        createCustomerIndividual();

        Log.info("Step 2: Create Auto CA Quote up to Membership tab.");
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, MembershipTab.class, false);

        // BondTODO: Click save to store the quote in the db so can be accessed.

        Log.info("Step 3: Validate that the Member Since Date in the DB is null.");
        // BondTODO: Get Data from DB
        // BondTODO: Assert data is not set yet.

        Log.info("Step 4: Order report in the UI.");
        policy.getDefaultView().getTab(MembershipTab.class).fillTab(testData);
        // BondTODO: Click save so value in DB gets updated.

        Log.info("Step 5: Validate that the Member Since Date in the DB now matches the Stub response.");
        // BondTODO: Get Data from DB
        // BondTODO: Assert data is not set yet.
    }
}
