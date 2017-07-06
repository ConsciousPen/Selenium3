/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.productfactory.policy;

import aaa.admin.modules.product.productfactory.ProductFactoryActions;
import aaa.admin.modules.product.productfactory.policy.views.DefaultView;
import aaa.common.Workspace;

public final class ProductFactoryPolicyActions {

    public static class Copy extends ProductFactoryActions.Copy {

        @Override
        public String getName() {
            return "Copy";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

    }

    public static class Update extends ProductFactoryActions.Update {

        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

    }

    public static class Inquiry extends ProductFactoryActions.Inquiry {

        @Override
        public String getName() {
            return "Inquiry";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

    }
}
