package aaa.modules.regression.document_fulfillment.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.DocumentDataElement;
import aaa.helpers.xml.model.DocumentPackage;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestDecTransactionDateTime extends AutoSSBaseTest {

	/**
	 * @author Maris Strazds
	 * @name DecPage BOUND_DT content check
	 * @scenario
	 * 1. Create customer
	 * 2. Create a policy
	 * 3. Check AA02VA contains BOUND_DT with transaction date and time and the date and time are Arizona time zones (server time zone)
	 * 4. Make Endorsement
	 * 5. Check AA02VA contains BOUND_DT with transaction date and time and the date and time are Arizona time zones (server time zone)
	 * 6. Generate Renewal document package at Renewal offer generation date
	 * 7. Check AA02VA contains BOUND_DT with transaction date and time and the date and time are Arizona time zones (server time zone) (transaction date is renewal Image generation Date)
	 * 8. Retrieve renewal image in data gathering mode and make changes to it
	 * 9. Check AA02VA contains BOUND_DT with transaction date and time and the date and time are Arizona time zones (server time zone)
	 * 10. Revise renewal image by making endorsement to current term
	 * 11. Check AA02VA contains BOUND_DT with transaction date and time and the date and time are Arizona time zones (server time zone)
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_SS, testCaseId = "PAS-14498")
	public void pas14498_TestDecTransactionDateTime(@Optional("VA") String state) {
		TestData testData = getPolicyDefaultTD();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.PolicyInformation.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
				"$<today+2d>"); //using future dated policy effective date to make sure that transaction date not policy effective date is used in XML

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(testData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_PENDING);
		validateBoundDtTag(policyNumber, POLICY_ISSUE);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob); //Policy status changes to Active.

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
		endorsementSteps();
		validateBoundDtTag(policyNumber, ENDORSEMENT_ISSUE);

		////////Renewals//////////
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		LocalDateTime renewalProposalDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);

		//Generate renewal image
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		LocalDateTime renewImageGenDateTime = TimeSetterUtil.getInstance().getCurrentTime();

		TimeSetterUtil.getInstance().nextPhase(renewalProposalDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		//validateBoundDtTag(policyNumber, RENEWAL_OFFER); //uncomment this line (use this method) if requirements change to use Renewal offer generation Date and Time
		validateBoundDtTagBatch(policyNumber, RENEWAL_OFFER, renewImageGenDateTime); //remove this line and delete method if requirements change to use Renewal offer generation Date and Time

		//10. Revise renewal image by making endorsement to current term
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2)); //changing date to make sure that transaction date not Renewal offer generation date is used in XML
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		endorsementSteps();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1); // needed to generate renewal offer xmls
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2); // needed to generate renewal offer xmls
		validateBoundDtTag(policyNumber, RENEWAL_OFFER);
		validateBoundDtTag(policyNumber, ENDORSEMENT_ISSUE);

		//8. Retrieve renewal image in data gathering mode and make changes to it
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1)); //changing date to make sure that transaction date not Renewal offer generation date is used in XML
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		endorsementSteps();
		validateBoundDtTag(policyNumber, RENEWAL_OFFER);

	}

	private void endorsementSteps() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//make some changes
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		if (!premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getValue().contains("300,000")) {
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValue("contains=300,000");
		} else {
			premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValue("contains=500,000");
		}

		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new BindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	// BOUND_DT example: <aaan:DateTimeField>2018-07-06T12:28:11.491-07:00</aaan:DateTimeField>
	private void validateBoundDtTag(String policyNumber, AaaDocGenEntityQueries.EventNames eventName) {
		DocumentPackage documentPackage = DocGenHelper.getDocumentPackage(policyNumber, eventName);
		DocumentDataElement filteredDataElement = documentPackage.getArchiveData().getDocumentDataElements().stream().filter(dataElement -> "BOUND_DT".equals(dataElement.getName())).findFirst().orElse(null);
		String boundDtActualValue = filteredDataElement.getDataElementChoice().getDateTimeField();

		ZonedDateTime zonedDateTimeXmlField = ZonedDateTime.parse(boundDtActualValue).truncatedTo(ChronoUnit.HOURS);
		ZoneOffset zoneoffset = zonedDateTimeXmlField.getOffset(); //get timezone from the xml field
		ZonedDateTime zonedSystemDateTime = TimeSetterUtil.getInstance().getCurrentTime().atZone(zoneoffset).truncatedTo(ChronoUnit.HOURS);

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(zonedDateTimeXmlField.equals(zonedSystemDateTime)).isTrue();
			softly.assertThat(boundDtActualValue.length()).isEqualTo(29);
		});
	}

	private void validateBoundDtTagBatch(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, LocalDateTime renewImageGenDateTime) {
		DocumentPackage documentPackage = DocGenHelper.getDocumentPackage(policyNumber, eventName);
		DocumentDataElement filteredDataElement = documentPackage.getArchiveData().getDocumentDataElements().stream().filter(dataElement -> "BOUND_DT".equals(dataElement.getName())).findFirst().orElse(null);
		String boundDtActualValue = filteredDataElement.getDataElementChoice().getDateTimeField();

		ZonedDateTime zonedDateTimeXmlField = ZonedDateTime.parse(boundDtActualValue).truncatedTo(ChronoUnit.HOURS);
		ZoneOffset zoneoffset = zonedDateTimeXmlField.getOffset(); //get timezone from the xml field
		ZonedDateTime zonedSystemDateTime = renewImageGenDateTime.atZone(zoneoffset).truncatedTo(ChronoUnit.HOURS);

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(zonedDateTimeXmlField.equals(zonedSystemDateTime)).isTrue();
			softly.assertThat(boundDtActualValue.length()).isEqualTo(29);
		});
	}
}
