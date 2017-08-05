/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.cem.campaigns;

import aaa.EntityLogger;
import aaa.admin.modules.cem.campaigns.CampaignActions.ArchiveCampaign;
import aaa.admin.modules.cem.campaigns.CampaignActions.CopyCampaign;
import aaa.admin.modules.cem.campaigns.CampaignActions.CreateChildCampaign;
import aaa.admin.modules.cem.campaigns.CampaignActions.EndCampaign;
import aaa.admin.modules.cem.campaigns.CampaignActions.RestartCampaign;
import aaa.admin.modules.cem.campaigns.CampaignActions.StartCampaign;
import aaa.admin.modules.cem.campaigns.CampaignActions.SuspendCampaign;
import aaa.admin.modules.cem.campaigns.CampaignActions.UpdateCampaign;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.admin.modules.cem.campaigns.views.DefaultView;
import aaa.admin.pages.cem.CampaignPage;
import aaa.admin.pages.cem.CemPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AdminAppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class Campaign implements ICampaign {

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.CAMPAIGN);
        CreateCampaignTab.buttonSave.click();
        log.info("Created Campaign " + entity);
    }

    @Override
    public void initiate() {
        navigate();
        CampaignPage.buttonCreateNewCampaign.click();
    }

    @Override
    public void search(TestData td) {
        CemPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.CEM.get());
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.CEM_CAMPAIGNS.get());
    }

    @Override
    public StartCampaign startCampaign() {
        return new CampaignActions.StartCampaign();
    }

    @Override
    public UpdateCampaign updateCampaign() {
        return new CampaignActions.UpdateCampaign();
    }

    @Override
    public EndCampaign endCampaign() {
        return new CampaignActions.EndCampaign();
    }

    @Override
    public SuspendCampaign suspendCampaign() {
        return new CampaignActions.SuspendCampaign();
    }

    @Override
    public RestartCampaign restartCampaign() {
        return new CampaignActions.RestartCampaign();
    }

    @Override
    public CopyCampaign copyCampaign() {
        return new CampaignActions.CopyCampaign();
    }

    @Override
    public CreateChildCampaign createChildCampaign() {
        return new CampaignActions.CreateChildCampaign();
    }

    @Override
    public ArchiveCampaign archiveCampaign() {
        return new CampaignActions.ArchiveCampaign();
    }
}
