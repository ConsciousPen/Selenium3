package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.AAAEndorseResponse;
import aaa.modules.regression.service.helper.dtoDxp.AAAVehicleVinInfoRestResponseWrapper;
import aaa.modules.regression.service.helper.dtoDxp.ValidateEndorsementResponse;
import aaa.modules.regression.service.helper.dtoDxp.Vehicle;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesNonPremiumBearingAbstract extends PolicyBaseTest {

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getVehicleTabElement();

	protected abstract AssetDescriptor<Button> getCalculatePremium();

	protected void pas1441_emailChangeOutOfPasTestBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//BUG PAS-5815 There is an extra Endorse action available for product
		NavigationPage.comboBoxListAction.verify.noOption("Endorse");

		//will be used to check PAS-6364 Sleepy hollow: when doing Service Endorsement after regular endorsement, components are loaded in incorrect order
		secondEndorsementIssueCheck();

		//PAS-343 start
		String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "%%");
		int numberOfDocumentsRecordsInDb = Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get());
		//PAS-343 end

		String emailAddressChanged = "osi.test@email.com";
		String authorizedBy = "John Smith";
		HelperCommon.executeContactInfoRequest(policyNumber, emailAddressChanged, authorizedBy);

		emailUpdateTransactionHistoryCheck(policyNumber);
		emailAddressChangedInEndorsementCheck(emailAddressChanged, authorizedBy);

		//PAS-343 start
		CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get()), numberOfDocumentsRecordsInDb);
		//PAS-343 end

		secondEndorsementIssueCheck();
	}

	protected void pas6560_endorsementValidateAllowedNoEffectiveDate(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas6560_endorsementValidateAllowed(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		secondEndorsementIssueCheck();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas6562_endorsementValidateNotAllowedNano(PolicyType policyType, String state) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testDataManager.getDefault(TestPolicyNano.class).getTestData("TestData_" + state));
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements).isEmpty();
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors.get(0)).contains("NANO Policy");
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas6560_endorsementValidateAllowedPendedEndorsementUser(PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Endorsement creation
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getDocumentsAndBindTabElement().saveAndExit();
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(true);

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas6562_endorsementValidateNotAllowedPendedEndorsementSystem(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Endorsement creation
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getDocumentsAndBindTabElement().saveAndExit();
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(true);
		//Update to make the endorsement SYSTEM
		String getPolicySummaryId = "select id from (\n"
				+ "select ps.id, ps.SYSGENERATEDTXIND\n"
				+ "from policysummary ps\n"
				+ "where policynumber = '%s'\n"
				+ "order by id desc)\n"
				+ "where rownum = 1 ";
		String updateSystemGeneratedInd = "update policysummary ps\n"
				+ "set ps.SYSGENERATEDTXIND = 1\n"
				+ "where id = %s";

		String policySummaryId = DBService.get().getValue(String.format(getPolicySummaryId, policyNumber)).get();
		DBService.get().executeUpdate(String.format(updateSystemGeneratedInd, policySummaryId));

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		//TODO should fail here
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements).isEmpty();
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors.get(0)).contains("System Created Pended Endorsement");
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas6562_endorsementValidateNotAllowedFutureDatedEndorsement(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Future Dated Endorsement creation
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(getPremiumAndCoverageTab());
		getDocumentsAndBindTabElement().saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements).isEmpty();
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors.get(0)).contains("OOSE or Future Dated Endorsement Exists");
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas6562_endorsementValidateNotAllowedUBI(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createQuote(getPolicyTD());

		policy.dataGather().start();
		NavigationPage.toViewTab(getVehicleTab());
		getVehicleTabElement().getAssetList().getAsset(ENROLL_IN_USAGE_BASED_INSURANCE.getLabel(), RadioGroup.class).setValue("Yes");
		getVehicleTabElement().getAssetList().getAsset(GET_VEHICLE_DETAILS.getLabel(), Button.class).click();
		getVehicleTabElement().getAssetList().getAsset(VEHICLE_ELIGIBILITY_RESPONCE.getLabel(), ComboBox.class).setValue("Vehicle Eligible");
		getVehicleTabElement().getAssetList().getAsset(GRANT_PATRITIPATION_DISCOUNT.getLabel(), Link.class).click();
		NavigationPage.toViewTab(getPremiumAndCoverageTab());
		getPremiumAndCoverageTabElement().saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements).isEmpty();
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors.get(0)).contains("UBI Vehicle");
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas6562_endorsementValidateNotAllowedOutOfBound(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		//BUG OSI: new story PAS-9337 Green Button Service - Abracradabra
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements).isEmpty();
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(0).warnings).isEmpty();
			softly.assertThat(response.ruleSets.get(1).name).isEqualTo("VehicleRules");
			softly.assertThat(response.ruleSets.get(1).errors).isEmpty();
			softly.assertThat(response.ruleSets.get(1).warnings).isEmpty();
		});
	}

	protected void pas8784_endorsementValidateNotAllowedCustomer(PolicyType policyType) {
		int numberOfDaysDelayBeforeDelete = 2;
		LocalDateTime testStartDate = TimeSetterUtil.getInstance().getCurrentTime();

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, null);
		assertSoftly(softly ->
				softly.assertThat(response.policyNumber).isEqualTo(policyNumber)
		);

		//immediate endorsement delete attempt should not be allowed for UT
		ValidateEndorsementResponse responseValidateCanCreateEndorsement1 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement1.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement1.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement1.ruleSets.get(0).errors.get(0)).contains("Customer Created Endorsement");
		});

		//endorsement delete attempt should not be allowed on the Delay Day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement2 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement2.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).errors.get(0)).contains("Customer Created Endorsement");
		});

		//endorsement delete attempt should be allowed on the Delay Day + 1 day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete + 1));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertThat(responseValidateCanCreateEndorsement3.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
	}

	protected void pas8784_endorsementValidateStateSpecificConfigVersioning(PolicyType policyType) {
		int numberOfDaysDelayBeforeDelete = 5;
		int numberOfDaysForNewConfigVersion = 10;
		LocalDateTime testStartDate = TimeSetterUtil.getInstance().getCurrentTime();

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//New Config Version testing for AZ = 0 days delay
		AAAEndorseResponse responseNewConfigEffective = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertSoftly(softly ->
				softly.assertThat(responseNewConfigEffective.policyNumber).isEqualTo(policyNumber)
		);
		//validation returns "can be deleted"
		ValidateEndorsementResponse responseValidateCanCreateEndorsementNewConfigEffective = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly ->
				softly.assertThat(responseValidateCanCreateEndorsementNewConfigEffective.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle")
		);

		//shift time till different config becomes current for AZ = 5 days delay , delete old endorsement, add new endorsement
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysForNewConfigVersion + 1));
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertSoftly(softly ->
				softly.assertThat(response.policyNumber).isEqualTo(policyNumber)
		);

		//endorsement delete attempt should not be allowed on the Delay Day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysForNewConfigVersion + numberOfDaysDelayBeforeDelete));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement2 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement2.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).errors.get(0)).contains("Customer Created Endorsement");
		});

		//endorsement delete attempt should be allowed on the Delay Day + 1 day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysForNewConfigVersion + numberOfDaysDelayBeforeDelete + 1));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly ->
				softly.assertThat(responseValidateCanCreateEndorsement3.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle")
		);
	}

	protected void pas8784_endorsementValidateNoDelayAllowedAgent(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		manualPendedEndorsementCreate();

		//endorsement delete attempt should be allowed on the Delay Day + 1 day
		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly ->
				softly.assertThat(responseValidateCanCreateEndorsement3.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle")
		);
	}

	protected void pas8784_endorsementValidateNoDelayNotAllowedSystem(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		manualPendedEndorsementCreate();
		convertAgentEndorsementToSystemEndorsement(policyNumber);

		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement3.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement3.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement3.ruleSets.get(0).errors.get(0)).contains("System Created Pended Endorsement");
		});
	}

	protected void pas9997_paymentPlansLookup() {
		String ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_ADD_WY = "INSERT INTO LOOKUPVALUE\n"
				+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
				+ "values\n"
				+ "('BaseProductLookupValue', 'XXXX', 'FALSE', 'AAA_SS', 'WY', (select SYSDATE-10 from dual), (select SYSDATE-5 from dual),\n"
				+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentPlans'))";
		String ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_CHANGE_WY = "INSERT INTO LOOKUPVALUE\n"
				+ "(dtype, code, displayValue, productCd, riskStateCd, EFFECTIVE, EXPIRATION, lookuplist_id)\n"
				+ "values\n"
				+ "('BaseProductLookupValue', 'annualSS', 'FALSE', 'AAA_SS', 'WY', (select SYSDATE-4 from dual), null,\n"
				+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAAeValueQualifyingPaymentPlans'))";

		String DELETE_ADDED_CONFIGS_WY = "delete lookupvalue\n"
				+ "where 1=1\n"
				+ "and lookuplist_id = (select id from lookuplist where lookupname = 'AAAeValueQualifyingPaymentPlans')\n"
				+ "and riskstatecd = 'WY'";

		assertSoftly(softly -> {
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_ADD_WY);
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_CHANGE_WY);

			//mainApp().open();
			String lookupName1 = "AAAeValueQualifyingPaymentPlans";
			String lookupName2 = "AAAeValueQualifyingPaymentMethods";
			String productCd = "AAA_SS";
			String riskStateCd = "WY";

			String effectiveDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup1 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate1);
			softly.assertThat(responseValidateLookup1.get("annualSS").equals("FALSE")).isTrue();
			softly.assertThat(responseValidateLookup1.get("semiAnnual6SS").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup1.get("annualSS_R").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup1.get("semiAnnual6SS_R").equals("TRUE")).isTrue();
			assertThat(responseValidateLookup1.size()).isEqualTo(4);
			responseValidateLookup1.values();
			responseValidateLookup1.keySet();

			HashMap<String, String> responseValidateLookup = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate1);
			softly.assertThat(responseValidateLookup.containsKey("pciDebitCard")).isTrue();
			softly.assertThat(responseValidateLookup.get("pciDebitCard").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup.get("pciCreditCard").equals("FALSE")).isTrue();
			softly.assertThat(responseValidateLookup.get("eft").equals("TRUE")).isTrue();
			assertThat(responseValidateLookup.size()).isEqualTo(3);
			responseValidateLookup.values();
			responseValidateLookup.keySet();



			String effectiveDate2 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup21 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate2);
			softly.assertThat(responseValidateLookup21.get("annualSS").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup21.get("semiAnnual6SS").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup21.get("annualSS_R").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup21.get("semiAnnual6SS_R").equals("TRUE")).isTrue();
			assertThat(responseValidateLookup1.size()).isEqualTo(4);
			responseValidateLookup1.values();
			responseValidateLookup1.keySet();

			HashMap<String, String> responseValidateLookup22 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate2);
			softly.assertThat(responseValidateLookup22.containsKey("pciDebitCard")).isTrue();
			softly.assertThat(responseValidateLookup22.get("pciDebitCard").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup22.get("pciCreditCard").equals("FALSE")).isTrue();
			softly.assertThat(responseValidateLookup22.get("eft").equals("TRUE")).isTrue();
			assertThat(responseValidateLookup.size()).isEqualTo(3);
			responseValidateLookup.values();
			responseValidateLookup.keySet();

			String effectiveDate3 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup31 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate3);
			softly.assertThat(responseValidateLookup31.get("annualSS").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup31.get("semiAnnual6SS").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup31.get("annualSS_R").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup31.get("semiAnnual6SS_R").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup31.get("XXXX").equals("FALSE")).isTrue();
			assertThat(responseValidateLookup31.size()).isEqualTo(5);
			responseValidateLookup1.values();
			responseValidateLookup1.keySet();

			HashMap<String, String> responseValidateLookup32 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate3);
			softly.assertThat(responseValidateLookup32.containsKey("pciDebitCard")).isTrue();
			softly.assertThat(responseValidateLookup32.get("pciDebitCard").equals("TRUE")).isTrue();
			softly.assertThat(responseValidateLookup32.get("pciCreditCard").equals("FALSE")).isTrue();
			softly.assertThat(responseValidateLookup32.get("eft").equals("TRUE")).isTrue();
			assertThat(responseValidateLookup.size()).isEqualTo(3);
			responseValidateLookup.values();
			responseValidateLookup.keySet();


		DBService.get().executeUpdate(DELETE_ADDED_CONFIGS_WY);
		});
	}

	private void manualPendedEndorsementCreate() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(getPremiumAndCoverageTab());//to get status = Premium Calculated
		getPremiumAndCoverageTabElement().saveAndExit();
	}

	protected void pas8275_vinValidateCheck(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String vin1 = "aaaa"; //VIN too short
		AAAVehicleVinInfoRestResponseWrapper response = HelperCommon.executeVinValidate(policyNumber, vin1, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response.getVehicles()).isEmpty();
			softly.assertThat(response.getValidationMessage()).isEqualTo("Invalid VIN length");
		});

		String vin2 = "12345678901234567890"; //VIN too long
		AAAVehicleVinInfoRestResponseWrapper response2 = HelperCommon.executeVinValidate(policyNumber, vin2, null);
		assertSoftly(softly -> {
			softly.assertThat(response2.getVehicles()).isEmpty();
			softly.assertThat(response2.getValidationMessage()).isEqualTo("Invalid VIN length");
		});

		String vin3 = "1D30E42K451234567"; //VIN check digit failed
		AAAVehicleVinInfoRestResponseWrapper response3 = HelperCommon.executeVinValidate(policyNumber, vin3, null);
		assertSoftly(softly -> {
			softly.assertThat(response3.getVehicles()).isEmpty();
			softly.assertThat(response3.getValidationMessage()).isEqualTo("Check Digit is Incorrect");
		});

		String vin4 = "1D30E42K45"; //VIN from VIN table but too short
		AAAVehicleVinInfoRestResponseWrapper response4 = HelperCommon.executeVinValidate(policyNumber, vin4, null);
		assertSoftly(softly -> {
			softly.assertThat(response4.getVehicles()).isEmpty();
			softly.assertThat(response4.getValidationMessage()).isEqualTo("Invalid VIN length");
		});

		String vin5 = "1D30E42J451234567"; //VIN NOT from VIN table to Check VIN service
		AAAVehicleVinInfoRestResponseWrapper response5 = HelperCommon.executeVinValidate(policyNumber, vin5, null);
		assertSoftly(softly -> {
			softly.assertThat(response5.getVehicles()).isEmpty();
			softly.assertThat(response5.getValidationMessage()).isEqualTo("VIN is not on AAA VIN Table");
		});

		String vin0 = "1D30E42K351234567"; //VIN from VIN table
		AAAVehicleVinInfoRestResponseWrapper response0 = HelperCommon.executeVinValidate(policyNumber, vin0, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response0.getVehicles().get(0).getVin()).isNotEmpty();
			softly.assertThat(response0.getVehicles().get(0).getYear().toString()).isNotEmpty();
			softly.assertThat(response0.getVehicles().get(0).getMake()).isNotEmpty();
			softly.assertThat(response0.getVehicles().get(0).getModelText()).isNotEmpty();
			softly.assertThat(response0.getVehicles().get(0).getSeriesText()).isNotEmpty();
			softly.assertThat(response0.getVehicles().get(0).getBodyStyleCd()).isNotEmpty();
			softly.assertThat(response0.getValidationMessage()).isEmpty();
		});
	}

	protected void pas7332_deletePendingEndorsementStartNewEndorsementThroughService(PolicyType policyType, String endorsementType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Pended Endorsement creation
		manualPendedEndorsementCreate();
		if ("System".equals(endorsementType)) {
			convertAgentEndorsementToSystemEndorsement(policyNumber);
		}
		pas8785_createdEndorsementTransactionProperties("Premium Calculated", TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY), "QA QA user");

		//Start endorsement service call
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, endorsementDate);
		assertSoftly(softly ->
				softly.assertThat(response.policyNumber).isEqualTo(policyNumber)
		);

		//check that new endorsement was created
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		pas8785_createdEndorsementTransactionProperties("Gathering Info", TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY), "MyPolicy MyPolicy");
	}

	protected void pas8273_CheckIfOnlyActiveVehiclesAreAllowed(PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();

		VehicleTab vehicleTab = new VehicleTab();
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AllVehicles").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		//BUG PAS-9722 Random sequence for Vehicles on DXP
		String modelYear1 = vehicleTab.getInquiryAssetList().getStaticElement(YEAR.getLabel()).getValue();
		String manufacturer1 = vehicleTab.getInquiryAssetList().getStaticElement(MAKE.getLabel()).getValue();
		String series1 = vehicleTab.getInquiryAssetList().getStaticElement(SERIES.getLabel()).getValue();
		String model1 = vehicleTab.getInquiryAssetList().getStaticElement(MODEL.getLabel()).getValue();
		String bodyStyle1 = vehicleTab.getInquiryAssetList().getStaticElement(BODY_STYLE.getLabel()).getValue();

		VehicleTab.tableVehicleList.selectRow(2);

		String modelYear2 = vehicleTab.getInquiryAssetList().getStaticElement(YEAR.getLabel()).getValue();
		String manufacturer2 = vehicleTab.getInquiryAssetList().getStaticElement(MAKE.getLabel()).getValue();
		String series2 = vehicleTab.getInquiryAssetList().getStaticElement(SERIES.getLabel()).getValue();
		String model2 = vehicleTab.getInquiryAssetList().getStaticElement(MODEL.getLabel()).getValue();
		String bodyStyle2 = vehicleTab.getInquiryAssetList().getStaticElement(BODY_STYLE.getLabel()).getValue();

		Vehicle[] response = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			//BUG PAS-9722 Random sequence for Vehicles on DXP
			softly.assertThat(response[0].getModelYear()).isEqualTo(modelYear1);
			softly.assertThat(response[0].getManufacturer()).isEqualTo(manufacturer1);
			softly.assertThat(response[0].getSeries()).isEqualTo(series1);
			softly.assertThat(response[0].getModel()).isEqualTo(model1);
			softly.assertThat(response[0].getBodyStyle()).isEqualTo(bodyStyle1);

			softly.assertThat(response[1].getModelYear()).isEqualTo(modelYear2);
			softly.assertThat(response[1].getManufacturer()).isEqualTo(manufacturer2);
			softly.assertThat(response[1].getSeries()).isEqualTo(series2);
			softly.assertThat(response[1].getModel()).isEqualTo(model2);
			softly.assertThat(response[1].getBodyStyle()).isEqualTo(bodyStyle2);
		});

		VehicleTab.buttonCancel.click();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(VIN).setValue("1FMEU15H7KLB19840");
		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		Vehicle[] response1 = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response1[0].getModelYear()).isEqualTo(modelYear1);
			softly.assertThat(response1[0].getManufacturer()).isEqualTo(manufacturer1);
			softly.assertThat(response1[0].getSeries()).isEqualTo(series1);
			softly.assertThat(response1[0].getModel()).isEqualTo(model1);
			softly.assertThat(response1[0].getBodyStyle()).isEqualTo(bodyStyle1);

			softly.assertThat(response1[1].getModelYear()).isEqualTo(modelYear2);
			softly.assertThat(response1[1].getManufacturer()).isEqualTo(manufacturer2);
			softly.assertThat(response1[1].getSeries()).isEqualTo(series2);
			softly.assertThat(response1[1].getModel()).isEqualTo(model2);
			softly.assertThat(response1[1].getBodyStyle()).isEqualTo(bodyStyle2);
		});

		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);

		String modelYear3 = vehicleTab.getInquiryAssetList().getStaticElement(YEAR.getLabel()).getValue();
		String manufacturer3 = vehicleTab.getInquiryAssetList().getStaticElement(MAKE.getLabel()).getValue();
		String series3 = vehicleTab.getInquiryAssetList().getStaticElement(SERIES.getLabel()).getValue();
		String model3 = vehicleTab.getInquiryAssetList().getStaticElement(MODEL.getLabel()).getValue();
		String bodyStyle3 = vehicleTab.getInquiryAssetList().getStaticElement(BODY_STYLE.getLabel()).getValue();

		Vehicle[] response2 = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response2[0].getModelYear()).isEqualTo(modelYear1);
			softly.assertThat(response2[0].getManufacturer()).isEqualTo(manufacturer1);
			softly.assertThat(response2[0].getSeries()).isEqualTo(series1);
			softly.assertThat(response2[0].getModel()).isEqualTo(model1);
			softly.assertThat(response2[0].getBodyStyle()).isEqualTo(bodyStyle1);

			softly.assertThat(response2[1].getModelYear()).isEqualTo(modelYear3);
			softly.assertThat(response2[1].getManufacturer()).isEqualTo(manufacturer3);
			softly.assertThat(response2[1].getSeries()).isEqualTo(series3);
			softly.assertThat(response2[1].getModel()).isEqualTo(model3);
			softly.assertThat(response2[1].getBodyStyle()).isEqualTo(bodyStyle3);
		});
	}

	protected void pas8273_CheckIfNanoPolicyNotReturningVehicle(PolicyType policyType, String state) {

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testDataManager.getDefault(TestPolicyNano.class).getTestData("TestData_" + state));
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		Vehicle[] response = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertThat(response.length == 0).isTrue();
	}

	private void pas8785_createdEndorsementTransactionProperties(String status, String date, String user) {
		PolicySummaryPage.buttonPendedEndorsement.click();
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").verify.value(status);
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Eff. Date").verify.value(date);
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Last Performer").verify.value(user);
	}

	private void convertAgentEndorsementToSystemEndorsement(String policyNumber) {
		String getPolicySummaryId = "select id from (\n"
				+ "select ps.id, ps.SYSGENERATEDTXIND\n"
				+ "from policysummary ps\n"
				+ "where policynumber = '%s'\n"
				+ "order by id desc)\n"
				+ "where rownum = 1 ";
		String updateSystemGeneratedInd = "update policysummary ps\n"
				+ "set ps.SYSGENERATEDTXIND = 1\n"
				+ "where id = %s";

		String policySummaryId = DBService.get().getValue(String.format(getPolicySummaryId, policyNumber)).get();
		DBService.get().executeUpdate(String.format(updateSystemGeneratedInd, policySummaryId));
	}

	private void emailAddressChangedInEndorsementCheck(String emailAddressChanged, String authorizedBy) {
		policy.policyInquiry().start();

		getGeneralTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel()).verify.value(emailAddressChanged);
		NavigationPage.toViewTab(getDocumentsAndBindTab());

		if (getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel()).isPresent()) {
			getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel()).verify.value(emailAddressChanged);
		}
		if (getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY.getLabel()).isPresent()) {
			getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY.getLabel()).verify.value(authorizedBy);
		}
		Tab.buttonCancel.click();
	}

	private void secondEndorsementIssueCheck() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(getPremiumAndCoverageTab());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(getCalculatePremium()).click();
		getPremiumAndCoverageTabElement().saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
	}

	private void emailUpdateTransactionHistoryCheck(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).verify.present();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").verify.value("Email Updated - Exte...");
		Tab.buttonCancel.click();
	}

}
