package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.USAGE;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.VIN;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.core.Response;
import org.assertj.core.api.SoftAssertions;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.modules.policy.auto_ss.actiontabs.UpdateRulesOverrideActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesUwRulesAbstract extends PolicyBaseTest {

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private ErrorTab errorTab = new ErrorTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private VehicleTab vehicleTab = new VehicleTab();
	private DriverTab driverTab = new DriverTab();
	private GeneralTab generalTab = new GeneralTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private static String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getVehicleTabElement();

	protected abstract AssetDescriptor<JavaScriptButton> getCalculatePremium();

	/**
	 * @author Oleg Stasyuk
	 * @name Test Current Bill Service for non-Annual
	 * @scenario 1. Create a policy
	 * 2. run the current bill service
	 * 3. check zero balances
	 * 4. generate the bill1
	 * 5. check Due Date, Min Due, Past Due (0) are returned same as are there in UI
	 * 6. partially pay the bill
	 * 7. check Due Date, Min Due, Past Due (0) are returned same as are there in UI
	 * 8. generate the bill2
	 * 9. check Due Date, Min Due, Past Due (non-0) are returned same as are there in UI
	 * 10. pay policy total due
	 * 11. check Due Date, Min Due, Past Due (non-0) are returned same as are there in UI
	 * @details
	 */
	protected void pas12852_GaragedInMichigan200020Body(SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		HelperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = HelperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

		HelperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCode = "48002";
		String addressLine1 = "4112 FORREST HILLS DR";
		String city = "Allenton";
		String state = "MI";
		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleGaraging = new VehicleUpdateDto();
		updateVehicleGaraging.garagingDifferent = true;
		updateVehicleGaraging.garagingAddress = new Address();
		updateVehicleGaraging.garagingAddress.postalCode = zipCode;
		updateVehicleGaraging.garagingAddress.addressLine1 = addressLine1;
		updateVehicleGaraging.garagingAddress.city = city;
		updateVehicleGaraging.garagingAddress.stateProvCd = state;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleGaraging);

		//TODO Zip code is not applicable; please check again. If this is incorrect, please
		ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber, 422);
		softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.ZIP_CODE_IS_NOT_APPLICABLE.getCode());
		softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.ZIP_CODE_IS_NOT_APPLICABLE.getMessage());
		softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("postalCode");
