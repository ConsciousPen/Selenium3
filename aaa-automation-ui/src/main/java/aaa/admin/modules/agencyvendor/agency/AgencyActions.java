/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.agencyvendor.agency;

import aaa.admin.modules.agencyvendor.AgencyVendorActions;
import aaa.admin.modules.agencyvendor.agency.defaulttabs.SupportTeamTab;
import aaa.admin.modules.agencyvendor.agency.views.DefaultView;
import aaa.admin.pages.agencyvendor.AgencyVendorPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.admin.constants.AdminConstants;
import toolkit.datax.TestData;

public final class AgencyActions {
    private AgencyActions() {}

    public static class Update extends AgencyVendorActions.Update {

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        @Override
        public AbstractAction start(int rowNumber) {
            AgencyVendorPage.tableAgencies.getRow(rowNumber).getCell(AdminConstants.AdminAgenciesTable.ACTION).controls.links.getFirst().click();
            return this;
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            SupportTeamTab.buttonDone.click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }
    }
}
