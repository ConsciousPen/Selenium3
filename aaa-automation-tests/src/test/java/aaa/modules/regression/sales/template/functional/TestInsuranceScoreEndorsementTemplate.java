package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.jobs.JobUtils;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.CustomerType;
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
	private String primaryInsuredLN;
	private String spouseFN;
	private String childFN;
	private String parentFN;

	private int insuranceScoreUsedInRating;

	protected void testQualifiedNamedInsuredAddedOnMidTermEndorsement(String orderInsuranceScoreSpouse, String reorderAtRenewalSpouse, Boolean reorderAt36months){
		//Get names for named insureds
		insuredList = testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData("TestData_EndorsementWithQualifiedNamedInsured").getTestDataList("NamedInsured");
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
				testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData("TestData_EndorsementWithQualifiedNamedInsured"));
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
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);

		//User should not be able to order insurance score manually on midterm endorsement
		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", primaryInsuredFN)
				.getCell("Report").getValue()).isNotEqualTo("Order report");
//		assertThat(reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", spouseFN)
//				.getCell("Report")).isNotEqualTo("Order report");

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

	protected void testQualifiedNamedInsuredDeletedOnMidTermEndorsement(String testData, int primaryInsuranceScoreNB, int primaryInsuranceScoreRenewal, int spouseInsuranceScoreNB){
		insuredList = testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData(testData).getTestDataList("NamedInsured");
		primaryInsuredFN = insuredList.get(0).getValue("First name");
		primaryInsuredLN = insuredList.get(0).getValue("Last name");
		spouseFN = insuredList.get(1).getValue("First name");
		childFN = insuredList.get(2).getValue("First name");
		parentFN = insuredList.get(3).getValue("First name");

		mainApp().open();
		TestData customerTd = getStateTestData(testDataManager.customer.get(CustomerType.INDIVIDUAL), "DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), primaryInsuredFN)
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), primaryInsuredLN);

		createCustomerIndividual(customerTd);

		//Add 4 different insureds
		TestData policyTD = getPolicyDefaultTD();
		policyTD.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel()), insuredList);

		String policyNumber = createPolicy(policyTD, primaryInsuranceScoreNB, spouseInsuranceScoreNB);
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Create endorsement and remove qualified named insured used in rating during NB
		policyTD.adjust(TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName()),
				testDataManager.getDefault(TestInsuranceScoreEndorsement.class).getTestData("TestData_Endorsement_ReportsTab"));

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Day"));

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.tblInsuredList.getRowContains("First name", spouseFN).getCell("Modify").controls.links
				.get("Remove").click();
		Page.dialogConfirmation.confirm();
		applicantTab.submitTab();

		//In Insurance score report section can can only be seen Primary Insured and Spouse
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreReport.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.FALSE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(primaryInsuredFN)).isEqualTo(Boolean.TRUE);
		assertThat(reportsTab.tblInsuranceScoreOverride.getValue().toString().contains(spouseFN)).isEqualTo(Boolean.FALSE);

		//Insurance Score should be ordered only for Primary Insured
		assertThat(reportsTab.tblInsuranceScoreOverride.getRowsCount()).isEqualTo(1);

		reportsTab.submitTab();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		//PAS-29054 - FR score (insurance score) should keep same score as in NB even though we deleted qualified named insured used in rating
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score")).as("FR Score value is wrong in Rating Details").isEqualTo(String.valueOf(insuranceScoreUsedInRating));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		premiumsAndCoveragesQuoteTab.submitTab();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();

		//Create Renewal
		createRenewal(policyNumber, policyExpirationDate);

		// Check insurance score details on renewal
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

		//Check that insurance score and order date is correct
		//If spouse was NOT used in rating during NB, no new report should be ordered
		if (primaryInsuranceScoreNB > spouseInsuranceScoreNB) {
			checkInsuranceScoreDate(primaryInsuredFN, policyEffectiveDate);
			checkInsuranceScore(primaryInsuredFN, primaryInsuranceScoreNB);
		} else {
			//If spouse WAS used in rating during NB, NEW report should be ordered
			checkInsuranceScoreDate(primaryInsuredFN, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
			checkInsuranceScore(primaryInsuredFN, primaryInsuranceScoreRenewal);
			insuranceScoreUsedInRating = (primaryInsuranceScoreNB > primaryInsuranceScoreRenewal) ? primaryInsuranceScoreNB : primaryInsuranceScoreRenewal;
		}

		premiumsAndCoveragesQuoteTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score")).as("FR Score value is wrong in Rating Details").isEqualTo(String.valueOf(insuranceScoreUsedInRating));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
	}

	private String createPolicy(TestData policyTD, int primaryInsuranceScoreNB, int spouseInsuranceScoreNB) {
		policy.initiate();
		policy.getDefaultView().fillUpTo(policyTD, ReportsTab.class, false);

		reportsTab.fillTab(policyTD);
		reportsTab.tblInsuranceScoreReport.getColumn("Report").getCell(2).controls.links.get("Order report").click();

		//In Insurance score report section can can only be seen Primary Insured and Spouse
		checkInsuranceScoreIsOnlyOrderedForPrimaryInsuredAndSpouse();

		checkInsuranceScore(primaryInsuredFN, primaryInsuranceScoreNB);
		checkInsuranceScore(spouseFN, spouseInsuranceScoreNB);

		reportsTab.submitTab();
		policy.getDefaultView().fillFromTo(policyTD, PropertyInfoTab.class, PremiumsAndCoveragesQuoteTab.class, true);

		//Check which insurance score was used
		//It should use the best score from both named insured
		insuranceScoreUsedInRating = (primaryInsuranceScoreNB > spouseInsuranceScoreNB) ? primaryInsuranceScoreNB : spouseInsuranceScoreNB;

		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score")).as("FR Score value is wrong in Rating Details").isEqualTo(String.valueOf(insuranceScoreUsedInRating));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		premiumsAndCoveragesQuoteTab.submitTab();

		//Finish creating Policy
		policy.getDefaultView().fillFromTo(policyTD, MortgageesTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		return PolicySummaryPage.getPolicyNumber();
	}

	private void checkInsuranceScore(String name, int insuranceScore) {
		assertThat(reportsTab.tblInsuranceScoreOverride.getRowContains("Named Insured", name)
				.getCell("Insurance Score").getValue()).isEqualTo(String.valueOf(insuranceScore));
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