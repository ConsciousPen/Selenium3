package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static aaa.modules.regression.service.helper.preconditions.TestMiniServicesNonPremiumBearingAbstractPreconditions.DELETE_INSERT_EFFECTIVE_DATE;
import static aaa.modules.regression.service.helper.preconditions.TestMiniServicesNonPremiumBearingAbstractPreconditions.INSERT_EFFECTIVE_DATE;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.BooleanUtils;
import org.assertj.core.api.SoftAssertions;
import org.springframework.util.CollectionUtils;
import org.testng.ITestContext;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.MaigConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.*;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
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
	private VehicleTab vehicleTab = new VehicleTab();
	private GeneralTab generalTab = new GeneralTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();

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
		String policyNumber = getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		testEValueDiscount.secondEndorsementIssueCheck();

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

		mainApp().close();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
		String policyNumber = getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

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
		String policyNumber = getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//Future Dated Endorsement creation
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(getPremiumAndCoverageTab());
		getDocumentsAndBindTabElement().saveAndExit();

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
		String policyNumber = getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
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
		String policyNumber = getCopiedPolicy();

		PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
		String policyNumber = getCopiedPolicy();

		//New Config Version testing for AZ = 0 days delay
		PolicySummary responseNewConfigEffective = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
		String policyNumber = getCopiedPolicy();

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

	private void manualPendedEndorsementCreate() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		NavigationPage.toViewSubTab(getPremiumAndCoverageTab());//to get status = Premium Calculated
		getPremiumAndCoverageTabElement().saveAndExit();
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

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//check that new endorsement was created
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		pas8785_createdEndorsementTransactionProperties("Gathering Info", TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY), "MyPolicy MyPolicy");
	}

	protected void pas8273_CheckIfOnlyActiveVehiclesAreAllowed(SoftAssertions softly, PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
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
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();
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
		mainApp().close();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ErrorResponseDto response = HelperCommon.startEndorsementError(policyNumber, endorsementDate, 422);//UNPROCESSED ENTITY
		assertSoftly(softly -> {
			softly.assertThat(response.errorCode).isEqualTo(ErrorDxpEnum.Errors.ACTION_IS_NOT_AVAILABLE.getCode());
			softly.assertThat(response.message).isEqualTo(ErrorDxpEnum.Errors.ACTION_IS_NOT_AVAILABLE.getMessage());
		});
	}

	protected void pas15846_CheckTransactionDateForEndorsementsBody(){
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = getCopiedPolicy();
		assertSoftly(softly -> {
			//Today date endorsement
			String todayDateEndorsement = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String expirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			PolicySummary todayDateEndorsementResponse = HelperCommon.createEndorsement(policyNumber, todayDateEndorsement);
			softly.assertThat(todayDateEndorsementResponse.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(todayDateEndorsementResponse.policyStatus).isEqualTo("dataGather");
			softly.assertThat(todayDateEndorsementResponse.timedPolicyStatus).isEqualTo("dataGather");
			softly.assertThat(todayDateEndorsementResponse.effectiveDate).isEqualTo(todayDateEndorsement);
			softly.assertThat(todayDateEndorsementResponse.expirationDate).isEqualTo(expirationDate);
			softly.assertThat(todayDateEndorsementResponse.sourceOfBusiness).isEqualTo("NEW");
			softly.assertThat(todayDateEndorsementResponse.renewalCycle).isEqualTo(0);
			softly.assertThat(todayDateEndorsementResponse.eValueStatus).isEqualTo("NOTENROLLED");
			softly.assertThat(todayDateEndorsementResponse.actualAmt).isNotEmpty();
			softly.assertThat(todayDateEndorsementResponse.termPremium).isNotEmpty();
			softly.assertThat(todayDateEndorsementResponse.residentialAddress.addressLine1).isNotEmpty();
			softly.assertThat(todayDateEndorsementResponse.residentialAddress.addressLine2).isNotBlank();
			softly.assertThat(todayDateEndorsementResponse.residentialAddress.city).isNotEmpty();
			softly.assertThat(todayDateEndorsementResponse.residentialAddress.stateProvCd).isNotEmpty();
			softly.assertThat(todayDateEndorsementResponse.residentialAddress.postalCode).isNotEmpty();
			softly.assertThat(todayDateEndorsementResponse.transactionEffectiveDate).isEqualTo(todayDateEndorsement);

			PolicySummary pendingEndorsementImageInfo1 = HelperCommon.viewPendingEndorsementImageInfo(policyNumber);
			softly.assertThat(pendingEndorsementImageInfo1.transactionEffectiveDate).isEqualTo(todayDateEndorsement);

			//Future endorsement
			String futureDateEndorsement = TimeSetterUtil.getInstance().getCurrentTime().plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			PolicySummary futureDateEndorsementResponse = HelperCommon.createEndorsement(policyNumber, futureDateEndorsement);
			softly.assertThat(futureDateEndorsementResponse.transactionEffectiveDate).isEqualTo(futureDateEndorsement);

			PolicySummary pendingEndorsementImageInfo2 = HelperCommon.viewPendingEndorsementImageInfo(policyNumber);
			softly.assertThat(pendingEndorsementImageInfo2.transactionEffectiveDate).isEqualTo(futureDateEndorsement);

			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(7));
			PolicySummary pendingEndorsementImageInfo3 = HelperCommon.viewPendingEndorsementImageInfo(policyNumber);
			softly.assertThat(pendingEndorsementImageInfo3.transactionEffectiveDate).isEqualTo(futureDateEndorsement);

			helperMiniServices.endorsementRateAndBind(policyNumber);
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

	protected void pas9716_policySummaryForConversionManualBody() {
		assertSoftly(softly -> {
			LocalDateTime effDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(45);
			mainApp().open();
			createCustomerIndividual();
			customer.initiateRenewalEntry().perform(getPolicyTD("InitiateRenewalEntry", "TestData"), effDate);
			policy.getDefaultView().fill(getPolicyTD("Conversion", "TestData"));
			Tab.buttonBack.click();
			String policyNum = PolicySummaryPage.getPolicyNumber();
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
			//time shifting to let the tests pass
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

	protected void pas9456_9455_PolicyLockUnlockServicesBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		mainApp().close();

		assertSoftly(softly -> {
			//Lock policy id1 and check service response
			PolicyLockUnlockDto response = HelperCommon.executePolicyLockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
			softly.assertThat(response.policyNumber).isEqualTo(policyNumber);
			softly.assertThat(response.status).isEqualTo("Locked");

			//Hit start endorsement info service with Id1
			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
		testEValueDiscount.secondEndorsementIssueCheck();
	}

	protected void pas508_BindServiceEndorsement() {
		String authorizedBy = "Osi Testas Insured";

		mainApp().open();
		String policyNumber = getCopiedPolicy();

		assertSoftly(softly -> {
			String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "ENDORSEMENT_ISSUE");
			int numberOfDocumentsRecordsInDb = Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get());

			//Create pended endorsement
			PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, null);
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Create pended endorsement
			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

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
			testEValueDiscount.secondEndorsementIssueCheck();
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
		PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin = "4S2CK58W8X4307498";
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

	protected void pas12767_ServiceEndorsementCancelBody() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();
			SearchPage.openPolicy(policyNumber);

			//Perform Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			SearchPage.openPolicy(policyNumber);
			assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isTrue();
			HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
			SearchPage.openPolicy(policyNumber);
			assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			SearchPage.openPolicy(policyNumber);
			assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isTrue();
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
			SearchPage.openPolicy(policyNumber);
			assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
		});
	}

	protected void pas15897_TransactionHistoryAndMessage() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			softly.assertThat(policyResponse.changeType).isEqualTo("NO_CHANGES");
		});

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas14539_transactionInfoDriverAssignmentBody() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin = "4S2CK58W8X4307498";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		String newVehicleOid=addVehicle.oid;
		assertThat(addVehicle.oid).isNotEmpty();

		//Update Vehicle with proper Usage and Registered Owner
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		ViewDriversResponse response = HelperCommon.viewPolicyDrivers(policyNumber);
		String dOid = response.driverList.get(0).oid;

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh1 = policyResponse.vehicles.get(newVehicleOid);
		assertSoftly(softly -> {
			softly.assertThat(veh1.driverAssignments.get(dOid).changeType).isEqualTo("ADDED");
			softly.assertThat(veh1.driverAssignments.get(dOid).data.driverOid).isEqualTo(dOid);
			softly.assertThat(veh1.driverAssignments.get(dOid).data.vehicleDisplayValue).isEqualTo(dOid);
			softly.assertThat(veh1.driverAssignments.get(dOid).data.relationshipType).isEqualTo("occasional");

		});

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas14539_transactionInfoAddVehicleCoveragesBody() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2012-02-21";
		String vin = "SHHFK7H41JU201444";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();
		String newVehicleOid = addVehicle.oid;

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

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


		ViewDriversResponse response = HelperCommon.viewPolicyDrivers(policyNumber);
		String dOid = response.driverList.get(0).oid;

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh1 = policyResponse.vehicles.get(newVehicleOid);
		assertSoftly(softly -> {
			softly.assertThat(veh1.driverAssignments.get(dOid).changeType).isEqualTo("ADDED");
			softly.assertThat(veh1.driverAssignments.get(dOid).data.driverOid).isEqualTo(dOid);
			softly.assertThat(veh1.driverAssignments.get(dOid).data.vehicleDisplayValue).isEqualTo(dOid);
			softly.assertThat(veh1.driverAssignments.get(dOid).data.relationshipType).isEqualTo("occasional");

		});

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas12767_ManualEndorsementCancelBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		SearchPage.openPolicy(policyNumber);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		Tab.buttonSaveAndExit.click();
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	private void endorsePolicyAddEvalue() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		premiumAndCoveragesTab.calculatePremium();
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
				+ "set ps.SYSGENERATEDTXIND = 1, ps.CREATEDBY = 'ipbsysp'\n"
				+ "where id = %s";

		String policySummaryId = DBService.get().getValue(String.format(getPolicySummaryId, policyNumber)).get();
		DBService.get().executeUpdate(String.format(updateSystemGeneratedInd, policySummaryId));
	}

}
