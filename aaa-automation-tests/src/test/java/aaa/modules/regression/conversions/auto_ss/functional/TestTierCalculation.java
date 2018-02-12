package aaa.modules.regression.conversions.auto_ss.functional;

import static aaa.main.metadata.BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT;
import static aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.AGREEMENT;
import static aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LICENSE_STATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.POLICY_INFORMATION;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.LEAD_SOURCE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN;
import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.buttonViewRatingDetails;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.auto_ss.AutoSSPolicyActions;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.conversions.ConvAutoSsBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public class TestTierCalculation extends ConvAutoSsBaseTest {
    private final Tab generalTab = new GeneralTab();
    private final Tab driverReportTab = new DriverActivityReportsTab();
    private final Tab premiumCovTab = new PremiumAndCoveragesTab();
    private final DocumentsAndBindTab documentsTab = new DocumentsAndBindTab();
    private String policyNumberNb;
    private String policyNumberConv;
    private Dollar premiumValue;

    /**
     * * @author Igor Garkusha
     *
     *@name Test Paperless Preferences properties and Inquiry mode
     *@scenario
     *1. Create two customers .
     *2. Create Auto SS NY Quote.
     *3. Open customer #2.
     *4. Select “initiateRenewalEntryEvent”.
     *5. Fill up "Initiate Renewal Entry" and set Policy Term to “Annual”.
     *6. Submit tab.
     *7. Fill up the same policy data as on first policy.
     *8. Calculate premium.
     *8. Click on View Premium Details.
     *9. Navigate to P&C Tab.
     *10.Compare Tier factors and Tier with new Business Policy.
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4145")
    public void pas4145_calculateTierNyConversionCheckNB(@Optional("NY") String state) {
        //Prepare TD
        TestData policyData = DataProviderFactory.emptyData().adjust(getPolicyTD().resolveLinks());
        policyData.adjust(generalTab.getMetaKey(), getTestSpecificTD("TestData").getTestData(generalTab.getMetaKey()));
        //Create Policy
        mainApp().open();
        createCustomerIndividual();
        policyNumberNb = createPolicy(policyData.resolveLinks());
        //Save policy Premium and Tier values
        policy.policyInquiry().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        Map<String, String> nbParams = paramMapToCompere();
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        Tab.buttonTopCancel.click();

        //Initiate manual conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        TestData policy2 = DataProviderFactory.emptyData().adjust(policyData.resolveLinks()).resolveLinks();
        policy.getDefaultView().fillUpTo(prepareConvTD(policyData), PremiumAndCoveragesTab.class, true);
        PremiumAndCoveragesTab.calculatePremium();
        //Save conversion policy Premium and Tier values
        Map<String, String> convParams = paramMapToCompere();
        premiumValue = new Dollar(convParams.get("Premium"));
        completeConversionPolicyCreation();

        policy2.adjust(TestData.makeKeyPath(documentsTab.getMetaKey(), documentsTab.getRequiredToBindAssetList().getName(), PROOF_OF_PRIOR_INSURANCE.getLabel()), "Yes").
                mask(TestData.makeKeyPath(documentsTab.getMetaKey(), AGREEMENT.getLabel()));

        policy.getDefaultView().fillFromTo(policy2, DocumentsAndBindTab.class, PurchaseTab.class);
        policyNumberConv = PolicySummaryPage.linkPolicy.getValue();

        //Compare new business and conversion values
        CustomAssert.assertEquals(nbParams, convParams);

    }

    /**
     * * @author Igor Garkusha
     *
     *@name Test Paperless Preferences properties and Inquiry mode
     *@scenario
     *1. Move server time to prepare renewal for two policies.
     *2. Create renewal images for this two policies.
     *3. Calculate premium and check tier value and tier factors values.
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, dependsOnMethods = "pas4145_calculateTierNyConversionCheckNB")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4145")
    public void pas4145_calculateTierNyConversionCheckRenewal(@Optional("NY") String state) {
        LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationJob);
        mainApp().reopen();
        SearchPage.openBilling(policyNumberConv);
        BillingSummaryPage.linkAcceptPayment.click();
        Tab acceptPayment = new AcceptPaymentActionTab();
        acceptPayment.fillTab(getTestSpecificTD("TestData").
                adjust(TestData.makeKeyPath(acceptPayment.getMetaKey(), AMOUNT.getLabel()), premiumValue.add(20).toString())).submitTab();
        //proposePolicyIfNeeded();
        TimeSetterUtil.getInstance().nextPhase(effDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumberConv);
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(PolicySummaryPage.getExpirationDate()));
        JobUtils.executeJob(Jobs.renewalJob);
        CustomAssert.assertEquals(getRenewalValues(policyNumberConv), getRenewalValues(policyNumberNb));

    }

    private void completeConversionPolicyCreation() {
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        premiumCovTab.submitTab();
        driverReportTab.getAssetList().getAsset("Validate Driving History", Button.class).click();
        driverReportTab.getAssetList().getAsset("Sales Agent Agreement", RadioGroup.class).setValue("I Agree");
        driverReportTab.submitTab();
    }

    private Map<String, String> getRenewalValues(String policyNumber) {
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        new AutoSSPolicyActions.DataGather().start();
        PremiumAndCoveragesTab.calculatePremium();
        Map<String, String> result = paramMapToCompere();
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        mainApp().close();
        return result;
    }

    private void proposePolicyIfNeeded() {
        if (!"Policy Pending".equals(BillingSummaryPage.tableBillingAccountPolicies.getColumn("Policy Status").getValue().get(0))) {
            return;
        }
        BillingSummaryPage.tableBillingAccountPolicies.getRow("Policy #", policyNumberConv).getCell(1).controls.links.get(2).click();
        Tab.buttonGo.click();
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        PremiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new BindTab().btnPurchase.click();
        Page.dialogConfirmation.confirm();
    }

    private Map<String, String> paramMapToCompere() {
        Map<String, String> params = new HashMap<String, String>() {{
            put("Premium", PremiumAndCoveragesTab.totalActualPremium.getValue());
        }};
        buttonViewRatingDetails.click();
        params.put("UW points", PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue());
        params.put("Tier", PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Customer's Tier").getCell(2).getValue());
        return params;
    }

    private TestData prepareConvTD(TestData policyTd) {
        return policyTd.mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), EFFECTIVE_DATE.getLabel())).
                mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), LEAD_SOURCE.getLabel())).
                mask(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), LAST_NAME.getLabel())).
                adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), CURRENT_AAA_MEMBER.getLabel()), "No").
                mask(TestData.makeKeyPath(premiumCovTab.getMetaKey(), POLICY_TERM.getLabel())).
                adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), PAYMENT_PLAN.getLabel()), "Annual (Renewal)").
                adjust(TestData.makeKeyPath(new DriverTab().getMetaKey(), LICENSE_STATE.getLabel()), "NY").
                adjust(TestData.makeKeyPath(new VehicleTab().getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Private Passenger Auto");
    }
}
