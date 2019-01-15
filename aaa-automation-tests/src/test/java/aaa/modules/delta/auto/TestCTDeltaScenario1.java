/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
* CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.delta.auto;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.RatingDetailsViewPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.Arrays;
import java.util.List;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

/**
 * @author Viktor Petrenko
 * @name
 * @scenario 1. Create customer
 * 2. Initiate AutoSS quote creation
 * 3. Go to General Tab
 * 4. Verify Dropdown Values on General tab
 * 5. Verify that there is no Motorcycle option in 'AAA Products Owned' section
 * 6. Select option "Yes" For all available products owned - Life, Home, Renters, Condo.
 * 7. Do not provide policy numbers for any of those current AAA policies.
 * 8. Verify field TollFree Number visible
 * 9. Select any option other than "None" for 'Adversely Impacted' field.
 * 10. Verify dropdown visible
 * @details
 */
@Test(groups = {Groups.DELTA, Groups.HIGH})
public class TestCTDeltaScenario1 extends AutoSSBaseTest {

    private static final String ERROR_MESSAGE = "Extraordinary Life Circumstance was applied to the policy";
    public String scenarioPolicyType = "Auto SS";
    private String quoteNumber;
    private DriverTab driverTab = new DriverTab();
    private VehicleTab vehicleTab = new VehicleTab();
    private PrefillTab prefillTab = new PrefillTab();
    private GeneralTab generalTab = new GeneralTab();
    private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private ErrorTab errorTab = new ErrorTab();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Create customer
     * 2. Initiate AutoSS quote creation
     * 3. Move to Prefill tab
     * 4. Verify Dropdown Values in Prefill tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testSC1_TC01(@Optional("") String state) {
        initiateQuote();

        //010-005CT
        //If the zip code is associated with only one county/township, the drop down list contains only that county/township value with default being that value

        assertThat(prefillTab.getAssetList().getAsset(AutoSSMetaData.PrefillTab.COUNTY_TOWNSHIP).getValue()).isEqualTo("New Haven / Middlebury");

        //If the zip code spans across counties/townships, the drop down list will contain the applicable counties/townships that are associated with the zip code; the default value is 'blank'.

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Move to General tab
     * 3. Verify Dropdown Values at General tab
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC02(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.GENERAL);

        //Residence
        List<String> expectedValuesOfResidence = Arrays.asList("Own Home", "Own Condo", "Own Mobile Home", "Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other");
        assertThat(generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE).getAllValues()).containsSequence(expectedValuesOfResidence);
        //Current AAA Member
        List<String> expectedValuesOfCurrentMember = Arrays.asList("Yes", "No", "Membership Pending");
        assertThat(generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).getAllValues()).containsSequence(expectedValuesOfCurrentMember);
        //Source of Business

        List<String> expectedValuesOfSourceOfBusiness = Arrays.asList("New Business", "Rewrite", "Split", "Spin");
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.SOURCE_OF_BUSINESS).getAllValues()).containsSequence(expectedValuesOfSourceOfBusiness);
        //Policy Type
        List<String> expectedValuesOfPolicyType = Arrays.asList("Standard", "Named Non Owner");
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE).getAllValues()).containsSequence(expectedValuesOfPolicyType);
        //PolicyTerm
        List<String> expectedValuesOfPolicyTerm = Arrays.asList("Annual", "Semi-annual");
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM).getAllValues()).containsSequence(expectedValuesOfPolicyTerm);
        //Motorcycle related items should be absent on page
        assertThat(generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE).isPresent()).isEqualTo(true);
        assertThat(generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE).isPresent()).isEqualTo(false);

        generalTab.fillTab(getPolicyTD());

        //1. ELC field is present
        //2. Drop-down contain correct values
        //3. ELC default value = None


        assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE).isPresent()).isEqualTo(true);
        List<String> expectedValuesOfELC = Arrays.asList("None",
                "A catastrophic illness or injury",
                "The death of a spouse, child or parent",
                "Divorce",
                "Involuntary loss of employment for a period of 3 months/ more, if it results from involuntary termination",
                "Total or other loss that makes your home uninhabitable",
                "Identity theft",
                "Other events, as determined by the insurer",
                "Declined");
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE).getAllValues()).containsSequence(expectedValuesOfELC);

        //020-008CT, ELC default = None
        assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE).getValue()).isEqualTo("None");

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Move to General tab
     * 3. check country/township defaulted to blank
     * 4. check country/township defaulted to value.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC03(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.GENERAL);

        //Set zip = 06756 (associated with several county/township)
        //Check country/township defaulted to blank
        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE).setValue("06756");

        assertThat(generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.COUNTY_TOWNSHIP).getValue()).isEmpty();

        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE)
                .setValue(getCustomerIndividualTD("DataGather", "GeneralTab_CT").getValue("Zip Code"));

        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.COUNTY_TOWNSHIP).setValue("index=1");

        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_BTN).click();
        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG).submit();
        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Move to Driver tab
     * 3. Check driver tab dropdown values
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC04(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.DRIVER);

        //Driver Type
        List<String> expectedDriverType = Arrays.asList("Excluded", "Available for Rating", "Not Available for Rating");
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DRIVER_TYPE).getAllValues()).containsSequence(expectedDriverType);

        List<String> expectedRelationToNI = Arrays.asList("First Named Insured", "Spouse", "Child", "Parent",
                "Sibling", "Other Resident Relative", "Employee", "Other");
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED).getAllValues()).containsSequence(expectedRelationToNI);

        List<String> expectedGender = Arrays.asList("Male", "Female");
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.GENDER).getAllValues()).containsSequence(expectedGender);

        List<String> expectedMaritalStatus = Arrays.asList("Married", "Single", "Divorced", "Widowed", "Separated");
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS).getAllValues()).containsSequence(expectedMaritalStatus);

        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS).getAllValues()).doesNotContain("Domestic Partner");
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS).getAllValues()).doesNotContain("Registered Domestic Partner");
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS).getAllValues()).doesNotContain("Civil Union");

        //PAS13 ER fix-App change#21-As per US 28555, License type dropdown values have changed
        List<String> expectedLicenseStatus = Arrays.asList("Licensed (US)", "Licensed (Canadian)", "Foreign", "Not Licensed", "Learner's Permit");
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_TYPE).getAllValues()).containsSequence(expectedLicenseStatus);

    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Move to Driver tab
     * 3. Fill violation section Add Minor violation, Add Speeding violation, Add PD violation
     * 4. Verify Claim points = 0
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC05_6_7(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.DRIVER);

        driverTab.fillTab(getPolicyTD());
        driverTab.fillTab(getTestSpecificTD("TestData_CT567"));

        //violation points should be = 0
        assertSoftly(softly -> {
            softly.assertThat(DriverTab.tableActivityInformationList.getRow("Description", "Improper Turn").getCell("Points")).hasValue("0");
            softly.assertThat(DriverTab.tableActivityInformationList.getRow("Description", "Speeding").getCell("Points")).hasValue("0");
            softly.assertThat(DriverTab.tableActivityInformationList.getRow("Description", "Accident (Property Damage Only)").getCell("Points")).hasValue("0");
        });
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Move to Driver tab
     * 3. Add Major violation , check points
     * 4. Add BI violation , check points
     * 5. Check points for Major violation
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC08(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.DRIVER);

        driverTab.fillTab(getPolicyTD());
        driverTab.fillTab(getTestSpecificTD("TestData_CT8"));

            assertSoftly(softly -> {
        //violation points should be = 7
            softly.assertThat(DriverTab.tableActivityInformationList.getRow("Description", "Accident (Resulting in Bodily Injury)").getCell("Points")).hasValue("7");
        //violation points should be = 4
            softly.assertThat(DriverTab.tableActivityInformationList.getRow("Description", "Hit and Run").getCell("Points")).hasValue("4");
        //go to Major accident, points for the same day should be = 0
        DriverTab.tableActivityInformationList.getRow("Description", "Hit and Run").getCell(8).controls.links.getFirst().click(Waiters.AJAX);
        driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER).setValue("No");
            softly.assertThat(driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.VIOLATION_POINTS)).hasValue("0");
        });
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Move to Driver tab
     * 3. Override Score points
     * 4. Check - ELC message is not present
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC09(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS);

        ratingDetailReportsTab.fillTab(getTestSpecificTD("RatingDetailReportsTab_TC9"));
        assertThat(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.ELC_MESSAGE).isPresent()).isFalse();

        Tab.buttonSaveAndExit.click(Waiters.AJAX);

    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Move to Vehicle tab
     * 3. Verify dropdowns
     * 4. Verify Motorcycle Discount Not Present;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC10_11(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.VEHICLE);

        List<String> expectedValuesOfVehicleType = Arrays.asList("Private Passenger Auto", "Limited Production/Antique", "Trailer", "Motor Home", "Conversion Van");
        assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE)).hasOptions(expectedValuesOfVehicleType);

        List<String> expectedValuesOfVehicleUsage = Arrays.asList("Pleasure", "Commute", "Business", "Artisan", "Farm");
        assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE)).hasOptions(expectedValuesOfVehicleUsage);

        vehicleTab.fillTab(getTestSpecificTD("TestData_CT10"));

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1)
                .getValue()).doesNotContain("Motorcycle Discount");

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Go to the premium coverages tab and select high bi limit
     * 3. Get error message: “UMBI/UIMBI limits may not exceed twice the BI limits”.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC12(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY)
                .setValueByRegex("\\$500,000.*1,000,000.*");
        new PremiumAndCoveragesTab().calculatePremium();
        String expected_ER = "UMBI/UIMBI limits may not exceed twice the BI limits";
        assertThat(errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, expected_ER).isPresent()).isEqualTo(true);
        errorTab.cancel();

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY)
                .setValueByRegex("\\$50,000.*100,000.*");
        new PremiumAndCoveragesTab().calculatePremium();

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Check default value for "Underinsured Motorist Conversion Coverage" = No
     * 3. Apply "UIM Conversion Coverage" and check presence
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC13(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY)
                .setValueByRegex("\\$200,000.*600,000.*");
        new PremiumAndCoveragesTab().calculatePremium();

        //default value for "Underinsured Motorist Conversion Coverage" = No
            assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_CONVERSION_COVERAGE)).hasValue("No");

        assertThat(PremiumAndCoveragesTab.tableTermPremiumbyVehicle.getColumn(1).getValue().contains("UIM Conversion Coverage not selected")).isEqualTo(true);

        // UIM Conversion Coverage = NO
        premiumAndCoveragesTab.getRatingDetailsVehiclesData().forEach(i -> assertThat(i.getValue("UIM Conversion Coverage")).isEqualTo("No"));

        assertThat(premiumAndCoveragesTab.getRatingDetailsQuoteInfoData().getValue("ELC Applied")).contains("No");

        RatingDetailsViewPage.buttonRatingDetailsOk.click();

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_CONVERSION_COVERAGE)
                .setValue("Yes");

        assertThat(PremiumAndCoveragesTab.tableTermPremiumbyVehicle.getColumn(1).getValue()).contains("UIM Conversion Coverage selected");

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Viktor Petrenko
     * @name
     * @scenario 1. Open created quoted
     * 2. Check EXTRAORDINARY_LIFE_CIRCUMSTANCE default value and Apply ELC
     * 3. Message should present "Extraordinary Life Circumstance was applied to the policy"
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC14(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.GENERAL);

            assertThat(generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE)).hasValue("None");
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE).setValue("Identity theft");
        //Go to Premium tab, calculate premium
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().calculatePremium();

        assertThat(premiumAndCoveragesTab.getRatingDetailsQuoteInfoData().getValue("ELC Applied")).contains("Yes");
        RatingDetailsViewPage.buttonRatingDetailsOk.click();

        //Go to Driver Reports tab, check message
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
        assertThat(ratingDetailReportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.ELC_MESSAGE).getValue()).contains(ERROR_MESSAGE);
        Tab.buttonSaveAndExit.click();
    }

    private void preconditions(NavigationEnum.AutoSSTab navigateTo) {
        initiateQuote();
        NavigationPage.toViewTab(navigateTo.get());
    }

    private void initiateQuote() {
        mainApp().open();
        String quote = getQuoteNumber();
        MainPage.QuickSearch.buttonSearchPlus.click();

        SearchPage.openQuote(quote);
        policy.dataGather().start();
    }

    private String getQuoteNumber() {
        if (quoteNumber == null) {
            mainApp().open();
            createCustomerIndividual();
            policy.initiate();
            policy.getDefaultView().fillUpTo(getPolicyTD(), GeneralTab.class, true);
            Tab.buttonSaveAndExit.click();
            quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
            log.info("DELTA CT SC1: ASS Quote created with #{}", quoteNumber);
        }
        return quoteNumber;
    }
}
