/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.agencyvendor;

import aaa.utils.EntityLogger;
import aaa.admin.modules.agencyvendor.agency.defaulttabs.SupportTeamTab;
import aaa.admin.pages.agencyvendor.AgencyVendorPage;
import toolkit.datax.TestData;

public abstract class AgencyVendor implements IAgencyVendor {

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.AGENCY_VENDOR);
        SupportTeamTab.buttonDone.click();
        log.info("Created " + entity);
    }

    @Override
    public void search(TestData td) {
        AgencyVendorPage.search(td);
    }

    public String createAgency(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.AGENCY_NAME);
        SupportTeamTab.buttonDone.click();
        log.info("Created Agency with Name:" + entity);

        return entity;
    }
}
