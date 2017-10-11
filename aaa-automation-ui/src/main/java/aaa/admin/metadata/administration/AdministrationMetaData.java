package aaa.admin.metadata.administration;

import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class AdministrationMetaData {

    public static final class VinTableTab extends MetaData {
        public static final AssetDescriptor<DialogAssetList> UPLOAD_DIALOG = declare("UPLOAD_BUTTON Dialog", DialogAssetList.class, UploadDialog.class, By.id("uploadToVINTablePopup_container"));

        public static final AssetDescriptor<Button> UPLOAD_BUTTON = declare("UPLOAD_BUTTON", Button.class, Waiters.AJAX, By.id("uploadToVINTableForm:uploadBtn"));
        public static final AssetDescriptor<RadioButton> UPLOAD_TO_VIN_TABLE_OPTION = declare("UPLOAD_BUTTON to VIN table", RadioButton.class, Waiters.DEFAULT, By.xpath("//table[@class='ui-selectoneradio ui-widget ']//td[1]"));
        public static final AssetDescriptor<RadioButton> UPLOAD_TO_VIN_CONTROL_TABLE_OPTION = declare("UPLOAD_BUTTON to VIN control table", RadioButton.class, Waiters.DEFAULT, By.xpath("//table[@class='ui-selectoneradio ui-widget ']//td[3]"));

        public static final class UploadDialog extends MetaData {

            public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare("Synchronize", Button.class, Waiters.AJAX, false,
                    By.id("uploadToVINTablePopupForm:synchronizePopupButton"));
            public static final AssetDescriptor<Button> BUTTON_CANCEL_POPUP = declare("Cancel", Button.class, Waiters.DEFAULT, false,
                    By.id("uploadToVINTablePopupForm:cancelPopupButton"));

            public static final AssetDescriptor<FileUpload> FILE_PATH_UPLOAD_ELEMENT = declare("File path", FileUpload.class, By.id("fileupload"));
        }
    }

    public static final class GenerateProductSchema extends MetaData {
        public static final AssetDescriptor<ComboBox> SELECT_CONFIGURATION = declare("Select Configuration:", ComboBox.class, Waiters.DEFAULT);
    }

}
