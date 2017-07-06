/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.productfactory.policy;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import aaa.admin.metadata.product.ProductMetaData.ProductProductFactoryCopy;
import aaa.admin.metadata.product.ProductMetaData.ProductProductFactorySearch;
import aaa.admin.modules.product.productfactory.IProductFactory;
import aaa.admin.modules.product.productfactory.ProductFactoryType;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.RulesTab;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Ivan Kisly
 * @name Test Product Factory Manage Rules
 * @scenario
 * 1. Open 'Rules' page of predefined product (copy of predefined product)
 * 2. Create a new rule
 * 3. Verify rule has been added
 * 4. Disable rule
 * 5. Verify rule is disabled
 * 6. Remove rule
 * 7. Verify rule has been deleted
 * @details
 */
public class TestProductFactoryManageRules extends BaseTest {

    ProductFactoryType type = ProductFactoryType.POLICY;
    IProductFactory productFactory = type.get();
    TestData tdProductFactory = testDataManager.productFactory.get(ProductFactoryType.POLICY);

    String testProductId = "testID" + RandomStringUtils.random(6, false, true);

    @Test
    @TestInfo(component = "ProductFactory")
    public void testProductFactoryManageRules() {
        adminApp().open();

        productFactory.navigate();
        productFactory.copy().perform(tdProductFactory.getTestData("Copy", "TestData").adjust(TestData.makeKeyPath(ProductProductFactoryCopy.class.getSimpleName(),
                ProductProductFactoryCopy.NEW_PRODUCT_CODE.getLabel()), testProductId));

        productFactory.update().start(tdSpecific.getTestData("TestData").adjust(TestData.makeKeyPath(ProductProductFactorySearch.class.getSimpleName(),
                ProductProductFactorySearch.PRODUCT_CODE.getLabel()), testProductId));

        log.info("TEST: Add New Rule");
        productFactory.getDefaultView().fillUpTo(tdSpecific.getTestData("TestData")
                .adjust(TestData.makeKeyPath(RulesTab.class.getSimpleName()), tdSpecific.getTestData("RuleAdd")),
                RulesTab.class, true);

        productFactory.getDefaultView().getTab(RulesTab.class).verify.ruleExists(tdSpecific.getTestData("RuleEdit"), true);

        log.info("TEST: Edit New Rule");
        productFactory.getDefaultView().fillFromTo(tdSpecific.getTestData("TestData")
                .adjust(TestData.makeKeyPath(RulesTab.class.getSimpleName()), tdSpecific.getTestData("RuleEdit")),
                RulesTab.class, RulesTab.class, true);

        productFactory.getDefaultView().getTab(RulesTab.class).verify.ruleStatus(tdSpecific.getTestData("RuleEdit"), false);

        log.info("TEST: Delete New Rule");
        productFactory.getDefaultView().fillFromTo(tdSpecific.getTestData("TestData")
                .adjust(TestData.makeKeyPath(RulesTab.class.getSimpleName()), tdSpecific.getTestData("RuleDelete")),
                RulesTab.class, RulesTab.class, true);

        productFactory.getDefaultView().getTab(RulesTab.class).verify.ruleExists(tdSpecific.getTestData("RuleEdit"), false);

        productFactory.update().submit();
    }
}
