/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.JobRunner;
import aaa.admin.pages.general.GeneralSchedulerPage.Job;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoSSMetaData.ChangeBrokerActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Viachaslau Markouski
 * @name Test Change Broker Inter Agency for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Change Broker Inter Agency for Policy
 * 4. Verify Broker name was not changed
 * 5. Shift time to next day
 * 6. Run 'policyBORTransferJob' job in com.exigen.ipb.etcsa.admin panel
 * 7. Verify Broker name was changed
 * @details
 */
public class TestPolicyChangeBrokerInterAgencyOther extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyChangeBrokerInterAgencyOther() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        String initialBrokerName = PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(
                "Brokerage").getValue();

        log.info("TEST: Change Broker Inter Agency for Policy #" + policyNumber);
        policy.changeBrokerRequest().perform(getPolicyTD("ChangeBroker", "TestDataInter_Other_Plus1Day"));

        String newBrokerName = getPolicyTD("ChangeBroker", "TestDataInter_Other_Plus1Day").getValue(
                ChangeBrokerActionTab.class.getSimpleName(),
                ChangeBrokerActionTab.LOCATION_NAME.getLabel(),
                ChangeBrokerActionTab.ChangeLocationMetaData.AGENCY_NAME.getLabel());

        CustomAssert.assertFalse(String.format("Broker name was changed from %s to %s", newBrokerName, initialBrokerName),
                initialBrokerName.equals(newBrokerName));

        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.POLICY_BOR_TRANSFER_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);

        PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(
                "Brokerage").verify.value(newBrokerName);
    }
}
