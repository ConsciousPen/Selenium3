/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import java.util.List;

import org.testng.annotations.Test;

import aaa.common.enums.SearchEnum;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Uladzimir Shvets
 * @name Test Policy Spin
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (Preconfigured) Policy with 3 Drivers and 3 Vehicles
 * 3. Spin policy(Select 1 driver and 1 Vehicle)
 * 4. Search new policy
 * 5. Verify spinned driver and vehicle in new policy
 * 6. Issue new policy
 * 7. Make Spin endorsement
 * 8. Search 1st policy
 * 9. Calculate premium and issue spin
 * 10. Verify drivers and vehicles
 * @details
 */
public class TestPolicySpin extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicySpin() {
        mainApp().open();

        createCustomerIndividual();
        String customerID = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        //TODO(ushvets): remove list adjustment form test
        TestData t = getTestSpecificTD("TestData").getTestDataList("DriverTab").get(1).adjust("Last Name", "FirstDriverLName" + customerID);
        List<TestData> list = getTestSpecificTD("TestData").getTestDataList("DriverTab");
        list.set(1, t);

        policy.createPolicy(getTestSpecificTD("TestData").adjust("DriverTab", list)
                .adjust(getPolicyTD("Issue", "TestData").resolveLinks()));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Spin Policy #" + policyNumber);
        policy.policySpin().perform(getTestSpecificTD("TestData").resolveLinks());

        SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.LAST_NAME, "FirstDriverLName" + customerID);
        String newPolicyNumber = CustomerSummaryPage.tableQuotes.getRow(1).getCell(CustomerConstants.CustomerQuotesTable.QUOTE).getValue();
        MainPage.QuickSearch.search(newPolicyNumber);

        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "FirstDriverFName FirstDriverLName" + customerID).verify.present();
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "SecondDriverFName SecondDriverLName").verify.present(false);
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, customerName).verify.present(false);

        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "ACURA").verify.present();
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "AUDI").verify.present(false);
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "FORD").verify.present(false);

        policy.dataGather().perform(getTestSpecificTD("DataGather"));
//        policy.issue().perform(getPolicyTD("Issue", "TestData"));
        policy.purchase(getPolicyTD("DataGather", "TestData"));

        MainPage.QuickSearch.search(policyNumber);

        PolicySummaryPage.buttonPendedEndorsement.click();
//        policy.calculatePremium(td);
//        PolicySummaryPage.buttonPendedEndorsement.click();
//        policy.issue().perform(getPolicyTD("Issue", "TestDataWOPaymentPlan"));
        policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestDataWOPaymentPlan"));

        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "FirstDriverFName FirstDriverLName" + customerID).verify.present(false);
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, "SecondDriverFName SecondDriverLName").verify.present();
        PolicySummaryPage.tablePolicyDrivers.getRow(PolicyConstants.PolicyDriversTable.NAME, customerName).verify.present();

        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "ACURA").verify.present(false);
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "AUDI").verify.present();
        PolicySummaryPage.tablePolicyVehicles.getRow(PolicyConstants.PolicyVehiclesTable.MAKE, "FORD").verify.present();
    }
}
