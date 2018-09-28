package aaa.admin.modules.reports.dashboard;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class Dashboard implements IDashboard {

    @Override
    public void search(TestData td) {
        throw new UnsupportedOperationException("search(TestData td) method is not supported for this Class.");
    }

    @Override
    public void navigate() {
        NavigationPage.toViewTab(NavigationEnum.ReportsTab.DASHBOARD.get());
    }
}
