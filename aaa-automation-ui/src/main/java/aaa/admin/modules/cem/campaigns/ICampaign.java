/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.campaigns;

import aaa.admin.modules.IAdmin;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface ICampaign extends IAdmin {

    Workspace getDefaultView();

    void create(TestData td);

    void initiate();

    CampaignActions.StartCampaign startCampaign();

    CampaignActions.UpdateCampaign updateCampaign();

    CampaignActions.EndCampaign endCampaign();

    CampaignActions.SuspendCampaign suspendCampaign();

    CampaignActions.RestartCampaign restartCampaign();

    CampaignActions.CopyCampaign copyCampaign();

    CampaignActions.CreateChildCampaign createChildCampaign();

    CampaignActions.ArchiveCampaign archiveCampaign();
}
