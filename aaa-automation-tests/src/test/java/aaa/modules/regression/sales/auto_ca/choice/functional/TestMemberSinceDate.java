package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.utils.StateList;
import org.assertj.core.api.Assertions;
import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
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
     * 3. Validate that the Member Since Date in the DB and UI are null.
     * 4. Order report in the UI.
     * 5. Validate that the Member Since Date in the DB now matches the Stub response.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-17193")
    public void pas17193_MemberSinceDate_DB_Matches_Stub_Response(@Optional("CA") String state) {


        /*--Step 1--*/
        Log.info("Step 1: Create Customer.");

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_RMS_MembershipValid").resolveLinks());
        mainApp().open();
        createCustomerIndividual();

        /*--Step 2--*/
        Log.info("Step 2: Create Auto CA Quote up to Membership tab.");
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, MembershipTab.class, false);

        /*--Step 3--*/
        Log.info("Step 3: Validate that the Member Since Date in the DB and UI are null.");
        String quoteNumber = policy.getDefaultView().getTab(MembershipTab.class).getPolicyNumber();

        // Click save to store the quote in the db so can be accessed.
        Tab.buttonTopSave.click();

        Boolean dbMemberSinceDateIsNull = !GetAAAMemberSinceDateFromSQL(quoteNumber).isPresent();

        Assertions.assertThat(dbMemberSinceDateIsNull);

        // BondTODO: Validate no control for this is present in the UI. May have to try/catch this one.
        //policy.getDefaultView().getTab(MembershipTab.class).//verifyFieldIsNotDisplayed("Member Since");

        /*--Step 4--*/
        Log.info("Step 4: Order report in the UI.");
        policy.getDefaultView().getTab(MembershipTab.class).fillTab(testData);
        // BondTODO: Click save so value in DB gets updated.

        /*--Step 5--*/
        Log.info("Step 5: Validate that the Member Since Date in the DB now matches the Stub response.");
        // BondTODO: Get Data from DB
        // BondTODO: Assert data is not set yet.
    }

    /**
     * Returns the AAA Membership Member Since Date from DB.
     * @param quoteOrPolicyNumber
     * @return
     */
    private java.util.Optional<String> GetAAAMemberSinceDateFromSQL(String quoteOrPolicyNumber){

        String quoteOrPolicyColumn = quoteOrPolicyNumber.toUpperCase().startsWith("Q") ? "quotenumber" : "policynumber";

        String columnToJoinOn = String.format("AND ps.%1s ='%2s' ", quoteOrPolicyColumn, quoteOrPolicyNumber);

        String query =
                "SELECT MS.MEMBERSINCEDATE AS MS_MEMBERSINCEDATE " +
                        "FROM policysummary ps " +
                        "JOIN OTHERORPRIORPOLICY OP ON OP.POLICYDETAIL_ID=ps.POLICYDETAIL_ID AND OP.PRODUCTCD ='membership' " +
                         columnToJoinOn +
                        "JOIN MEMBERSHIPSUMMARYENTITY MS ON MS.ID=ps.MEMBERSHIPSUMMARY_ID";

        return DBService.get().getValue(query);
    }
}
