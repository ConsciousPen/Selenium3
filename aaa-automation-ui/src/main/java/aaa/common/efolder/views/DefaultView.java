/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.efolder.views;

import aaa.common.Workspace;
import aaa.common.efolder.defaulttabs.AddExtFileTab;
import aaa.common.efolder.defaulttabs.AddFileTab;
import aaa.common.efolder.defaulttabs.ReidexFileTab;
import aaa.common.efolder.defaulttabs.RenameFileTab;

public class DefaultView extends Workspace {

    public DefaultView() {
        registerTab(AddFileTab.class);
        registerTab(AddExtFileTab.class);
        registerTab(RenameFileTab.class);
        registerTab(ReidexFileTab.class);
    }
}
