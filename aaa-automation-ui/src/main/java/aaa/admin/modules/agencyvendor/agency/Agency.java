/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.agencyvendor.agency;

import aaa.admin.modules.agencyvendor.AgencyVendor;
import aaa.admin.modules.agencyvendor.agency.AgencyActions.Update;
import aaa.admin.modules.agencyvendor.agency.views.DefaultView;
import aaa.admin.pages.agencyvendor.AgencyVendorPage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;

public class Agency extends AgencyVendor {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        AgencyVendorPage.buttonAddNewAgency.click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.AGENCY_VENDOR.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.AGENCY_VENDOR_AGENCY.get());
    }

    @Override
    public AgencyActions.Update update() {
        return new Update();
    }
}
