package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.Tab;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.ComboBox;

@StateList(states = Constants.States.CA)
public class TestInaccurateRatingFromOmittedPoints extends AutoCaSelectBaseTest {

    @DataProvider(name = "newBusinessTest")
    private static Object[][] newBusinessTest(){
        return new Object[][]{
                {"CA", "JOHN", "J", "SHEPARD", "01/01/1980", "404 MASS ST", "C7654321", "CA Choice"}/*, //Scenario: Product Evaluates to CHOICE
                {"CA", "JANE", "B", "DRAPEHS", "02/02/1980", "100 EDI LN", "C1234789", "CA Select", true} //Scenario: Product Evaluates to SELECT */
        };
    }

    PremiumAndCoveragesTab pncTab = new PremiumAndCoveragesTab();
    TestDataHelper _tdHelper = new TestDataHelper();
    ErrorTab et = new ErrorTab();
    DriverActivityReportsTab dart =  new DriverActivityReportsTab();
    DocumentsAndBindTab dabt = new DocumentsAndBindTab();
    PurchaseTab pt = new PurchaseTab();
    TestDataHelper tdHelper = new TestDataHelper();
    DriverTab dt = new DriverTab();
    String _productTypeExpectedThroughoutNewBusiness = null;
    String _productDetermined = null;


    /**
     *      Claim Data: <br>
     *     1. One accident where the insured is NOT the vehicle operator with occurrence date more than 31 months but less than 33 months of the policy effective date that would result in 2 points or more. <br>
     *     2. Another accident that falls more than 33 months but less than 36 months that would result in 1 point. <br>
     *     3. The 3rd accident which falls within 84 months of the policy effective date. <br>
     *     TODO: Add a mechanism to dynamically generate/return a CLUE response, for control over the dates of the claims. OR STUB must somehow reference NB-33mo, NB-36mo, etc.
     *     @author Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL}, description = "Inaccurate rating at NB caused by Include in Points and/or YAF not systematically included in rating")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-17328")
    public void pas17328_Scenario1_SelectToChoice(@Optional("") String state) {
        // Build Test Data
        TestData _td = getPolicyDefaultTD();
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel(), "EUGENE");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.MIDDLE_NAME.getLabel(), "J");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.LAST_NAME.getLabel(), "LUNDIN");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1980");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.ADDRESS_LINE_1.getLabel(), "840 MELROSE HILL ST");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.ZIP_CODE.getLabel(), "90029");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.CITY.getLabel(), "LOS ANGELES");
        _tdHelper.adjustTD(_td, PremiumAndCoveragesTab.class, AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel(), "$100,000/$300,000 (+$0.00)");

        // Open App, Create Customer, Initiate Quote, Fill Up To PNC Tab.
        createQuoteAndFillUpTo(_td, PremiumAndCoveragesTab.class);

        // Capture Product Type. Verify it's 'Select'.
        String productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Select");

        // Advance to order Driver Activity Reports and Order Reports.
        policy.getDefaultView().fillFromTo(_td, PremiumAndCoveragesTab.class, DriverActivityReportsTab.class, true);
        
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        
		TestData activity = DataProviderFactory.emptyData()
                .adjust(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS.getLabel(), "Yes")
                .adjust(AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), DateTimeUtils.getCurrentDateTime().minusMonths(33).format(DateTimeUtils.MM_DD_YYYY));

		
        _td.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel()), activity);
        new DriverTab().fillTab(_td);
        
        // Return to PNC Tab. Capture Product Type. Verify it's 'Select' now.
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        //Changed the assertion to Select for 2 dsr points
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Select");

        // Calculate Premium. Verify Product Hasn't Changed.
        pncTab.calculatePremium();
        //PAS-27328 Changed the assertion to Select for 2 dsr points
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Select");

        // Navigate to Driver Tab and Return to PNC Tab. For debugging later: Adding a BP after this line allows for simple verification of claim data gathered.
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        // Return to PNC Tab. Calculate Premium. Verify Product Hasn't Changed.
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        pncTab.calculatePremium();
        //PAS-27328 Changed the assertion to Select for 2 dsr points
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Select");
    }

    /**
     * This test will become outdated around 2 months after it was first created. <br>
     *    Created April 10th, 2019. STUB Data will grow stale around June 10th, 2019. <br>
     *    Location of Test Data to Update (ON VDM): D:\AAA\Build_to_deploy\aaa-external-stub-services-app\WEB-INF\classes\META-INF\mock\ChoicePointClueMockData.xls <br>
     *    Location of Test Data: TAB = 'CLUE_RESPONSE'; ID = 'YEARS_ACCIDENT_FREE'; <br>
     *        Update CLAIM 1 : Occurrence Date = PolicyEffectiveDate - 32 months. <br>
     *        Update CLAIM 2 : Occurrence Date = PolicyEffectiveDate - 35 months. <br>
     *        Update CLAIM 3 : Occurrence Date = PolicyEffectiveDate - 80 months.
     * @param state
     */
    @Parameters({"state"})
    @Test(dataProvider = "newBusinessTest")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-28101")
    public void testRatingAtNewBusiness(@Optional String state, String firstName, String middleName, String lastName, String DOB, String address, String licenseNumber, String expectedProduct){
        // Prepare Data
        _productTypeExpectedThroughoutNewBusiness = expectedProduct;
        TestData _td = buildTestData(firstName, middleName, lastName, DOB, address, "90029", "LOS ANGELES", licenseNumber);

        // Begin Test
        createQuoteAndFillUpTo(_td, DriverTab.class);
        setForeignLicenseIfScenarioRequires();
        assertFirstProductDetermination(_td); //AC1, AC2
        recalculatePremiumAssertPolicyType(); //AC3, AC5
        orderReportsAndEvaluateProductType(_td, PremiumAndCoveragesTab.class);
        //validateActivityInformation();
        updateActivityToChangeProductTypes(1);
        String newPolicyType = calculatePremiumAndValidatePolicyHasChanged(); //AC4, AC6
        completePolicyBind(_td, newPolicyType);
        //startEndorsementCalculatePremiumValidateUnchanged() //AC7, AC8
    }

