package aaa.modules.openl;

import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class HomeCaPremiumCalculationTest<P extends HomeCaOpenLPolicy<?>, F extends HomeCaOpenLFile<P>> extends OpenLRatingBaseTest<P, F> {
	@Override
	protected TestData getRatingDataPattern() {
		return getPolicyTD("DataGather", "TestData_CA").mask(new PurchaseTab().getMetaKey());
	}

	@Override
	protected List<P> getOpenLPoliciesWithExpectedPremiums(F openLFile, List<Integer> policyNumbers) {
		List<P> openLPolicies = super.getOpenLPoliciesWithExpectedPremiums(openLFile, policyNumbers);
		for (P openLPolicy : openLPolicies) {
			double premiumLimit = openLPolicy.getForms().stream().filter(f -> "premium".equals(f.getFormCode())).findFirst()
					.orElseThrow(() -> new IstfException("Policy does not have form with formCode=\"premium\"")).getLimit();

			Dollar totalPremium = openLPolicy.getExpectedPremium().add(premiumLimit);
			openLPolicy.setExpectedPremium(totalPremium);
		}
		return openLPolicies;
	}

	@Override
	protected Dollar createAndRateQuote(TestDataGenerator<P> tdGenerator, P openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		new PremiumsAndCoveragesQuoteTab().fillTab(quoteRatingData);
		return PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
	}
}
