/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.JobRunner;
import aaa.admin.pages.general.GeneralSchedulerPage.Job;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoSSMetaData.ChangeBrokerActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Viachaslau Markouski
 * @name Test Change Broker Inter Agency for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (Preconfigured) Policy
 * 3. Change Broker Inter Agency for Policy
 * 4. Verify Agency name was not changed
 * 5. Shift time to next day
 * 6. Go to com.exigen.ipb.etcsa.admin's panel and run 'policyBORTransferJob' job
 * 7. Verify Agency name was changed
 * @details
 */
public class TestPolicyChangeBrokerInterAgencyOther extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyChangeBrokerInterAgencyOther() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        String initialBrokerName = PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(
                "Agency / Producer").getValue();

        log.info("TEST: Change Broker Inter Agency for Policy #" + policyNumber);
        policy.changeBrokerRequest().perform(tdPolicy.getTestData("ChangeBroker", "TestDataInter_Other_Plus1Day"));

        String newBrokerName = tdPolicy.getTestData("ChangeBroker", "TestDataInter_Other_Plus1Day").getValue(
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
                "Agency / Producer").verify.value(newBrokerName);
    }
}
