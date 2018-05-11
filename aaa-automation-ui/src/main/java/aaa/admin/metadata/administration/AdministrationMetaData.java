package aaa.admin.metadata.administration;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.FileUpload;
import toolkit.webdriver.controls.RadioButton;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class AdministrationMetaData {

	public static final class VinTableTab extends MetaData {

		public static final AssetDescriptor<Button> UPLOAD_BUTTON = declare("Upload", Button.class, Waiters.AJAX, By.id("uploadToVINTableForm:uploadBtn"));
		public static final AssetDescriptor<RadioButton> UPLOAD_TO_VIN_TABLE_OPTION =
				declare("Upload to VIN table", RadioButton.class, Waiters.AJAX, By.xpath("//table[contains(@id,'uploadToVINTableForm')]//td[1]//div[contains(@class,'ui-radiobutton-box')]"));
		public static final AssetDescriptor<RadioButton> UPLOAD_TO_VIN_CONTROL_TABLE_OPTION =
				declare("Upload to VIN control table", RadioButton.class, Waiters.AJAX, By.xpath("//table[contains(@id,'uploadToVINTableForm')]//td[3]//div[contains(@class,'ui-radiobutton-box')]"));

		public static final AssetDescriptor<FileUpload> FILE_PATH_UPLOAD_ELEMENT =
				declare("File path", FileUpload.class, By.xpath("//*[contains(@id,'uploadToVINTableForm:filePld')]//input[@id='uploadToVINTableForm:filePld_input']"));
	}

	public static final class GenerateProductSchema extends MetaData {
		public static final AssetDescriptor<ComboBox> SELECT_CONFIGURATION = declare("Select Configuration:", ComboBox.class, Waiters.DEFAULT);
	}

	public static final class CacheManager extends MetaData {
	}

}
