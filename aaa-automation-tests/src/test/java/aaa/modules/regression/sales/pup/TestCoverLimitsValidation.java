package aaa.modules.regression.sales.pup;

import java.util.Arrays;
import java.util.Map;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.DoubleTextBox;
import toolkit.webdriver.controls.TextBox;
import aaa.common.Tab;
import aaa.common.enums.ErrorPageEnum.ErrorsColumn;
import aaa.common.enums.NavigationEnum.PersonalUmbrellaTab;
import aaa.common.pages.ErrorPage;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksOtherVehiclesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

/**
 * @author Ryan Yu
 * Objectives : Validate BI/PD/CSL limits.
 * <pre>
 * TC Steps:
 * 1. Create new or open existent Customer;
 * 2. Initiate Pup quote creation.
 * 3. Fill Prefill and General tabs, 
 * 4. Navigate to Underlying Risks tab - > Auto,
 * 
 * 5. Verify a limit for single type coverage for Automobile
 *        Add Automobile.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Single"
 *        Set wrong value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check error message appears.
 *        Set right value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check that vehicle was added (remove it)
 *        
 * 6. Verify a limit for single type coverage for Motorcycle
 *        Add Motorcycle.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Single"
 *        Set wrong value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check error message appears.
 *        Set right value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check that vehicle was added (remove it)
 *        
 * 7. Verify a limit for single type coverage for MotorHome
 *        Add MotorHome.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Single"
 *        Set wrong value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check error message appears.
 *        Set right value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check that vehicle was added (remove it) 
 *        
 * 8. Verify a limit for single type coverage for Recreational vehicle
 *        Navigate to Underlying Risks tab - > Other Vehicles,
 *        Add Recreational vehicle.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Single"
 *        Set wrong value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check error message appears.
 *        Set right value for "Combined Single Limit"
 *        Press "Add" button.
 *        Check that vehicle was added (remove it) 
 *
 * 9. Verify a coverage limits for split type coverage for Automobile
 *        Add Automobile.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Split"
 *        Set wrong value for "First BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "Second BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "PD Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set correct values for all limits
 *        Press "Add" button.
 *        Check that vehicle was added (remove it)
 *        
 * 10. Verify a coverage limits for split type coverage for Motorcycle
 *        Add Motorcycle.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Split"
 *        Set wrong value for "First BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "Second BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "PD Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set correct values for all limits
 *        Press "Add" button.
 *        Check that vehicle was added (remove it)
 *        
 * 11. Verify a coverage limits for split type coverage for Motorhome
 *        Add Motorhome.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Split"
 *        Set wrong value for "First BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "Second BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "PD Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set correct values for all limits
 *        Press "Add" button.
 *        Check that vehicle was added (remove it) 
 *        
 * 12. Verify a coverage limits for split type coverage for Recreational vehicle
 *        Navigate to Underlying Risks tab - > Other Vehicles
 *        Add Recreational vehicle.
 *        Fill all mandatory fields.
 *        Set "Coverage Type" to "Split"
 *        Set wrong value for "First BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "Second BI Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set wrong value for "PD Limit" all the rest limits set with correct values
 *        Press "Add" button.
 *        Check error message appears.
 *        Set correct values for all limits
 *        Press "Add" button.
 *        Check that vehicle was added (remove it)       
 *       
 *  // Stories
 *  14337 - US : PUP: Validate BI/PD/CSL limits.
 *  18256:US CA PUP Capture Motorcycle Information
 *  18251:US CA PUP Capture Motorhome Information
 *  18337:US CA PUP-Capture Recreational Vehicles (Off-road) Information
 *  21644:US CA PUP Capture Private Passenger auto Information v2.0
 * </pre>
 **/
public class TestCoverLimitsValidation  extends PersonalUmbrellaBaseTest {
	private PrefillTab prefillTab = policy.getDefaultView().getTab(PrefillTab.class);
	private UnderlyingRisksAutoTab autoTab = policy.getDefaultView().getTab(UnderlyingRisksAutoTab.class);
	private UnderlyingRisksOtherVehiclesTab otherVehiclesTab = policy.getDefaultView().getTab(UnderlyingRisksOtherVehiclesTab.class);
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testCoverLimitsValidation() {
		mainApp().open();

		// 1 ~ 4
		createCustomerIndividual();
		Map<String, String> primaryPolicies = getPrimaryPoliciesForPup();
		TestData td = prefillTab.adjustWithRealPolicies(getPolicyTD(), primaryPolicies);
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, UnderlyingRisksAutoTab.class);

		// 5
//		verifySingleTypeCoverageLimit(VehicleType.AUTOMOBILE);
		// 6
		verifySingleTypeCoverageLimit(VehicleType.MOTORCYCLE);
		// 7
		verifySingleTypeCoverageLimit(VehicleType.MOTORHOME);
		// 8
		verifySingleTypeCoverageLimit(VehicleType.RECREATIONAL);

