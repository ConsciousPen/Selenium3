package aaa.toolkit.webdriver.customcontrols;

import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

/**
 * Control for enabling/disabling drivers in "Unacceptable Risk Surcharge" section in Premium & Coverages tab.
 * Test data should contain driver's name as a key and appropriate boolean value. Is is also allowed to set value by part of driver's name and index in list, e.g.:
 * <pre>
 * {@code Unverifiable Driving Record Surcharge {
 *     'DriverFirstName DriverLastName' = false,        // set value by driver full name<n>
 *     'contains=Smith' = true,                         // set value by part of driver's name<p>
 *     'number=3' = false                               // set value by driver number in surcharges list<p>
 * }}</pre>
 *
 */
public class UnverifiableDrivingRecordSurcharge extends AssetList {
	public static final String DRIVER_SELECTION_BY_CONTAINS_KEY = "contains=";
	public static final String DRIVER_SELECTION_BY_NUMBER_KEY = "number=";
	private ByT checkboxDriverTemplate = ByT.id("policyDataGatherForm:unverifiableDrivingRecordSurchargeTable:%1$s:unverifiableDrivingRecordSurchargeCheckbox");

	public UnverifiableDrivingRecordSurcharge(By locator) {
		super(locator);
	}

	public UnverifiableDrivingRecordSurcharge(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public UnverifiableDrivingRecordSurcharge(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected TestData getRawValue() {
		Map<String, Boolean> driversData = new HashMap<>();
		Map<String, CheckBox> unverifiableDrivingRecordSurchargeCheckBoxes = getUnverifiableDrivingRecordSurchargeCheckBoxes();
		for (Map.Entry<String, CheckBox> checkboxDriver : unverifiableDrivingRecordSurchargeCheckBoxes.entrySet()) {
			driversData.put(checkboxDriver.getKey(), checkboxDriver.getValue().getValue());
		}
		return new SimpleDataProvider(driversData);
	}

	@Override
	protected void setRawValue(TestData value) {
		Map<String, CheckBox> driverNamesAndCheckBoxes = getUnverifiableDrivingRecordSurchargeCheckBoxes();
		for (String driverKey : value.getKeys()) {
			boolean enableDriver = Boolean.valueOf(value.getValue(driverKey));
			if (driverKey.startsWith(DRIVER_SELECTION_BY_CONTAINS_KEY)) {
				String containsDriverKey = driverNamesAndCheckBoxes.keySet().stream().filter(d -> d.contains(driverKey.replaceAll(DRIVER_SELECTION_BY_CONTAINS_KEY, ""))).findFirst().orElse("");
				if (containsDriverKey.isEmpty()) {
					throw new IstfException(String.format("Unable to set value to %1$s control, it does not have check box which contains driver name \"%2$s\".", this.getName(), driverKey));
				}
				driverNamesAndCheckBoxes.get(containsDriverKey).setValue(enableDriver);
			} else if (driverKey.startsWith(DRIVER_SELECTION_BY_NUMBER_KEY)) {
				int driverNumber = Integer.valueOf(driverKey.replaceAll(DRIVER_SELECTION_BY_NUMBER_KEY, "")) - 1; // driver index in html starts from 0
				CheckBox checkBox = new CheckBox(new ByChained(this.locator, checkboxDriverTemplate.format(driverNumber)));
				if (!checkBox.isPresent()) {
					throw new IstfException(String.format("Unable to set value to %1$s control, it does not have driver #%2$s.", this.getName(), driverNumber));
				}
				checkBox.setValue(enableDriver);
			} else {
				if (!driverNamesAndCheckBoxes.containsKey(driverKey)) {
					throw new IstfException(String.format("Unable to set value to %1$s control, it does not have check box for \"%2$s\" driver.", this.getName(), driverKey));
				}
				driverNamesAndCheckBoxes.get(driverKey).setValue(enableDriver);
			}
		}
	}

	protected Map<String, CheckBox> getUnverifiableDrivingRecordSurchargeCheckBoxes() {
		Map<String, CheckBox> unverifiableDrivingRecordSurchargeCheckBoxes = new HashMap<>();
		int index = 0;
		CheckBox checkboxDriver = new CheckBox(new ByChained(this.locator, checkboxDriverTemplate.format(index)));
		while (checkboxDriver.isPresent()) {
			String driverName = WebDriverHelper.getInnerText(new ByChained(this.locator, checkboxDriverTemplate.format(index), By.xpath("./following-sibling::span"))).trim();
			unverifiableDrivingRecordSurchargeCheckBoxes.put(driverName, checkboxDriver);
			index++;
			checkboxDriver = new CheckBox(new ByChained(this.locator, checkboxDriverTemplate.format(index)));
		}
		return unverifiableDrivingRecordSurchargeCheckBoxes;
	}
}
