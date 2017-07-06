/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test Customer General Info
 * @scenario
 * 1. Create Non-Individual Customer with specific values in General Info section
 * 2. Verify entered values on Customer Summary page
 * @details
 */

public class TestCustomerGeneralInfo extends CustomerBaseTest {

    private TestData tdCustomerAdjusted = tdCustomerNonIndividual.getTestData("DataGather", "TestData")
            .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_GeneralInfo")).resolveLinks();

    @Test(groups = "7.2_All_Enter/updateNon-IndividualCustomerGeneralInfo")
    @TestInfo(component = "CRM.Customer")
    public void testCustomerGeneralInfo() {
        mainApp().open();

        String customerNameLegal = tdCustomerAdjusted.getValue(CustomerMetaData.GeneralTab.class.getSimpleName(),
                CustomerMetaData.GeneralTab.NAME_LEGAL.getLabel());
        String customerNameDba = tdCustomerAdjusted.getValue(CustomerMetaData.GeneralTab.class.getSimpleName(),
                CustomerMetaData.GeneralTab.NAME_DBA.getLabel());

        customer.createViaUI(tdCustomerAdjusted);

        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        CustomAssert.enableSoftMode();

        CustomerSummaryPage.labelCustomerName.verify.value(customerNameLegal);
        CustomerSummaryPage.lableCustomerType.verify.value(tdCustomerAdjusted.getValue(
                CustomerMetaData.GeneralTab.class.getSimpleName(),
                CustomerMetaData.GeneralTab.NON_INDIVIDUAL_TYPE.getLabel()));
        CustomerSummaryPage.tableCustomerInformation.getRow(1).getCell(CustomerConstants.CustomerInformationTable.NAME_DBA).verify.value(customerNameDba);

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }
}
