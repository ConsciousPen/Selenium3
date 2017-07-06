/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.productfactory.policy;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.controls.productfactory.ManageAttributes;

import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactoryCopy;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactorySearch;
import aaa.admin.modules.product.productfactory.IProductFactory;
import aaa.admin.modules.product.productfactory.ProductFactoryType;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.ComponentsTab;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Ivan Kisly
 * @name Test Product Factory Manage Component Attributes
 * @scenario
 * 1. Open 'Components' page of predefined product
 * 2. Select a component
 * 3. Open attribute management dialog
 * 4. Create new attribute
 * 5. Remove an attribute
 * 6. Add removed attribute
 * 7. Assert. Attribute is added back
 * 8. Change attribute visibility
 * 9. Assert. Visibility is saved successfully
 * @details
 */
public class TestProductFactoryManageComponentAttributes extends BaseTest {

    ProductFactoryType type = ProductFactoryType.POLICY;
    IProductFactory productFactory = type.get();
    TestData tdProductFactory = testDataManager.productFactory.get(ProductFactoryType.POLICY);

    String testProductId = "testID" + RandomStringUtils.random(6, false, true);

    @Test
    @TestInfo(component = "ProductFactory")
    public void testProductFactoryManageComponentAttributes() {
        adminApp().open();

        productFactory.navigate();
        productFactory.copy().perform(tdProductFactory.getTestData("Copy", "TestData").adjust(TestData.makeKeyPath(ProductProductFactoryCopy.class.getSimpleName(),
                ProductProductFactoryCopy.NEW_PRODUCT_CODE.getLabel()), testProductId));

        log.info("TEST: Manage Component Attributes");
        productFactory.update().perform(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(ProductProductFactorySearch.class.getSimpleName(),
                ProductProductFactorySearch.PRODUCT_CODE.getLabel()), testProductId));

        productFactory.inquiry().start(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(ProductProductFactorySearch.class.getSimpleName(),
                ProductProductFactorySearch.PRODUCT_CODE.getLabel()), testProductId));
        productFactory.getDefaultView().fillUpTo(tdSpecific.getTestData("TestDataInquiry"), ComponentsTab.class, true);

        verifyAttributesVisibility();
    }

    private void verifyAttributesVisibility() {
        Map<String, Boolean> actualValue =
                productFactory.getDefaultView().getTab(ComponentsTab.class).getPropertiesAssetList()
                        .getControl(ProductMetaData.ComponentsTab.MANAGE_ATTRIBUTES.getLabel(), ManageAttributes.class).getValue();

        Map<String, Boolean> expectedValue = getExpectedValue(tdSpecific.getTestData("ComponentManageComponents"));

        CustomAssert.enableSoftMode();
        for (String str : expectedValue.keySet()) {
            CustomAssert.assertTrue("Attribute [" + str + "] has value = " + expectedValue.get(str), expectedValue.get(str).equals(actualValue.get(str)));
        }
        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }

    private Map<String, Boolean> getExpectedValue(TestData td) {
        Map<String, Boolean> result = new HashMap<>();
        for (String value : td.getTestData(productFactory.getDefaultView().getTab(ComponentsTab.class).getPropertiesAssetList().getName(),
                ProductMetaData.ComponentsTab.MANAGE_ATTRIBUTES.getLabel()).getList("Visible")) {
            result.put(value, true);
        }
        return result;
    }
}
