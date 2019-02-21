package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.products.HomeSSConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.conversions.home_ss.dp3.functional.TestSpecialNonRenewalLetterKY;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestInsuranceScoreNB;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import toolkit.datax.TestData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static toolkit.verification.CustomAssertions.assertThat;

public abstract class TestInsuranceScoreNBTemplate extends PolicyBaseTest {

	private List<TestData> insuredList = testDataManager.getDefault(TestInsuranceScoreNB.class).getTestData("ApplicantTab_OneSpouse_OneChild").getTestDataList("NamedInsured");

	protected PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	protected ApplicantTab applicantTab = new ApplicantTab();
	protected ReportsTab reportsTab = new ReportsTab();
	protected ErrorTab errorTab = new ErrorTab();
	protected BindTab bindTab = new BindTab();

	protected void testInsuranceScoreNB(){
		mainApp().open();
		createCustomerIndividual();

		//Add 4 different insureds
		TestData policyTD = getPolicyDefaultTD();
		policyTD.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel()), insuredList);

		policy.initiate();
		policy.getDefaultView().fillUpTo(policyTD, ReportsTab.class, false);

		//Insurance score report can be only ordered for: Primary Insured, Spouse and Domestic Partnership (in states where it is applicable)
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(insuredList.get(0).getValue("First name"))).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(insuredList.get(1).getValue("First name"))).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(insuredList.get(2).getValue("First name"))).isEqualTo(Boolean.FALSE);

		//Do not order for Spouse
		reportsTab.fillTab(policyTD);

		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(insuredList.get(0).getValue("First name"))).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(insuredList.get(1).getValue("First name"))).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(insuredList.get(2).getValue("First name"))).isEqualTo(Boolean.FALSE);

		reportsTab.submitTab();
		policy.getDefaultView().fillFromTo(policyTD, PropertyInfoTab.class, BindTab.class, true);
		bindTab.submitTab();

		//Check that Error page appears
		assertThat(errorTab.isVisible()).isEqualTo(Boolean.TRUE);
		assertThat(errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_HO_SS67cbad46.getCode())).isEqualTo(Boolean.TRUE);

		//Go to Applicant tab and add another insured
		errorTab.cancel();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.fillTab(testDataManager.getDefault(TestInsuranceScoreNB.class).getTestData("TestData_NamedInsuredOther"));
		applicantTab.submitTab();

		//Check that in Reports tab still only Primary insured and Spouse is available
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(insuredList.get(0).getValue("First name"))).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(insuredList.get(1).getValue("First name"))).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(insuredList.get(2).getValue("First name"))).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(testDataManager.getDefault(TestInsuranceScoreNB.class)
				.getTestData("TestData_NamedInsuredOther").getTestDataList("ApplicantTab").get(0)
				.getTestDataList("NamedInsured").get(0).getValue("First name"))).isEqualTo(Boolean.FALSE);

		//Order report for spouse
		reportsTab.reorderReports();
		reportsTab.tblInsuranceScoreReport.getColumn("Report").getCell(2).controls.links.get("Order report").click();
		reportsTab.tblClueReport.getColumn("Report").getCell(1).controls.links.get("Order report").click();

		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(insuredList.get(0).getValue("First name"))).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(insuredList.get(1).getValue("First name"))).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(insuredList.get(2).getValue("First name"))).isEqualTo(Boolean.FALSE);

		//check that Error page does not show up as rule which requires all insureds insurance score report ordered
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();
		assertThat(errorTab.isVisible()).isEqualTo(Boolean.FALSE);
	}

	protected void testInsuranceScoreNBMD() {
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyDefaultTD(), ReportsTab.class, false);
		assertThat(reportsTab.tblInsuranceScoreReport.isPresent()).isEqualTo(Boolean.FALSE);
	}

}