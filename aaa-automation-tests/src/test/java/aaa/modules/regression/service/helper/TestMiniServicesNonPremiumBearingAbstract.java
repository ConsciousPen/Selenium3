package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static aaa.modules.regression.service.helper.preconditions.TestMiniServicesNonPremiumBearingAbstractPreconditions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.testng.ITestContext;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.MaigConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.modules.policy.home_ss.actiontabs.ReinstatementActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.conversions.auto_ss.MaigConversionTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesNonPremiumBearingAbstract extends PolicyBaseTest {

	private static final String START_ENDORSEMENT_INFO_ERROR_1 = "Cannot endorse policy - policy term does not exist for endorsement date";
	private static final String START_ENDORSEMENT_INFO_ERROR_2 = "Action is not available";
	private static final String START_ENDORSEMENT_INFO_ERROR_3 = "OOSE or Future Dated Endorsement Exists";
	private static final String START_ENDORSEMENT_INFO_ERROR_4 = "Policy is locked";
	private static final String START_ENDORSEMENT_INFO_ERROR_5 = "The requested entity is currently locked by other user";
	private static final String START_ENDORSEMENT_INFO_ERROR_6 = "Could not acquire a new lock: the requested entity is currently locked";
	private static final String START_ENDORSEMENT_INFO_ERROR_7 = "State does not allow endorsements";
	private static final String sessionId1 = "oid1";
	private static final String sessionId2 = "oid2";
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private ErrorTab errorTab = new ErrorTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private VehicleTab vehicleTab = new VehicleTab();

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getVehicleTabElement();

	protected abstract AssetDescriptor<JavaScriptButton> getCalculatePremium();

	protected void pas1441_emailChangeOutOfPasTestBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

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

		HelperCommon.executeContactInfoRequest(policyNumber, emailAddressChanged, authorizedBy);

		//Popup to avoid conflicting transactions
		policy.endorse().start();
		CustomAssert.assertTrue("Policy version you are working with is marked as NOT current (Probable cause - another user working with the same policy). Please reload policy to continue working with it.".equals(Page.dialogConfirmation.labelMessage.getValue()));
		Page.dialogConfirmation.reject();

		SearchPage.openPolicy(policyNumber);
		secondEndorsementIssueCheck();
	}

	protected void pas6560_endorsementValidateAllowedNoEffectiveDate() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		mainApp().close();

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

	protected void pas6560_endorsementValidateAllowed() {
		mainApp().open();
		getCopiedPolicy();
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
		mainApp().close();
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
		getCopiedPolicy();
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
		getCopiedPolicy();
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
		mainApp().close();

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
		getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		//BUG OSI: new story PAS-9337 Green Button Service - Abracradabra
		assertSoftly(softly -> {
			softly.assertThat(response.allowedEndorsements).isEmpty();
			softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(response.ruleSets.get(0).errors.toString().contains(START_ENDORSEMENT_INFO_ERROR_1)).isTrue();
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
		getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete - 1));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement2 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement2.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).errors.get(0)).contains("Customer Created Endorsement");
		});

		//endorsement delete attempt should be allowed on the Delay Day + 1 day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertThat(responseValidateCanCreateEndorsement3.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
	}

	protected void pas8784_endorsementValidateStateSpecificConfigVersioning(PolicyType policyType) {
		int numberOfDaysDelayBeforeDelete = 5;
		int numberOfDaysForNewConfigVersion = 10;
		LocalDateTime testStartDate = TimeSetterUtil.getInstance().getCurrentTime();

		mainApp().open();
		getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

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
		getCopiedPolicy();
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
		getCopiedPolicy();
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

	protected void pas9997_paymentMethodsLookup() {
		assertSoftly(softly -> {
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_ADD_WY);
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_CHANGE_WY);

			mainApp().open();
			String lookupName2 = "AAAeValueQualifyingPaymentMethods";
			String productCd = "AAA_SS";
			String riskStateCd = "WY";

			String effectiveDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup12 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate1);
			softly.assertThat("FALSE".equals(responseValidateLookup12.get("pciDebitCard"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup12.get("pciCreditCard"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup12.get("eft"))).isTrue();
			softly.assertThat(responseValidateLookup12.size()).isEqualTo(3);
			responseValidateLookup12.values();
			responseValidateLookup12.keySet();

			String effectiveDate2 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup22 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate2);
			softly.assertThat("FALSE".equals(responseValidateLookup22.get("pciDebitCard"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup22.get("pciCreditCard"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup22.get("eft"))).isTrue();
			softly.assertThat(responseValidateLookup12.size()).isEqualTo(3);

			String effectiveDate3 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup32 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate3);
			softly.assertThat("TRUE".equals(responseValidateLookup32.get("pciDebitCard"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup32.get("pciCreditCard"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup32.get("eft"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup32.get("XXXX"))).isTrue();
			softly.assertThat(responseValidateLookup32.size()).isEqualTo(4);

			DBService.get().executeUpdate(DELETE_ADDED_PAYMENT_METHOD_CONFIGS_WY);
		});
	}

	protected void pas9997_paymentPlansLookup() {
		assertSoftly(softly -> {
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_ADD_WY);
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_CHANGE_WY);

			mainApp().open();
			String lookupName1 = "AAAeValueQualifyingPaymentPlans";
			String productCd = "AAA_SS";
			String riskStateCd = "WY";

			String effectiveDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup11 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate1);
			softly.assertThat("FALSE".equals(responseValidateLookup11.get("annualSS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup11.get("semiAnnual6SS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup11.get("annualSS_R"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup11.get("semiAnnual6SS_R"))).isTrue();
			softly.assertThat(responseValidateLookup11.size()).isEqualTo(4);
			responseValidateLookup11.values();
			responseValidateLookup11.keySet();

			String effectiveDate2 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup21 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate2);
			softly.assertThat("FALSE".equals(responseValidateLookup21.get("annualSS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup21.get("semiAnnual6SS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup21.get("annualSS_R"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup21.get("semiAnnual6SS_R"))).isTrue();
			softly.assertThat(responseValidateLookup21.size()).isEqualTo(4);

			String effectiveDate3 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup31 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate3);
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("annualSS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("semiAnnual6SS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("annualSS_R"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("semiAnnual6SS_R"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup31.get("XXXX"))).isTrue();
			softly.assertThat(responseValidateLookup31.size()).isEqualTo(5);

			DBService.get().executeUpdate(DELETE_ADDED_PAYMENT_PLANS_CONFIGS_WY);
		});
	}

	private void manualPendedEndorsementCreate() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(getPremiumAndCoverageTab());//to get status = Premium Calculated
		getPremiumAndCoverageTabElement().saveAndExit();
	}

	protected void pas8275_vinValidateCheck(PolicyType policyType) {

		String getAnyActivePolicy = "select ps.policyNumber, ps.POLICYSTATUSCD, ps.EFFECTIVE\n"
				+ "from policySummary ps\n"
				+ "where 1=1\n"
				+ "and ps.policyNumber not like 'Q%'\n"
				+ "and ps.policyNumber like '%SS%'\n"
				+ "and ps.POLICYSTATUSCD = 'issued'\n"
				+ "and to_char(ps.EFFECTIVE, 'yyyy-MM-dd') = to_char(sysdate, 'yyyy-MM-dd')\n"
				+ "and rownum = 1";

		String policyNumber;
		if (DBService.get().getValue(getAnyActivePolicy).isPresent()) {
			policyNumber = DBService.get().getValue(getAnyActivePolicy).get();
		} else {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(getPolicyTD());
			policyNumber = PolicySummaryPage.getPolicyNumber();
		}

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

		String vin3 = "4T1BF1FK0H1234567"; //VIN check digit failed
		AAAVehicleVinInfoRestResponseWrapper response3 = HelperCommon.executeVinValidate(policyNumber, vin3, null);
		assertSoftly(softly -> {
			softly.assertThat(response3.getVehicles()).isEmpty();
			softly.assertThat(response3.getValidationMessage()).isEqualTo("Check Digit is Incorrect");
		});

		String vin4 = "4T1BF1FK0H"; //VIN from VIN table but too short
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

		String vin0 = "4T1BF1FK0HU624693"; //VIN from VIN table
		AAAVehicleVinInfoRestResponseWrapper response0 = HelperCommon.executeVinValidate(policyNumber, vin0, endorsementDate);
		assertSoftly(softly -> {
			softly.assertThat(response0.getVehicles().get(0).getVin()).isEqualTo(vin0);
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
		String vehIdentificationNo1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		VehicleTab.tableVehicleList.selectRow(2);

		String modelYear2 = vehicleTab.getInquiryAssetList().getStaticElement(YEAR.getLabel()).getValue();
		String manufacturer2 = vehicleTab.getInquiryAssetList().getStaticElement(MAKE.getLabel()).getValue();
		String series2 = vehicleTab.getInquiryAssetList().getStaticElement(SERIES.getLabel()).getValue();
		String model2 = vehicleTab.getInquiryAssetList().getStaticElement(MODEL.getLabel()).getValue();
		String bodyStyle2 = vehicleTab.getInquiryAssetList().getStaticElement(BODY_STYLE.getLabel()).getValue();
		String vehIdentificationNo2 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();

		Vehicle[] response = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			//BUG PAS-9722 Random sequence for Vehicles on DXP
			softly.assertThat(response[0].getModelYear()).isEqualTo(modelYear1);
			softly.assertThat(response[0].getManufacturer()).isEqualTo(manufacturer1);
			softly.assertThat(response[0].getSeries()).isEqualTo(series1);
			softly.assertThat(response[0].getModel()).isEqualTo(model1);
			softly.assertThat(response[0].getBodyStyle()).isEqualTo(bodyStyle1);
			softly.assertThat(response[0].vehIdentificationNo).isEqualTo(vehIdentificationNo1);

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
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		Vehicle[] response1 = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response1[0].getModelYear()).isEqualTo(modelYear1);
			softly.assertThat(response1[0].getManufacturer()).isEqualTo(manufacturer1);
			softly.assertThat(response1[0].getSeries()).isEqualTo(series1);
			softly.assertThat(response1[0].getModel()).isEqualTo(model1);
			softly.assertThat(response1[0].getBodyStyle()).isEqualTo(bodyStyle1);
			softly.assertThat(response1[0].vehIdentificationNo).isEqualTo(vehIdentificationNo1);

			softly.assertThat(response1[1].getModelYear()).isEqualTo(modelYear2);
			softly.assertThat(response1[1].getManufacturer()).isEqualTo(manufacturer2);
			softly.assertThat(response1[1].getSeries()).isEqualTo(series2);
			softly.assertThat(response1[1].getModel()).isEqualTo(model2);
			softly.assertThat(response1[1].getBodyStyle()).isEqualTo(bodyStyle2);
			softly.assertThat(response1[1].vehIdentificationNo).isEqualTo(vehIdentificationNo2);
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
		String vehIdentificationNo3 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();

		Vehicle[] response2 = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response2[0].getModelYear()).isEqualTo(modelYear1);
			softly.assertThat(response2[0].getManufacturer()).isEqualTo(manufacturer1);
			softly.assertThat(response2[0].getSeries()).isEqualTo(series1);
			softly.assertThat(response2[0].getModel()).isEqualTo(model1);
			softly.assertThat(response2[0].getBodyStyle()).isEqualTo(bodyStyle1);
			softly.assertThat(response2[0].vehIdentificationNo).isEqualTo(vehIdentificationNo1);

			softly.assertThat(response2[1].getModelYear()).isEqualTo(modelYear3);
			softly.assertThat(response2[1].getManufacturer()).isEqualTo(manufacturer3);
			softly.assertThat(response2[1].getSeries()).isEqualTo(series3);
			softly.assertThat(response2[1].getModel()).isEqualTo(model3);
			softly.assertThat(response2[1].getBodyStyle()).isEqualTo(bodyStyle3);
			softly.assertThat(response2[1].vehIdentificationNo).isEqualTo(vehIdentificationNo3);
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

	protected void pas9337_CheckStartEndorsementInfoServerResponseErrorForEffectiveDate(PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		assertSoftly(softly -> {

			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			ValidateEndorsementResponse responseNd = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
			assertThat(responseNd.ruleSets.get(0).errors.toString().contains(START_ENDORSEMENT_INFO_ERROR_7)).isTrue();

			DBService.get().executeUpdate(INSERT_EFFECTIVE_DATE);

			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(8));
			String endorsementDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			ValidateEndorsementResponse responseNd1 = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate1);

			softly.assertThat(responseNd1.ruleSets.get(0).errors).isEmpty();
			softly.assertThat(responseNd1.ruleSets.get(0).warnings).isEmpty();

			DBService.get().executeUpdate(DELETE_INSERT_EFFECTIVE_DATE);
		});

	}

	protected void pas9337_CheckStartEndorsementInfoServerResponseForFuturePolicy(PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();

		TestData td = getPolicyTD("DataGather", "TestData").adjust(TestData.makeKeyPath(new GeneralTab().getMetaKey(),
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
				DateTimeUtils.getCurrentDateTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY));

		//Future policy
		policyType.get().createPolicy(td);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		//Check future policy message in service
		ValidateEndorsementResponse response = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertThat(response.ruleSets.get(0).errors.toString().contains(START_ENDORSEMENT_INFO_ERROR_1)).isTrue();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(20));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(20).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Check Policy locked message
		ValidateEndorsementResponse responseNd = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertThat(responseNd.ruleSets.get(0).errors.toString().contains(START_ENDORSEMENT_INFO_ERROR_4)).isTrue();
	}

	protected void pas9337_CheckStartEndorsementInfoServerResponseForCancelPolicy(PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//Policy cancellation
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		ErrorResponseDto response = HelperCommon.validateEndorsementResponseError(policyNumber, null, 422);
		assertSoftly(softly -> {
			softly.assertThat(response.getErrorCode()).isEqualTo("PFW093");
			softly.assertThat(response.getMessage()).isEqualTo(START_ENDORSEMENT_INFO_ERROR_2);
		});

		//Policy reinstatement
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);
		ReinstatementActionTab reinstatementTab = new ReinstatementActionTab();
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation
				.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		String reinstatementDate = cancellationDate.plusDays(10).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), AutoSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());
		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, reinstatementDate));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(5));
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		ValidateEndorsementResponse responseNd = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertThat(responseNd.ruleSets.get(0).errors.toString().contains(START_ENDORSEMENT_INFO_ERROR_3)).isTrue();
	}

	protected void pas7082_AddVehicle(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		VehicleTab vehicleTab = new VehicleTab();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		String vin1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		mainApp().close();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Add new vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "ZFFCW56A830133118";

		Vehicle response1 = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin2);
		assertSoftly(softly ->
				softly.assertThat(response1.oid).isNotEmpty()
		);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(USAGE.getLabel(), ComboBox.class).setValue("Pleasure");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		//View added vehicle in view vehicle service
		Vehicle[] response3 = HelperCommon.executeVehicleInfoValidate(policyNumber);
		if (response3[0].vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response3[0].vehIdentificationNo).isEqualTo(vin1);

				softly.assertThat(response3[1].vehIdentificationNo).isEqualTo(vin2);

			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response3[0].vehIdentificationNo).isEqualTo(vin2);

				softly.assertThat(response3[1].vehIdentificationNo).isEqualTo(vin1);

			});
		}
	}

	protected void pas9337_CheckStartEndorsementInfoServerResponseForExpiredPolicy(PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_EXPIRED);
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		mainApp().close();

		ErrorResponseDto response = HelperCommon.validateEndorsementResponseError(policyNumber, endorsementDate, 422);
		assertSoftly(softly -> {
			softly.assertThat(response.getErrorCode()).isEqualTo("PFW093");
			softly.assertThat(response.getMessage()).isEqualTo(START_ENDORSEMENT_INFO_ERROR_2);
		});
	}

	protected void pas9716_policySummaryForPolicy(PolicyType policyType, String state) {
		assertSoftly(softly -> {
			TestData td = getPolicyTD("DataGather", "TestData").adjust(TestData.makeKeyPath(new GeneralTab().getMetaKey(),
					AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
					AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
					DateTimeUtils.getCurrentDateTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY));

			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);

			String policyNumber = PolicySummaryPage.getPolicyNumber();
			LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

			PolicySummary responsePolicyPending = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyPending.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyPending.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyPending.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyPending.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyPending.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyPending.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyPending.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyPending, state, "NOTENROLLED");

			PolicySummary responsePolicyPendingRenewal = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 404);
			assertThat(responsePolicyPendingRenewal.errorCode).isEqualTo("400");
			assertThat(responsePolicyPendingRenewal.message).contains("Renewal quote version or issued pending renewal not found for policy number " + policyNumber + ".");

			TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyActive = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyActive.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyActive.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActive.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActive.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyActive.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyActive.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyActive.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyPending, state, "NOTENROLLED");

			PolicySummary responsePolicyActiveRenewal = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 404);
			assertThat(responsePolicyActiveRenewal.errorCode).isEqualTo("400");
			assertThat(responsePolicyActiveRenewal.message).contains("Renewal quote version or issued pending renewal not found for policy number " + policyNumber + ".");
		});
	}

	protected void pas9716_policySummaryForLapsedRenewal(PolicyType policyType, String state) {
		assertSoftly(softly -> {

			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(getPolicyTD());
			if ("VA".equals(state)) {
				endorsePolicyAddEvalue();
			}

			String policyNumber = PolicySummaryPage.getPolicyNumber();
			LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

			LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);

			PolicySummary responsePolicyActive = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyActive.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyActive.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActive.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActive.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyActive.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyActive.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyActive.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyActive, state, "NOTENROLLED");

			PolicySummary responsePolicyRenewalPreview = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 200);
			softly.assertThat(responsePolicyRenewalPreview.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalPreview.policyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.timedPolicyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalPreview.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalPreview.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalPreview.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalPreview, state, "NOTENROLLED");

			LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

			PolicySummary responsePolicyOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOffer.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOffer.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOffer.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyOffer.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOffer.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOffer, state, "NOTENROLLED");

			PolicySummary responsePolicyRenewalOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 200);
			softly.assertThat(responsePolicyRenewalOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOffer.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOffer.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOffer.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOffer.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOffer.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOffer, state, "NOTENROLLED");

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyOfferExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferExpired.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferExpired.timedPolicyStatus).isEqualTo("expired");
			softly.assertThat(responsePolicyOfferExpired.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferExpired.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferExpired.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOfferExpired, state, "NOTENROLLED");

			PolicySummary responsePolicyRenewalOfferExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 200);
			softly.assertThat(responsePolicyRenewalOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOfferExpired.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOfferExpired.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOfferExpired.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOfferExpired.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOfferExpired.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOfferExpired, state, "NOTENROLLED");

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(15));
			JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

			PolicySummary responsePolicyOfferLapsed = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyOfferLapsed.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferLapsed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferLapsed.timedPolicyStatus).isEqualTo("expired");
			softly.assertThat(responsePolicyOfferLapsed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferLapsed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferLapsed.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferLapsed.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOfferLapsed, state, "NOTENROLLED");

			//BUG PAS-10482 Lapsed policy is not returned by renewal term service after R+15
			PolicySummary responsePolicyRenewalOfferLapsed = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 200);
			softly.assertThat(responsePolicyRenewalOfferLapsed.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOfferLapsed.policyStatus).isEqualTo("customerDeclined");
			softly.assertThat(responsePolicyRenewalOfferLapsed.timedPolicyStatus).isEqualTo("customerDeclined");
			softly.assertThat(responsePolicyRenewalOfferLapsed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOfferLapsed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.plusYears(2).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOfferLapsed.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOfferLapsed.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOfferLapsed, state, "NOTENROLLED");
		});
	}

	protected void pas9716_policySummaryForActiveRenewalBody(String state) {
		assertSoftly(softly -> {

			mainApp().open();
			String policyNumber = getCopiedPolicy();
			if ("VA".equals(state)) {
				endorsePolicyAddEvalue();
			}

			LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

			LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);

			PolicySummary responsePolicyActive = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyActive.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyActive.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActive.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActive.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyActive.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyActive.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyActive.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyActive, state, "ACTIVE");

			PolicySummary responsePolicyRenewalPreview = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 200);
			softly.assertThat(responsePolicyRenewalPreview.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalPreview.policyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.timedPolicyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalPreview.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalPreview.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalPreview.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalPreview, state, "ACTIVE");

			LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

			PolicySummary responsePolicyOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOffer.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOffer.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOffer.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyOffer.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOffer.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOffer, state, "ACTIVE");

			PolicySummary responsePolicyRenewalOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 200);
			softly.assertThat(responsePolicyRenewalOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOffer.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyRenewalOffer.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyRenewalOffer.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOffer.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyRenewalOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOffer.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOffer, state, "ACTIVE");

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyOfferExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", 200);
			softly.assertThat(responsePolicyOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferExpired.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferExpired.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOfferExpired.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyEffectiveDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferExpired.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(policyExpirationDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferExpired.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyOfferExpired, state, "ACTIVE");

			PolicySummary responsePolicyPending = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", 404);
			softly.assertThat(responsePolicyPending.errorCode).isEqualTo("400");
			softly.assertThat(responsePolicyPending.message).contains("Renewal quote version or issued pending renewal not found for policy number " + policyNumber + ".");
		});
	}

	protected void pas10484_ViewDriverAssignmentService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_AllDrivers").getTestDataList("DriverTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create a pended Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());

		List<TestData> assignmentsPrimary = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue();

		String driverAssignment1 = assignmentsPrimary.get(0).toString();
		String driverAssignment2 = assignmentsPrimary.get(1).toString();

		DriverAssignmentDto[] response = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment1.contains(response[0].vehicleDisplayValue));
			softly.assertThat(response[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment1.contains(response[0].driverDisplayValue));
			softly.assertThat(response[0].driverOid).isNotNull();
			softly.assertThat(response[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment2.contains(response[1].vehicleDisplayValue));
			softly.assertThat(response[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment2.contains(response[1].driverDisplayValue));
			softly.assertThat(response[1].driverOid).isNotNull();
			softly.assertThat(response[1].relationshipType).isEqualTo("primary");

		});
		assignmentTab.saveAndExit();

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "4S2CK58W8X4307498";

		Vehicle addVehicle = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin2);
		assertSoftly(softly ->
				softly.assertThat(addVehicle.oid).isNotEmpty()
		);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());

		String driverAssignment3 = assignmentsPrimary.get(0).toString();
		String driverAssignment4 = assignmentsPrimary.get(1).toString();

		List<TestData> assignmentsUnassigned = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment5 = assignmentsUnassigned.get(0).toString();

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponce = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment3.contains(driverAssignmentAfterAddingVehicleResponce[0].vehicleDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3.contains(driverAssignmentAfterAddingVehicleResponce[0].driverDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[0].driverOid).isNotNull();
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4.contains(driverAssignmentAfterAddingVehicleResponce[1].vehicleDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4.contains(driverAssignmentAfterAddingVehicleResponce[1].driverDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[1].driverOid).isNotNull();
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[1].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment5.contains(driverAssignmentAfterAddingVehicleResponce[2].vehicleDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[2].vehicleOid).isNotNull();
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[2].driverDisplayValue).isEqualTo("unassigned");
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[2].driverOid).isNotNull();
			softly.assertThat(driverAssignmentAfterAddingVehicleResponce[2].relationshipType).isEqualTo("unassigned");
		});
		assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getTable().getRow(1).getCell("Select Driver").controls.comboBoxes.getFirst().setValue("index=1");
		Tab.buttonTopSave.click();

		List<TestData> assignmentOccasional = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment6 = assignmentOccasional.get(0).toString();

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse1 = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment3.contains(driverAssignmentAfterAddingVehicleResponse1[0].vehicleDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3.contains(driverAssignmentAfterAddingVehicleResponse1[0].driverDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].driverOid).isNotNull();
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4.contains(driverAssignmentAfterAddingVehicleResponse1[1].vehicleDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4.contains(driverAssignmentAfterAddingVehicleResponse1[1].driverDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].driverOid).isNotNull();
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment6.contains(driverAssignmentAfterAddingVehicleResponse1[2].vehicleDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment6.contains(driverAssignmentAfterAddingVehicleResponse1[2].driverDisplayValue));
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].driverOid).isNotNull();
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].relationshipType).isEqualTo("occasional");
		});

	}

	protected void pas9716_policySummaryForConversionManualBody() {
		assertSoftly(softly -> {

			LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(45);
			mainApp().open();
			createCustomerIndividual();
			customer.initiateRenewalEntry().perform(getPolicyTD("InitiateRenewalEntry", "TestData"), effDate);
			policy.getDefaultView().fill(getPolicyTD("Conversion", "TestData"));
			String policyNum = PolicySummaryPage.linkPolicy.getValue();
			SearchPage.openPolicy(policyNum);
			new ProductRenewalsVerifier().setStatus(PREMIUM_CALCULATED).verify(1);

			//BUG PAS-10481 Conversion stub policy is not returned for current term before it becomes active
			PolicySummary responsePolicyStub = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyStub.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStub.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStub.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStub.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.minusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyStub.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyStub.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStub.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStub.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferRated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 200);
			softly.assertThat(responsePolicyOfferRated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferRated.policyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.timedPolicyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferRated.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyOfferRated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferRated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferRated.renewalCycle).isEqualTo(1);
			//BUG PAS-10480 eValue Status is not shown for conversion stub policy

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

			PolicySummary responsePolicyStubProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyStubProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposed.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.minusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyStubProposed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyStubProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposed.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 200);
			softly.assertThat(responsePolicyOfferProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposed.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferProposed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyOfferProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposed.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(effDate));
			JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
			mainApp().open();
			SearchPage.openBilling(policyNum);
			Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

			PolicySummary responsePolicyStubProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyStubProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposedPaid.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposedPaid.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.minusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyStubProposedPaid.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyStubProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposedPaid.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 200);
			softly.assertThat(responsePolicyOfferProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferProposedPaid.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyOfferProposed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferProposed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyOfferProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposedPaid.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			PolicySummary responsePolicyActivated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyActivated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyActivated.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActivated.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActivated.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyActivated.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyActivated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyActivated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyActivated.renewalCycle).isEqualTo(1);

			PolicySummary responsePolicyStubExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 404);
			softly.assertThat(responsePolicyStubExpired.errorCode).isEqualTo("400");
			softly.assertThat(responsePolicyStubExpired.message).contains("Renewal quote version or issued pending renewal not found for policy number " + policyNum + ".");
		});
	}

	protected void pas9716_policySummaryForConversionBody(String file, ITestContext context) {
		assertSoftly(softly -> {
			//timeshifting to let the tests pass
			mainApp().open();
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));

			LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
			ConversionPolicyData data = new MaigConversionData(file, effDate);
			String policyNum = ConversionUtils.importPolicy(data, context);

			mainApp().open();
			SearchPage.openPolicy(policyNum);
			MaigConversionTest maigConversionTest = new MaigConversionTest();
			maigConversionTest.fillPolicy();
			new ProductRenewalsVerifier().setStatus(PREMIUM_CALCULATED).verify(1);

			//BUG PAS-10481 Conversion stub policy is not returned for current term before it becomes active
			PolicySummary responsePolicyStub = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyStub.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStub.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStub.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStub.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.minusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyStub.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyStub.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStub.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStub.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferRated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 200);
			softly.assertThat(responsePolicyOfferRated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferRated.policyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.timedPolicyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferRated.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyOfferRated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferRated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferRated.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

			PolicySummary responsePolicyStubProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyStubProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposed.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.minusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyStubProposed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyStubProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposed.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 200);
			softly.assertThat(responsePolicyOfferProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposed.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferProposed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyOfferProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposed.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(effDate));
			JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
			mainApp().open();
			SearchPage.openBilling(policyNum);
			Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

			PolicySummary responsePolicyStubProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyStubProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposedPaid.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposedPaid.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.minusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyStubProposedPaid.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyStubProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposedPaid.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 200);
			softly.assertThat(responsePolicyOfferProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferProposedPaid.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyOfferProposed.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyOfferProposed.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyOfferProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposedPaid.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			PolicySummary responsePolicyActivated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", 200);
			softly.assertThat(responsePolicyActivated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyActivated.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActivated.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActivated.effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.toLocalDate()));
			softly.assertThat(responsePolicyActivated.expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(effDate.plusYears(1).toLocalDate()));
			softly.assertThat(responsePolicyActivated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyActivated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyActivated.renewalCycle).isEqualTo(1);

			PolicySummary responsePolicyStubExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", 404);
			softly.assertThat(responsePolicyStubExpired.errorCode).isEqualTo("400");
			softly.assertThat(responsePolicyStubExpired.message).contains("Renewal quote version or issued pending renewal not found for policy number " + policyNum + ".");
		});
	}

	private void eValueStatusCheck(SoftAssertions softly, PolicySummary responsePolicyPending, String state, String eValueStatus) {
		if ("CA".equals(state)) {
			softly.assertThat(responsePolicyPending.eValueStatus).isEqualTo(null);
		} else {
			softly.assertThat(responsePolicyPending.eValueStatus).isEqualTo(eValueStatus);
		}
	}

	protected void pas9456_9455_PolicyLockUnlockServices() {

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		mainApp().open();
		String policyNumber = getCopiedPolicy();
        mainApp().close();

		//Lock policy id1 and check service response
		PolicyLockUnlockDto response = HelperCommon.executePolicyLockService(policyNumber, 200, sessionId1);
		assertSoftly(softly -> {
			softly.assertThat(response.getPolicyNumber()).isEqualTo(policyNumber);
			softly.assertThat(response.getStatus()).isEqualTo("Locked");
		});

		//Hit start endorsement info service with Id1
		ValidateEndorsementResponse endorsementInfoResp1 = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate, sessionId1);
		assertThat(endorsementInfoResp1.ruleSets.get(0).errors).isEmpty();

		//Hit start endorsement info service with Id2
		ValidateEndorsementResponse endorsementInfoResp2 = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate, sessionId2);
		assertThat(endorsementInfoResp2.ruleSets.get(0).errors.toString().contains(START_ENDORSEMENT_INFO_ERROR_4)).isTrue();

		//Try to lock policy with id2
		PolicyLockUnlockDto response1 = HelperCommon.executePolicyLockService(policyNumber, 500, sessionId2);
		assertSoftly(softly -> {
			softly.assertThat(response1.getErrorCode()).isEqualTo("300");
			softly.assertThat(response1.getMessage()).isEqualTo(START_ENDORSEMENT_INFO_ERROR_5);
		});

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE,policyNumber);
		policy.endorse().start();

		//Check if policy was locked in PAS
		assertThat(errorTab.tableBaseErrors.getRow(1).getCell("Description").getValue()).isEqualTo(START_ENDORSEMENT_INFO_ERROR_6);
		PolicySummaryPage.buttonBackFromErrorPage.click();

		//Try unlock policy with id2
		PolicyLockUnlockDto response2 = HelperCommon.executePolicyUnlockService(policyNumber, 500, sessionId2);
		assertSoftly(softly -> {
			softly.assertThat(response2.getErrorCode()).isEqualTo("300");
			softly.assertThat(response2.getMessage()).isEqualTo(START_ENDORSEMENT_INFO_ERROR_5);
		});

		//Unlock policy with id1
		PolicyLockUnlockDto response3 = HelperCommon.executePolicyUnlockService(policyNumber, 200, sessionId1);
		assertSoftly(softly -> {
			softly.assertThat(response3.getPolicyNumber()).isEqualTo(policyNumber);
			softly.assertThat(response3.getStatus()).isEqualTo("Unlocked");
		});

		//Start do endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		//Check if policy can be locked using lock service
		PolicyLockUnlockDto response4 = HelperCommon.executePolicyLockService(policyNumber, 500, sessionId1);
		assertSoftly(softly -> {
			softly.assertThat(response4.getErrorCode()).isEqualTo("300");
			softly.assertThat(response4.getMessage()).isEqualTo(START_ENDORSEMENT_INFO_ERROR_5);
		});

		//Check if policy can be unlocked using unlock service
		PolicyLockUnlockDto response5 = HelperCommon.executePolicyUnlockService(policyNumber, 500, sessionId1);
		assertSoftly(softly -> {
			softly.assertThat(response5.getErrorCode()).isEqualTo("300");
			softly.assertThat(response5.getMessage()).isEqualTo(START_ENDORSEMENT_INFO_ERROR_5);
		});
	}

	protected void pas9490_ViewVehicleServiceCheckVehiclesStatus() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		String vin1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		VehicleTab.buttonCancel.click();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Start PAS-479
		//Check premium
		PolicyPremiumInfo[] rateResponse = HelperCommon.executeEndorsementRate(policyNumber, 200);
		assertSoftly(softly -> {
			softly.assertThat(rateResponse[0].getPremiumType()).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(rateResponse[0].getPremiumCode()).isEqualTo("GWT");
		});
		Dollar dxpPremium = new Dollar(rateResponse[0].getActualAmt());

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").getValue()).isEqualTo(PREMIUM_CALCULATED);
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(new Dollar(PremiumAndCoveragesTab.getActualPremium())).isEqualTo(dxpPremium);
		PremiumAndCoveragesTab.buttonCancel.click();

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin2 = "1HGFA16526L081415";
		Vehicle response2 = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin2);
		assertThat(response2.oid).isNotEmpty();

		//View vehicles status
		Vehicle[] response3 = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);

		if (response3[0].vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response3[0].vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3[0].vehicleStatus).isEqualTo("active");
				softly.assertThat(response3[1].vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3[1].vehicleStatus).isEqualTo("pending");
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response3[0].vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3[0].vehicleStatus).isEqualTo("pending");
				softly.assertThat(response3[1].vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3[1].vehicleStatus).isEqualTo("active");
			});
		}

		SearchPage.openPolicy(policyNumber);
		//Add usage (this part should be removed when usage dxp service will be added)
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(USAGE.getLabel(), ComboBox.class).setValue("Pleasure");
		vehicleTab.saveAndExit();

		//Check premium after new vehicle was added
		PolicyPremiumInfo[] rateResponse2 = HelperCommon.executeEndorsementRate(policyNumber, 200);
		Dollar dxpPremium2 = new Dollar(rateResponse2[0].getActualAmt());

		PolicySummaryPage.buttonPendedEndorsement.click();
		assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").getValue()).isEqualTo(PREMIUM_CALCULATED);
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		assertThat(new Dollar(PremiumAndCoveragesTab.getActualPremium())).isEqualTo(dxpPremium2);
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		premiumAndCoveragesTab.cancel();
		//End PAS-479

		//Issue pended endorsement
		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		//View vehicles status after endorsement was bind
		Vehicle[] response4 = HelperCommon.executeVehicleInfoValidate(policyNumber);

		if (response3[0].vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response4[0].vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response4[0].vehicleStatus).isEqualTo("active");
				softly.assertThat(response4[1].vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response4[1].vehicleStatus).isEqualTo("active");
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response4[0].vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response4[0].vehicleStatus).isEqualTo("active");
				softly.assertThat(response4[1].vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response4[1].vehicleStatus).isEqualTo("active");
			});
		}
	}

	protected void pas10449_ViewVehicleServiceCheckOrderOfVehicle(PolicyType policyType, String state) {

		mainApp().open();
		createCustomerIndividual();
        TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_Vehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//hit view vehicle service to get Vehicle order
		Vehicle[] viewVehicleResponse = HelperCommon.executeVehicleInfoValidate(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(viewVehicleResponse[0].oid);
			softly.assertThat(viewVehicleResponse[0].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[0].vehTypeCd).isEqualTo("PPA");
			softly.assertThat(viewVehicleResponse[0].vehIdentificationNo).isEqualTo("1GAZG1FG7D1145543");

			softly.assertThat(viewVehicleResponse[1].oid);
			softly.assertThat(viewVehicleResponse[1].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[1].vehTypeCd).isEqualTo("PPA");
			softly.assertThat(viewVehicleResponse[1].vehIdentificationNo).isEqualTo("WDCYC7BB0B6729451");

			softly.assertThat(viewVehicleResponse[2].oid);
			softly.assertThat(viewVehicleResponse[2].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[2].vehTypeCd).isEqualTo("Motor");
			softly.assertThat(viewVehicleResponse[2].vehIdentificationNo).isEqualTo("5B4MP67G123353230");

			softly.assertThat(viewVehicleResponse[3].oid);
			softly.assertThat(viewVehicleResponse[3].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[3].vehTypeCd).isEqualTo("Conversion");
			softly.assertThat(viewVehicleResponse[3].vehIdentificationNo).isEqualTo("5FNRL5H64GB087983");

		});

	    // Perform endorsement
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);

		//Add new vehicle to have pending vehicle
		String purchaseDate = "2013-02-22";
		String vin2 = "1HGFA16526L081415";
		Vehicle addVehicleResponse = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin2);
		assertThat(addVehicleResponse.oid).isNotEmpty();

     	Vehicle[] viewVehicleEndorsementResponse= HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(viewVehicleEndorsementResponse[0].oid).isNotNull();
			softly.assertThat(viewVehicleEndorsementResponse[0].vehicleStatus).isEqualTo("pending");
			softly.assertThat(viewVehicleEndorsementResponse[0].vehTypeCd).isEqualTo("PPA");
			softly.assertThat(viewVehicleEndorsementResponse[0].vehIdentificationNo).isEqualTo("1HGFA16526L081415");

			softly.assertThat(viewVehicleResponse[0].oid);
			softly.assertThat(viewVehicleResponse[0].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[0].vehTypeCd).isEqualTo("PPA");
			softly.assertThat(viewVehicleResponse[0].vehIdentificationNo).isEqualTo("1GAZG1FG7D1145543");

			softly.assertThat(viewVehicleResponse[1].oid);
			softly.assertThat(viewVehicleResponse[1].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[1].vehTypeCd).isEqualTo("PPA");
			softly.assertThat(viewVehicleResponse[1].vehIdentificationNo).isEqualTo("WDCYC7BB0B6729451");

			softly.assertThat(viewVehicleResponse[2].oid);
			softly.assertThat(viewVehicleResponse[2].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[2].vehTypeCd).isEqualTo("Motor");
			softly.assertThat(viewVehicleResponse[2].vehIdentificationNo).isEqualTo("5B4MP67G123353230");

			softly.assertThat(viewVehicleResponse[3].oid);
			softly.assertThat(viewVehicleResponse[3].vehicleStatus).isEqualTo("active");
			softly.assertThat(viewVehicleResponse[3].vehTypeCd).isEqualTo("Conversion");
			softly.assertThat(viewVehicleResponse[3].vehIdentificationNo).isEqualTo("5FNRL5H64GB087983");

		});

	}

	protected void pas9610_UpdateVehicleService() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//Get OID from View vehicle
		Vehicle[] viewVehicleResponse = HelperCommon.executeVehicleInfoValidate(policyNumber);
		String oid = viewVehicleResponse[0].oid;

		//send request to update vehicle service
		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.ownership = "OWN";
		updateVehicleRequest.usage = "Pleasure";
		updateVehicleRequest.salvaged = false;
		updateVehicleRequest.garagingDifferent = false;
		updateVehicleRequest.antiTheft = "STD";
		updateVehicleRequest.registeredOwner = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.ownership).isEqualTo("OWN");
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("Pleasure");
			softly.assertThat(updateVehicleResponse.salvaged).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.garagingDifferent).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.antiTheft).isEqualTo("STD");
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);
		});

		//verify updated information with pendedEndorsementValidateVehicleInfo
		Vehicle[] pendedEndorsementValidateVehicleResponse = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].modelYear).isEqualTo(updateVehicleResponse.modelYear);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].manufacturer).isEqualTo(updateVehicleResponse.manufacturer);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].series).isEqualTo(updateVehicleResponse.series);

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].model).isEqualTo(updateVehicleResponse.model);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].bodyStyle).isEqualTo(updateVehicleResponse.bodyStyle);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].vehIdentificationNo).isEqualTo(updateVehicleResponse.vehIdentificationNo);

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].ownership).isEqualTo("OWN");
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].usage).isEqualTo("Pleasure");
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].salvaged).isEqualTo(false);

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].garagingDifferent).isEqualTo(false);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].antiTheft).isEqualTo("STD");
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].registeredOwner).isEqualTo(false);
		});
	}

	protected void pas508_BindManualEndorsement() {
		String authorizedBy = "Osi Testas Insured";

		mainApp().open();
		String policyNumber = getCopiedPolicy();

		String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "%%");
		int numberOfDocumentsRecordsInDb = Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get());

		//Create pended endorsement manually
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(getPremiumAndCoverageTab());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(getCalculatePremium()).click();
		getPremiumAndCoverageTabElement().saveAndExit();
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isTrue();

		//issue through service
		HelperCommon.executeEndorsementBind(policyNumber, authorizedBy, 200);
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		//check bound endorsement created by
		checkAuthorizedByChanged(authorizedBy);

		//check number of documents in DB
		assertThat(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get())).isEqualTo(numberOfDocumentsRecordsInDb + 1);

		//Create additional endorsement
		SearchPage.openPolicy(policyNumber);
		secondEndorsementIssueCheck();
	}

	protected void pas508_BindServiceEndorsement() {
		String authorizedBy = "Osi Testas Insured";

		mainApp().open();
		String policyNumber = getCopiedPolicy();

		assertSoftly(softly -> {
			String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "ENDORSEMENT_ISSUE");
			int numberOfDocumentsRecordsInDb = Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get());

			//Create pended endorsement
			AAAEndorseResponse endorsementResponse = HelperCommon.executeEndorseStart(policyNumber, null);
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

			PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.executeEndorsementRate(policyNumber, 200);
			softly.assertThat(endorsementRateResponse[0].getPremiumType()).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].getPremiumCode()).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].getActualAmt()).isNotBlank();

			//Create pended endorsement
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").verify.value(PREMIUM_CALCULATED);
			Tab.buttonBack.click();

			//issue through service
			HelperCommon.executeEndorsementBind(policyNumber, authorizedBy, 200);
			SearchPage.openPolicy(policyNumber);
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

			//check bound endorsement created by
			checkAuthorizedByChanged(authorizedBy);

			//check number of documents in DB
			softly.assertThat(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get())).isEqualTo(numberOfDocumentsRecordsInDb + 1);

			//Create additional endorsement
			SearchPage.openPolicy(policyNumber);
			secondEndorsementIssueCheck();
		});
	}

	private void checkAuthorizedByChanged(String authorizedBy) {
		policy.policyInquiry().start();
		NavigationPage.toViewTab(getDocumentsAndBindTab());
		if (getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY.getLabel()).isPresent()) {
			getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY.getLabel()).verify.value(authorizedBy);
		}
		Tab.buttonCancel.click();
	}

	protected void pas10227_ViewPremiumServiceForPolicy() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		String actualPremium = PremiumAndCoveragesTab.totalActualPremium.getValue();
		String totalPremium = PremiumAndCoveragesTab.totalTermPremium.getValue();

		PolicyPremiumInfo[] response = HelperCommon.viewPremiumInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response[0].getPremiumType()).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(response[0].getPremiumCode()).isEqualTo("GWT");
			softly.assertThat(new Dollar(response[0].getActualAmt())).isEqualTo(new Dollar(actualPremium));
			softly.assertThat(new Dollar(response[0].getTermPremium())).isEqualTo(new Dollar(totalPremium));
		});
	}

	protected void pas10227_ViewPremiumServiceForPendedEndorsement() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		//Create a pended Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin = "4S2CK58W8X4307498";
		VehicleTab vehicleTab = new VehicleTab();
		Vehicle addVehicle = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin);
		assertSoftly(softly ->
				softly.assertThat(addVehicle.oid).isNotEmpty()
		);

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(USAGE.getLabel(), ComboBox.class).setValue("Pleasure");
		NavigationPage.toViewTab(getPremiumAndCoverageTab());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(getCalculatePremium()).click();
		String actualPremium = PremiumAndCoveragesTab.totalActualPremium.getValue();
		String totalPremium = PremiumAndCoveragesTab.totalTermPremium.getValue();

		PolicyPremiumInfo[] response = HelperCommon.viewPremiumInfoPendedEndorsementResponse(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response[0].getPremiumType()).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(response[0].getPremiumCode()).isEqualTo("GWT");
			softly.assertThat(new Dollar(response[0].getActualAmt())).isEqualTo(new Dollar(actualPremium));
			softly.assertThat(new Dollar(response[0].getTermPremium())).isEqualTo(new Dollar(totalPremium));
		});
	}

	private void endorsePolicyAddEvalue() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();
		getPremiumAndCoverageTabElement().saveAndExit();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
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
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
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
