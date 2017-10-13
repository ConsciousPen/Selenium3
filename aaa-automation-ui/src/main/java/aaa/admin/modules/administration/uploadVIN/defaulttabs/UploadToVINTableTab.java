/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.administration.uploadVIN.defaulttabs;

import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.common.DefaultTab;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioButton;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

import java.io.File;

public class UploadToVINTableTab extends DefaultTab {

    public UploadToVINTableTab() {
        super(AdministrationMetaData.VinTableTab.class);
    }


    public static Button BTN_UPLOAD = new Button(By.id("uploadToVINTableForm:uploadBtn"));

    protected final static String defaultpath = "src/test/resources/uploadingfiles/vinUploadFiles/";


    public void uploadExcel(AssetDescriptor<RadioButton> buttonAssetDescriptor, String fileName){

        getAssetList().getAsset(buttonAssetDescriptor).setValue(true);
        BTN_UPLOAD.click();
        getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_DIALOG)
                .getAsset(AdministrationMetaData.VinTableTab.UploadDialog.FILE_PATH_UPLOAD_ELEMENT).setValue(new File(defaultpath+fileName));

        getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_DIALOG)
                .getAsset(AdministrationMetaData.VinTableTab.UploadDialog.BUTTON_SUBMIT_POPUP).click();
    }
}
