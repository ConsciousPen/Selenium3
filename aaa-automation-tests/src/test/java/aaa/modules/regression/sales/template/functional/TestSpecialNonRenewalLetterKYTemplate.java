/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.DocumentPackage;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.conversions.home_ss.dp3.functional.TestSpecialNonRenewalLetterKY;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigConversionHomeAbstract;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestSpecialNonRenewalLetterKYTemplate extends TestMaigConversionHomeAbstract {

	protected void specialNonRenewalLetterBeforeR80Generated(Boolean isOtherActivePolicyPup) throws NoSuchFieldException {
		mainApp().open();
		createCustomerIndividual();

		TestData policyTestData = getConversionPolicyDefaultTD();

		//This will affect content in XML -> 'PupCvrgYN' will have Y or N depending on this adjust
		if (isOtherActivePolicyPup) {
			policyTestData.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(),
					HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()),
					testDataManager.getDefault(TestSpecialNonRenewalLetterKY.class).getTestDataList("OtherActiveAAAPolicies"));
		}

		//Conversion Policy should be created earlier than Conversion Specific Special Non-renewal letter date R-80
		String conversionRenewalEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(81).format(DateTimeUtils.MM_DD_YYYY);

		//Create Conversion Policy at R-81
		String policyNumber = createConversionPolicy(getManualConversionInitiationTd()
						.adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
						CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), conversionRenewalEffectiveDate),
				policyTestData);
		LocalDateTime conversionExpDate = PolicySummaryPage.getExpirationDate();

		//Try to generate Conversion Specific Special Non-renewal letter (FORM# HSSNRKY 01 18) (document should not be generated)
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);

		//Check that document is not generated - special conversion non renewal letter for KY (HSSNRKY)
		DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.HSSNRKYXX, policyNumber, PRE_RENEWAL, false);
		//Check that organic letter is supressed on conversion policy creation (HSRNKY)
		DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.HSRNKY, policyNumber, PRE_RENEWAL, false);

		//Move time to R-80 and try generating document
		runPreRenewalNoticeJob(conversionExpDate.minusDays(80));

		//Check that 'HSSNRKY' document is generated.
		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.HSSNRKYXX, policyNumber, PRE_RENEWAL);
		assertThat(getPolicyTransactionCodes(policyNumber, PRE_RENEWAL)).contains("MCON");

		// 'PupCvrgYN' will have Y or N depending on policy was created with PUP endorsement or not
		if (isOtherActivePolicyPup) {
			verifyTagData(document, "PupCvrgYN", "Y");
		} else {
			verifyTagData(document, "PupCvrgYN", "N");
		}

		//Check that organic letter is supressed on conversion policy creation (HSRNKY)
		DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.HSRNKY, policyNumber, PRE_RENEWAL, false);

		proposeAndPurchaseConversionPolicy(conversionExpDate, policyNumber);
		LocalDateTime secondPolicyExpirationDate = PolicySummaryPage.getExpirationDate();

		runPreRenewalNoticeJob(secondPolicyExpirationDate.minusDays(80));

		//Check that document is not generated (Conversion Specific Special Non-renewal letter is not sent on organic Renewal)
		assertThat(DocGenHelper.waitForMultipleDocumentsAppearanceInDB(DocGenEnum.Documents.HSSNRKYXX, policyNumber, PRE_RENEWAL).size()).isEqualTo(1);
	}

	protected void specialNonRenewalLetterAfterR80NotGenerated() {
		String policyNumber = createConversionPolicyAtR81();
		LocalDateTime conversionExpDate = PolicySummaryPage.getExpirationDate();
		specialNonRenewalLetterIsNotGenerated(conversionExpDate.minusDays(79), policyNumber);
	}

	protected void specialNonRenewalLetterR80PUPNotGenerated() {
		String policyNumber = createConversionPolicyAtR81();
		LocalDateTime conversionExpDate = PolicySummaryPage.getExpirationDate();
		specialNonRenewalLetterIsNotGenerated(conversionExpDate.minusDays(80), policyNumber);
	}

	private String createConversionPolicyAtR81() {
		mainApp().open();
		createCustomerIndividual();

		//Conversion Policy should be created earlier than Conversion Specific Special Non-renewal letter date R-80
		String conversionRenewalEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(81).format(DateTimeUtils.MM_DD_YYYY);

		//Create Conversion Policy at R-81
		return createConversionPolicy(getManualConversionInitiationTd()
						.adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(),
								CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), conversionRenewalEffectiveDate),
				getConversionPolicyDefaultTD());
	}

	private void specialNonRenewalLetterIsNotGenerated(LocalDateTime timeline, String policyNumber) {
		//Change time to timeline (R-79 for home policies, R-80 for PUP) ad try generating document (document should not be generated)
		runPreRenewalNoticeJob(timeline);

		//Check that document is not generated - special conversion non renewal letter for KY (HSSNRKY)
		DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.HSSNRKYXX, policyNumber, PRE_RENEWAL, false);
		//Check that organic letter is supressed on conversion policy creation (HSRNKY)
		DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.HSRNKY, policyNumber, PRE_RENEWAL, false);
	}

	/**
	 * This method gets element data in Document section then there is 2 events with same eventName on transaction
	 *
	 * @param policyNumber
	 * @param eventName
	 */
	private List<String> getPolicyTransactionCodes(String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		List<DocumentPackage> documentPackages = DocGenHelper.getAllDocumentPackages(policyNumber, eventName);
		return DocGenHelper.getPackageDataElementsByNameFromDocumentPackageList(documentPackages, "PolicyDetails", "PlcyTransCd");
	}

	private void runPreRenewalNoticeJob(LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
	}

	private void proposeAndPurchaseConversionPolicy(LocalDateTime conversionExpDate, String policyNumber) {
		TimeSetterUtil.getInstance().nextPhase(conversionExpDate);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		policy.dataGather().start();
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();

		payTotalAmtDue(conversionExpDate, policyNumber);
	}
}


