package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.USAGE;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.VIN;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
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
	 * @scenario 1. Create a policy
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

			//can't even rate policy with vehicle Garaged in MI, ZIP is not resolved
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.ZIP_CODE_IS_NOT_APPLICABLE.getCode(), ErrorDxpEnum.Errors.ZIP_CODE_IS_NOT_APPLICABLE.getMessage(), "postalCode");
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

			helperMiniServices.rateEndorsementWithCheck(softly, policyNumber);

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode(), ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();
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

			helperMiniServices.rateEndorsementWithCheck(softly, policyNumber);

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.UNIQUE_VIN.getCode(), ErrorDxpEnum.Errors.UNIQUE_VIN.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			policy.updateRulesOverride().start();
			CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200031").getCell(1)).isPresent();
			UpdateRulesOverrideActionTab.btnCancel.click();
		});
	}

	protected void pas12852_ExpensiveVehicle200022Body() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String vin = "ZFFCW56A830133118";
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.buttonAddVehicle.click();
			vehicleTab.getAssetList().getAsset(VIN).setValue(vin);
			vehicleTab.getAssetList().getAsset(USAGE).setValue("Pleasure");
			vehicleTab.saveAndExit();

			helperMiniServices.rateEndorsementWithCheck(softly, policyNumber);

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.EXPENSIVE_VEHICLE.getCode(), ErrorDxpEnum.Errors.EXPENSIVE_VEHICLE.getMessage(), "vehTypeCd");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			policy.updateRulesOverride().start();
			CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200022").getCell(1)).isPresent();
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

			VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicle);
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(originalVehicle);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");

			//BUG PAS-15481 Rating through service fails, when New Vehicle is added through Service and Original Vehicle removed
			helperMiniServices.rateEndorsementWithCheck(softly, policyNumber);

			//TODO workaround for failed to rate Endorsement
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			//BUG  PAS-15482 Bind through service fails, when New Vehicle is added through Service and Original Vehicle removed
			//TODO workaround for failed issue Endorsement
			//NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			premiumAndCoveragesTab.calculatePremium();
			premiumAndCoveragesTab.saveAndExit();

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE_ONLY_VEHICLE.getCode(), ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE_ONLY_VEHICLE.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			policy.updateRulesOverride().start();
			CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200018").getCell(1)).isPresent();
			UpdateRulesOverrideActionTab.btnCancel.click();
		});
	}

	protected void pas12852_MustHavePPA200016Body(TestData motorhomeData) {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();
			//String policyNumber = "VASS952918552";
			ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String originalVehicleOid = responseViewVehicles.vehicleList.get(0).oid;

			helperMiniServices.createEndorsementWithCheck(policyNumber);

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
		String newVehicleOid2 = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest2);
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid2);*/

			VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicleOid);
			//BUG PAS-15483 Delete Vehicle doesnt return response in some cases
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(originalVehicleOid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");

			helperMiniServices.rateEndorsementWithCheck(softly, policyNumber);

			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.MUST_HAVE_PPA.getCode(), ErrorDxpEnum.Errors.MUST_HAVE_PPA.getMessage(), "attributeForRules");

			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			policy.updateRulesOverride().start();
			CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200016").getCell(1)).isPresent();
			UpdateRulesOverrideActionTab.btnCancel.click();
		});
	}
}
