package aaa.modules.regression.document_fulfillment.template.functional;

import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
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
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

public abstract class TestMaigSpecificFormsGenerationTemplate extends PolicyBaseTest {
	private static final String SELECT_POLICY_SOURCE_NUMBER = "select p.SOURCEPOLICYNUM from POLICYSUMMARY p Where p.Policynumber = '%s'";
	private static final String INSERT_HOME_BANKING_FOR_POLICY = "INSERT INTO REMINDERPOLICYNUMBERCHANGE rpc"
			+ "(rpc.ID, rpc.DTYPE, rpc.POLICYNUMBER, rpc.DATEOFLASTPAYMENT, rpc.NUMBEROFPAYMENTS, rpc.SINGLEPAYMENTSCOUNT, rpc.RECURRINGPAYMENTSCOUNT, rpc.REMINDERIND)"
			+ "values (eis_sequence.nextval,'HBReminderPolicyNumberChangeEntity', '%1$s', to_date('%2$s', 'YYYY-MM-dd'), 10,'' ,'', 0)";

	protected TestData tdBilling = testDataManager.billingAccount;

	protected BillingAccount billingAccount = new BillingAccount();

	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();

	private ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();

	/**
	 * @author Viktor Petrenko
	 *
	 * PAS-10668 CONTENT & TRIGGER (timeline): Pre-Renewal letter (mortgagee) PA DP3
	 * PAS-6731 CONTENT & TRIGGER (timeline): Pre-Renewal letter (insured bill) PA DP3
	 *
	 * @scenario
	 * 1. Initiate manual entry
	 * 2. Shift time
	 * 3. Run jobs to generate aaaPreRenewalNoticeAsyncJob
	 *
	 */
	public String generatePreRenewalEvent(TestData testData, LocalDateTime renewalOfferEffectiveDate, LocalDateTime preRenewalGenDate) {
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
		policy.getDefaultView().fill(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		TimeSetterUtil.getInstance().nextPhase(preRenewalGenDate);

		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
		return policyNumber;
	}

	/**
	 * @author Viktor Petrenko
	 *
	 * PAS-2674 MAIG CONVERSION: test conversion renewal offer package generation and print sequence of end/notices (HO3,HO4,HO6,DP3,PUP)
	 * PAS-9607	BFC for Conversion Renewal Offer and Billing Packages (HO3, HO4, HO6, DP3, PUP)
	 *
	 * @scenario
	 * 1. Initiate manual entry on RENEW_GENERATE_OFFER
	 * 2. Verify conversion specific renewal offer packet was generated in right sequence
	 * 3. Verify Policy Transaction Code
	 * 4. Run renewalOfferGenerationPart2 and aaaBatchMarkerJob
	 * 5. General bill and accept payment and run policyStatusUpdateJob
	 * 6.Create and issue second renewal
	 * 7. Check verify Policy Transaction Code and conversion specific forms absense
	 */

	protected void verifyConversionFormsSequence(TestData testData) throws NoSuchFieldException {
		// Specific conditions which will reflected in forms which will be verified later
		boolean specificProductCondition = Arrays.asList("HomeSS","HomeSS_HO4", "HomeSS_HO6", "HomeSS_DP3").contains(getPolicyType().getShortName());
		boolean mortgageePaymentPlanPresence = false;
		if (!getPolicyType().getShortName().equals("PUP")) {
			mortgageePaymentPlanPresence = testData.getTestData(new PremiumsAndCoveragesQuoteTab().getMetaKey()).getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()).contains("Mortgagee Bill");
		}
		// Get State/Product specific forms
		List<String> forms = getConversionSpecificGeneratedForms(mortgageePaymentPlanPresence, specificProductCondition);

		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER);

		/* Start PAS-2764 Scenario 1, Generate forms and check sequence*/
		/**PAS-9774, PAS-10111 - both has the same root cause which is a Base defect EISAAASP-1852 and has been already resolved in Base EIS 8.17.
		 It will come with next upgrade, until then there's simple workaround - need to run aaa-admin application instead of aaa-app.
		 Both, manual propose and automated propose should work running under aaa-admin.**/

