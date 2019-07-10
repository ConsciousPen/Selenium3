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
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@StateList(states = Constants.States.CA)
public class TestProductDetermination extends AutoCaSelectBaseTest {

    private static final String CHOICE = "CA Choice";
    private static final String SELECT = "CA Select";

    private static final List<String> CUSTOMERS_WITH_VIOLATIONS = Arrays.asList("OneMajor", "ThreeMinor", "OneWithInjuryOneMinor", "OneAtFaultTwoMinor", "TwoAtFaultOneMinor", "DUI");

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

        validateProductDetermination("Suspended", "B3698701");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for a major moving violation in the past 36 months
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
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921, PAS-31654"})
    public void pas30921_testMajorMovingViolation(@Optional("CA") String state) {

        validateProductDetermination("OneMajor", "B3698702");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for three minor moving violations in the past 36 months
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
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921, PAS-31654"})
    public void pas30921_testThreeMinorMovingViolations(@Optional("CA") String state) {

        validateProductDetermination("ThreeMinor", "B3698703");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for three AF accidents without injury in the past 36 months
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

        validateProductDetermination("ThreeAtFault", "B3698704");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for two AF accidents with injury in the past 36 months
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

        validateProductDetermination("TwoWithInjury", "B3698705");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for one AF accident with injury and one minor violation in the past 36 months
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
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921, PAS-31654"})
    public void pas30921_testOneAtFaultAccidentWithInjuryAndOneMinorMovingViolation(@Optional("CA") String state) {

        validateProductDetermination("OneWithInjuryOneMinor", "B3698706");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for one AF accident with injury, one AF accident without injury in the past 36 months
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

        validateProductDetermination("OneWithInjuryOneWithout", "B3698707");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination two minor violations and one AF accident in the past 36 months
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
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921, PAS-31654"})
    public void pas30921_testTwoMinorViolationsAndOneAtFaultAccident(@Optional("CA") String state) {

        validateProductDetermination("OneAtFaultTwoMinor", "B3698708");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for one minor violation and two AF accidents in the past 36 months
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
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921, PAS-31654"})
    public void pas30921_testOneMinorViolationsAndTwoAtFaultAccidents(@Optional("CA") String state) {

        validateProductDetermination("TwoAtFaultOneMinor", "B3698709");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for DUI in past 10 years
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Validate product type is Choice
     * 5. Change date of reinstatement to 121 months ago
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921, PAS-31654"})
    public void pas30921_testAlcoholRelatedConviction(@Optional("CA") String state) {

        validateProductDetermination("DUI", "B3698710");

    }

    /**
     * @author Josh Carpenter
     * @name Test Choice vs. Select Product determination for no valid US or Canadian license
     * @scenario
     * 1. Create new customer
     * 2. Create quote and validate product type is Select
     * 3. Continue with quote and order reports
     * 4. Update license type to 'Foreign'
     * 5. Validate product type is Choice
     * 6. Validate product type is Select
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-30921"})
    public void pas30921_testNonUSCanadianLicense(@Optional("CA") String state) {

        TestData td = getPolicyTD()
                .adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$100,000/$300,000");

        // Create quote and validate product type is Select
        createQuoteAndFillUpTo(td, DriverActivityReportsTab.class);
        validateProductType(SELECT);

        // Update license type to 'Foreign', validate product type is Choice
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_TYPE).setValueContains("Foreign");
        validateProductType(CHOICE);

    }

    private void validateProductDetermination(String lastName, String licenseNum) {
        TestData td = getPolicyTD()
                .adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$100,000/$300,000")
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel()), "Jimbo")
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.LAST_NAME.getLabel()), lastName)
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.DATE_OF_BIRTH.getLabel()), "12/12/1980")
                .adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNum);

        int months = 37;
        if ("DUI".equals(lastName)) {
            months = 121;
        }

        // Create quote and validate product type is Select
        createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class);
        validateProductType(SELECT);

        // Continue with quote and order reports
        premiumAndCoveragesTab.submitTab();
        driverActivityReportsTab.fillTab(td);

        // Validate product type is Choice
        validateProductType(CHOICE);

        // Change date of oldest activity to push it out of 36 month window
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        String newDate = driverTab.getEffectiveDate().minusMonths(months).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        LocalDate oldestDate = LocalDate.of(2500, Month.JANUARY, 1);
        int row = 0;
        for (int i = 1; i <= DriverTab.tableActivityInformationList.getRowsCount(); i++) {
            String thisRowDateString = DriverTab.tableActivityInformationList.getRow(i).getCell("Date").getValue().substring(0, 10);
            LocalDate thisRowDate = LocalDate.parse(thisRowDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            if (thisRowDate.isBefore(oldestDate)) {
                oldestDate = thisRowDate;
                row = i;
            }
        }
        DriverTab.tableActivityInformationList.selectRow(row);
        driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS).setValue("Yes");
        if ("Suspended".equals(lastName)) {
            driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.REINSTATEMENT_DATE).setValue(newDate);
        } else if (CUSTOMERS_WITH_VIOLATIONS.contains(lastName)) {
            driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).setValue(newDate);
        } else {
            driverTab.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue(newDate);
        }

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
