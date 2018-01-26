/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.administration.uploadVIN.defaulttabs;

import static org.assertj.core.api.Assertions.fail;
import java.io.File;
import org.openqa.selenium.By;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.common.DefaultTab;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;

public class UploadToVINTableTab extends DefaultTab {

	public UploadToVINTableTab() {
		super(AdministrationMetaData.VinTableTab.class);
	}

	public static StaticElement labelUploadSuccessful = new StaticElement(By.id("uploadToVINTableForm:uploadSuccesful"));
	public static StaticElement labelUploadFailed = new StaticElement(By.id("uploadToVINTableForm:uploadFailed"));
	public static Button buttonUpload = new Button(By.id("uploadToVINTableForm:uploadBtn"));

	protected static final String DEFAULT_PATH = "src/test/resources/uploadingfiles/vinUploadFiles/";

	public void uploadControlTable(String fileName) {
		getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION).setValue(true);
		uploadFile(fileName);
	}

	public void uploadVinTable(String fileName) {
		getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION).setValue(true);
		uploadFile(fileName);
	}

	private void uploadFile(String fileName) {
		buttonUpload.click();
		getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_DIALOG)
				.getAsset(AdministrationMetaData.VinTableTab.UploadDialog.FILE_PATH_UPLOAD_ELEMENT).setValue(new File(DEFAULT_PATH + fileName));

		getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_DIALOG)
				.getAsset(AdministrationMetaData.VinTableTab.UploadDialog.BUTTON_SUBMIT_POPUP).click();

		if (!labelUploadSuccessful.isPresent()) {
			fail("File " + fileName + " was not uploaded. See error: \n" + labelUploadFailed.getValue());
		}
	}
}