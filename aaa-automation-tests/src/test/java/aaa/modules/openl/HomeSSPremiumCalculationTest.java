package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLFile;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeSSTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeSSPremiumCalculationTest extends OpenLRatingBaseTest<HomeSSOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected Dollar createAndRateQuote(TestDataGenerator<HomeSSOpenLPolicy> tdGenerator, HomeSSOpenLPolicy openLPolicy) {
		boolean isLegacyConvPolicy = false;
		if (TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().get(0).getProgramCode())) {
			isLegacyConvPolicy = true;
			TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.initiate();
		}

		TestData quoteRatingData = ((HomeSSTestDataGenerator) tdGenerator).getRatingData(openLPolicy, isLegacyConvPolicy);

		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);

		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0492".equals(c.getFormCode()))) {
			TestData formHS0492Data = ((HomeSSTestDataGenerator) tdGenerator).getFormHS0492Data(openLPolicy);
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
			policy.getDefaultView().fillUpTo(formHS0492Data, PremiumsAndCoveragesQuoteTab.class, false);
		}

		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
		premiumsAndCoveragesQuoteTab.fillTab(quoteRatingData);

		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0904".equals(c.getFormCode())) && !TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().get(0).getProgramCode())) {
			premiumsAndCoveragesQuoteTab.submitTab();
			TestData policyIssueData = ((HomeSSTestDataGenerator) tdGenerator).getPolicyIssueData(openLPolicy);

			policy.getDefaultView().fillUpTo(policyIssueData, PurchaseTab.class, false);
			ErrorTab errorTab = new ErrorTab();
			if (errorTab.isVisible()) {
				errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_HO_SS3230000);
				errorTab.override();
				new BindTab().submitTab();
			}
			policy.getDefaultView().fill(DataProviderFactory.dataOf(PurchaseTab.class.getSimpleName(), getPolicyTD().getTestData(PurchaseTab.class.getSimpleName())));

			TestData endorsementData = ((HomeSSTestDataGenerator) tdGenerator).getEndorsementData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
			}
			policy.endorse().performAndFill(endorsementData);
			new PremiumsAndCoveragesQuoteTab().calculatePremium();
		}

		return PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeSSOpenLPolicy> tdGenerator = new HomeSSTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeSSOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
