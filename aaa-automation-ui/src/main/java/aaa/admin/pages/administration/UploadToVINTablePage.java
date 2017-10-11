package aaa.admin.pages.administration;

import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.admin.pages.AdminPage;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;

import java.io.File;

public class UploadToVINTablePage extends AdminPage {

    public static Button BTN_UPLOAD = new Button(By.id("uploadToVINTableForm:uploadBtn"));

    protected final static String defaultpath = "src/test/resources/uploadingfiles/vinUploadFiles/";

    UploadToVINTableTab uploadToVINTable = new UploadToVINTableTab();

    public void uploadExcel(String fileName){

        UploadToVINTablePage.BTN_UPLOAD.click();
        uploadToVINTable.getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_DIALOG)
                .getAsset(AdministrationMetaData.VinTableTab.UploadDialog.FILE_PATH_UPLOAD_ELEMENT).setValue(new File(defaultpath+fileName));

        uploadToVINTable.getAssetList().getAsset(AdministrationMetaData.VinTableTab.UPLOAD_DIALOG)
                .getAsset(AdministrationMetaData.VinTableTab.UploadDialog.BUTTON_SUBMIT_POPUP).click();
    }
}
