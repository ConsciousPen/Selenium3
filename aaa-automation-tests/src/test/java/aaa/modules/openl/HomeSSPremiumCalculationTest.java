package aaa.modules.openl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class HomeSSPremiumCalculationTest extends OpenLRatingBaseTest<HomeSSOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		throw new IstfException("Please override method in appropriate child class with relevant policy type");
	}

	@Override
	protected String createQuote(HomeSSOpenLPolicy openLPolicy) {
		//		ApplicationMocksManager.restartStubServer();
		if (!getPolicyType().getShortName().contains(openLPolicy.getPolicyType())) {
			throw new IstfException(String.format("Test can't use selected policy with policy type '%s'", openLPolicy.getPolicyType()));
		}
		HomeSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern());
		// create real Auto Policy for PA state
		if (Constants.States.PA.equals(openLPolicy.getPolicyAddress().getState()) && openLPolicy.getPolicyDiscountInformation().isAutoPolicyInd()) {
			TestData autoPolicyData = tdGenerator.getAutoPolicyData(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData"), openLPolicy);
			PolicyType.AUTO_SS.get().createQuote(autoPolicyData);
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
			TestData formHS0492Data = tdGenerator.getFormHS0492Data(openLPolicy);
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
			policy.getDefaultView().fillUpTo(formHS0492Data, PremiumsAndCoveragesQuoteTab.class, false);
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

	@Override
	protected Map<String, String> getOpenLFieldsMapFromTest(HomeSSOpenLPolicy openLPolicy) {
		Map<String, String> openLFieldsMap = super.getOpenLFieldsMapFromTest(openLPolicy);
		openLFieldsMap.remove("policy.id");
		List<String> policyKeys = openLFieldsMap.entrySet().stream().filter(e -> e.getKey().startsWith("policy.")).map(Map.Entry::getKey).collect(Collectors.toList());
		policyKeys.forEach(k -> openLFieldsMap.put(k.replace("policy.", "p."), openLFieldsMap.remove(k)));
		return openLFieldsMap;
	}

	private Dollar getSpecificFees(HomeSSOpenLPolicy openLPolicy) {
		if (Constants.States.OH.equals(openLPolicy.getPolicyAddress().getState()) && !openLPolicy.getForms().stream().anyMatch(c -> "DSMSI2".equals(c.getFormCode()))) {
			if (PremiumsAndCoveragesQuoteTab.tableEndorsementForms.isPresent() && PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", "DS MS I2 Ohio Mine Subsidence Insurance").isPresent()) {
				return new Dollar(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains("Description", "DS MS I2 Ohio Mine Subsidence Insurance").getCell("Term Premium ($)").getValue());
			}
		}
		if (Constants.States.WV.equals(openLPolicy.getPolicyAddress().getState())) {
			if (PremiumsAndCoveragesQuoteTab.tableTaxes.isPresent()) {
				return new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes.getRowContains("Description", "Total").getCell("Term Premium ($)").getValue());
			}
		}
		//TODO add other specific taxes and fees
		return new Dollar(0);
	}
}
