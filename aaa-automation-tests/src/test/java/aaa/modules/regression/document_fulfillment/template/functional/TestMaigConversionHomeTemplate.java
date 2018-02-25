package aaa.modules.regression.document_fulfillment.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.inject.internal.ImmutableList;
import com.google.inject.internal.ImmutableMap;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.MaigManualConversionHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public abstract class TestMaigConversionHomeTemplate extends PolicyBaseTest {
	private MaigManualConversionHelper maigManualConversionHelper = new MaigManualConversionHelper();

	private static final Map<AaaDocGenEntityQueries.EventNames, List<Job>> JOBS_FOR_EVENT =
			ImmutableMap.of(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob),
					RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2));

	private ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();
	protected BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
	protected TestData tdBilling = testDataManager.billingAccount;


	public void verifyFormsSequence(TestData testData) throws NoSuchFieldException {
		// Get State/Product specific forms
		List<String> forms = getConversionGeneratedForms();
		//Change Membership number in testData to get AHMVCNV form - Validation letter
		String membershipFieldMetaKey =
				TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

		/* Start PAS-2764 Scenario 1, Generate forms and check sequence*/
		/**PAS-9774, PAS-10111 - both has the same root cause which is a Base defect EISAAASP-1852 and has been already resolved in Base EIS 8.17.
		 It will come with next upgrade, until then there's simple workaround - need to run aaa-admin application instead of aaa-app.
		 Both, manual propose and automated propose should work running under aaa-admin.**/
		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER);

		// Create manual entry
		mainApp().open();
		createCustomerIndividual();
		//TestData td = getConversionPolicyDefaultTD();
		if (getPolicyType().equals(PolicyType.PUP)) {
			testData = new PrefillTab().adjustWithRealPolicies(testData, getPrimaryPoliciesForPup());
		}
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
		// Needed for Membership AHMVCNV form, membership number have to be != active For all products except PUP
		if (!getPolicyType().equals(PolicyType.PUP)) {
			testData.adjust(membershipFieldMetaKey, "4290072030989503");
		}
		policy.getDefaultView().fill(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		List<Document> actualDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		assertThat(actualDocumentsList).isNotEmpty().isNotNull();

		maigManualConversionHelper.verifyFormSequence(forms, actualDocumentsList);
		// End PAS-2764 Scenario 1

		//PAS-9607 Verify that packages are generated with correct transaction code (Suresh staff)
		maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("MCON", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		//needed for home banking form generation
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		/*
		//generate first renewal bills 
		//enable auto pay
		Tab.buttonBack.click();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccount().update().perform(testDataManager.billingAccount.getTestData("Update", "TestData_AddAutopay"));
		*/
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalOfferEffectiveDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);

		/*//PAS-9816 Verify that Billing Renewal package forms are generated and are in correct order
		pas9816_verifyRenewalBillingPackageForms(policyNumber);
		*/
		//PAS-9607 Verify that packages are generated with correct transaction code
//		maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("STMT", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);

		// PAS-2764 Scenario 1 Issue first renewal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalOfferEffectiveDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//Here should be a verifier for PAS-9707 (MaigManualConversionHelper#pas9607_verifyPolicyTransactionCode). Expected code should be clarified

		/* Scenario 2, create and issue second renewal and verify documents list */
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		/**https://csaaig.atlassian.net/browse/PAS-9157*/
		/**PAS-10256
		 Cannot rate Home SS policy with effective date higher or equal to 2020-02-018*/

		List<Document> actualDocumentsListAfterSecondRenewal2 = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		assertThat(actualDocumentsList).isNotEmpty().isNotNull();

		//PAS-9607 Verify that packages are generated with correct transaction code (Suresh staff)
		maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("0210", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

		List<String> allDocs2 = new ArrayList<>();
		actualDocumentsListAfterSecondRenewal2.forEach(doc -> allDocs2.add(doc.getTemplateId()));

		List<String> onlyConversionSpecificForms = new ArrayList<>(forms);
		onlyConversionSpecificForms.removeAll(
				Arrays.asList(
						DocGenEnum.Documents.HSTP.getId(),
						DocGenEnum.Documents.AHPNXX.getId(),
						DocGenEnum.Documents.HS02.getId(),
						DocGenEnum.Documents.HS02_4.getId(),
						DocGenEnum.Documents.HS02_6.getId(),
						DocGenEnum.Documents.HSCSNA.getId(),
						DocGenEnum.Documents.PS02.getId(),
						DocGenEnum.Documents.DS02.getId()
				));
		assertThat(allDocs2).doesNotContainAnyElementsOf(onlyConversionSpecificForms);


		//TODO resolve comments below
		//generate 2nd renewal bill

		//PAS-9607 Verify that packages are generated with correct transaction code (Suresh staff)
		//maigManualConversionHelper.pas9607_verifyPolicyTransactionCode("STMT", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		
		/* Issue 2 renewal will be here */

		//after aaaRenewalNoticeBillAsyncJob
		//PAS-9816 Verify that Billing Renewal package forms are generated and are in correct order will be here

		//PolicyBecomesActive
		//Here should be a verifier for PAS-9607 (MaigManualConversionHelper#pas9607_verifyPolicyTransactionCode). Expected code should be clarified

	}

	public void openPolicy(String policyNumber) {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
	}

	private List<String> getConversionGeneratedForms() {
		List<String> forms = new ArrayList<>();

		switch (getPolicyType().getShortName()) {
			case "HomeSS":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getHO3NJForms();
				} else {
					forms = maigManualConversionHelper.getHO3OtherStatesForms();
				}
				break;
			case "HomeSS_HO4":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getHO4NJForms();
				} else {
					forms = maigManualConversionHelper.getHO4OtherStatesForms();
				}
				break;
			case "HomeSS_HO6":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getHO6NJForms();
				} else {
					forms = maigManualConversionHelper.getHO6OtherStatesForms();
				}
				break;
			case "HomeSS_DP3":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getDP3NJForms();
				} else {
					forms = maigManualConversionHelper.getDP3OtherStatesForms();
				}
				break;
			case "PUP":
				if (Constants.States.NJ.equals(getState())) {
					forms = maigManualConversionHelper.getPupNJForms();
				} else {
					forms = maigManualConversionHelper.getPupOtherStatesForms();
				}
				break;
		}
		return forms;
	}

	public void pas9816_verifyRenewalBillingPackageForms(String policyNumber) {

		//Get list of presented forms
		List<Document> actualConversionRenewalBillingDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		assertThat(actualConversionRenewalBillingDocumentsList).isNotEmpty().isNotNull();

		List<String> expectedFormsAndOrder = Arrays.asList(
				DocGenEnum.Documents.AHRBXX.getId(),
				DocGenEnum.Documents.AH35XX.getId()
		);

		//Adding of 'Delta' form for PUP and Home products in the Forms List
		if (!getPolicyType().equals(PolicyType.PUP)) {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBXX.getId());
		} else {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBPUPXX.getId());
		}

		maigManualConversionHelper.verifyFormSequence(expectedFormsAndOrder, actualConversionRenewalBillingDocumentsList);
	}

	/**
	 * @name Test Conversion Document generation (Pre-renewal package)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HSPRNXX document is getting generated
	 * @details
	 */
	public void pas2305_preRenewalLetterHSPRNXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		preRenewalLetterFormGeneration(getConversionPolicyDefaultTD(), HSPRNXX, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Pre-renewal package)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Initiate PUP conversion policy
	 * 5. Check that HSPRNXX document is getting generated
	 * @details
	 */
	public void pas2305_preRenewalLetterPupConvHSPRNXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		preRenewalLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSPRNXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Pre-renewal package)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
	 * 4. Check that HSPRNXX document is getting generated with PUP section
	 * @details
	 */
	public void pas9170_preRenewalLetterPupHSPRNXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		preRenewalLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSPRNXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Pre-renewal package)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
	 * 4. Check that HSPRNMXX document is getting generated
	 * @details
	 */
	public void pas7342_preRenewalLetterHSPRNMXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		preRenewalLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Pre-renewal package)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan
	 * 4. Initiate PUP conversion policy
	 * 5. Check that HSPRNMXX document is getting generated
	 * @details
	 */
	public void pas7342_preRenewalLetterPupConvHSPRNMXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		preRenewalLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSPRNMXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Pre-renewal package)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies  for Home
	 * 4. Check that HSPRNMXX document is getting generated
	 * @details
	 */
	public void pas9170_preRenewalLetterPupHSPRNMXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		preRenewalLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSPRNMXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for Home
	 * 4. Check that HSRNHODPXX document is getting generated
	 * @details
	 */
	public void pas2309_renewalCoverLetterHSRNHODPXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		renewalCoverLetterFormGeneration(getConversionPolicyDefaultTD(), HSRNHODPXX, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data for home
	 * 4. Initiate PUP conversion policy
	 * 5. Check that HSRNHODPXX document is getting generated
	 * @details
	 */
	public void pas2309_renewalCoverLetterPupConvHSRNHODPXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		renewalCoverLetterFormGenerationPup(getConversionPolicyDefaultTD(), HSRNHODPXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with PUP added to OtherActiveAAAPolicies for Home
	 * 4. Check that HSRNHODPXX document is getting generated
	 * @details
	 */
	public void pas2309_renewalCoverLetterPupHSRNHODPXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		renewalCoverLetterFormGeneration(adjustWithPupData(getConversionPolicyDefaultTD()), HSRNHODPXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
	 * 4. Check that HSRNMXX document is getting generated
	 * @details
	 */
	public void pas2570_renewalCoverLetterHSRNMXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		renewalCoverLetterFormGeneration(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan for Home
	 * 4. Initiate PUP conversion policy.
	 * 5. Check that HSRNMXX document is getting generated
	 * @details
	 */
	public void pas2570_renewalCoverLetterPupConvHSRNMXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		renewalCoverLetterFormGenerationPup(adjustWithMortgageeData(getConversionPolicyDefaultTD()), HSRNMXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Test Conversion Document generation (Renewal cover letter)
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data with Mortgagee payment plan and PUP added to OtherActiveAAAPolicies for Home
	 * 4. Check that HSRNMXX document is getting generated
	 * @details
	 */
	public void pas2570_renewalCoverLetterPupHSRNMXX(String state) throws NoSuchFieldException {
		CustomAssert.enableSoftMode();

		renewalCoverLetterFormGeneration(adjustWithPupData(adjustWithMortgageeData(getConversionPolicyDefaultTD())), HSRNMXX, true);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @name Creation converted policy for checking Pre-renewal letter
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	protected void preRenewalLetterFormGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
		String policyNumber = createManualConversionRenewalEntry(testData);
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();

		processRenewal(PRE_RENEWAL, null, policyNumber);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
	}

	/**
	 * @name Creation converted policy for checking Pre-renewal letter
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 4. Initiate PUP conversion policy.
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	protected void preRenewalLetterFormGenerationPup(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
		String policyNumber = createManualConversionRenewalEntry(testData);
		String legacyPolicyNumber = createPolicyForTDPup();

		processRenewal(PRE_RENEWAL, null, policyNumber);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, PRE_RENEWAL);
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, PRE_RENEWAL);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
	}

	/**
	 * @name Creation converted policy for checking Renewal Cover letter
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	protected void renewalCoverLetterFormGeneration(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
		String policyNumber = createManualConversionRenewalEntry(testData);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
	}

	/**
	 * @name Creation converted policy for checking Renewal Cover letter
	 * @scenario 1. Create Customer
	 * 2. Initiate Renewal Entry
	 * 3. Fill Conversion Policy data based on Test Data
	 * 4. Initiate PUP conversion policy
	 * 3. Check that form is getting generated with correct content
	 * @details
	 */
	protected void renewalCoverLetterFormGenerationPup(TestData testData, DocGenEnum.Documents form, boolean isPupPresent) throws NoSuchFieldException {
		String policyNumber = createManualConversionRenewalEntry(testData);
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		String legacyPolicyNumber = createPolicyForTDPup();

		processRenewal(RENEWAL_OFFER, effectiveDate, policyNumber);

		Document document = DocGenHelper.waitForDocumentsAppearanceInDB(form, policyNumber, RENEWAL_OFFER);
		maigManualConversionHelper.verifyPackageTagData(legacyPolicyNumber, policyNumber, RENEWAL_OFFER);
		maigManualConversionHelper.verifyDocumentTagData(document, testData, isPupPresent);
	}

	/**
	 * Create conversion policy based on Test Data
	 */
	private String createManualConversionRenewalEntry(TestData testData) {

		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER).plusDays(5);

		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
		policy.getDefaultView().fillUpTo(testData, BindTab.class, false);
		policy.getDefaultView().getTab(BindTab.class).submitTab();
		return PolicySummaryPage.getPolicyNumber();
	}

	/**
	 * Create conversion policy based on Test Data with linked PUP converted
	 * ToDo: Refactor this after moving InitiateRenewalEntry to policy level
	 */
	private String createPolicyForTDPup() {
		String legacyPolicyNumber = policy.policyInquiry().start().getView().getTab(GeneralTab.class).getInquiryAssetList().
				getAsset(HomeSSMetaData.GeneralTab.SOURCE_POLICY_NUMBER.getLabel()).getValue().toString();
		TestData testDataPup = getManualConversionInitiationTd()
				.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER
						.getLabel()), legacyPolicyNumber);
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(testDataPup);
		Tab.buttonSaveAndExit.click();
		return legacyPolicyNumber;
	}

	/**
	 * Run needed renewal job based on event name for the document
	 */
	public void processRenewal(AaaDocGenEntityQueries.EventNames eventName, LocalDateTime effectiveDate, String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		switch (eventName) {
			case PRE_RENEWAL:
				JOBS_FOR_EVENT.get(eventName).forEach(job -> JobUtils.executeJob(job));
				break;
			case RENEWAL_OFFER:
				LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(effectiveDate);
				TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
				JOBS_FOR_EVENT.get(eventName).forEach(job -> JobUtils.executeJob(job));
				openPolicy(policyNumber);
				productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
				break;
			default:
				throw new IllegalArgumentException("Undefined eventName " + eventName.name());
		}
	}

	/**
	 * Utility method that enhances Conversion {@link TestData} with Mortgagee info
	 */
	public TestData adjustWithMortgageeData(TestData policyTD) {
		//adjust TestData with Mortgagee tab data
		String mortgageeTabKey = TestData.makeKeyPath(HomeSSMetaData.MortgageesTab.class.getSimpleName());
		TestData mortgageeTD = getTestSpecificTD("MortgageesTab");
		//adjust TestData with Premium and Coverage tab data
		String premiumAndCoverageTabKey = TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName());
		TestData premiumAndCoverageTD = getTestSpecificTD("PremiumsAndCoveragesQuoteTab_Mortgagee");
		return policyTD.adjust(mortgageeTabKey, mortgageeTD).adjust(premiumAndCoverageTabKey, premiumAndCoverageTD);
	}

	/**
	 * Utility method that enhances Conversion {@link TestData} with PUP in OtherActiveAAAPolicies
	 */
	public TestData adjustWithPupData(TestData policyTD) {
		TestData testDataOtherActiveAAAPolicies = getTestSpecificTD("OtherActiveAAAPolicies").resolveLinks();
		String pupOtherActiveAAAPoliciesTabKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
		return policyTD.adjust(pupOtherActiveAAAPoliciesTabKey, testDataOtherActiveAAAPolicies);
	}

	public TestData adjustWithSeniorInsuredDataHO4(TestData policyTD) {
		String insuredDOBPath =
				TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel());
		return policyTD.adjust(insuredDOBPath, TimeSetterUtil.getInstance().getCurrentTime().minusYears(65).format(DateTimeUtils.MM_DD_YYYY));
	}

	public TestData adjustWithSeniorInsuredData(TestData policyTD) {
		String mortgageeTabMetaKey = new MortgageesTab().getMetaKey();

		String insuredDOBPath =
				TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel());

		TestData additionalInterestData = new DataProviderFactory().emptyData()
				.adjust(HomeSSMetaData.MortgageesTab.AdditionalInterest.NAME.getLabel(), "Test")
				.adjust(HomeSSMetaData.MortgageesTab.AdditionalInterest.ZIP_CODE.getLabel(), "85085")
				.adjust(HomeSSMetaData.MortgageesTab.AdditionalInterest.STREET_ADDRESS_1.getLabel(), "Test");

		return policyTD.adjust(insuredDOBPath, TimeSetterUtil.getInstance().getCurrentTime().minusYears(65).format(DateTimeUtils.MM_DD_YYYY))
				.adjust(TestData.makeKeyPath(mortgageeTabMetaKey, HomeSSMetaData.MortgageesTab.IS_THERE_ADDITIONA_INTEREST.getLabel()), "Yes")
				.adjust(TestData.makeKeyPath(mortgageeTabMetaKey, HomeSSMetaData.MortgageesTab.ADDITIONAL_INTEREST.getLabel()), additionalInterestData);
	}

}