    /**
     * Create the test data to be used in testing.
     * @return
     */
    private TestData buildTestData(String firstName, String middleName, String lastName, String DOB, String address, String zip, String city, String licenseNumber){
        TestData td = getPolicyDefaultTD();
        _tdHelper.adjustTD(td, PrefillTab.class, AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel(), firstName);
        _tdHelper.adjustTD(td, PrefillTab.class, AutoCaMetaData.PrefillTab.MIDDLE_NAME.getLabel(), middleName);
        _tdHelper.adjustTD(td, PrefillTab.class, AutoCaMetaData.PrefillTab.LAST_NAME.getLabel(), lastName);
        _tdHelper.adjustTD(td, PrefillTab.class, AutoCaMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), DOB);
        _tdHelper.adjustTD(td, PrefillTab.class, AutoCaMetaData.PrefillTab.ADDRESS_LINE_1.getLabel(), address);
        _tdHelper.adjustTD(td, PrefillTab.class, AutoCaMetaData.PrefillTab.ZIP_CODE.getLabel(), zip);
        _tdHelper.adjustTD(td, PrefillTab.class, AutoCaMetaData.PrefillTab.CITY.getLabel(), city);
        _tdHelper.adjustTD(td, DriverTab.class, AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel(), licenseNumber);
        _tdHelper.adjustTD(td, PremiumAndCoveragesTab.class, AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel(), "$100,000/$300,000 (+$0.00)"); //This line is required to satisfy some of the policy creation requirements in PAS.

