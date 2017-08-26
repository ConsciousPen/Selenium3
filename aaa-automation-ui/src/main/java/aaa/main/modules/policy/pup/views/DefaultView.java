/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ClaimsTab;
import aaa.main.modules.policy.pup.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.pup.defaulttabs.EndorsementsTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchasePaymentMethodTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAllResidentsTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksOtherVehiclesTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksPropertyTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderwritingAndApprovalTab;


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
        registerTab(UnderlyingRisksPropertyTab.class);
        registerTab(UnderlyingRisksAutoTab.class);
        registerTab(UnderlyingRisksOtherVehiclesTab.class);
        registerTab(UnderlyingRisksAllResidentsTab.class);
        registerTab(ClaimsTab.class);
        registerTab(EndorsementsTab.class);
        registerTab(PremiumAndCoveragesQuoteTab.class);
        registerTab(UnderwritingAndApprovalTab.class);
        registerTab(DocumentsTab.class);
        registerTab(BindTab.class);
        registerTab(ErrorTab.class);
        registerTab(PurchaseTab.class);
        registerTab(PurchasePaymentMethodTab.class);
    }
}
