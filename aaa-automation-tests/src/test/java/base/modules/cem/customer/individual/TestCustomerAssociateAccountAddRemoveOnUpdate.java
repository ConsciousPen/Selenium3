/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Associate Account Add Remove On Update
 * @scenario
 * 1. Create Individual Customer
 * 2. Select Action Update from drop-down
 * 3. In General Tab click Associate Account button
 * 3. In pop up select New Account radio button
 * 4. Fill all required fields
 * 5. Click Add button
 * 6. Verify that Associate Account is present in Major/Large Account table
 * 7. Click Remove This Account button
 * 8. Verify that Associate Account is not present in Major/Large Account table
 * @details
 */
public class TestCustomerAssociateAccountAddRemoveOnUpdate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerAssociateAccountAddRemoveOnUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        customer.update().perform(tdCustomerIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerIndividual.getTestData("DataGather", "Adjustment_WithAssociateAccountOnUpdate").resolveLinks()));

        NavigationPage.setActionAndGo("Update");

        CustomerSummaryPage.tableMajorLargeAccount.getRow(1).getCell(2).verify.value(tdCustomerIndividual.getTestData("DataGather", "Adjustment_WithAssociateAccount").getTestData(
                CustomerMetaData.GeneralTab.class.getSimpleName()).getValue(CustomerMetaData.GeneralTab.ACCOUNT_DESIGNATION_TYPE.getLabel()));

        CustomerSummaryPage.buttonRemoveThisAccount.click();

        CustomerSummaryPage.tableMajorLargeAccount.getRow(1).verify.present(false);
    }
}
