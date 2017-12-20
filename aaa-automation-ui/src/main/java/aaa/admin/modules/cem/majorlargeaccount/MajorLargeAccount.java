/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.majorlargeaccount;

import aaa.utils.EntityLogger;
import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.modules.cem.majorlargeaccount.MajorLargeAccountActions.DeleteMajorLargeAccount;
import aaa.admin.modules.cem.majorlargeaccount.MajorLargeAccountActions.EditMajorLargeAccount;
import aaa.admin.modules.cem.majorlargeaccount.defaulttabs.CreateMajorLargeAccountTab;
import aaa.admin.modules.cem.majorlargeaccount.views.DefaultView;
import aaa.admin.pages.cem.CemPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AdminAppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class MajorLargeAccount implements IMajorLargeAccount {

    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.MAJOR_LARGE_ACCOUNT);
        CreateMajorLargeAccountTab.buttonSave.click();
        log.info("Created Major/Large Account " + entity);
    }

    @Override
    public void initiate() {
        navigate();
        CemPage.buttonCreateNewMajorLargeAccount.click();
    }

    @Override
    public void search(TestData td) {
        CemPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.CEM.get());
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.CEM_MAJOR_LARGE_ACCOUNT.get());
    }

    public String createWithId(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String accountId = new CreateMajorLargeAccountTab().getAssetList().getAsset(CemMetaData.CreateMajorLargeAccountTab.MAJOR_LARGE_ACCOUNT_ID.getLabel()).getValue().toString();
        CreateMajorLargeAccountTab.buttonSave.click();
        return accountId;
    }

    @Override
    public EditMajorLargeAccount editMajorLargeAccount() {
        return new MajorLargeAccountActions.EditMajorLargeAccount();
    }

    @Override
    public DeleteMajorLargeAccount deleteMajorLargeAccount() {
        return new MajorLargeAccountActions.DeleteMajorLargeAccount();
    }
}
