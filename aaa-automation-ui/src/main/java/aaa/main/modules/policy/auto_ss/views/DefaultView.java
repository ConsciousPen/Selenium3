/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.auto_ss.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTabCalculatePremium;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;

/**
 * Concrete workspace implementation. Modify this class if workspace filling
 * procedure has to be customized (e.g. extra buttons need to be clicked during
 * navigation between tabs).
 * 
 * @category Generated
 */
public class DefaultView extends Workspace {
	public DefaultView() {
		registerTab(PrefillTab.class);
		registerTab(GeneralTab.class);
		registerTab(DriverTab.class);
		registerTab(RatingDetailReportsTab.class);
		registerTab(VehicleTab.class);
		registerTab(AssignmentTab.class);
		registerTab(FormsTab.class);
		registerTab(PremiumAndCoveragesTab.class);
		registerTab(ErrorTabCalculatePremium.class);
		registerTab(DriverActivityReportsTab.class);
		registerTab(DocumentsAndBindTab.class);
		registerTab(ErrorTab.class);
		registerTab(PurchaseTab.class);
	}
}
