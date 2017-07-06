/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.productfactory.policy;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.controls.productfactory.ProductFactoryCheckBox;

import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactoryCopy;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactorySearch;
import aaa.admin.modules.product.productfactory.IProductFactory;
import aaa.admin.modules.product.productfactory.ProductFactoryType;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.ComponentsTab;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Ivan Kisly
 * @name Test Product Factory Update Component Attribute
 * @scenario
 * 1. Open 'Components' page of predefined product (copy of predefined product)
 * Select component's atribute
 * Change attribute configuration
 * Assert. Attribute configuration is saved successfully
 * @details
 */
public class TestProductFactoryUpdateComponentAttribute extends BaseTest {

    ProductFactoryType type = ProductFactoryType.POLICY;
    IProductFactory productFactory = type.get();
    TestData tdProductFactory = testDataManager.productFactory.get(ProductFactoryType.POLICY);

    String testProductId = "testID" + RandomStringUtils.random(6, false, true);

    @Test
    @TestInfo(component = "ProductFactory")
    public void testProductFactoryUpdateComponentAttribute() {
        adminApp().open();

        productFactory.navigate();
        productFactory.copy().perform(tdProductFactory.getTestData("Copy", "TestData").adjust(TestData.makeKeyPath(ProductProductFactoryCopy.class.getSimpleName(),
                ProductProductFactoryCopy.NEW_PRODUCT_CODE.getLabel()), testProductId));

        log.info("TEST: Update Component Attribute");
        productFactory.update().perform(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(ProductProductFactorySearch.class.getSimpleName(),
                ProductProductFactorySearch.PRODUCT_CODE.getLabel()), testProductId));

        productFactory.inquiry().start(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(ProductProductFactorySearch.class.getSimpleName(),
                ProductProductFactorySearch.PRODUCT_CODE.getLabel()), testProductId));
        productFactory.getDefaultView().fillUpTo(tdSpecific.getTestData("TestDataInquiry"), ComponentsTab.class, true);

        productFactory.getDefaultView().getTab(ComponentsTab.class)
                .getPropertiesAssetList().getControl(ProductMetaData.ComponentsTab.REQUIRED.getLabel(), ProductFactoryCheckBox.class).verify
                .value(Boolean.parseBoolean(tdSpecific.getTestData("PolicyNumberItem").getValue("Properties", ProductMetaData.ComponentsTab.REQUIRED.getLabel())));
        productFactory.inquiry().submit();
    }
}
