package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@StateList(states = Constants.States.AZ)
public class TestMemberSinceDate extends HomeSSHO3BaseTest {

    /**
     * @author Brian Bond
     * @name MemberSinceDate in database matches stub response - PAS-17193
     * @scenario Precondition: Have an active valid membership response from the Stub
     * 1. Create Customer.
     * 2. Create HO3 SS Quote up to Reports tab.
     * 3. Validate that the Member Since Date in the DB is null.
     * 4. Order report in the UI.
     * 5. Validate that the Member Since Date in the DB and UI now matches the Stub response.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-17193")
    public void pas17193_MemberSinceDate_DB_Matches_Stub_Response(@Optional("") String state) {

        // Pattern Definition
        DateTimeFormatter formatSQL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatUI = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        /*--Step 1--*/
        log.info("Step 1: Create Customer.");

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_RMS_ValidMemberSinceDate").resolveLinks());
        mainApp().open();
        createCustomerIndividual();

        /*--Step 2--*/
        log.info("Step 2: Create HO3 SS Quote up to Reports tab.");
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, ReportsTab.class, false);


        /*--Step 3--*/
        log.info("Step 3: Validate that the Member Since Date in the DB and UI are null.");
        String quoteNumber = policy.getDefaultView().getTab(ReportsTab.class).getPolicyNumber();

        // Click save to store the quote in the db so can be accessed.
        Tab.buttonTopSave.click();

        assertThat(AAAMembershipQueries.getAAAMemberSinceDateFromSQL(quoteNumber)).isNotPresent();


        /*--Step 4--*/
        log.info("Step 4: Order report in the UI.");
        policy.getDefaultView().getTab(ReportsTab.class).fillTab(testData);

        // Click save so value in DB gets updated.
        Tab.buttonTopSave.click();


        /*--Step 5--*/
        log.info("Step 5: Validate that the Member Since Date in the DB now matches the Stub response.");

        String dbMemberSinceDate = AAAMembershipQueries.getAAAMemberSinceDateFromSQL(quoteNumber).orElse("Null Value");

        LocalDateTime DateTime = LocalDateTime.parse(dbMemberSinceDate, formatSQL);

        String sqlExpected = DateTime.format(formatSQL);

        assertThat(sqlExpected).isEqualTo("2010-07-27 00:00:00");

        String uiMemberSinceDate = policy.getDefaultView().getTab(ReportsTab.class).getAssetList().
                getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1).
                getCell(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.MEMBER_SINCE_DATE.getLabel()).getValue();

        String uiExpected = DateTime.format(formatUI);
        assertThat(uiExpected).isEqualTo(uiMemberSinceDate);
    }
}