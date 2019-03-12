package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestInsuranceScoreRenewal;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;

import static toolkit.verification.CustomAssertions.assertThat;
import org.apache.commons.collections4.map.LinkedMap;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ArrayListMultimap;

public abstract class TestInsuranceScoreRenewalTemplate extends PolicyBaseTest {

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private ReportsTab reportsTab = new ReportsTab();

	private List<TestData> insuredList;
	private String primaryInsuredFN;
	private String spouseFN;
	private String childFN;
	private String parentFN;

	private int primaryInsuredInsuranceScoreShown;
	private int spouseInsuranceScoreShown;

	TreeMap<LocalDateTime, Integer> primaryInsuredInsuranceScoreList = new TreeMap<>();
	TreeMap<LocalDateTime, Integer> spouseInsuranceScoreList = new TreeMap<>();

	protected void testInsuranceScoreThenReorderedAtRenewal(String testData, int primaryInsuranceScoreNB, int spouseInsuranceScoreNB, int primaryInsuranceScoreAfterReordering, int spouseInsuranceScoreAfterReordering, Boolean reorderForPrimaryInsured, Boolean reorderForSpouse, Boolean reorderAt36months){
		setTestData(testData);

		mainApp().open();
		createCustomerIndividual();

		//Add 4 different insureds
		TestData policyTD = getPolicyDefaultTD();
		policyTD.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel()), insuredList);

		policy.initiate();
		policy.getDefaultView().fillUpTo(policyTD, ReportsTab.class, false);

		reportsTab.fillTab(policyTD);
		reportsTab.tblInsuranceScoreReport.getColumn("Report").getCell(2).controls.links.get("Order report").click();

		//In Insurance score report section can can only be seen Primary Insured and Spouse
		checkInsuranceScoreIsOnlyOrderedForPrimaryInsuredAndSpouse();

		checkInsuranceScore(primaryInsuredFN, primaryInsuranceScoreNB);
		checkInsuranceScore(spouseFN, spouseInsuranceScoreNB);

		//Reorder at renewal = Yes
		if (reorderForPrimaryInsured) {
			reorderAtRenewal(primaryInsuredFN);
		}
		if (reorderForSpouse) {
			reorderAtRenewal(spouseFN);
		}

		reportsTab.submitTab();
		policy.getDefaultView().fillFromTo(policyTD, PropertyInfoTab.class, PremiumsAndCoveragesQuoteTab.class, true);

		//Check which insurance score was used
		//It should use the best score from both named insured
		int insuranceScoreUsedInRating = (primaryInsuranceScoreNB > spouseInsuranceScoreNB) ? primaryInsuranceScoreNB : spouseInsuranceScoreNB;

		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score")).as("FR Score value is wrong in Rating Details").isEqualTo(String.valueOf(insuranceScoreUsedInRating));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		premiumsAndCoveragesQuoteTab.submitTab();

		//Finish creating Policy
		policy.getDefaultView().fillFromTo(policyTD, MortgageesTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		LocalDateTime firstRenewalExpirationDate;
		LocalDateTime secondRenewalExpirationDate;
		LocalDateTime firstRenewalEffectiveDate;
		LocalDateTime thirdRenewalEffectiveDate = null;

		//Create Renewal (Re-order at renewal, automatic reorder at 36 months and mixed scenarios work)
		if (reorderAt36months) {
			firstRenewalExpirationDate = createRenewal(policyNumber, policyExpirationDate);
			firstRenewalEffectiveDate = PolicySummaryPage.getEffectiveDate();
			secondRenewalExpirationDate = createRenewal(policyNumber, firstRenewalExpirationDate);
			createRenewal(policyNumber, secondRenewalExpirationDate);
			thirdRenewalEffectiveDate = PolicySummaryPage.getEffectiveDate();
		} else {
			createRenewal(policyNumber, policyExpirationDate);
			firstRenewalEffectiveDate = PolicySummaryPage.getEffectiveDate();
		}

		// Check insurance score details on renewal
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

		//In Insurance score report section can can only be seen Primary Insured and Spouse
		checkInsuranceScoreIsOnlyOrderedForPrimaryInsuredAndSpouse();

		checkInsuranceReportsDates(policyEffectiveDate, primaryInsuranceScoreNB, reorderForPrimaryInsured, firstRenewalEffectiveDate,
				primaryInsuranceScoreAfterReordering, spouseInsuranceScoreNB, reorderForSpouse, spouseInsuranceScoreAfterReordering,
				reorderAt36months, thirdRenewalEffectiveDate);

		checkInsuranceScore(primaryInsuredFN, primaryInsuredInsuranceScoreShown);
		checkInsuranceScore(spouseFN, spouseInsuranceScoreShown);

		//Check which insurance score was used in rating
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		//If Insurance Score for renewal is lower, system should leave higher score (from new business) for everything except VA
		int insuranceScoreUsedOnRenewal = (primaryInsuredInsuranceScoreShown > spouseInsuranceScoreShown) ? primaryInsuredInsuranceScoreShown : spouseInsuranceScoreShown;

		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score")).as("FR Score value is wrong in Rating Details").isEqualTo(String.valueOf(insuranceScoreUsedOnRenewal));
	}

	private void checkInsuranceReportsDates(LocalDateTime policyEffectiveDate, int primaryInsuranceScoreNB, Boolean reorderForPrimaryInsured,
			LocalDateTime firstRenewalEffectiveDate, int primaryInsuranceScoreAfterReordering, int spouseInsuranceScoreNB, Boolean reorderForSpouse,
			int spouseInsuranceScoreAfterReordering, Boolean reorderAt36months, LocalDateTime thirdRenewalEffectiveDate) {
		//Make Map for Insurance score Report Date
		primaryInsuredInsuranceScoreList.put(policyEffectiveDate, primaryInsuranceScoreNB);
		spouseInsuranceScoreList.put(policyEffectiveDate, spouseInsuranceScoreNB);

		if (reorderForPrimaryInsured) {
			primaryInsuredInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(firstRenewalEffectiveDate), primaryInsuranceScoreAfterReordering);
		}
		if (reorderForSpouse) {
			spouseInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(firstRenewalEffectiveDate), spouseInsuranceScoreAfterReordering);
		}

		//For VA only NEWEST term applies (for named insured which had their insurance score report ordered)
		//For CW BEST score from all terms
		getInsuranceScoreWhichWillBeShown();

		if (reorderAt36months) {
			get36MonthsAutomaticReorderLogic(thirdRenewalEffectiveDate,
					primaryInsuranceScoreAfterReordering, spouseInsuranceScoreAfterReordering);
		}

		//Check that insurance score order date is correct
		checkInsuranceScoreDate(primaryInsuredFN, primaryInsuredInsuranceScoreList.lastKey());
		checkInsuranceScoreDate(spouseFN, spouseInsuranceScoreList.lastKey());
	}

	private void get36MonthsAutomaticReorderLogic(LocalDateTime thirdRenewalEffectiveDate,
			int primaryInsuranceScoreAfterReordering, int spouseInsuranceScoreAfterReordering) {
		//Need to check for who - Primary Insured or Spouse automatic renewal will be ordered
		//VA chooses best score from NEWEST term
		if (getState().equals(Constants.States.VA)) {
			if (primaryInsuredInsuranceScoreList.lastKey().isAfter(spouseInsuranceScoreList.lastKey())) {
				primaryInsuredInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), primaryInsuranceScoreAfterReordering);
				getInsuranceScoreWhichWillBeShown();
			} else if (primaryInsuredInsuranceScoreList.lastKey().isBefore(spouseInsuranceScoreList.lastKey())) {
				spouseInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), spouseInsuranceScoreAfterReordering);
				getInsuranceScoreWhichWillBeShown();
			} else {
				//if equal need to compare which named insured had best score - for it automatic reorder will be done.
				if (primaryInsuredInsuranceScoreShown > spouseInsuranceScoreShown) {
					primaryInsuredInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), primaryInsuranceScoreAfterReordering);
					getInsuranceScoreWhichWillBeShown();
				} else if (primaryInsuredInsuranceScoreShown < spouseInsuranceScoreShown) {
					spouseInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), spouseInsuranceScoreAfterReordering);
					getInsuranceScoreWhichWillBeShown();
				} else {
					//if order dates AND score is equal it should order for oldest one. From testdata it is Spouse
					spouseInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), spouseInsuranceScoreAfterReordering);
					getInsuranceScoreWhichWillBeShown();
				}
			}
			//CW chooses BEST score from all terms
		} else {
			if (primaryInsuredInsuranceScoreShown > spouseInsuranceScoreShown) {
				primaryInsuredInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), primaryInsuranceScoreAfterReordering);
				getInsuranceScoreWhichWillBeShown();
			} else if (primaryInsuredInsuranceScoreShown < spouseInsuranceScoreShown) {
				spouseInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), spouseInsuranceScoreAfterReordering);
				getInsuranceScoreWhichWillBeShown();
			} else {
				//if order dates AND score is equal it should order for oldest one. From testdata it is Spouse
				spouseInsuranceScoreList.put(getTimePoints().getRenewOfferGenerationDate(thirdRenewalEffectiveDate), spouseInsuranceScoreAfterReordering);
				getInsuranceScoreWhichWillBeShown();
			}
		}
	}

	private void getInsuranceScoreWhichWillBeShown() {
		//Check which insurance score was used
		//For VA it should pick up only the NEWEST insurance score from the NEWEST term
		//If Only one named insured is getting new insurance score, other named insured should be ignored.
		if (getState().equals(Constants.States.VA)) {
			while (primaryInsuredInsuranceScoreList.size() > 1) {
				primaryInsuredInsuranceScoreList.pollFirstEntry();
			}
			while (spouseInsuranceScoreList.size() > 1) {
				spouseInsuranceScoreList.pollFirstEntry();
			}

			primaryInsuredInsuranceScoreShown = primaryInsuredInsuranceScoreList.firstEntry().getValue();
			spouseInsuranceScoreShown = spouseInsuranceScoreList.firstEntry().getValue();

			//For everything else other than VA it should pick up the BEST score from all terms and show it.
		} else {
			for (Map.Entry<LocalDateTime, Integer> entry : primaryInsuredInsuranceScoreList.entrySet()) {
				if (primaryInsuredInsuranceScoreShown < entry.getValue()) {
					primaryInsuredInsuranceScoreShown = entry.getValue();
				}
			}
			for (Map.Entry<LocalDateTime, Integer> entry : spouseInsuranceScoreList.entrySet()) {
				if (spouseInsuranceScoreShown < entry.getValue()) {
					spouseInsuranceScoreShown = entry.getValue();
				}
			}
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

	private void reorderAtRenewal(String name) {
		reportsTab.tblInsuranceScoreReport.getRowContains("Named Insured", name)
				.getCell("Reorder at renewal").controls.radioGroups.get(1).setValue("Yes");
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

	private void setTestData(String testData) {
		insuredList = testDataManager.getDefault(TestInsuranceScoreRenewal.class).getTestData(testData).getTestDataList("NamedInsured");
		primaryInsuredFN = insuredList.get(0).getValue("First name");
		spouseFN = insuredList.get(1).getValue("First name");
		childFN = insuredList.get(2).getValue("First name");
		parentFN = insuredList.get(3).getValue("First name");
	}
}