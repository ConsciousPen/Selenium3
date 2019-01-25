/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.delta.auto;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.util.Arrays;
import java.util.Collections;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.*;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.DoNotRenewActionTab;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.endorsements.AutoSSForms;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * @author Dmitry Chubkov
 * @name General tab controls check for AutoSS product, CO state [TC01]
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
public class TestCODeltaScenario1 extends AutoSSBaseTest {
    private String quoteNumber;
    private String policyNumber;

    private DriverTab driverTab = new DriverTab();
    private PremiumAndCoveragesTab pacTab = new PremiumAndCoveragesTab();
    private GeneralTab gTab = new GeneralTab();
    private FormsTab fTab = new FormsTab();
    private DocumentsAndBindTab dTab = new DocumentsAndBindTab();
    private ActivityInformationMultiAssetList aiAssetList = driverTab.getActivityInformationAssetList();
    private ErrorTab errorTab = new ErrorTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private GenerateOnDemandDocumentActionTab onDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();
    DoNotRenewActionTab doNotRenewActionTab = new DoNotRenewActionTab();
    private AutoSSForms.AutoSSPolicyFormsController policyForms = fTab.getAssetList().getAsset(AutoSSMetaData.FormsTab.POLICY_FORMS);
    private AutoSSForms.AutoSSDriverFormsController driverForms = fTab.getAssetList().getAsset(AutoSSMetaData.FormsTab.DRIVER_FORMS);

    @Parameters({"state"})
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC01(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.GENERAL);

        assertSoftly(softly -> {
            //Verify Dropdown Values on General tab
            softly.assertThat(gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("Own Home", "Own Condo", "Own Mobile Home", "Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other"));

            softly.assertThat(gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.SOURCE_OF_BUSINESS.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("New Business", "Spin", "Split", "Rewrite", "Book Roll"));

            softly.assertThat(gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("Standard", "Named Non Owner"));

            softly.assertThat(gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("Annual", "Semi-annual"));

            //Verify that there is no Motorcycle option in 'AAA Products Owned' section
            softly.assertThat(gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED).getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE)).isPresent(false);

