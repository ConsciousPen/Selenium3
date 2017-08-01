package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;

public class ApplicantEndorsementTab extends ApplicantTab {

	@Override
    public Tab submitTab() {
    	NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        return this;
    }
}
