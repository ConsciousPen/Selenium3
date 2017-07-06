/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.home_ss.actiontabs.AuthorityActionTab;
import aaa.main.modules.policy.home_ss.actiontabs.EndorsementActionTab;

/**
 * Shared workspace class for Endorse action
 * @category Static
 */
public class EndorseView extends Workspace {
    public EndorseView() {
        super();
        registerTab(AuthorityActionTab.class);
        registerTab(EndorsementActionTab.class);
    }
}