            //Select option "Yes" For all available products owned - Life, Home, Renters, Condo.
            gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED).getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE).setValue(true);
            gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED).getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).setValue(true);
            gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED).getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).setValue(true);
            gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED).getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).setValue(true);

            //Verify field TollFree Number visible
            softly.assertThat(gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.TOLLFREE_NUMBER)).isPresent();

            //Select any option other than "None" for 'Adversely Impacted' field.
            gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).setAnyValueExcept("None");

            //Verify dropdown visible
            softly.assertThat(gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED)).isPresent();
        });

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name Driver tab controls check for AutoSS product, CO state [TC02]
     * @scenario 1. Create customer
     * 2. Initiate AutoSS quote creation
     * 3. Move to Driver tab
     * 4. Add second drive
     * 5. Verify following Marital Statuses available for CO: "Registered Domestic Partner/Civil Union", "Common Law"
     * 6. Verify Dropdown Values in Driver tab
     * @details
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC02(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.DRIVER);

        driverTab.fillTab(getTestSpecificTD("TestData"));

        assertSoftly(softly -> {

            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("Available for Rating", "Not Available for Rating", "Excluded"));

            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("First Named Insured", "Spouse", "Child", "Parent", "Sibling", "Other Resident Relative", "Employee", "Other", "Registered Domestic Partner/Civil Union"));

            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.GENDER.getLabel(), ComboBox.class)).hasOptions(Arrays.asList("Male", "Female"));

            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("Married", "Single", "Divorced", "Widowed", "Separated", "Registered Domestic Partner/Civil Union", "Common Law"));

            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), ComboBox.class)).hasOptions(
                    Arrays.asList("Licensed (US)", "Licensed (Canadian)", "Foreign", "Not Licensed", "Learner's Permit"));
        });

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name Driver tab controls check for AutoSS product, CO state [TC03]
     * @scenario 1. Create customer
     * 2. Initiate AutoSS quote creation
     * 3. Move to Driver tab
     * 4. Click on the 'Add Activity', select Type: 'Principally At-Fault Accident', Description: 'Principally At-Fault Accident (Property Damage Only)'
     * 5. Review the dropdown values for 'Apply Waiver' field, check that 'Insurance Incident Waiver' not applicable for CO
     * 6. Remove activity information
     * 7. Click on the 'Add Activity', select Type 'Minor Violation', Description: 'Improper Passing'
     * 8. Set Occurrence Date = '01/10/2012', verify 'Conviction Date' field appears as enabled and empty by default
     * 9. Leave 'Conviction Date' empty, verify that no 'Violation points' are calculated (equals to '0')
     * 10. Enter a 'Conviction Date', verify that 'Violation points' are calculated (not 0)
     * 11. Enter a 'Conviction Date' that is later than the current date and click Continue button
     * 12. Verify that error message appears: 'Conviction Date later than current date' is displayed. User should stay in Driver tab
     * 13. Remove activity information, verify that 'List of Activity Information' table gets empty
     * @details
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC03(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.DRIVER);

        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Principally At-Fault Accident");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Principally At-Fault Accident (Property Damage Only)");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2012");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).setValue("1500");
        //Skipped step: Review the dropdown values for 'Apply Waiver' field, check that 'Insurance Incident Waiver' not applicable for CO
        DriverTab.tableActivityInformationList.removeRow(1);

        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("Minor Violation");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Improper Passing");
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue("01/10/2012");

        assertSoftly(softly -> {
            softly.assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE)).isPresent();
            softly.assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE)).isEnabled();
            softly.assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE)).hasValue("");
            softly.assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.VIOLATION_POINTS)).hasValue("0");
        });
        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).setValue("01/01/2015");

        aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(5).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(aiAssetList.getWarning(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE)).hasValue("Conviction Date later than current date");
        Tab.buttonNext.click();
        NavigationPage.Verify.viewTabSelected(NavigationEnum.AutoSSTab.DRIVER.get());
        DriverTab.tableActivityInformationList.isPresent();

        DriverTab.tableActivityInformationList.removeRow(1);
        //#TODO [DriverTab.tableActivityInformationList.verify.empty();]

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name CO_SC1_TC04
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC04(@Optional("") String state) {
        VehicleTab vTab = new VehicleTab();

        preconditions(NavigationEnum.AutoSSTab.DRIVER);
        TestData adjustedData = getTestSpecificTD("TestData").adjust(driverTab.getMetaKey(), Collections.singletonList(getTestSpecificTD("DriverTab_TC04")));
        policy.getDefaultView().fillFromTo(adjustedData, DriverTab.class, VehicleTab.class, true);

        assertSoftly(softly -> {
            softly.assertThat(vTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.TYPE)).hasOptions(Arrays.asList("Private Passenger Auto", "Limited Production/Antique", "Trailer", "Motor Home", "Conversion Van", "Trailer"));
            softly.assertThat(vTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE)).hasOptions(Arrays.asList("Pleasure", "Commute", "Business", "Artisan", "Farm"));
        });

        vTab.submitTab();

        Tab.buttonNext.click();
        new PremiumAndCoveragesTab().btnCalculatePremium().click();
        //in old test this verification was skipped with comment 'PAS12:AS per RSG, commenting premium rating verifications'
        //PremiumAndCoveragesTab.totalTermPremium.verify.value(new Dollar("3,409.00").toString());

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name CO_SC1_TC05
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC05(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);

        assertSoftly(softly -> {
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY)).hasValue("$100,000/$300,000 (+$0.00)");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY)).hasValue("$50,000  (+$0.00)");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY)).hasValue("$100,000/$300,000 (+$0.00)");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS)).hasValue("$5,000  (+$0.00)");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE)).hasValue("$250  (+$0.00)");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE)).hasValue("$500  (+$0.00)");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE)).hasValue("$1,500.00");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS)).hasValue("No Coverage");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT)).hasValue("No Coverage (+$0.00)");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE)).hasValue("No Coverage (+$0.00)");
        });

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name CO_SC1_TC06
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC06(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);
        PremiumAndCoveragesTab.RatingDetailsView.open();
        //CO DELTA - No full safety glass
        //Update: 080-006CO_VA_V3.0 is updated to add Full safety glass coverage
        assertSoftly(softly -> {
            softly.assertThat(pacTab.getRatingDetailsVehiclesData().stream().allMatch(td -> td.containsKey("Full Safety Glass"))).isTrue();
            softly.assertThat(pacTab.getRatingDetailsQuoteInfoData().getValue("Adversely Impacted Applied")).isEqualTo("Yes");
        });

        pacTab.submitTab();
        //PAS 11 fix application change #35
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_200103);
        //PAS11 CR fix
        errorTab.overrideErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.TEMPORARY_ISSUE, ErrorEnum.Errors.ERROR_200103);
        errorTab.override();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
        StaticElement warningMessage = new StaticElement(By.id("policyDataGatherForm:warningMessage"));
        assertThat(warningMessage).hasValue(String.format("Adversely Impacted was applied to the policy effective %s.", QuoteDataGatherPage.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY)));

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).setValue("None");
        gTab.submitTab();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().btnCalculatePremium().click();
        PremiumAndCoveragesTab.RatingDetailsView.open();
        assertThat(pacTab.getRatingDetailsQuoteInfoData().getValue("Adversely Impacted Applied")).isEqualTo("No");
        pacTab.submitTab();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
        assertThat(warningMessage).isPresent(false);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).setAnyValueExcept("None");
        gTab.submitTab();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().btnCalculatePremium().click();

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name CO_SC1_TC07
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC07(@Optional("") String state) {
        preconditions(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES);

        assertSoftly(softly -> {
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS)).hasValue("No Coverage");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE)).hasValue(new Dollar(1500).toString());

            pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueByRegex("No Coverage.*");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE)).hasValue("$250  (+$0.00)");

            pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueByRegex("\\$250.*");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE)).isPresent(false);

            pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS).setValueByRegex("No Coverage.*");
            new PremiumAndCoveragesTab().btnCalculatePremium().click();
            pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS).setValue("Yes");
            softly.assertThat(pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.MOTORCYCLE)).isPresent(false);
            pacTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS).setValue("No");
        });

        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name CO_SC1_TC08
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC08(@Optional("") String state) {
        DocumentsAndBindTab dabTab = new DocumentsAndBindTab();

        preconditions(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS);

        assertSoftly(softly -> {
            policy.getDefaultView().fillFromTo(getPolicyTD(), DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
            softly.assertThat(dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE)).isPresent();
            softly.assertThat(dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE)).hasValue("Yes");

            softly.assertThat(dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE)).isPresent();

            softly.assertThat(dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_LIFE_POLICY_NUMBER)).isPresent();
            softly.assertThat(dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_HOME_POLICY_NUMBER)).isPresent();
            softly.assertThat(dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_RENTERS_POLICY_NUMBER)).isPresent();
            softly.assertThat(dabTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EXISTING_AAA_CONDO_POLICY_NUMBER)).isPresent();
        });

        dabTab.submitTab();
        errorTab.verify.errorsPresent(
                ErrorEnum.Errors.ERROR_200060_CO,
                ErrorEnum.Errors.ERROR_200401,
                ErrorEnum.Errors.ERROR_AAA_CSA3080819,
                ErrorEnum.Errors.ERROR_AAA_CSA3082394,
                ErrorEnum.Errors.ERROR_AAA_CSA3083444,
                ErrorEnum.Errors.ERROR_AAA_CSA3080903);

        errorTab.overrideErrors(ErrorEnum.Errors.ERROR_200060_CO, ErrorEnum.Errors.ERROR_200401);

        Tab.buttonCancel.click();
        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Dmitry Chubkov
     * @name CO_SC1_TC09
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC09(@Optional("") String state) {
        GenerateOnDemandDocumentActionTab goddTab = new GenerateOnDemandDocumentActionTab();

        mainApp().open();
        SearchPage.openQuote(getQuoteNumber());
        policy.quoteDocGen().start();

        goddTab.verify
                .documentsPresent(DocGenEnum.Documents.AA11CO, DocGenEnum.Documents.AA43CO, DocGenEnum.Documents.AAIQCO, DocGenEnum.Documents.AHFMXX, DocGenEnum.Documents.AU03, DocGenEnum.Documents.AA16CO, DocGenEnum.Documents.AADNCO);
        //#TODO: not presented DocGenEnum.Documents.AHAUXX
        assertThat(goddTab.getDocumentsControl().getTable().getRow(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, DocGenEnum.Documents.AA43CO.getId())
                .getCell(DocGenConstants.OnDemandDocumentsTable.SELECT).controls.checkBoxes.getFirst()).isDisabled();

        TestData td = DataProviderFactory.dataOf(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DocumentsRow.FREE_FORM_TEXT.getLabel(), "Free Text");
        goddTab.generateDocuments(td);
        WebDriverHelper.switchToDefault();
        NavigationPage.Verify.mainTabSelected(NavigationEnum.AppMainTabs.QUOTE.get());
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
    }

    /**
     * @author Dmitry Chubkov
     * @name CO_SC1_TC10
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC10(@Optional("") String state) {
        DocGenHelper
                .verifyDocumentsGenerated(getQuoteNumber(), DocGenEnum.Documents.AA11CO, DocGenEnum.Documents.AAIQCO, DocGenEnum.Documents.AHFMXX, DocGenEnum.Documents.AU03, DocGenEnum.Documents.AA16CO, DocGenEnum.Documents.AADNCO);
        //#TODO: not presented DocGenEnum.Documents.AHAUXX);
    }

    /**
     * @author Maksim Piatrouski
     * @name CO_SC1_TC11_12
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC11_12(@Optional("") String state) {
        mainApp().open();
        SearchPage.openQuote(getQuoteNumber());
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_TC11"), GeneralTab.class, DriverActivityReportsTab.class, false);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());

        assertSoftly(softly -> {
            softly.assertThat(driverForms.tableSelectedForms).hasMatchingRows(1, ImmutableMap.of("Name", "AA43CO 01 13", "Description", "Named Driver Exclusion Endorsement"));
            softly.assertThat(policyForms.tableSelectedForms).hasMatchingRows(1, ImmutableMap.of("Name", "AA16CO 08 09", "Description", "Medical Payments Rejection Coverage"));
            //#TODO: another NAME displayed -  softly.assertThat(policyForms.tableSelectedForms).hasMatchingRows(1, ImmutableMap.of("Name", "AA52COA 07 06", "Description", "Rejection of Uninsured/Underinsured Motorists Coverage"));
        });
    }

    /**
     * @author Maksim Piatrouski
     * @name CO_SC1_TC13_14
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC13_14(@Optional("") String state) {
        mainApp().open();
        SearchPage.openQuote(getQuoteNumber());
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        assertThat(dTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.STATEMENT_ELECTING_LOWER_LIMITS_FOR_UM_UIM_COVERAGE)).isPresent();
        dTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.STATEMENT_ELECTING_LOWER_LIMITS_FOR_UM_UIM_COVERAGE).setValue("No");
        dTab.submitTab();

        assertThat(errorTab.tableErrors).hasMatchingRows(1, ImmutableMap.of("Code", "200037_CO", "Message", "A signed Uninsured and Underinsured motorist coverage selection form must be ..."));
        errorTab.tableErrors.getRowContains("Code", "200037_CO").getCell("Code").controls.links.getFirst().click();
        assertThat(dTab.getDocumentsForPrintingAssetList()).isPresent();

        dTab.submitTab();
        overrideAllErrors();

        dTab.submitTab();
        purchaseTab.fillTab(getPolicyDefaultTD()).submitTab();

        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CO SC1: ASS Policy created with #" + policyNumber);
    }

    /**
     * @author Maksim Piatrouski
     * @name CO_SC1_TC15
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC15(@Optional("") String state) {
        mainApp().open();
        SearchPage.openPolicy(getPolicyNumber());
        PolicySummaryPage.buttonTasks.click();
        assertSoftly(softly -> {
            softly.assertThat(MyWorkSummaryPage.tableTasks).hasMatchingRows(1, ImmutableMap.of("Task Name", "If Medical Payments coverage is rejected, a signed form must be received. Need to go to RFI if overridden"));
            softly.assertThat(MyWorkSummaryPage.tableTasks).hasMatchingRows(1, ImmutableMap.of("Task Name", "A Signed Statement Electing Lower Limits for Uninsured and Underinsured Motorists Coverage must be received"));
        });
        Tab.buttonCancel.click();
    }

    /**
     * @author Maksim Piatrouski
     * @name CO_SC1_TC16
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC16(@Optional("") String state) {
        mainApp().open();
        SearchPage.openPolicy(getPolicyNumber());
        policy.policyDocGen().start();

        Table table = onDemandDocumentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.ON_DEMAND_DOCUMENTS).getTable();

        assertSoftly(softly -> {
            for (String key : getTestSpecificTD("TestData_DocGen").getKeys()) {
                softly.assertThat(table)
                        .hasMatchingRows(1, ImmutableMap.of("Document #", key, "Document Name", getTestSpecificTD("TestData_DocGen").getValue(key)));
            }
        });

        //#TODO: every doc need different data to fill
        /*for (Row row : table.getRows()) {
            if (row.getCell("Select").controls.checkBoxes.getFirst().isEnabled())
                row.getCell("Select").controls.checkBoxes.getFirst().setValue(true);
        }

        onDemandDocumentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).setValue("Central Print");
        onDemandDocumentActionTab.submitTab();*/
    }

    /**
     * @author Maksim Piatrouski
     * @name CO_SC1_TC18_19
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC18_19(@Optional("") String state) {
        mainApp().open();
        SearchPage.openPolicy(getPolicyNumber());
        policy.endorse().perform(getTestSpecificTD("TestData_TC18").getTestData("EndorsementActionTab"));

        policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_TC18").getTestData("Policy"), FormsTab.class, false);
        assertThat(policyForms.tableSelectedForms).hasMatchingRows(1, ImmutableMap.of("Name", "AA41CO 01 12", "Description", "Named Non-Owner Coverage Endorsement"));

        fTab.submitTab();
        assertThat(errorTab.tableErrors).hasMatchingRows(1, ImmutableMap.of("Code", "AAA_SS10260450_CO", "Message", "Only \"spouse\" or \"registered domestic partner/civil union\" can be selected fo..."));

        errorTab.tableErrors.getRowContains("Code", "AAA_SS10260450_CO").getCell("Code").controls.links.getFirst().click();
        assertThat(DriverTab.tableDriverList).isPresent();

        policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_TC19").getTestData("Policy1"), PremiumAndCoveragesTab.class, false);
        assertThat(errorTab.tableErrors).hasMatchingRows(1, ImmutableMap.of("Code", "AAA_SS10260110", "Message", "NANO policy cannot have more than 2 insureds, a Named Insured and Spouse"));
        errorTab.tableErrors.getRowContains("Code", "AAA_SS10260110").getCell("Code").controls.links.getFirst().click();
        assertThat(DriverTab.tableDriverList).isPresent();

        policy.getDefaultView().fill(getTestSpecificTD("TestData_TC19").getTestData("Policy2"));
    }

    /**
     * @author Maksim Piatrouski
     * @name CO_SC1_TC20
     */
    @Parameters({"state"})
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC20(@Optional("") String state) {
        mainApp().open();
        SearchPage.openPolicy(getPolicyNumber());

        policy.doNotRenew().start();
        doNotRenewActionTab.fillTab(getTestSpecificTD("TestData_TC20"));
        assertSoftly(softly -> {
            softly.assertThat(DoNotRenewActionTab.tableDriverActivities.getHeader().getValue()).
                    contains("Driver Name", "Accident/Violation Date", "Accident/Violation Description", "Acc/Loss Payment", "Source");
            softly.assertThat(DoNotRenewActionTab.underwritingGuidelines).isPresent();
        });

        //#TODO: AJAX Loading error
        Tab.buttonOk.click(Waiters.AJAX(60 * 1000));
        assertThat(errorTab.tableErrors).hasMatchingRows(1, ImmutableMap.of("Message", "Please select the Driver Activity resulting in non-renewal of the policy."));

        DoNotRenewActionTab.tableDriverActivities.getRow(1).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        Tab.buttonOk.click();
    }

    private void preconditions(NavigationEnum.AutoSSTab navigateTo) {
        mainApp().open();
        String quote = getQuoteNumber();
        MainPage.QuickSearch.buttonSearchPlus.click();
        if (Page.dialogConfirmation.isPresent()) { //happens if previous test in queue was failed
            Page.dialogConfirmation.confirm();
        }
        SearchPage.openQuote(quote);
        policy.dataGather().start();
        NavigationPage.toViewTab(navigateTo.get());
    }

    private String getQuoteNumber() {
        if (quoteNumber == null) {
            mainApp().open();
            createCustomerIndividual();
            policy.initiate();
            policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData"), GeneralTab.class, true);
            Tab.buttonSaveAndExit.click();
            quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
            log.info("DELTA CO SC1: ASS Quote created with #" + quoteNumber);
        }
        return quoteNumber;
    }

    private String getPolicyNumber() {
        if (policyNumber == null) {
            mainApp().open();
            createCustomerIndividual();
            createPolicy();
            policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
            log.info("DELTA CO SC1: ASS Quote created with #" + quoteNumber);
        }
        return policyNumber;
    }

    private void overrideAllErrors() {
        for (Row row : errorTab.tableErrors.getRows()) {
            row.getCell("Override").controls.checkBoxes.getFirst().setValue(true);
            row.getCell("Duration").controls.radioGroups.getFirst().setValue("Life");
            row.getCell("Reason for override").controls.comboBoxes.getFirst().setValueByIndex(0);
        }
        errorTab.buttonOverride.click();
    }
}