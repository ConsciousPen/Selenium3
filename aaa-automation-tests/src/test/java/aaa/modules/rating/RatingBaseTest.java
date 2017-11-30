package aaa.modules.rating;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.Tab;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.datax.TestData;

public class RatingBaseTest<P extends OpenLPolicy> extends PolicyBaseTest {
	protected static final String OPENL_RATING_TESTS_FOLDER = "src/test/resources/openl";
	protected static final Logger log = LoggerFactory.getLogger(RatingBaseTest.class);
	private TestDataGenerator<P> testDataGenerator;

	public RatingBaseTest(TestDataGenerator<P> testDataGenerator) {
		this.testDataGenerator = testDataGenerator;
	}

	protected <O extends OpenLFile<P>> void verifyPremiums(String openLFileName, Class<O> openLFileModelClass) {
		this.testDataGenerator.setRatingDataPattern(getRatingDataPattern());
		OpenLFile<P> openLFile = getOpenLFileObject(openLFileName, openLFileModelClass);

		mainApp().open();
		createCustomerIndividual();

		for (P openLPolicy : openLFile.getPolicies()) {
			log.info("Premium calculation verification initiated for OpenL test with policy: {}", openLPolicy);
			TestData quoteRatingData = testDataGenerator.getRatingData(openLPolicy);

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

	protected TestData getRatingDataPattern() {
		TestData td = getPolicyTD().mask(new PurchaseTab().getMetaKey());
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return td;
	}
}
