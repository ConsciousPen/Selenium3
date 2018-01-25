package aaa.modules.openl;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import com.exigen.ipb.etcsa.utils.Dollar;
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
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class OpenLRatingBaseTest<P extends OpenLPolicy> extends PolicyBaseTest {
	protected static final Logger log = LoggerFactory.getLogger(OpenLRatingBaseTest.class);
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
		TestData td = getPolicyTD().mask(new PurchaseTab().getMetaKey());
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		return td;
	}

	protected <O extends OpenLFile<P>> void verifyPremiums(String openLFileName, Class<O> openLFileModelClass, TestDataGenerator<P> tdGenerator, List<Integer> policyNumbers) {
		Map<P, Dollar> openLPoliciesAndPremiumsMap = getOpenLPoliciesAndExpectedPremiums(openLFileName, openLFileModelClass, policyNumbers);

		mainApp().open();
		createCustomerIndividual();
		for (Map.Entry<P, Dollar> policyAndPremium : openLPoliciesAndPremiumsMap.entrySet()) {
			log.info("Premium calculation verification initiated for test with policy number {} and expected premium {} from {} OpenL file",
					policyAndPremium.getKey().getNumber(), policyAndPremium.getValue(), openLFileName);

			TestData quoteRatingData = tdGenerator.getRatingData(policyAndPremium.getKey());

			/*SearchPage.openQuote("QORSS952012721");
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());*/
			//TestData quoteRatingData = getPolicyDefaultTD();
			policy.initiate();
			policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
			new PremiumAndCoveragesTab().fillTab(quoteRatingData);

			assertSoftly(softly -> softly.assertThat(PremiumAndCoveragesTab.totalTermPremium).hasValue(policyAndPremium.getValue().toString()));
			Tab.buttonCancel.click();
		}
	}

	protected <O extends OpenLFile<P>> Map<P, Dollar> getOpenLPoliciesAndExpectedPremiums(String openLFileName, Class<O> openLFileModelClass, List<Integer> policyNumbers) {
		ExcelManager openLFileManager = new ExcelManager(new File(getTestsDir() + "/" + openLFileName));

		if (CollectionUtils.isNotEmpty(policyNumbers)) {
			// Exclude extra rows from policies table to reduce time required for excel unmarshalling
			ExcelTable policiesTable = openLFileManager.getSheet(OpenLFile.POLICY_SHEET_NAME).getTable(OpenLFile.POLICY_HEADER_ROW_NUMBER);
			List<Integer> rowsToExclude = policiesTable.getRowsIndexes().stream().filter(i -> !policyNumbers.contains(i)).collect(Collectors.toList());
			policiesTable.excludeRows(rowsToExclude.toArray(new Integer[policyNumbers.size()])).setComparisonRules(false, true);
		}

		ExcelUnmarshaller eUnmarshaller = new ExcelUnmarshaller();
		OpenLFile<P> openLFile = eUnmarshaller.unmarshal(openLFileManager, openLFileModelClass, false, false);

		List<P> openLPoliciesList = CollectionUtils.isEmpty(policyNumbers)
				? openLFile.getPolicies()
				: openLFile.getPolicies().stream().filter(p -> policyNumbers.contains(p.getNumber())).collect(Collectors.toList());

		Map<P, Dollar> openLPoliciesAndPremiumsMap = new HashMap<>(openLPoliciesList.size());
		for (P openLPolicy : openLPoliciesList) {
			TableRow row = openLFileManager.getSheet(OpenLFile.TESTS_SHEET_NAME).getTable(OpenLFile.TESTS_HEADER_ROW_NUMBER).getRow("policy", openLPolicy.getNumber());
			Dollar expectedPremium = new Dollar(row.getSumContains("_res_.$Value"));
			openLPoliciesAndPremiumsMap.put(openLPolicy, expectedPremium);
		}

		openLFileManager.close();
		return openLPoliciesAndPremiumsMap;
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
