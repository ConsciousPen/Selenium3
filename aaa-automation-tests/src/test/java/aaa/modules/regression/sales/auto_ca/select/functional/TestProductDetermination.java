package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import org.testng.annotations.*;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.TextBox;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@StateList(states = Constants.States.CA)
public class TestProductDetermination extends AutoCaSelectBaseTest {

    private static final String CHOICE = "CA Choice";
    private static final String SELECT = "CA Select";

    private DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private DriverTab driverTab = new DriverTab();

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testSuspendedRevokedLicense(@Optional("CA") String state) {

        validateProductDetermination("Suspended");

    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testInvalidLicense(@Optional("CA") String state) {

        // TODO Talk to Alex and implement

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testMajorMovingViolation(@Optional("CA") String state) {

        validateProductType("OneMajor");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testThreeMinorMovingViolations(@Optional("CA") String state) {

        validateProductType("ThreeMinor");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testThreeAtFaultAccidents(@Optional("CA") String state) {

        validateProductDetermination("ThreeAtFault");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testTwoAtFaultAccidentsWithInjury(@Optional("CA") String state) {

        validateProductDetermination("TwoWithInjury");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testOneAtFaultAccidentWithInjuryAndOneMinorMovingViolation(@Optional("CA") String state) {

        validateProductDetermination("OneWithInjuryOneMinor");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testOneAtFaultAccidentWithInjuryAndOneAtFaultAccident(@Optional("CA") String state) {

        validateProductDetermination("OneWithInjuryOneWithout");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testTwoMinorViolationsAndOneAtFaultAccident(@Optional("CA") String state) {

        validateProductDetermination("OneAtFaultTwoMinor");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testOneMinorViolationsAndTwoAtFaultAccidents(@Optional("CA") String state) {

        validateProductDetermination("TwoAtFaultOneMinor");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testAlcoholRelatedConviction(@Optional("CA") String state) {

        validateProductDetermination("DUI");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a suspended or revoked license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 37 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testConvictedOfInsuranceFraud(@Optional("CA") String state) {

        // TODO not sure how to mock this up

    }

    private void validateProductDetermination(String lastName) {
        TestData td = getPolicyTD()
                .adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$100,000/$300,000")
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel()), "Jimbo")
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.LAST_NAME.getLabel()), lastName)
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.DATE_OF_BIRTH.getLabel()), "12/12/1980")
                .adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "B3698701");

        // Create quote, validate product type is Select, order reports
        // Create quote and validate product type is Select
        createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class);
        validateProductType(SELECT);

        // Continue with quote and order reports
        premiumAndCoveragesTab.submitTab();
        driverActivityReportsTab.fillTab(td);

        // Validate product type is Choice
        validateProductType(CHOICE);

        // Change date of most recent activity to qualify for Select
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        String newDate = driverTab.getEffectiveDate().minusMonths(37).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        LocalDate mostRecentDate = LocalDate.of(1900, Month.JANUARY, 1);
        int row = 0;
        for (int i = 1; i <= DriverTab.tableActivityInformationList.getRowsCount(); i++) {
            String thisRowDateString = DriverTab.tableActivityInformationList.getRow(1).getCell("Date").getValue().substring(0, 10);
            LocalDate thisRowDate = LocalDate.parse(thisRowDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            if (thisRowDate.isAfter(mostRecentDate)) {
                //                mostRecentDate = thisRowDate;
                row = i;
            }
        }
        DriverTab.tableActivityInformationList.selectRow(row);
        driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS).setValue("Yes");
        TextBox dateField;
        if (driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.REINSTATEMENT_DATE).isPresent()) {
            dateField = driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.REINSTATEMENT_DATE);
        } else {
            dateField = driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_DATE);
        }
        dateField.setValue(newDate);

        // Validate product type is Select
        validateProductType(SELECT);
    }

    private void validateProductType(String expectedType) {
        if (!premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).isPresent()) {
            premiumAndCoveragesTab.calculatePremium();
        }
        assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).getValue()).isEqualTo(expectedType);
    }

}
