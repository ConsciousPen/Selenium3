package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import aaa.main.pages.summary.PolicySummaryPage;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;

public class TestEUIMCoverageBehavior extends AutoSSBaseTest {

    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
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
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorNB(@Optional("MD") String state) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JULY, 1, 0, 0));

        // Initiate Policy, calculate premium
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumAndCoveragesTab.class, true);

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();
    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter, Sreekanth Kopparapu
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Endorsements
     *@scenario
     * 1. Create Customer
     * 2. Create Auto SS MD Policy after 07/01/2018
     * 3. Endorse Policy and Navigate to P&C View Rating Details
     * 4. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage()
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorEndorsement(@Optional("MD") String state) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JULY, 1, 0, 0));

        // Initiate Policy, calculate premium
        mainApp().open();
        getCopiedPolicy();

        // Initiate Mid-Term Endorsement and Navigate to P&C Page.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();
    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter, Sreekanth Kopparapu
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Renewals
     *@scenario
     * 1. Create Customer
     * 2. Create Auto SS MD Policy after 07/01/2018
     * 3. Initiate Renewal
     * 4. Navigate to P&C
     * 5. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage()
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorRenewal(@Optional("MD") String state) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JULY, 1, 0, 0));

        // Create customer & policy
        mainApp().open();
        getCopiedPolicy();

        // Initiate Renewal
        policy.renew().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();
    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter, Sreekanth Kopparapu
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Conversions
     *@scenario
     * 1. Create Customer
     * 2. Initiate Auto SS MD Conversion Policy after 07/01/2018
     * 3. Fill up to P&C Tab
     * 4. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage()
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-11620, PAS-11204, PAS-11448, PAS-11209")
    public void pas11620_testEUIMCoverageBehaviorConversion(@Optional("MD") String state) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(LocalDateTime.of(2018, Month.JULY, 1, 0, 0));

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Conversion and fill up to P & C Tab
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(getConversionPolicyDefaultTD(), PremiumAndCoveragesTab.class);

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();
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
        bodilyInjury.setValueByIndex(4);
        assertThat(uninsuredBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());

        //AC2 PAS-11620. Rating Error if EUIM BI limits do not match BI limits.
        uninsuredBodilyInjury.setValueByIndex(1);
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

        //PAS-11448. Validate Help text when moused over EUIM, EUIMPD and EUIMBI
        String euimExpectedText = "Allows the insured to collect up to the limits of their coverage regardless of how much is recovered from the at-fault third party. "
                + "Insured has the option to choose EUIM coverage in place of UM coverage. Coverage applies to both EUIMBI and EUIMPD.";
        String euimBIExpectedText = "Pays up to the specified limit if the insured, resident family members, or occupants of an insured vehicle are injured in an accident and the driver "
                + "who is legally liable does not have insurance or has insufficient limits of liability insurance. Please see policy contract and EUIM endorsement.";
        String euimPDExpectedText = "Pays up to the specified limit the insured is legally entitled to recover or waives the collision deductible to cover damages resulting from an accident "
                + "with an uninsured motor vehicle. Please see policy contract and EUIM endorsement.";
        assertThat(PremiumAndCoveragesTab.enhancedUIMHelpText.getAttribute("innerText")).contains(euimExpectedText);
        assertThat(PremiumAndCoveragesTab.enhancedUIMBIHelpText.getAttribute("innerText")).contains(euimBIExpectedText);
        assertThat(PremiumAndCoveragesTab.enhancedUIMPDHelpText.getAttribute("innerText")).contains(euimPDExpectedText);

        //PAS-11204. Display EUIM UIPD/UIMBI in 'Total Term Premium' section P&C Page.
        List<TestData> totalTermPremiumTD = premiumAndCoveragesTab.getTermPremiumByVehicleData();
        assertThat(totalTermPremiumTD.get(0).getKeys()).contains("Enhanced Uninsured/Underinsured Motorist Bodily Injury");
        assertThat(totalTermPremiumTD.get(0).getKeys()).contains("Enhanced Uninsured Motorist Property Damage");

        // AC1 PAS-11209. Display EUIM UIPD/UIMBI in VRD page.
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        List<TestData> vehicleVRDTestData = premiumAndCoveragesTab.getRatingDetailsVehiclesData();
        assertThat(vehicleVRDTestData.get(0).getKeys()).contains("Enhanced Uninsured/Underinsured Motorist Bodily Injury");
        assertThat(vehicleVRDTestData.get(0).getKeys()).contains("Enhanced Uninsured Motorist Property Damage");
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        PremiumAndCoveragesTab.buttonSaveAndExit.click();

        // AC2 PAS-11209. Display EUIM UIPD/UIMBI in Policy Consolidated view Coverages section.
        assertThat(PolicySummaryPage.getAutoCoveragesSummaryTextAt(2,2)).isEqualTo("Bodily Injury Liability");
        assertThat(PolicySummaryPage.getAutoCoveragesSummaryTextAt(3,2)).isEqualTo("Property Damage Liability");
        assertThat(PolicySummaryPage.getAutoCoveragesSummaryTextAt(4,2)).isEqualTo("Enhanced Uninsured/Underinsured Motorist Bodily Injury");
        assertThat(PolicySummaryPage.getAutoCoveragesSummaryTextAt(5,2)).isEqualTo("Enhanced Uninsured Motorist Property Damage");
    }
}
