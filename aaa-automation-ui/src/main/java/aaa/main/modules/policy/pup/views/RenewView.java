/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.pup.actiontabs.RenewActionTab;

/**
 * Shared workspace class for Renew action
 * @category Static
 */
public class RenewView extends Workspace {
    public RenewView() {
        super();
        registerTab(RenewActionTab.class);
    }
}
