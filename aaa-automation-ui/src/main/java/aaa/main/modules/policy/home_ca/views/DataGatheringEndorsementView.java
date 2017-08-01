/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PersonalPropertyTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.UnderwritingAndApprovalTab;

/**
 * Concrete workspace implementation.
 * Modify this class if workspace filling procedure has to be customized
 * (e.g. extra buttons need to be clicked during navigation between tabs).
 * @category Generated
 */
public class DataGatheringEndorsementView extends Workspace {
    public DataGatheringEndorsementView() {
        super();
        registerTab(GeneralTab.class);
		registerTab(ApplicantTab.class);
		registerTab(ReportsTab.class);
		registerTab(PropertyInfoTab.class);
		registerTab(EndorsementTab.class);
		registerTab(PersonalPropertyTab.class);
		registerTab(PremiumsAndCoveragesQuoteTab.class);
		registerTab(MortgageesTab.class);
		registerTab(UnderwritingAndApprovalTab.class);
		registerTab(DocumentsTab.class);
		registerTab(BindTab.class);
		registerTab(PurchaseTab.class);
    }
}
