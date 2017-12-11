package aaa.modules.regression.sales.common_helpers;

import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class VinUploadCommonMethods extends PolicyBaseTest {

	private final String policyType ;

	public VinUploadCommonMethods(String policyType){
		this.policyType = policyType;
	}

	/**
	 * Go to the admin -> administration -> Vin upload and upload two tables
	 * @param vinTableFile
	 */
	public void uploadFiles(String vinTableFile) {

		UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();

		//open Admin application and navigate to Administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, getControlTableFile());
	}

	/**
	 * Go to the admin -> administration -> Vin upload and upload two tables
	 * @param vinTableFile
	 * @param controlTableFile
	 */
	public void uploadFiles(String controlTableFile, String vinTableFile) {

		UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();

		//open Admin application and navigate to Administration tab
		adminApp().open();
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION, vinTableFile);
		uploadToVINTableTab.uploadExcel(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION, controlTableFile);
	}

	public void precondsTestVINUpload(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, tab, true);
	}

	public void verifyActivitiesAndUserNotes(String vinNumber) {
		//method added for verification of PAS-544 - Activities and User Notes
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "VIN data has been updated for the following vehicle(s): " + vinNumber)
				.verify.present("PAS-544 - Activities and User Notes may be broken: VIN refresh record is missed in Activities and User Notes:");
	}

	public String getSpecificUploadFile(String fileType) {
		String defaultFileName = null;
		switch (policyType) {
			case "AutoSS":
				defaultFileName = "%sVIN_%s_SS.xlsx";
				break;
			case "AutoCA":defaultFileName = "%sVIN_%s_SELECT.xlsx";
				break;
			case "AutoCAC":
				defaultFileName = "%sVIN_%s_CHOICE.xlsx";
				break;
			default:
				log.info("Name of VIN Table file was not selected correctly");
				break;
		}
		return String.format(defaultFileName, fileType, getState());
	}

	public String getControlTableFile() {
		String defaultControlFileName = null;
		switch (policyType) {
			case "AutoSS":
				defaultControlFileName = "controlTable_%s_SS.xlsx";
				break;
			case "AutoCA":
				defaultControlFileName = "controlTable_%s_SELECT.xlsx";
				break;
			case "AutoCAC":
				defaultControlFileName = "controlTable_%s_CHOICE.xlsx";
				break;
			default:
				log.info("Name of control file was not selected correctly");
				break;
		}
		return String.format(defaultControlFileName, getState());
	}

	public enum UploadFilesTypes {
		UPDATED_VIN("Updated"),
		ADDED_VIN("Added");

		private String type;

		UploadFilesTypes(String type) {
			set(type);
		}

		public void set(String type) {
			this.type = type;
		}

		public String get() {
			return type;
		}
	}
}
