/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.modules.cem.cemconfiguration.defaulttabs.FieldsConfigurationTab;
import aaa.admin.pages.cem.CemPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.CustomerCreationPage;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;

/**
 * @author Veronika Shkatulo
 * @name Configuring CEM fields (2987756)
 * @scenario
 * 1. In admin page: CEM -> CEM Configuration
 * 2. Set field's order, default value and visibility
 * 3. In main app open customer creation form
 * 4. Verify filed order, value and visibility
 * @details
 */
public class TestCustomerFieldOrderConfiguration extends CustomerBaseTest {

    private TestData testData = tdSpecific.getTestData("TestData");

    @Test
    public void testCustomerFieldOrderConfiguration() {
        adminApp().open();

        NavigationPage.toViewTab(NavigationEnum.AdminAppMainTabs.CEM.get());
        getCemConfigurationTab();

        new FieldsConfigurationTab().fillTab(testData);
        FieldsConfigurationTab.buttonSave.click();

        mainApp().reopen();

        customer.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData").ksam("CustomerType"));

        CustomAssert.enableSoftMode();

        CustomAssert.assertEquals(new GeneralTab().getAssetList().getControl(CustomerMetaData.GeneralTab.ADDRESS_LINE_2.getLabel(), TextBox.class).getValue(),
                testData.getTestData(CemMetaData.FieldsConfigurationTab.class.getSimpleName())
                .getValue(CemMetaData.FieldsConfigurationTab.ADDRESS_LINE_2_DEFAULT_VALUE.getLabel()));
        new GeneralTab().getAssetList().getControl(CustomerMetaData.GeneralTab.ADDRESS_LINE_3.getLabel())
                .verify.present(!new Boolean(testData.getTestData(CemMetaData.FieldsConfigurationTab.class.getSimpleName())
                .getValue(CemMetaData.FieldsConfigurationTab.ADDRESS_LINE_3_HIDDEN.getLabel())));
        CustomAssert.assertTrue(CustomerCreationPage.tableADDRESS_DETAILS.getRow(1).getCell(1).getValue().startsWith(CustomerConstants.ATTENTION));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        adminApp().reopen();

        NavigationPage.toViewTab(NavigationEnum.AdminAppMainTabs.CEM.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.CEM_CEM_CONFIGURATION.get());

        new FieldsConfigurationTab().fillTab(tdSpecific.getTestData("TearDown"));
        FieldsConfigurationTab.buttonSave.click();
    }

    private void getCemConfigurationTab() {
        if (CemPage.customerUiConfiguration.getValue() == false) {
            CemPage.customerUiConfiguration.setValue(true);
            CemPage.buttonSave.click();
            NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.CEM_CEM_CONFIGURATION.get());
            CemPage.buttonConfirm.click();
        } else {
            NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.CEM_CEM_CONFIGURATION.get());
        }
    }
}
