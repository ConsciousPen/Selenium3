package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.products.HomeSSConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.conversions.home_ss.dp3.functional.TestSpecialNonRenewalLetterKY;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestInsuranceScoreNB;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import toolkit.datax.TestData;

import java.util.HashMap;
import java.util.Map;

import static toolkit.verification.CustomAssertions.assertThat;

public abstract class TestInsuranceScoreNBTemplate extends PolicyBaseTest {

	protected ReportsTab reportsTab = new ReportsTab();

	protected void testInsuranceScoreNB(){
		mainApp().open();
		createCustomerIndividual();

		TestData policyTD = getPolicyDefaultTD();
		policyTD.adjust(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
				testDataManager.getDefault(TestInsuranceScoreNB.class).getTestDataList("TestData_TwoSpouses_TwoOthers"));

		policy.initiate();
		policy.getDefaultView().fillUpTo(policyTD, ReportsTab.class, true);

		//check that only legit members are shown
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains(HomeSSConstants.InsuranceScoreReportTable.NAMED_INSURED, "")).isEqualTo(BillingConstants.BillsAndStatementsType.BILL);
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains(HomeSSConstants.InsuranceScoreReportTable.NAMED_INSURED, "")).isEqualTo(BillingConstants.BillsAndStatementsType.BILL);

		reportsTab.submitTab();
		policy.getDefaultView().fillFromTo(policyTD, PropertyInfoTab.class, BindTab.class, true);

		//check the popup
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains(HomeSSConstants.InsuranceScoreReportTable.NAMED_INSURED, "")).isEqualTo(BillingConstants.BillsAndStatementsType.BILL);

		//check that Error page does not show up as rule which requires all insureds insurance score report ordered

	}
}