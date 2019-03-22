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
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestInsuranceScoreEndorsement;
import toolkit.datax.TestData;

public abstract class TestInsuranceScoreEndorsementTemplate extends PolicyBaseTest {

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private ApplicantTab applicantTab = new ApplicantTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private ReportsTab reportsTab = new ReportsTab();
	private BindTab bindTab = new BindTab();

	private List<TestData> insuredList;
	private String primaryInsuredFN;
	private String spouseFN;
	private String childFN;
	private String parentFN;

	private int primaryInsuredInsuranceScoreShown;
	private int spouseInsuranceScoreShown;

	TreeMap<LocalDateTime, Integer> primaryInsuredInsuranceScoreList = new TreeMap<>();
	TreeMap<LocalDateTime, Integer> spouseInsuranceScoreList = new TreeMap<>();

	//TODO Rokas Lazdauskas: due to many blocking production issues found, this story is on hold. Need cleanup and add assertions for full coverage.
	protected void testQualifiedNamedInsuredAddedOnMidTermEndorsement(String testData, int primaryInsuranceScoreNB, int spouseInsuranceScoreNB, int primaryInsuranceScoreAfterReordering, int spouseInsuranceScoreAfterReordering, Boolean reorderForPrimaryInsured, Boolean reorderForSpouse, Boolean reorderAt36months){
		//Get names for named insureds
		insuredList = testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData(testData).getTestDataList("NamedInsured");
		primaryInsuredFN = insuredList.get(0).getValue("First name");
		spouseFN = insuredList.get(1).getValue("First name");
		childFN = insuredList.get(2).getValue("First name");
		parentFN = insuredList.get(3).getValue("First name");

		mainApp().open();
		createCustomerIndividual();

		createPolicy();

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

		//In Insurance score report section can can only be seen Primary Insured and Spouse
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(childFN)).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(parentFN)).isEqualTo(Boolean.FALSE);

		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(childFN)).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(parentFN)).isEqualTo(Boolean.FALSE);

		//Insurance Score should be ordered only for Primary Insured
		assertThat(reportsTab.tblInsuranceScoreOverride.getRowsCount()).isEqualTo(1);

		//Additional Qualified Named insureds Insurance score report is NOT ordered on midterm endorsement
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
//				.getCell("Report").isEnabled()).isEqualTo("Decline");
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
//				.getCell("Order Insurance Score").isEnabled()).isEqualTo(Boolean.FALSE);

		reportsTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime firstRenewalEffectiveDate;

		createRenewal(policyNumber, policyExpirationDate);
		firstRenewalEffectiveDate = PolicySummaryPage.getEffectiveDate();

		// Check insurance score details on renewal
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

		//In Insurance score report section can can only be seen Primary Insured and Spouse
		checkInsuranceScoreIsOnlyOrderedForPrimaryInsuredAndSpouse();

		//Check that insurance score order date is correct
		checkInsuranceScoreDate(primaryInsuredFN, getTimePoints().getRenewOfferGenerationDate(firstRenewalEffectiveDate));
		checkInsuranceScoreDate(spouseFN, getTimePoints().getRenewOfferGenerationDate(firstRenewalEffectiveDate));
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
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(childFN)).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(parentFN)).isEqualTo(Boolean.FALSE);

		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(childFN)).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(parentFN)).isEqualTo(Boolean.FALSE);
	}

	private void checkInsuranceScore(String name, int insuranceScore) {
		assertThat(reportsTab.tblInsuranceScoreOverride.getRowContains("Named Insured", name)
				.getCell("Insurance Score").getValue()).isEqualTo(String.valueOf(insuranceScore));
	}

	private void checkInsuranceScoreDate(String name, LocalDateTime date) {
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", name)
				.getCell("Order Date").getValue()).isEqualTo(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
	}
}