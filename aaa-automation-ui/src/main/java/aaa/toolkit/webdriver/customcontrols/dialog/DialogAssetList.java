/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.toolkit.webdriver.customcontrols.dialog;

import java.util.Map;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class DialogAssetList extends AbstractDialog<TestData, Void> {

	private static final String ADDITIONAL_POPUP_SUBMIT = "Additional Popup Submit";

	public DialogAssetList(By locator) {
		super(locator);
	}

	public DialogAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public DialogAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected Void getRawValue() {
		return null;
	}

	@Override
	protected void setRawValue(TestData data) {
		for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {
			if (entry.getKey().equals(ADDITIONAL_POPUP_SUBMIT)) {
				continue;
			}
			entry.getValue().fill(data);
		}
	}

	@Override
	public TestData.Type testDataType() {
		return TestData.Type.TESTDATA;
	}

	@Override
	public TestData getValueToFill(TestData td) {
		return td.getTestData(name);
	}

	@Override
	protected Void normalize(Object rawValue) {
		if (rawValue instanceof Void) {
			return (Void) rawValue;
		}
		throw new IllegalArgumentException("Value " + rawValue + " has incorrect type " + rawValue.getClass());
	}

	@Override
	public void fill(TestData td) {
		fill(td, true);
	}

	@Override
	public void submit() {
		super.submit();
	}

	/**
	 * Fills dialog asset list
	 *
	 * @param td             - td to fill
	 * @param closeOpenLogic - true - include close/open auto logic, otherwise - false
	 */
	public void fill(TestData td, boolean closeOpenLogic) {
		if (td.containsKey(this.name)) {
			if (closeOpenLogic) {
				this.openDialog();
				this.setValue(this.getValueToFill(td));
				this.submit();
				if (this.getValueToFill(td).containsKey(ADDITIONAL_POPUP_SUBMIT)) {
					this.getAsset(ADDITIONAL_POPUP_SUBMIT, DialogAssetList.class).setValue(this.getValueToFill(td).getTestData(ADDITIONAL_POPUP_SUBMIT));
				}
			} else {
				this.setValue(this.getValueToFill(td));
			}
		}
	}
}
