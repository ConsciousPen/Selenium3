package aaa.admin.metadata.administration;

import org.openqa.selenium.By;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.FileUpload;
import toolkit.webdriver.controls.RadioButton;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class AdministrationMetaData {

    public static final class VinTableTab extends MetaData {
        public static final AssetDescriptor<DialogAssetList> UPLOAD_DIALOG = declare("Upload Dialog", DialogAssetList.class, UploadDialog.class, By.id("uploadToVINTablePopup_container"));

        public static final AssetDescriptor<Button> UPLOAD_BUTTON = declare("Upload", Button.class, Waiters.AJAX, By.id("uploadToVINTableForm:uploadBtn"));
        public static final AssetDescriptor<RadioButton> UPLOAD_TO_VIN_TABLE_OPTION = declare("Upload to VIN table", RadioButton.class, Waiters.AJAX, By.xpath("//table[@class='ui-selectoneradio ui-widget ']//td[1]"));
        public static final AssetDescriptor<RadioButton> UPLOAD_TO_VIN_CONTROL_TABLE_OPTION = declare("Upload to VIN control table", RadioButton.class, Waiters.AJAX, By.xpath("//table[@class='ui-selectoneradio ui-widget ']//td[3]"));

        public static final class UploadDialog extends MetaData {

            public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare("Synchronize", Button.class, Waiters.AJAX, false,
                    By.id("uploadToVINTablePopupForm:synchronizePopupButton"));
            public static final AssetDescriptor<Button> BUTTON_CANCEL_POPUP = declare("Cancel", Button.class, Waiters.AJAX, false,
                    By.id("uploadToVINTablePopupForm:cancelPopupButton"));

            public static final AssetDescriptor<FileUpload> FILE_PATH_UPLOAD_ELEMENT = declare("File path", FileUpload.class, By.id("fileupload"));
        }
    }

    public static final class GenerateProductSchema extends MetaData {
        public static final AssetDescriptor<ComboBox> SELECT_CONFIGURATION = declare("Select Configuration:", ComboBox.class, Waiters.DEFAULT);
    }

}
