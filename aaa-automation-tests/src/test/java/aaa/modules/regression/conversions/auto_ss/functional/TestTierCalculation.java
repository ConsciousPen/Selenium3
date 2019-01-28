package aaa.modules.regression.conversions.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.NY)
@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public class TestTierCalculation extends AutoSSBaseTest {

    private final GeneralTab generalTab = new GeneralTab();
    private final PremiumAndCoveragesTab premiumCovTab = new PremiumAndCoveragesTab();
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2443")
    public void pas2443_calculateTierNyConversionCheckNB(@Optional("NY") String state) {

        TestData tdAutoNB = getPolicyDefaultTD()
                .adjust(generalTab.getMetaKey(), getTestSpecificTD("TestData").getTestData(generalTab.getMetaKey()))
                .adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$250,000/$500,000");
        TestData tdAutoConv = getConversionPolicyDefaultTD()
				.mask(TestData.makeKeyPath(generalTab.getMetaKey(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()))
				.adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()), "No")
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()), getTestSpecificTD("CurrentCarrierInformation"))
                .adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel()), "contains=$250,000/$500,000");

        //Create Policy
		policyNumberNb = openAppAndCreatePolicy(tdAutoNB);

        //Save policy Premium and Tier values
        policy.policyInquiry().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        Map<String, String> nbParams = paramMapToCompere();
         PremiumAndCoveragesTab.RatingDetailsView.close();
        Tab.buttonTopCancel.click();

        //Initiate manual conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(tdAutoConv, PremiumAndCoveragesTab.class, true);
	    premiumCovTab.calculatePremium();

        //Save conversion policy Premium and Tier values
        Map<String, String> convParams = paramMapToCompere();
        premiumValue = new Dollar(convParams.get("Premium"));
         PremiumAndCoveragesTab.RatingDetailsView.close();
        premiumCovTab.submitTab();

        //Finish policy and save/exit
        policy.getDefaultView().fillFromTo(tdAutoConv, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
        documentsTab.submitTab();
        PolicySummaryPage.buttonBackFromRenewals.click();
        policyNumberConv = PolicySummaryPage.getPolicyNumber();

        //Compare new business and conversion values
        assertThat(nbParams).isEqualTo(convParams);

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
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, dependsOnMethods = "pas2443_calculateTierNyConversionCheckNB")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2443")
    public void pas2443_calculateTierNyConversionCheckRenewal(@Optional("NY") String state) {
        LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));

        // Propose conversion renewal image
		mainApp().open();
		SearchPage.openPolicy(policyNumberConv);
		policy.dataGather().start();
		premiumCovTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsTab.submitTab();

		// Pay amount due
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.linkAcceptPayment.click();
		Tab acceptPayment = new AcceptPaymentActionTab();
		acceptPayment.fillTab(getTestSpecificTD("TestData")
				.adjust(TestData.makeKeyPath(acceptPayment.getMetaKey(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()), premiumValue.add(20).toString())).submitTab();

		// Run policy status update job
		mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(effDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);

        // Change time point to renewal image generation date
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumberConv);
        LocalDateTime expDate = PolicySummaryPage.getExpirationDate();
		mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(expDate));

        // Create renewal image for both policies and validate tier values
		mainApp().open();
        assertThat(getRenewalValues(policyNumberConv)).isEqualTo(getRenewalValues(policyNumberNb));

    }

    private Map<String, String> getRenewalValues(String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		policy.renew().start();
		premiumCovTab.calculatePremium();
        Map<String, String> result = paramMapToCompere();
         PremiumAndCoveragesTab.RatingDetailsView.close();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsTab.submitTab();
        return result;
    }

    private Map<String, String> paramMapToCompere() {
        Map<String, String> params = new HashMap<String, String>() {{
            put("Premium", PremiumAndCoveragesTab.totalActualPremium.getValue());
        }};
        PremiumAndCoveragesTab.RatingDetailsView.open();
        params.put("UW points", PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Total Underwriter Points Used in Tier").getCell(6).getValue());
        params.put("Tier", PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Customer's Tier").getCell(2).getValue());
        return params;
    }

}
