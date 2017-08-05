/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.productfactory.policy.views;

import aaa.admin.modules.product.productfactory.policy.defaulttabs.ActionsTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.ComponentsTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.ConsolidationTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.DecisionTablesTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.HomeTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.NewProductTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.PropertiesTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.RuleSetsTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.RulesTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.TemplatesTab;
import aaa.admin.modules.product.productfactory.policy.defaulttabs.WorkspacesTab;
import aaa.common.Workspace;

public class DefaultView extends Workspace {

    public DefaultView() {
        registerTab(NewProductTab.class);
        registerTab(HomeTab.class);
        registerTab(PropertiesTab.class);
        registerTab(ActionsTab.class);
        registerTab(ComponentsTab.class);
        registerTab(WorkspacesTab.class);
        registerTab(DecisionTablesTab.class);
        registerTab(ConsolidationTab.class);
        registerTab(TemplatesTab.class);
        registerTab(RulesTab.class);
        registerTab(RuleSetsTab.class);
    }
}
