package aaa.modules.rating;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Tab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.modules.BaseTest;
import aaa.utils.openl.model.OpenLPolicy;
import aaa.utils.openl.testdata_builder.TestDataBuilder;
import toolkit.datax.TestData;

public class RatingBaseTest<P extends OpenLPolicy> extends BaseTest {
	protected static final String OPENL_RATING_TESTS_FOLDER = "src/test/resources/openl";
	protected static final Logger log = LoggerFactory.getLogger(RatingBaseTest.class);
	protected IPolicy policy;
	private TestDataBuilder<P> testDataBuilder;

	public RatingBaseTest(TestDataBuilder<P> testDataBuilder) {
		this.testDataBuilder = testDataBuilder;
	}

	protected void verifyPremiums(List<P> openLPolicies) {
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		for (P oPolicy : openLPolicies) {
			log.info("Premium calculation verification initiated for OpenL test with policy: {}", oPolicy);
			TestData quoteRatingData = testDataBuilder.buildRatingData(oPolicy);
			if (getPolicyType().equals(PolicyType.PUP)) {
				quoteRatingData = new PrefillTab().adjustWithRealPolicies(quoteRatingData, getPrimaryPoliciesForPup());
			}

			policy.initiate();
			policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
			new PremiumAndCoveragesTab().fillTab(quoteRatingData);

			assertSoftly(softly ->
					softly.assertThat(PremiumAndCoveragesTab.totalTermPremium).hasValue(oPolicy.getExpectedPremium().toString()));
			Tab.buttonCancel.click();
		}
	}
}
