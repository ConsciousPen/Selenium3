package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;

@StateList(states = Constants.States.MD)
public class TestEUIMCoverageBehavior extends AutoSSBaseTest {

    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private ErrorTab errorTab = new ErrorTab();

    private ComboBox bodilyInjury = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY);
    private ComboBox propertyDamage = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY);
    private CheckBox enhancedUIM = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UIM);
    private ComboBox uninsuredBodilyInjury = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY);
    private ComboBox uninsuredPropertyDamage = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE);

    /**
     *@author Dominykas Razgunas, Josh Carpenter, Sreekanth Kopparapu
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for NB
     *@scenario
     * 1. Create Customer
     * 2. Initiate Auto SS MD Quote after 07/01/2018
     * 3. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage()
     * 4. Save and Exit
     * 5. Verify That Enhanced UIM in Coverages section on Policy Consolidated View is set to Yes
     * 6. Initiate Data gather for the policy
     * 7. Change EUIM to false
     * 8. Calculate Premium
     * 9. Issue Policy
     * 10. Verify That Enhanced UIM in Coverages section on Policy Consolidated View is set to No
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorNB(@Optional("MD") String state) {

        // Initiate Policy, calculate premium
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumAndCoveragesTab.class, true);

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();
        premiumAndCoveragesTab.saveAndExit();

        // AC2 PAS-11209. Display EUIM UIPD/UIMBI in Policy Consolidated view Coverages section.
        verifyPolicySummaryPage("Yes");

        // Issue Policy. Change EUIM to false.
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        enhancedUIM.setValue(false);
        new PremiumAndCoveragesTab().calculatePremium();
        new PremiumAndCoveragesTab().submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), DriverActivityReportsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        // AC2 PAS-11209. Display EUIM UIPD/UIMBI in Policy Consolidated view Coverages section.
        verifyPolicySummaryPage("No");
    }


    /**
     *@author Dominykas Razgunas
     *@name MD Auto Total premium difference for enhanced and standard UIM NB,Endorsement, Renewal
     *@scenario
     * 1. Create Customer
     * 2. Initiate Auto SS MD Quote after 07/01/2018
     * 3. Calculate Premium with standard UIM
     * 4. Save Total Premium1 Value
     * 5. Calculate Total Premium2 with enhanced UIM
     * 6. Check That Total Premium1 < Total Premium2
     * 7. Issue Policy with standard UIM
     * 8. Endorse Policy
     * 9. Calculate Total Premium2 with enhanced UIM
     * 10. Check That Total Premium1 < Total Premium2
     * 11. Save and exit Endorsement
     * 12. Initiate Manual Renewal
     * 13. Navigate to P&C tab
     * 14. Calculate Premium with standard UIM
     * 15. Save Total Premium3 Value
     * 16. Calculate Total Premium4 with enhanced UIM
     * 17. Check That Total Premium3 < Total Premium4
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11620")
    public void pas11620_PremiumChangeBetweenEnhancedAndStandardUIM(@Optional("MD") String state) {

        // Initiate Policy, calculate premium
        createQuoteAndFillUpTo(getPolicyTD(), PremiumAndCoveragesTab.class);

        // Save Standard UIM Total Premium value NB
        Dollar standardUIMNBvalue = new Dollar(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getValue("Total Vehicle Term Premium"));

        // Assert that Enhanced UIM Total Premium > Standard UIM Total Premium during New Business
        enhancedUIM.setValue(true);
        new PremiumAndCoveragesTab().calculatePremium();
        Dollar enhancedUIMNBvalue = new Dollar(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getValue("Total Vehicle Term Premium"));
        assertThat(standardUIMNBvalue.lessThan(enhancedUIMNBvalue)).as(standardUIMNBvalue + "Should be less than" + enhancedUIMNBvalue).isTrue();

        // Issue Policy. Change EUIM to false.
        enhancedUIM.setValue(false);
        new PremiumAndCoveragesTab().calculatePremium();
        new PremiumAndCoveragesTab().submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), DriverActivityReportsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        setDoNotRenewFlag(PolicySummaryPage.getPolicyNumber());

        // Initiate Mid-Term Endorsement and Navigate to P&C Page.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        // Assert that Enhanced UIM Total Premium > Standard UIM Total Premium during Endorsement
        enhancedUIM.setValue(true);
        new PremiumAndCoveragesTab().calculatePremium();
        Dollar enhancedUIMNBvalue1 = new Dollar(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getValue("Total Vehicle Term Premium"));
        assertThat(standardUIMNBvalue.lessThan(enhancedUIMNBvalue1)).as(standardUIMNBvalue + "Should be less than" + enhancedUIMNBvalue1).isTrue();

        // Initiate Renewal navigate to P&C and calculate premium
        premiumAndCoveragesTab.saveAndExit();
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime expDate = PolicySummaryPage.getExpirationDate();
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(expDate));
        mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.removeDoNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		policy.renew().start();
        new PremiumAndCoveragesTab().calculatePremium();

        // Save Standard UIM Total Premium value
        Dollar standardUIMRvalue = new Dollar(new PremiumAndCoveragesTab().getTermPremiumByVehicleData().get(0).getValue("Total Vehicle Term Premium"));

        // Assert that Enhanced UIM Total Premium > Standard UIM Total Premium during Renewal
        enhancedUIM.setValue(true);
        new PremiumAndCoveragesTab().calculatePremium();
        Dollar enhancedUIMRvalue = new Dollar(new PremiumAndCoveragesTab().getTermPremiumByVehicleData().get(0).getValue("Total Vehicle Term Premium"));
        assertThat(standardUIMRvalue.lessThan(enhancedUIMRvalue)).as(standardUIMRvalue + "Should be less than" + enhancedUIMRvalue).isTrue();
    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter, Sreekanth Kopparapu
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Endorsements
     *@scenario
     * 1. Create Customer
     * 2. Create Auto SS MD Policy after 07/01/2018
     * 3. Endorse Policy and Navigate to P&C View Rating Details
     * 4. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage()
     * 5. Issue Policy.
     * 6. Verify That Enhanced UIM in Coverages section on Policy Consolidated View is set to Yes.
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorEndorsement(@Optional("MD") String state) {

        // Initiate Policy, calculate premium
        mainApp().open();
        getCopiedPolicy();

        // Initiate Mid-Term Endorsement and Navigate to P&C Page.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();

        // Issue Policy.
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        // AC2 PAS-11209. Display EUIM UIPD/UIMBI in Policy Consolidated view Coverages section.
        verifyPolicySummaryPage("Yes");
    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter, Sreekanth Kopparapu
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Renewals
     *@scenario
     * 1. Create Customer
     * 2. Create Auto SS MD Policy after 07/01/2018
     * 3. Verify That Enhanced UIM in Coverages section on Policy Consolidated View is set to No
     * 4. Initiate Renewal
     * 5. Navigate to P&C
     * 6. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage()
     * 7. Purchase Renewal
     * 8. Navigate to renewed policy
     * 9. Verify That Enhanced UIM in Coverages section on Policy Consolidated View is set to Yes.
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorRenewal(@Optional("MD") String state) {

        // Create customer & policy
        mainApp().open();
        getCopiedPolicy();
        String policyNum = PolicySummaryPage.getPolicyNumber();
        LocalDateTime expDate = PolicySummaryPage.getExpirationDate();
		mainApp().close();
        setDoNotRenewFlag(policyNum);

        // Change Date to policies renewals proposal date
        TimeSetterUtil.getInstance().nextPhase(expDate);

        // open app search for policy
        mainApp().open();
        SearchPage.openPolicy(policyNum);

        // AC2 PAS-11209. Display EUIM UIPD/UIMBI in Policy Consolidated view Coverages section.
        verifyPolicySummaryPage("No");

        // Initiate Renewal
		policy.removeDoNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
        policy.renew().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();

        // Issue Policy.
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        payTotalAmtDue(policyNum);

        // Navigate to Renewal
        PolicySummaryPage.buttonRenewals.click();

        // AC2 PAS-11209. Display EUIM UIPD/UIMBI in Policy Consolidated view Coverages section.
        verifyPolicySummaryPage("Yes");
    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter, Sreekanth Kopparapu
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Conversions
     *@scenario
     * 1. Create Customer
     * 2. Initiate Auto SS MD Conversion Policy after 07/01/2018
     * 3. Fill up to P&C Tab
     * 4. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage()
     * 5. Issue Policy
     * 6. Purchase conversion policy
     * 7. Verify That Enhanced UIM in Coverages section on Policy Consolidated View is set to Yes.
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorConversion(@Optional("MD") String state) {

        String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        TestData tdManualConversionInitiation = getManualConversionInitiationTd().adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
                CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), today);

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Conversion and fill up to P & C Tab
        customer.initiateRenewalEntry().perform(tdManualConversionInitiation);
        policy.getDefaultView().fillUpTo(getConversionPolicyDefaultTD(), PremiumAndCoveragesTab.class);

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();

        // Issue Policy
        new PremiumAndCoveragesTab().submitTab();
        policy.getDefaultView().fillFromTo(getConversionPolicyDefaultTD(), DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
        new DocumentsAndBindTab().submitTab();
        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_CSACN0100);
            errorTab.override();
            new DocumentsAndBindTab().submitTab();
        }
        PolicySummaryPage.buttonBackFromRenewals.click();
        String policyNum = PolicySummaryPage.getPolicyNumber();
        payTotalAmtDue(policyNum);

        // AC2 PAS-11209. Display EUIM UIPD/UIMBI in Policy Consolidated view Coverages section.
        verifyPolicySummaryPage("Yes");
    }

    private void verifyEnhancedUIMCoverage() {

        // Prepare to check AC1 PAS-11620
        propertyDamage.setValueByIndex(0);
        bodilyInjury.setValueByIndex(0);
        uninsuredBodilyInjury.setValueByIndex(1);
        uninsuredPropertyDamage.setValueByIndex(1);

        //AC1 PAS-11620. Quotes and Policies created on or after 2018/07/01 EUIM should be present and EUIM BI and PD limits are defaulted to BI/PD limits
        enhancedUIM.setValue(true);
        assertThat(uninsuredPropertyDamage.getValue()).isEqualTo(propertyDamage.getValue());
        assertThat(uninsuredBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());

        //AC1 PAS-11620. Changing BI/PD limits also changes EUIM BI/PD.
        propertyDamage.setValueByIndex(2);
        assertThat(uninsuredPropertyDamage.getValue()).isEqualTo(propertyDamage.getValue());
        bodilyInjury.setValueByIndex(2);
        assertThat(uninsuredBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());
        propertyDamage.setValueByIndex(4);
        assertThat(uninsuredPropertyDamage.getValue()).isEqualTo(propertyDamage.getValue());
        bodilyInjury.setValueContains("$500,000/$500,000");
        assertThat(uninsuredBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());

        //AC2 PAS-11620. Rating Error if EUIM BI limits do not match BI limits.
        uninsuredBodilyInjury.setValueContains("$500,000/$1,000,000");
        premiumAndCoveragesTab.calculatePremium();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS41800882_MD);
        errorTab.cancel();

        //AC2 PAS-11620. Rating Error if EUIM PD limits do not match PD limits.
        bodilyInjury.setValueByIndex(3);
        uninsuredPropertyDamage.setValueByIndex(1);
        premiumAndCoveragesTab.calculatePremium();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS41800881_MD);
        errorTab.cancel();

        //AC2 PAS-11620. Rating Error if EUIM BI/PD limits do not match BI/PD limits.
        uninsuredBodilyInjury.setValueByIndex(1);
        premiumAndCoveragesTab.calculatePremium();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS41800881_MD);
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS41800882_MD);
        errorTab.cancel();

        //AC3 PAS-11620. Switching between Standard and Enhanced UIM sets Vehicle and Policy Level Liability Coverages Premium to 0.
        propertyDamage.setValueByIndex(3);
        bodilyInjury.setValueByIndex(2);
        premiumAndCoveragesTab.calculatePremium();
        enhancedUIM.setValue(false);
        new Dollar(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getValue("Total Vehicle Term Premium")).verify.zero();
        premiumAndCoveragesTab.calculatePremium();
        enhancedUIM.setValue(true);
        new Dollar(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getValue("Total Vehicle Term Premium")).verify.zero();
        premiumAndCoveragesTab.calculatePremium();

        //PAS-11448. Validate Help text when moused over EUIM, UIMPD, and UIMBI
        String euimHelpText = "Allows the insured to collect up to the limits of their coverage regardless of how much is recovered from the at-fault third party. "
                + "Insured has the option to choose EUIM coverage in place of UM coverage. Coverage applies to both EUIMBI and EUIMPD.";
        String euimBIHelpText = "Pays up to the specified limit if the insured, resident family members, or occupants of an insured vehicle are injured in an accident and the driver "
                + "who is legally liable does not have insurance or has insufficient limits of liability insurance. Please see policy contract and EUIM endorsement.";
        String euimPDHelpText = "Pays up to the specified limit the insured is legally entitled to recover or waives the collision deductible to cover damages resulting from an accident "
                + "with an uninsured motor vehicle. Please see policy contract and EUIM endorsement.";
        String uimBIHelpText = "Pays up to the specified limit if the insured, resident family members, or occupants of an insured vehicle are injured or killed in an accident in which the owner "
                + "or operator of a motor vehicle who is legally liable does not have liability insurance. Please see policy contract.";
        String uimPDHelpText = "Pays up to the limit the insured is legally entitled to recover or waives the collision deductible, to cover damages resulting from an accident with an "
                + "uninsured motor vehicle. Please see policy contract.";

        assertThat(PremiumAndCoveragesTab.euimHelpText.getAttribute("innerText")).contains(euimHelpText);
        assertThat(PremiumAndCoveragesTab.uimBIHelpText.getAttribute("innerText")).contains(euimBIHelpText);
        assertThat(PremiumAndCoveragesTab.uimPDHelpText.getAttribute("innerText")).contains(euimPDHelpText);

        enhancedUIM.setValue(false);
        premiumAndCoveragesTab.calculatePremium();
        assertThat(PremiumAndCoveragesTab.euimHelpText.getAttribute("innerText")).contains(euimHelpText);
        assertThat(PremiumAndCoveragesTab.uimBIHelpText.getAttribute("innerText")).contains(uimBIHelpText);
        assertThat(PremiumAndCoveragesTab.uimPDHelpText.getAttribute("innerText")).contains(uimPDHelpText);

        // AC1 PAS-11209. Display EUIM UIMPD/UIMBI in VRD page.
        verifyUIMVRD("No");

        //PAS-11204. Display 'Enhanced UIM Selected' in 'Total Term Premium' section P&C Page.
        String euimSelectedText = "Enhanced UIM";
        assertThat(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getKeys()).doesNotContain(euimSelectedText);
        enhancedUIM.setValue(true);
        premiumAndCoveragesTab.calculatePremium();
        assertThat(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getKeys()).contains(euimSelectedText);

        //Verify the next 2 lines in the Total Term Premium are UIM/BI and PD
        List<String> totalTermPremiumKeys = new ArrayList<>(premiumAndCoveragesTab.getTermPremiumByVehicleData().get(0).getKeys());
        int euimIndex = IntStream.range(0, totalTermPremiumKeys.size() - 1).filter(i -> totalTermPremiumKeys.get(i).equals(euimSelectedText)).findFirst().orElse(-3);
        assertThat(totalTermPremiumKeys.get(euimIndex + 1)).isEqualTo("Uninsured/Underinsured Motorist Bodily Injury");
        assertThat(totalTermPremiumKeys.get(euimIndex + 2)).isEqualTo("Uninsured Motorist Property Damage");

        // PAS-11209. Display EUIM UIMPD/UIMBI on VRD and Policy Consolidated view Coverages section
        verifyUIMVRD("Yes");
    }

    private void verifyUIMVRD(String value) {
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        String euim = "Enhanced UIM";
        List<TestData> vrdData = premiumAndCoveragesTab.getRatingDetailsVehiclesData();
        if (!vrdData.get(0).getValue(euim).equals(value)) {
            assertThat(vrdData.get(1).getValue(euim)).isEqualTo(value);
        }
        List<String> vrdKeys = new ArrayList<>(vrdData.get(0).getKeys());
        int euimIndex = IntStream.range(0, vrdKeys.size() - 1).filter(i -> vrdKeys.get(i).equals(euim)).findFirst().orElse(-3);
        assertThat(vrdKeys.get(euimIndex + 1)).isEqualTo("Uninsured Motorist/Underinsured Motorist");
        assertThat(vrdKeys.get(euimIndex + 2)).isEqualTo("Uninsured Motorist Property Damage Limit");
        PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk.click();
    }

    private void verifyPolicySummaryPage(String value) {
        String euim = "Enhanced UIM";
        String firstVehicle = PolicySummaryPage.getAutoCoveragesSummaryTextAt(1, 1);
        TestData coveragesSummary = PolicySummaryPage.getAutoCoveragesSummaryTestData();
        assertThat(coveragesSummary.getTestData(firstVehicle).getTestData(euim).getValue("Limit")).isEqualTo(value);
        List<String> summaryKeys = new ArrayList<>(coveragesSummary.getTestData(firstVehicle).getKeys());
        int euimIndex = IntStream.range(0, summaryKeys.size() - 1).filter(i -> summaryKeys.get(i).equals(euim)).findFirst().orElse(-3);
        assertThat(summaryKeys.get(euimIndex + 1)).isEqualTo("Uninsured/Underinsured Motorist Bodily Injury");
        assertThat(summaryKeys.get(euimIndex + 2)).isEqualTo("Uninsured Motorist Property Damage");
    }
}