        return td;
    }

    private void setForeignLicenseIfScenarioRequires(){
        DriverTab dt = new DriverTab();
        if(_productTypeExpectedThroughoutNewBusiness.equalsIgnoreCase("CA Choice")){
            dt.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_TYPE).setValue("Foreign");
        }
    }

    /**
     * Fills up to the Driver Activity Report Page. <br>
     * Orders DriverTab.tableActivityInformationList.getRow(1).getCell("Points").getValue()all reports.
     */
    private void orderReportsAndEvaluateProductType(TestData in_data, Class<? extends Tab> fromTab){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());

        //If Foreign License, Handle Error with Override
        if(_productTypeExpectedThroughoutNewBusiness.equalsIgnoreCase("CA Choice")){
            et.overrideAllErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.SYSTEM_ISSUE);
            et.buttonOverride.click();
        }

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY).setValue("Yes");
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();


        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());

        // Capture Product Type.
        _productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(_productDetermined).isEqualToIgnoringCase(_productTypeExpectedThroughoutNewBusiness);
    }

    private void assertFirstProductDetermination(TestData in_data){
        //Go to calculate premium for the First Product Determination
        policy.getDefaultView().fillFromTo(in_data, DriverTab.class, PremiumAndCoveragesTab.class, true);

        // Capture Product Type.
        _productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(_productDetermined).isEqualToIgnoringCase(_productTypeExpectedThroughoutNewBusiness);
    }

    private void recalculatePremiumAssertPolicyType(){
        // Recalculate Premium. Assert unchanged.
        new PremiumAndCoveragesTab().calculatePremium();
        _productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(_productDetermined).isEqualToIgnoringCase(_productTypeExpectedThroughoutNewBusiness);
    }

    private void validateActivityInformation(){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        if(_productTypeExpectedThroughoutNewBusiness.equalsIgnoreCase("CA Choice")){
            // Validate Points
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(1).getCell("Points").getValue()).isEqualToIgnoringCase(Integer.toString(2));
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(2).getCell("Points").getValue()).isEqualToIgnoringCase(Integer.toString(1));
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(3).getCell("Points").getValue()).isEqualToIgnoringCase(Integer.toString(1));

            // Validate Include in Points and/or YAF?
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(1).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("Yes");
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(2).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("Yes");
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(3).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("Yes");
        }
        else
            {
            // Validate Points
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(1).getCell("Points").getValue()).isEqualToIgnoringCase(Integer.toString(2));
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(2).getCell("Points").getValue()).isEqualToIgnoringCase(Integer.toString(1));
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(3).getCell("Points").getValue()).isEqualToIgnoringCase(Integer.toString(1));

            // Validate Include in Points and/or YAF?
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(1).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("Yes");
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(2).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("Yes");
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(3).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("Yes");
        }
    }

    private String completePolicyBind(TestData in_data, String currentPolicyType){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        if(currentPolicyType.equalsIgnoreCase("CA Select")){
            dabt.getRequiredToBindAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.RequiredToBind.CALIFORNIA_CAR_POLICY_APPLICATION).setValue("Physically Signed");
            dabt.getRequiredToBindAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.RequiredToBind.SUBSCRIBER_AGGREEMENT).setValue("Physically Signed");
        }else {
            dabt.getRequiredToBindAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.RequiredToBind.PERSONAL_AUTO_APPLICATION).setValue("Physically Signed");
            dabt.getRequiredToBindAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.RequiredToBind.DELETING_UNINSURED_MOTORIST_PROPERTY_DAMAGE_COVERAGE).setValue("Physically Signed");
            dabt.getAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.VEHICLE_INFORMATION).getAsset(AutoCaMetaData.DocumentsAndBindTab.VehicleInformation.ARE_THERE_ANY_ADDITIONAL_INTERESTS).setValue("Yes");
        }
        dabt.btnPurchase.click();
        et.overrideAllErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.SYSTEM_ISSUE);
        et.buttonOverride.click();
        dabt.submitTab();
        pt.fillTab(this.getPolicyDefaultTD());
        pt.submitTab();
        return PolicySummaryPage.getPolicyNumber();
    }

    private void updateActivityToChangeProductTypes(Integer row){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        removeActivityFromIncludedInPointsAndYAF(row);
    }

    private void removeActivityFromIncludedInPointsAndYAF(Integer rowIndex){
        DriverTab.tableActivityInformationList.getRow(rowIndex).getCell(8).controls.links.get("View/Edit").click();
        dt.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OVERRIDE_ACTIVITY_DETAILS).setValue("Yes");
        dt.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF).setValue("No");
        dt.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.NOT_INCLUDED_IN_POINTS_AND_OR_YAF_REASON_CODES).setValue("Company Error");

        // Also remove foreign license if still present.
        if(_productTypeExpectedThroughoutNewBusiness.equalsIgnoreCase("CA Choice")){
            dt.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_TYPE).setValue("Valid");
            new DriverTab().getAssetList().getAsset(AutoCaMetaData.DriverTab.FIRST_US_CANADA_LICENSE_DATE).setValue("01/01/2000");
            new DriverTab().getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_STATE).setValue("CA");
        }
    }

    private String calculatePremiumAndValidatePolicyHasChanged(){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        String policyTypeAfterActivityUpdates = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        pncTab.calculatePremium();
        CustomAssertions.assertThat(policyTypeAfterActivityUpdates).isNotEqualToIgnoringCase(_productTypeExpectedThroughoutNewBusiness);
        return policyTypeAfterActivityUpdates;
    }
}
