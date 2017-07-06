/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.cem;

import aaa.admin.modules.cem.campaigns.Campaign;
import aaa.admin.modules.cem.campaigns.CampaignType;
import aaa.admin.modules.cem.groupsinformation.GroupInformation;
import aaa.admin.modules.cem.groupsinformation.GroupInformationType;
import aaa.admin.modules.cem.majorlargeaccount.MajorLargeAccount;
import aaa.admin.modules.cem.majorlargeaccount.MajorLargeAccountType;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class CemBaseTest extends BaseTest {

    private CampaignType campaignType = CampaignType.CAMPAIGNS;
    protected Campaign campaign = new Campaign();
    protected TestData tdCampaign = testDataManager.campaign.get(campaignType);

    private MajorLargeAccountType majorLargeAccountType = MajorLargeAccountType.MAJOR_LARGE_ACCOUNT;
    protected MajorLargeAccount majorAccount = new MajorLargeAccount();
    protected TestData tdMajorLargeAccount = testDataManager.majorLargeAccount.get(majorLargeAccountType);

    private GroupInformationType groupsInformationType = GroupInformationType.GROUPS_INFORMATION;
    protected GroupInformation groupsInformation = new GroupInformation();
    protected TestData tdGroupsInformation = testDataManager.groupsInformation.get(groupsInformationType);

    /**
     * System take some time after starting of campaign to establish new communication with customer and show it on com.exigen.ipb.etcsa.
     * @param time
     */
    protected void synchonizeCustomerCommunicationTab(int time, int expectedRowCount) {
        int count = 0;
        while ((CommunicationActionTab.tableCommunications.getRowsCount() < expectedRowCount)
                && (count < time)) {
            CustomerSummaryPage.linkCommunicationTab.click();
            count++;
        }
    }
}
