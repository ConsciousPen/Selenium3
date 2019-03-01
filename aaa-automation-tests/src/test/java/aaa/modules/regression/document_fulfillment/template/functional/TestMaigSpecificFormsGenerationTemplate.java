package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.DocumentPackage;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestMaigSpecificFormsGenerationTemplate extends PolicyBaseTest {
	private static final String SELECT_POLICY_SOURCE_NUMBER = "select p.SOURCEPOLICYNUM from POLICYSUMMARY p Where p.Policynumber = '%s'";
	private static final String INSERT_HOME_BANKING_FOR_POLICY = "INSERT INTO REMINDERPOLICYNUMBERCHANGE rpc"
			+ "(rpc.ID, rpc.DTYPE, rpc.POLICYNUMBER, rpc.DATEOFLASTPAYMENT, rpc.NUMBEROFPAYMENTS, rpc.SINGLEPAYMENTSCOUNT, rpc.RECURRINGPAYMENTSCOUNT, rpc.REMINDERIND)"
			+ "values (eis_sequence.nextval,'HBReminderPolicyNumberChangeEntity', '%1$s', to_date('%2$s', 'YYYY-MM-dd'), 10,'' ,'', 0)";

	protected TestData tdBilling = testDataManager.billingAccount;

	protected BillingAccount billingAccount = new BillingAccount();

	private ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-10668 CONTENT & TRIGGER (timeline): Pre-Renewal letter (mortgagee) PA DP3
	 * PAS-6731 CONTENT & TRIGGER (timeline): Pre-Renewal letter (insured bill) PA DP3
	 * @scenario 1. Initiate manual entry
	 * 2. Shift time
	 * 3. Run jobs to generate aaaPreRenewalNoticeAsyncJob
	 */
	protected String generatePreRenewalEvent(TestData testData, LocalDateTime renewalOfferEffectiveDate, LocalDateTime preRenewalGenDate) {
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
		policy.getDefaultView().fill(testData);

		PolicySummaryPage.buttonBackFromRenewals.click();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		TimeSetterUtil.getInstance().nextPhase(preRenewalGenDate);

		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
		return policyNumber;
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2674 MAIG CONVERSION: test conversion renewal offer package generation and print sequence of end/notices (HO3,HO4,HO6,DP3,PUP)
	 * PAS-9607	BFC for Conversion Renewal Offer and Billing Packages (HO3, HO4, HO6, DP3, PUP)
	 * PAS-9651	Print Sequence: Conversion Renewal OFFER (PA & MD)
	 * PAS-18908 Specific print order sequence - IN
	 *
	 * @scenario
	 * 1. Initiate manual entry on RENEW_GENERATE_OFFER
	 * 2. Verify conversion specific renewal offer packet was generated in right sequence
	 * 3. Verify Policy Transaction Code
	 * 4. Run renewalOfferGenerationPart2 and aaaBatchMarkerJob
	 * 5. General bill and accept payment and run policyStatusUpdateJob
	 * 6. Create and issue second renewal
	 * 7. Check verify Policy Transaction Code and conversion specific forms absense
	 * 8. Check forms sequence after 2nd renewal (for subsequent renewals) is correct
	 */

	protected void verifyConversionFormsSequence(TestData testData) throws NoSuchFieldException {
		boolean specificProductCondition = isHOProduct(getPolicyType());
		boolean mortgageePaymentPlanPresence = isMortgageePaymentPlanPresence(testData);
		// Get State/Product specific forms
		List<String> forms = getConversionSpecificGeneratedForms(mortgageePaymentPlanPresence, specificProductCondition);

		/* Start PAS-2764 Scenario 1, Generate forms and check sequence*/
		/**PAS-9774, PAS-10111 - both has the same root cause which is a Base defect EISAAASP-1852 and has been already resolved in Base EIS 8.17.
		 It will come with next upgrade, until then there's simple workaround - need to run aaa-admin application instead of aaa-app.
		 Both, manual propose and automated propose should work running under aaa-admin.**/

		// Create conversion renewal
		String policyNumber = createFormsSpecificManualEntry(testData);

		LocalDateTime conversionPolicyEffectiveDate = PolicySummaryPage.getExpirationDate();

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(conversionPolicyEffectiveDate));

		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//https://csaaig.atlassian.net/browse/PAS-11474
		List<DocumentPackage> allDocumentPackages = DocGenHelper.getAllDocumentPackages(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		List<Document> actualDocumentsListAfterFirstRenewal = DocGenHelper.getDocumentsFromDocumentPackagesList(allDocumentPackages);

		verifyFormSequence(forms, actualDocumentsListAfterFirstRenewal);

		//PAS-9607 Verify that packages are generated with correct transaction code
		verifyPolicyTransactionCode("MCON", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		// End PAS-2764 Scenario 1

		billGeneration(conversionPolicyEffectiveDate);

		// Start PAS-2764 Scenario 1 Issue first renewal
		mainApp().reopen();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = new Dollar(BillingSummaryPage.getTotalDue());
		billingAccount.acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue.subtract(new Dollar(10)));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(conversionPolicyEffectiveDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		// End PAS-2764 Scenario 1 Issue first renewal

		/* Scenario 2, create and issue second renewal and verify documents list */
		issueSecondRenewal(conversionPolicyEffectiveDate);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		/**
		 * https://csaaig.atlassian.net/browse/PAS-9157
		 * PAS-10256 Cannot rate Home SS policy with effective date higher or equal to 2020-02-018
		 */
		//PAS-9607 Verify that packages are generated with correct transaction code
		verifyPolicyTransactionCode("0210", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		// Shouldn't be after second renewal
		List<Document> actualDocumentsAfterSecondRenewal = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		List<String> listOfFormsAfterSecondRenewal = new ArrayList<>();
		actualDocumentsAfterSecondRenewal.forEach(doc -> listOfFormsAfterSecondRenewal.add(doc.getTemplateId()));

		verifyConversionRenewalPackageAbsence(forms, listOfFormsAfterSecondRenewal);

		//Check sequence of documents after second renewal
		verifyFormSequence(getSecondRenewalForms(), actualDocumentsAfterSecondRenewal);

		// PAS-8777, PAS-8766
		if (specificProductCondition) {
			assertThat(actualDocumentsAfterSecondRenewal.stream().map(Document::getTemplateId).toArray()).doesNotContain(DocGenEnum.Documents.HSRNHODPXX.getIdInXml());
		}
	}
	//TODO ADD SWITCH HERE
	protected List<String> getSecondRenewalForms() {
		switch (getPolicyType().getShortName()) {
			case "HomeSS_HO3":
				return Arrays.asList(
						DocGenEnum.Documents.HSRNXX.getIdInXml(),
						DocGenEnum.Documents.HS02.getIdInXml(),
						DocGenEnum.Documents.AHAUXX.getIdInXml(),
						DocGenEnum.Documents.AHPNXX.getIdInXml());

			case "HomeSS_HO4":
				return Arrays.asList(
						DocGenEnum.Documents.HSRNXX.getIdInXml(),
						DocGenEnum.Documents.HS02_4.getIdInXml(),
						DocGenEnum.Documents.AHAUXX.getIdInXml(),
						DocGenEnum.Documents.AHPNXX.getIdInXml());
			case "HomeSS_HO6":
				return Arrays.asList(
						DocGenEnum.Documents.HSRNXX.getIdInXml(),
						DocGenEnum.Documents.HS02_6.getIdInXml(),
						DocGenEnum.Documents.AHAUXX.getIdInXml(),
						DocGenEnum.Documents.AHPNXX.getIdInXml());
			case "HomeSS_DP3":
				return Arrays.asList(
						DocGenEnum.Documents.HSRNXX.getIdInXml(),
						DocGenEnum.Documents.DS02.getIdInXml(),
						DocGenEnum.Documents.AHAUXX.getIdInXml(),
						DocGenEnum.Documents.AHPNXX.getIdInXml());
			case "PUP":
				return Arrays.asList(
						DocGenEnum.Documents.HSRNXX.getIdInXml(),
						DocGenEnum.Documents.PS02.getIdInXml(),
						DocGenEnum.Documents.AHPNXX.getIdInXml());
				default:
					throw new IstfException("Product used is unexpected. Expecting HomeSS_HO4, HomeSS_HO6, HomeSS_DP3, or PUP");
		}

	}

//		return Arrays.asList(
//				DocGenEnum.Documents.HSRNXX.getIdInXml(),
//				DocGenEnum.Documents.HS02.getIdInXml(),
//				DocGenEnum.Documents.AHAUXX.getIdInXml(),
//				DocGenEnum.Documents.AHPNXX.getIdInXml());
//	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-9816 PAS-9816 MAIG CONVERSION: test conversion renewal billing package generation and print sequence (HO3,HO4,HO6,DP3,PUP)
	 * PAS-9607	BFC for Conversion Renewal Offer and Billing Packages (HO3, HO4, HO6, DP3, PUP)
	 * PAS-9650	Print Sequence: Conversion Renewal BILLING (PA & MD)
	 * PAS-18908 Specific print order sequence - IN
	 *
	 * @scenario
	 * 1. Initiate manual entry on RENEW_GENERATE_OFFER
	 * 2. Verify billing specific renewal offer packet was generated in right sequence
	 * 3. Verify Policy Transaction Code
	 * 4. set Up Trigger Home Banking Conversion Renewal
	 * 5. Add credit card payment method and enable autopay
	 * 6. Run renewalOfferGenerationPart2 and aaaBatchMarkerJob
	 * 7. General bill and accept payment and run policyStatusUpdateJob
	 * 8. Create and issue second renewal
	 * 9. Check verify Policy Transaction Code and billing specific forms absense
	 */
	protected void verifyBillingFormsSequence(TestData testData, Boolean isOnAutopay) throws NoSuchFieldException {
		/* Start PAS-9816 Scenario 1, Generate forms and check sequence*/
		/**PAS-9774, PAS-10111 - both has the same root cause which is a Base defect EISAAASP-1852 and has been already resolved in Base EIS 8.17.
		 It will come with next upgrade, until then there's simple workaround - need to run aaa-admin application instead of aaa-app.
		 Both, manual propose and automated propose should work running under aaa-admin.**/
		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getPhaseStartTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER);
		LocalDateTime renewalBillGenerationDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getPhaseStartTime(), TimePoints.TimepointsList.BILL_GENERATION);

		// Create manual entry
		String policyNumber = createFormsSpecificManualEntry(testData);

		//Move time to R-20 for bill
		TimeSetterUtil.getInstance().nextPhase(renewalBillGenerationDate);

		//Propose the policy
		mainApp().open();
//		SearchPage.openPolicy(policyNumber);
//		policy.dataGather().start();
//		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
//		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
//		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
//		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
//		new BindTab().submitTab();

//		TestData td = getPolicyTD().adjust(AutoSSMetaData.DriverTab.class.getSimpleName(), DataProviderFactory.emptyData());
		TestData proposeTd = getPolicyTD().adjust(HomeSSMetaData.ReportsTab.class.getSimpleName(), DataProviderFactory.emptyData());
		policy.calculatePremiumAndPurchase(proposeTd);

		//Verify policy is in proposed status
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		//needed for home banking form generation
		setUpTriggerHomeBankingConversionRenewal(policyNumber);

		if (isOnAutopay){
			// Add Credit Card payment method and Enable AutoPayment
			Tab.buttonBack.click();
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			billingAccount.update().perform(testDataManager.billingAccount.getTestData("Update", "TestData_AddAutopay"));
		}

		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		billGeneration(renewalOfferEffectiveDate);

		//PAS-9607 Verify that packages are generated with correct transaction code
		String policyTransactionCode = getPackageTag(policyNumber, "PlcyTransCd", AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);

		assertThat(policyTransactionCode.equals("STMT") || policyTransactionCode.equals("0210")).isTrue();
		//PAS-9816 Verify that Billing Renewal package forms are generated and are in correct order
		verifyRenewalBillingPackageFormsPresence(policyNumber, getPolicyType(), isOnAutopay);

		// Start PAS-9816 Scenario 1 Issue first renewal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue.subtract(new Dollar(30)));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalOfferEffectiveDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		// End PAS-9816 Scenario 1 Issue first renewal

		/* Scenario 2, create and issue second renewal and verify documents list */
		issueSecondRenewal(renewalOfferEffectiveDate);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		LocalDateTime secondRenewalExpirationDate = PolicySummaryPage.getExpirationDate();
		PolicySummaryPage.buttonRenewals.click();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		/**
		 * https://csaaig.atlassian.net/browse/PAS-9157
		 * PAS-10256
		 * Cannot rate Home SS policy with effective date higher or equal to 2020-02-018
		 */
		//Generate Bill for the second renewal to verify Home Banking forms
		billGeneration(secondRenewalExpirationDate);
		// Shouldn't be after second renewal
		verifyBillingRenewalPackageAbsence(policyNumber);

		//PAS-9607 Verify that packages are generated with correct transaction code
		String policyTransactionCode2 = getPackageTag(policyNumber, "PlcyTransCd", AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);

		assertThat(policyTransactionCode2.equals("STMT") || policyTransactionCode2.equals("0210")).isTrue();
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-9816 PAS-9816 MAIG CONVERSION: test conversion renewal billing package generation and print sequence (HO3,HO4,HO6,DP3,PUP)
	 * PAS-9607	BFC for Conversion Renewal Offer and Billing Packages (HO3, HO4, HO6, DP3, PUP)
	 * PAS-9650	Print Sequence: Conversion Renewal BILLING (PA & MD)
	 *
	 * @scenario
	 * 1. Initiate manual entry on PRE RENEWAL
	 * 2. Verify PRE RENEWAL packet was generated in right sequence

	 */
	protected void verifyPreRenewalFormsSequence(TestData testData,LocalDateTime renewalOfferEffectiveDate,LocalDateTime preRenewalGenerationDate) throws NoSuchFieldException {
		String policyNumber = generatePreRenewalEvent(testData,renewalOfferEffectiveDate, preRenewalGenerationDate);

		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber,PRE_RENEWAL);
		List<String> forms = new ArrayList<>(Arrays.asList("stub",DocGenEnum.Documents.DS65PA.getIdInXml(),DocGenEnum.Documents.DS65PA.getIdInXml()));

		if(isMortgageePaymentPlanPresence(testData)){
			forms.set(0, DocGenEnum.Documents.HSPRNMXX.getIdInXml());
		}
		else{
			forms.set(0, DocGenEnum.Documents.HSPRNXX.getIdInXml());
		}

		verifyFormSequence(forms, docs);

	}

	/**
	 * Create Very specific Manual Entry
	 * @param testData for policy creation
	 * @return
	 */
	private String createFormsSpecificManualEntry(TestData testData) {
		String membershipFieldMetaKey =
				TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

		mainApp().open();
		// Set birthdate if NJ to generate Senior Discount
		conditionalCustomerCreation();

		if (getPolicyType().equals(PolicyType.PUP)) {
			testData = new PrefillTab().adjustWithRealPolicies(testData, getPrimaryPoliciesForPup());
			// Workaround for prefill
			/*Map<String,String> policies = new HashMap<>();
			TestData tdHomeEntry = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "InitiateRenewalEntry", "TestData");
			TestData tdHomeConversion = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "Conversion", "TestData");
			customer.initiateRenewalEntry().perform(tdHomeEntry);
			PolicyType.HOME_SS_HO3.get().getDefaultView().fill(tdHomeConversion);
			String homePolicyNum = PolicySummaryPage.getPolicyNumber();
			policies.put("Primary_HO3", homePolicyNum);
			testData = new PrefillTab().adjustWithRealPolicies(testData, policies);

			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());*/
		}
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		// Needed for Membership AHMVCNV form, membership number have to be != active For all products except PUP
		if (!getPolicyType().equals(PolicyType.PUP)) {
			testData.adjust(membershipFieldMetaKey, "4343433333333335");
		}
		policy.getDefaultView().fill(testData);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		PolicySummaryPage.buttonBackFromRenewals.click();

		return PolicySummaryPage.labelPolicyNumber.getValue();
	}

	private void conditionalCustomerCreation() {
		if (getState().equals(Constants.States.NJ)) {
			createCustomerIndividual(getCustomerIndividualTD("DataGather", "TestData")
					.adjust(TestData.makeKeyPath("GeneralTab", "Date of Birth"), TimeSetterUtil.getInstance().getCurrentTime().minusYears(65)
							.format(DateTimeUtils.MM_DD_YYYY))); // if NJ adjust Date Of Birth
		} else {
			createCustomerIndividual();
		}
	}

	/* Helpers */

	protected void setUpTriggerHomeBankingConversionRenewal(String policyNumber) {
		String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));

		int a = DBService.get().executeUpdate(String.format(INSERT_HOME_BANKING_FOR_POLICY, getSourcePolicyNumber(policyNumber), currentDate));
		assertThat(a).as("MaigManualConversionHelper# setUpTriggerHomeBankingConversionRenewal method failed, value was not inserted in DB").isGreaterThan(0);
	}

	private void issueSecondRenewal(LocalDateTime renewalOfferEffectiveDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalOfferEffectiveDate.plusYears(1)));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	private void billGeneration(LocalDateTime renewalOfferEffectiveDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalOfferEffectiveDate));
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
	}

	public String getPackageTag(String policyNumber, String tag, AaaDocGenEntityQueries.EventNames name) throws NoSuchFieldException {
		return getPackageDataElemByName(policyNumber, "PolicyDetails", tag, name);
	}

	private String getSourcePolicyNumber(String policyNumber) {
		String sourcePolicyNumberValue = DBService.get().getValue(String.format(SELECT_POLICY_SOURCE_NUMBER, policyNumber)).orElse(null);
		assertThat(sourcePolicyNumberValue).isNotEqualTo(null);
		return sourcePolicyNumberValue;
	}

	/* Checks */
	private List<Document> getDocumentsFromListDocumentPackages(List<DocumentPackage> allDocumentPackages) {
		List<Document> actualDocumentsListAfterFirstRenewal = new ArrayList<>();
		for(DocumentPackage documentPackage: allDocumentPackages){
			actualDocumentsListAfterFirstRenewal.addAll(documentPackage.getDocuments());
		}
		return actualDocumentsListAfterFirstRenewal;
	}

	private void verifyBillingRenewalPackageAbsence(String policyNumber) {
		List<Document> billingDocumentsListAfterSecondRenewal = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		assertThat(billingDocumentsListAfterSecondRenewal).isNotEmpty().isNotNull();
		assertThat(billingDocumentsListAfterSecondRenewal).as("Renewal Bill for 2nd Renewal is not generated").isEqualTo(2);

		List<String> billingFormsAfterSecondRenewal = new ArrayList<>();
		billingDocumentsListAfterSecondRenewal.forEach(doc -> billingFormsAfterSecondRenewal.add(doc.getTemplateId()));

		assertThat(billingFormsAfterSecondRenewal).doesNotContain(
				DocGenEnum.Documents.HSRNHBXX.getIdInXml(),
				DocGenEnum.Documents.HSRNHBPUP.getIdInXml());
	}

	private void verifyConversionRenewalPackageAbsence(List<String> forms, List<String> listOfFormsAfterSecondRenewal) {
		// Remove renewal specific forms
		List<String> getOnlyConversionSpecificForms = getOnlyRenewalSpecificForms(forms);
		log.info("List of forms which are not expected on second renewal :" + getOnlyConversionSpecificForms);
		log.info("List of forms on second renewal :" + listOfFormsAfterSecondRenewal);
		assertThat(listOfFormsAfterSecondRenewal).doesNotContainAnyElementsOf(getOnlyConversionSpecificForms);
	}

	private void verifyPolicyTransactionCode(String expectedCode, String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		String policyTransactionCode = getPackageTag(policyNumber, "PlcyTransCd", eventName);
		assertThat(policyTransactionCode).isEqualTo(expectedCode);
	}

	private void verifyRenewalBillingPackageFormsPresence(String policyNumber, PolicyType policyType, Boolean isOnAutopay) {
		List<String> expectedFormsAndOrder = new ArrayList<>(Arrays.asList(
				DocGenEnum.Documents.AHRBXX.getIdInXml()
		));

		if (isOnAutopay) {
			expectedFormsAndOrder.add(DocGenEnum.Documents.AH35XX.getIdInXml());
		}

		//Adding of 'Delta' form for PUP and Home products in the Forms List
		if (!policyType.equals(PolicyType.PUP)) {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBXX.getIdInXml());
		} else {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBPUP.getIdInXml());
		}

		List<Document> actualConversionRenewalBillingDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		verifyFormSequence(expectedFormsAndOrder, actualConversionRenewalBillingDocumentsList);
	}

	/**
	 * Verify that PolicyDetails value is present in the Package
	 */

	protected void verifyFormSequence(List<String> expectedFormsOrder, List<Document> documentList) {
		assertThat(documentList).isNotEmpty().isNotNull();
		assertSoftly(softly -> {
			List<String> collectedDocs = documentList.stream().map(Document::getTemplateId).collect(Collectors.toList());
			log.info("Actual list of forms : " + collectedDocs.toString());
			log.info("Expected list of forms : " + expectedFormsOrder.toString());
			// Check that all documents where generated
			softly.assertThat(collectedDocs).containsAll(expectedFormsOrder);
			// Get all docs +  sequence number
			HashMap<Integer, String> actualDocuments = new HashMap<>();
			documentList.forEach(doc -> actualDocuments.put(Integer.parseInt(doc.getSequence()), doc.getTemplateId()));
			// Sort keys
			List<Integer> sortedKeys = new ArrayList(actualDocuments.keySet());
			Collections.sort(sortedKeys);
			// Get documents order by sequence number
			List<String> actualOrderBySequenceNumber = new ArrayList<>();
			sortedKeys.forEach(sequenceId -> actualOrderBySequenceNumber.add(actualDocuments.get(sequenceId)));
			log.info(actualOrderBySequenceNumber.toString());
			// Get one by one items from expected order to build actual list
			List<String> intersectionsWithActualList = new ArrayList<>();
			for(String expectedForm : expectedFormsOrder){
				if(actualOrderBySequenceNumber.contains(expectedForm)){
					intersectionsWithActualList.add(expectedForm);
				}else{
					System.out.println(expectedForm + " was not added to intersections list");
				}
			}
			// Check sequence
			softly.assertThat(intersectionsWithActualList).as("Form Sequence is not correct").isEqualTo(expectedFormsOrder);
		});
	}

	/**
	 * Check mortgage plan presence via TD
	 * @param testData
	 * @return boolean
	 */
	private boolean isMortgageePaymentPlanPresence(TestData testData) {
		boolean mortgageePaymentPlanPresence = false;
		if (!getPolicyType().getShortName().equals("PUP")) {
			mortgageePaymentPlanPresence = testData.getTestData(new PremiumsAndCoveragesQuoteTab().getMetaKey()).getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()).contains("Mortgagee Bill");
		}
		return mortgageePaymentPlanPresence;
	}

	private boolean isHOProduct(PolicyType policyType) {
		// Specific conditions which will reflected in forms which will be verified later
		return Arrays.asList(
				PolicyType.HOME_SS_HO3.getShortName(),
				PolicyType.HOME_SS_HO4.getShortName(),
				PolicyType.HOME_SS_HO6.getShortName(),
				PolicyType.HOME_SS_DP3.getShortName())
				.contains(policyType.getShortName());
	}

	/* Data */
	private List<String> getConversionSpecificGeneratedForms(boolean mortgageePaymentPlanPresence, boolean specificProductCondition) {
		List<String> forms = new ArrayList<>(getTestSpecificTD("ConversionForms").getList("FormsList"));
		editFirstFormDependingOnPaymentPlan(mortgageePaymentPlanPresence, specificProductCondition, forms);
		log.info("List of forms we expect : {}", forms);
		return forms;
	}

	private void editFirstFormDependingOnPaymentPlan(boolean mortgageePaymentPlanPresence, boolean specificProductCondition, List<String> forms) {
		//"HO3","H04","HO6","DP3" if test data has Mortgagee payment plan, swap first form in sequence to HSRNHODPXX
		if (specificProductCondition && mortgageePaymentPlanPresence) {
			forms.set(0, DocGenEnum.Documents.HSRNMXX.getIdInXml());
		}
		//"HO3","H04","HO6","DP3" swap first form in sequence to HSRNMXX
		else if (specificProductCondition && !mortgageePaymentPlanPresence) {
			forms.set(0, DocGenEnum.Documents.HSRNHODPXX.getIdInXml());
		}
	}

	private List<String> getOnlyRenewalSpecificForms(List<String> forms) {
		List<String> getOnlyConversionSpecificForms = new ArrayList<>(forms);
		getOnlyConversionSpecificForms.removeAll(
				Arrays.asList(
						DocGenEnum.Documents.HSTP.getIdInXml(),
						DocGenEnum.Documents.AHPNXX.getIdInXml(),
						DocGenEnum.Documents.AHAUXX.getIdInXml(),
						DocGenEnum.Documents.HS02.getIdInXml(),
						DocGenEnum.Documents.HS02_4.getIdInXml(),
						DocGenEnum.Documents.HS02_6.getIdInXml(),
						DocGenEnum.Documents.HSCSNA.getIdInXml(),
						DocGenEnum.Documents.PS02.getIdInXml(),
						DocGenEnum.Documents.DS02.getIdInXml(),
						DocGenEnum.Documents.IL_09_10.getIdInXml(),
						DocGenEnum.Documents.DSACCCMD.getIdInXml(),
						DocGenEnum.Documents.HSAOCMDA.getIdInXml(),
						DocGenEnum.Documents.HSSNMDA.getIdInXml(),
						DocGenEnum.Documents.HSCRRMD.getIdInXml(),
						DocGenEnum.Documents.HSSNMDB.getIdInXml(),
						DocGenEnum.Documents.HSAOCMDB.getIdInXml(),
						DocGenEnum.Documents.HSAOCMDC.getIdInXml(),
						DocGenEnum.Documents.HSSNMDC.getIdInXml()
				));
		return getOnlyConversionSpecificForms;
	}

	protected TestData getTestDataWithAdditionalInterest(TestData policyTD) {
		// HSTP form
		String mortgageeTabMetaKey = new MortgageesTab().getMetaKey();

		TestData additionalInterestData = new DataProviderFactory().emptyData()
				.adjust(HomeSSMetaData.MortgageesTab.AdditionalInterest.NAME.getLabel(), "Test")
				.adjust(HomeSSMetaData.MortgageesTab.AdditionalInterest.ZIP_CODE.getLabel(), "85085")
				.adjust(HomeSSMetaData.MortgageesTab.AdditionalInterest.STREET_ADDRESS_1.getLabel(), "Test");

		return policyTD
				.adjust(TestData.makeKeyPath(mortgageeTabMetaKey, HomeSSMetaData.MortgageesTab.IS_THERE_ADDITIONA_INTEREST.getLabel()), "Yes")
				.adjust(TestData.makeKeyPath(mortgageeTabMetaKey, HomeSSMetaData.MortgageesTab.ADDITIONAL_INTEREST.getLabel()), additionalInterestData);
	}


	protected TestData adjustWithMortgageeData(TestData policyTD) {
		TestData testDataMortgagee = getTestSpecificTD("MortgageesTab");
		//adjust TestData with Premium and Coverage tab data
		TestData testDataPremiumTabWithMortgageePaymentPlan = getTestSpecificTD("PremiumsAndCoveragesQuoteTab_Mortgagee");
		return policyTD.adjust(new MortgageesTab().getMetaKey(), testDataMortgagee).adjust(new PremiumsAndCoveragesQuoteTab().getMetaKey(), testDataPremiumTabWithMortgageePaymentPlan);
	}

	public TestData getSpecificFormsTestData() {
		TestData testData = getConversionPolicyDefaultTD();

		if (getState().equalsIgnoreCase(Constants.States.MD) || getState().equalsIgnoreCase(Constants.States.IN)) {
			// enable HSWSBMD (for MD) || HS 04 93 and HS 23 83 (for IN)
			testData.adjust("EndorsementTab", getTestSpecificTD("EndorsementTab"));
		}
		return testData;
	}
}
