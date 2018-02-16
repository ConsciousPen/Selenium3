package aaa.helpers.product;

import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;

public class VinUploadHelper {

	private String policyType;
	private String state;
	UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();

	public VinUploadHelper(PolicyType policyType,String state) {
		this.policyType = policyType.getShortName();
		this.state = state;
	}

	/**
	 * Go to the admin -> administration -> Vin upload and upload two tables
	 * @param vinTableFile
	 */
	public void uploadFiles(String vinTableFile) {
		uploadFiles(getControlTableFile(),vinTableFile);
	}

	/**
	 * Go to the admin -> administration -> Vin upload and upload two tables
	 * @param vinTableFile
	 * @param controlTableFile
	 */
	public void uploadFiles(String controlTableFile, String vinTableFile) {
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadVinTable(vinTableFile);
		uploadToVINTableTab.uploadControlTable(controlTableFile);
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
			case "AutoCA":
				defaultFileName = "%sVIN_%s_SELECT.xlsx";
				break;
			case "AutoCAC":
				defaultFileName = "%sVIN_%s_CHOICE.xlsx";
				break;
			default:
				throw new IllegalArgumentException("Name of VIN Table file was not selected correctly");
		}
		return String.format(defaultFileName, fileType, state);
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
				throw new IllegalArgumentException("Name of VIN Table file was not selected correctly");
		}
		return String.format(defaultControlFileName, state);
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