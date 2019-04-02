package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.main.enums.PolicyConstants.InspectionOrWaiverRequiredSection.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableList;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.AdvancedRadioGroup;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestCARCOInspectionOrWaiverRequiredSection extends AutoSSBaseTest {
	private final VehicleTab vehicleTab = new VehicleTab();
	private final PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final PurchaseTab purchaseTab = new PurchaseTab();
	private final ErrorTab errorTab = new ErrorTab();
	private static final List<String> CARCO_RADIOGROUP_OPTIONS = ImmutableList.of(INSPECTION_RECEIVED, SALES_AGREEMENT_RECEIVED, PRIOR_DECLARATION_RECEIVED, NO_DOCUMENT_RECEIVED);

	/**
	 * @author Maris Strazds
	 * @name Test CARCO 'Inspection Or Waiver Required' Section in Documents and Bind Tab
	 * @scenario
	 * 1. Initiate quote, add CARCO qualifying/not qualifying vehicles and check 'Inspection Or Waiver Required' Section in Documents and Bind Tab
	 * 2. Bind Quote
	 * 3. Initiate Endorsement and add CARCO qualifying/not qualifying vehicles, update existing vehicles and check 'Inspection Or Waiver Required' Section in Documents and Bind Tab
	 * @NOTE CARCO Qualifying vehicle is vehicle <= 7 years old. (such vehicles has field "Less Than 1,000 Miles")
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-26666", "PAS-26664", "PAS-26665", "PAS-27606", "PAS-27615", "PAS-27828"})
	public void pas26664_CARCOInspectionOrWaiverRequiredSection(@Optional("NJ") String state) {
		TestData td = getPolicyDefaultTD();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AddMultipleVewhicleLessThan7YearsOld").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_INSURANCE_INSPECTION.getLabel()), "Physically Signed");
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.INSPECTION_WAIVER_SALES_AGREEMENT_REQUIRED.getLabel()), "Physically Signed");

		createQuoteAndFillUpTo(td, PremiumAndCoveragesTab.class, true);
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "No Coverage");//2017, TOYOTA, CAMRY
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(4, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "No Coverage");////2017, AUDI, A6
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.submitTab();
		policy.getDefaultView().fillFromTo(td, DriverActivityReportsTab.class, DocumentsAndBindTab.class, false);

		verifyCARCOVehicle("1", "2017, AUDI, TTS", NO_DOCUMENT_RECEIVED, false, false, false);
		verifyCARCOVehicle("2", "2017, CHEVROLET, CAMARO", NO_DOCUMENT_RECEIVED, true, false, false);
		verifyCARCOVehicle("3", "2017, MINI, COOPER", NO_DOCUMENT_RECEIVED, true, false, false);
		verifyCARCOVehicle("4", "2017, NISSAN, PATHFINDER", NO_DOCUMENT_RECEIVED, false, false, false);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5)).isPresent(false);

		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_1).setValue(CARCO_RADIOGROUP_OPTIONS.get(2));
		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_2).setValue(CARCO_RADIOGROUP_OPTIONS.get(1));
		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_3).setValue(CARCO_RADIOGROUP_OPTIONS.get(0));

		//Make sure that after selecting Agreement, 'Inspection or Waiver Required' section is not reset (PAS-27828)
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(3);//2017, AUDI, TTS
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		VehicleTab.tableVehicleList.selectRow(5);//2017, CHEVROLET, CAMARO
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		VehicleTab.tableVehicleList.selectRow(6);//2017, MINI, COOPER
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		VehicleTab.tableVehicleList.selectRow(7);//2017, NISSAN, PATHFINDER
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		verifyCARCOVehicle("1", "2017, AUDI, TTS", PRIOR_DECLARATION_RECEIVED, true, false, false);
		verifyCARCOVehicle("2", "2017, CHEVROLET, CAMARO", NO_DOCUMENT_RECEIVED, false, false, false);
		verifyCARCOVehicle("3", "2017, MINI, COOPER", INSPECTION_RECEIVED, false, false, false);
		verifyCARCOVehicle("4", "2017, NISSAN, PATHFINDER", NO_DOCUMENT_RECEIVED, true, false, false);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5)).isPresent(false);
		policy.getDefaultView().fillFromTo(td, DocumentsAndBindTab.class, PurchaseTab.class, true);
		purchaseTab.submitTab();

		//Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		//Endorsement AC#5
		verifyCARCOVehicle("1", "2017, AUDI, TTS", PRIOR_DECLARATION_RECEIVED, true, true, true);
		verifyCARCOVehicle("2", "2017, CHEVROLET, CAMARO", NO_DOCUMENT_RECEIVED, false, true, false);
		verifyCARCOVehicle("3", "2017, MINI, COOPER", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("4", "2017, NISSAN, PATHFINDER", NO_DOCUMENT_RECEIVED, true, true, false);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5)).isPresent(false);

		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_2).setValue(CARCO_RADIOGROUP_OPTIONS.get(0));//2017, CHEVROLET, CAMARO
		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_4).setValue(CARCO_RADIOGROUP_OPTIONS.get(1));//2017, NISSAN, PATHFINDER
		documentsAndBindTab.submitTab();

		//Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		verifyCARCOVehicle("1", "2017, AUDI, TTS", PRIOR_DECLARATION_RECEIVED, true, true, true);
		verifyCARCOVehicle("2", "2017, CHEVROLET, CAMARO", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("3", "2017, MINI, COOPER", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("4", "2017, NISSAN, PATHFINDER", SALES_AGREEMENT_RECEIVED, true, true, true);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5)).isPresent(false);

		//ENDORSEMENT AC#1 - I add a qualifying vehicle OR I add comp and/or coll to an existing vehicle that makes it qualifying with < 1000 miles = Yes (Comp)
		//ENDORSEMENT AC#2 - I add a qualifying vehicle OR I add comp and/or coll to an existing vehicle that makes it qualifying with < 1000 miles = No (Comp)
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "$1,000");//2017, TOYOTA, CAMRY
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(4, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "$1,000");//2017, AUDI, A6
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		verifyCARCOVehicle("1", "2017, TOYOTA, CAMRY", NO_DOCUMENT_RECEIVED, false, true, false);//new
		verifyCARCOVehicle("2", "2017, AUDI, TTS", PRIOR_DECLARATION_RECEIVED, true, true, true);
		verifyCARCOVehicle("3", "2017, AUDI, A6", NO_DOCUMENT_RECEIVED, true, true, false);//new
		verifyCARCOVehicle("4", "2017, CHEVROLET, CAMARO", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("5", "2017, MINI, COOPER", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("6", "2017, NISSAN, PATHFINDER", SALES_AGREEMENT_RECEIVED, true, true, true);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_7)).isPresent(false);

		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_1).setValue(CARCO_RADIOGROUP_OPTIONS.get(0));
		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_3).setValue(CARCO_RADIOGROUP_OPTIONS.get(1));

		//ENDORSEMENT AC#4 - change from yes to no
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);//2017, TOYOTA, CAMRY
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		VehicleTab.tableVehicleList.selectRow(3);//2017, AUDI, TTS
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		VehicleTab.tableVehicleList.selectRow(4);//2017, AUDI, A6
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		VehicleTab.tableVehicleList.selectRow(5);//2017, CHEVROLET, CAMARO
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		VehicleTab.tableVehicleList.selectRow(6);//2017, MINI, COOPER
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		VehicleTab.tableVehicleList.selectRow(7);//2017, NISSAN, PATHFINDER
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		verifyCARCOVehicle("1", "2017, TOYOTA, CAMRY", INSPECTION_RECEIVED, true, true, false);//new
		verifyCARCOVehicle("2", "2017, AUDI, TTS", PRIOR_DECLARATION_RECEIVED, true, true, true);
		verifyCARCOVehicle("3", "2017, AUDI, A6", NO_DOCUMENT_RECEIVED, false, true, false);//new
		verifyCARCOVehicle("4", "2017, CHEVROLET, CAMARO", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("5", "2017, MINI, COOPER", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("6", "2017, NISSAN, PATHFINDER", SALES_AGREEMENT_RECEIVED, true, true, true);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_7)).isPresent(false);
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_INSURANCE_INSPECTION).setValue("Physically Signed");//to not get error on bind
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.INSPECTION_WAIVER_SALES_AGREEMENT_REQUIRED).setValue("Physically Signed");//to not get error on bind
		documentsAndBindTab.submitTab();

		//Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		verifyCARCOVehicle("1", "2017, TOYOTA, CAMRY", INSPECTION_RECEIVED, true, true, true);//new
		verifyCARCOVehicle("2", "2017, AUDI, TTS", PRIOR_DECLARATION_RECEIVED, true, true, true);
		verifyCARCOVehicle("3", "2017, AUDI, A6", NO_DOCUMENT_RECEIVED, false, true, false);//new
		verifyCARCOVehicle("4", "2017, CHEVROLET, CAMARO", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("5", "2017, MINI, COOPER", INSPECTION_RECEIVED, false, true, true);
		verifyCARCOVehicle("6", "2017, NISSAN, PATHFINDER", SALES_AGREEMENT_RECEIVED, true, true, true);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_7)).isPresent(false);

		//ENDORSEMENT AC#3 - I remove or replace (with not qualifying) vehicle that is on the CARCO table
		//replace 2017, TOYOTA, CAMRY with 2006 Honda Accord (not qualifying vehicle)
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().
				getRow(2).getCell(5).controls.links.get("Replace").click();
		Page.dialogConfirmation.confirm();
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue("3HGCM56476GT10709");//2006 Honda Accord

		//replace 2017, AUDI, TTS with 2017 FIAT 500 3C3CFFKR0HT699997 (qualifying vehicle, less than 1,000 miles = no)
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().
				getRow(3).getCell(5).controls.links.get("Replace").click();
		Page.dialogConfirmation.confirm();
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue("3C3CFFKR0HT699997");//2017 FIAT 500
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		//replace 2017, AUDI, A6 with Ford Explorer 1FM5K7F86HGC68684 (qualifying vehicle, less than 1,000 miles = Yes)
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().
				getRow(4).getCell(5).controls.links.get("Replace").click();
		Page.dialogConfirmation.confirm();
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue("1FM5K7F86HGC68684");//Ford Explorer
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		//remove other vehicles
		int vehiclesToRemoveCount = 3;
		while (vehiclesToRemoveCount > 0) {
			vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LIST_OF_VEHICLE).getTable().
					getRow(5).getCell(5).controls.links.get("Remove").click();
			Page.dialogConfirmation.confirm();
			vehiclesToRemoveCount--;
		}

		//Add qualifying vehicle 2017, SUBARU, CROSSTREK  (Less than 1,000 miles = No) (Endorsement)
		//Add qualifying vehicle 2017, BUICK, ENCORE (Less than 1,000 miles = Yes) (Endorsement)
		TestData tdAddVehiclesEndorsement = getTestSpecificTD("TestData_addVehiclesEndorsement");
		policy.getDefaultView().fillFromTo(tdAddVehiclesEndorsement, VehicleTab.class, VehicleTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		verifyCARCOVehicle("1", "2017, FIAT, 500", NO_DOCUMENT_RECEIVED, false, true, false);
		verifyCARCOVehicle("2", "2017, FORD, EXPLORER", NO_DOCUMENT_RECEIVED, true, true, false);
		verifyCARCOVehicle("3", "2017, SUBARU, CROSSTREK", NO_DOCUMENT_RECEIVED, false, true, false);
		verifyCARCOVehicle("4", "2017, BUICK, ENCORE", NO_DOCUMENT_RECEIVED, true, true, false);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5)).isPresent(false);

		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_1).setValue(CARCO_RADIOGROUP_OPTIONS.get(0));
		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_2).setValue(CARCO_RADIOGROUP_OPTIONS.get(1));
		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_3).setValue(CARCO_RADIOGROUP_OPTIONS.get(0));
		documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_4).setValue(CARCO_RADIOGROUP_OPTIONS.get(0));

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(3);//2017, FIAT, 500
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		VehicleTab.tableVehicleList.selectRow(4);//2017, FORD, EXPLORER
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		VehicleTab.tableVehicleList.selectRow(5);//2017, SUBARU, CROSSTREK
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("Yes");

		VehicleTab.tableVehicleList.selectRow(6);//2017, BUICK, ENCORE
		vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.LESS_THAN_1000_MILES).setValue("No");

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		verifyCARCOVehicle("1", "2017, FIAT, 500", INSPECTION_RECEIVED, true, true, false);
		verifyCARCOVehicle("2", "2017, FORD, EXPLORER", NO_DOCUMENT_RECEIVED, false, true, false);
		verifyCARCOVehicle("3", "2017, SUBARU, CROSSTREK", INSPECTION_RECEIVED, true, true, false);
		verifyCARCOVehicle("4", "2017, BUICK, ENCORE", INSPECTION_RECEIVED, false, true, false);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5)).isPresent(false);

		documentsAndBindTab.submitTab();
		errorTab.overrideAllErrors();
		errorTab.submitTab();

		//PAS-27615 - Create endorsement and remove COMP from carco vehicles. Then create another endorsement and put COMP back to the same Vehicles. ---> 'Inspection or Waiver Required' section should be reset to 'No Document Received'
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(3, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "No Coverage");//2017, FIAT, 500
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(4, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "No Coverage");//2017, FORD, EXPLORER
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(5, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "No Coverage");//2017, SUBARU, CROSSTREK
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(6, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "No Coverage");//2017, BUICK, ENCORE
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(3, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "$1,000");//2017, FIAT, 500
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(4, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "$1,000");//2017, FORD, EXPLORER
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(5, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "$1,000");//2017, SUBARU, CROSSTREK
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(6, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "$1,000");//2017, BUICK, ENCORE
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		verifyCARCOVehicle("1", "2017, FIAT, 500", NO_DOCUMENT_RECEIVED, true, true, false);
		verifyCARCOVehicle("2", "2017, FORD, EXPLORER", NO_DOCUMENT_RECEIVED, false, true, false);
		verifyCARCOVehicle("3", "2017, SUBARU, CROSSTREK", NO_DOCUMENT_RECEIVED, true, true, false);
		verifyCARCOVehicle("4", "2017, BUICK, ENCORE", NO_DOCUMENT_RECEIVED, false, true, false);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5)).isPresent(false);
		documentsAndBindTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);//makes sure that endorsement is bound
	}

	private void verifyCARCOVehicle(String vehicleNumber, String vehicleName, String expectedValue, boolean isLessThan1000Miles, boolean isEndorsement, boolean shouldAllOptionsBeDisabled) {
		Map<String, AssetDescriptor<StaticElement>> vehicleNameMap = new HashMap<>();
		vehicleNameMap.put("1", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_1);
		vehicleNameMap.put("2", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_2);
		vehicleNameMap.put("3", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_3);
		vehicleNameMap.put("4", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_4);
		vehicleNameMap.put("5", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_5);
		vehicleNameMap.put("6", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_6);
		vehicleNameMap.put("7", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_NAME_7);

		Map<String, AssetDescriptor<AdvancedRadioGroup>> vehicleRadioGroupMap = new HashMap<>();
		vehicleRadioGroupMap.put("1", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_1);
		vehicleRadioGroupMap.put("2", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_2);
		vehicleRadioGroupMap.put("3", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_3);
		vehicleRadioGroupMap.put("4", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_4);
		vehicleRadioGroupMap.put("5", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_5);
		vehicleRadioGroupMap.put("6", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_6);
		vehicleRadioGroupMap.put("7", AutoSSMetaData.DocumentsAndBindTab.InspectionOrWaiverRequired.CARCO_VEHICLE_RADIOGROUP_7);

		AssetDescriptor<AdvancedRadioGroup> vehicleRadioGroup = vehicleRadioGroupMap.get(vehicleNumber);

		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleNameMap.get(vehicleNumber)).getValue()).isEqualTo(vehicleName);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getValue()).isEqualTo(expectedValue);
		assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup)).hasOptions(CARCO_RADIOGROUP_OPTIONS);

		if (shouldAllOptionsBeDisabled) {
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(0))).isDisabled();
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(1))).isDisabled();
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(2))).isDisabled();
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(3))).isDisabled();
		} else {
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(0))).isEnabled();
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(1))).isEnabled(isLessThan1000Miles);
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(2))).isEnabled(!isEndorsement);
			assertThat(documentsAndBindTab.getInspectionOrWaiverRequiredAssetList().getAsset(vehicleRadioGroup).getRadioButton(CARCO_RADIOGROUP_OPTIONS.get(3))).isEnabled();
		}
	}
}
