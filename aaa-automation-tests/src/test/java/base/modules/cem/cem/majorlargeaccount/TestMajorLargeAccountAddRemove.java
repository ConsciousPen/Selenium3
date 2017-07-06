/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem.majorlargeaccount;

import org.testng.annotations.Test;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.metadata.cem.CemMetaData.SearchByField;
import aaa.admin.pages.cem.CemPage;
import aaa.main.enums.CEMConstants;
import base.modules.cem.cem.CemBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer MajorLargeAccount Add Remove
 * @scenario
 * 1. Navigate to Admin application
 * 2. Navigate to CEM tab
 * 3. Navigate to Major Large Account subtab in left menu
 * 4. Add New Major Large Account
 * 5. Verify that Account is added to table
 * 6. Remove account
 * 5. Verify that Account is removed
 * @details
 */
public class TestMajorLargeAccountAddRemove extends CemBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testMajorLargeAccountAddRemove() {
        adminApp().open();

        log.info("TEST: Create New Major/Large Account");
        String accountId = majorAccount.createWithId(tdMajorLargeAccount.getTestData("MajorLargeAccount", "TestData"));

        majorAccount.search(tdMajorLargeAccount.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.ACCOUNT_ID.getLabel()), accountId));

        String accountDesignationType = tdMajorLargeAccount.getValue("MajorLargeAccount", "TestData",
                CemMetaData.CreateMajorLargeAccountTab.class.getSimpleName(),
                CemMetaData.CreateMajorLargeAccountTab.ACCOUNT_DESIGNATION_TYPE.getLabel());

        CemPage.tableMajorLargeAccount.getRow(1).getCell(CEMConstants.CEMMajorLargeAccountTable.DESIGNATION_TYPE).verify.value(accountDesignationType);

        log.info("STEP: Remove Major/Large Account");
        majorAccount.deleteMajorLargeAccount().perform(1);

        majorAccount.search(tdMajorLargeAccount.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                SearchByField.class.getSimpleName(), SearchByField.ACCOUNT_ID.getLabel()), accountId));

        CemPage.tableMajorLargeAccount.getRow(1).verify.present(false);
    }
}
