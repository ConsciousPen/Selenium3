package aaa.helpers.rating;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Tab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class RatingBaseTest extends BaseTest {
	protected static final String OPENL_RATING_TESTS_FOLDER = "//openl";
	protected static final Logger log = LoggerFactory.getLogger(RatingBaseTest.class);
	protected IPolicy policy;

	protected void verifyPremiums(OpenLTestsHolder<?, ?> openLTests) {
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		for (OpenLTestsHolder.OpenLTest openLTest : openLTests.getTests()) {
			log.info("Premium calculation verification initiated for OpenL test: {}", openLTest);
			TestData quoteRatingData = openLTest.getTestData();
			if (getPolicyType().equals(PolicyType.PUP)) {
				quoteRatingData = new PrefillTab().adjustWithRealPolicies(quoteRatingData, getPrimaryPoliciesForPup());
			}

			policy.initiate();
			policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
			new PremiumAndCoveragesTab().fillTab(quoteRatingData);

			assertSoftly(softly ->
					softly.assertThat(PremiumAndCoveragesTab.totalTermPremium).hasValue(openLTest.getExpectedPremium().toString()));
			Tab.buttonCancel.click();
		}
	}
}
