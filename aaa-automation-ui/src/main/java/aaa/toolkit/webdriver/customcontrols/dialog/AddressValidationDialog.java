package aaa.toolkit.webdriver.customcontrols.dialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class AddressValidationDialog extends DialogAssetList {

	// public static final String DEFAULT_POPUP_OPENER_NAME = "Validate
	// Address";
	protected static final String YOU_ENTERED = "You entered";
	protected static final String DEFAULT_POPUP_SUBMITTER_NAME = "Ok";
	protected static final String DEFAULT_POPUP_CLOSER_NAME = "Cancel";
	protected static final String DEFAULT_SELECT_ADDRESS_NAME = "Select Address";
	//protected static final String OVERRIDE = "Override and use original address";
	protected static final String STREET_NAME = "Street Name";
	protected static final String STREET_NUMBER = "Street number";

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
		if (getAssetCollection().containsKey(DEFAULT_POPUP_SUBMITTER_NAME)) {
			Button buttonClosePopup = getAsset(DEFAULT_POPUP_SUBMITTER_NAME, Button.class);
			if (buttonClosePopup != null) {
				buttonClosePopup.click();
			}
		}
	}

	@Override
	public synchronized void fill(TestData td) {
		if (td.containsKey(name)) {

			//TODO auto ss: popup opens with delay, temp solution
			//Button buttonClosePopup = (Button) getAssetCollection().get(DEFAULT_POPUP_SUBMITTER_NAME);
			//if (!buttonClosePopup.isPresent()){
			//	buttonClosePopup.waitForAccessible(7000);
			//}
			//TODO Think on filling.
			// openDialog();
			TestData data = getValueToFill(td);

			if (data.containsKey(DEFAULT_SELECT_ADDRESS_NAME)) {
				switch (data.getValue(DEFAULT_SELECT_ADDRESS_NAME)) {
					case "default":
						break;
					default:
						break;
				}
			} else if (data.getKeys().isEmpty()) {
				TextBox streetName = (TextBox) getAsset(STREET_NAME);
				if (streetName.isPresent() && streetName.isVisible()) {
					log.info("Address Validation failed, starting address override.");
					try {
						String address = ((StaticElement) getAsset(YOU_ENTERED)).getValue();
						Pattern pattern = Pattern.compile("(\\d+)\\s+(.*)");
						Matcher matcher = pattern.matcher(address.split(",")[0]);
						if (matcher.matches()) {
							((TextBox) getAsset(STREET_NUMBER)).setValue(matcher.group(1).trim());
							((TextBox) getAsset(STREET_NAME)).setValue(matcher.group(2).trim());
						} else {
							log.info("Unable to parse and fill Address Validation Form");
						}
					} catch (Exception e) {
						throw new IstfException("Unable to parse and fill Address Validation Form: ", e);
					}
				}
			} else {
				setValue(data);
			}
			submit();
		}
	}

	protected void close() {
		Button buttonClosePopup = (Button) getAssetCollection().get(DEFAULT_POPUP_CLOSER_NAME);
		if (buttonClosePopup != null) {
			buttonClosePopup.click();
		}
	}

}
