/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.productfactory.policy;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import aaa.admin.metadata.product.ProductMetaData.NewProductTab;
import aaa.admin.modules.product.productfactory.IProductFactory;
import aaa.admin.modules.product.productfactory.ProductFactoryType;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

/**
 * @author Ivan Kisly
 * @name Test Product Factory Add New Product
 * @scenario
 * 1. Create new product
 * 2. Assign basic actions to the product
 * 3. Connect components
 * 4. Create default workspace
 * 5. Activate the product
 * 6. Initiate quote creation
 * 7. Assert. Quote can be saved successfully
 * @details
 */
public class TestProductFactoryAddNewProduct extends CustomerBaseTest {

    ProductFactoryType type = ProductFactoryType.POLICY;
    IProductFactory productFactory = type.get();

    TestData tdProductFactory = testDataManager.productFactory.get(ProductFactoryType.POLICY);
    String testProductName = "test name " + RandomStringUtils.random(6, false, true);

    @Test
    @TestInfo(component = "ProductFactory")
    public void testProductFactoryAddNewProduct() {
        adminApp().open();
        productFactory.create(tdProductFactory.getTestData("Create", "TestData")
                .adjust(TestData.makeKeyPath(NewProductTab.class.getSimpleName(), NewProductTab.PRODUCT_NAME.getLabel()), testProductName));

        mainApp().open();
        customer.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));

        log.info("TEST: Create Quote for just created Product with Name = " + testProductName);
        initiateQuoteCreation();
        fillTestProduct(tdSpecific.getTestData("TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);

    }

    private void initiateQuoteCreation() {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
        QuoteSummaryPage.comboBoxProduct.setValue(testProductName);
        QuoteSummaryPage.buttonAddNewQuote.click();
    }

    private void fillTestProduct(TestData td) {
        new TestPolicyView().fill(td);
    }

    public class TestPolicyView extends Workspace {
        public TestPolicyView() {
            registerTab(PolicyTab.class);
            registerTab(RiskItemRiskItemTab.class);
            registerTab(RiskItemCoveragesTab.class);
            registerTab(NewTabTab.class);
        }

        @Override
        protected void registerTab(Class<? extends Tab> tabClass) {
            try {
                Constructor<?> constructor = tabClass.getDeclaredConstructors()[0];
                tabs.put(tabClass, (Tab) constructor.newInstance(TestProductFactoryAddNewProduct.this));
            } catch (Exception e) {
                throw new RuntimeException("Can't init tab class", e);
            }
        }
    }

    public class NewTabTab extends DefaultTab {
        public NewTabTab() {
            super(TestProductMetaData.NewTabTab.class);
        }
    }
    public class PolicyTab extends DefaultTab {
        public PolicyTab() {
            super(TestProductMetaData.PolicyTab.class);
        }
    }
    public class RiskItemCoveragesTab extends DefaultTab {
        public RiskItemCoveragesTab() {
            super(TestProductMetaData.RiskItemCoveragesTab.class);
        }
    }
    public class RiskItemRiskItemTab extends DefaultTab {
        public RiskItemRiskItemTab() {
            super(TestProductMetaData.RiskItemRiskItemTab.class);
        }
    }

    public static final class TestProductMetaData {
        public static final class PolicyTab extends MetaData {
            public static final AttributeDescriptor SOURCE = declare("Source", ComboBox.class);
            public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
            public static final AttributeDescriptor TERM = declare("Term", ComboBox.class);
            public static final AttributeDescriptor PREMIUM_STATE_PROVINCE = declare("Premium State/Province", ComboBox.class);
            public static final AttributeDescriptor UNDERWRITING_COMPANY = declare("Underwriting Company", ComboBox.class);
            public static final AttributeDescriptor INSURED_TYPE = declare("Insured Type", ComboBox.class);
            public static final AttributeDescriptor OVERRIDE_RATE_EFFECTIVE_DATE = declare("Override Rate Effective Date", RadioGroup.class);
            public static final AttributeDescriptor RATE_EFFECTIVE_DATE = declare("Rate Effective Date", TextBox.class);
            public static final AttributeDescriptor BRANCH = declare("Branch", ComboBox.class);
        }
        public static final class RiskItemRiskItemTab extends MetaData {
            public static final AttributeDescriptor ITEM_NAME = declare("itemName", TextBox.class);
        }
        public static final class RiskItemCoveragesTab extends MetaData {}
        public static final class NewTabTab extends MetaData {}
    }
}