		// Create manual entry
		String policyNumber = createFormsSpecificManualEntry(testData, renewalOfferEffectiveDate);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		List<Document> actualDocumentsListAfterFirstRenewal = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		verifyFormSequence(forms, actualDocumentsListAfterFirstRenewal);
		// End PAS-2764 Scenario 1

		//PAS-9607 Verify that packages are generated with correct transaction code
		pas9607_verifyPolicyTransactionCode("MCON", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		billGeneration(renewalOfferEffectiveDate);

		// Start PAS-2764 Scenario 1 Issue first renewal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue.subtract(new Dollar(10)));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalOfferEffectiveDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		// End PAS-2764 Scenario 1 Issue first renewal

		/* Scenario 2, create and issue second renewal and verify documents list */
		issueSecondRenewal(renewalOfferEffectiveDate);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		/**https://csaaig.atlassian.net/browse/PAS-9157*/
		/**PAS-10256
		 Cannot rate Home SS policy with effective date higher or equal to 2020-02-018*/

		//PAS-9607 Verify that packages are generated with correct transaction code
		pas9607_verifyPolicyTransactionCode("0210", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		// Shouldn't be after second renewal
		List<Document> actualDocumentsAfterSecondRenewal = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		pas2674_verifyConversionRenewalPackageAbsence(forms, actualDocumentsAfterSecondRenewal);

		// PAS-8777, PAS-8766
		if(specificProductCondition){
			assertThat(actualDocumentsAfterSecondRenewal.stream().map(Document::getTemplateId).toArray()).contains(DocGenEnum.Documents.HSRNMXX.getIdInXml());
		}

	}

	/**
	 * @author Viktor Petrenko
	 *
	 * PAS-9816 PAS-9816 MAIG CONVERSION: test conversion renewal billing package generation and print sequence (HO3,HO4,HO6,DP3,PUP)
	 * PAS-9607	BFC for Conversion Renewal Offer and Billing Packages (HO3, HO4, HO6, DP3, PUP)
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
	protected void verifyBillingFormsSequence(TestData testData) throws NoSuchFieldException {
		/* Start PAS-9816 Scenario 1, Generate forms and check sequence*/
		/**PAS-9774, PAS-10111 - both has the same root cause which is a Base defect EISAAASP-1852 and has been already resolved in Base EIS 8.17.
		 It will come with next upgrade, until then there's simple workaround - need to run aaa-admin application instead of aaa-app.
		 Both, manual propose and automated propose should work running under aaa-admin.**/
		LocalDateTime renewalOfferEffectiveDate = getTimePoints().getEffectiveDateForTimePoint(
				TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_OFFER);

		// Create manual entry
		String policyNumber = createFormsSpecificManualEntry(testData, renewalOfferEffectiveDate);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		//needed for home banking form generation
		setUpTriggerHomeBankingConversionRenewal(policyNumber);
		// Add Credit Card payment method and Enable AutoPayment
		Tab.buttonBack.click();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.update().perform(testDataManager.billingAccount.getTestData("Update", "TestData_AddAutopay"));

		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		billGeneration(renewalOfferEffectiveDate);

		//PAS-9607 Verify that packages are generated with correct transaction code
		pas9607_verifyPolicyTransactionCode("STMT", policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);

		//PAS-9816 Verify that Billing Renewal package forms are generated and are in correct order
		pas9816_verifyRenewalBillingPackageFormsPresence(policyNumber, getPolicyType());

		// Start PAS-9816 Scenario 1 Issue first renewal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue.subtract(new Dollar(10)));

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
		PolicySummaryPage.buttonRenewals.click();
		productRenewalsVerifier.setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		/**https://csaaig.atlassian.net/browse/PAS-9157*/
		/**PAS-10256
		 Cannot rate Home SS policy with effective date higher or equal to 2020-02-018*/

		//Generate Bill for the second renewal to verify Home Banking forms
		billGeneration(renewalOfferEffectiveDate.plusYears(1));
		// Shouldn't be after second renewal
		pas9816_verifyBillingRenewalPackageAbsence(policyNumber);

		//PAS-9607 Verify that packages are generated with correct transaction code
		String policyTransactionCode = getPackageTag(policyNumber, "PlcyTransCd", AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		assertThat(policyTransactionCode.equals("STMT") || policyTransactionCode.equals("0210")).isEqualTo(true);
	}

	public void pas9816_verifyBillingRenewalPackageAbsence(String policyNumber) {
		List<Document> billingDocumentsListAfterSecondRenewal = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		assertThat(billingDocumentsListAfterSecondRenewal).isNotEmpty().isNotNull();

		List<String> billingFormsAfterSecondRenewal = new ArrayList<>();
		billingDocumentsListAfterSecondRenewal.forEach(doc -> billingFormsAfterSecondRenewal.add(doc.getTemplateId()));

		assertThat(billingFormsAfterSecondRenewal).doesNotContain(
				DocGenEnum.Documents.HSRNHBXX.getIdInXml(),
				DocGenEnum.Documents.HSRNHBPUP.getIdInXml());
	}

	public void pas2674_verifyConversionRenewalPackageAbsence(List<String> forms, List<Document> actualDocumentsListAfterFirstRenewal) {
		assertThat(actualDocumentsListAfterFirstRenewal).isNotEmpty().isNotNull();

		List<String> listOfFormsAfterSecondRenewal = new ArrayList<>();
		actualDocumentsListAfterFirstRenewal.forEach(doc -> listOfFormsAfterSecondRenewal.add(doc.getTemplateId()));

		// Remove renewal specific forms
		List<String> getOnlyConversionSpecificForms = getOnlyRenewalSpecificForms(forms);

		assertThat(listOfFormsAfterSecondRenewal).doesNotContainAnyElementsOf(getOnlyConversionSpecificForms);
	}

	public void pas9607_verifyPolicyTransactionCode(String expectedCode, String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		String policyTransactionCode = getPackageTag(policyNumber, "PlcyTransCd", eventName);
		assertThat(policyTransactionCode).isEqualTo(expectedCode);
	}

	public void pas9816_verifyRenewalBillingPackageFormsPresence(String policyNumber, PolicyType policyType) {
		List<String> expectedFormsAndOrder = new ArrayList<>(Arrays.asList(
				DocGenEnum.Documents.AHRBXX.getIdInXml(),
				DocGenEnum.Documents.AH35XX.getIdInXml()
		));

		//Adding of 'Delta' form for PUP and Home products in the Forms List
		if (!policyType.equals(PolicyType.PUP)) {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBXX.getIdInXml());
		} else {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBPUP.getIdInXml());
		}

		List<Document> actualConversionRenewalBillingDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		verifyFormSequence(expectedFormsAndOrder, actualConversionRenewalBillingDocumentsList);
	}

	private String createFormsSpecificManualEntry(TestData testData, LocalDateTime renewalOfferEffectiveDate) {
		String membershipFieldMetaKey =
				TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

		mainApp().open();
		if (getState().equals(Constants.States.NJ)) {
			createCustomerIndividual(getCustomerIndividualTD("DataGather", "TestData")
					.adjust(TestData.makeKeyPath("GeneralTab", "Date of Birth"), TimeSetterUtil.getInstance().getCurrentTime().minusYears(65)
							.format(DateTimeUtils.MM_DD_YYYY))); // if NJ adjust Date Of Birth
		} else {
			createCustomerIndividual();
		}

		// adjust with real policies if PUP )
		if (getPolicyType().equals(PolicyType.PUP)) {
			testData = new PrefillTab().adjustWithRealPolicies(testData, getPrimaryPoliciesForPup());
		}

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), renewalOfferEffectiveDate);
		// Needed for Membership AHMVCNV form, membership number have to be != active For all products except PUP
		if (!getPolicyType().equals(PolicyType.PUP)) {
			testData.adjust(membershipFieldMetaKey, "4290072030989503");
		}
		policy.getDefaultView().fill(testData);

		return PolicySummaryPage.getPolicyNumber();
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

	/**
	 * Verify that PolicyDetails value is present in the Package
	 */
	private String getPackageTag(String policyNumber, String tag, AaaDocGenEntityQueries.EventNames name) throws NoSuchFieldException {
		return getPackageDataElemByName(policyNumber, "PolicyDetails", tag, name);
	}

	private String getSourcePolicyNumber(String policyNumber) {
		String sourcePolicyNumberValue = DBService.get().getValue(String.format(SELECT_POLICY_SOURCE_NUMBER, policyNumber)).orElse(null);
		assertThat(sourcePolicyNumberValue).isNotEqualTo(null);
		return sourcePolicyNumberValue;
	}

	public void verifyFormSequence(List<String> expectedFormsOrder, List<Document> documentList) {
		Assertions.assertThat(documentList).isNotEmpty().isNotNull();
		assertSoftly(softly -> {
			// Check that all documents where generated
			List<String> allDocs = new ArrayList<>();
			documentList.forEach(doc -> allDocs.add(doc.getTemplateId()));
			assertThat(allDocs).containsAll(expectedFormsOrder);
			// Get all docs +  sequence number
			HashMap<Integer, String> actualDocuments = new HashMap<>();
			documentList.forEach(doc -> actualDocuments.put(Integer.parseInt(doc.getSequence()), doc.getTemplateId()));
			// Sort keys
			List<Integer> sortedKeys = new ArrayList(actualDocuments.keySet());
			Collections.sort(sortedKeys);
			// Get documents order by sequence number
			List<String> actualOrder = new ArrayList<>();
			sortedKeys.forEach(sequenceId -> actualOrder.add(actualDocuments.get(sequenceId)));
			// Get Intersection order
			List<String> intersectionsWithActualList = actualOrder.stream().filter(expectedFormsOrder::contains).collect(Collectors.toList());
			// Check sequence
			softly.assertThat(intersectionsWithActualList).isEqualTo(expectedFormsOrder);
		});
	}

	public void setUpTriggerHomeBankingConversionRenewal(String policyNumber) {
		String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));

		int a = DBService.get().executeUpdate(String.format(INSERT_HOME_BANKING_FOR_POLICY, getSourcePolicyNumber(policyNumber), currentDate));
		assertThat(a).isGreaterThan(0).as("MaigManualConversionHelper# setUpTriggerHomeBankingConversionRenewal method failed, value was not inserted in DB");
	}

	/* Data */
	private List<String> getConversionSpecificGeneratedForms(boolean mortgageePaymentPlanPresence, boolean specificProductCondition) {

		List<String> forms = new ArrayList<>();

		switch (getPolicyType().getShortName()) {
			case "HomeSS":
				if (Constants.States.NJ.equals(getState())) {
					forms = getHO3NJConversionSpecificForms();
				} else {
					forms = getHO3OtherStatesConversionSpecificForms();
				}
				break;
			case "HomeSS_HO4":
				if (Constants.States.NJ.equals(getState())) {
					forms = getHO4NJConversionSpecificForms();
				} else {
					forms = getHO4OtherStatesConversionSpecificForms();
				}
				break;
			case "HomeSS_HO6":
				if (Constants.States.NJ.equals(getState())) {
					forms = getHO6NJConversionSpecificForms();
				} else {
					forms = getHO6OtherStatesConversionSpecificForms();
				}
				break;
			case "HomeSS_DP3":
				if (Constants.States.NJ.equals(getState())) {
					forms = getDP3NJConversionSpecificForms();
				} else {
					forms = getDP3OtherStatesConversionSpecificForms();
				}
				break;
			case "PUP":
				if (Constants.States.NJ.equals(getState())) {
					forms = getPupNJConversionSpecificForms();
				} else {
					forms = getPupOtherStatesConversionSpecificForms();
				}
				break;
		}

		//"HO3","H04","HO6","DP3" if test data has Mortgagee payment plan, swap first form in sequence to HSRNHODPXX
		if(specificProductCondition && mortgageePaymentPlanPresence){
			forms.set(0,DocGenEnum.Documents.HSRNMXX.getIdInXml());
		}
		//"HO3","H04","HO6","DP3" swap first form in sequence to HSRNMXX
		else if(specificProductCondition && !mortgageePaymentPlanPresence){
			forms.set(0,DocGenEnum.Documents.HSRNHODPXX.getIdInXml());
		}

		return forms;
	}

	private List<String> getOnlyRenewalSpecificForms(List<String> forms) {
		List<String> getOnlyConversionSpecificForms = new ArrayList<>(forms);
		getOnlyConversionSpecificForms.removeAll(
				Arrays.asList(
						DocGenEnum.Documents.HSTP.getIdInXml(),
						DocGenEnum.Documents.AHPNXX.getIdInXml(),
						DocGenEnum.Documents.HS02.getIdInXml(),
						DocGenEnum.Documents.HS02_4.getIdInXml(),
						DocGenEnum.Documents.HS02_6.getIdInXml(),
						DocGenEnum.Documents.HSCSNA.getIdInXml(),
						DocGenEnum.Documents.PS02.getIdInXml(),
						DocGenEnum.Documents.DS02.getIdInXml()
				));
		return getOnlyConversionSpecificForms;
	}

	private List<String> getHO3NJConversionSpecificForms() {
		return Arrays.asList(
				"stub", // will be replaced to HSRNHODPXX or HSRNMXX
				DocGenEnum.Documents.HSTP.getIdInXml(),
				DocGenEnum.Documents.HS02.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(),
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml(),
				DocGenEnum.Documents.HSCSNA.getIdInXml()
		);
	}

	private List<String> getHO3OtherStatesConversionSpecificForms() {
		return Arrays.asList(
				"stub", // will be replaced to HSRNHODPXX or HSRNMXX
				DocGenEnum.Documents.HS02.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(),
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml()
		);
	}

	private List<String> getHO4NJConversionSpecificForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNHODPXX.getIdInXml(),
				DocGenEnum.Documents.HSTP.getIdInXml(),
				DocGenEnum.Documents.HS02_4.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(), //membership validation
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml() //multi policy discount
				//todo add HSCSNB
		);
	}

	private List<String> getHO4OtherStatesConversionSpecificForms() {
		return Arrays.asList(
				"stub", // will be replaced to HSRNHODPXX or HSRNMXX
				DocGenEnum.Documents.HS02_4.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(), //membership validation
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml() //multi policy discount
		);
	}

	private List<String> getHO6NJConversionSpecificForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNHODPXX.getIdInXml(), // will be replaced to HSRNHODPXX or HSRNMXX
				DocGenEnum.Documents.HSTP.getIdInXml(),
				DocGenEnum.Documents.HS02_6.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(),
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml()
		);
	}

	private List<String> getHO6OtherStatesConversionSpecificForms() {
		return Arrays.asList(
				"stub", // will be replaced to HSRNHODPXX or HSRNMXX
				DocGenEnum.Documents.HS02_6.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(),
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml()
				//todo add HSCSNB
		);
	}

	private List<String> getPupNJConversionSpecificForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNPUPXX.getIdInXml(),
				DocGenEnum.Documents.HSTP.getIdInXml(),
				DocGenEnum.Documents.PS02.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml()
		);
	}

	private List<String> getPupOtherStatesConversionSpecificForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNPUPXX.getIdInXml(),
				DocGenEnum.Documents.PS02.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml()
		);
	}

	private List<String> getDP3NJConversionSpecificForms() {
		return Arrays.asList(
				"stub", // will be replaced to HSRNHODPXX or HSRNMXX
				DocGenEnum.Documents.HSTP.getIdInXml(),
				DocGenEnum.Documents.DS02.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(),
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml()
		);
	}

	private List<String> getDP3OtherStatesConversionSpecificForms() {
		return Arrays.asList(
				"stub", // will be replaced to HSRNHODPXX or HSRNMXX
				DocGenEnum.Documents.DS02.getIdInXml(),
				DocGenEnum.Documents.AHAUXX.getIdInXml(),
				DocGenEnum.Documents.AHPNXX.getIdInXml(),
				DocGenEnum.Documents.AHMVCNV.getIdInXml(),
				DocGenEnum.Documents.HSMPDCNVXX.getIdInXml()
		);
	}

	public TestData getTestDataWithAdditionalInterest(TestData policyTD) {
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

	public TestData adjustWithMortgageeData(TestData policyTD) {
		TestData testDataMortgagee = getTestSpecificTD("MortgageesTab");
		//adjust TestData with Premium and Coverage tab data
		TestData testDataPremiumTabWithMortgageePaymentPlan = getTestSpecificTD("PremiumsAndCoveragesQuoteTab_Mortgagee");
		return policyTD.adjust(new MortgageesTab().getMetaKey(), testDataMortgagee).adjust(new PremiumsAndCoveragesQuoteTab().getMetaKey(), testDataPremiumTabWithMortgageePaymentPlan);
	}

}
