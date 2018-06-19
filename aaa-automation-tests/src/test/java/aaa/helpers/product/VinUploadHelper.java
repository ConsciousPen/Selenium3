package aaa.helpers.product;

import static aaa.main.enums.CacheManagerEnums.CacheNameEnum;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;

public class VinUploadHelper {

	private String policyType;
	private String state;
	private UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
	protected static Logger log = LoggerFactory.getLogger(VinUploadHelper.class);

	public VinUploadHelper(PolicyType policyType, String state) {
		this.policyType = policyType.getShortName();
		this.state = state;
	}

	/**
	 * Go to the admin -> administration -> Vin upload and upload two tables
	 * @param vinTableFile
	 */
	public void uploadFiles(String vinTableFile) {
		uploadFiles(getControlTableFile(), vinTableFile);
	}

	/**
	 * Go to the admin -> administration -> Vin upload and upload two tables
	 * @param vinTableFile
	 */
	public void uploadFiles(String controlTableFile, String vinTableFile) {
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		uploadToVINTableTab.uploadVinTable(vinTableFile);
		uploadToVINTableTab.uploadControlTable(controlTableFile);
	}

	/**
	 * Go to the admin -> administration -> Upload to vehicledatavin table
	 * @param vinTableFileName xls
	 */
	public void uploadVinTable(String vinTableFileName) {
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		uploadToVINTableTab.uploadVinTable(vinTableFileName);
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.CACHE_MANAGER.get());
		List<String> cacheName = Arrays.asList(CacheNameEnum.BASE_LOOKUP_CACHE.get(), CacheNameEnum.LOOKUP_CACHE.get(), CacheNameEnum.VEHICLE_VIN_REF_CACHE.get());
		for (String cache : cacheName) {
			CacheManager.clearFromCacheManagerTable(cache);
		}
		log.info("\n\nFile {} was uploaded\n\n", vinTableFileName);
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
				defaultFileName = "%s_%s_SS.xlsx";
				break;
			case "AutoCA":
				defaultFileName = "%s_%s_SELECT.xlsx";
				break;
			case "AutoCAC":
				defaultFileName = "%s_%s_CHOICE.xlsx";
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
}
