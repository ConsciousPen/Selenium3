package aaa.modules.openl;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import aaa.common.Tab;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLTest;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.ExcelCell;
import aaa.utils.excel.io.entity.TableRow;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class OpenLRatingBaseTest<P extends OpenLPolicy> extends PolicyBaseTest {
	protected static final Logger log = LoggerFactory.getLogger(OpenLRatingBaseTest.class);
	private String testsDir;
	private TestDataGenerator<P> testDataGenerator;

	public OpenLRatingBaseTest(TestDataGenerator<P> testDataGenerator) {
		this.testDataGenerator = testDataGenerator;
	}

	protected String getTestsDir() {
		return testsDir;
	}

	@BeforeTest
	@Parameters({"testsDir"})
	public void setTestsDir(String testsDir) {
		this.testsDir = testsDir;
	}

	protected TestData getRatingDataPattern() {
		TestData td = getPolicyTD().mask(new PurchaseTab().getMetaKey());
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return td;
	}

	protected <O extends OpenLFile<P>> void verifyPremiums(String openLFileName, Class<O> openLFileModelClass, List<Integer> policyNumbers) {
		this.testDataGenerator.setRatingDataPattern(getRatingDataPattern());
		OpenLFile<P> openLFile = getOpenLFileObject(openLFileName, openLFileModelClass);

		mainApp().open();
		createCustomerIndividual();

		for (P openLPolicy : getOpenLPolicies(openLFile, policyNumbers)) {
			log.info("Premium calculation verification initiated for test with policy number {} from {} OpenL filename", openLPolicy.getNumber(), openLFileName);
			TestData quoteRatingData = testDataGenerator.getRatingData(openLPolicy);
			String expectedPremium = String.valueOf(getExpectedPremium(openLFile, openLFileName, openLPolicy.getNumber()));

			policy.initiate();
			policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
			new PremiumAndCoveragesTab().fillTab(quoteRatingData);

			assertSoftly(softly -> softly.assertThat(PremiumAndCoveragesTab.totalTermPremium).hasValue(expectedPremium));
			Tab.buttonCancel.click();
		}
	}

	protected List<P> getOpenLPolicies(OpenLFile<P> openLFile, List<Integer> policyNumbers) {
		if (policyNumbers.isEmpty()) {
			return openLFile.getPolicies();
		}
		return openLFile.getPolicies().stream().filter(p -> policyNumbers.contains(p.getNumber())).collect(Collectors.toList());
	}

	protected <T> T getOpenLFileObject(String openLFileName, Class<T> openLFileModelClass) {
		ExcelUnmarshaller eUnmarshaller = new ExcelUnmarshaller();
		return eUnmarshaller.unmarshal(getOpenLFile(openLFileName), openLFileModelClass);
	}

	protected int getExpectedPremium(OpenLFile<P> openLFile, String openLFileName, int policyNumber) {
		OpenLTest test = openLFile.getTests().stream().filter(t -> t.getPolicy() == policyNumber).findFirst()
				.orElseThrow(() -> new IstfException("There is no rating test with policy number " + policyNumber));

		if (test.getTotalPremium() == null) {
			int expectedPremium;
			ExcelManager excelManager = new ExcelManager(getOpenLFile(openLFileName));
			TableRow row = excelManager.getSheet(OpenLFile.TESTS_SHEET_NAME).getTable(OpenLFile.TESTS_HEADER_ROW_NUMBER).getRow("policy", policyNumber);
			expectedPremium = row.getCellsContains("_res_.$Value").stream().mapToInt(ExcelCell::getIntValue).sum();
			excelManager.close();
			return expectedPremium;
		}
		return test.getTotalPremium();
	}

	protected List<Integer> getPolicyNumbers(String policies) {
		if (policies.isEmpty()) {
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

	private File getOpenLFile(String openLFileName) {
		return new File(getTestsDir() + "/" + openLFileName);
	}
}
