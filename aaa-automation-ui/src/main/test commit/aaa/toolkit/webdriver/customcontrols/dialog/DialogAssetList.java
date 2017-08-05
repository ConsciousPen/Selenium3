/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.toolkit.webdriver.customcontrols.dialog;

import java.util.Map;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import toolkit.datax.TestData;
import toolkit.datax.TestData.Type;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class DialogAssetList extends AbstractDialog<TestData, Void> {

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
    public Type testDataType() {
        return Type.TESTDATA;
    }

    @Override
    protected void setRawValue(TestData data) {
        for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {
            entry.getValue().fill(data);
        }
    }

    @Override
    protected Void getRawValue() {
        return null;
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
    public void submit() {
      super.submit();
    }
}
