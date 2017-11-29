package aaa.modules.rating;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Tab;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.modules.BaseTest;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.datax.TestData;

public class RatingBaseTest<P extends OpenLPolicy> extends BaseTest {
	protected static final String OPENL_RATING_TESTS_FOLDER = "src/test/resources/openl";
	protected static final Logger log = LoggerFactory.getLogger(RatingBaseTest.class);
	protected IPolicy policy;
	private TestDataGenerator<P> testDataGenerator;

	public RatingBaseTest(TestDataGenerator<P> testDataGenerator) {
		this.testDataGenerator = testDataGenerator;
	}

	protected <O extends OpenLFile<P>> void verifyPremiums(String openLFileName, Class<O> openLFileModelClass) {
		policy = getPolicyType().get();
		OpenLFile<P> openLFile = getOpenLFileObject(openLFileName, openLFileModelClass);

		mainApp().open();
		createCustomerIndividual();

		for (P openLPolicy : openLFile.getPolicies()) {
			log.info("Premium calculation verification initiated for OpenL test with policy: {}", openLPolicy);
			TestData quoteRatingData = testDataGenerator.getRatingData(openLPolicy);
			if (getPolicyType().equals(PolicyType.PUP)) {
				quoteRatingData = new PrefillTab().adjustWithRealPolicies(quoteRatingData, getPrimaryPoliciesForPup());
			}

			policy.initiate();
			policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
			new PremiumAndCoveragesTab().fillTab(quoteRatingData);

			assertSoftly(softly ->
					softly.assertThat(PremiumAndCoveragesTab.totalTermPremium).hasValue(getExpectedPremium(openLFile, openLPolicy.getNumber())));
			Tab.buttonCancel.click();
		}
	}

	protected <T> T getOpenLFileObject(String openLFileName, Class<T> openLFileModelClass) {
		String filePath = OPENL_RATING_TESTS_FOLDER + "/" + openLFileName;
		ExcelUnmarshaller eUnmarshaller = new ExcelUnmarshaller();
		return eUnmarshaller.unmarshal(new File(filePath), openLFileModelClass);
	}

	protected String getExpectedPremium(OpenLFile<P> openLFile, int policyNumber) {
		return String.valueOf(openLFile.getTests().stream().filter(t -> t.getPolicy() == policyNumber).findFirst().get().getTotalPremium());
	}
}
