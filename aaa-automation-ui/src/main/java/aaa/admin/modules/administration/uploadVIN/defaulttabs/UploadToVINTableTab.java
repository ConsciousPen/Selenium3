/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.administration.uploadVIN.defaulttabs;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.common.DefaultTab;
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
	public static StaticElement uploadToVINTableForm = new StaticElement(By.xpath("//*[@id='uploadToVINTableForm:successStatusMessage']//*[@id='uploadToVINTableForm:uploadSuccesful']"));

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
		long timeoutInSeconds = 5;

		getAssetList().getAsset(AdministrationMetaData.VinTableTab.FILE_PATH_UPLOAD_ELEMENT).setValue(new File(DEFAULT_PATH + fileName));
		buttonUpload.click();

		long timeout = System.currentTimeMillis() + timeoutInSeconds * 1000;

		while (timeout < System.currentTimeMillis()) {
			assertThat(labelUploadSuccessful.isPresent()).isTrue();
		}
	}
}
