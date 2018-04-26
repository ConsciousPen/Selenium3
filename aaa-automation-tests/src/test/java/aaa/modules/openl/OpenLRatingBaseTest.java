package aaa.modules.openl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLTest;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.modules.policy.PolicyBaseTest;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public abstract class OpenLRatingBaseTest<P extends OpenLPolicy> extends PolicyBaseTest {
	protected static final Logger log = LoggerFactory.getLogger(OpenLRatingBaseTest.class);

	private static final Object UNMARSHAL_LOCK = new Object();
	private String testsDir;

	protected String getTestsDir() {
		return testsDir;
	}

	@BeforeTest
	@Parameters({"testsDir"})
	public void setTestsDir(String testsDir) {
		this.testsDir = testsDir;
	}

	protected TestData getRatingDataPattern() {
		return getPolicyTD();
	}

	protected void verifyPremiums(String openLFileName, Class<P> openLPolicyModelClass, TestDataGenerator<P> tdGenerator, List<Integer> policyNumbers) {
		//TODO-dchubkov: assert that date in openLFileName is valid
		List<P> openLPolicies = getOpenLPolicies(openLFileName, openLPolicyModelClass, policyNumbers);

		/*mainApp().open();
		String customerNumber = createCustomerIndividual();
		assertSoftly(softly -> {
			for (P openLPolicy : openLPolicies) {
				log.info("Premium calculation verification initiated for test with policy number {} and expected premium {} from {} OpenL file",
						openLPolicy.getNumber(), openLPolicy.getExpectedPremium(), openLFileName);

				//TODO-dchubkov: add assertion that Effective date cannot be more than ? months/years prior to current date (each product/state has it's own value)
				if (openLPolicy.getEffectiveDate().isAfter(TimeSetterUtil.getInstance().getCurrentTime())) {
					TimeSetterUtil.getInstance().nextPhase(openLPolicy.getEffectiveDate());
					mainApp().reopen();
					SearchPage.openCustomer(customerNumber);
				}

				Dollar actualPremium = createAndRateQuote(tdGenerator, openLPolicy);
				softly.assertThat(actualPremium).as("Total premium is not equal to expected one").isEqualTo(openLPolicy.getExpectedPremium());
				log.info("Premium calculation verification for policy #{} has been {}", Tab.labelPolicyNumber.getValue(), actualPremium.equals(openLPolicy.getExpectedPremium()) ? "passed" : "failed");
				Tab.buttonSaveAndExit.click();
			}
		});*/
	}

	protected abstract Dollar createAndRateQuote(TestDataGenerator<P> tdGenerator, P openLPolicy);

	protected List<P> getOpenLPolicies(String openLFileName, Class<P> openLPolicyModelClass, List<Integer> policyNumbers) {
		List<P> openLPolicies;
		List<OpenLTest> openLTests;

		synchronized (UNMARSHAL_LOCK) { // Used to solve performance issues when parsing thousands of excel rows simultaneously in multiple threads
			ExcelUnmarshaller excelUnmarshaller = new ExcelUnmarshaller(new File(getTestsDir() + "/" + openLFileName), false);
			openLTests = excelUnmarshaller.unmarshalRows(OpenLTest.class, policyNumbers);
			openLPolicies = excelUnmarshaller.unmarshalRows(openLPolicyModelClass, policyNumbers);
			excelUnmarshaller.flushCache().close();
		}

		//openLPolicies = getOpenLPoliciesWithExpectedPremiums(openLPolicies, openLTests);

		//Sort policies list by effective date for further valid time shifts
		//openLPolicies = openLPolicies.stream().sorted(Comparator.comparing(OpenLPolicy::getEffectiveDate)).collect(Collectors.toList());
		return openLPolicies;
	}

	protected List<P> getOpenLPoliciesWithExpectedPremiums(List<P> openLPolicies, List<OpenLTest> openLTests) {
		for (P policy : openLPolicies) {
			Dollar expectedPremium = openLTests.stream().filter(t -> t.getPolicy().equals(policy.getNumber())).findFirst().get().getTotalPremium();
			if (policy.getTerm() == 6) {
				expectedPremium = expectedPremium.divide(2);
			}
			policy.setExpectedPremium(expectedPremium);
		}
		return openLPolicies;
	}

	protected List<Integer> getPolicyNumbers(String policies) {
		if (StringUtils.isBlank(policies)) {
			return Collections.emptyList();
		}
		String[] policyNumberStrings = policies.split(",");
		List<Integer> policyNumbers = new ArrayList<>(policyNumberStrings.length);
		for (String p : policyNumberStrings) {
			int policyNumber;
			try {
				policyNumber = Integer.parseInt(p.trim());
			} catch (NumberFormatException e) {
				throw new IstfException(String.format("Unable get policy number from \"%s\" string.", p), e);
			}
			policyNumbers.add(policyNumber);
		}
		return policyNumbers;
	}
}
