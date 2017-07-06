/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.auto_ss.actiontabs.NonPremiumBearingEndorsementActionTab;
import aaa.main.modules.policy.auto_ss.actiontabs.StartNonPremiumBearingEndorsementActionTab;

/**
 * Concrete workspace implementation.
 * Modify this class if workspace filling procedure has to be customized
 * (e.g. extra buttons need to be clicked during navigation between tabs).
 * @category Generated
 */
public class NonPremiumBearingEndorsementView extends Workspace {
    public NonPremiumBearingEndorsementView() {
        super();
        registerTab(StartNonPremiumBearingEndorsementActionTab.class);
        registerTab(NonPremiumBearingEndorsementActionTab.class);
    }
}
