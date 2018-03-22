package aaa.modules.openl;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLTest;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLFile;
import aaa.helpers.openl.model.pup.PUPOpenLFile;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
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
		/*if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}*/
		return td;
	}

	protected <O extends OpenLFile<P>> void verifyPremiums(String openLFileName, Class<O> openLFileModelClass, TestDataGenerator<P> tdGenerator, List<Integer> policyNumbers) {
		//TODO-dchubkov: assert that date in openLFileName is valid
		Map<P, Dollar> openLPoliciesAndPremiumsMap = getOpenLPoliciesAndExpectedPremiums(openLFileName, openLFileModelClass, policyNumbers);

		mainApp().open();
		String customerNumber = createCustomerIndividual();
		assertSoftly(softly -> {
			for (Map.Entry<P, Dollar> policyAndPremium : openLPoliciesAndPremiumsMap.entrySet()) {
				P policyObject = policyAndPremium.getKey();
				log.info("Premium calculation verification initiated for test with policy number {} and expected premium {} from {} OpenL file",
						policyObject.getNumber(), policyAndPremium.getValue(), openLFileName);

				//TODO-dchubkov: add assertion that policy effective date is not too old (ask about valid policy age requirement)
				if (policyObject.getEffectiveDate().isAfter(TimeSetterUtil.getInstance().getCurrentTime())) {
					TimeSetterUtil.getInstance().nextPhase(policyObject.getEffectiveDate());
					mainApp().reopen();
					SearchPage.openCustomer(customerNumber);
				}

				createAndRateQuote(tdGenerator, policyObject);
				softly.assertThat(PremiumAndCoveragesTab.totalTermPremium).hasValue(policyAndPremium.getValue().toString());
				Tab.buttonSaveAndExit.click();
			}
		});
	}

	protected void createAndRateQuote(TestDataGenerator<P> tdGenerator, P openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().fillTab(quoteRatingData);
	}

	protected <O extends OpenLFile<P>> Map<P, Dollar> getOpenLPoliciesAndExpectedPremiums(String openLFileName, Class<O> openLFileModelClass, List<Integer> policyNumbers) {
		ExcelManager openLFileManager = new ExcelManager(new File(getTestsDir() + "/" + openLFileName));

		if (CollectionUtils.isNotEmpty(policyNumbers)) {
			String policySheetName = OpenLFile.POLICY_SHEET_NAME;
			if (getPolicyType().equals(PolicyType.AUTO_SS)) {
				policySheetName = AutoSSOpenLFile.POLICY_SHEET_NAME;
			} else if (getPolicyType().equals(PolicyType.PUP)) {
				policySheetName = PUPOpenLFile.PUP_POLICY_SHEET_NAME;
			}
			// Find policies table with only needed test rows and store it in ExcelManager instance to reduce time required for further excel unmarshalling of this table
			((ExcelSheet) openLFileManager.getSheet(policySheetName).considerRowsOnComparison(false)).getTable(OpenLFile.POLICY_HEADER_ROW_NUMBER, new HashSet<>(policyNumbers));
		}

		ExcelUnmarshaller eUnmarshaller = new ExcelUnmarshaller();
		OpenLFile<P> openLFile = eUnmarshaller.unmarshal(openLFileManager, openLFileModelClass, false, false);
		List<P> openLPoliciesList = CollectionUtils.isEmpty(policyNumbers)
				? openLFile.getPolicies()
				: openLFile.getPolicies().stream().filter(p -> policyNumbers.contains(p.getNumber())).collect(Collectors.toList());

		Map<P, Dollar> openLPoliciesAndPremiumsMap = new LinkedHashMap<>(openLPoliciesList.size());
		ExcelTable testsTable = openLFileManager.getSheet(OpenLFile.TESTS_SHEET_NAME).getTable(OpenLFile.TESTS_HEADER_ROW_NUMBER, new HashSet<>(policyNumbers));
		Dollar expectedPremium;
		for (P openLPolicy : openLPoliciesList) {
			TableRow row = testsTable.getRow("policy", openLPolicy.getNumber());
			if (row.hasColumn(OpenLTest.TOTAL_PREMIUM_COLUMN_NAME) && !row.isEmpty(OpenLTest.TOTAL_PREMIUM_COLUMN_NAME)) {
				expectedPremium = new Dollar(row.getValue(OpenLTest.TOTAL_PREMIUM_COLUMN_NAME));
			} else {
				expectedPremium = new Dollar(row.getSumContains("_res_.$Value"));
				if (openLPolicy.getTerm() == 6) {
					expectedPremium = expectedPremium.divide(2);
				}
			}
			openLPoliciesAndPremiumsMap.put(openLPolicy, expectedPremium);
		}
		openLFileManager.close();

		//Sort map by policies effective dates for further valid timeshifts
		openLPoliciesAndPremiumsMap = openLPoliciesAndPremiumsMap.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(OpenLPolicy::getEffectiveDate)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

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
