package aaa.admin.modules.reports.operationalreports;

import org.openqa.selenium.By;
import aaa.admin.modules.reports.operationalreports.views.DefaultView;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;

public class OperationalReport implements IOperationalReport {

    @Override
    public void navigate() {
        NavigationPage.toViewTab(NavigationEnum.ReportsTab.OPERATIONAL_REPORTS.get());
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
}
