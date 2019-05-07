package aaa.modules.openl;

import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.helpers.openl.testdata_generator.HomeSSTestDataGenerator;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeSSPremiumCalculationTest extends OpenLRatingBaseTest<HomeSSOpenLPolicy> {

	@Override
	protected String createQuote(HomeSSOpenLPolicy openLPolicy) {
		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
		HomeSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getRatingDataPattern());

		// create real Auto Policy for PA state
		if (Constants.States.PA.equals(openLPolicy.getPolicyAddress().getState()) && openLPolicy.getPolicyDiscountInformation().isAutoPolicyInd()) {
			TestData autoPolicyData = tdGenerator.getAutoPolicyData(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData"), openLPolicy);
			PolicyType.AUTO_SS.get().createPolicy(autoPolicyData);
			tdGenerator.autoPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		}

		if (openLPolicy.isLegacyConvPolicy()) {
			TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.get().initiate();
		}

		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);

		policy.get().getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		checkFormHS0492(tdGenerator, openLPolicy);
		checkFormHS0490(tdGenerator, openLPolicy);
		policy.get().getDefaultView().fillUpTo(tdGenerator.getPremiumsAndCoveragesQuoteTabData(openLPolicy), PremiumsAndCoveragesQuoteTab.class, true);

		TestData proofData = tdGenerator.getProofData(openLPolicy);
		if (!proofData.equals(DataProviderFactory.emptyData())) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
			policy.get().getDefaultView().fill(DataProviderFactory.dataOf(new DocumentsTab().getMetaKey(), proofData));
		}

		TestData overrideErrorData = tdGenerator.getOverrideErrorData(openLPolicy);
		if (!overrideErrorData.equals(DataProviderFactory.emptyData())) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
			policy.get().getDefaultView().fill(overrideErrorData);
			ErrorTab errorTab = new ErrorTab();
			if (errorTab.isVisible()) {
				errorTab.cancel();
			}
		}

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		checkFormHS0904(tdGenerator, openLPolicy);

		if (openLPolicy.isCappedPolicy() && !PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.isPresent()) {
			premiumsAndCoveragesQuoteTab.calculatePremium();
			assertThat(PremiumsAndCoveragesQuoteTab.linkViewCappingDetails).as("View Capping Details link did not appear after premium calculation").isPresent();
		}

		// Set capping factor from test if policy is capped
		if (PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.isPresent()) {
			PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.VIEW_CAPPING_DETAILS_DIALOG).fill(tdGenerator.getCappingData(openLPolicy));
		}

		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(HomeSSOpenLPolicy openLPolicy) {
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		return PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().subtract(getSpecificFees(openLPolicy));
	}

	@Override
	protected String createCustomerIndividual(HomeSSOpenLPolicy openLPolicy) {
		return createCustomerIndividual();
	}

	private void checkFormHS0492(HomeSSTestDataGenerator tdGenerator, HomeSSOpenLPolicy openLPolicy) {
		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0492".equals(c.getFormCode()))) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
			policy.get().getDefaultView().fillUpTo(tdGenerator.getFormHS0492Data(openLPolicy), PremiumsAndCoveragesQuoteTab.class, false);
		}
	}

	private void checkFormHS0490(HomeSSTestDataGenerator tdGenerator, HomeSSOpenLPolicy openLPolicy) {
		if (openLPolicy.getForms().stream().noneMatch(c -> "HS0490".equals(c.getFormCode())) && PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", "HS 04 90").isPresent()) {
			policy.get().getDefaultView().fillUpTo(tdGenerator.getChangeCoverageCData(openLPolicy), PremiumsAndCoveragesQuoteTab.class, true);
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
			policy.get().getDefaultView().fill(tdGenerator.getRemoveHS0490Data(openLPolicy));
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		}
	}

	private void checkFormHS0904(HomeSSTestDataGenerator tdGenerator, HomeSSOpenLPolicy openLPolicy) {
		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0904".equals(c.getFormCode())) && !openLPolicy.isLegacyConvPolicy()) {
			new PremiumsAndCoveragesQuoteTab().calculatePremium();
			PremiumsAndCoveragesQuoteTab.btnContinue.click();

			TestData policyPurchaseData = tdGenerator.getPolicyPurchaseData(openLPolicy);
			policy.get().getDefaultView().fillUpTo(policyPurchaseData, PurchaseTab.class, false);
			ErrorTab errorTab = new ErrorTab();
			if (errorTab.isVisible()) {
				errorTab.overrideAllErrors();
				errorTab.submitTab();
			}

			policy.get().getDefaultView().fill(DataProviderFactory.dataOf(PurchaseTab.class.getSimpleName(), policyPurchaseData.getTestData(PurchaseTab.class.getSimpleName())));

			TestData endorsementData = tdGenerator.getEndorsementData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
			}
			policy.get().endorse().performAndFill(endorsementData);
		}
	}

	private Dollar getSpecificFees(HomeSSOpenLPolicy openLPolicy) {
		Dollar specificFees = new Dollar(0);
		if (PremiumsAndCoveragesQuoteTab.tableTaxes.isPresent()) {
			specificFees = new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes.getRowContains("Description", "Total").getCell("Term Premium ($)").getValue());
		}
		if (openLPolicy.getPolicyAddress().isOhioMineSubsidenceCounty()) {
			specificFees = specificFees.add(new Dollar(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", "Ohio Mine Subsidence Insurance").getCell("Term Premium ($)").getValue()));
		}
		return specificFees;
	}
}
