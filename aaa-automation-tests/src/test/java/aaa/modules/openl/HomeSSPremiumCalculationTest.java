package aaa.modules.openl;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLFile;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeSSTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import toolkit.datax.TestData;

public class HomeSSPremiumCalculationTest extends OpenLRatingBaseTest<HomeSSOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected String createAndRateQuote(TestDataGenerator<HomeSSOpenLPolicy> tdGenerator, HomeSSOpenLPolicy openLPolicy) {
		boolean isLegacyConvPolicy = false;
		if (HomeSSTestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals((openLPolicy.getCappingDetails().get(0).getProgramCode()))) {
			isLegacyConvPolicy = true;
			TestData renewalEntryData = ((HomeSSTestDataGenerator) tdGenerator).getRenewalEntryData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.initiate();
		}

		TestData quoteRatingData = ((HomeSSTestDataGenerator) tdGenerator).getRatingData(openLPolicy, isLegacyConvPolicy);

		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
		premiumsAndCoveragesQuoteTab.fillTab(quoteRatingData);

		if (openLPolicy.getForms().stream().filter(c -> "HS0904".equals(c.getFormCode())).findFirst().isPresent()) {
			premiumsAndCoveragesQuoteTab.submitTab();
			TestData policyIssueTd = getPolicyTD().ksam(new MortgageesTab().getMetaKey(), new UnderwritingAndApprovalTab().getMetaKey(), new DocumentsTab().getMetaKey(), new BindTab().getMetaKey(), new PurchaseTab().getMetaKey());
			policy.getDefaultView().fillUpTo(policyIssueTd, PurchaseTab.class, false);
			TestData endorsementData = ((HomeSSTestDataGenerator)tdGenerator).getEndorsementData(openLPolicy);
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
			}
			policy.endorse().perform(endorsementData);
		}

		Dollar finalTestPremium = PremiumsAndCoveragesQuoteTab.getPolicyDwellingPremium();
		return finalTestPremium.toString();
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeSSOpenLPolicy> tdGenerator = new HomeSSTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeSSOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
