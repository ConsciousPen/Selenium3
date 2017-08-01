/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;

public class ApplicantEndorsementTab extends ApplicantTab {
	
	@Override
    public Tab submitTab() {
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        return this;
    }
}
