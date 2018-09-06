package aaa.admin.modules.reports.templates;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;

public class Template implements ITemplate {

	@Override
	public void search(TestData td) {
		throw new UnsupportedOperationException("search(TestData td) method is not supported for this Class.");
	}

	@Override
	public void navigate() {
		if (!NavigationPage.isMainTabSelected(NavigationEnum.AdminAppMainTabs.REPORTS.get())) {
			NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.REPORTS.get());
			NavigationPage.toViewTab(NavigationEnum.ReportsTab.OPERATIONAL_REPORTS.get());
			loginToReports();
		}
		NavigationPage.toViewTab(NavigationEnum.ReportsTab.TEMPLATES.get());
	}

	@Override
	public void validate() {
		navigate();
		new Button(By.id("actions:validateBtn")).click();
	}

	private void loginToReports() {
		CSAAApplicationFactory.get().opReportApp().getLogin().login();
	}
}
