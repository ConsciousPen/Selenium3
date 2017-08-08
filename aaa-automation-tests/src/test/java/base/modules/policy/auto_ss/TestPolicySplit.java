/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import org.testng.annotations.Test;

import aaa.main.enums.SearchEnum;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Uladzimir Shvets
 * @name Test Policy Split
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (Preconfigured) Policy with 3 Drivers and 3 Vehicles
 * 3. Split policy(Select 1 driver and 1 Vehicle)
 * 4. Calculate premium and issue split
 * 5. Verify drivers and vehicles
 * 6. Search new policy
 * 7. Issue new policy
 * 8. Verify spinned driver and vehicle in new policy
 * @details
 */
public class TestPolicySplit extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicySplit() {
        mainApp().open();

        createCustomerIndividual();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        policy.createPolicy(getTestSpecificTD("TestData")
                .adjust(getPolicyTD("Issue", "TestData").resolveLinks()));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Spin Policy #" + policyNumber);

        policy.policySplit().perform(getTestSpecificTD("TestData").resolveLinks());

        PolicySummaryPage.buttonPendedEndorsement.click();
        policy.dataGather().perform(getTestSpecificTD("DataGather_AfterSplitForOneInsured"));
        PolicySummaryPage.buttonPendedEndorsement.click();
//        policy.calculatePremium(td);
//        PolicySummaryPage.buttonPendedEndorsement.click();
//        policy.issue().perform(getPolicyTD("Issue", "TestDataWOPaymentPlan"));
        policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestDataWOPaymentPlan"));

        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "FirstDriverFName FirstDriverLName").verify.present();
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "SecondDriverFName SecondDriverLName").verify.present();
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, customerName).verify.present(false);

        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "ACURA").verify.present(false);
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "AUDI").verify.present();
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "FORD").verify.present();

        SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.FIRST_NAME, customerName.split(" ")[0]);
        SearchPage.selectSearchedResult(2);

        String newPolicyNumber = CustomerSummaryPage.tableQuotes.getRow(1).getCell(CustomerConstants.CustomerQuotesTable.QUOTE).getValue();
        MainPage.QuickSearch.search(newPolicyNumber);

        policy.dataGather().perform(getTestSpecificTD("DataGather"));
//        policy.issue().perform(getPolicyTD("Issue", "TestData"));
        policy.purchase(getPolicyTD("DataGather", "TestData"));

        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "FirstDriverFName FirstDriverLName").verify.present(false);
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "SecondDriverFName SecondDriverLName").verify.present(false);
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, customerName).verify.present();

        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "ACURA").verify.present();
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "AUDI").verify.present(false);
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "FORD").verify.present(false);
    }
}
