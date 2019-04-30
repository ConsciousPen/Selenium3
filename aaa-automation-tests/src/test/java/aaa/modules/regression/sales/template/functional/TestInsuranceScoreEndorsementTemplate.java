package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestInsuranceScoreEndorsement;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public abstract class TestInsuranceScoreEndorsementTemplate extends PolicyBaseTest {

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private ApplicantTab applicantTab = new ApplicantTab();
	private ReportsTab reportsTab = new ReportsTab();
	private BindTab bindTab = new BindTab();

	private List<TestData> insuredList;
	private String primaryInsuredFN;
	private String primaryInsuredLN;
	private String spouseFN;

	protected void testQualifiedNamedInsuredAddedOnMidTermEndorsement(String testData, String orderInsuranceScoreSpouse, String reorderAtRenewalSpouse, Boolean reorderAt36months){
		//Get names for named insureds
		insuredList = testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData(testData).getTestDataList("NamedInsured");
		primaryInsuredFN = insuredList.get(0).getValue("First name");
		primaryInsuredLN = insuredList.get(0).getValue("Last name");
		spouseFN = insuredList.get(1).getValue("First name");

		mainApp().open();
		TestData customerTd = getStateTestData(testDataManager.customer.get(CustomerType.INDIVIDUAL), "DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), primaryInsuredFN)
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), primaryInsuredLN);

		createCustomerIndividual(customerTd);

		String policyNumber = createPolicy();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime firstOrThirdExpirationDate = PolicySummaryPage.getExpirationDate();

		if (reorderAt36months) {
			LocalDateTime firstRenewalExpirationDate = createRenewal(policyNumber, firstOrThirdExpirationDate);
			createRenewal(policyNumber, firstRenewalExpirationDate);
		}

		//Create endorsement, add qualified named insured (spouse) and not qualified (e.g. child).
		TestData policyTD = getPolicyDefaultTD();
		policyTD.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName()),
				testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData(testData));
		policyTD.adjust(TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName()),
				testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData("TestData_Endorsement_ReportsTab"));

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.fillTab(policyTD);
		applicantTab.submitTab();
		reportsTab.fillTab(policyTD);

//		//In Insurance score report section can can only be seen Primary Insured and Spouse
//		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
//		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.TRUE);
//		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
//		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.FALSE);
//
//		//Insurance Score should be ordered only for Primary Insured
//		assertThat(reportsTab.tblInsuranceScoreOverride.getRowsCount()).isEqualTo(1);

		//User should not be able to order insurance score manually on midterm endorsement
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", primaryInsuredFN)
				.getCell("Report").getValue()).isNotEqualTo("Order report");
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
//				.getCell("Report")).isNotEqualTo("Order report");

		//PAS-27928 - Primary Insured - 'Order Insurance Score' should be automatically selected as 'Yes' and should be enabled
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", primaryInsuredFN)
//				.getCell("Order Insurance Score").controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", primaryInsuredFN)
//				.getCell("Order Insurance Score").controls.radioGroups.getFirst().isEnabled()).isEqualTo(Boolean.TRUE);
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", primaryInsuredFN)
//				.getCell("Reorder at renewal").controls.radioGroups.getFirst().getValue()).isEqualTo("No");
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", primaryInsuredFN)
//				.getCell("Reorder at renewal").controls.radioGroups.getFirst().isEnabled()).isEqualTo(Boolean.TRUE);

		//PAS-27382 - Spouse - 'Order Insurance Score' should be automatically selected as 'Yes' and should be enabled
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
				.getCell("Order Insurance Score").controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
				.getCell("Order Insurance Score").controls.radioGroups.getFirst().isEnabled()).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
				.getCell("Reorder at renewal").controls.radioGroups.getFirst().getValue()).isEqualTo("No");
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
				.getCell("Reorder at renewal").controls.radioGroups.getFirst().isEnabled()).isEqualTo(Boolean.TRUE);

		reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
				.getCell("Order Insurance Score").controls.radioGroups.getFirst().setValue(orderInsuranceScoreSpouse);
		reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
				.getCell("Reorder at renewal").controls.radioGroups.getFirst().setValue(reorderAtRenewalSpouse);

		reportsTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();

		firstOrThirdExpirationDate = PolicySummaryPage.getExpirationDate();

		createRenewal(policyNumber, firstOrThirdExpirationDate);
		LocalDateTime firstOrThirdRenewalEffectiveDate = PolicySummaryPage.getEffectiveDate();

		// Check insurance score details on renewal
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

		//In Insurance score report section can can only be seen Primary Insured and Spouse
		checkInsuranceScoreIsOnlyOrderedForPrimaryInsuredAndSpouse();

		//Check that insurance score order date is correct
		if (orderInsuranceScoreSpouse.equals("Decline")) {
			checkInsuranceScoreDate(primaryInsuredFN, policyEffectiveDate);
			assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
					.getCell("Order Date").getValue()).isEqualTo("");
			assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
					.getCell("Order Insurance Score").getValue()).isEqualTo("Decline");
			assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
					.getCell("Status").getValue()).isEqualTo("Declined");
		} else {
			checkInsuranceScoreDate(primaryInsuredFN, getTimePoints().getRenewOfferGenerationDate(firstOrThirdRenewalEffectiveDate));
			checkInsuranceScoreDate(spouseFN, getTimePoints().getRenewOfferGenerationDate(firstOrThirdRenewalEffectiveDate));
		}
	}

	private LocalDateTime createRenewal(String policyNumber, LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewInsuranceScoreReorderingDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);

		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_CREDIT_SCORE_BATCH);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);

		payTotalAmtDue(policyNumber);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		return PolicySummaryPage.getExpirationDate();
	}

	private void checkInsuranceScoreIsOnlyOrderedForPrimaryInsuredAndSpouse() {
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.TRUE);

		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.TRUE);
	}

	private void checkInsuranceScoreDate(String name, LocalDateTime date) {
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", name)
				.getCell("Order Date").getValue()).isEqualTo(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
	}
}