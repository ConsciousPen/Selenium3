package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.MIDDLE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static aaa.modules.regression.service.helper.preconditions.TestMiniServicesNonPremiumBearingAbstractPreconditions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.BooleanUtils;
import org.assertj.core.api.SoftAssertions;
import org.testng.ITestContext;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.MaigConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.*;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.customer.CustomerType;
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
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesNonPremiumBearingAbstract extends PolicyBaseTest {

	private static final String SESSION_ID_1 = "oid1";
	private static final String SESSION_ID_2 = "oid2";
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private ErrorTab errorTab = new ErrorTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private VehicleTab vehicleTab = new VehicleTab();
	private DriverTab driverTab = new DriverTab();

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
		CustomAssert
				.assertTrue("Policy version you are working with is marked as NOT current (Probable cause - another user working with the same policy). Please reload policy to continue working with it."
						.equals(Page.dialogConfirmation.labelMessage.getValue()));
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
			softly.assertThat(response.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.NANO_POLICY.getMessage());
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
			softly.assertThat(response.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.SYSTEM_CREATED_PENDED_ENDORSEMENT.getMessage());
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
			softly.assertThat(response.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.POLICY_TERM_DOES_NOT_EXIST.getMessage())).isTrue();
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
			softly.assertThat(responseValidateCanCreateEndorsement1.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.CUSTOMER_CREATED_ENDORSEMENT.getMessage());
		});

		//endorsement delete attempt should not be allowed on the Delay Day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete - 1));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement2 = HelperCommon.executeEndorsementsValidate(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement2.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.CUSTOMER_CREATED_ENDORSEMENT.getMessage());
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
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.CUSTOMER_CREATED_ENDORSEMENT.getMessage());
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
			softly.assertThat(responseValidateCanCreateEndorsement3.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.SYSTEM_CREATED_PENDED_ENDORSEMENT.getMessage());
		});
	}

	protected void pas9997_paymentMethodsLookup() {
		assertSoftly(softly -> {
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_ADD_WY);
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_CHANGE_WY);

			String lookupName2 = "AAAeValueQualifyingPaymentMethods";
			String productCd = "AAA_SS";
			String riskStateCd = "WY";

			String effectiveDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup12 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate1);
			softly.assertThat("FALSE".equals(responseValidateLookup12.get("pciDebitCard"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup12.get("pciCreditCard"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup12.get("eft"))).isTrue();
			softly.assertThat(responseValidateLookup12.size()).isEqualTo(3);

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

	protected void pas8275_vinValidateCheck(SoftAssertions softly, PolicyType policyType) {
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
		softly.assertThat(response.vehicles).isEmpty();
		softly.assertThat(response.validationMessage).isEqualTo("Invalid VIN length");

		String vin2 = "12345678901234567890"; //VIN too long
		AAAVehicleVinInfoRestResponseWrapper response2 = HelperCommon.executeVinValidate(policyNumber, vin2, null);
		softly.assertThat(response2.vehicles).isEmpty();
		softly.assertThat(response2.validationMessage).isEqualTo("Invalid VIN length");

		String vin3 = "4T1BF1FK0H1234567"; //VIN check digit failed
		AAAVehicleVinInfoRestResponseWrapper response3 = HelperCommon.executeVinValidate(policyNumber, vin3, null);
		softly.assertThat(response3.vehicles).isEmpty();
		softly.assertThat(response3.validationMessage).isEqualTo("Check Digit is Incorrect");

		String vin4 = "4T1BF1FK0H"; //VIN from VIN table but too short
		AAAVehicleVinInfoRestResponseWrapper response4 = HelperCommon.executeVinValidate(policyNumber, vin4, null);
		softly.assertThat(response4.vehicles).isEmpty();
		softly.assertThat(response4.validationMessage).isEqualTo("Invalid VIN length");

		String vin5 = "1D30E42J451234567"; //VIN NOT from VIN table to Check VIN service
		AAAVehicleVinInfoRestResponseWrapper response5 = HelperCommon.executeVinValidate(policyNumber, vin5, null);
		softly.assertThat(response5.vehicles).isEmpty();
		softly.assertThat(response5.validationMessage).isEqualTo("VIN is not on AAA VIN Table");

		String vin0 = "4T1BF1FK0HU624693"; //VIN from VIN table
		AAAVehicleVinInfoRestResponseWrapper response0 = HelperCommon.executeVinValidate(policyNumber, vin0, endorsementDate);
		softly.assertThat(response0.vehicles.get(0).vin).isEqualTo(vin0);
		softly.assertThat(response0.vehicles.get(0).year.toString()).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).make).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).modelText).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).seriesText).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).bodyStyleCd).isNotEmpty();
		softly.assertThat(response0.validationMessage).isEmpty();
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
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//check that new endorsement was created
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		pas8785_createdEndorsementTransactionProperties("Gathering Info", TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY), "MyPolicy MyPolicy");
	}

	protected void pas8273_CheckIfOnlyActiveVehiclesAreAllowed(SoftAssertions softly, PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		VehicleTab vehicleTab = new VehicleTab();
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AllVehicles").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		//All info about first vehicle
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		String modelYear1 = vehicleTab.getInquiryAssetList().getStaticElement(YEAR.getLabel()).getValue();
		String manufacturer1 = vehicleTab.getInquiryAssetList().getStaticElement(MAKE.getLabel()).getValue();
		String series1 = vehicleTab.getInquiryAssetList().getStaticElement(SERIES.getLabel()).getValue();
		String model1 = vehicleTab.getInquiryAssetList().getStaticElement(MODEL.getLabel()).getValue();
		String bodyStyle1 = vehicleTab.getInquiryAssetList().getStaticElement(BODY_STYLE.getLabel()).getValue();
		String vehIdentificationNo1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		String ownership1 = vehicleTab.getInquiryAssetList().getStaticElement(Ownership.OWNERSHIP_TYPE.getLabel()).getValue().replace("Owned", "OWN");
		String usage1 = vehicleTab.getInquiryAssetList().getStaticElement(USAGE.getLabel()).getValue();
		String garagingDifferent1 = vehicleTab.getInquiryAssetList().getStaticElement(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel()).getValue().toLowerCase();
		String antiTheft1 = vehicleTab.getInquiryAssetList().getStaticElement(ANTI_THEFT.getLabel()).getValue().toUpperCase();
		String vehType1 = vehicleTab.getInquiryAssetList().getStaticElement(TYPE.getLabel()).getValue().replace("Private Passenger Auto", "PPA");
		//Garaging address for first vehicle
		String zipCode1 = vehicleTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
		String address1 = vehicleTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
		String city1 = vehicleTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
		String state1 = vehicleTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();
		VehicleTab.tableVehicleList.selectRow(2);

		//Get all info about second vehicle
		String modelYear2 = vehicleTab.getInquiryAssetList().getStaticElement(YEAR.getLabel()).getValue();
		String manufacturer2 = vehicleTab.getInquiryAssetList().getStaticElement(MAKE.getLabel()).getValue();
		String series2 = vehicleTab.getInquiryAssetList().getStaticElement(SERIES.getLabel()).getValue();
		String model2 = vehicleTab.getInquiryAssetList().getStaticElement(MODEL.getLabel()).getValue();
		String bodyStyle2 = vehicleTab.getInquiryAssetList().getStaticElement(BODY_STYLE.getLabel()).getValue();
		String vehIdentificationNo2 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		String ownership2 = vehicleTab.getInquiryAssetList().getStaticElement(Ownership.OWNERSHIP_TYPE.getLabel()).getValue().replace("Owned", "OWN");
		String usage2 = vehicleTab.getInquiryAssetList().getStaticElement(USAGE.getLabel()).getValue();
		String garagingDifferent2 = vehicleTab.getInquiryAssetList().getStaticElement(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel()).getValue().toLowerCase();
		String antiTheft2 = vehicleTab.getInquiryAssetList().getStaticElement(ANTI_THEFT.getLabel()).getValue().toUpperCase();
		String vehType2 = vehicleTab.getInquiryAssetList().getStaticElement(TYPE.getLabel()).getValue().replace("Private Passenger Auto", "PPA");
		//Get garaging address for second vehicle
		String zipCode2 = vehicleTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
		String address2 = vehicleTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
		String city2 = vehicleTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
		String state2 = vehicleTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();

		Vehicle[] response = HelperCommon.executeVehicleInfoValidate(policyNumber);
		Vehicle vehicleSt = Arrays.stream(response).filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleNd = Arrays.stream(response).filter(vehicle -> vehIdentificationNo2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt).isNotNull();
		softly.assertThat(vehicleSt.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt.series).isEqualTo(series1);
		softly.assertThat(vehicleSt.model).isEqualTo(model1);
		softly.assertThat(vehicleSt.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt.garagingDifferent)).isEqualTo(garagingDifferent1);
		softly.assertThat(vehicleSt.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt.oid).isNotEmpty();
		softly.assertThat(vehicleSt.garagingAddressPostalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt.city).isEqualTo(city1);

		softly.assertThat(vehicleNd).isNotNull();
		softly.assertThat(vehicleNd.modelYear).isEqualTo(modelYear2);
		softly.assertThat(vehicleNd.manufacturer).isEqualTo(manufacturer2);
		softly.assertThat(vehicleNd.series).isEqualTo(series2);
		softly.assertThat(vehicleNd.model).isEqualTo(model2);
		softly.assertThat(vehicleNd.bodyStyle).isEqualTo(bodyStyle2);
		softly.assertThat(vehicleNd.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleNd.ownership).isEqualTo(ownership2);
		softly.assertThat(vehicleNd.usage).isEqualTo(usage2);
		softly.assertThat(vehicleNd.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleNd.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleNd.antiTheft).isEqualTo(antiTheft2);
		softly.assertThat(vehicleNd.vehTypeCd).isEqualTo(vehType2);
		softly.assertThat(vehicleNd.oid).isNotEmpty();
		softly.assertThat(vehicleNd.garagingAddressPostalCode).isEqualTo(zipCode2);
		softly.assertThat(vehicleNd.addressLine1).isEqualTo(address2);
		softly.assertThat(vehicleNd.stateProvCd).isEqualTo(state2);
		softly.assertThat(vehicleNd.city).isEqualTo(city2);

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
		Vehicle vehicleSt1 = Arrays.stream(response1).filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleNd1 = Arrays.stream(response1).filter(vehicle -> vehIdentificationNo2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt1).isNotNull();
		softly.assertThat(vehicleSt1.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt1.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt1.series).isEqualTo(series1);
		softly.assertThat(vehicleSt1.model).isEqualTo(model1);
		softly.assertThat(vehicleSt1.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt1.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt1.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt1.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt1.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt1.garagingDifferent)).isEqualTo(garagingDifferent1);
		softly.assertThat(vehicleSt1.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt1.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt1.oid).isNotEmpty();
		softly.assertThat(vehicleSt1.garagingAddressPostalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt1.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt1.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt1.city).isEqualTo(city1);

		softly.assertThat(vehicleNd1).isNotNull();
		softly.assertThat(vehicleNd1.modelYear).isEqualTo(modelYear2);
		softly.assertThat(vehicleNd1.manufacturer).isEqualTo(manufacturer2);
		softly.assertThat(vehicleNd1.series).isEqualTo(series2);
		softly.assertThat(vehicleNd1.model).isEqualTo(model2);
		softly.assertThat(vehicleNd1.bodyStyle).isEqualTo(bodyStyle2);
		softly.assertThat(vehicleNd1.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleNd1.ownership).isEqualTo(ownership2);
		softly.assertThat(vehicleNd1.usage).isEqualTo(usage2);
		softly.assertThat(vehicleNd1.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleNd1.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleNd1.antiTheft).isEqualTo(antiTheft2);
		softly.assertThat(vehicleNd1.vehTypeCd).isEqualTo(vehType2);
		softly.assertThat(vehicleNd1.oid).isNotEmpty();
		softly.assertThat(vehicleNd1.garagingAddressPostalCode).isEqualTo(zipCode2);
		softly.assertThat(vehicleNd1.addressLine1).isEqualTo(address2);
		softly.assertThat(vehicleNd1.stateProvCd).isEqualTo(state2);
		softly.assertThat(vehicleNd1.city).isEqualTo(city2);

		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.policyInquiry().start();
		//Gel all info about third vehicle
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		String modelYear3 = vehicleTab.getInquiryAssetList().getStaticElement(YEAR.getLabel()).getValue();
		String manufacturer3 = vehicleTab.getInquiryAssetList().getStaticElement(MAKE.getLabel()).getValue();
		String series3 = vehicleTab.getInquiryAssetList().getStaticElement(SERIES.getLabel()).getValue();
		String model3 = vehicleTab.getInquiryAssetList().getStaticElement(MODEL.getLabel()).getValue();
		String bodyStyle3 = vehicleTab.getInquiryAssetList().getStaticElement(BODY_STYLE.getLabel()).getValue();
		String vehIdentificationNo3 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		String ownership3 = vehicleTab.getInquiryAssetList().getStaticElement(Ownership.OWNERSHIP_TYPE.getLabel()).getValue().replace("Owned", "OWN");
		String usage3 = vehicleTab.getInquiryAssetList().getStaticElement(USAGE.getLabel()).getValue();
		String garagingDifferent3 = vehicleTab.getInquiryAssetList().getStaticElement(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel()).getValue().toLowerCase();
		String antiTheft3 = vehicleTab.getInquiryAssetList().getStaticElement(ANTI_THEFT.getLabel()).getValue().toUpperCase();
		String vehType3 = vehicleTab.getInquiryAssetList().getStaticElement(TYPE.getLabel()).getValue().replace("Private Passenger Auto", "PPA");
		//Garaging address for third vehicle
		String zipCode3 = vehicleTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
		String address3 = vehicleTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
		String city3 = vehicleTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
		String state3 = vehicleTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();

		Vehicle[] response2 = HelperCommon.executeVehicleInfoValidate(policyNumber);
		Vehicle vehicleSt2 = Arrays.stream(response2).filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleRd2 = Arrays.stream(response2).filter(vehicle -> vehIdentificationNo3.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt2).isNotNull();
		softly.assertThat(vehicleSt2.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt2.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt2.series).isEqualTo(series1);
		softly.assertThat(vehicleSt2.model).isEqualTo(model1);
		softly.assertThat(vehicleSt2.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt2.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt2.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt2.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt2.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt2.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleSt2.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt2.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt2.oid).isNotEmpty();
		softly.assertThat(vehicleSt2.garagingAddressPostalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt2.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt2.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt2.city).isEqualTo(city1);

		softly.assertThat(vehicleRd2).isNotNull();
		softly.assertThat(vehicleRd2.modelYear).isEqualTo(modelYear3);
		softly.assertThat(vehicleRd2.manufacturer).isEqualTo(manufacturer3);
		softly.assertThat(vehicleRd2.series).isEqualTo(series3);
		softly.assertThat(vehicleRd2.model).isEqualTo(model3);
		softly.assertThat(vehicleRd2.bodyStyle).isEqualTo(bodyStyle3);
		softly.assertThat(vehicleRd2.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleRd2.ownership).isEqualTo(ownership3);
		softly.assertThat(vehicleRd2.usage).isEqualTo(usage3);
		softly.assertThat(vehicleRd2.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleRd2.garagingDifferent)).isEqualTo(garagingDifferent3);
		softly.assertThat(vehicleRd2.antiTheft).isEqualTo(antiTheft3);
		softly.assertThat(vehicleRd2.vehTypeCd).isEqualTo(vehType3);
		softly.assertThat(vehicleRd2.oid).isNotEmpty();
		softly.assertThat(vehicleRd2.garagingAddressPostalCode).isEqualTo(zipCode3);
		softly.assertThat(vehicleRd2.addressLine1).isEqualTo(address3);
		softly.assertThat(vehicleRd2.stateProvCd).isEqualTo(state3);
		softly.assertThat(vehicleRd2.city).isEqualTo(city3);
	}

	protected void pas11932_viewDriversInfo(PolicyType policyType, String state) {
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			String notAvailableForRating = "nafr";
			String availableForRating = "afr";
			TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			TestData td = getPolicyTD("DataGather", "TestData");
			TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_ThreeDrivers").getTestDataList("DriverTab")).resolveLinks();

			policyType.get().createPolicy(testData);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Drivers info from testData
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");

			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String middleName2 = td.getTestDataList("DriverTab").get(1).getValue("Middle Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");
			String suffix2 = td.getTestDataList("DriverTab").get(1).getValue("Suffix");

			String firstName3 = td.getTestDataList("DriverTab").get(2).getValue("First Name");
			String middleName3 = td.getTestDataList("DriverTab").get(2).getValue("Middle Name");
			String lastName3 = td.getTestDataList("DriverTab").get(2).getValue("Last Name");
			String suffix3 = td.getTestDataList("DriverTab").get(2).getValue("Suffix");

			//Hit service for the first time
			DriversDto[] response = HelperCommon.executeViewDrivers(policyNumber);
			DriversDto driverSt = Arrays.stream(response).filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd = Arrays.stream(response).filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driverRd = Arrays.stream(response).filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt).isNotNull();
			softly.assertThat(driverSt.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt.oid).isNotEmpty();

			softly.assertThat(driverNd).isNotNull();
			softly.assertThat(driverNd.middleName).isEqualTo(middleName2);
			softly.assertThat(driverNd.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverNd.oid).isNotEmpty();

			softly.assertThat(driverRd).isNotNull();
			softly.assertThat(driverRd.middleName).isEqualTo(middleName3);
			softly.assertThat(driverRd.lastName).isEqualTo(lastName3);
			softly.assertThat(driverRd.suffix).isEqualTo(suffix3);
			softly.assertThat(driverRd.driverType).isEqualTo(notAvailableForRating);
			softly.assertThat(driverRd.oid).isNotEmpty();

			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
			DriverTab.tableDriverList.removeRow(3);
			DriverTab.tableDriverList.selectRow(2);
			driverTab.getAssetList().getAsset(MIDDLE_NAME).setValue("Kevin");
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			new PremiumAndCoveragesTab().calculatePremium();
			premiumAndCoveragesTab.saveAndExit();

			//Check dxp service with pending endorsement
			DriversDto[] response2 = HelperCommon.executeViewDrivers(policyNumber);
			DriversDto driverSt2 = Arrays.stream(response2).filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd2 = Arrays.stream(response2).filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driverRd2 = Arrays.stream(response2).filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt2).isNotNull();
			softly.assertThat(driverSt2.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt2.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverSt2.oid).isNotEmpty();

			softly.assertThat(driverNd2).isNotNull();
			softly.assertThat(driverNd2.middleName).isEqualTo(middleName2);
			softly.assertThat(driverNd2.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd2.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd2.oid).isNotEmpty();

			softly.assertThat(driverRd2).isNotNull();
			softly.assertThat(driverRd2.middleName).isEqualTo(middleName3);
			softly.assertThat(driverRd2.lastName).isEqualTo(lastName3);
			softly.assertThat(driverRd2.suffix).isEqualTo(suffix3);
			softly.assertThat(driverRd2.oid).isNotEmpty();

			//Check dxp service what we have in endorsement
			DriversDto[] response3 = HelperCommon.executeEndorsementViewDrivers(policyNumber);
			DriversDto driverSt3 = Arrays.stream(response3).filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd3 = Arrays.stream(response3).filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt3).isNotNull();
			softly.assertThat(driverSt3.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt3.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverSt3.oid).isNotEmpty();

			softly.assertThat(driverNd3).isNotNull();
			softly.assertThat(driverNd3.middleName).isEqualTo("Kevin");
			softly.assertThat(driverNd3.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd3.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd3.oid).isNotEmpty();

			//Issue pended endorsement
			TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			//Check dxp service if endorsement changes were applied
			DriversDto[] response4 = HelperCommon.executeViewDrivers(policyNumber);
			DriversDto driverSt4 = Arrays.stream(response4).filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd4 = Arrays.stream(response4).filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt4).isNotNull();
			softly.assertThat(driverSt4.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt4.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverSt4.oid).isNotEmpty();

			softly.assertThat(driverNd4).isNotNull();
			softly.assertThat(driverNd4.middleName).isEqualTo("Kevin");
			softly.assertThat(driverNd4.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd4.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd4.oid).isNotEmpty();
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
			assertThat(responseNd.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.STATE_DOES_NOT_ALLOW_ENDORSEMENTS.getMessage())).isTrue();

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
		assertThat(response.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.POLICY_TERM_DOES_NOT_EXIST.getMessage())).isTrue();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(20));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(20).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Check Policy locked message
		ValidateEndorsementResponse responseNd = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		assertThat(responseNd.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.POLICY_IS_LOCKED.getMessage())).isTrue();
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

		ErrorResponseDto response = HelperCommon.validateEndorsementResponseError(policyNumber, null, 422);//UNPROCESSED ENTITY
		assertSoftly(softly -> {
			softly.assertThat(response.errorCode).isEqualTo(ErrorDxpEnum.Errors.ACTION_IS_NOT_AVAILABLE.getCode());
			softly.assertThat(response.message).isEqualTo(ErrorDxpEnum.Errors.ACTION_IS_NOT_AVAILABLE.getMessage());
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
		assertThat(responseNd.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.OOSE_OR_FUTURE_DATED_ENDORSEMENT.getMessage())).isTrue();
	}

	protected void pas7082_AddVehicle(PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		VehicleTab vehicleTab = new VehicleTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		GeneralTab generalTab = new GeneralTab();
		String zipCodeDefault = generalTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();

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
		assertSoftly(softly -> {
					softly.assertThat(response1.modelYear).isEqualTo("2003");
					softly.assertThat(response1.manufacturer).isEqualTo("FERRARI");
					softly.assertThat(response1.series).isEqualTo("ENZO");
					softly.assertThat(response1.model).isEqualTo("ENZO");
					softly.assertThat(response1.bodyStyle).isEqualTo("COUPE");
					softly.assertThat(response1.oid).isNotNull();
					softly.assertThat(response1.vehIdentificationNo).isEqualTo(vin2);
					softly.assertThat(response1.garagingDifferent).isEqualTo(false);
					softly.assertThat(response1.vehTypeCd).isEqualTo("PPA");
					softly.assertThat(response1.garagingAddressPostalCode).isEqualTo(zipCodeDefault);
					softly.assertThat(response1.addressLine1).isEqualTo(addressDefault);
					softly.assertThat(response1.city).isEqualTo(cityDefault);
					softly.assertThat(response1.stateProvCd).isEqualTo(stateDefault);
				}
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
		//BUG PAS-11468 When endorsement is started through service, issueing it triggers Members Last Name error
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

		ErrorResponseDto response = HelperCommon.validateEndorsementResponseError(policyNumber, endorsementDate, 422);//UNPROCESSED ENTITY
		assertSoftly(softly -> {
			softly.assertThat(response.errorCode).isEqualTo(ErrorDxpEnum.Errors.ACTION_IS_NOT_AVAILABLE.getCode());
			softly.assertThat(response.message).isEqualTo(ErrorDxpEnum.Errors.ACTION_IS_NOT_AVAILABLE.getMessage());
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

			PolicyPremiumInfo[] response = HelperCommon.viewPremiumInfo(policyNumber);
			String totalPremium = response[0].termPremium;
			String actualPremium = response[0].actualAmt;

			PolicySummary responsePolicyPending = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyPending.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyPending.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyPending.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyPending.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyPending.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyPending.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyPending.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyPending, state, "NOTENROLLED");
			assertThat(responsePolicyPending.actualAmt).isEqualTo(actualPremium);
			assertThat(responsePolicyPending.termPremium).isEqualTo(totalPremium);

			PolicySummary responsePolicyPendingRenewal = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
			assertThat(responsePolicyPendingRenewal.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
			assertThat(responsePolicyPendingRenewal.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber);

			TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyActive = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActive.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyActive.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActive.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyPending.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyPending.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyActive.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyPending, state, "NOTENROLLED");

			PolicySummary responsePolicyActiveRenewal = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
			assertThat(responsePolicyActiveRenewal.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
			assertThat(responsePolicyActiveRenewal.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber);

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

			PolicyPremiumInfo[] response = HelperCommon.viewPremiumInfo(policyNumber);
			String totalPremium = response[0].termPremium;
			String actualPremium = response[0].actualAmt;

			PolicySummary responsePolicyActive = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActive.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyActive.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActive.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActive.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyActive.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyActive, state, "NOTENROLLED");
			softly.assertThat(responsePolicyActive.actualAmt).isEqualTo(actualPremium);
			softly.assertThat(responsePolicyActive.termPremium).isEqualTo(totalPremium);

			PolicyPremiumInfo[] viewPremiumRenewalResponse = HelperCommon.viewPremiumInfoRenewal(policyNumber);
			String renewalActualPremium = viewPremiumRenewalResponse[0].actualAmt;
			String renewalTermPremium = viewPremiumRenewalResponse[0].termPremium;

			PolicySummary responsePolicyRenewalPreview = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalPreview.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalPreview.policyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.timedPolicyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalPreview.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalPreview, state, "NOTENROLLED");
			softly.assertThat(responsePolicyRenewalPreview.actualAmt).isEqualTo(null);
			softly.assertThat(responsePolicyRenewalPreview.termPremium).isEqualTo(null);

			LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

			PolicySummary responsePolicyOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOffer.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOffer.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOffer.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOffer.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOffer.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOffer, state, "NOTENROLLED");

			PolicyPremiumInfo[] viewPremiumRenewalResponse1 = HelperCommon.viewPremiumInfoRenewal(policyNumber);
			String renewalActualPremium1 = viewPremiumRenewalResponse1[0].actualAmt;
			String renewalTermPremium1 = viewPremiumRenewalResponse1[0].termPremium;

			PolicySummary responsePolicyRenewalOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOffer.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOffer.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOffer.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOffer.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOffer.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOffer, state, "NOTENROLLED");
			softly.assertThat(responsePolicyRenewalOffer.actualAmt).isEqualTo(renewalActualPremium1);
			softly.assertThat(responsePolicyRenewalOffer.termPremium).isEqualTo(renewalTermPremium1);

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyOfferExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferExpired.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferExpired.timedPolicyStatus).isEqualTo("expired");
			softly.assertThat(responsePolicyActive.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferExpired.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOfferExpired, state, "NOTENROLLED");

			PolicySummary responsePolicyRenewalOfferExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOfferExpired.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOfferExpired.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOfferExpired.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOfferExpired.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOfferExpired.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOfferExpired, state, "NOTENROLLED");

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(15));
			JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

			PolicySummary responsePolicyOfferLapsed = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferLapsed.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferLapsed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferLapsed.timedPolicyStatus).isEqualTo("expired");
			softly.assertThat(responsePolicyOfferLapsed.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferLapsed.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferLapsed.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferLapsed.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOfferLapsed, state, "NOTENROLLED");

			//BUG PAS-10482 Lapsed policy is not returned by renewal term service after R+15
			PolicySummary responsePolicyRenewalOfferLapsed = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalOfferLapsed.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOfferLapsed.policyStatus).isEqualTo("customerDeclined");
			softly.assertThat(responsePolicyRenewalOfferLapsed.timedPolicyStatus).isEqualTo("customerDeclined");
			softly.assertThat(responsePolicyRenewalOfferLapsed.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOfferLapsed.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
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

			PolicyPremiumInfo[] response = HelperCommon.viewPremiumInfo(policyNumber);
			String totalPremium = response[0].termPremium;
			String actualPremium = response[0].actualAmt;

			PolicySummary responsePolicyActive = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActive.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyActive.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActive.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActive.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyActive.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyActive, state, "ACTIVE");
			softly.assertThat(responsePolicyActive.actualAmt).isEqualTo(totalPremium);
			softly.assertThat(responsePolicyActive.termPremium).isEqualTo(actualPremium);

			PolicyPremiumInfo[] viewPremiumRenewalResponse = HelperCommon.viewPremiumInfoRenewal(policyNumber);
			String renewalActualPremium = viewPremiumRenewalResponse[0].actualAmt;
			String renewalTermPremium = viewPremiumRenewalResponse[0].termPremium;

			PolicySummary responsePolicyRenewalPreview = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalPreview.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalPreview.policyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.timedPolicyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalPreview.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalPreview, state, "ACTIVE");
			softly.assertThat(responsePolicyRenewalPreview.actualAmt).isEqualTo(renewalActualPremium);
			softly.assertThat(responsePolicyRenewalPreview.termPremium).isEqualTo(renewalTermPremium);

			LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

			PolicyPremiumInfo[] viewPremiumRenewalResponseAfterRating = HelperCommon.viewPremiumInfo(policyNumber);
			String renewalActualPremiumAfterRating = viewPremiumRenewalResponseAfterRating[0].actualAmt;
			String renewalPremiumAfterRating = viewPremiumRenewalResponseAfterRating[0].termPremium;

			PolicySummary responsePolicyOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOffer.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOffer.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOffer.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOffer.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOffer.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOffer, state, "ACTIVE");
			softly.assertThat(responsePolicyOffer.actualAmt).isEqualTo(renewalActualPremiumAfterRating);
			softly.assertThat(responsePolicyOffer.termPremium).isEqualTo(renewalPremiumAfterRating);

			PolicyPremiumInfo[] viewPremiumRenewalOfferResponse = HelperCommon.viewPremiumInfoRenewal(policyNumber);
			String renewalOfferActualPremium = viewPremiumRenewalOfferResponse[0].actualAmt;
			String renewalOfferTermPremium = viewPremiumRenewalOfferResponse[0].termPremium;

			PolicySummary responsePolicyRenewalOffer = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOffer.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyRenewalOffer.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyRenewalOffer.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOffer.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOffer.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOffer, state, "ACTIVE");
			softly.assertThat(responsePolicyRenewalOffer.actualAmt).isEqualTo(renewalOfferActualPremium);
			softly.assertThat(responsePolicyRenewalOffer.termPremium).isEqualTo(renewalOfferTermPremium);

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyOfferExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferExpired.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferExpired.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOfferExpired.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferExpired.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferExpired.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyOfferExpired, state, "ACTIVE");

			PolicySummary responsePolicyPending = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
			softly.assertThat(responsePolicyPending.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
			softly.assertThat(responsePolicyPending.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber + ".");
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

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		List<TestData> assignmentsPrimary = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue();
		String driverAssignment1 = assignmentsPrimary.get(0).toString();
		String driverAssignment2 = assignmentsPrimary.get(1).toString();
		String driverNameFromAssignment1 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(0).getValue("Driver");
		String driverNameFromAssignment2 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(1).getValue("Driver");
		DriversDto[] responseViewDriver = HelperCommon.executeViewDrivers(policyNumber);
		String driverOid1 = Arrays.stream(responseViewDriver).filter(driver -> driverNameFromAssignment1.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid2 = Arrays.stream(responseViewDriver).filter(driver -> driverNameFromAssignment2.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		DriverAssignmentDto[] response = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment1).contains(response[0].vehicleDisplayValue);
			softly.assertThat(response[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment1).contains(response[0].driverDisplayValue);
			softly.assertThat(response[0].driverOid).isEqualTo(driverOid1);
			softly.assertThat(response[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment2).contains(response[1].vehicleDisplayValue);
			softly.assertThat(response[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment2).contains(response[1].driverDisplayValue);
			softly.assertThat(response[1].driverOid).isEqualTo(driverOid2);
			softly.assertThat(response[1].relationshipType).isEqualTo("primary");
		});
		assignmentTab.saveAndExit();

		//add vehicle
		String purchaseDate = "2012-02-21";
		String newVin = "4S2CK58W8X4307498";

		Vehicle addVehicle = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, newVin);
		assertSoftly(softly ->
				softly.assertThat(addVehicle.oid).isNotEmpty()
		);
		String newVehOid = addVehicle.oid;

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		String driverAssignment3 = assignmentsPrimary.get(0).toString();
		String driverAssignment4 = assignmentsPrimary.get(1).toString();
		String driverNameFromAssignment3 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(0).getValue("Driver");
		String driverNameFromAssignment4 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(1).getValue("Driver");
		DriversDto[] responseViewDriver2 = HelperCommon.executeViewDrivers(policyNumber);
		String driverOid3 = Arrays.stream(responseViewDriver2).filter(driver -> driverNameFromAssignment3.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid4 = Arrays.stream(responseViewDriver2).filter(driver -> driverNameFromAssignment4.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		assertThat(driverOid1).isEqualTo(driverOid3);
		assertThat(driverOid2).isEqualTo(driverOid4);

		List<TestData> assignmentsUnassigned = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment5 = assignmentsUnassigned.get(0).toString();

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse[0].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse[0].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[0].driverOid).isEqualTo(driverOid3);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse[1].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse[1].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[1].driverOid).isEqualTo(driverOid4);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[1].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment5).contains(driverAssignmentAfterAddingVehicleResponse[2].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].vehicleOid).isEqualTo(newVehOid);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].driverDisplayValue).isEqualTo("unassigned");
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].driverOid).isEqualTo("unassigned");
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].relationshipType).isEqualTo("unassigned");
		});
		assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getTable().getRow(1).getCell("Select Driver").controls.comboBoxes.getFirst()
				.setValue(driverNameFromAssignment3);
		Tab.buttonTopSave.click();

		List<TestData> assignmentOccasional = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment6 = assignmentOccasional.get(0).toString();

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse1 = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse1[0].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse1[0].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].driverOid).isEqualTo(driverOid3);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse1[1].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse1[1].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].driverOid).isEqualTo(driverOid4);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment6).contains(driverAssignmentAfterAddingVehicleResponse1[2].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].vehicleOid).isEqualTo(newVehOid);
			softly.assertThat(driverAssignment6).contains(driverAssignmentAfterAddingVehicleResponse1[2].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].driverOid).isEqualTo(driverOid3);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].relationshipType).isEqualTo("occasional");
		});
		//Check that View Vehicle service returns the same veh OID, that was added
		Vehicle[] viewVehicleResponse = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
		List<Vehicle> sortedVehicles = Arrays.asList(viewVehicleResponse);
		sortedVehicles.sort(new Vehicle.VehicleComparator());
		assertThat(viewVehicleResponse).containsAll(sortedVehicles);
		Vehicle newVehicle = Arrays.stream(viewVehicleResponse).filter(veh -> newVehOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle.vehIdentificationNo).isEqualTo(newVin);
	}

	protected void pas11633_ViewDriverAssignmentAutoAssignService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		//Create a pended Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "4S2CK58W8X4307498";

		Vehicle addVehicle = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin2);
		assertSoftly(softly ->
				softly.assertThat(addVehicle.oid).isNotEmpty()
		);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());

		String vehicle1 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getTable().getRow(1).getCell("Select Vehicle").controls.comboBoxes.getFirst()
				.getValue();
		List<TestData> assignmentsPrimary = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue();
		String driver1 = assignmentsPrimary.get(0).getValue("Driver");
		List<TestData> assignmentsPrimary1 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String vehicle2 = assignmentsPrimary1.get(0).getValue("Excess Vehicle(s)");

		Vehicle[] viewVehicleResponse = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
		String vehicleOid1 = viewVehicleResponse[0].oid;
		String vehicleOid2 = viewVehicleResponse[1].oid;

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
		assertSoftly(softly -> {
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].vehicleDisplayValue).isEqualTo(vehicle1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].vehicleOid).isEqualTo(vehicleOid2);
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].driverDisplayValue).isEqualTo(driver1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].relationshipType).isEqualTo("primary");

			assertThat(driverAssignmentAfterAddingVehicleResponse[1].vehicleDisplayValue).isEqualTo(vehicle2);
			assertThat(driverAssignmentAfterAddingVehicleResponse[1].vehicleOid).isEqualTo(vehicleOid1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[1].driverDisplayValue).isEqualTo(driver1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[1].relationshipType).isEqualTo("occasional");
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
			PolicySummary responsePolicyStub = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStub.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStub.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStub.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStub.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStub.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStub.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStub.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStub.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferRated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferRated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferRated.policyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.timedPolicyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferRated.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferRated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferRated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferRated.renewalCycle).isEqualTo(1);
			//BUG PAS-10480 eValue Status is not shown for conversion stub policy

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

			PolicySummary responsePolicyStubProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposed.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposed.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposed.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposed.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposed.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposed.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(effDate));
			JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
			mainApp().open();
			SearchPage.openBilling(policyNum);
			Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

			PolicySummary responsePolicyStubProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposedPaid.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposedPaid.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposedPaid.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferProposedPaid.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyOfferProposedPaid.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposedPaid.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposedPaid.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			PolicySummary responsePolicyActivated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActivated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyActivated.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActivated.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActivated.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyActivated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyActivated.renewalCycle).isEqualTo(1);

			PolicySummary responsePolicyStubExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.NOT_FOUND.getStatusCode());
			softly.assertThat(responsePolicyStubExpired.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
			softly.assertThat(responsePolicyStubExpired.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNum + ".");
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
			PolicySummary responsePolicyStub = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStub.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStub.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStub.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStub.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStub.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStub.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStub.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStub.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferRated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferRated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferRated.policyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.timedPolicyStatus).isEqualTo("rated");
			softly.assertThat(responsePolicyOfferRated.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferRated.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferRated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferRated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferRated.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(effDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

			PolicySummary responsePolicyStubProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposed.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposed.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposed.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposed = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposed.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyOfferProposed.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposed.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposed.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(effDate));
			JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
			mainApp().open();
			SearchPage.openBilling(policyNum);
			Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

			PolicySummary responsePolicyStubProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposedPaid.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposedPaid.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposedPaid.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposedPaid = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferProposedPaid.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyOfferProposedPaid.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposedPaid.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposedPaid.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			PolicySummary responsePolicyActivated = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActivated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyActivated.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActivated.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActivated.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyActivated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyActivated.renewalCycle).isEqualTo(1);

			PolicySummary responsePolicyStubExpired = HelperCommon.executeViewPolicyRenewalSummary(policyNum, "renewal", Response.Status.NOT_FOUND.getStatusCode());
			softly.assertThat(responsePolicyStubExpired.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
			softly.assertThat(responsePolicyStubExpired.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNum + ".");
		});
	}

	private void eValueStatusCheck(SoftAssertions softly, PolicySummary responsePolicyPending, String state, String eValueStatus) {
		if ("CA".equals(state)) {
			softly.assertThat(responsePolicyPending.eValueStatus).isEqualTo(null);
		} else {
			softly.assertThat(responsePolicyPending.eValueStatus).isEqualTo(eValueStatus);
		}
	}

	protected void pas9456_9455_PolicyLockUnlockServicesBody(SoftAssertions softly) {
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		mainApp().open();
		String policyNumber = getCopiedPolicy();
		mainApp().close();

		//Lock policy id1 and check service response
		PolicyLockUnlockDto response = HelperCommon.executePolicyLockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		softly.assertThat(response.policyNumber).isEqualTo(policyNumber);
		softly.assertThat(response.status).isEqualTo("Locked");

		//Hit start endorsement info service with Id1
		ValidateEndorsementResponse endorsementInfoResp1 = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate, SESSION_ID_1);
		assertThat(endorsementInfoResp1.ruleSets.get(0).errors).isEmpty();

		//Hit start endorsement info service with Id2
		ValidateEndorsementResponse endorsementInfoResp2 = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate, SESSION_ID_2);
		assertThat(endorsementInfoResp2.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.POLICY_IS_LOCKED.getMessage())).isTrue();

		//Try to lock policy with id2
		PolicyLockUnlockDto response1 = HelperCommon.executePolicyLockService(policyNumber, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), SESSION_ID_2);
		softly.assertThat(response1.errorCode).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getCode());
		softly.assertThat(response1.message).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getMessage());

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().start();

		//Check if policy was locked in PAS
		assertThat(errorTab.tableBaseErrors.getRow(1).getCell("Description").getValue()).isEqualTo(ErrorDxpEnum.Errors.COULD_NOT_ACQUIRE_LOCK.getMessage());
		PolicySummaryPage.buttonBackFromErrorPage.click();

		//Try unlock policy with id2
		PolicyLockUnlockDto response2 = HelperCommon.executePolicyUnlockService(policyNumber, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), SESSION_ID_2);
		softly.assertThat(response2.errorCode).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getCode());
		softly.assertThat(response2.message).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getMessage());

		//Unlock policy with id1
		PolicyLockUnlockDto response3 = HelperCommon.executePolicyUnlockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		softly.assertThat(response3.policyNumber).isEqualTo(policyNumber);
		softly.assertThat(response3.status).isEqualTo("Unlocked");

		//Start do endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		//Check if policy can be locked using lock service
		PolicyLockUnlockDto response4 = HelperCommon.executePolicyLockService(policyNumber, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), SESSION_ID_1);
		softly.assertThat(response4.errorCode).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getCode());
		softly.assertThat(response4.message).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getMessage());

		//Check if policy can be unlocked using unlock service
		PolicyLockUnlockDto response5 = HelperCommon.executePolicyUnlockService(policyNumber, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), SESSION_ID_1);
		softly.assertThat(response5.errorCode).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getCode());
		softly.assertThat(response5.message).isEqualTo(ErrorDxpEnum.Errors.ENTITY_IS_LOCKED_BY_OTHER_USER.getMessage());
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
		PolicyPremiumInfo[] rateResponse = HelperCommon.executeEndorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			softly.assertThat(rateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(rateResponse[0].premiumCode).isEqualTo("GWT");
		});
		Dollar dxpPremium = new Dollar(rateResponse[0].actualAmt);

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
		PolicyPremiumInfo[] rateResponse2 = HelperCommon.executeEndorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		Dollar dxpPremium2 = new Dollar(rateResponse2[0].actualAmt);

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

		if (response4[0].vehIdentificationNo.contains(vin1)) {
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

		List<Vehicle> sortedVehicles = Arrays.asList(viewVehicleResponse);

		sortedVehicles.sort(new Vehicle.VehicleComparator());

		assertSoftly(softly -> {

			assertThat(viewVehicleResponse).containsAll(sortedVehicles);

			Vehicle vehicle1 = Arrays.stream(viewVehicleResponse).filter(veh -> "1GAZG1FG7D1145543".equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle2 = Arrays.stream(viewVehicleResponse).filter(veh -> "WDCYC7BB0B6729451".equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle3 = Arrays.stream(viewVehicleResponse).filter(veh -> "5B4MP67G123353230".equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicle4 = Arrays.stream(viewVehicleResponse).filter(veh -> "5FNRL5H64GB087983".equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Conversion");

			// Perform endorsement
			AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			assertThat(response.policyNumber).isEqualTo(policyNumber);

			SearchPage.openPolicy(policyNumber);

			//Add new vehicle to have pending vehicle
			String purchaseDate = "2013-02-22";
			String vin2 = "1HGFA16526L081415";
			Vehicle addVehicleResponse = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin2);
			assertThat(addVehicleResponse.oid).isNotEmpty();

			Vehicle[] viewVehicleEndorsementResponse = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
			List<Vehicle> sortedVehicles1 = Arrays.asList(viewVehicleEndorsementResponse);
			sortedVehicles1.sort(new Vehicle.VehicleComparator());
			softly.assertThat(viewVehicleEndorsementResponse).containsAll(sortedVehicles1);
		});
	}

	protected void pas9610_UpdateVehicleService() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Get garage address from UI
		policy.policyInquiry().start();
		GeneralTab generalTab = new GeneralTab();
		String zipCodeDefault = generalTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();
		GeneralTab.buttonCancel.click();

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

			//verify updated information with pendedEndorsementValidateVehicleInfo
			Vehicle[] pendedEndorsementValidateVehicleResponse = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
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

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].garagingAddressPostalCode).isEqualTo(zipCodeDefault);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].addressLine1).isEqualTo(addressDefault);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].stateProvCd).isEqualTo(stateDefault);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].city).isEqualTo(cityDefault);
		});

		//PAS-7145 Start
		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";

		//send request to update vehicle service
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.ownership = "OWN";
		updateGaragingAddressVehicleRequest.usage = "Pleasure";
		updateGaragingAddressVehicleRequest.salvaged = false;
		updateGaragingAddressVehicleRequest.garagingDifferent = false;
		updateGaragingAddressVehicleRequest.antiTheft = "STD";
		updateGaragingAddressVehicleRequest.registeredOwner = false;
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddressPostalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.city = cityGarage;
		updateGaragingAddressVehicleRequest.stateProvCd = stateGarage;

		Vehicle updateVehicleResponseGaragingAddress = HelperCommon.updateVehicle(policyNumber, oid, updateGaragingAddressVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponseGaragingAddress.ownership).isEqualTo("OWN");
			softly.assertThat(updateVehicleResponseGaragingAddress.usage).isEqualTo("Pleasure");
			softly.assertThat(updateVehicleResponseGaragingAddress.salvaged).isEqualTo(false);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingDifferent).isEqualTo(true);
			softly.assertThat(updateVehicleResponseGaragingAddress.antiTheft).isEqualTo("STD");
			softly.assertThat(updateVehicleResponseGaragingAddress.registeredOwner).isEqualTo(false);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddressPostalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.addressLine1).isEqualTo(addressGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.city).isEqualTo(cityGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.stateProvCd).isEqualTo(stateGarage);

			Vehicle[] pendedEndorsementValidateVehicleResponse = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].modelYear).isEqualTo(updateVehicleResponse.modelYear);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].manufacturer).isEqualTo(updateVehicleResponse.manufacturer);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].series).isEqualTo(updateVehicleResponse.series);

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].model).isEqualTo(updateVehicleResponse.model);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].bodyStyle).isEqualTo(updateVehicleResponse.bodyStyle);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].vehIdentificationNo).isEqualTo(updateVehicleResponse.vehIdentificationNo);

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].ownership).isEqualTo("OWN");
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].usage).isEqualTo("Pleasure");
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].salvaged).isEqualTo(false);

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].garagingDifferent).isEqualTo(true);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].antiTheft).isEqualTo("STD");
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].registeredOwner).isEqualTo(false);

			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].garagingAddressPostalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].addressLine1).isEqualTo(addressGarage);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].stateProvCd).isEqualTo(stateGarage);
			softly.assertThat(pendedEndorsementValidateVehicleResponse[0].city).isEqualTo(cityGarage);
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
		HelperCommon.executeEndorsementBind(policyNumber, authorizedBy, Response.Status.OK.getStatusCode());
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

			PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.executeEndorsementRate(policyNumber, Response.Status.OK.getStatusCode());
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();

			//Create pended endorsement
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").verify.value(PREMIUM_CALCULATED);
			Tab.buttonBack.click();

			//issue through service
			HelperCommon.executeEndorsementBind(policyNumber, authorizedBy, Response.Status.OK.getStatusCode());
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
			softly.assertThat(response[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(response[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(new Dollar(response[0].actualAmt)).isEqualTo(new Dollar(actualPremium));
			softly.assertThat(new Dollar(response[0].termPremium)).isEqualTo(new Dollar(totalPremium));
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
		assertThat(addVehicle.oid).isNotEmpty();

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
			softly.assertThat(response[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(response[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(new Dollar(response[0].actualAmt)).isEqualTo(new Dollar(actualPremium));
			softly.assertThat(new Dollar(response[0].termPremium)).isEqualTo(new Dollar(totalPremium));
		});
	}

	protected void pas11741_ViewManageVehicleLevelCoverages(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();

		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AllVehiclesCoverage").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		AAAEndorseResponse endorsementResponse = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible =
				new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()).replace("  (+$0.00)", ""));
		Dollar collisionDeductible =
				new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel()).replace("  (+$0.00)", ""));
		//TO Do Next Sprint
		//String full_safety_glass_veh1 = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCov = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel()).replace(" (+$0.00)", "");
		String transportationExpense =
				premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel()).replace(" (Included)", "")
						.replace(" (+$0.00)", "").replace("$", "");
		String towingAndLabor = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel()).replace(" (Included)", "")
				.replace(" (+$0.00)", "").replace("$", "");
		Dollar excessElectronicEquipment = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel()));

		PolicyCoverageInfo coverageResponse = HelperCommon.viewCoverageInfo(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Transportation Expense");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo(transportationExpense);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).contains(transportationExpense);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("per day/maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Towing and Labor Coverage");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit).isEqualTo("0/0");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(false);

		});

		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewCoverageInfoEndorsement(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay))
					.isEqualTo(collisionDeductible);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Transportation Expense");
			assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo(transportationExpense);
			assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).contains(transportationExpense);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("per day/maximum");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Towing and Labor Coverage");
			assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit).isEqualTo("0/0");
			assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(false);
		});
	}

	protected void pas7147_VehicleUpdateBusinessBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle responseAddVehicle = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.usage = "Business";
		//TODO remove garaging address from code once it is not necessary to pass it
		//updateVehicleRequest.garagingDifferent = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("Business");
			assertThat(((VehicleUpdateResponseDto) updateVehicleResponse).ruleSets.get(0).errors.get(0)).contains("Usage is Business");
		});

		//Check premium after new vehicle was added
		HashMap<String, String> rateResponse = HelperCommon.executeEndorsementRateError(policyNumber, 422);
		assertThat(rateResponse.entrySet().toString()).contains(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());

		HashMap<String, String> bindResponse = HelperCommon.executeEndorsementBindError(policyNumber, "PAS-7147", 422);
		assertThat(bindResponse.entrySet().toString()).contains(ErrorDxpEnum.Errors.VALIDATION_ERROR_HAPPENED_DURING_BIND.getMessage());
	}

	protected void pas7147_VehicleUpdateRegisteredOwnerBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle responseAddVehicle = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.registeredOwner = false;
		//TODO remove garaging address from code once it is not necessary to pass it
		//updateVehicleRequest.garagingDifferent = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);
			assertThat(((VehicleUpdateResponseDto) updateVehicleResponse).ruleSets.get(0).errors.get(0)).contains("Registered Owners");
		});

		//Check premium after new vehicle was added
		HashMap<String, String> rateResponse = HelperCommon.executeEndorsementRateError(policyNumber, 422);
		assertThat(rateResponse.entrySet().toString()).contains(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());

		HashMap<String, String> bindResponse = HelperCommon.executeEndorsementBindError(policyNumber, "PAS-7147", 422);
		assertThat(bindResponse.entrySet().toString()).contains(ErrorDxpEnum.Errors.VALIDATION_ERROR_HAPPENED_DURING_BIND.getMessage());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Business
	 * @scenario 1. Create active policy
	 * 2. Run through the flow using services
	 * //View Policy
	 * //View Renewal which doesn't exist
	 * //get all vehicles
	 * //get all drivers
	 * //get all coverages
	 * //Check endorsement is allowed
	 * //Lock the policy
	 * //Create pended endorsement
	 * //Validate VIN
	 * //Add new vehicle
	 * //Update Vehicle with proper Usage and Registered Owner
	 * //Check vehicle update service when  garage address is different
	 * //View endorsement vehicles
	 * //View endorsement drivers
	 * //View driver assignment if VA
	 * //Rate endorsement
	 * //View premium after new vehicle was added
	 * //Bind endorsement
	 * //Unlock policy
	 */
	protected void pas12866_e2eBctBody(String state, boolean isNewPolicy, SoftAssertions softly) {
		String policyNumber = "";
		if (isNewPolicy) {
			mainApp().open();
			policyNumber = getCopiedPolicy();
		} else {
			if ("AZ".equals(state)) {
				policyNumber = "AZSS926232005";
			} else if ("KY".equals(state)) {
				policyNumber = "KYSS926232030";
			}
		}

		mainApp().open();
		//View Policy
		PolicySummary responseViewPolicy = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
		softly.assertThat(responseViewPolicy.policyNumber).isEqualTo(policyNumber);
		softly.assertThat(responseViewPolicy.policyStatus).isEqualTo("issued");

		//View Renewal which doesn't exist
		PolicySummary responseViewRenewal = HelperCommon.executeViewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
		softly.assertThat(responseViewRenewal.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
		softly.assertThat(responseViewRenewal.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber + ".");

		//get all vehicles
		Vehicle[] responseViewVehicles = HelperCommon.executeVehicleInfoValidate(policyNumber);
		String originalVehicle = responseViewVehicles[0].oid;

		//get all drivers
		DriversDto[] responseViewDrivers = HelperCommon.executeViewDrivers(policyNumber);
		String originalDriver = responseViewDrivers[0].oid;

		//get all coverages
		PolicyCoverageInfo coverageResponse = HelperCommon.viewCoverageInfo(policyNumber);
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");

		//Check endorsement is allowed
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse responseValidateEndorse = HelperCommon.executeEndorsementsValidate(policyNumber, endorsementDate);
		softly.assertThat(responseValidateEndorse.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");

		//Lock the policy
		PolicyLockUnlockDto responseLock = HelperCommon.executePolicyLockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		assertThat(responseLock.policyNumber).isEqualTo(policyNumber);
		assertThat(responseLock.status).isEqualTo("Locked");

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.executeEndorseStart(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Validate VIN
		AAAVehicleVinInfoRestResponseWrapper vinValidateResponse = HelperCommon.executeVinValidate(policyNumber, vin, endorsementDate);
		softly.assertThat(vinValidateResponse.vehicles.get(0).vin).isEqualTo(vin);

		//Add new vehicle
		Vehicle responseAddVehicle = HelperCommon.executeVehicleAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		SearchPage.openPolicy(policyNumber);

		//Update Vehicle with proper Usage and Registered Owner
		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");

		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddressPostalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.city = cityGarage;
		updateGaragingAddressVehicleRequest.stateProvCd = stateGarage;
		Vehicle updateVehicleGaragingAddressResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingDifferent).isEqualTo(true);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddressPostalCode).isEqualTo(zipCodeGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.addressLine1).isEqualTo(addressGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.city).isEqualTo(cityGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.stateProvCd).isEqualTo(stateGarage);
		SearchPage.openPolicy(policyNumber);

		//View endorsement vehicles
		Vehicle[] viewEndorsementVehicleResponse = HelperCommon.pendedEndorsementValidateVehicleInfo(policyNumber);
		List<Vehicle> sortedVehicles = Arrays.asList(viewEndorsementVehicleResponse);
		sortedVehicles.sort(new Vehicle.VehicleComparator());
		assertThat(viewEndorsementVehicleResponse).containsAll(sortedVehicles);
		Vehicle newVehicle = Arrays.stream(viewEndorsementVehicleResponse).filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle.vehIdentificationNo).isEqualTo(vin);

		//View endorsement drivers
		DriversDto[] responseViewDriver = HelperCommon.executeEndorsementViewDrivers(policyNumber);
		String driverOid = responseViewDriver[0].oid;
		assertThat(driverOid.equals(originalDriver)).isTrue();

		//View driver assignment if VA
		if("VA".equals(state)) {
			DriverAssignmentDto[] responseDriverAssignment = HelperCommon.pendedEndorsementDriverAssignmentInfo(policyNumber);
			softly.assertThat(responseDriverAssignment[0].vehicleOid).isEqualTo(originalVehicle);
			softly.assertThat(responseDriverAssignment[0].driverOid).isEqualTo(driverOid);
			softly.assertThat(responseDriverAssignment[0].relationshipType).isEqualTo("primary");

			softly.assertThat(responseDriverAssignment[1].vehicleOid).isEqualTo(newVehicleOid);
			softly.assertThat(responseDriverAssignment[1].driverOid).isEqualTo(driverOid);
			softly.assertThat(responseDriverAssignment[1].relationshipType).isEqualTo("occasional");
		}

		//Rate endorsement
		PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.executeEndorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
		softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();

		//View premium after new vehicle was added
		PolicyPremiumInfo[] viewPremiumInfoPendedEndorsementResponse = HelperCommon.viewPremiumInfoPendedEndorsementResponse(policyNumber);
		softly.assertThat(viewPremiumInfoPendedEndorsementResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		softly.assertThat(viewPremiumInfoPendedEndorsementResponse[0].premiumCode).isEqualTo("GWT");
		softly.assertThat(new Dollar(viewPremiumInfoPendedEndorsementResponse[0].actualAmt)).isNotNull();
		softly.assertThat(new Dollar(viewPremiumInfoPendedEndorsementResponse[0].termPremium)).isNotNull();

		//Bind endorsement
		HelperCommon.executeEndorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		//Unlock policy
		PolicyLockUnlockDto responseUnlock = HelperCommon.executePolicyUnlockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		assertThat(responseUnlock.policyNumber).isEqualTo(policyNumber);
		assertThat(responseUnlock.status).isEqualTo("Unlocked");
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
