package aaa.modules.openl;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.openl.OpenLTestInfo;
import aaa.helpers.openl.OpenLTestsManager;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public abstract class OpenLRatingBaseTest<P extends OpenLPolicy> extends PolicyBaseTest {
	protected static final Logger log = LoggerFactory.getLogger(OpenLRatingBaseTest.class);
	
	private static final Object TESTS_PREPARATIONS_LOCK = new Object();
	private static OpenLTestsManager openLTestsManager;

	/**
	 * Get base policy creation TestData which will be used as second argument in appropriate {@link TestDataGenerator#TestDataGenerator(String, TestData)} constructor.
	 * It should be used to add common test data to test specific rating test data generated by {@link TestDataGenerator#getRatingData(OpenLPolicy)}
	 * By default it returns {@code getPolicyTD("DataGather", "TestData")} test data but should be overridden in test class level if needed (e.g. to mask some tabs)
	 *
	 * @return base policy creation TestData
	 */
	protected TestData getRatingDataPattern() {
		return getPolicyTD();
	}

	/**
	 * Performs test preparations for all suites and their child suites at once such as creation of OpenL policy objects from excel files (remote or local) and mock files updates on endpoint (if needed)
	 *
	 * @param context injected TestNG context with all test run information
	 */
	@BeforeSuite
	public void testsPreparations(ITestContext context) {
		synchronized (TESTS_PREPARATIONS_LOCK) {
			if (openLTestsManager == null) {
				openLTestsManager = new OpenLTestsManager(context);
				//openLTestsManager.updateMocks();
			}
		}
	}

	/**
	 * Main test method for policy creation, premium calculation and Total Premium verification
	 *
	 *  @param context injected TestNG context with all test run information
	 */
	protected void verifyPremiums(ITestContext context) {
		OpenLTestInfo<P> testInfo = openLTestsManager.getTestInfo(context);
		if (testInfo.isFailed()) {
			Assert.fail("OpenL test preparation has been failed for file: " + testInfo.getOpenLFilePath(), testInfo.getException());
		}

		mainApp().open();
		String customerNumber = createCustomerIndividual();
		assertSoftly(softly -> {
			for (P openLPolicy : testInfo.getOpenLPolicies()) {
				log.info("Premium calculation verification initiated for test {} and expected premium {} from \"{}\" OpenL file",
						openLPolicy.getNumber(), openLPolicy.getExpectedPremium(), testInfo.getOpenLFilePath());

				//TODO-dchubkov: add assertion that Effective date cannot be more than ? months/years prior to current date (each product/state has it's own value)
				if (openLPolicy.getEffectiveDate().isAfter(TimeSetterUtil.getInstance().getCurrentTime().toLocalDate())) {
					TimeSetterUtil.getInstance().nextPhase(openLPolicy.getEffectiveDate().atStartOfDay());
					mainApp().reopen();
					SearchPage.openCustomer(customerNumber);
				}
				Dollar actualPremium = createAndRateQuote(openLPolicy);
				softly.assertThat(actualPremium).as("Total premium for policy number %s is not equal to expected one", openLPolicy.getNumber()).isEqualTo(openLPolicy.getExpectedPremium());
				log.info("Premium calculation verification for test {} with policy #{} has been {}",
						openLPolicy.getNumber(), Tab.labelPolicyNumber.getValue(), actualPremium.equals(openLPolicy.getExpectedPremium()) ? "passed" : "failed");
				Tab.buttonSaveAndExit.click();
			}
		});
	}

	/**
	 * This method should generate appropriate test data to create policy (and/or perform endorsement(s), renewal(s) if needed), retrieve total premium from UI and return it
	 *
	 * @param openLPolicy openl policy object as a representation of row from {@link OpenLFile#POLICY_SHEET_NAME} excel sheet
	 * @return actual total premium from UI
	 */
	protected abstract Dollar createAndRateQuote(P openLPolicy);
}
