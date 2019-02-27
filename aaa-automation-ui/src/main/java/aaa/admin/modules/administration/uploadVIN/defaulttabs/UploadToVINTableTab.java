/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.administration.uploadVIN.defaulttabs;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
import aaa.common.DefaultTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CacheManagerEnums;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class UploadToVINTableTab extends DefaultTab {

	protected Logger log = LoggerFactory.getLogger(UploadToVINTableTab.class);

	public UploadToVINTableTab() {
		super(AdministrationMetaData.VinTableTab.class);
		assetList = new AssetList(By.xpath("//*"), metaDataClass);
	}

	public static StaticElement labelUploadSuccessful = new StaticElement(By.id("uploadToVINTableForm:uploadSuccesful"));
	public static StaticElement labelUploadFailed = new StaticElement(By.id("uploadToVINTableForm:messagesBlock"));
	public static StaticElement uploadToVINTableForm = new StaticElement(By.xpath("//*[@id='uploadToVINTableForm:successStatusMessage']//*[@id='uploadToVINTableForm:uploadSuccesful']"));

	public static Button buttonUpload = new Button(By.className("start"));
	public static Button buttonChoose = new Button(By.className("fileinput-button"));

	private static ReentrantLock lock = new ReentrantLock();

	protected static final String DEFAULT_PATH = "src/test/resources/uploadingfiles/vinUploadFiles/";

	/**
	 * Go to the admin -> administration -> Vin upload and uploadControlTable
	 * @param vinTableFile
	 */
	public void uploadFiles(String controlTableFile, String vinTableFile) {
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		//Uploading of VIN_Control table info, then uploading of the updates for VIN table
		uploadControlTable(controlTableFile);
		uploadVinTable(vinTableFile);
	}

	public void uploadControlTable(String controlTable) {
		log.info("WARN Vin control file {} upload started",controlTable);
		openUploadToVinTableTab();

		getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_CONTROL_TABLE_OPTION).setValue(true);
		uploadFile(controlTable);
		CacheManager cacheManager = new CacheManager();
		cacheManager.getToCacheManagerTab();
		List<String> cacheName = Arrays.asList(CacheManagerEnums.CacheNameEnum.BASE_LOOKUP_CACHE.get(), CacheManagerEnums.CacheNameEnum.LOOKUP_CACHE.get(), CacheManagerEnums.CacheNameEnum.VEHICLE_VIN_REF_CACHE.get());
		for (String cache : cacheName) {
			cacheManager.clearFromCacheManagerTable(cache);
		}
		log.info("WARN Vin control file {} upload finished",controlTable);

	}

	public void openUploadToVinTableTab() {
		if(!isOpened()){
			NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
			NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.UPLOAD_TO_VIN_TABLE.get());
		}
	}

	/**
	 * Go to the admin -> administration -> Upload to vehicledatavin table
	 * @param vinTableFileName xls
	 */
	public void uploadVinTable(String vinTableFileName) {
		log.info("WARN Vin table {} upload started",vinTableFileName);
		openUploadToVinTableTab();
		getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_TO_VIN_TABLE_OPTION).setValue(true);
		uploadFile(vinTableFileName);
		CacheManager cacheManager = new CacheManager();
		cacheManager.getToCacheManagerTab();
		List<String> cacheName = Arrays.asList(CacheManagerEnums.CacheNameEnum.BASE_LOOKUP_CACHE.get(), CacheManagerEnums.CacheNameEnum.LOOKUP_CACHE.get(), CacheManagerEnums.CacheNameEnum.VEHICLE_VIN_REF_CACHE.get());
		for (String cache : cacheName) {
			cacheManager.clearFromCacheManagerTable(cache);
		}
		log.info("WARN Vin table {} upload finished",vinTableFileName);
	}

	private void uploadFile(String fileName) {
		log.info("Upload vin queue state: count of thread {}", lock.getQueueLength());
		lock.lock();
		try {
			getAssetList().getAsset(AdministrationMetaData.VinTableTab.FILE_PATH_UPLOAD_ELEMENT).setValue(new File(DEFAULT_PATH + fileName));
			buttonUpload.click();

			long timeoutInSeconds = 10;
			long timeout = System.currentTimeMillis() + timeoutInSeconds * 1000;

			while (timeout > System.currentTimeMillis()) {
				try {
					Thread.sleep(1000);
					log.info("Wait for file upload, in miliseconds left: {}", timeout - System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				assertThat(new StaticElement(By.xpath("//*[@id='uploadToVINTableForm']")).getValue()).doesNotContain("Error");
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean isOpened(){
		return labelUploadSuccessful.isPresent();
	}
}
