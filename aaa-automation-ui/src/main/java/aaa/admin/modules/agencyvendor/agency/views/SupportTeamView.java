/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.agencyvendor.agency.views;

import aaa.admin.modules.agencyvendor.agency.defaulttabs.SupportTeamTab;
import aaa.common.Workspace;

public class SupportTeamView extends Workspace {

    public SupportTeamView() {
        super();
        registerTab(SupportTeamTab.class);
    }
}
