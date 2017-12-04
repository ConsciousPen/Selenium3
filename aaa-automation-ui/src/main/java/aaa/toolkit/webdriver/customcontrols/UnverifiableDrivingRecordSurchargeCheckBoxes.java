package aaa.toolkit.webdriver.customcontrols;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import toolkit.datax.TestData;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class UnverifiableDrivingRecordSurchargeCheckBoxes extends AbstractContainer<TestData, TestData> {
	private static final String DRIVER_SELECTION_BY_CONTAINS_KEY = "contains=";
	private static final String DRIVER_SELECTION_BY_NUMBER_KEY = "number=";
	private ByT driverCheckboxSelectionByNumberTemplate = ByT.id("policyDataGatherForm:unverifiableDrivingRecordSurchargeTable:%1$s:unverifiableDrivingRecordSurchargeCheckbox']");
	private ByT driverCheckboxSelectionByNameTemplate =
			ByT.xpath(".//span[contains(@id, 'unverifiableDrivingRecordSurchargeLabel') and text()='\\u00a0\\u00a0%1$s']/preceding-sibling::input[@type='checkbox']");
	private ByT driverCheckboxSelectionByContainsNameTemplate =
			ByT.xpath(".//span[contains(@id, 'unverifiableDrivingRecordSurchargeLabel') and contains(text(), '%1$s')]/preceding-sibling::input[@type='checkbox']");

	public UnverifiableDrivingRecordSurchargeCheckBoxes(By locator) {
		super(locator);
	}

	public UnverifiableDrivingRecordSurchargeCheckBoxes(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public UnverifiableDrivingRecordSurchargeCheckBoxes(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected TestData getRawValue() {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("getRawValue() is not implemented yet");
	}

	@Override
	protected void setRawValue(TestData value) {
		CheckBox checkBoxDriver;
		for (String driverKey : value.getKeys()) {
			boolean enableDriver = Boolean.valueOf(value.getValue(driverKey));
			if (driverKey.startsWith(DRIVER_SELECTION_BY_CONTAINS_KEY)) {
				checkBoxDriver = new CheckBox(new ByChained(this.locator, driverCheckboxSelectionByContainsNameTemplate.format(driverKey.replaceAll(DRIVER_SELECTION_BY_CONTAINS_KEY, ""))));
			} else if (driverKey.startsWith(DRIVER_SELECTION_BY_NUMBER_KEY)) {
				int driverNumber = Integer.valueOf(driverKey.replaceAll(DRIVER_SELECTION_BY_NUMBER_KEY, "")) - 1; // driver index in html starts from 0
				checkBoxDriver = new CheckBox(new ByChained(this.locator, driverCheckboxSelectionByNumberTemplate.format(driverNumber)));
			} else {
				checkBoxDriver = new CheckBox(new ByChained(this.locator, driverCheckboxSelectionByNameTemplate.format(driverKey)));
			}
			checkBoxDriver.setValue(enableDriver);
		}
	}

	@Override
	public TestData.Type testDataType() {
		return TestData.Type.TESTDATA;
	}

	@Override
	protected TestData normalize(Object rawValue) {
		if (rawValue instanceof TestData) {
			return (TestData) rawValue;
		}
		throw new IllegalArgumentException("Value " + rawValue + " has incorrect type " + rawValue.getClass());
	}

	@Override
	public void fill(TestData td) {
		if (td.containsKey(name) && !td.getTestDataList(name).isEmpty()) {
			setValue(td);
		}
	}
}
