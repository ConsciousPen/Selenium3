package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.AAAEndorseResponse;
import aaa.modules.regression.service.helper.dtoDxp.AAAVehicleVinInfoRestResponseWrapper;
import aaa.modules.regression.service.helper.dtoDxp.ValidateEndorsementResponse;
import aaa.modules.regression.service.helper.dtoDxp.Vehicle;
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
		convertAgentEndorsementToSystemEndorsement(policyNumber);

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);

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
			softly.assertThat(response.ruleSets.get(0).errors.get(0)).contains("OOSE or Future Dated Endorsement Exists");
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
		//TODO will fail here due to not in policy term
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
		//String policyNumber = "UTSS926232055";
		int numberOfDaysDelayBeforeDelete = 2;
		LocalDateTime testStartDate = TimeSetterUtil.getInstance().getCurrentTime();
		//String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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
		assertSoftly(softly ->
				softly.assertThat(responseValidateCanCreateEndorsement3.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle")
		);
	}

	protected void pas8784_endorsementValidateStateSpecificConfigVersioning(PolicyType policyType) {
		//String policyNumber = "AZSS926232058";
		int numberOfDaysDelayBeforeDelete = 5;
		int numberOfDaysForNewConfigVersion = 10;
		LocalDateTime testStartDate = TimeSetterUtil.getInstance().getCurrentTime();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//New Config Version testing for AZ = 0 days delay
		AAAEndorseResponse responseNewConfigEffective = HelperCommon.executeEndorseStart(policyNumber, endorsementDate);
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
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, null);
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
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete + numberOfDaysDelayBeforeDelete + 1));
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

	private void manualPendedEndorsementCreate() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(getPremiumAndCoverageTab());//to get status = Premium Calculated
		getPremiumAndCoverageTabElement().saveAndExit();
	}

	protected void pas8275_vinValidateCheck(PolicyType policyType) {
		mainApp().open();
/*		createCustomerIndividual();
		policyType.get().createQuote(getPolicyTD());
		String policyNumber = PolicySummaryPage.getPolicyNumber();*/

		String policyNumber = "VASS926232058";

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
		createdEndorsementTransactionProperties("Premium Calculated", TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY), "QA QA user");

		//Start endorsement service call
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, endorsementDate);
		assertSoftly(softly ->
				softly.assertThat(response.policyNumber).isEqualTo(policyNumber)
		);

		//check that new endorsement was created
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		createdEndorsementTransactionProperties("Gathering Info", TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY), "MyPolicy MyPolicy");
	}

	//***********************************
	protected void pas8273_CheckIfOnlyActiveVehiclesAreAllowed(PolicyType policyType) {

	/*	mainApp().open();
		createCustomerIndividual();

		VehicleTab vehicleTab = new VehicleTab();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AllVehicles").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		String v1_modelYear = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.YEAR.getLabel()).getValue();
		String v1_manufacturer = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue();
		String v1_series = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.SERIES.getLabel()).getValue();
		String v1_model = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue();
		String v1_bodyStyle = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue();

		VehicleTab.tableVehicleList.selectRow(2);

		String v2_modelYear = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.YEAR.getLabel()).getValue();
		String v2_manufacturer = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue();
		String v2_series = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.SERIES.getLabel()).getValue();
		String v2_model = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue();
		String v2_bodyStyle = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue();*/

		String policyNumber = "VASS926232070";

		Vehicle[] response = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response).isEqualTo("fdgsg");
			/*softly.assertThat(response.manufacturer).isEqualTo("fdhfd");*/
			/*softly.assertThat(response.ruleSets.get(0).series).isEqualTo(v1_series);
			softly.assertThat(response.ruleSets.get(0).model).isEqualTo(v1_model);
			softly.assertThat(response.ruleSets.get(0).bodyStyle).isEqualTo(v1_bodyStyle);
			softly.assertThat(response.ruleSets.get(1).modelYear).isEqualTo(v2_modelYear);
			softly.assertThat(response.ruleSets.get(1).manufacturer).isEqualTo(v2_manufacturer);
			softly.assertThat(response.ruleSets.get(1).series).isEqualTo(v2_series);
			softly.assertThat(response.ruleSets.get(1).model).isEqualTo(v2_model);
			softly.assertThat(response.ruleSets.get(1).bodyStyle).isEqualTo(v2_bodyStyle);
*/
		});

/*
		VehicleTab.buttonCancel.click();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue("1FMEU15H7KLB19840");

		//CIA

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.calculatePremium();
		Tab.buttonSaveAndExit.click();

		//CIA

		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.policyInquiry().start();

		VehicleTab.tableVehicleList.selectRow(2);

		v2_modelYear = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.YEAR.getLabel()).getValue();
		v2_manufacturer = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.MAKE.getLabel()).getValue();
		v2_series = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.SERIES.getLabel()).getValue();
		v2_model = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.MODEL.getLabel()).getValue();
		v2_bodyStyle = vehicleTab.getInquiryAssetList().getStaticElement(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()).getValue();

		//CIA

		//    TestData tdEndorsement = getTestSpecificTD("TestData_Endorsement").getTestData("VehicleTab").resolveLinks()
		//          .adjust(getPolicyTD("Endorsement", "TestData"));
		//   policy.endorse().performAndFill(tdEndorsement);
*/

	}

	//***********************************
	private void createdEndorsementTransactionProperties(String status, String date, String user) {
		PolicySummaryPage.buttonPendedEndorsement.click();
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").verify.value("Premium Calculated");
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Eff. Date").verify.value(TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY));
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Last Performer").verify.value("QA QA user");
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
