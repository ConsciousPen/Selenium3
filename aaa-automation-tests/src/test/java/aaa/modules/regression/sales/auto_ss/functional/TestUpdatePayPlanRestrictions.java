package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestUpdatePayPlanRestrictions extends AutoSSBaseTest {

    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

    String disableEvaluePriorBiLimit = "$25,000/$50,000";
    String enableEvaluePriorBiLimit = "$100,000/$300,000";

    List<String> expectedValuesOfPaymentPlanForAnnual = Arrays.asList("Annual", "Monthly");
    List<String> expectedValuesOfPaymentPlanForSemiAnnual = Arrays.asList("Semi-Annual");

    List<String> expectedValuesOfUnrestrictedPaymentPlanForAnnual = Arrays.asList(
            "Annual", "Monthly - Zero Down",
            "Monthly - Low Down",
            "Semi-Annual",
            "Quarterly",
            "Eleven Pay - Standard",
            "Eleven Pay - Zero Down",
            "Eleven Pay - Low Down");

    List<String> expectedValuesOfUnrestrictedPaymentPlanForSemiAnnual = Arrays.asList(
            "Semi-Annual",
            "Quarterly",
            "Five Pay - Zero Down",
            "Five Pay - Low Down",
            "Five Pay - Standard",
            "Monthly - Zero Down",
            "Monthly - Low Down");

    /**
     * @author Sarunas Jaraminas
     * @name Update Pay Plan Restrictions for DE based on BI/Prior BI Selections
     * @scenario
     *1. Create customer.
     *2. Create Auto SS Quote.
     *3. "Override Prefilled Current Carrier?" = YES
     *4. "Agent Entered BI Limits" = "$25,000/$50,000"
     *5. Verify existence of Policy Terms and Payment Plan options.
     *6. Checks if "Monthly" pay plan calculates a deposit amount
     * as configured in the "Pay Plan Restriction for States Control Table" (50%)
     *
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12476")
    public void pas12476_TestUpdatePayPlanRestrictionsWhenCarrierIsYes(@Optional("DE") String state) {

        mainApp().open();
        createCustomerIndividual();
        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(),
                AutoSSMetaData.VehicleTab.VIN.getLabel()), "JT2AE91A7M3425407")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel()),
                        disableEvaluePriorBiLimit);

        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, false);

        checkExpectedPaymentPlans();
        setMonthlyPaymentPlan();
        clickPaymentPlanLink();
        verifyMonthlyPaymentPlanFigures();
    }

    /**
     * @author Sarunas Jaraminas
     * @name Update Pay Plan Restrictions for DE based on BI/Prior BI Selections
     * @scenario
     *1. Create customer.
     *2. Create Auto SS Quote.
     *3. "Override Prefilled Current Carrier?" = NO
     *4. "Agent Entered BI Limits" = "$25,000/$50,000"
     *5. Verify existence of Policy Terms and Payment Plan options.
     *6. Checks if "Monthly" pay plan calculates a deposit amount
     * as configured in the "Pay Plan Restriction for States Control Table" (50%)
     *
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12476")
    public void pas12476_TestUpdatePayPlanRestrictionsWhenCarrierIsNO(@Optional("DE") String state){

        mainApp().open();
        createCustomerIndividual();
        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PrefillTab.class.getSimpleName(),
                        AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel()), "Bill")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PrefillTab.class.getSimpleName(),
                        AutoSSMetaData.PrefillTab.LAST_NAME.getLabel()), "Johns")
                .adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(),
                        AutoSSMetaData.VehicleTab.VIN.getLabel()), "JT2AE91A7M3425407")
                .mask(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()));

        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, false);

        checkExpectedPaymentPlans();
        setMonthlyPaymentPlan();
        clickPaymentPlanLink();
        verifyMonthlyPaymentPlanFigures();
    }

    /**
     * @author Sarunas Jaraminas
     * @name Update Pay Plan Restrictions for DE based on BI/Prior BI Selections
     * @scenario
     *1. Create customer.
     *2. Create Auto SS Quote.
     *3. "Override Prefilled Current Carrier?" = YES
     *4. "Agent Entered BI Limits" = "$100,000/$300,000"
     *5. Verify existence of Policy Terms and Payment Plan options.
     *
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-12476")
    public void pas12476_TestUpdatePayPlanRestrictionsWhenBiLimitIsMoreThen50K(@Optional("DE") String state) {

        mainApp().open();
        createCustomerIndividual();
        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(),
                        AutoSSMetaData.VehicleTab.VIN.getLabel()), "JT2AE91A7M3425407")
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel()),
                        enableEvaluePriorBiLimit);

        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, false);

        checkExpectedUnrestrictedPaymentPlans();
    }


    private void checkExpectedPaymentPlans() {

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_TERM)
                .setValue("Semi-annual");
        assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN)
                .getAllValues().containsAll(expectedValuesOfPaymentPlanForSemiAnnual)).isTrue();

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_TERM)
                .setValue("Annual");
        assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN)
                .getAllValues().containsAll(expectedValuesOfPaymentPlanForAnnual)).isTrue();
    }

    private void checkExpectedUnrestrictedPaymentPlans() {

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_TERM)
                .setValue("Semi-annual");
        assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN)
                .getAllValues().containsAll(expectedValuesOfUnrestrictedPaymentPlanForSemiAnnual)).isTrue();

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_TERM)
                .setValue("Annual");
        assertThat(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN)
                .getAllValues().containsAll(expectedValuesOfUnrestrictedPaymentPlanForAnnual)).isTrue();
    }

    private void setMonthlyPaymentPlan() {

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN)
                .setValue("Monthly");
        premiumAndCoveragesTab.calculatePremium();
    }

    private void clickPaymentPlanLink() {
		PremiumAndCoveragesTab.linkPaymentPlan.click();
    }

    private void verifyMonthlyPaymentPlanFigures() {

        Dollar premium = new Dollar(PremiumAndCoveragesTab.tablePaymentPlans.getRow(1)
                .getCell(PolicyConstants.PolicyPaymentPlanTable.PREMIUM).getValue());

        Dollar minDownPayment = premium.multiply(0.50);

        new Dollar(PremiumAndCoveragesTab.tablePaymentPlans.getRow(1)
                .getCell(PolicyConstants.PolicyPaymentPlanTable.MINIMUM_DOWN_PAYMENT).getValue())
                .verify.equals(minDownPayment);
    }
}