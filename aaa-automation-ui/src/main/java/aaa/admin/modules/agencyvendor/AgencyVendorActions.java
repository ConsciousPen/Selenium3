/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.agencyvendor;

import aaa.common.AbstractAction;
import toolkit.datax.TestData;

public final class AgencyVendorActions {

    private AgencyVendorActions() {}

    public abstract static class Update extends AbstractAction {
        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        public AbstractAction start(int rowNumber) {
            return this;
        }

        @Override
        public AbstractAction submit() {
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            return this;
        }
    }
}
