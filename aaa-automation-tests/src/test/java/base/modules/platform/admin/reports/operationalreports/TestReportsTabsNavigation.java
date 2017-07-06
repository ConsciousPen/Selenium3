package base.modules.platform.admin.reports.operationalreports;

import org.testng.annotations.Test;

import aaa.admin.modules.reports.dashboard.DashboardType;
import aaa.admin.modules.reports.dashboard.IDashboard;
import aaa.admin.modules.reports.templates.ITemplate;
import aaa.admin.modules.reports.templates.TemplateType;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test for Report Tabs navigation
 * @scenario
 * 1. Login to Operational Reports
 * 2. Open Templates tab
 * 3. Check that Templates tab is opened
 * 4. Open Dashboard tab
 * 5. Check that Dashboard tab is opened
 * @details
 */

public class TestReportsTabsNavigation extends BaseTest {

    private TemplateType templateType = TemplateType.TEMPLATE;
    private ITemplate template = templateType.get();
    private DashboardType dashboardType = DashboardType.DASHBOARD;
    private IDashboard dashboard = dashboardType.get();

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testReportsTabsNavigation() {

        opReportApp().open();

        template.navigate();
        NavigationPage.Verify.viewTabPresent(NavigationEnum.ReportsTab.TEMPLATES.get(), true);

        dashboard.navigate();
        NavigationPage.Verify.viewTabPresent(NavigationEnum.ReportsTab.DASHBOARD.get(), true);

    }
}
