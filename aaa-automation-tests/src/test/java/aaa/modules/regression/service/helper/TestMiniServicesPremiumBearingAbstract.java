package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.MIDDLE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static aaa.modules.regression.service.helper.preconditions.TestMiniServicesNonPremiumBearingAbstractPreconditions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.BooleanUtils;
import org.assertj.core.api.SoftAssertions;
import org.springframework.util.CollectionUtils;
import org.testng.ITestContext;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
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
import aaa.main.modules.policy.auto_ss.actiontabs.UpdateRulesOverrideActionTab;
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
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesPremiumBearingAbstract extends PolicyBaseTest {

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

	protected void pas6560_endorsementValidateAllowedNoEffectiveDate() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		mainApp().close();

		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, null);
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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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

		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertSoftly(softly ->
				softly.assertThat(response.policyNumber).isEqualTo(policyNumber)
		);

		//immediate endorsement delete attempt should not be allowed for UT
		ValidateEndorsementResponse responseValidateCanCreateEndorsement1 = HelperCommon.startEndorsement(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement1.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement1.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement1.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.CUSTOMER_CREATED_ENDORSEMENT.getMessage());
		});

		//endorsement delete attempt should not be allowed on the Delay Day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete - 1));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement2 = HelperCommon.startEndorsement(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement2.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.CUSTOMER_CREATED_ENDORSEMENT.getMessage());
		});

		//endorsement delete attempt should be allowed on the Delay Day + 1 day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysDelayBeforeDelete));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.startEndorsement(policyNumber, null);
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
		AAAEndorseResponse responseNewConfigEffective = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertSoftly(softly ->
				softly.assertThat(responseNewConfigEffective.policyNumber).isEqualTo(policyNumber)
		);
		//validation returns "can be deleted"
		ValidateEndorsementResponse responseValidateCanCreateEndorsementNewConfigEffective = HelperCommon.startEndorsement(policyNumber, null);
		assertSoftly(softly ->
				softly.assertThat(responseValidateCanCreateEndorsementNewConfigEffective.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle")
		);

		//shift time till different config becomes current for AZ = 5 days delay , delete old endorsement, add new endorsement
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysForNewConfigVersion + 1));
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertSoftly(softly ->
				softly.assertThat(response.policyNumber).isEqualTo(policyNumber)
		);

		//endorsement delete attempt should not be allowed on the Delay Day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysForNewConfigVersion + numberOfDaysDelayBeforeDelete));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement2 = HelperCommon.startEndorsement(policyNumber, null);
		assertSoftly(softly -> {
			softly.assertThat(responseValidateCanCreateEndorsement2.allowedEndorsements).isEmpty();
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).name).isEqualTo("PolicyRules");
			softly.assertThat(responseValidateCanCreateEndorsement2.ruleSets.get(0).errors.get(0)).contains(ErrorDxpEnum.Errors.CUSTOMER_CREATED_ENDORSEMENT.getMessage());
		});

		//endorsement delete attempt should be allowed on the Delay Day + 1 day
		TimeSetterUtil.getInstance().nextPhase(testStartDate.plusDays(numberOfDaysForNewConfigVersion + numberOfDaysDelayBeforeDelete + 1));
		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.startEndorsement(policyNumber, null);
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
		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.startEndorsement(policyNumber, null);
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

		ValidateEndorsementResponse responseValidateCanCreateEndorsement3 = HelperCommon.startEndorsement(policyNumber, null);
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
		AAAVehicleVinInfoRestResponseWrapper response = HelperCommon.executeVinInfo(policyNumber, vin1, endorsementDate);
		softly.assertThat(response.vehicles).isEmpty();
		softly.assertThat(response.validationMessage).isEqualTo("Invalid VIN length");

		String vin2 = "12345678901234567890"; //VIN too long
		AAAVehicleVinInfoRestResponseWrapper response2 = HelperCommon.executeVinInfo(policyNumber, vin2, null);
		softly.assertThat(response2.vehicles).isEmpty();
		softly.assertThat(response2.validationMessage).isEqualTo("Invalid VIN length");

		String vin3 = "4T1BF1FK0H1234567"; //VIN check digit failed
		AAAVehicleVinInfoRestResponseWrapper response3 = HelperCommon.executeVinInfo(policyNumber, vin3, null);
		softly.assertThat(response3.vehicles).isEmpty();
		softly.assertThat(response3.validationMessage).isEqualTo("Check Digit is Incorrect");

		String vin4 = "4T1BF1FK0H"; //VIN from VIN table but too short
		AAAVehicleVinInfoRestResponseWrapper response4 = HelperCommon.executeVinInfo(policyNumber, vin4, null);
		softly.assertThat(response4.vehicles).isEmpty();
		softly.assertThat(response4.validationMessage).isEqualTo("Invalid VIN length");

		String vin5 = "1D30E42J451234567"; //VIN NOT from VIN table to Check VIN service
		AAAVehicleVinInfoRestResponseWrapper response5 = HelperCommon.executeVinInfo(policyNumber, vin5, null);
		softly.assertThat(response5.vehicles).isEmpty();
		softly.assertThat(response5.validationMessage).isEqualTo("VIN is not on AAA VIN Table");

		String vin0 = "4T1BF1FK0HU624693"; //VIN from VIN table
		AAAVehicleVinInfoRestResponseWrapper response0 = HelperCommon.executeVinInfo(policyNumber, vin0, endorsementDate);
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
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
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
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehiclesGaragingAddress").getTestDataList("VehicleTab")).resolveLinks();
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

		ViewVehicleResponse response = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicleSt = response.vehicleList.stream().filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleNd = response.vehicleList.stream().filter(vehicle -> vehIdentificationNo2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt).isNotNull();
		softly.assertThat(vehicleSt.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt.series).isEqualTo(series1);
		softly.assertThat(vehicleSt.model).isEqualTo(model1);
		softly.assertThat(vehicleSt.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt.vehicleOwnership.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt.garagingDifferent)).isEqualTo(garagingDifferent1);
		softly.assertThat(vehicleSt.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt.oid).isNotEmpty();
		softly.assertThat(vehicleSt.garagingAddress.postalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt.garagingAddress.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt.garagingAddress.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt.garagingAddress.city).isEqualTo(city1);

		softly.assertThat(vehicleNd).isNotNull();
		softly.assertThat(vehicleNd.modelYear).isEqualTo(modelYear2);
		softly.assertThat(vehicleNd.manufacturer).isEqualTo(manufacturer2);
		softly.assertThat(vehicleNd.series).isEqualTo(series2);
		softly.assertThat(vehicleNd.model).isEqualTo(model2);
		softly.assertThat(vehicleNd.bodyStyle).isEqualTo(bodyStyle2);
		softly.assertThat(vehicleNd.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleNd.vehicleOwnership.ownership).isEqualTo(ownership2);
		softly.assertThat(vehicleNd.usage).isEqualTo(usage2);
		softly.assertThat(vehicleNd.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleNd.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleNd.antiTheft).isEqualTo(antiTheft2);
		softly.assertThat(vehicleNd.vehTypeCd).isEqualTo(vehType2);
		softly.assertThat(vehicleNd.oid).isNotEmpty();
		softly.assertThat(vehicleNd.garagingAddress.postalCode).isEqualTo(zipCode2);
		softly.assertThat(vehicleNd.garagingAddress.addressLine1).isEqualTo(address2);
		softly.assertThat(vehicleNd.garagingAddress.stateProvCd).isEqualTo(state2);
		softly.assertThat(vehicleNd.garagingAddress.city).isEqualTo(city2);

		VehicleTab.buttonCancel.click();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(VIN).setValue("1FMEU15H7KLB19840");
		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		ViewVehicleResponse response1 = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicleSt1 = response1.vehicleList.stream().filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleNd1 = response1.vehicleList.stream().filter(vehicle -> vehIdentificationNo2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt1).isNotNull();
		softly.assertThat(vehicleSt1.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt1.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt1.series).isEqualTo(series1);
		softly.assertThat(vehicleSt1.model).isEqualTo(model1);
		softly.assertThat(vehicleSt1.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt1.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt1.vehicleOwnership.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt1.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt1.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt1.garagingDifferent)).isEqualTo(garagingDifferent1);
		softly.assertThat(vehicleSt1.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt1.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt1.oid).isNotEmpty();
		softly.assertThat(vehicleSt1.garagingAddress.postalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt1.garagingAddress.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt1.garagingAddress.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt1.garagingAddress.city).isEqualTo(city1);

		softly.assertThat(vehicleNd1).isNotNull();
		softly.assertThat(vehicleNd1.modelYear).isEqualTo(modelYear2);
		softly.assertThat(vehicleNd1.manufacturer).isEqualTo(manufacturer2);
		softly.assertThat(vehicleNd1.series).isEqualTo(series2);
		softly.assertThat(vehicleNd1.model).isEqualTo(model2);
		softly.assertThat(vehicleNd1.bodyStyle).isEqualTo(bodyStyle2);
		softly.assertThat(vehicleNd1.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleNd1.vehicleOwnership.ownership).isEqualTo(ownership2);
		softly.assertThat(vehicleNd1.usage).isEqualTo(usage2);
		softly.assertThat(vehicleNd1.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleNd1.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleNd1.antiTheft).isEqualTo(antiTheft2);
		softly.assertThat(vehicleNd1.vehTypeCd).isEqualTo(vehType2);
		softly.assertThat(vehicleNd1.oid).isNotEmpty();
		softly.assertThat(vehicleNd1.garagingAddress.postalCode).isEqualTo(zipCode2);
		softly.assertThat(vehicleNd1.garagingAddress.addressLine1).isEqualTo(address2);
		softly.assertThat(vehicleNd1.garagingAddress.stateProvCd).isEqualTo(state2);
		softly.assertThat(vehicleNd1.garagingAddress.city).isEqualTo(city2);

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

		ViewVehicleResponse response2 = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicleSt2 = response2.vehicleList.stream().filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleRd2 = response2.vehicleList.stream().filter(vehicle -> vehIdentificationNo3.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt2).isNotNull();
		softly.assertThat(vehicleSt2.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt2.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt2.series).isEqualTo(series1);
		softly.assertThat(vehicleSt2.model).isEqualTo(model1);
		softly.assertThat(vehicleSt2.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt2.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt2.vehicleOwnership.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt2.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt2.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt2.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleSt2.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt2.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt2.oid).isNotEmpty();
		softly.assertThat(vehicleSt2.garagingAddress.postalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt2.garagingAddress.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt2.garagingAddress.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt2.garagingAddress.city).isEqualTo(city1);

		softly.assertThat(vehicleRd2).isNotNull();
		softly.assertThat(vehicleRd2.modelYear).isEqualTo(modelYear3);
		softly.assertThat(vehicleRd2.manufacturer).isEqualTo(manufacturer3);
		softly.assertThat(vehicleRd2.series).isEqualTo(series3);
		softly.assertThat(vehicleRd2.model).isEqualTo(model3);
		softly.assertThat(vehicleRd2.bodyStyle).isEqualTo(bodyStyle3);
		softly.assertThat(vehicleRd2.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleRd2.vehicleOwnership.ownership).isEqualTo(ownership3);
		softly.assertThat(vehicleRd2.usage).isEqualTo(usage3);
		softly.assertThat(vehicleRd2.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleRd2.garagingDifferent)).isEqualTo(garagingDifferent3);
		softly.assertThat(vehicleRd2.antiTheft).isEqualTo(antiTheft3);
		softly.assertThat(vehicleRd2.vehTypeCd).isEqualTo(vehType3);
		softly.assertThat(vehicleRd2.oid).isNotEmpty();
		softly.assertThat(vehicleRd2.garagingAddress.postalCode).isEqualTo(zipCode3);
		softly.assertThat(vehicleRd2.garagingAddress.addressLine1).isEqualTo(address3);
		softly.assertThat(vehicleRd2.garagingAddress.stateProvCd).isEqualTo(state3);
		softly.assertThat(vehicleRd2.garagingAddress.city).isEqualTo(city3);
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
			ViewDriversResponse response = HelperCommon.viewPolicyDrivers(policyNumber);
			DriversDto driverSt = response.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd = response.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driverRd = response.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);

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
			ViewDriversResponse response2 = HelperCommon.viewPolicyDrivers(policyNumber);
			DriversDto driverSt2 = response2.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd2 = response2.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driverRd2 = response2.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);

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
			ViewDriversResponse response3 = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driverSt3 = response3.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd3 = response3.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

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
			ViewDriversResponse response4 = HelperCommon.viewPolicyDrivers(policyNumber);
			DriversDto driverSt4 = response4.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd4 = response4.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

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

		ViewVehicleResponse response = HelperCommon.viewPolicyVehicles(policyNumber);
		assertThat(CollectionUtils.isEmpty(response.vehicleList)).isTrue();
	}

	protected void pas9337_CheckStartEndorsementInfoServerResponseErrorForEffectiveDate() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		assertSoftly(softly -> {

			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			ValidateEndorsementResponse responseNd = HelperCommon.startEndorsement(policyNumber, endorsementDate);
			assertThat(responseNd.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.STATE_DOES_NOT_ALLOW_ENDORSEMENTS.getMessage())).isTrue();

			DBService.get().executeUpdate(INSERT_EFFECTIVE_DATE);

			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(8));
			String endorsementDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			ValidateEndorsementResponse responseNd1 = HelperCommon.startEndorsement(policyNumber, endorsementDate1);

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
		ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, null);
		assertThat(response.ruleSets.get(0).errors.toString().contains(ErrorDxpEnum.Errors.POLICY_TERM_DOES_NOT_EXIST.getMessage())).isTrue();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(20));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().start();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(20).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Check Policy locked message
		ValidateEndorsementResponse responseNd = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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

		ErrorResponseDto response = HelperCommon.startEndorsementError(policyNumber, null, 422);//UNPROCESSED ENTITY
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

		ValidateEndorsementResponse responseNd = HelperCommon.startEndorsement(policyNumber, endorsementDate);
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
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Add new vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "1HGEM21504L055795";

		Vehicle response1 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
		assertSoftly(softly -> {
					softly.assertThat(response1.modelYear).isEqualTo("2004");
					softly.assertThat(response1.manufacturer).isEqualTo("HONDA");
					softly.assertThat(response1.series).isEqualTo("CIVIC LX");
					softly.assertThat(response1.model).isEqualTo("CIVIC");
					softly.assertThat(response1.bodyStyle).isEqualTo("COUPE");
					softly.assertThat(response1.oid).isNotNull();
					softly.assertThat(response1.vehIdentificationNo).isEqualTo(vin2);
					softly.assertThat(response1.garagingDifferent).isEqualTo(false);
					softly.assertThat(response1.vehTypeCd).isEqualTo("PPA");
					softly.assertThat(response1.garagingAddress.postalCode).isEqualTo(zipCodeDefault);
					softly.assertThat(response1.garagingAddress.addressLine1).isEqualTo(addressDefault);
					softly.assertThat(response1.garagingAddress.city).isEqualTo(cityDefault);
					softly.assertThat(response1.garagingAddress.stateProvCd).isEqualTo(stateDefault);
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
		ViewVehicleResponse response3 = HelperCommon.viewPolicyVehicles(policyNumber);
		if (response3.vehicleList.get(0).vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin2);
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin1);
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

		ErrorResponseDto response = HelperCommon.startEndorsementError(policyNumber, endorsementDate, 422);//UNPROCESSED ENTITY
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

			//Start PAS-10351
			policy.policyInquiry().start();
			GeneralTab generalTab = new GeneralTab();
			String zipCode1 = generalTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
			String address1 = generalTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
			String city1 = generalTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
			String state1 = generalTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();
			GeneralTab.buttonCancel.click();

			String policyNumber = PolicySummaryPage.getPolicyNumber();
			LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

			PolicyPremiumInfo[] response = HelperCommon.viewPolicyPremiums(policyNumber);
			String totalPremium = response[0].termPremium;
			String actualPremium = response[0].actualAmt;

			PolicySummary responsePolicyPending = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
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
			//BUG PAS-14396 PolicySummaryService doesnt return ResidentialAddress when renewal exists
			softly.assertThat(responsePolicyPending.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyPending.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyPending.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyPending.residentialAddress.stateProvCd).isEqualTo(state1);

			PolicySummary responsePolicyPendingRenewal = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
			assertThat(responsePolicyPendingRenewal.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
			assertThat(responsePolicyPendingRenewal.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber);

			TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyActive = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActive.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyActive.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActive.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyPending.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyPending.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyActive.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyPending, state, "NOTENROLLED");
			softly.assertThat(responsePolicyActive.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyActive.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyActive.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyActive.residentialAddress.stateProvCd).isEqualTo(state1);

			PolicySummary responsePolicyActiveRenewal = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
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

			//Start PAS-10351
			policy.policyInquiry().start();
			GeneralTab generalTab = new GeneralTab();
			String zipCode1 = generalTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
			String address1 = generalTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
			String city1 = generalTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
			String state1 = generalTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();
			GeneralTab.buttonCancel.click();

			String policyNumber = PolicySummaryPage.getPolicyNumber();
			LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

			LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);

			PolicyPremiumInfo[] response = HelperCommon.viewPolicyPremiums(policyNumber);
			String totalPremium = response[0].termPremium;
			String actualPremium = response[0].actualAmt;

			PolicySummary responsePolicyActive = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
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
			softly.assertThat(responsePolicyActive.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyActive.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyActive.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyActive.residentialAddress.stateProvCd).isEqualTo(state1);

			ErrorResponseDto viewPremiumRenewalResponseError = HelperCommon.viewRenewalPremiumsError(policyNumber, Response.Status.NOT_FOUND.getStatusCode());
			softly.assertThat(viewPremiumRenewalResponseError.errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED.getCode());
			softly.assertThat(viewPremiumRenewalResponseError.message).contains(ErrorDxpEnum.Errors.POLICY_NOT_RATED.getMessage());

			PolicySummary responsePolicyRenewalPreview = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalPreview.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalPreview.policyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.timedPolicyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalPreview.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalPreview, state, "NOTENROLLED");
			softly.assertThat(responsePolicyRenewalPreview.actualAmt).isEqualTo("0");
			softly.assertThat(responsePolicyRenewalPreview.termPremium).isEqualTo("0");
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.stateProvCd).isEqualTo(state1);

			LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

			PolicySummary responsePolicyOffer = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOffer.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOffer.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOffer.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOffer.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOffer.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOffer.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOffer.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOffer, state, "NOTENROLLED");
			softly.assertThat(responsePolicyOffer.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyOffer.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyOffer.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyOffer.residentialAddress.stateProvCd).isEqualTo(state1);

			PolicyPremiumInfo[] viewPremiumRenewalResponse1 = HelperCommon.viewRenewalPremiums(policyNumber);
			String renewalActualPremium1 = viewPremiumRenewalResponse1[0].actualAmt;
			String renewalTermPremium1 = viewPremiumRenewalResponse1[0].termPremium;

			PolicySummary responsePolicyRenewalOffer = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
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
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.stateProvCd).isEqualTo(state1);

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyOfferExpired = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferExpired.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferExpired.timedPolicyStatus).isEqualTo("expired");
			softly.assertThat(responsePolicyActive.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActive.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferExpired.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOfferExpired, state, "NOTENROLLED");
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.stateProvCd).isEqualTo(state1);

			PolicySummary responsePolicyRenewalOfferExpired = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOfferExpired.policyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOfferExpired.timedPolicyStatus).isEqualTo("proposed");
			softly.assertThat(responsePolicyRenewalOfferExpired.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOfferExpired.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOfferExpired.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOfferExpired, state, "NOTENROLLED");
			softly.assertThat(responsePolicyRenewalOfferExpired.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyRenewalOfferExpired.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyRenewalOfferExpired.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyRenewalOfferExpired.residentialAddress.stateProvCd).isEqualTo(state1);

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(15));
			JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

			PolicySummary responsePolicyOfferLapsed = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferLapsed.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferLapsed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferLapsed.timedPolicyStatus).isEqualTo("expired");
			softly.assertThat(responsePolicyOfferLapsed.effectiveDate).isEqualTo(policyEffectiveDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferLapsed.expirationDate).isEqualTo(policyExpirationDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferLapsed.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferLapsed.renewalCycle).isEqualTo(0);
			eValueStatusCheck(softly, responsePolicyOfferLapsed, state, "NOTENROLLED");
			softly.assertThat(responsePolicyOfferLapsed.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyOfferLapsed.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyOfferLapsed.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyOfferLapsed.residentialAddress.stateProvCd).isEqualTo(state1);

			//BUG PAS-10482 Lapsed policy is not returned by renewal term service after R+15
			PolicySummary responsePolicyRenewalOfferLapsed = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalOfferLapsed.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalOfferLapsed.policyStatus).isEqualTo("customerDeclined");
			softly.assertThat(responsePolicyRenewalOfferLapsed.timedPolicyStatus).isEqualTo("customerDeclined");
			softly.assertThat(responsePolicyRenewalOfferLapsed.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOfferLapsed.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalOfferLapsed.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalOfferLapsed.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalOfferLapsed, state, "NOTENROLLED");
			softly.assertThat(responsePolicyRenewalOfferLapsed.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyRenewalOfferLapsed.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyRenewalOfferLapsed.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyRenewalOfferLapsed.residentialAddress.stateProvCd).isEqualTo(state1);
		});
	}

	protected void pas9716_policySummaryForActiveRenewalBody(String state) {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();
			if ("VA".equals(state)) {
				endorsePolicyAddEvalue();
			}

			//Start PAS-10351
			policy.policyInquiry().start();
			GeneralTab generalTab = new GeneralTab();
			String zipCode1 = generalTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
			String address1 = generalTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
			String city1 = generalTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
			String state1 = generalTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();
			GeneralTab.buttonCancel.click();

			LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

			LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);

			PolicyPremiumInfo[] response = HelperCommon.viewPolicyPremiums(policyNumber);
			String totalPremium = response[0].termPremium;
			String actualPremium = response[0].actualAmt;

			PolicySummary responsePolicyActive = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
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
			//BUG  PAS-14396 PolicySummaryService doesnt return ResidentialAddress when renewal exists
			softly.assertThat(responsePolicyActive.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyActive.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyActive.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyActive.residentialAddress.stateProvCd).isEqualTo(state1);

			ErrorResponseDto viewPremiumRenewalResponseError = HelperCommon.viewRenewalPremiumsError(policyNumber, Response.Status.NOT_FOUND.getStatusCode());
			softly.assertThat(viewPremiumRenewalResponseError.errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED.getCode());
			softly.assertThat(viewPremiumRenewalResponseError.message).contains(ErrorDxpEnum.Errors.POLICY_NOT_RATED.getMessage());

			PolicySummary responsePolicyRenewalPreview = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyRenewalPreview.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyRenewalPreview.policyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.timedPolicyStatus).isEqualTo("dataGather");
			softly.assertThat(responsePolicyRenewalPreview.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyRenewalPreview.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyRenewalPreview.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyRenewalPreview, state, "ACTIVE");
			softly.assertThat(responsePolicyRenewalPreview.actualAmt).isEqualTo("0");
			softly.assertThat(responsePolicyRenewalPreview.termPremium).isEqualTo("0");
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyRenewalPreview.residentialAddress.stateProvCd).isEqualTo(state1);

			LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
			TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

			PolicyPremiumInfo[] viewPremiumRenewalResponseAfterRating = HelperCommon.viewPolicyPremiums(policyNumber);
			String renewalActualPremiumAfterRating = viewPremiumRenewalResponseAfterRating[0].actualAmt;
			String renewalPremiumAfterRating = viewPremiumRenewalResponseAfterRating[0].termPremium;

			PolicySummary responsePolicyOffer = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
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
			softly.assertThat(responsePolicyOffer.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyOffer.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyOffer.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyOffer.residentialAddress.stateProvCd).isEqualTo(state1);

			PolicyPremiumInfo[] viewPremiumRenewalOfferResponse = HelperCommon.viewRenewalPremiums(policyNumber);
			String renewalOfferActualPremium = viewPremiumRenewalOfferResponse[0].actualAmt;
			String renewalOfferTermPremium = viewPremiumRenewalOfferResponse[0].termPremium;

			PolicySummary responsePolicyRenewalOffer = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.OK.getStatusCode());
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
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyRenewalOffer.residentialAddress.stateProvCd).isEqualTo(state1);

			TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);

			PolicySummary responsePolicyOfferExpired = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferExpired.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(responsePolicyOfferExpired.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferExpired.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyOfferExpired.effectiveDate).isEqualTo(policyEffectiveDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferExpired.expirationDate).isEqualTo(policyExpirationDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyOfferExpired.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(responsePolicyOfferExpired.renewalCycle).isEqualTo(1);
			eValueStatusCheck(softly, responsePolicyOfferExpired, state, "ACTIVE");
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.postalCode).isEqualTo(zipCode1);
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.addressLine1).isEqualTo(address1);
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.city).isEqualTo(city1);
			softly.assertThat(responsePolicyOfferExpired.residentialAddress.stateProvCd).isEqualTo(state1);

			PolicySummary responsePolicyPending = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
			softly.assertThat(responsePolicyPending.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
			softly.assertThat(responsePolicyPending.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber + ".");
		});
	}

	protected void pas10484_ViewDriverAssignmentService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create a pended Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
		ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid1 =responseViewDriver.driverList.stream().filter(driver -> driverNameFromAssignment1.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid2 = responseViewDriver.driverList.stream().filter(driver -> driverNameFromAssignment2.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		DriverAssignmentDto[] response = HelperCommon.viewEndorsementAssignments(policyNumber);
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

		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, newVin);
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
		ViewDriversResponse responseViewDriver2 = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid3 = responseViewDriver2.driverList.stream().filter(driver -> driverNameFromAssignment3.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid4 = responseViewDriver2.driverList.stream().filter(driver -> driverNameFromAssignment4.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		assertThat(driverOid1).isEqualTo(driverOid3);
		assertThat(driverOid2).isEqualTo(driverOid4);

		List<TestData> assignmentsUnassigned = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment5 = assignmentsUnassigned.get(0).toString();

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
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

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
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
		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);
		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse2.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> newVehOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle1.vehIdentificationNo).isEqualTo(newVin);
	}

	protected void pas11633_ViewDriverAssignmentAutoAssignService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		//Create a pended Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "4S2CK58W8X4307498";

		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
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

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);
		List<Vehicle> sortedVehicles1 = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles1.sort(Vehicle.PENDING_ENDORSEMENT_COMPARATOR);
		String vehicleOid1 = viewEndorsementVehicleResponse2.vehicleList.get(0).oid;
		String vehicleOid2 = viewEndorsementVehicleResponse2.vehicleList.get(1).oid;

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
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

	protected void pas13994_UpdateDriverAssignmentServiceRule1Body(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData_VA");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();

			policyType.get().createPolicy(testData);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

			//V1 vin from testData
			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");

			//add V2
			String purchaseDate = "2012-02-21";
			String vin2 = "1HGEM21504L055795";
			Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
			assertThat(addVehicle.oid).isNotEmpty();

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");

			String firstName2 = testData.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = testData.getTestDataList("DriverTab").get(1).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 =dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;

			//Update: V2-->D1 ,Check V2-->D1, V1-->D2
			DriverAssignmentDto[] updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid1);
			List<DriverAssignmentDto> v1ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v2ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Update: V1-->D1, Check V1-->D1,D2, V2-->Unn
			DriverAssignmentDto[] updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, driverOid1);
			v1ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);

			//Update V2-->D2 (V1-->D1, V2-->D2)
			DriverAssignmentDto[] updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid2);
			v1ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule2Body(PolicyType policyType) {

		TestData td = getPolicyTD("DataGather", "TestData_VA");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_ThreeVehicles").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();

			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
			String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
			String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");

			//add V4
			String purchaseDate = "2012-02-21";
			String vin4 = "1NXBR32E53Z168489";
			Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin4);
			assertThat(addVehicle.oid).isNotEmpty();

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");

			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle3 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle4 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;
			softly.assertThat(vehicle3.vehIdentificationNo).isEqualTo(vin3);
			String vehicleOid3 = vehicle3.oid;
			softly.assertThat(vehicle4.vehIdentificationNo).isEqualTo(vin4);
			String vehicleOid4 = vehicle4.oid;

			//Update: V4-->D1, Check D1-->V1,V3,V4, D2-->V2
			DriverAssignmentDto[] updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid4, driverOid2);
			List<DriverAssignmentDto> v1ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v2ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v3ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v4ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//TODO-jpukenaite It will be fixed with PAS-14699
			//Update: V4-->D2, Check D1-->V1,V3, D2-->V2, V4
			//			DriverAssignmentDto[] updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber,vehicleOid4,driverOid1);
			//			v1ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			//			v2ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			//			v3ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			//			v4ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());
			//
			//			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			//			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			//			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			//			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule3Body(PolicyType policyType) {

		TestData td = getPolicyTD("DataGather", "TestData_VA");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_FourDrivers").getTestDataList("DriverTab")).resolveLinks();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_TwoVehicles").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTabFourDrivers")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			//Create a pended Endorsement
			AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
			String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

			//add V3
			String purchaseDate = "2012-02-21";
			String vin3 = "1NXBR32E53Z168489";
			Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin3);
			assertThat(addVehicle.oid).isNotEmpty();

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");
			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");
			String firstName3 = td.getTestDataList("DriverTab").get(2).getValue("First Name");
			String lastName3 = td.getTestDataList("DriverTab").get(2).getValue("Last Name");
			String firstName4 = td.getTestDataList("DriverTab").get(3).getValue("First Name");
			String lastName4 = td.getTestDataList("DriverTab").get(3).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driver3 = dResponse.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driver4 = dResponse.driverList.stream().filter(driver -> firstName4.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;
			softly.assertThat(driver3.lastName).isEqualTo(lastName3).isEqualTo(lastName3);
			String driverOid3 = driver3.oid;
			softly.assertThat(driver4.lastName).isEqualTo(lastName4).isEqualTo(lastName4);
			String driverOid4 = driver4.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle3 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;
			softly.assertThat(vehicle3.vehIdentificationNo).isEqualTo(vin3);
			String vehicleOid3 = vehicle3.oid;

			//Update: V3-->D1, Check V1-->D2, V2-->D3, D4, V3-->D1
			DriverAssignmentDto[] updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, driverOid1);
			List<DriverAssignmentDto> v1ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v2ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v3ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Update: V2-->D2, Check V1-->Unn, V2-->D3,D4,D2, V3-->D1
			DriverAssignmentDto[] updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid2);
			v1ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Update: V2-->D1, Check V1-->Unn, V2-->D3,D4,D2,D1 V3-->Unn
			DriverAssignmentDto[] updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid1);
			v1ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);
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
			PolicySummary responsePolicyStub = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStub.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStub.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStub.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStub.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStub.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStub.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStub.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStub.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferRated = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
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

			PolicySummary responsePolicyStubProposed = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposed.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposed.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposed.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposed = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
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

			PolicySummary responsePolicyStubProposedPaid = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposedPaid.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposedPaid.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposedPaid.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposedPaid = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
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

			PolicySummary responsePolicyActivated = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActivated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyActivated.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActivated.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActivated.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyActivated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyActivated.renewalCycle).isEqualTo(1);

			PolicySummary responsePolicyStubExpired = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.NOT_FOUND.getStatusCode());
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
			//BUG PAS-14444 ERROR_DXP_GATEWAY_INTERNAL_ERROR when ViewingPolicySummary for Converted policy
			PolicySummary responsePolicyStub = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStub.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStub.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStub.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStub.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStub.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStub.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStub.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStub.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferRated = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
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

			PolicySummary responsePolicyStubProposed = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposed.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposed.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposed.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposed.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposed.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposed.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposed.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposed = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
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

			PolicySummary responsePolicyStubProposedPaid = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyStubProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyStubProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyStubProposedPaid.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyStubProposedPaid.effectiveDate).isEqualTo(effDate.minusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.expirationDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyStubProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyStubProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyStubProposedPaid.renewalCycle).isEqualTo(0);

			PolicySummary responsePolicyOfferProposedPaid = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyOfferProposedPaid.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyOfferProposedPaid.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyOfferProposedPaid.timedPolicyStatus).isEqualTo("inForcePending");
			softly.assertThat(responsePolicyOfferProposedPaid.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyOfferProposedPaid.expirationDate).isEqualTo(effDate.toLocalDate().plusYears(1).toString());
			softly.assertThat(responsePolicyOfferProposedPaid.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyOfferProposedPaid.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyOfferProposedPaid.renewalCycle).isEqualTo(1);

			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(effDate));
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			PolicySummary responsePolicyActivated = HelperCommon.viewPolicyRenewalSummary(policyNum, "policy", Response.Status.OK.getStatusCode());
			softly.assertThat(responsePolicyActivated.policyNumber).isEqualTo(policyNum);
			softly.assertThat(responsePolicyActivated.policyStatus).isEqualTo("issued");
			softly.assertThat(responsePolicyActivated.timedPolicyStatus).isEqualTo("inForce");
			softly.assertThat(responsePolicyActivated.effectiveDate).isEqualTo(effDate.toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.expirationDate).isEqualTo(effDate.plusYears(1).toLocalDate().toString());
			softly.assertThat(responsePolicyActivated.sourcePolicyNumber).isNotEmpty();
			softly.assertThat(responsePolicyActivated.sourceOfBusiness).isEqualTo("CONV");
			softly.assertThat(responsePolicyActivated.renewalCycle).isEqualTo(1);

			PolicySummary responsePolicyStubExpired = HelperCommon.viewPolicyRenewalSummary(policyNum, "renewal", Response.Status.NOT_FOUND.getStatusCode());
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
		ValidateEndorsementResponse endorsementInfoResp1 = HelperCommon.startEndorsement(policyNumber, endorsementDate, SESSION_ID_1);
		assertThat(endorsementInfoResp1.ruleSets.get(0).errors).isEmpty();

		//Hit start endorsement info service with Id2
		ValidateEndorsementResponse endorsementInfoResp2 = HelperCommon.startEndorsement(policyNumber, endorsementDate, SESSION_ID_2);
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
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Start PAS-479
		//Check premium
		PolicyPremiumInfo[] rateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			softly.assertThat(rateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(rateResponse[0].premiumCode).isEqualTo("GWT");
		});
		Dollar dxpPremium = new Dollar(rateResponse[0].actualAmt);

		pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(new Dollar(PremiumAndCoveragesTab.getActualPremium())).isEqualTo(dxpPremium);
		PremiumAndCoveragesTab.buttonCancel.click();

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin2 = "1HGFA16526L081415";
		Vehicle response2 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
		assertThat(response2.oid).isNotEmpty();
		String newVehicleOid = response2.oid;

		//View vehicles status
		ViewVehicleResponse response3 = HelperCommon.viewEndorsementVehicles(policyNumber);

		if (response3.vehicleList.get(0).vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3.vehicleList.get(0).vehicleStatus).isEqualTo("active");
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3.vehicleList.get(1).vehicleStatus).isEqualTo("pending");
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3.vehicleList.get(0).vehicleStatus).isEqualTo("pending");
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3.vehicleList.get(1).vehicleStatus).isEqualTo("active");
			});
		}

		SearchPage.openPolicy(policyNumber);

		//Update Vehicle with proper Usage and Registered Owner
		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		//Check premium after new vehicle was added
		PolicyPremiumInfo[] rateResponse2 = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		Dollar dxpPremium2 = new Dollar(rateResponse2[0].actualAmt);

		pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

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
		ViewVehicleResponse response4 = HelperCommon.viewPolicyVehicles(policyNumber);

		if (response4.vehicleList.get(0).vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response4.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response4.vehicleList.get(0).vehicleStatus).isEqualTo("active");
				softly.assertThat(response4.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response4.vehicleList.get(1).vehicleStatus).isEqualTo("active");
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response4.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response4.vehicleList.get(0).vehicleStatus).isEqualTo("active");
				softly.assertThat(response4.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response4.vehicleList.get(1).vehicleStatus).isEqualTo("active");
			});
		}
	}

	protected void pas10449_ViewVehicleServiceCheckOrderOfVehicle(PolicyType policyType, String state) {

		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Get VIN's
		String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
		String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");
		String vin4 = td.getTestDataList("VehicleTab").get(3).getValue("VIN");

		//hit view vehicle service to get Vehicle order
		ViewVehicleResponse viewVehicleResponse1 = HelperCommon.viewPolicyVehicles(policyNumber);
		assertThat(viewVehicleResponse1.canAddVehicle).isEqualTo(true);
		List<Vehicle> originalOrderingFromResponse = ImmutableList.copyOf(viewVehicleResponse1.vehicleList);
		List<Vehicle> sortedVehicles = viewVehicleResponse1.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);

		assertSoftly(softly -> {
			assertThat(originalOrderingFromResponse).containsAll(sortedVehicles);

			Vehicle vehicle1 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle2 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle3 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicle4 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Conversion");
		});

		// Perform endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);

		//Add new vehicle to have pending vehicle
		String purchaseDate = "2013-02-22";
		String vin5 = "1HGFA16526L081415";
		Vehicle addVehicleResponse = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin5);
		assertThat(addVehicleResponse.oid).isNotEmpty();

		String vin6 = "2GTEC19K8S1525936";
		Vehicle addVehicleResponse2 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin6);
		assertThat(addVehicleResponse2.oid).isNotEmpty();

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);

		List<Vehicle> originalOrderingFromResponse2 = ImmutableList.copyOf(viewEndorsementVehicleResponse2.vehicleList);
		List<Vehicle> sortedVehicles1 = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles1.sort(Vehicle.PENDING_ENDORSEMENT_COMPARATOR);
		assertSoftly(softly -> {
			softly.assertThat(originalOrderingFromResponse2).isEqualTo(sortedVehicles1);

			Vehicle vehicle6 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin6.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle6).isNotNull();
			softly.assertThat(vehicle6.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehicle6.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle5 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin5.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle5).isNotNull();
			softly.assertThat(vehicle5.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehicle5.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle3 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicle4 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Conversion");
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
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//Get OID from View vehicle
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		//send request to update vehicle service
		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleRequest.vehicleOwnership.ownership = "OWN";
		updateVehicleRequest.usage = "Pleasure";
		updateVehicleRequest.salvaged = false;
		updateVehicleRequest.garagingDifferent = false;
		updateVehicleRequest.antiTheft = "STD";
		updateVehicleRequest.registeredOwner = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("Pleasure");
			softly.assertThat(updateVehicleResponse.salvaged).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.garagingDifferent).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.antiTheft).isEqualTo("STD");
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);

			//verify updated information with viewEndorsementVehicles
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).modelYear).isEqualTo(updateVehicleResponse.modelYear);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).manufacturer).isEqualTo(updateVehicleResponse.manufacturer);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).series).isEqualTo(updateVehicleResponse.series);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).model).isEqualTo(updateVehicleResponse.model);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).bodyStyle).isEqualTo(updateVehicleResponse.bodyStyle);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehIdentificationNo).isEqualTo(updateVehicleResponse.vehIdentificationNo);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).usage).isEqualTo("Pleasure");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).salvaged).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingDifferent).isEqualTo(false);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).antiTheft).isEqualTo("STD");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).registeredOwner).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.postalCode).isEqualTo(zipCodeDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.addressLine1).isEqualTo(addressDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.stateProvCd).isEqualTo(stateDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.city).isEqualTo(cityDefault);
		});

		//PAS-7145 Start
		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";

		//send request to update vehicle service
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.vehicleOwnership = new VehicleOwnership();
		updateGaragingAddressVehicleRequest.vehicleOwnership.ownership = "OWN";
		updateGaragingAddressVehicleRequest.usage = "Pleasure";
		updateGaragingAddressVehicleRequest.salvaged = false;
		updateGaragingAddressVehicleRequest.garagingDifferent = false;
		updateGaragingAddressVehicleRequest.antiTheft = "STD";
		updateGaragingAddressVehicleRequest.registeredOwner = false;
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddress = new Address();
		updateGaragingAddressVehicleRequest.garagingAddress.postalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.city = cityGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.stateProvCd = stateGarage;

		Vehicle updateVehicleResponseGaragingAddress = HelperCommon.updateVehicle(policyNumber, oid, updateGaragingAddressVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponseGaragingAddress.vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(updateVehicleResponseGaragingAddress.usage).isEqualTo("Pleasure");
			softly.assertThat(updateVehicleResponseGaragingAddress.salvaged).isEqualTo(false);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingDifferent).isEqualTo(true);
			softly.assertThat(updateVehicleResponseGaragingAddress.antiTheft).isEqualTo("STD");
			softly.assertThat(updateVehicleResponseGaragingAddress.registeredOwner).isEqualTo(false);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.addressLine1).isEqualTo(addressGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.city).isEqualTo(cityGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.stateProvCd).isEqualTo(stateGarage);

			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).modelYear).isEqualTo(updateVehicleResponse.modelYear);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).manufacturer).isEqualTo(updateVehicleResponse.manufacturer);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).series).isEqualTo(updateVehicleResponse.series);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).model).isEqualTo(updateVehicleResponse.model);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).bodyStyle).isEqualTo(updateVehicleResponse.bodyStyle);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehIdentificationNo).isEqualTo(updateVehicleResponse.vehIdentificationNo);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).usage).isEqualTo("Pleasure");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).salvaged).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingDifferent).isEqualTo(true);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).antiTheft).isEqualTo("STD");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).registeredOwner).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.postalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.addressLine1).isEqualTo(addressGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.stateProvCd).isEqualTo(stateGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.city).isEqualTo(cityGarage);
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
		HelperCommon.endorsementBind(policyNumber, authorizedBy, Response.Status.OK.getStatusCode());
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
			AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, null);
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

			rateEndorsement(softly, policyNumber);

			//Create pended endorsement
			pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");
			Tab.buttonBack.click();

			//issue through service
			HelperCommon.endorsementBind(policyNumber, authorizedBy, Response.Status.OK.getStatusCode());
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

		PolicyPremiumInfo[] response = HelperCommon.viewPolicyPremiums(policyNumber);
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
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin = "4S2CK58W8X4307498";
		VehicleTab vehicleTab = new VehicleTab();
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(USAGE.getLabel(), ComboBox.class).setValue("Pleasure");
		vehicleTab.saveAndExit();

		ErrorResponseDto viewEndorsementPremiumsErrorResponse = HelperCommon.viewEndorsementPremiumsError(policyNumber, Response.Status.NOT_FOUND.getStatusCode());
		assertSoftly(softly -> {
			assertThat(viewEndorsementPremiumsErrorResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED.getCode());
			assertThat(viewEndorsementPremiumsErrorResponse.message).contains(ErrorDxpEnum.Errors.POLICY_NOT_RATED.getMessage());
		});

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(getPremiumAndCoverageTab());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(getCalculatePremium()).click();
		String actualPremium = PremiumAndCoveragesTab.totalActualPremium.getValue();
		String totalPremium = PremiumAndCoveragesTab.totalTermPremium.getValue();

		PolicyPremiumInfo[] response = HelperCommon.viewEndorsementPremiums(policyNumber);
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
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
		assertSoftly(softly -> {

			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh1);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpense);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).customerDisplayed).isEqualTo(false);

		});

		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "1,000");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "750");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), "900");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), "50");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "1,500");

		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		SearchPage.openPolicy(policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh2 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible1);
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible1);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible1);
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible1);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh2);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageEndorsementResponse);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov1);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageEndorsementResponse);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpense1);
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay)).isEqualTo(transportationExpense1);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponse);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay.replace("$", "")).isEqualTo(towingAndLabor1);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageEndorsementResponse);

			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment1);
			softly.assertThat(coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);
		});
	}

	protected void pas13353_LoanLeaseCoverage(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();

		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String loanLeaseCov1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		String fullSafetyGlassVeh1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		Dollar transportationExpense = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
		assertSoftly(softly -> {
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh1);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov1);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpense);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).canChangeCoverage).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).customerDisplayed).isEqualTo(false);

		});

		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.vehicleOwnership.ownership = "OWN";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh2 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		Dollar transportationExpense1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewEndorsementCoverages(policyNumber);
		assertSoftly(softly -> {
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			assertThat(new Dollar(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible1);
			assertThat(new Dollar(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible1);
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompColl(coverageResponse1);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible1);
			softly.assertThat(new Dollar(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible1);
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompColl(coverageResponse1);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh2);
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse1);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo("No Coverage");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(false);
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(3).canChangeCoverage).isEqualTo(false);

			assertCoverageLimitLoan(coverageResponse1);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpense1);
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).contains(transportationExpense1.toString().replace(".00", ""));
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse1);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor1);
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse1);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment1);
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);
			assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(6).canChangeCoverage).isEqualTo(true);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(7).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageResponse1.vehicleLevelCoverages.get(0).coverages.get(8).customerDisplayed).isEqualTo(false);

		});
	}

	protected void pas11741_ViewManageVehicleLevelCoveragesForAZ(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()).replace("(+$0.00)","").trim());
		Dollar collisionDeductible = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel()).replace("(+$0.00)", "").trim());
		String fullSafetyGlassVeh1 = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCov = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.VEHICLE_LOAN_LEASE_PROTECTION.getLabel()).replace("(+$0.00)", "").trim();
		String transportationExpense = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT.getLabel().replace("(+$0.00)", "").trim());
		String towingAndLabor = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel()).replace("(Included)", "").replace("(+$0.00)", "").replace("$", "").trim();
		Dollar excessElectronicEquipment = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE.getLabel()));

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Comprehensive Deductible");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimit).isEqualTo("50");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimitDisplay).isEqualTo("$50");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimit).isEqualTo("100");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimit).isEqualTo("250");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimit).isEqualTo("500");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimit).isEqualTo("750");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(5).coverageLimit).isEqualTo("1000");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(5).coverageLimitDisplay).isEqualTo("$1,000");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(0).coverageLimit).isEqualTo("100");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(0).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(1).coverageLimit).isEqualTo("250");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(2).coverageLimit).isEqualTo("500");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(2).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(3).coverageLimit).isEqualTo("750");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(3).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(4).coverageLimit).isEqualTo("1000");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).availableLimits.get(4).coverageLimitDisplay).isEqualTo("$1,000");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("false");
			//softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh1);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(0).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(1).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Vehicle Loan/Lease Protection");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(1).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Rental Reimbursement");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit).isEqualTo("0/0");
			//softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("per day/maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$30/$900");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(2).coverageLimit).isEqualTo("40/1200");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(2).coverageLimitDisplay).isEqualTo("$40/$1,200");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(3).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(3).coverageLimitDisplay).isEqualTo("$50/$1,500");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(1).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50/$300");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Special Equipment Coverage");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).customerDisplayed).isEqualTo(false);

		});
	}

	protected void pas10352_ManageVehicleCoverageUpdateCoverage(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		String purchaseDate = "2012-02-21";
		String vin = "SHHFK7H41JU201444";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();
		String newVehicleOid = addVehicle.oid;

		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCodeOwnership = "23703";
		String addressLine1Ownership = "4112 FORREST HILLS DR";
		String addressLine2Ownership = "Apt. 202";
		String cityOwnership = "PORTSMOUTH";
		String stateOwnership = "VA";
		String otherNameOwnership = "other name";
		String secondNameOwnership = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.city = cityOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = stateOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCodeOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherNameOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondNameOwnership;
		VehicleUpdateResponseDto ownershipUpdateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse.vehicleOwnership.ownership).isEqualTo("LSD");

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)", "(+$39.00)");
		Dollar collisionDeductible1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)", "(+$185.00)");
		String fullSafetyGlassVeh1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)", " (+$21.00)");
		String towingAndLabor1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		premiumAndCoveragesTab.saveAndExit();

		String coverageCd = "COMPDED";
		String availableLimits = "100";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd, availableLimits);
		assertSoftly(softly -> {

					coverageXproperties(softly, 0, coverageResponse, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

					assertCoverageLimitForCompCollLoanLease(coverageResponse);

					coverageXproperties(softly, 1, coverageResponse, "COLLDED", "Collision Deductible", collisionDeductible1.toPlaingString(), collisionDeductible1.toPlaingString(), "Deductible", true, true);

					assertCoverageLimitForCompCollLoanLease(coverageResponse);

					coverageXproperties(softly, 2, coverageResponse, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);

					assertCoverageLimitFullGlassCov(coverageResponse);

					coverageXproperties(softly, 3, coverageResponse, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);

					assertCoverageLimitLoan(coverageResponse);

					coverageXproperties(softly, 4, coverageResponse, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

					assertCoverageLimitTransportationExpense(coverageResponse);

					coverageXproperties(softly, 5, coverageResponse, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

					assertCoverageLimitTowingLabor(coverageResponse);
				}
		);

		String coverageCd1 = "COLLDED";
		String availableLimits1 = "100";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd1, availableLimits1);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse1, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse1, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse1, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd2 = "GLASS";
		String availableLimits2 = "Yes";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd2, availableLimits2);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse2, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse2, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse2, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse2, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse2, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse2, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd3, availableLimits3);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse3, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse3, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse3, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse3, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse3, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse3, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd4, availableLimits4);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse4, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse4, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse4, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse4, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse4, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse4, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd5, availableLimits5);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse5, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse5, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse5, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse5, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse5, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse5, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdRemove, availableLimitsRemove);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse14, "COMPDED", "Other Than Collision", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse14, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);

			coverageXproperties(softly, 2, coverageResponse14, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);

			coverageXproperties(softly, 3, coverageResponse14, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, false);

			//coverageXproperties(softly, 4, coverageResponse14, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, false);

			coverageXproperties(softly, 5, coverageResponse14, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChange, availableLimitsChange);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse6, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse6, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse6, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse6, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, false);

			//	coverageXproperties(softly, 4, coverageResponse6, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse6, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeColl, availableLimitsChangeColl);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse7, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse7, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse7, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse7, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse7, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse7, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlass = "GLASS";
		String availableLimitsChangeGlass = "Yes";

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeGlass, availableLimitsChangeGlass);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse8, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse8, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse8, "GLASS", "Full Safety Glass", "true", availableLimitsChangeGlass, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse8, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse8, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse8, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "No Coverage";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse9, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse9, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse9, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse9, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);

			//	coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse9, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse10, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse10, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse10, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse10, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse10, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "900";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTransport, availableLimitsChangeTransport);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse11, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse11, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse11, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse11, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse11, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse11, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTowing, availableLimitsChangeTowing);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse12, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse12, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse12, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse12, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			//	coverageXproperties(softly, 4, coverageResponse12, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse12, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse13, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse13, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse13, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse13, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse13, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse13, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowingNoCov, "No Coverage", "Per Disablement/Maximum", true, true);

		});

		endorsementRateAndBind(policyNumber);

		secondEndorsementIssueCheck();
	}

	private void coverageXproperties(SoftAssertions softly, int coverageXnumber, PolicyCoverageInfo coverageResponse, String coverageCd, String coverageDesc, String availableLimits, String coverageLimitDisplay, String coverageType, boolean customerDisplay, boolean canChangeCoverage) {
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageCd).isEqualTo(coverageCd);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageDescription).isEqualTo(coverageDesc);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageLimit.replace(".00", "")).isEqualTo(availableLimits.replace(".00", ""));
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageLimitDisplay).contains(coverageLimitDisplay.toString().replace(".00", ""));
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageType).isEqualTo(coverageType);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).customerDisplayed).isEqualTo(customerDisplay);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).canChangeCoverage).isEqualTo(canChangeCoverage);
	}

	private Coverage getCoverageX(PolicyCoverageInfo coverageResponse, int number) {
		return coverageResponse.vehicleLevelCoverages.get(0).coverages.get(number);
	}

	private void assertCoverageLimitTowingLabor(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(1).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50/$300");
		});
	}

	private void assertCoverageLimitTransportationExpense(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(0).coverageLimit).isEqualTo("600");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(1).coverageLimit).isEqualTo("900");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$900");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(2).coverageLimit).isEqualTo("1200");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(2).coverageLimitDisplay).isEqualTo("$1,200");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(3).coverageLimit).isEqualTo("1500");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.get(3).coverageLimitDisplay).isEqualTo("$1,500");
		});
	}

	private void assertCoverageLimitLoan(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(1).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");
		});
	}

	private void assertCoverageLimitForCompColl(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimit).isEqualTo("-1");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimit).isEqualTo("100");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimit).isEqualTo("250");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimit).isEqualTo("500");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimit).isEqualTo("750");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(5).coverageLimit).isEqualTo("1000");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(5).coverageLimitDisplay).isEqualTo("$1,000");
		});
	}

	private void assertCoverageLimitForCompCollLoanLease(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimit).isEqualTo("100");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimit).isEqualTo("250");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimit).isEqualTo("500");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimit).isEqualTo("750");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimit).isEqualTo("1000");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimitDisplay).isEqualTo("$1,000");
		});
	}

	private void assertCoverageLimitFullGlassCov(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(0).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(1).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");
		});
	}

	protected void pas11741_ViewVehicleLevelCoverages(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		SearchPage.openPolicy(policyNumber);

		// view vehicle coverage to get OID
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		//Perform Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//add  vehicle1
		String purchaseDate = "2012-02-21";
		String vin = "SHHFK7H41JU201444";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();
		String newVehicleOid = addVehicle.oid;

		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.vehicleOwnership.ownership = "LSD";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");

		SearchPage.openPolicy(policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//vehicle1
		Dollar comprehensiveDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponseV = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, oid);
		assertSoftly(softly -> {
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(new Dollar(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(2).canChangeCoverage).isEqualTo(true);
			assertCoverageLimitFullGlassCov(coverageResponseV);

			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponseV);

			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpense);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponseV);

			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponseV);

			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageResponseV.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);

		});

		// Vehicle2
		Dollar comprehensiveDeductiblePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCovPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductiblePendingV);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductiblePendingV);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductiblePendingV);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductiblePendingV);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVehPendingV);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCovPendingV);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpensePendingV);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).contains(transportationExpensePendingV.toString().replace(".00", ""));
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay).isEqualTo(towingAndLaborPendingV);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipmentPendingV);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).customerDisplayed).isEqualTo(false);
		});

		//Change coverages v1
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "1,000");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "750");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), "900");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), "50");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "1,500");
		// change cov 2
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "500");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "750");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), "900");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), "50");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "1,500");

		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductiblePendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCovPendingV1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponseV1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid);
		assertSoftly(softly -> {
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductiblePendingV1);
			softly.assertThat(new Dollar(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductiblePendingV1);
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponseV1);

			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductiblePendingV1);
			softly.assertThat(new Dollar(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductiblePendingV1);
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponseV1);

			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVehPendingV1);
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageEndorsementResponseV1);

			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCovPendingV1);
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);

			assertCoverageLimitLoan(coverageEndorsementResponseV1);

			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpensePendingV1);
			softly.assertThat(new Dollar(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay)).isEqualTo(transportationExpensePendingV1);
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponseV1);

			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay.replace("$", "")).isEqualTo(towingAndLaborPendingV1);
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageEndorsementResponseV1);

			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipmentPendingV1);
			softly.assertThat(coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);
		});

		//vehicle2
		Dollar comprehensiveDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCovPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponsePendingV2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		assertSoftly(softly -> {
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductiblePendingV2);
			softly.assertThat(new Dollar(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductiblePendingV2);
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponsePendingV2);

			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductiblePendingV2);
			softly.assertThat(new Dollar(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductiblePendingV2);
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponsePendingV2);

			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVehPendingV2);
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageEndorsementResponsePendingV2);

			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCovPendingV2);
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);

			assertCoverageLimitLoan(coverageEndorsementResponsePendingV2);

			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpensePendingV2);
			softly.assertThat(new Dollar(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay)).isEqualTo(transportationExpensePendingV2);
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponsePendingV2);

			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay.replace("$", "")).isEqualTo(towingAndLaborPendingV2);
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponsePendingV2);

			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipmentPendingV2);
			softly.assertThat(coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);

		});
	}

	protected void pas7147_VehicleUpdateBusinessBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
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

		ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber, 422);
		assertSoftly(softly -> {
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.REGISTERED_OWNERS.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.REGISTERED_OWNERS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("vehOwnerInd");
			softly.assertThat(rateResponse.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.USAGE_IS_BUSINESS.getCode());
			softly.assertThat(rateResponse.errors.get(1).message).contains(ErrorDxpEnum.Errors.USAGE_IS_BUSINESS.getMessage());
			softly.assertThat(rateResponse.errors.get(1).field).isEqualTo("vehicleUsageCd");
		});

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "PAS-7147", 422);
		assertSoftly(softly -> {
			softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getCode());
			softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getMessage());
		});
	}

	protected void pas7147_VehicleUpdateRegisteredOwnerBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.registeredOwner = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);
			assertThat(((VehicleUpdateResponseDto) updateVehicleResponse).ruleSets.get(0).errors.get(0)).contains("Registered Owners");
		});
		//TODO jpukenaite-issue or not, "Usage is Business" should not be displaying
		//Check premium after new vehicle was added
		//		HashMap<String, String> rateResponse = HelperCommon.endorsementRateError(policyNumber, 422);
		//		assertThat(rateResponse.entrySet().toString()).contains(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "PAS-7147", 422);
		assertSoftly(softly -> {
			softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getCode());
			softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getMessage());
		});
	}

	protected void pas488_VehicleDeleteBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//run get vehicle information service.
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;
		String vin = viewVehicleResponse.vehicleList.get(0).vehIdentificationNo;

		//run delete vehicle service
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oid);
		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin);
			assertThat(deleteVehicleResponse.ruleSets).isEqualTo(null);
		});

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertSoftly(softly -> {
			assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).oid).isEqualTo(oid);
			assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin);
			assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehicleStatus).isEqualTo("pendingRemoval");
		});

		//Rate policy
		PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
		});

		//Bind policy
		HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas502_CheckDuplicateVinAddVehicleService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		TestData vehicleData = new TestDataManager().policy.get(policyType);

		//Create a pended Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//add vehicle
		String purchaseDate1 = "2012-02-21";
		String vin1 = getStateTestData(vehicleData, "DataGather", "TestData").getTestDataList("VehicleTab").get(0).getValue("VIN");

		ErrorResponseDto errorResponse = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate1, vin1);
		assertSoftly(softly -> {
			softly.assertThat(errorResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getCode());
			softly.assertThat(errorResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getMessage());
			softly.assertThat(errorResponse.errors.get(0).field).isEqualTo("vehIdentificationNo");
		});
		String purchaseDate2 = "2015-02-11";
		String vin2 = "9BWFL61J244023215";

		//add vehicle
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate2, vin2);
		assertThat(responseAddVehicle.oid).isNotEmpty();

		//try add the same vehicle one more time
		ErrorResponseDto errorResponse2 = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate2, vin2);
		assertSoftly(softly -> {
			softly.assertThat(errorResponse2.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponse2.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponse2.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getCode());
			softly.assertThat(errorResponse2.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getMessage());
			softly.assertThat(errorResponse2.errors.get(0).field).isEqualTo("vehIdentificationNo");
		});

		//Start PAS-11005
		String purchaseDate3 = "2015-02-11";
		String vin3 = "ZFFCW56A830133118";

		//try add to expensive vehicle
		ErrorResponseDto errorResponse3 = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate3, vin3);
		assertSoftly(softly -> {
			softly.assertThat(errorResponse3.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponse3.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponse3.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.TOO_EXPENSIVE_VEHICLE.getCode());
			softly.assertThat(errorResponse3.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.TOO_EXPENSIVE_VEHICLE.getMessage());
			softly.assertThat(errorResponse3.errors.get(0).field).isEqualTo("vehIdentificationNo");
		});
	}

	protected void pas12246_ViewVehiclePendingRemovalService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Get VIN's
		String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
		String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");
		String vin4 = td.getTestDataList("VehicleTab").get(3).getValue("VIN");

		//run get vehicle information service.
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		List<Vehicle> originalOrderingFromResponse = ImmutableList.copyOf(viewVehicleResponse.vehicleList);
		List<Vehicle> sortedVehicles = viewVehicleResponse.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		assertSoftly(softly -> {

			assertThat(originalOrderingFromResponse).containsAll(sortedVehicles);

			Vehicle vehicle1 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle2 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle3 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicle4 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Conversion");
		});

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vinNew = "3FAFP31341R200709";
		VehicleTab vehicleTab = new VehicleTab();
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vinNew);
		assertThat(addVehicle.oid).isNotEmpty();

		//run delete vehicle service
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oid);
		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin1);
			assertThat(deleteVehicleResponse.ruleSets).isEqualTo(null);
		});

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);

		List<Vehicle> originalOrderingFromResponse2 = ImmutableList.copyOf(viewEndorsementVehicleResponse2.vehicleList);
		List<Vehicle> sortedVehicles1 = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles1.sort(Vehicle.PENDING_ENDORSEMENT_COMPARATOR);
		assertSoftly(softly -> {
			softly.assertThat(originalOrderingFromResponse2).isEqualTo(sortedVehicles1);

			Vehicle vehiclePendingRemoval = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehiclePendingRemoval).isNotNull();
			softly.assertThat(vehiclePendingRemoval.oid).isEqualTo(oid);
			softly.assertThat(vehiclePendingRemoval.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(vehiclePendingRemoval.vehTypeCd).isEqualTo("PPA");
			softly.assertThat(vehiclePendingRemoval.vehIdentificationNo).isEqualTo(vin1);

			Vehicle vehiclePending = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vinNew.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehiclePending.vehIdentificationNo).isEqualTo(vinNew);
			softly.assertThat(vehiclePending.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehiclePending.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicleActive = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicleActive).isNotNull();
			softly.assertThat(vehicleActive.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicleActive.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicleActive2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicleActive2).isNotNull();
			softly.assertThat(vehicleActive2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicleActive2.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicleActive3 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicleActive3).isNotNull();
			softly.assertThat(vehicleActive3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicleActive3.vehTypeCd).isEqualTo("Conversion");
		});

	}

	protected void pas12407_bigDataService(SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsmentVehiclesMetaData(policyNumber, oid);
		AttributeMetadata metaDataFieldResponseVehTypeCd = getAttributeMetadata(metaDataResponse, "vehTypeCd", true, true, true, null);
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("PPA")).isEqualTo("Private Passenger Auto");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Conversion")).isEqualTo("Conversion Van");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Motor")).isEqualTo("Motor Home");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Trailer")).isEqualTo("Trailer");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("AC")).isEqualTo("Limited Production/Antique");

		AttributeMetadata metaDataFieldResponseUsage = getAttributeMetadata(metaDataResponse, "usage", true, true, true, null);
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Pleasure")).isEqualTo("Pleasure");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("WorkCommute")).isEqualTo("Commute");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Business")).isEqualTo("Business");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Artisan")).isEqualTo("Artisan");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Farm")).isEqualTo("Farm");

		getAttributeMetadata(metaDataResponse, "vehIdentificationNo", true, true, false, "20");
		getAttributeMetadata(metaDataResponse, "modelYear", false, true, true, "5");
		getAttributeMetadata(metaDataResponse, "manufacturer", false, true, true, null);
		getAttributeMetadata(metaDataResponse, "model", false, true, true, null);
		getAttributeMetadata(metaDataResponse, "series", false, true, false, null);

		AttributeMetadata metaDataFieldResponseBodyStyle = getAttributeMetadata(metaDataResponse, "bodyStyle", false, true, false, null);
		softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("")).isEqualTo("");
		softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("SPORT VAN")).isEqualTo("SPORT VAN");
		softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("OTHER")).isEqualTo("OTHER");

		getAttributeMetadata(metaDataResponse, "salvaged", true, true, false, null);

		AttributeMetadata metaDataFieldResponseAntiTheft = getAttributeMetadata(metaDataResponse, "antiTheft", true, true, false, null);
		softly.assertThat(metaDataFieldResponseAntiTheft.valueRange.get("")).isEqualTo("");
		softly.assertThat(metaDataFieldResponseAntiTheft.valueRange.get("NONE")).isEqualTo("None");
		softly.assertThat(metaDataFieldResponseAntiTheft.valueRange.get("STD")).isEqualTo("Vehicle Recovery Device");

		getAttributeMetadata(metaDataResponse, "vehicleStatus", true, false, false, null);
		getAttributeMetadata(metaDataResponse, "registeredOwner", true, false, false, null);
		//Defect PAS-13252: "Is Garaging different from Residential?" radio button doesn't exist
		//getAttributeMetadata(metaDataResponse, "garagingAddress.different", true, true, true, null);
		getAttributeMetadata(metaDataResponse, "garagingAddress.postalCode", true, false, true, "10");
		getAttributeMetadata(metaDataResponse, "garagingAddress.addressLine1", true, false, true, "40");
		getAttributeMetadata(metaDataResponse, "garagingAddress.addressLine2", true, false, false, "40");
		getAttributeMetadata(metaDataResponse, "garagingAddress.city", true, false, true, "30");
		getAttributeMetadata(metaDataResponse, "garagingAddress.stateProvCd", true, false, true, null);

		AttributeMetadata metaDataFieldResponseOwnership = getAttributeMetadata(metaDataResponse, "vehicleOwnership.ownership", true, true, false, null);
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("")).isEqualTo("");
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("OWN")).isEqualTo("Owned");
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("FNC")).isEqualTo("Financed");
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("LSD")).isEqualTo("Leased");

		getAttributeMetadata(metaDataResponse, "vehicleOwnership.name", false, false, false, "100");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.secondName", false, false, false, "100");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.postalCode", false, false, false, "10");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine1", false, false, false, "40");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine2", false, false, false, "40");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.city", false, false, false, "30");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.stateProvCd", false, false, false, null);

		//edit pending endorsement
		vehicleTab.getAssetList().getAsset(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).setValue("Yes");
		vehicleTab.getAssetList().getAsset(ZIP_CODE).setValue("23703");
		vehicleTab.getAssetList().getAsset(ADDRESS_LINE_1).setValue("4112 FORREST HILLS DR");
		vehicleTab.getAssetList().getAsset(CITY).setValue("PORTSMOUTH");
		vehicleTab.getAssetList().getAsset(STATE).setValue("VA");
		vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE).setValue("Leased");
		vehicleTab.getOwnershipAssetList().getAsset(Ownership.FIRST_NAME).setValue("GMAC");
		vehicleTab.saveAndExit();

		AttributeMetadata[] metaDataResponse2 = HelperCommon.viewEndorsmentVehiclesMetaData(policyNumber, oid);
		//Defect PAS-13252: "Is Garaging different from Residential?" radio button doesn't exist
		//getAttributeMetadata(metaDataResponse, "garagingAddress.different", true, true, true, null);
		getAttributeMetadata(metaDataResponse2, "garagingAddress.postalCode", true, true, true, "10");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.addressLine1", true, true, true, "40");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.addressLine2", true, true, false, "40");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.city", true, true, true, "30");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.stateProvCd", true, true, true, null);

		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.name", true, true, false, "100");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.secondName", false, true, false, "100");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.postalCode", true, true, false, "10");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.addressLine1", true, true, false, "40");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.addressLine2", true, true, false, "40");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.city", true, true, false, "30");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.stateProvCd", true, true, false, null);
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
			switch (state) {
				case "AZ":
					policyNumber = "AZSS926232043";
					break;
				case "CO":
					policyNumber = "COSS926232041";
					break;
				case "CT":
					policyNumber = "CTSS926232046";
					break;
				case "DC":
					policyNumber = "DCSS926232042";
					break;
				case "MD":
					policyNumber = "MDSS926232047";
					break;
				case "NJ":
					policyNumber = "NJSS926232048";
					break;
				case "NV":
					policyNumber = "NVSS926232045";
					break;
				case "WV":
					policyNumber = "WVSS926232044";
					break;
				case "WY":
					policyNumber = "WYSS926232049";
					break;
				case "DE":
					policyNumber = "DESS926232053";
					break;
				case "ID":
					policyNumber = "IDSS926232058";
					break;
				case "IN":
					policyNumber = "INSS926232057";
					break;
				case "KS":
					policyNumber = "KSSS926232056";
					break;
				case "KY":
					policyNumber = "KYSS926232055";
					break;
				case "MT":
					policyNumber = "MTSS926232059";
					break;
				case "NY":
					policyNumber = "NYSS926232052";
					break;
				case "OK":
					policyNumber = "OKSS926232050";
					break;
				case "SD":
					policyNumber = "SDSS926232054";
					break;
				case "VA":
					policyNumber = "VASS926232051";
					break;
				case "OH":
					policyNumber = "OHSS926232062";
					break;
				case "OR":
					policyNumber = "ORSS926232061";
					break;
				case "PA":
					policyNumber = "PASS926232060";
					break;
				default:
			}
		}
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		//View Policy
		PolicySummary responseViewPolicy = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
		softly.assertThat(responseViewPolicy.policyNumber).isEqualTo(policyNumber);
		softly.assertThat(responseViewPolicy.policyStatus).isEqualTo("issued");

		//View Renewal which doesn't exist
		PolicySummary responseViewRenewal = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
		softly.assertThat(responseViewRenewal.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
		softly.assertThat(responseViewRenewal.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber + ".");

		//get all vehicles
		ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String originalVehicle = responseViewVehicles.vehicleList.get(0).oid;

		//get all drivers
		ViewDriversResponse responseViewDrivers = HelperCommon.viewPolicyDrivers(policyNumber);
		String originalDriver = responseViewDrivers.driverList.get(0).oid;

		//get all coverages
		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");

		//get all discounts
		DiscountSummary policyDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, "policy", 200);
		assertThat(policyDiscountsResponse.policyDiscounts.get(1).discountCd).isNotNull();

		//Check endorsement is allowed
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse responseValidateEndorse = HelperCommon.startEndorsement(policyNumber, endorsementDate);
		softly.assertThat(responseValidateEndorse.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");

		//Lock the policy
/*		PolicyLockUnlockDto responseLock = HelperCommon.executePolicyLockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		assertThat(responseLock.policyNumber).isEqualTo(policyNumber);
		assertThat(responseLock.status).isEqualTo("Locked");*/

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Validate VIN
		AAAVehicleVinInfoRestResponseWrapper vinValidateResponse = HelperCommon.executeVinInfo(policyNumber, vin, endorsementDate);
		softly.assertThat(vinValidateResponse.vehicles.get(0).vin).isEqualTo(vin);

		//Add new vehicle
		//BUG PAS-14688, PAS-14689, PAS-14690, PAS-14691 - Add Vehicle for DC, KS, NY, OR
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		SearchPage.openPolicy(policyNumber);

		//Update Vehicle with proper Usage and Registered Owner
		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddress = new Address();
		updateGaragingAddressVehicleRequest.garagingAddress.postalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.city = cityGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.stateProvCd = stateGarage;
		Vehicle updateVehicleGaragingAddressResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingDifferent).isEqualTo(true);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.addressLine1).isEqualTo(addressGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.city).isEqualTo(cityGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.stateProvCd).isEqualTo(stateGarage);

		//BUG PAS-14393 When sending GaragingDifferent = False, garaging address is not updated
		VehicleUpdateDto updateGaragingAddressVehicleRequest2 = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest2.garagingDifferent = false;
		Vehicle updateVehicleGaragingAddressResponse2 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest2);
		softly.assertThat(updateVehicleGaragingAddressResponse2.garagingDifferent).isEqualTo(false);

		//PAS-14501 start
		Vehicle updateVehicleGaragingAddressResponse3 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest);
		softly.assertThat(updateVehicleGaragingAddressResponse3.garagingDifferent).isEqualTo(true);
		softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.addressLine1).isEqualTo(addressGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.city).isEqualTo(cityGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.stateProvCd).isEqualTo(stateGarage);
		//PAS-14501 end

		String zipCodeOwnership = "23703";
		String addressLine1Ownership = "4112 FORREST HILLS DR";
		String addressLine2Ownership = "Apt. 202";
		String cityOwnership = "PORTSMOUTH";
		String stateOwnership = "VA";
		String otherNameOwnership = "other name";
		String secondNameOwnership = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.city = cityOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = stateOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCodeOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherNameOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondNameOwnership;
		VehicleUpdateResponseDto ownershipUpdateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse.vehicleOwnership.ownership).isEqualTo("LSD");

		//PAS-13252 start
		String purchaseDate2 = "2014-03-22";
		VehicleUpdateDto updatePurchaseDateVehicleRequest = new VehicleUpdateDto();
		updatePurchaseDateVehicleRequest.purchaseDate = purchaseDate2;
		Vehicle updatePurchaseDateVehicleResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updatePurchaseDateVehicleRequest);
		//BUG PAS-13524 UpdateVehicle response contains NULLs for some fields
		softly.assertThat(updatePurchaseDateVehicleResponse.purchaseDate.replace("T00:00:00Z", "")).isEqualTo(purchaseDate2);
		softly.assertThat(updatePurchaseDateVehicleResponse.oid).isEqualTo(newVehicleOid);
		softly.assertThat(updatePurchaseDateVehicleResponse.salvaged.toString()).isEqualTo("false");
		//PAS-13252 end

		//View endorsement vehicles
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse.canAddVehicle).isEqualTo(true);

		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle.vehIdentificationNo).isEqualTo(vin);

		//View endorsement drivers
		ViewDriversResponse responseViewDriver = HelperCommon.viewEndorsementDrivers(policyNumber);
		assertThat(responseViewDriver.driverList.stream().filter(driver -> originalDriver.equals(driver.oid)).findFirst().orElse(null)).isNotNull();

		//View driver assignment if VA
		if ("VA, NY, CA".contains(state)) {
			DriverAssignmentDto[] responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment[0].vehicleOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment[0].driverOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment[0].relationshipType).isEqualTo("primary");

			softly.assertThat(responseDriverAssignment[1].vehicleOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment[1].driverOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment[1].relationshipType).isEqualTo("occasional");
		} else {
			ErrorResponseDto responseDriverAssignment = HelperCommon.viewEndorsementAssignmentsError(policyNumber, 422);
			softly.assertThat(responseDriverAssignment.errorCode).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getCode());
			softly.assertThat(responseDriverAssignment.message).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getMessage());
		}

		//update coverages
		String compDedCovCd = "COMPDED";
		String compDedAvailableLimits = "100";
		PolicyCoverageInfo coverageResponseCompDedResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, compDedCovCd, compDedAvailableLimits);
		Coverage filteredCoverageResponse = coverageResponseCompDedResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(compDedCovCd)).findFirst().orElse(null);
		assertThat(filteredCoverageResponse.coverageLimit).isEqualTo("100");

		//get all discounts
		DiscountSummary endorsementDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, "endorsement", 200);
		assertThat(endorsementDiscountsResponse.policyDiscounts.get(1).discountCd).isNotEmpty();

		//Rate endorsement
		rateEndorsement(softly, policyNumber);

		//View premium after new vehicle was added
		PolicyPremiumInfo[] viewPremiumInfoPendedEndorsementResponse = HelperCommon.viewEndorsementPremiums(policyNumber);
		softly.assertThat(viewPremiumInfoPendedEndorsementResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		softly.assertThat(viewPremiumInfoPendedEndorsementResponse[0].premiumCode).isEqualTo("GWT");
		softly.assertThat(new Dollar(viewPremiumInfoPendedEndorsementResponse[0].actualAmt)).isNotNull();
		softly.assertThat(new Dollar(viewPremiumInfoPendedEndorsementResponse[0].termPremium)).isNotNull();

		//update coverages
		String compDedCovCd2 = "COMPDED";
		String compDedAvailableLimits2 = "500";
		PolicyCoverageInfo coverageCompDedResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, compDedCovCd2, compDedAvailableLimits2);
		Coverage filteredUpdateCoverageResponse2 = coverageCompDedResponse2.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(compDedCovCd2)).findFirst().orElse(null);
		assertThat(filteredUpdateCoverageResponse2.coverageLimit).isEqualTo("500");

		//View endorsement Coverage
		PolicyCoverageInfo viewEndorsementCoveragesByVehicleResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage filteredViewEndorsementCoverageResponse = viewEndorsementCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(compDedCovCd2)).findFirst().orElse(null);
		assertThat(filteredViewEndorsementCoverageResponse.coverageLimit).isEqualTo("500");
		pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		//Bind endorsement
		HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		//Unlock policy
		PolicyLockUnlockDto responseUnlock = HelperCommon.executePolicyUnlockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		assertThat(responseUnlock.policyNumber).isEqualTo(policyNumber);
		assertThat(responseUnlock.status).isEqualTo("Unlocked");

		//View endorsement Coverage
		PolicyCoverageInfo viewPolicyCoveragesByVehicleResponse = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage filteredViewPolicyCoverageResponse = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(compDedCovCd2)).findFirst().orElse(null);
		assertThat(filteredViewPolicyCoverageResponse.coverageLimit).isEqualTo("500");
		SearchPage.openPolicy(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		//BUG PAS-13652 When Endorsement screen shows Endorsement Date field twice, if creating endorsement after endorsement created/issued through service
		//BUG PAS-13651 Instantiate state specific coverages
		secondEndorsementIssueCheck();

		//Create pended endorsement
		AAAEndorseResponse createEndorsementResponse = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(createEndorsementResponse.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, newVehicleOid);
		softly.assertThat(deleteVehicleResponse.oid).isEqualTo(newVehicleOid);
		softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
		softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin);
		assertThat(deleteVehicleResponse.ruleSets).isEqualTo(null);

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).oid).isEqualTo(newVehicleOid);
		assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin);
		assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehicleStatus).isEqualTo("pendingRemoval");

		endorsementRateAndBind(policyNumber);
	}

	protected void pas11684_DriverAssignmentExistsForStateBody(String state, SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//View driver assignment if VA
		if ("VA".equals(state) || "CA".equals(state) || "NY".equals(state)) {
			DriverAssignmentDto[] responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment[0].vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].relationshipType).isEqualTo("primary");
		} else {
			ErrorResponseDto responseDriverAssignment = HelperCommon.viewEndorsementAssignmentsError(policyNumber, 422);
			softly.assertThat(responseDriverAssignment.errorCode).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getCode());
			softly.assertThat(responseDriverAssignment.message).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getMessage());
		}

		endorsementRateAndBind(policyNumber);
	}

	protected void pas11618_UpdateVehicleLeasedFinancedInfoBody(SoftAssertions softly, String ownershipType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Add vehicle with specific info
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = vehicleAddWithCheck(policyNumber, vehicleAddRequest);

		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCode = "23703";
		String addressLine1 = "4112 FORREST HILLS DR";
		String addressLine2 = "Apt. 202";
		String city = "PORTSMOUTH";
		String state = "VA";
		String otherName = "other name";
		String secondName = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2;
		updateVehicleLeasedFinanced.vehicleOwnership.city = city;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = state;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCode;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = ownershipType;
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherName;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondName;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		if ("LSD".equals(ownershipType)) {
			assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE)).hasValue("Leased");
		}
		if ("FNC".equals(ownershipType)) {
			assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE)).hasValue("Financed");
		}
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.FIRST_NAME)).hasValue("Other");
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNER_NO_LABEL)).hasValue(otherName); //can't take the value of the field with no label
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.SECOND_NAME)).hasValue(secondName);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.ADDRESS_LINE_1)).hasValue(addressLine1);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.ADDRESS_LINE_2)).hasValue(addressLine2);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.CITY)).hasValue(city);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.STATE)).hasValue(state);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.ZIP_CODE)).hasValue(zipCode);
		mainApp().close();

		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse.canAddVehicle).isEqualTo(true);
		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle1.vehIdentificationNo).isEqualTo(vin);
		assertThat(newVehicle1).isNotNull();
		assertThat(newVehicle1.oid).isEqualTo(newVehicleOid);
		softly.assertThat(newVehicle1.vehicleOwnership.ownership).isEqualTo(ownershipType);
		softly.assertThat(newVehicle1.vehicleOwnership.addressLine1).isEqualTo(addressLine1);
		softly.assertThat(newVehicle1.vehicleOwnership.addressLine2).isEqualTo(addressLine2);
		softly.assertThat(newVehicle1.vehicleOwnership.city).isEqualTo(city);
		softly.assertThat(newVehicle1.vehicleOwnership.stateProvCd).isEqualTo(state);
		softly.assertThat(newVehicle1.vehicleOwnership.postalCode).isEqualTo(zipCode);
		softly.assertThat(newVehicle1.vehicleOwnership.name).isEqualTo(otherName);
		softly.assertThat(newVehicle1.vehicleOwnership.secondName).isEqualTo(secondName);

		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsmentVehiclesMetaData(policyNumber, newVehicleOid);
		AttributeMetadata metaDataFieldResponse = getAttributeMetadata(metaDataResponse, "vehicleOwnership.ownership", true, true, false, null);
		softly.assertThat(metaDataFieldResponse.valueRange.get("OWN")).isEqualTo("Owned");
		softly.assertThat(metaDataFieldResponse.valueRange.get("FNC")).isEqualTo("Financed");
		softly.assertThat(metaDataFieldResponse.valueRange.get("LSD")).isEqualTo("Leased");

		getAttributeMetadata(metaDataResponse, "vehicleOwnership.name", true, true, false, "100");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.secondName", true, true, false, "100");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.postalCode", true, true, false, "10");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine1", true, true, false, "40");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine2", true, true, false, "40");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.city", true, true, false, "30");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.stateProvCd", true, true, false, null);

		ViewVehicleResponse policyValidateVehicleInfoResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oldVehicleOid = policyValidateVehicleInfoResponse.vehicleList.get(0).oid;
		AttributeMetadata[] metaDataResponseOwned = HelperCommon.viewEndorsmentVehiclesMetaData(policyNumber, oldVehicleOid);
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.ownership", true, true, false, null);
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.name", false, false, false, "100");
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.secondName", false, false, false, "100");
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.postalCode", false, false, false, "10");
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.addressLine1", false, false, false, "40");
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.addressLine2", false, false, false, "40");
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.city", false, false, false, "30");
		getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.stateProvCd", false, false, false, null);

		endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		AAAEndorseResponse response2 = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response2.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		//BUG PAS-14395 Update Vehicle service failed to update ownership
		softly.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE).getValue()).isEqualTo("Owned");
		mainApp().close();

		endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		secondEndorsementIssueCheck();
	}

	protected void pas14316_LoanLeasedCovForLeasedOldVehicleBody(SoftAssertions softly, String ownershipType) {
		mainApp().open();
		//String policyNumber = "VASS952918542";

		String policyNumber = getCopiedPolicy();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//VIN Will need to be updated yearly to make the test functional
		String purchaseDate = "2013-02-22";
		String vin = "JF1GJAA64EH012557"; //2014 Subaru Impreza
		//String vin = "JF1GPAT66FH237608"; //2015 Subaru Impreza

		//Add vehicle with specific info
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = vehicleAddWithCheck(policyNumber, vehicleAddRequest);

		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		rateEndorsement(softly, policyNumber);
		//Add coverage check here
		String coverageCdValue = "LOAN";
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseOwnedOldVehFiltered = endorsementCoverageResponseOwnedOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVehFiltered, "0", "No Coverage", false, false);

		String zipCode = "23703";
		String addressLine1 = "4112 FORREST HILLS DR";
		String addressLine2 = "Apt. 202";
		String city = "PORTSMOUTH";
		String state = "VA";
		String otherName = "other name";
		String secondName = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2;
		updateVehicleLeasedFinanced.vehicleOwnership.city = city;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = state;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCode;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = ownershipType;
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherName;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondName;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		rateEndorsement(softly, policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		AAAEndorseResponse response2 = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response2.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		rateEndorsement(softly, policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVeh2Filtered, "0", "No Coverage", false, false);
	}

	protected void pas14316_LoanLeasedCovForFinancedNewVehicleBody(SoftAssertions softly, String ownershipType) {
		mainApp().open();
		//String policyNumber = "VASS952918542";

		String policyNumber = getCopiedPolicy();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//VIN Will need to be updated yearly to make the test functional
		String purchaseDate = "2013-02-22";
		//String vin = "JF1GJAA64EH012557"; //2014 Subaru Impreza
		String vin = "JF1GPAT66FH237608"; //2015 Subaru Impreza

		//Add vehicle with specific info
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = vehicleAddWithCheck(policyNumber, vehicleAddRequest);

		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		rateEndorsement(softly, policyNumber);
		//Add coverage check here
		String coverageCdValue = "LOAN";
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseOwnedOldVehFiltered = endorsementCoverageResponseOwnedOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVehFiltered, "0", "No Coverage", false, false);

		String zipCode = "23703";
		String addressLine1 = "4112 FORREST HILLS DR";
		String addressLine2 = "Apt. 202";
		String city = "PORTSMOUTH";
		String state = "VA";
		String otherName = "other name";
		String secondName = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2;
		updateVehicleLeasedFinanced.vehicleOwnership.city = city;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = state;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCode;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = ownershipType;
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherName;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondName;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		rateEndorsement(softly, policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", true, true);

		PolicyCoverageInfo updateLoanLeaseCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdValue, "1");
		Coverage updateLoanLeaseCoverageFiltered = updateLoanLeaseCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, updateLoanLeaseCoverageFiltered, "1", "Yes", true, true);
		//BUG Status is not reset when updating coverages
		pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		rateEndorsement(softly, policyNumber);

		endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "1", "Yes", true, true);

		AAAEndorseResponse response2 = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response2.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		rateEndorsement(softly, policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVeh2Filtered, "0", "No Coverage", false, false);
	}

	private void loanLeaseCovPropertiesCheck(SoftAssertions softly, Coverage endorsementCoverageResponseOwnedOldVeh2Filtered, String coverageLimit, String coverageDisplayLimit, boolean customerDisplayed, boolean canChangeCoverage) {
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageCd).isEqualTo("LOAN");
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageLimit).isEqualTo(coverageLimit);
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageLimitDisplay).isEqualTo(coverageDisplayLimit);
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageType).isEqualTo("None");
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.customerDisplayed).isEqualTo(customerDisplayed);
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.canChangeCoverage).isEqualTo(canChangeCoverage);
	}

	private AttributeMetadata getAttributeMetadata(AttributeMetadata[] metaDataResponse, String fieldName, boolean enabled, boolean visible, boolean required, String maxLength) {
		AttributeMetadata metaDataFieldResponse = Arrays.stream(metaDataResponse).filter(attributeMetadata -> fieldName.equals(attributeMetadata.attributeName)).findFirst().orElse(null);
		assertThat(metaDataFieldResponse.enabled).isEqualTo(enabled);
		assertThat(metaDataFieldResponse.visible).isEqualTo(visible);
		assertThat(metaDataFieldResponse.required).isEqualTo(required);
		assertThat(metaDataFieldResponse.maxLength).isEqualTo(maxLength);
		return metaDataFieldResponse;
	}

	protected void pas13252_UpdateVehicleGaragingAddressProblemBody(SoftAssertions softly) {
		mainApp().open();
		//String policyNumber = getCopiedPolicy();
		String policyNumber = "VASS952918560";

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Add vehicle with specific info
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = vehicleAddWithCheck(policyNumber, vehicleAddRequest);

		String zipCode = "23703";
		String addressLine1 = "4112 FORREST HILLS DR";
		String city = "PORTSMOUTH";
		String state = "VA";
		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleGaraging = new VehicleUpdateDto();
		updateVehicleGaraging.garagingDifferent = true;
		updateVehicleGaraging.garagingAddress = new Address();
		updateVehicleGaraging.garagingAddress.postalCode = zipCode;
		updateVehicleGaraging.garagingAddress.addressLine1 = addressLine1;
		updateVehicleGaraging.garagingAddress.city = city;
		updateVehicleGaraging.garagingAddress.stateProvCd = state;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleGaraging);

		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).verify.value("Yes");
		vehicleTab.getAssetList().getAsset(ZIP_CODE).verify.value(zipCode);
		vehicleTab.getAssetList().getAsset(ADDRESS_LINE_1).verify.value(addressLine1);
		vehicleTab.getAssetList().getAsset(CITY).verify.value(city);
		vehicleTab.getAssetList().getAsset(STATE).verify.value(state);

		mainApp().close();
		endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		secondEndorsementIssueCheck();
	}

	@SuppressWarnings("unchecked")
	protected void pas9546_maxVehiclesBody(SoftAssertions softly) {
		mainApp().open();
		//Default policy has 1 vehicles
		String policyNumber = getCopiedPolicy();

		mainApp().open();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Add 6 vehicles
		String purchaseDate2 = "2013-01-20";
		String vin2 = "1C4BJWDG0JL847133"; //jeep wrangler 2018
		addVehicleWithChecks(policyNumber, purchaseDate2, vin2, true);

		String purchaseDate3 = "2013-01-21";
		String vin3 = "JF1GJAH65EH007244"; //Subaru Impreza 2014
		addVehicleWithChecks(policyNumber, purchaseDate3, vin3, true);

		String purchaseDate4 = "2013-02-22";
		String vin4 = "3MZBN1M39JM170308"; //Mazda 3 2018
		addVehicleWithChecks(policyNumber, purchaseDate4, vin4, true);

		String purchaseDate5 = "2013-03-23";
		String vin5 = "5YFBURHE0HP576402"; // Toyota Corolla 2017
		addVehicleWithChecks(policyNumber, purchaseDate5, vin5, true);

		String purchaseDate6 = "2013-04-24";
		String vin6 = "JTDKBRFU2H3564115"; //Toyota Prius 2017
		addVehicleWithChecks(policyNumber, purchaseDate6, vin6, true);

		String purchaseDate7 = "2013-05-25";
		String vin7 = "JTHHP5AY5JA002692"; //Lexus LC 500 2018
		addVehicleWithChecks(policyNumber, purchaseDate7, vin7, true);

		String purchaseDate8 = "2013-06-26";
		String vin8 = "2HGFC2F70HH505174"; //2017 Honda Civic
		addVehicleWithChecks(policyNumber, purchaseDate8, vin8, false);

		//add the 9th vehicle, check error
		String purchaseDate9 = "2013-06-27";
		String vin9 = "19XFC1F39HE010621"; //2017 Honda Civic
		Vehicle request = new Vehicle();
		request.purchaseDate = purchaseDate9;
		request.vehIdentificationNo = vin9;

		ErrorResponseDto responseAddVehicleError = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate9, vin9);
		softly.assertThat(responseAddVehicleError.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(responseAddVehicleError.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(responseAddVehicleError.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getCode());
		softly.assertThat(responseAddVehicleError.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getMessage());

		//Rate endorsement
		rateEndorsement(softly, policyNumber);

		//Bind endorsement
		HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		AAAEndorseResponse response2 = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response2.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Add 6 vehicles
		request.purchaseDate = purchaseDate9;
		request.vehIdentificationNo = vin9;

		ErrorResponseDto responseAddVehicleError2 = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate9, vin9);
		softly.assertThat(responseAddVehicleError2.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(responseAddVehicleError2.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(responseAddVehicleError2.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getCode());
		softly.assertThat(responseAddVehicleError2.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getMessage());

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(getVehicleTab());
		VehicleTab.tableVehicleList.removeRow(6);
		vehicleTab.saveAndExit();
		mainApp().close();

		addVehicleWithChecks(policyNumber, purchaseDate6, vin6, false);
		PolicyPremiumInfo[] endorsementRateResponse2 = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(endorsementRateResponse2[0].actualAmt).isNotBlank();

		HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		secondEndorsementIssueCheck();
	}

	protected void pas14501_garagingDifferentBody(String state, SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//get all vehicles
		ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String oldVehicleOid = responseViewVehicles.vehicleList.get(0).oid;

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		SearchPage.openPolicy(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Validate VIN
		AAAVehicleVinInfoRestResponseWrapper vinValidateResponse = HelperCommon.executeVinInfo(policyNumber, vin, endorsementDate);
		softly.assertThat(vinValidateResponse.vehicles.get(0).vin).isEqualTo(vin);

		//Add new vehicle
		//BUG PAS-14688, PAS-14689, PAS-14690, PAS-14691 - Add Vehicle for DC, KS, NY, OR
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		SearchPage.openPolicy(policyNumber);

		//Update Vehicle with proper Usage and Registered Owner
		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		//PAS-14501 start
		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddress = new Address();
		updateGaragingAddressVehicleRequest.garagingAddress.postalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.city = cityGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.stateProvCd = stateGarage;
		Vehicle updateVehicleGaragingAddressResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingDifferent).isEqualTo(true);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.addressLine1).isEqualTo(addressGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.city).isEqualTo(cityGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.stateProvCd).isEqualTo(stateGarage);
		//PAS-14501 end

		//Rate endorsement
		rateEndorsement(softly, policyNumber);

		//Bind endorsement
		HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		secondEndorsementIssueCheck();

		policy.updateRulesOverride().start();
		assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200021").getCell(1)).isAbsent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}

	private void rateEndorsement(SoftAssertions softly, String policyNumber) {
		PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
		softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
	}

	private void pas14952_checkEndorsementStatusWasReset(String policyNumber, String endorsementStatus) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		//BUG	PAS-14952 	Lets set coverages to Zero when needed
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").verify.value(endorsementStatus);
	}

	private String addVehicleWithChecks(String policyNumber, String purchaseDate, String vin, boolean allowedToAddVehicle) {
		//Add new vehicle
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);

		//Update Vehicle with proper Usage and Registered Owner
		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse.canAddVehicle).isEqualTo(allowedToAddVehicle);
		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle.vehIdentificationNo).isEqualTo(vin);
		return newVehicleOid;
	}

	private String vehicleAddWithCheck(String policyNumber, Vehicle vehicleAddRequest) {
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, vehicleAddRequest);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		return newVehicleOid;
	}

	private void updateVehicleUsageRegisteredOwner(String policyNumber, String newVehicleOid) {
		//Update Vehicle with proper Usage and Registered Owner
		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");
	}

	private void endorsePolicyAddEvalue() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		new PremiumAndCoveragesTab().calculatePremium();
		getPremiumAndCoverageTabElement().saveAndExit();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
	}

	private void endorsementRateAndBind(String policyNumber) {
		assertSoftly(softly -> {
			//Rate endorsement
			PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();

			//Bind endorsement
			HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
			mainApp().open();
			SearchPage.openPolicy(policyNumber);
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
		});
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
				+ "set ps.SYSGENERATEDTXIND = 1, ps.CREATEDBY = 'ipbsysp'\n"
				+ "where id = %s";

		String policySummaryId = DBService.get().getValue(String.format(getPolicySummaryId, policyNumber)).get();
		DBService.get().executeUpdate(String.format(updateSystemGeneratedInd, policySummaryId));
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

	private Dollar getCoverage(int index, String coverageCd, String... replacement) {
		String coverage = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(index, coverageCd);
		for (String replace : replacement) {
			coverage = coverage.replace(replace, "");
		}
		return new Dollar(coverage);
	}

	private String getCoverages(int index, String coverageCd, String... replacement) {
		String coverage = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(index, coverageCd);
		for (String replace : replacement) {
			coverage = coverage.replace(replace, "");

		}
		return coverage;
	}

}