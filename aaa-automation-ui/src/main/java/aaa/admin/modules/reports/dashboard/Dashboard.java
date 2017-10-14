package aaa.admin.modules.reports.dashboard;

import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.etcsa.base.app.LoginPage;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;

public class Dashboard implements IDashboard {

    @Override
    public void search(TestData td) {
        throw new UnsupportedOperationException("search(TestData td) method is not supported for this Class.");
    }

    @Override
    public void navigate() {
        if (!NavigationPage.isMainTabSelected(NavigationEnum.AdminAppMainTabs.REPORTS.get())) {
            NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.REPORTS.get());
            loginToReports();
        }
        NavigationPage.toViewTab(NavigationEnum.ReportsTab.DASHBOARD.get());
    }

    private void loginToReports() {
        if (!Tab.labelLoggedUser.isPresent()) {
            CSAAApplicationFactory.get().adminApp(new LoginPage(
                    PropertyProvider.getProperty(TestProperties.EU_USER),
                    PropertyProvider.getProperty(TestProperties.EU_PASSWORD))).getLogin().login();
        }
    }
}
