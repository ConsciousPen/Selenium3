/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.enums.CustomerConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Qualify In Qualification
 * @scenario
 * 1. Create Non-Individual Customer with In Qualification status
 * 2. Perform Qualify action
 * 3. Verify that Lead Status = Qualified
 * @details
 */
public class TestCustomerQualifyInQualification extends CustomerBaseTest {

    @Test(groups = "7.2_All_Enter/updateNon-IndividualCustomerGeneralInfo")
    @TestInfo(component = "CRM.Customer")
    public void testCustomerQualifyInQualification() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData_WithoutDivisionsTab").adjust(
                tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_InQualification")));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        CustomerSummaryPage.labelCustomerLeadStatus.verify.value(CustomerConstants.IN_QUALIFICATION);

        log.info("TEST: Qualify Customer # " + customerNumber);
        customer.qualify().perform();
        CustomerSummaryPage.labelCustomerLeadStatus.verify.value(CustomerConstants.QUALIFIED);
    }
}