		// 9
//		verifySplitTypeCoverageLimits(VehicleType.AUTOMOBILE);
		// 10
		verifySplitTypeCoverageLimits(VehicleType.MOTORCYCLE);
		// 11
		verifySplitTypeCoverageLimits(VehicleType.MOTORHOME);
		// 12
		verifySplitTypeCoverageLimits(VehicleType.RECREATIONAL);
		Tab.buttonSaveAndExit.click();
		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("==========================================");
		log.info(getState() + " Limits were tested. Quote: " + quoteNumber);
		log.info("==========================================");

	}

	private void verifySingleTypeCoverageLimit(VehicleType vehType) {
		TestData tdSpecific = getTestSpecificTD("TestData");
		String sLimitCorrect = "$500,000";
		String sLimitIncorrect = "$490,000";
		String sLimitErrorMsgExpected = "Combined Single Limit should not be less than $500,000.";

		switch (vehType) {
		case AUTOMOBILE:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getAutomobilesAssetList().fill(tdSpecific);
			changeSinglelimit(vehType, sLimitIncorrect);
			autoTab.getAutomobilesAssetList().getWarning(Automobiles.COMBINED_SINGLE_LIMIT.getLabel()).verify.value(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			autoTab.getAutomobilesAssetList().getWarning(Automobiles.COMBINED_SINGLE_LIMIT.getLabel()).verify.value("");
			break;
		case MOTORCYCLE:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorcyclesAssetList().fill(tdSpecific);
			changeSinglelimit(vehType, sLimitIncorrect);
			autoTab.getMotorcyclesAssetList().getWarning(Motorcycles.COMBINED_SINGLE_LIMIT.getLabel()).verify.value(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			autoTab.getMotorcyclesAssetList().getWarning(Motorcycles.COMBINED_SINGLE_LIMIT.getLabel()).verify.value("");
			break;
		case MOTORHOME:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorHomesAssetList().fill(tdSpecific);
			changeSinglelimit(vehType, sLimitIncorrect);
			autoTab.getMotorHomesAssetList().getWarning(MotorHomes.COMBINED_SINGLE_LIMIT.getLabel()).verify.value(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			autoTab.getMotorHomesAssetList().getWarning(MotorHomes.COMBINED_SINGLE_LIMIT.getLabel()).verify.value("");
			break;
		case RECREATIONAL:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_OTHER_VEHICLES.get());
			otherVehiclesTab.getRecreationalVehicleAssetList().fill(tdSpecific);
			changeSinglelimit(vehType, sLimitIncorrect);
			otherVehiclesTab.getRecreationalVehicleAssetList().getWarning(RecreationalVehicle.COMBINED_SINGLE_LIMIT.getLabel()).verify.value(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			otherVehiclesTab.getRecreationalVehicleAssetList().getWarning(RecreationalVehicle.COMBINED_SINGLE_LIMIT.getLabel()).verify.value("");
			break;
		}
	}
	
	private void verifySplitTypeCoverageLimits(VehicleType vehType) {
		TestData tdSpecific = getTestSpecificTD("TestData");
		String bILimitFirstCorrect = "$250,000";
		String bILimitFirstCorrectCA = "$500,000";
		String bILimitSecondCorrect = "$500,000";
		String bILimitFirstIncorrect = "$240,000";
		String bILimitFirstIncorrectCA = "$490,000";
		String bILimitSecondIncorrect = "$490,000";

		String pDLimitCorrect = "100,000";
		String pDLimitIncorrect = "90,000";

		String biLimitErrorMsgExpected = "BI Limits should not be less than $250,000/500,000";
		String biLimitErrorMsgExpectedCA = null;
		String _biLimitErrorMsgExpectedCA = "BI Limits should not be less than $500,000/500,000";
		String _biLimitErrorMsgExpectedCARecrV = "BI Limit should not be less than $500,000/500,000";
		String pdLimitErrorMsgExpected = "PD Limits should not be less than $100,000";

		switch (vehType) {
		case AUTOMOBILE:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getAutomobilesAssetList().fill(tdSpecific);
			biLimitErrorMsgExpectedCA = _biLimitErrorMsgExpectedCA;
			break;
		case MOTORCYCLE:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorcyclesAssetList().fill(tdSpecific);
			biLimitErrorMsgExpectedCA = _biLimitErrorMsgExpectedCA;
			break;
		case MOTORHOME:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorHomesAssetList().fill(tdSpecific);
			biLimitErrorMsgExpectedCA = _biLimitErrorMsgExpectedCA;
			break;
		case RECREATIONAL:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_OTHER_VEHICLES.get());
			otherVehiclesTab.getRecreationalVehicleAssetList().fill(tdSpecific);
			biLimitErrorMsgExpectedCA = _biLimitErrorMsgExpectedCARecrV;
			break;
		}
		if (!getState().equals("CA")) {
			changeSplitLimits(vehType, bILimitFirstCorrect, bILimitSecondIncorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpected);
			changeSplitLimits(vehType, bILimitFirstIncorrect, bILimitSecondCorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpected);
			changeSplitLimits(vehType, bILimitFirstCorrect, bILimitSecondCorrect, pDLimitIncorrect);
			checkErrorMsg(vehType, pdLimitErrorMsgExpected);
		} else {
			changeSplitLimits(vehType, bILimitFirstCorrectCA, bILimitSecondIncorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpectedCA);
			changeSplitLimits(vehType, bILimitFirstIncorrectCA, bILimitSecondCorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpectedCA);
			changeSplitLimits(vehType, bILimitFirstCorrectCA, bILimitSecondCorrect, pDLimitIncorrect);
			checkErrorMsg(vehType, pdLimitErrorMsgExpected);
		}
	}
	
	private void checkErrorMsg(VehicleType vehType, String errorMsg) {
		switch (vehType) {
		case AUTOMOBILE:
			autoTab.getAutomobilesAssetList().getAsset(Automobiles.ADD.getLabel(), Button.class).click();
			break;
		case MOTORCYCLE:
			autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.ADD.getLabel(), Button.class).click();
			break;
		case MOTORHOME:
			autoTab.getMotorHomesAssetList().getAsset(MotorHomes.ADD.getLabel(), Button.class).click();
			break;
		case RECREATIONAL:
			otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.ADD.getLabel(), Button.class).click();
			break;
		}
		ErrorPage.tableError.getRowContains(ErrorsColumn.MESSAGE.get(), errorMsg).verify.present();
		ErrorPage.buttonCancel.click();
	}

	private void changeSinglelimit(VehicleType vehType, String singleLimit) {
		TextBox sLimit = null;
		ComboBox coverageType = null;
		switch (vehType) {
		case AUTOMOBILE:
			coverageType = autoTab.getAutomobilesAssetList().getAsset(Automobiles.CAR_TYPE.getLabel(), ComboBox.class);
			coverageType.setValue("Single");
			sLimit = autoTab.getAutomobilesAssetList().getAsset(Automobiles.COMBINED_SINGLE_LIMIT.getLabel(), TextBox.class);
			sLimit.setValue(singleLimit);
			break;
		case MOTORCYCLE:
			coverageType = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.COVERAGE_TYPE.getLabel(), ComboBox.class);
			coverageType.setValue("Single");
			sLimit = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.COMBINED_SINGLE_LIMIT.getLabel(), TextBox.class);
			sLimit.setValue(singleLimit);
			break;
		case MOTORHOME:
			coverageType = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.COVERAGE_TYPE.getLabel(), ComboBox.class);
			coverageType.setValue("Single");
			sLimit = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.COMBINED_SINGLE_LIMIT.getLabel(), TextBox.class);
			sLimit.setValue(singleLimit);
			break;
		case RECREATIONAL:
			coverageType = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.COVERAGE_TYPE.getLabel(), ComboBox.class);
			coverageType.setValue("Single");
			sLimit = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.COMBINED_SINGLE_LIMIT.getLabel(), TextBox.class);
			sLimit.setValue(singleLimit);
			break;
		}
	}

	private void changeSplitLimits(VehicleType vehType, String biLimFirst, String biLimSec, String pdLim) {

		DoubleTextBox biLimit = null;
		TextBox pdLimits = null;

		switch (vehType) {
		case AUTOMOBILE:
			biLimit = autoTab.getAutomobilesAssetList().getAsset(Automobiles.BI_LIMITS.getLabel(), DoubleTextBox.class);
			pdLimits = autoTab.getAutomobilesAssetList().getAsset(Automobiles.PD_LIMITS.getLabel(), TextBox.class);
			break;
		case MOTORCYCLE:
			biLimit = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.BI_LIMITS.getLabel(), DoubleTextBox.class);
			pdLimits = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.PD_LIMITS.getLabel(), TextBox.class);
			break;
		case MOTORHOME:
			biLimit = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.BI_LIMITS.getLabel(), DoubleTextBox.class);
			pdLimits = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.PD_LIMITS.getLabel(), TextBox.class);
			break;
		case RECREATIONAL:
			biLimit = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.BI_LIMITS.getLabel(), DoubleTextBox.class);
			pdLimits = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.PD_LIMITS.getLabel(), TextBox.class);
			break;
		}

		biLimit.setValue(Arrays.asList(biLimFirst, biLimSec));
		pdLimits.setValue(pdLim);
	}
	
	private enum VehicleType {
		AUTOMOBILE, MOTORCYCLE, MOTORHOME, RECREATIONAL
	}
}
