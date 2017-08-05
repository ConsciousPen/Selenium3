/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;

/**
 * Concrete workspace implementation.
 * Modify this class if workspace filling procedure has to be customized
 * (e.g. extra buttons need to be clicked during navigation between tabs).
 * @category Generated
 */
public class DefaultView extends Workspace {
    public DefaultView() {
        super();
        registerTab(PrefillTab.class);
		registerTab(GeneralTab.class);
		registerTab(DriverTab.class);
		registerTab(MembershipTab.class);
		registerTab(VehicleTab.class);
		registerTab(AssignmentTab.class);
		registerTab(FormsTab.class);
		registerTab(PremiumAndCoveragesTab.class);
		registerTab(DriverActivityReportsTab.class);
		registerTab(DocumentsAndBindTab.class);
		registerTab(ErrorTab.class);
		registerTab(PurchaseTab.class);
    }
}
