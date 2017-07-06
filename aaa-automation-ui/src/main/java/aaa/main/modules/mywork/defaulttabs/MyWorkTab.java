/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.mywork.defaulttabs;

import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.main.metadata.MyWorkMetaData;

public class MyWorkTab extends DefaultTab {
    public MyWorkTab() {
        super(MyWorkMetaData.MyWorkTab.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
