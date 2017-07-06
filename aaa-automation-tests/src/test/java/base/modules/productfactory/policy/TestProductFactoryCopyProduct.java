/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.productfactory.policy;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import aaa.admin.metadata.product.ProductMetaData.ProductProductFactoryCopy;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactorySearch;
import aaa.admin.modules.product.productfactory.IProductFactory;
import aaa.admin.modules.product.productfactory.ProductFactoryType;
import aaa.admin.pages.product.ProductProductFactoryPage;
import aaa.main.enums.ProductConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Ivan Kisly
 * @name Test Product Factory Copy Product
 * @scenario
 * 1. Open product search page
 * 2. Copy predefined product
 * 3. Search for both products
 * 4. Assert. Both product should be available
 * @details
 */
public class TestProductFactoryCopyProduct extends BaseTest {

    ProductFactoryType type = ProductFactoryType.POLICY;
    IProductFactory productFactory = type.get();
    TestData tdProductFactory = testDataManager.productFactory.get(ProductFactoryType.POLICY);

    String testProductId = "testID" + RandomStringUtils.random(6, false, true);

    @Test
    @TestInfo(component = "ProductFactory")
    public void testProductFactoryCopyProduct() {
        adminApp().open();

        productFactory.navigate();
        productFactory.copy().perform(tdProductFactory.getTestData("Copy", "TestData").adjust(TestData.makeKeyPath(ProductProductFactoryCopy.class.getSimpleName(),
                ProductProductFactoryCopy.NEW_PRODUCT_CODE.getLabel()), testProductId));

        log.info("TEST: Search for Predefined Product");
        CustomAssert.enableSoftMode();
        productFactory.search(tdProductFactory.getTestData("Copy", "TestDataSearchPredefinedProduct"));
        ProductProductFactoryPage.tableProducts.verify.rowsCount(1);
        ProductProductFactoryPage.tableProducts.getRow(1).getCell(ProductConstants.ProductTable.PRODUCT_NAME).verify.value(tdProductFactory.getTestData("Copy", "TestDataSearchPredefinedProduct")
                .getValue(
                        ProductProductFactorySearch.class.getSimpleName(), ProductProductFactorySearch.PRODUCT_NAME.getLabel()));
        ProductProductFactoryPage.tableProducts.getRow(1).getCell(ProductConstants.ProductTable.PRODUCT_CODE).verify.value(tdProductFactory.getTestData("Copy", "TestDataSearchPredefinedProduct")
                .getValue(
                        ProductProductFactorySearch.class.getSimpleName(), ProductProductFactorySearch.PRODUCT_CODE.getLabel()));

        log.info("TEST: Search for New Product");
        productFactory.search(tdProductFactory.getTestData("Copy", "TestDataSearchTestProduct").adjust(TestData.makeKeyPath(ProductProductFactorySearch.class.getSimpleName(),
                ProductProductFactorySearch.PRODUCT_CODE.getLabel()), testProductId));

        ProductProductFactoryPage.tableProducts.verify.rowsCount(1);
        ProductProductFactoryPage.tableProducts.getRow(1).getCell(ProductConstants.ProductTable.PRODUCT_NAME).verify.value(tdProductFactory.getTestData("Copy", "TestDataSearchTestProduct").getValue(
                ProductProductFactorySearch.class.getSimpleName(), ProductProductFactorySearch.PRODUCT_NAME.getLabel()));
        ProductProductFactoryPage.tableProducts.getRow(1).getCell(ProductConstants.ProductTable.PRODUCT_CODE).verify.value(testProductId);
        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }
}
