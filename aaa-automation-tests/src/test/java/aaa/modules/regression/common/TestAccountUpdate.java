/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.common;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.AccountMetaData;
import aaa.main.pages.summary.CustomerSummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.modules.BaseTest;
import aaa.common.pages.NavigationPage;
import aaa.common.enums.NavigationEnum;
import aaa.main.modules.account.Account;
import aaa.main.modules.account.actiontabs.AcctInfoTab;


/**
 * @author N. Belakova
 * @name Test Update Account
 * @scenario
 * 1. Create Customer / Account 
 * 2. Update Customer
 * 3. Add second Customer to Account
 * 4. Update Account
 * @details
 */
public class TestAccountUpdate extends BaseTest {

    @Parameters({"state"})
	@Test(groups = { Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testAccountUpdate(@Optional("") String state) {
    	
        mainApp().open();

        String customertNumber = createCustomerIndividual(getTestSpecificTD("TD_CreateCustomer1"));
        
        log.info("Customer created: "+customertNumber);
        
        
        customer.update().perform(getTestSpecificTD("TD_UpdateCustomer1"));
        
        log.info("Customer updated: "+customertNumber);
        
        
        NavigationPage.toViewTab(NavigationEnum.CustomerSummaryTab.ACCOUNT.get()); 
        CustomerSummaryPage.buttonAddCustomer.click();
        
        customer.getDefaultView().fill(getTestSpecificTD("TD_CreateCustomer2"));
        
        log.info("Second Customer created in Account");
        
        
        new Account().update().perform(getTestSpecificTD("TD_UpdateAccount"));
        
        String accountName = getTestSpecificTD("TD_UpdateAccount").getTestData(TestData.makeKeyPath(new AcctInfoTab().getMetaKey())).getValue(AccountMetaData.AcctInfoTab.ACCOUNT_NAME.getLabel());
        CustomerSummaryPage.labelAccountName.verify.value(accountName);
        
        log.info("Account updated with Name: "+accountName);
        
    }
}
