package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.helpers.openl.testdata_generator.HomeSSTestDataGenerator;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class HomeSSPremiumCalculationTest extends OpenLRatingBaseTest<HomeSSOpenLPolicy> {

	@Override
	protected String createQuote(HomeSSOpenLPolicy openLPolicy) {
		if (!getPolicyType().getShortName().contains(openLPolicy.getPolicyType())) {
			throw new IstfException(String.format("Test can't use selected policy with policy type '%s'", openLPolicy.getPolicyType()));
		}
		HomeSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getRatingDataPattern());
		// create real Auto Policy for PA state
		if (Constants.States.PA.equals(openLPolicy.getPolicyAddress().getState()) && openLPolicy.getPolicyDiscountInformation().isAutoPolicyInd()) {
			TestData autoPolicyData = tdGenerator.getAutoPolicyData(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData"), openLPolicy);
			PolicyType.AUTO_SS.get().createPolicy(autoPolicyData);
			tdGenerator.autoPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		}
		boolean isLegacyConvPolicy = false;
		if (TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().getProgramCode())) {
			isLegacyConvPolicy = true;
			TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);
			renewalEntryData.adjust(TestData.makeKeyPath(new InitiateRenewalEntryActionTab().getMetaKey(), CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_POLICY_PREMIUM.getLabel()), openLPolicy.getCappingDetails().getPreviousPolicyPremium().toString());

			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.initiate();
		}

		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy, isLegacyConvPolicy);

		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);

		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0492".equals(c.getFormCode()))) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
			policy.getDefaultView().fillUpTo(tdGenerator.getFormHS0492Data(openLPolicy), PremiumsAndCoveragesQuoteTab.class, false);
		}

		TestData documentsToBindData = tdGenerator.getDocumentsToBindData(openLPolicy);
		if (!documentsToBindData.equals(DataProviderFactory.emptyData())) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
			policy.getDefaultView().fill(DataProviderFactory.dataOf(DocumentsTab.class.getSimpleName(), documentsToBindData));
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		}

		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
		premiumsAndCoveragesQuoteTab.getAssetList().fill(quoteRatingData);

		if (openLPolicy.getForms().stream().noneMatch(c -> "HS0490".equals(c.getFormCode())) && PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", "HS 04 90").isPresent()) {
			premiumsAndCoveragesQuoteTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
			policy.getDefaultView().fill(tdGenerator.getRemoveHS0490Data(openLPolicy));
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		}

		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0904".equals(c.getFormCode())) && !TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().getProgramCode())) {
			premiumsAndCoveragesQuoteTab.calculatePremium();
			premiumsAndCoveragesQuoteTab.submitTab();

			TestData policyIssueData = tdGenerator.getPolicyIssueData(openLPolicy);
			policy.getDefaultView().fillUpTo(policyIssueData, PurchaseTab.class, false);
			ErrorTab errorTab = new ErrorTab();
			if (errorTab.isVisible()) {
				errorTab.overrideAllErrors();
				errorTab.submitTab();
			}
			policy.getDefaultView().fill(DataProviderFactory.dataOf(PurchaseTab.class.getSimpleName(), getPolicyTD("DataGather", "PurchaseTab_WithAutopay")));

			TestData endorsementData = tdGenerator.getEndorsementData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
			}
			policy.endorse().performAndFill(endorsementData);
		}

		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(HomeSSOpenLPolicy openLPolicy) {
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		return PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().subtract(getSpecificFees(openLPolicy));
	}

	private Dollar getSpecificFees(HomeSSOpenLPolicy openLPolicy) {
		Dollar specificFees = new Dollar(0);
		if (PremiumsAndCoveragesQuoteTab.tableTaxes.isPresent()) {
			specificFees = new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes.getRowContains("Description", "Total").getCell("Term Premium ($)").getValue());
		}
		if (Constants.States.OH.equals(openLPolicy.getPolicyAddress().getState()) && openLPolicy.getForms().stream().noneMatch(c -> "DSMSI2".equals(c.getFormCode()))) {
			if (PremiumsAndCoveragesQuoteTab.tableEndorsementForms.isPresent() && PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", "DS MS I2 Ohio Mine Subsidence Insurance").isPresent()) {
				specificFees = specificFees.add(new Dollar(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", "DS MS I2 Ohio Mine Subsidence Insurance").getCell("Term Premium ($)").getValue()));
			}
		}
		return specificFees;
	}
}
