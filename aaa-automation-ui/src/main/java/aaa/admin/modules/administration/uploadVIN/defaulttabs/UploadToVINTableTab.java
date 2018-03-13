/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.administration.uploadVIN.defaulttabs;

import static org.assertj.core.api.Assertions.fail;
import java.io.File;
import org.openqa.selenium.By;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.common.DefaultTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class UploadToVINTableTab extends DefaultTab {

	protected static Logger log = LoggerFactory.getLogger(UploadToVINTableTab.class);

	public UploadToVINTableTab() {
		super(AdministrationMetaData.VinTableTab.class);
		assetList = new AssetList(By.xpath("//*"), metaDataClass);
	}

	public static StaticElement labelUploadSuccessful = new StaticElement(By.id("uploadToVINTableForm:uploadSuccesful"));
	public static StaticElement labelUploadFailed = new StaticElement(By.id("uploadToVINTableForm:messagesBlock"));

	public static Button buttonUpload = new Button(By.className("start"));
	public static Button buttonChoose = new Button(By.className("fileinput-button"));

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
		getAssetList().getAsset(AdministrationMetaData.VinTableTab.FILE_PATH_UPLOAD_ELEMENT).setValue(new File(DEFAULT_PATH + fileName));

		buttonUpload.click();

		//added a 'wait' here because the loading animation on the page was causing the upload verification to fail. This wait allows the animation to complete.
		long timeout = System.currentTimeMillis() + (60 * 1000);
		while (timeout > System.currentTimeMillis() && !labelUploadSuccessful.getValue().contains("Rows added")){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("wait issue");
			}
		}

		if (labelUploadSuccessful.getValue().contains("Rows added")) {
			// check successfull
			log.info("File {} was uploaded successfully", fileName);
		}
		else {
			fail("File " + fileName + " was not uploaded. See error: \n" + labelUploadFailed.getValue());
		}
	}
}