/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.campaigns.views;

import aaa.admin.modules.cem.campaigns.actiontabs.UpdateCampaignActionTab;
import aaa.common.Workspace;

public class UpdateCampaignView extends Workspace {
    public UpdateCampaignView() {
        super();
        registerTab(UpdateCampaignActionTab.class);
    }
}
