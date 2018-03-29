package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
    private ComboBox enhancedBodilyInjury = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY);
    private ComboBox enhancedPropertyDamage = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UNINSURED_MOTORIST_PROPERTY_DAMAGE);


    /**
     *@author Dominykas Razgunas, Josh Carpenter
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for NB
     *@scenario
     * 1. Create Customer.
     * 2. Initiate Auto SS MD Quote after 07/01/2018.
     * 3. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage().
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11200, PAS-11620, PAS-11204, PAS-11448")
    public void pas11200_testEUIMCoverageBehaviorNB(@Optional("MD") String state) {

        verifyAlgoDate();

        // Initiate Policy, calculate premium
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumAndCoveragesTab.class, true);

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();
    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Endorsements
     *@scenario
     * 1. Create Customer
     * 2. Create Auto SS MD Policy after 07/01/2018.
     * 3. Endorse Policy and Navigate to P&C View Rating Details.
     * 4. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage().
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11200, PAS-11620, PAS-11204, PAS-11448")
    public void pas11200_testEUIMCoverageBehaviorEndorsement(@Optional("MD") String state) {

        verifyAlgoDate();

        // Initiate Policy, calculate premium
        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        // Initiate Mid-Term Endorsement and Navigate to P&C Page.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.calculatePremium();

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();

    }

    /**
     *@author Dominykas Razgunas, Josh Carpenter
     *@name MD Auto Enhanced Uninsured/Underinsured Coverage Behavior for Renewals
     *@scenario
     * 1. Create Customer.
     * 2. Create Auto SS MD Policy after 07/01/2018.
     * 3. Initiate Renewal.
     * 4. Navigate to P&C.
     * 5. Verify all conditions in Verify Behavior of EUIM/BI and EUIM/PD fields - verifyEnhancedUIMCoverage().
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11200, PAS-11620, PAS-11204, PAS-11448")
    public void pas11200_testEUIMCoverageBehaviorRenewal(@Optional("MD") String state) {

        verifyAlgoDate();

        // Create customer & policy
        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        // Initiate Renewal
        policy.renew().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.calculatePremium();

        // Verify Behavior of EUIM/BI and EUIM/PD fields
        verifyEnhancedUIMCoverage();

    }

    //TODO remove verify algo date after 2018-07-01
    private void verifyAlgoDate() {
        LocalDateTime algoEffectiveDate = LocalDateTime.of(2018, Month.JULY, 1, 0, 0);
        if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(algoEffectiveDate)) {
            TimeSetterUtil.getInstance().nextPhase(algoEffectiveDate);
        }
    }

    private void verifyEnhancedUIMCoverage() {
        propertyDamage.setValueByIndex(1);
        bodilyInjury.setValueByIndex(1);

        //AC1,AC2 PAS-11200. for Quotes and Policies created on or after 2018/07/01 EUIM should be present.
        //AC1 PAS-11620. EUIM BI and PD limits are defaulted to BI/PD limits.
        enhancedUIM.setValue(true);
        assertThat(enhancedPropertyDamage.getValue()).isEqualTo(propertyDamage.getValue());
        assertThat(enhancedBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());

        //AC2 PAS-11620. Changing BI/PD limits also changes EUIM BI/PD.
        propertyDamage.setValueByIndex(2);
        assertThat(enhancedPropertyDamage.getValue()).isEqualTo(propertyDamage.getValue());
        bodilyInjury.setValueByIndex(2);
        assertThat(enhancedBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());

        //AC3 PAS-11620. Rating Error if EUIM BI/PD limits do not match BI/PD limits.
        enhancedBodilyInjury.setValueByIndex(1);
        PremiumAndCoveragesTab.calculatePremium();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS11111);
        errorTab.submitTab();

        //AC2 PAS-11620. Changing BI/PD limits also changes EUIM BI/PD.
        bodilyInjury.setValueByIndex(3);
        assertThat(enhancedBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());

        //AC3 PAS-11620. Rating Error if EUIM BI/PD limits do not match BI/PD limits.
        enhancedPropertyDamage.setValueByIndex(1);
        PremiumAndCoveragesTab.calculatePremium();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS22222);
        errorTab.submitTab();
        enhancedBodilyInjury.setValueByIndex(1);
        PremiumAndCoveragesTab.calculatePremium();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS11111);
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS22222);
        errorTab.submitTab();

        //AC2 PAS-11620. Changing BI/PD limits also changes EUIM BI/PD.
        propertyDamage.setValueByIndex(3);
        assertThat(enhancedPropertyDamage.getValue()).isEqualTo(propertyDamage.getValue());
        bodilyInjury.setValueByIndex(2);
        assertThat(enhancedBodilyInjury.getValue()).isEqualTo(bodilyInjury.getValue());

        //AC4 PAS-11620. Switching between Standard and Enhanced UIM sets Vehicle and Policy Level Liability Coverages Premium to 0.
        PremiumAndCoveragesTab.calculatePremium();
        enhancedUIM.setValue(false);
        assertThat(premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium()).isEqualTo("$0.00");
        assertThat(premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle(1)).isEqualTo("$0.00");
        PremiumAndCoveragesTab.calculatePremium();
        enhancedUIM.setValue(true);
        assertThat(premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium()).isEqualTo("$0.00");
        assertThat(premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle(1)).isEqualTo("$0.00");
        PremiumAndCoveragesTab.calculatePremium();

        //PAS-11448
        String euimExpectedText = "Allows the insured to collect up to the limits of their coverage regardless of how much is recovered from the at-fault third party. "
                + "Insured has the option to choose EUIM coverage in place of UM coverage.  Coverage applies to both EUIMBI and EUIMPD.";
        String euimbiExpectedText = "Pays up to the specified limit if the insured, resident family members, or occupants of an insured vehicle are injured in an accident and the driver "
                + "who is legally liable does not have insurance or has insufficient limits of liability insurance. Please see policy contract and EUIM endorsement.";
        String euimpdExpectedText = "Pays up to the specified limit the insured is legally entitled to recover or waives the collision deductible to cover damages resulting from an accident "
                + "with an uninsured motor vehicle. Please see policy contract and EUIM endorsement.";
        assertThat(PremiumAndCoveragesTab.enhancedUIMHelpText.getAttribute("title")).isEqualTo(euimExpectedText);
        assertThat(PremiumAndCoveragesTab.enhancedUIMBIHelpText.getAttribute("title")).isEqualTo(euimbiExpectedText);
        assertThat(PremiumAndCoveragesTab.enhancedUIMPDHelpText.getAttribute("title")).isEqualTo(euimpdExpectedText);
    }

}
