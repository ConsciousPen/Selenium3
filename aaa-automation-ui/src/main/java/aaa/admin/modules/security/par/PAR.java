/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security.par;

import java.util.ArrayList;
import java.util.List;

import aaa.EntityLogger;
import aaa.admin.modules.security.par.defaulttabs.GeneralPARTab;
import aaa.admin.modules.security.par.views.DefaultView;
import aaa.admin.pages.security.PARPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AdminAppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ActionConstants;
import aaa.admin.constants.AdminConstants;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.table.Row;

public class PAR implements IPAR {
    private Workspace defaultView = new DefaultView();

    public PAR() {}

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        PARPage.buttonAddPAR.click();
    }

    @Override
    public void search(TestData td) {
        PARPage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.SECURITY.get());
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.SECURITY_PRODUCT_ACCESS_ROLE.get());
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.PAR);
        GeneralPARTab.buttonSave.click();
        log.info("Created PAR " + entity);
    }

    @Override
    public void update(TestData td, int rowNumber) {
        PARPage.tableSearchResults.getRow(rowNumber).getCell(AdminConstants.AdminSearchResultsTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();
        getDefaultView().fill(td);
        GeneralPARTab.buttonUpdate.click();
    }

    public List<String> searchForRoles() {
        navigate();
        List<String> roles = new ArrayList<>();
        PARPage.buttonSearch.click();
        if (PARPage.tableSearchResults.isPresent()) {
            for (Row row : PARPage.tableSearchResults.getRows()) {
                String par = String.format("%s (%s)", row.getCell(AdminConstants.AdminSearchResultsTable.NAME).getValue(), row.getCell(AdminConstants.AdminSearchResultsTable.CODE).getValue());
                roles.add(par);
            }
        }
        return roles;
    }

}
