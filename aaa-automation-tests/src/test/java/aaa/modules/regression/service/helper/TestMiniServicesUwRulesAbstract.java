package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.USAGE;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.VIN;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import javax.ws.rs.core.Response;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.modules.policy.auto_ss.actiontabs.UpdateRulesOverrideActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
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
	 * @name Check UW rules are fired on Bind
	 * @scenario 1. Create a policy.
	 * @details
	 */

	/**
	 * @author Jovita Pukenaite
	 * @name Check UW rules are fired on Rate
	 * @scenario 1. Rules for check:
	 * 200019_C, 200031_C, 200022_C, 200018_C, 200016_C, 200020_C
	 * @details
	 */
	protected void pas12852_GaragedInMichigan200020Body() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String purchaseDate = "2013-02-22";
			String vin = "1HGFA16526L081415";
			Vehicle vehicleAddRequest = new Vehicle();
			vehicleAddRequest.purchaseDate = purchaseDate;
			vehicleAddRequest.vehIdentificationNo = vin;
			String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			String zipCode = "48002";
			String addressLine1 = "4112 FORREST HILLS DR";
			String city = "Allenton";
			String state = "MI";
			VehicleUpdateDto updateVehicleGaraging = new VehicleUpdateDto();
			updateVehicleGaraging.garagingDifferent = true;
			updateVehicleGaraging.garagingAddress = new Address();
			updateVehicleGaraging.garagingAddress.postalCode = zipCode;
			updateVehicleGaraging.garagingAddress.addressLine1 = addressLine1;
			updateVehicleGaraging.garagingAddress.city = city;
			updateVehicleGaraging.garagingAddress.stateProvCd = state;
			HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleGaraging);

			ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("attributeForRules");
			softly.assertThat(rateResponse.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.GARAGED_IN_MICHIGAN.getCode());
			softly.assertThat(rateResponse.errors.get(1).message).contains(ErrorDxpEnum.Errors.GARAGED_IN_MICHIGAN.getMessage());
			softly.assertThat(rateResponse.errors.get(1).field).isEqualTo("attributeForRules");
			softly.assertThat(rateResponse.errors.get(2).errorCode).isEqualTo(ErrorDxpEnum.Errors.ZIP_CODE_IS_NOT_APPLICABLE.getCode());
			softly.assertThat(rateResponse.errors.get(2).message).contains(ErrorDxpEnum.Errors.ZIP_CODE_IS_NOT_APPLICABLE.getMessage());
			softly.assertThat(rateResponse.errors.get(2).field).isEqualTo("postalCode");
			//bind action is not available, can't even rate policy with vehicle Garaged in MI, ZIP is not resolved
			});
	}

	protected void pas12852_GaragedOutOfState200019Body() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String purchaseDate = "2013-02-22";
			String vin = "1HGFA16526L081415";
			Vehicle vehicleAddRequest = new Vehicle();
			vehicleAddRequest.purchaseDate = purchaseDate;
			vehicleAddRequest.vehIdentificationNo = vin;
			String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

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

			ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("attributeForRules");

			rateEndorsementInPas(policyNumber);
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode(), ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			//START PAS-17648
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String vin2 = "1FMEU15H7KLB19840";
			Vehicle vehicleAddRequest2 = new Vehicle();
			vehicleAddRequest2.purchaseDate = purchaseDate;
			vehicleAddRequest2.vehIdentificationNo = vin2;
			String newVehicleOid2 = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest2);
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid2);

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas12852_DuplicateVin200031Body() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String purchaseDate = "2013-02-22";
			String vin = "1HGFA16526L081415";
			Vehicle vehicleAddRequest = new Vehicle();
			vehicleAddRequest.purchaseDate = purchaseDate;
			vehicleAddRequest.vehIdentificationNo = vin;
			String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.buttonAddVehicle.click();
			vehicleTab.getAssetList().getAsset(VIN).setValue(vin);
			vehicleTab.getAssetList().getAsset(USAGE).setValue("Pleasure");
			vehicleTab.saveAndExit();

			ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.UNIQUE_VIN.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.UNIQUE_VIN.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("attributeForRules");

			rateEndorsementInPas(policyNumber);
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.UNIQUE_VIN.getCode(), ErrorDxpEnum.Errors.UNIQUE_VIN.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			policy.updateRulesOverride().start();
			CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200031").getCell(1)).isPresent();
			UpdateRulesOverrideActionTab.btnCancel.click();
		});
	}

	protected void pas12852_GaragedOutOfStateOnlyOneVeh200018Body() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String originalVehicle = responseViewVehicles.vehicleList.get(0).oid;

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String purchaseDate = "2013-02-22";
			String vin = "1HGFA16526L081415";
			Vehicle vehicleAddRequest = new Vehicle();
			vehicleAddRequest.purchaseDate = purchaseDate;
			vehicleAddRequest.vehIdentificationNo = vin;
			String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

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

			VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicle,VehicleUpdateResponseDto.class, Response.Status.OK.getStatusCode());
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(originalVehicle);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");

			ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE_ONLY_VEHICLE.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE_ONLY_VEHICLE.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("attributeForRules");
			softly.assertThat(rateResponse.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode());
			softly.assertThat(rateResponse.errors.get(1).message).contains(ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage());
			softly.assertThat(rateResponse.errors.get(1).field).isEqualTo("attributeForRules");

			rateEndorsementInPas(policyNumber);
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE_ONLY_VEHICLE.getCode(), ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE_ONLY_VEHICLE.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			policy.updateRulesOverride().start();
			CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200018").getCell(1)).isPresent();
			UpdateRulesOverrideActionTab.btnCancel.click();
		});
	}

	protected void pas12852_MustHavePPA200016Body(TestData motorHomeData) {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String originalVehicleOid = responseViewVehicles.vehicleList.get(0).oid;

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			vehicleTab.fillTab(motorHomeData);
			premiumAndCoveragesTab.calculatePremium();
			premiumAndCoveragesTab.saveAndExit();
			//TODO Adding below steps makes PAS-15483 non-reproducible
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//TODO uncomment for scenario, when deleteVehicles returns proper response
			/*String purchaseDate2 = "2013-02-22";
			String vin2 = "WAUKJAFM8C6314628";
			Vehicle vehicleAddRequest2 = new Vehicle();
			vehicleAddRequest2.purchaseDate = purchaseDate2;
			vehicleAddRequest2.vehIdentificationNo = vin2;
			String newVehicleOid2 = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest2);
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid2);*/

			ErrorResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicleOid, ErrorResponseDto.class, 422);

			softly.assertThat(deleteVehicleResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.VEHICLE_CANNOT_BE_REMOVED_ERROR.getCode());
			softly.assertThat(deleteVehicleResponse.message).isEqualTo(ErrorDxpEnum.Errors.VEHICLE_CANNOT_BE_REMOVED_ERROR.getMessage());

			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.tableVehicleList.getRow(1).getCell(5).controls.links.get(2).click();
			Page.dialogConfirmation.confirm();
			premiumAndCoveragesTab.calculatePremium();
			premiumAndCoveragesTab.saveAndExit();

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.MUST_HAVE_PPA.getCode(), ErrorDxpEnum.Errors.MUST_HAVE_PPA.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			policy.updateRulesOverride().start();
			CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200016").getCell(1)).isPresent();
			UpdateRulesOverrideActionTab.btnCancel.click();
		});
	}

	private void rateEndorsementInPas (String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
	}
}
