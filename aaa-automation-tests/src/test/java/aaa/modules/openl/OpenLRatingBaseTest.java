package aaa.modules.openl;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import aaa.modules.policy.PolicyBaseTest;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.CellsQueue;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public abstract class OpenLRatingBaseTest<P extends OpenLPolicy> extends PolicyBaseTest {
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
		return getPolicyTD();
	}

	protected <O extends OpenLFile<P>> void verifyPremiums(String openLFileName, Class<O> openLFileModelClass, TestDataGenerator<P> tdGenerator, List<Integer> policyNumbers) {
		//TODO-dchubkov: assert that date in openLFileName is valid
		List<P> openLPolicies = getOpenLPolicies(openLFileName, openLFileModelClass, policyNumbers);

		mainApp().open();
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

				String actualPremium = createAndRateQuote(tdGenerator, openLPolicy);
				String expectedPremium = openLPolicy.getExpectedPremium().toString();
				softly.assertThat(actualPremium).as("Total premium is not equal to expected one").isEqualTo(expectedPremium);
				log.info("Premium calculation verification for policy #{} has been {}", Tab.labelPolicyNumber.getValue(), actualPremium.equals(expectedPremium) ? "passed" : "failed");
				Tab.buttonSaveAndExit.click();
			}
		});
	}

	protected abstract String createAndRateQuote(TestDataGenerator<P> tdGenerator, P openLPolicy);

	protected <O extends OpenLFile<P>> List<P> getOpenLPolicies(String openLFileName, Class<O> openLFileModelClass, List<Integer> policyNumbers) {
		ExcelManager openLFileManager = new ExcelManager(new File(getTestsDir() + "/" + openLFileName));

		if (CollectionUtils.isNotEmpty(policyNumbers)) {
			String policySheetName = OpenLFile.POLICY_SHEET_NAME;
			if (getPolicyType().equals(PolicyType.AUTO_SS)) {
				policySheetName = AutoSSOpenLFile.POLICY_SHEET_NAME;
			} else if (getPolicyType().equals(PolicyType.PUP)) {
				policySheetName = PUPOpenLFile.PUP_POLICY_SHEET_NAME;
			}
			// Find policies table and exclude not needed test rows and store it in ExcelManager instance to reduce time required for further excel unmarshalling of this table
			ExcelTable policiesTable = ((ExcelSheet) openLFileManager.getSheet(policySheetName).considerRowsOnComparison(false)).getTable(OpenLFile.POLICY_HEADER_ROW_NUMBER);
			Integer[] rowsIndexesToExclude = policiesTable.getRows().stream().filter(r -> !policyNumbers.contains(r.getIntValue(OpenLFile.PRIMARY_KEY_COLUMN_NAME))).map(CellsQueue::getIndex).toArray(Integer[]::new);
			policiesTable.excludeRows(rowsIndexesToExclude);
		}

		ExcelUnmarshaller eUnmarshaller = new ExcelUnmarshaller();
		OpenLFile<P> openLFile = eUnmarshaller.unmarshal(openLFileManager, openLFileModelClass, false, false);

		List<P> openLPoliciesList = getOpenLPoliciesWithExpectedPremiums(openLFileManager, openLFile);
		openLFileManager.close();
		assertThat(openLPoliciesList).as("Found policy objects amount is not equal to number of policies to be tested. Probably excel file has missed tests").hasSameSizeAs(policyNumbers);

		//Sort policies list by effective date for further valid time shifts
		openLPoliciesList = openLPoliciesList.stream().sorted(Comparator.comparing(OpenLPolicy::getEffectiveDate)).collect(Collectors.toList());
		return openLPoliciesList;
	}

	protected List<P> getOpenLPoliciesWithExpectedPremiums(ExcelManager openLFileManager, OpenLFile<P> openLFile) {
		ExcelTable testsTable = openLFileManager.getSheet(openLFile.getTestsSheetName()).getTable(OpenLFile.TESTS_HEADER_ROW_NUMBER);
		List<P> openLPoliciesList = openLFile.getPolicies();
		for (P openLPolicy : openLPoliciesList) {
			Dollar expectedPremium;
			TableRow row = testsTable.getRow(openLFile.getTestsPolicyHeaderColumnName(), openLPolicy.getNumber());
			if (row.hasColumn(OpenLTest.TOTAL_PREMIUM_COLUMN_NAME) && !row.isEmpty(OpenLTest.TOTAL_PREMIUM_COLUMN_NAME)) {
				expectedPremium = new Dollar(row.getValue(OpenLTest.TOTAL_PREMIUM_COLUMN_NAME));
			} else {
				expectedPremium = new Dollar(row.getSumContains("_res_"));
				if (openLPolicy.getTerm() == 6) {
					expectedPremium = expectedPremium.divide(2);
				}
			}
			openLPolicy.setExpectedPremium(expectedPremium);
		}
		return openLPoliciesList;
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
