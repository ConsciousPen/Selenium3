package aaa.toolkit.webdriver.customcontrols.dialog;

import org.openqa.selenium.By;

import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class AddressValidationDialog extends DialogAssetList {

	// public static final String DEFAULT_POPUP_OPENER_NAME = "Validate
	// Address";
	public static final String DEFAULT_POPUP_SUBMITTER_NAME = "Ok";
	public static final String DEFAULT_POPUP_CLOSER_NAME = "Cancel";
	public static final String DEFAULT_SELECT_ADDRESS_NAME = "Select Address";

	public AddressValidationDialog(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);

	}
	
	 public AddressValidationDialog(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
	        super(parent, locator, metaDataClass);
	    }

	@Override
	protected void openDialog() {
		/*
		 * Button buttonOpenPopup = (Button)
		 * getAssetCollection().get(DEFAULT_POPUP_OPENER_NAME); if
		 * (buttonOpenPopup != null) { buttonOpenPopup.click(); }
		 */
	}

	@Override
	public void submit() {
		Button buttonClosePopup = (Button) getAssetCollection().get(DEFAULT_POPUP_SUBMITTER_NAME);
		if (buttonClosePopup != null) {
			buttonClosePopup.click();
		}
	}

	protected void close() {
		Button buttonClosePopup = (Button) getAssetCollection().get(DEFAULT_POPUP_CLOSER_NAME);
		if (buttonClosePopup != null) {
			buttonClosePopup.click();
		}
	}

	@Override
	public void fill(TestData td) {
		if (td.containsKey(name)) {
			
			//TODO auto ss: popup opens with delay, temp solution
			Button buttonClosePopup = (Button) getAssetCollection().get(DEFAULT_POPUP_SUBMITTER_NAME);
			if (!buttonClosePopup.isPresent()){
				buttonClosePopup.waitForAccessible(5000);
			}
//TODO Think on filling.
			// openDialog();
			TestData data = getValueToFill(td);

			if (data.containsKey(DEFAULT_SELECT_ADDRESS_NAME)) {
				switch (data.getValue(DEFAULT_SELECT_ADDRESS_NAME)) {
				case "/default":
					break;
				case "/override":
					break;
				default:
					break;
				}
			} else
				setValue(data);
			submit();
		}
	}

}
