package aaa.modules.regression.sales.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.PersonalUmbrellaTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksOtherVehiclesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.DoubleTextBox;
import toolkit.webdriver.controls.TextBox;

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
	
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testCoverLimitsValidation(@Optional("CA") String state) {
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
			assertThat(autoTab.getAutomobilesAssetList().getWarning(Automobiles.COMBINED_SINGLE_LIMIT)).valueContains(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			assertThat(autoTab.getAutomobilesAssetList().getWarning(Automobiles.COMBINED_SINGLE_LIMIT)).valueContains("");
			break;
		case MOTORCYCLE:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorcyclesAssetList().fill(tdSpecific);
			changeSinglelimit(vehType, sLimitIncorrect);
			assertThat(autoTab.getMotorcyclesAssetList().getWarning(Motorcycles.COMBINED_SINGLE_LIMIT)).valueContains(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			assertThat(autoTab.getMotorcyclesAssetList().getWarning(Motorcycles.COMBINED_SINGLE_LIMIT)).valueContains("");
			break;
		case MOTORHOME:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorHomesAssetList().fill(tdSpecific);
			changeSinglelimit(vehType, sLimitIncorrect);
			assertThat(autoTab.getMotorHomesAssetList().getWarning(MotorHomes.COMBINED_SINGLE_LIMIT)).valueContains(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			assertThat(autoTab.getMotorHomesAssetList().getWarning(MotorHomes.COMBINED_SINGLE_LIMIT)).valueContains("");
			break;
		case RECREATIONAL:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_OTHER_VEHICLES.get());
			otherVehiclesTab.getRecreationalVehicleAssetList().fill(tdSpecific);
			changeSinglelimit(vehType, sLimitIncorrect);
			assertThat(otherVehiclesTab.getRecreationalVehicleAssetList().getWarning(RecreationalVehicle.COMBINED_SINGLE_LIMIT)).valueContains(sLimitErrorMsgExpected);
			changeSinglelimit(vehType, sLimitCorrect);
			assertThat(otherVehiclesTab.getRecreationalVehicleAssetList().getWarning(RecreationalVehicle.COMBINED_SINGLE_LIMIT)).valueContains("");
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

		ErrorEnum.Errors biLimitErrorMsgExpected1 = null;
		ErrorEnum.Errors biLimitErrorMsgExpected2 = null;
		ErrorEnum.Errors biLimitErrorMsgExpected3 = null;
		ErrorEnum.Errors biLimitErrorMsgExpectedCA1 = null;
		ErrorEnum.Errors biLimitErrorMsgExpectedCA2 = null;
		switch (vehType) {
		case AUTOMOBILE:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getAutomobilesAssetList().fill(tdSpecific);
			biLimitErrorMsgExpected1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240324;
			biLimitErrorMsgExpected2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240323;
			biLimitErrorMsgExpected3 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4290091;
			biLimitErrorMsgExpectedCA1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240324_CA;
			biLimitErrorMsgExpectedCA2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240323_CA;
			break;
		case MOTORCYCLE:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorcyclesAssetList().fill(tdSpecific);
			biLimitErrorMsgExpected1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240324;
			biLimitErrorMsgExpected2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240323;
			biLimitErrorMsgExpected3 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4290091;
			biLimitErrorMsgExpectedCA1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240324_CA;
			biLimitErrorMsgExpectedCA2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4240323_CA;
			break;
		case MOTORHOME:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
			autoTab.getMotorHomesAssetList().fill(tdSpecific);
			biLimitErrorMsgExpected1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4241939;
			biLimitErrorMsgExpected2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4241938;
			biLimitErrorMsgExpected3 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4244182;
			biLimitErrorMsgExpectedCA1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4241939_CA;
			biLimitErrorMsgExpectedCA2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4241938_CA;
			break;
		case RECREATIONAL:
			NavigationPage.toViewSubTab(PersonalUmbrellaTab.UNDERLYING_RISKS_OTHER_VEHICLES.get());
			otherVehiclesTab.getRecreationalVehicleAssetList().fill(tdSpecific);
			biLimitErrorMsgExpected1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4243801;
			biLimitErrorMsgExpected2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4243800;
			biLimitErrorMsgExpected3 = ErrorEnum.Errors.ERROR_AAA_PUP_SS4243200;
			biLimitErrorMsgExpectedCA1 = ErrorEnum.Errors.ERROR_AAA_PUP_SS3415672;
			biLimitErrorMsgExpectedCA2 = ErrorEnum.Errors.ERROR_AAA_PUP_SS3415671;
			break;
		}
		if (!getState().equals("CA")) {
			changeSplitLimits(vehType, bILimitFirstCorrect, bILimitSecondIncorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpected1);
			changeSplitLimits(vehType, bILimitFirstIncorrect, bILimitSecondCorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpected2);
			changeSplitLimits(vehType, bILimitFirstCorrect, bILimitSecondCorrect, pDLimitIncorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpected3);
		} else {
			changeSplitLimits(vehType, bILimitFirstCorrectCA, bILimitSecondIncorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpectedCA1);
			changeSplitLimits(vehType, bILimitFirstIncorrectCA, bILimitSecondCorrect, pDLimitCorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpectedCA2);
			changeSplitLimits(vehType, bILimitFirstCorrectCA, bILimitSecondCorrect, pDLimitIncorrect);
			checkErrorMsg(vehType, biLimitErrorMsgExpected3);
		}
	}
	
	private void checkErrorMsg(VehicleType vehType, ErrorEnum.Errors... errorMessages) {
		ErrorTab errorTab = new ErrorTab();
		switch (vehType) {
		case AUTOMOBILE:
			autoTab.getAutomobilesAssetList().getAsset(Automobiles.ADD).click();
			break;
		case MOTORCYCLE:
			autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.ADD).click();
			break;
		case MOTORHOME:
			autoTab.getMotorHomesAssetList().getAsset(MotorHomes.ADD).click();
			break;
		case RECREATIONAL:
			otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.ADD).click();
			break;
		}
		errorTab.verify.errorsPresent(errorMessages);
		errorTab.cancel();
	}

	private void changeSinglelimit(VehicleType vehType, String singleLimit) {
		TextBox sLimit;
		ComboBox coverageType;
		switch (vehType) {
		case AUTOMOBILE:
			coverageType = autoTab.getAutomobilesAssetList().getAsset(Automobiles.CAR_TYPE);
			coverageType.setValue("Single");
			sLimit = autoTab.getAutomobilesAssetList().getAsset(Automobiles.COMBINED_SINGLE_LIMIT);
			sLimit.setValue(singleLimit);
			break;
		case MOTORCYCLE:
			coverageType = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.COVERAGE_TYPE);
			coverageType.setValue("Single");
			sLimit = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.COMBINED_SINGLE_LIMIT);
			sLimit.setValue(singleLimit);
			break;
		case MOTORHOME:
			coverageType = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.COVERAGE_TYPE);
			coverageType.setValue("Single");
			sLimit = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.COMBINED_SINGLE_LIMIT);
			sLimit.setValue(singleLimit);
			break;
		case RECREATIONAL:
			coverageType = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.COVERAGE_TYPE);
			coverageType.setValue("Single");
			sLimit = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.COMBINED_SINGLE_LIMIT);
			sLimit.setValue(singleLimit);
			break;
		}
	}

	private void changeSplitLimits(VehicleType vehType, String biLimFirst, String biLimSec, String pdLim) {

		DoubleTextBox biLimit = null;
		TextBox pdLimits = null;

		switch (vehType) {
		case AUTOMOBILE:
			biLimit = autoTab.getAutomobilesAssetList().getAsset(Automobiles.BI_LIMITS);
			pdLimits = autoTab.getAutomobilesAssetList().getAsset(Automobiles.PD_LIMITS);
			break;
		case MOTORCYCLE:
			biLimit = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.BI_LIMITS);
			pdLimits = autoTab.getMotorcyclesAssetList().getAsset(Motorcycles.PD_LIMITS);
			break;
		case MOTORHOME:
			biLimit = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.BI_LIMITS);
			pdLimits = autoTab.getMotorHomesAssetList().getAsset(MotorHomes.PD_LIMITS);
			break;
		case RECREATIONAL:
			biLimit = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.BI_LIMITS);
			pdLimits = otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(RecreationalVehicle.PD_LIMITS);
			break;
		}

		biLimit.setValue(Arrays.asList(biLimFirst, biLimSec));
		pdLimits.setValue(pdLim);
	}
	
	private enum VehicleType {
		AUTOMOBILE, MOTORCYCLE, MOTORHOME, RECREATIONAL
	}
}
