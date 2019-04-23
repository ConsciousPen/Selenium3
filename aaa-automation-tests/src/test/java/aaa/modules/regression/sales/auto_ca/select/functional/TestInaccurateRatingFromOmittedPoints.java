package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.Tab;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import org.testng.annotations.*;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.ComboBox;

@StateList(states = Constants.States.CA)
public class TestInaccurateRatingFromOmittedPoints extends AutoCaSelectBaseTest {

    @DataProvider(name = "toggleTest")
    private static Object[][] toggleTest(){
        return new Object[][]{
                {"CA", "EUGENE", "J", "LUNDIN", "01/01/1980", "840 MELROSE HILL ST ", "C9654321", "CA Choice"}};
    }

    PremiumAndCoveragesTab pncTab = new PremiumAndCoveragesTab();
    TestDataHelper _tdHelper = new TestDataHelper();
    ErrorTab et = new ErrorTab();
    DriverActivityReportsTab dart =  new DriverActivityReportsTab();
    DocumentsAndBindTab dabt = new DocumentsAndBindTab();
    PurchaseTab pt = new PurchaseTab();
    DriverTab dt = new DriverTab();
    String _productTypeExpectedThroughoutNewBusiness = null;
    final String _PRODUCT_TYPE_BEFORE_REPORT_ORDER = "CA Select";
    String _productDetermined = null;
    String _capturedPremiumValue = null;

    /**
     * This test will become outdated around 2 months after it was first created. <br>
     *    Created April 23rd, 2019. STUB Data will grow stale around June 23rd, 2019. <br>
     *    Location of Test Data to Update (ON VDM): D:\AAA\Build_to_deploy\aaa-external-stub-services-app\WEB-INF\classes\META-INF\mock\ChoicePointClueMockData.xls <br>
     *    Location of Test Data: TAB = 'CLUE_RESPONSE'; ID = 'CAH10' (~line 147, 148, 149); <br>
     *        Update CLAIM 1 : Occurrence Date = PolicyEffectiveDate - 32 months. <br>
     *        Update CLAIM 2 : Occurrence Date = PolicyEffectiveDate - 35 months. <br>
     *        Update CLAIM 3 : Occurrence Date = PolicyEffectiveDate - 80 months.
     * @param state
     */
    @Parameters({"state"})
    @Test(dataProvider = "toggleTest")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-28101")
    public void testRatingAtNewBusiness(@Optional String state, String firstName, String middleName, String lastName, String DOB, String address, String licenseNumber, String expectedProduct){
        // Prepare Data
        _productTypeExpectedThroughoutNewBusiness = expectedProduct;
        TestData _td = buildTestData(firstName, middleName, lastName, DOB, address, "90029", "LOS ANGELES", licenseNumber);

        // Begin Test
        createQuoteAndFillUpTo(_td, DriverTab.class);
        assertFirstProductDetermination(_td); // AC1, AC2
        recalculatePremiumBeforeOrderReports(); // AC3, AC5
        orderReportsAndEvaluateProductType(_td, PremiumAndCoveragesTab.class);
        validateActivityInformation();
        updateActivityToChangeProductTypes(1);
        String newPolicyType = calculatePremiumAndValidatePolicyHasChanged(); // AC4, AC6
        reorderReports();
        completePolicyBind(_td, newPolicyType);
        startEndorsementCalculatePremiumValidateUnchanged(_td); // AC7, AC8
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

    /**
     * Fills up to the Driver Activity Report Page. <br>
     * Orders DriverTab.tableActivityInformationList.getRow(1).getCell("Points").getValue()all reports.
     */
    private void orderReportsAndEvaluateProductType(TestData in_data, Class<? extends Tab> fromTab){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());

        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY).setValue("Yes");
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV).setValue("I Agree");
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();


        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());

        // Capture Product Type.
        _productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(_productDetermined).isEqualToIgnoringCase(_productTypeExpectedThroughoutNewBusiness);
    }

    private void assertFirstProductDetermination(TestData in_data){
        // Go to calculate premium for the First Product Determination
        policy.getDefaultView().fillFromTo(in_data, DriverTab.class, PremiumAndCoveragesTab.class, true);

        // Capture Product Type
        _productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(_productDetermined).isEqualToIgnoringCase(_PRODUCT_TYPE_BEFORE_REPORT_ORDER);
    }

    private void recalculatePremiumBeforeOrderReports(){
        // Recalculate Premium. Assert unchanged.
        new PremiumAndCoveragesTab().calculatePremium();
        _productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(_productDetermined).isEqualToIgnoringCase(_PRODUCT_TYPE_BEFORE_REPORT_ORDER);
    }

    private void validateActivityInformation(){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        if(_productTypeExpectedThroughoutNewBusiness.equalsIgnoreCase("CA Choice")){
            // Validate Points
            validatePoints(1, 2);
            validatePoints(2, 0);
            validatePoints(3, 0);

            // Validate Include in Points and/or YAF?
            validateIncludedInPoints(1, true);
            validateIncludedInPoints(2, false);
            validateIncludedInPoints(3, false);
        }
        else
            {
            // Validate Points
            validatePoints(1, 0);
            validatePoints(2, 0);
            validatePoints(3, 0);

            // Validate Include in Points and/or YAF?
                validateIncludedInPoints(1, false);
                validateIncludedInPoints(2, false);
                validateIncludedInPoints(3, false);
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
        _capturedPremiumValue = PurchaseTab.tablePaymentPlan.getColumn("Premium").getCell(1).getValue();
        pt.submitTab();
        return PolicySummaryPage.getPolicyNumber();
    }

    /**
     * Updates activities on the policy to convert product type to opposite type. <br>
     *     Choice -> Select. Select -> Choice.
     * @param row
     */
    private void updateActivityToChangeProductTypes(Integer row){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        if(_productTypeExpectedThroughoutNewBusiness.equalsIgnoreCase("CA Choice")){
            removeActivityFromIncludedInPointsAndYAF(row);
        }else{
            dt.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
            dt.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE).setValue("Minor Violation");
            dt.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Improper passing");
            dt.getActivityInformationAssetList().getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2012");
        }
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

    private void reorderReports(){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV).setValue("I Agree");
        dart.getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
    }

    private void startEndorsementCalculatePremiumValidateUnchanged(TestData in_td){
        policy.createEndorsement(getPolicyTD("Endorsement", "TestData"));

        //Fill Endorsement with Blank Data
        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, PremiumAndCoveragesTab.class, true);
        String newPremium = PremiumAndCoveragesTab.getPolicyTermPremium().toString();
        CustomAssertions.assertThat(PremiumAndCoveragesTab.getPolicyTermPremium().toString()).isEqualTo(_capturedPremiumValue);
    }

    private void validatePoints(Integer row, Integer expectedPoints){
        CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(row).getCell("Points").getValue()).isEqualToIgnoringCase(Integer.toString(expectedPoints));
    }

    private void validateIncludedInPoints(Integer row, boolean bExpectedToBeIncludedInRating){
        if(bExpectedToBeIncludedInRating){
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(row).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("Yes");
        }else{
            CustomAssertions.assertThat(DriverTab.tableActivityInformationList.getRow(row).getCell("Include in Points and/or YAF?").getValue()).isEqualToIgnoringCase("No");
        }
    }
}