/*
		HelperMiniServices.rateEndorsement(softly, policyNumber);

		HelperCommon.endorsementBind(policyNumber, "osi", Response.Status.OK.getStatusCode());

		SearchPage.openPolicy(policyNumber);
		helperMiniServices.secondEndorsementIssueCheck();

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "PAS-7147", 422);
			softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getCode());
			softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getMessage());
			*/
	}

	protected void pas12852_GaragedOutOfState200019Body(SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		HelperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = HelperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

		HelperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCode = "84121";
		String addressLine1 = "4112 FORREST HILLS DR";
		String city = "Salt Lake City";
		String state = "UT";
		VehicleUpdateDto updateVehicleGaraging = new VehicleUpdateDto();
		updateVehicleGaraging.garagingDifferent = true;
		updateVehicleGaraging.garagingAddress = new Address();
		updateVehicleGaraging.garagingAddress.postalCode = zipCode;
		updateVehicleGaraging.garagingAddress.addressLine1 = addressLine1;
		updateVehicleGaraging.garagingAddress.city = city;
		updateVehicleGaraging.garagingAddress.stateProvCd = state;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleGaraging);

		HelperMiniServices.rateEndorsement(softly, policyNumber);

		HelperCommon.endorsementBind(policyNumber, "osi", Response.Status.OK.getStatusCode());

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "PAS-7147", 422);
		softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode());
		softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage());

		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.simplifiedPendedEndorsementIssue();
	}

	protected void pas12852_DuplicateVin200031Body(SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		HelperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = HelperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);
		HelperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().getAsset(VIN).setValue(vin);
		vehicleTab.getAssetList().getAsset(USAGE).setValue("Pleasure");
		vehicleTab.saveAndExit();

		HelperMiniServices.rateEndorsement(softly, policyNumber);

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "200031", 422);
		softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.UNIQUE_VIN.getCode());
		softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.UNIQUE_VIN.getMessage());
		softly.assertThat(bindResponse.errors.get(0).field).isEqualTo("attributeForRules");

		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.updateRulesOverride().start();
		CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200031").getCell(1)).isPresent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}

	protected void pas12852_ExpensiveVehicle200022Body(SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		HelperMiniServices.createEndorsementWithCheck(policyNumber);

		String vin = "ZFFCW56A830133118";
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.buttonAddVehicle.click();
		vehicleTab.getAssetList().getAsset(VIN).setValue(vin);
		vehicleTab.getAssetList().getAsset(USAGE).setValue("Pleasure");
		vehicleTab.saveAndExit();

		HelperMiniServices.rateEndorsement(softly, policyNumber);

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "200022", 422);
		softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.UNIQUE_VIN.getCode());
		softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.UNIQUE_VIN.getMessage());
		softly.assertThat(bindResponse.errors.get(0).field).isEqualTo("attributeForRules");

		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.updateRulesOverride().start();
		CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200022").getCell(1)).isPresent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}

	protected void pas12852_GaragedOutOfStateOnlyOneVeh200018Body(SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String originalVehicle = responseViewVehicles.vehicleList.get(0).oid;

		HelperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = HelperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

		HelperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCode = "84121";
		String addressLine1 = "4112 FORREST HILLS DR";
		String city = "Salt Lake City";
		String state = "UT";
		VehicleUpdateDto updateVehicleGaraging = new VehicleUpdateDto();
		updateVehicleGaraging.garagingDifferent = true;
		updateVehicleGaraging.garagingAddress = new Address();
		updateVehicleGaraging.garagingAddress.postalCode = zipCode;
		updateVehicleGaraging.garagingAddress.addressLine1 = addressLine1;
		updateVehicleGaraging.garagingAddress.city = city;
		updateVehicleGaraging.garagingAddress.stateProvCd = state;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleGaraging);

		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicle);
		softly.assertThat(deleteVehicleResponse.oid).isEqualTo(originalVehicle);
		softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");

		//BUG PAS-15481 Rating through service fails, when New Vehicle is added through Service and Original Vehicle removed
		HelperMiniServices.rateEndorsement(softly, policyNumber);

		//TODO workaround for failed to rate Endorsement
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		//BUG  PAS-15482 Bind through service fails, when New Vehicle is added through Service and Original Vehicle removed
		//TODO workaround for failed issue Endorsement
		//NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "200018", 422);
		softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		ErrorResponseDto bindResponseFiltered = bindResponse.errors.stream().filter(errors -> "200018".equals(errors.errorCode)).findFirst().orElse(null);
		assertThat(bindResponseFiltered.message).contains(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE_ONLY_VEHICLE.getMessage());

		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.updateRulesOverride().start();
		CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200018").getCell(1)).isPresent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}

	protected void pas12852_MustHavePPA200016Body(SoftAssertions softly, TestData motorhomeData) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		//String policyNumber = "VASS952918552";
		ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String originalVehicleOid = responseViewVehicles.vehicleList.get(0).oid;

		HelperMiniServices.createEndorsementWithCheck(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.fillTab(motorhomeData);
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		//TODO uncomment for scenario, when deleteVehicles returns proper response
/*		String purchaseDate2 = "2013-02-22";
		String vin2 = "WAUKJAFM8C6314628";
		Vehicle vehicleAddRequest2 = new Vehicle();
		vehicleAddRequest2.purchaseDate = purchaseDate2;
		vehicleAddRequest2.vehIdentificationNo = vin2;
		String newVehicleOid2 = HelperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest2);
		HelperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid2);*/

		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicleOid);
		//BUG PAS-15483 Delete Vehicle doesnt return response in some cases
		softly.assertThat(deleteVehicleResponse.oid).isEqualTo(originalVehicleOid);
		softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");

		HelperMiniServices.rateEndorsement(softly, policyNumber);

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "200016", 422);
		softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		ErrorResponseDto bindResponseFiltered = bindResponse.errors.stream().filter(errors -> ErrorDxpEnum.Errors.MUST_HAVE_PPA.getCode().equals(errors.errorCode)).findFirst().orElse(null);
		assertThat(bindResponseFiltered.message).contains(ErrorDxpEnum.Errors.MUST_HAVE_PPA.getMessage());

		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.updateRulesOverride().start();
		CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200016").getCell(1)).isPresent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}



}
