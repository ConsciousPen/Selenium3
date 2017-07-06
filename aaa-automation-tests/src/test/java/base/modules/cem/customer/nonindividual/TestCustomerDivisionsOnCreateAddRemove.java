/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.DivisionsTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Divisions On Create Add Remove
 * @scenario
 * 1. Create Non Individual Customer and add Divisions
 * 2. Verify that Divisions is present in Divisions table
 * 3. Navigate to Divisions tab and remove Divisions record
 * 3a. Uncheck Associate Divisions in Update Mode
 * 3b. Uncheck Associate Divisions and click Cancel on Warning dialog
 * 4. Verify that Divisions is not present in Divisions table
 * 4a. Verify that Divisions table Divisions tab are absent
 * @details
 */
public class TestCustomerDivisionsOnCreateAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerDivisionsOnCreateAddRemove() {
        mainApp().reopen();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions")).resolveLinks());

        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD).verify.value(
                tdCustomerNonIndividual.getTestData("DataGather").getTestData(
                        "Adjustment_Divisions").getTestData(DivisionsTab.class.getSimpleName()).getValue(
                        CustomerMetaData.DivisionsTab.BILLING_METHOD.getLabel()));

        log.info("TEST: Remove Divisions for Customer # " + customerNumber);
        customer.removeDivisions().perform();

        CustomerSummaryPage.tableDivisions.verify.present(false);
    }

    @Test(groups = "7.2_All_Enter/updateNon-IndividualCustomerGeneralInfo")
    @TestInfo(component = "CRM.Customer")
    public void testCustomerDivisionsOnAssociateDivisionsUncheck() {
        mainApp().reopen();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions")).resolveLinks());

        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD).verify.value(
                tdCustomerNonIndividual.getTestData("DataGather").getTestData(
                        "Adjustment_Divisions").getTestData(DivisionsTab.class.getSimpleName()).getValue(
                        CustomerMetaData.DivisionsTab.BILLING_METHOD.getLabel()));

        log.info("TEST: Uncheck Associate Divisions and Cancel this action for Customer # " + customerNumber);
        customer.associateDivisions().start();

        DivisionsTab.checkBoxAssociateDivisions.setValue(false);
        Page.dialogConfirmation.reject();
        GeneralTab.buttonSaveAndExit.click();

        CustomerSummaryPage.tableDivisions.verify.present();

        log.info("TEST: Uncheck Associate Divisions for Customer # " + customerNumber);
        customer.associateDivisions().perform(false);

        CustomerSummaryPage.tableDivisions.verify.present(false);

        NavigationPage.setActionAndGo("Update");
        NavigationPage.Verify.viewTabPresent("Divisions", false);
    }
}
