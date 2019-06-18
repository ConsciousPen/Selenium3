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
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

@StateList(states = Constants.States.CA)
public class TestProductDetermination extends AutoCaSelectBaseTest {

    private static final String CHOICE = "CA Choice";
    private static final String SELECT = "CA Select";
    private static final String AF_ACCIDENT = "At-Fault Accident";
    private static final String MAJOR_VIOLATION = "Major Violation";

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

        TestData td = getTdForSpecificCustomer("SuspendedOne");

        // Create quote and validate product type is Select
        createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class);
        assertThat(getProductType()).isEqualTo(SELECT);

        // Continue with quote and order reports
        premiumAndCoveragesTab.submitTab();
        driverActivityReportsTab.fillTab(td);

        // Validate product type is Choice
        assertThat(getProductType()).isEqualTo(CHOICE);

        // Change date of reinstatement
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        String reinstateDate = driverTab.getEffectiveDate().minusMonths(37).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS).setValue("Yes");
        driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.REINSTATEMENT_DATE).setValue(reinstateDate);

        // Validate product type is Select
        assertThat(getProductType()).isEqualTo(SELECT);

    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testInvalidLicense(@Optional("CA") String state) {

        // TODO Talk to Alex and implement

    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testMajorMovingViolation(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testThreeMinorMovingViolations(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testThreeAtFaultAccidents(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testTwoAtFaultAccidentsWithInjury(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testOneAtFaultAccidentWithInjuryAndOneMinorMovingViolation(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testOneAtFaultAccidentWithInjuryAndOneAtFaultAccident(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testTwoMinorViolationsAndOneAtFaultAccident(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testOneMinorViolationsAndTwoAtFaultAccidents(@Optional("CA") String state) {


    }


    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testAlcoholRelatedConviction(@Optional("CA") String state) {


    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testConvictedOfInsuranceFraud(@Optional("CA") String state) {


    }

    private String getProductType() {
        if (!premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).isPresent()) {
            premiumAndCoveragesTab.calculatePremium();
        }
        return premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).getValue();
    }

    private TestData getTdForSpecificCustomer(String lastName) {
        return getPolicyTD()
                .adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$100,000/$300,000")
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel()), "Jimbo")
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.LAST_NAME.getLabel()), lastName)
                .adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "B3698701");
    }

    private TestData getActivityInfoTd(String type, String description) {
        return DataProviderFactory.dataOf(
                AutoCaMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), type,
                AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), description,
                AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), "$<today-10M>",
                AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), "3000");
    }

}
