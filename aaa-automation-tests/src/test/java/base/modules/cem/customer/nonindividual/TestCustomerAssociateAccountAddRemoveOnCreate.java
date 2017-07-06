/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

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
 * @name Test Associate Account Add Remove On Create
 * @scenario
 * 1. Create Non Individual Customer
 * 2. In General Tab click Associate Account button
 * 3. In pop up select New Account radio button
 * 4. Fill all required fields
 * 5. Click Add button
 * 6. Verify that Associate Account is present in Major/Large Account table
 * 7. Click Remove This Account button
 * 8. Verify that Associate Account is not present in Major/Large Account table
 * @details
 */
public class TestCustomerAssociateAccountAddRemoveOnCreate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerAssociateAccountAddRemoveOnCreate() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_WithAssociateAccount").resolveLinks()));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.setActionAndGo("Update");

        CustomerSummaryPage.tableMajorLargeAccount.getRow(1).getCell(2).verify.value(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_WithAssociateAccount")
                .getTestData(
                        CustomerMetaData.GeneralTab.class.getSimpleName())
                .getValue(CustomerMetaData.GeneralTab.ACCOUNT_DESIGNATION_TYPE.getLabel()));

        CustomerSummaryPage.buttonRemoveThisAccount.click();

        CustomerSummaryPage.tableMajorLargeAccount.getRow(1).verify.present(false);
    }
}
