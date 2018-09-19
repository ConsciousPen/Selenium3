package aaa.modules.regression.sales.auto_ca.choice.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@StateList(states = Constants.States.CA)
public class TestMemberSinceDate extends AutoCaChoiceBaseTest {

    /**
     * @author Brian Bond
     * @name MemberSinceDate in database matches stub response - PAS-17193
     * @scenario Precondition: Have an active valid membership response from the Stub
     * 1. Create Customer.
     * 2. Create Auto CA Quote up to Membership tab.
     * 3. Validate that the Member Since Date in the DB is null.
     * 4. Order report in the UI.
     * 5. Validate that the Member Since Date in the DB and UI now matches the Stub response.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-17193")
    public void pas17193_MemberSinceDate_DB_Matches_Stub_Response(@Optional("CA") String state) {

        // Pattern Definition
        DateTimeFormatter formatSQL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /*--Step 1--*/
        log.info("Step 1: Create Customer.");

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_RMS_ValidMemberSinceDate").resolveLinks());
        mainApp().open();
        createCustomerIndividual();


        /*--Step 2--*/
        log.info("Step 2: Create Auto CA Quote up to Membership tab.");
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, MembershipTab.class, false);


        /*--Step 3--*/
        log.info("Step 3: Validate that the Member Since Date in the DB and UI are null.");
        String quoteNumber = policy.getDefaultView().getTab(MembershipTab.class).getPolicyNumber();

        // Click save to store the quote in the db so can be accessed.
        Tab.buttonTopSave.click();

        assertThat(AAAMembershipQueries.getAAAMemberSinceDateFromSQL(quoteNumber)).isNotPresent();


        /*--Step 4--*/
        log.info("Step 4: Order report in the UI.");
        policy.getDefaultView().getTab(MembershipTab.class).fillTab(testData);

        // Click save so value in DB gets updated.
        Tab.buttonTopSave.click();


        /*--Step 5--*/
        log.info("Step 5: Validate that the Member Since Date in the DB now matches the Stub response.");

        String dbMemberSinceDate = AAAMembershipQueries.getAAAMemberSinceDateFromSQL(quoteNumber).orElse("Null Value");

        LocalDateTime DateTime = LocalDateTime.parse(dbMemberSinceDate, formatSQL);

        String sqlExpected = DateTime.format(formatSQL);

        assertThat(sqlExpected).isEqualTo("2010-07-27 00:00:00");
    }
}
