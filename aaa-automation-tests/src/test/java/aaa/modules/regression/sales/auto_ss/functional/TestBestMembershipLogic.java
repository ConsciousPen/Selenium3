package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@StateList(states = Constants.States.AZ)
public class TestBestMembershipLogic extends AutoSSBaseTest {

    /**
     * This test requires manual intervention so is disabled until BML wiremock piece put in place.
     * @author Brian Bond
     * @name BML considers Expiration Date on Transfer In Status - PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the BML up for mocking.
     * 2. *Manual Intervention Required* Mock the BML service for Inactive.
     * 3. Create policy with no membership.
     * 4. Run jobs and move VDM forward to NB + 15.
     * 5. *Manual Intervention Required* Mock BML service for an expiration date prior to effective date and transfer-in status.
     * 6. Verify the correct job handling occurs given response.
     * @details
     */
    @Parameters({"state"})
    @Test(/*enabled = false,*/ groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17193")
    public void pas15944_BML_considers_Expiration_Date_On_Transfer_In_Status(@Optional("") String state) {

        /*--Step 1--*/
        log.info("Step 1: *Manual Intervention Required* Set the BML up for mocking.");

        /*--Step 2--*/
        log.info("Step 2: *Manual Intervention Required* Mock the BML service for no-hit.");

        /*--Step 3--*/
        log.info("Step 3: Create policy with no membership.");

        String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());
        String keypathCurrentMember = TestData.makeKeyPath(keypathTabSection, AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel());
        String keypathMemberNum = TestData.makeKeyPath(keypathTabSection, AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());

        TestData testData = getPolicyTD()
                .adjust(keypathCurrentMember, "No")
                .mask(keypathMemberNum);

        mainApp().open();
        createCustomerIndividual();
        createPolicy(testData);

        /*--Step 4--*/
        log.info("Run jobs and move VDM forward to NB + 15.");


        /*--Step 5--*/
        log.info("Step 5: *Manual Intervention Required* Mock BML service for an expiration date prior to effective date and transfer-in status.");


        /*--Step 6--*/
        log.info("Step 6: Verify the correct job handling occurs given response.");

    }
}
