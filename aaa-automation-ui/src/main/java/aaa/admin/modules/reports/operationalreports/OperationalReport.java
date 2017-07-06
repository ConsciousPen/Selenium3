package aaa.admin.modules.reports.operationalreports;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.base.app.ApplicationFactory;
import com.exigen.ipb.etcsa.base.app.LoginPage;
import com.exigen.ipb.etcsa.base.config.CustomTestProperties;

import aaa.admin.modules.reports.operationalreports.views.DefaultView;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;

public class OperationalReport implements IOperationalReport {

    @Override
    public void navigate() {
        if (!NavigationPage.isMainTabSelected(NavigationEnum.AdminAppMainTabs.REPORTS.get())) {
            NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.REPORTS.get());
            loginToReports();
        }
        NavigationPage.toViewTab(NavigationEnum.ReportsTab.OPERATIONAL_REPORTS.get());
        loginToReports();
    }

    @Override
    public void initiate(TestData td) {
        navigate();
    }

    @Override
    public Workspace getDefaultView() {
        return new DefaultView();
    }

    @Override
    public void create(TestData td) {
        initiate(td);
        getDefaultView().fill(td);

        new Button(By.id("jobsForm:generateReport")).click(Waiters.AJAX.then(Waiters.SLEEP(5000)));
    }

    @Override
    public void schedule(TestData td) {
        initiate(td);
        getDefaultView().fill(td);

        new Button(By.id("jobsForm:schedule")).click();
    }

    @Override
    public void search(TestData td) {
        throw new UnsupportedOperationException("search(TestData td) method is not supported for this Class.");
    }

    private void loginToReports() {
        if (!Tab.labelLoggedUser.isPresent()) {
            if (PropertyProvider.getProperty(CustomTestProperties.OR_URL_TEMPLATE).isEmpty()) {
                LoginPage.textBoxLogin.setValue(PropertyProvider.getProperty(TestProperties.EU_USER));
                LoginPage.textBoxPassword.setValue(PropertyProvider.getProperty(TestProperties.EU_PASSWORD));
                LoginPage.buttonLogin.click();
            } else {
                ApplicationFactory.get().opReportApp(new LoginPage(
                        PropertyProvider.getProperty(TestProperties.EU_USER),
                        PropertyProvider.getProperty(TestProperties.EU_PASSWORD))).getLogin().login();
            }
        }
    }
}
