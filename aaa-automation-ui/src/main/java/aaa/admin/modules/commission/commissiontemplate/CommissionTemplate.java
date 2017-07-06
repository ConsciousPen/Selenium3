/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissiontemplate;

import aaa.admin.modules.commission.commissiontemplate.CommissionTemplateActions.DeleteCommissionTemplate;
import aaa.admin.modules.commission.commissiontemplate.CommissionTemplateActions.EditCommissionTemplate;
import aaa.admin.modules.commission.commissiontemplate.views.DefaultView;
import aaa.admin.pages.commission.CommissionTemplatePage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class CommissionTemplate implements ICommissionTemplate {
    private Workspace defaultView = new DefaultView();
    private String type;

    public CommissionTemplate(String type) {
        this.type = type;
    }

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        CommissionTemplatePage.comboboxCommissionTemplateType.setValue(type);
        CommissionTemplatePage.buttonAddNewCommissionTemplate.click();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        CommissionTemplatePage.buttonOK.click();
        log.info("Created Commission Template " + td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.COMMISSION_COMMISSION_TEMPLATE.get());
    }

    @Override
    public void search(TestData td) {
        CommissionTemplatePage.search(td);
    }

    @Override
    public EditCommissionTemplate edit() {
        return new EditCommissionTemplate();
    }

    @Override
    public DeleteCommissionTemplate delete() {
        return new DeleteCommissionTemplate();
    }
}
