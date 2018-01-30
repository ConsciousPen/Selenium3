package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.composite.table.Table;

public class ErrorTab extends CommonErrorTab {

	public Table tableErrors = new Table(By.xpath(".//form[@id='errorsForm']//table"));

	public ErrorTab() {
		super(HomeSSMetaData.ErrorTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonOverride.click();
		new BindTab().submitTab();
		return this;
	}

	@Override
	public FillableErrorTable getErrorsControl() {
		return getAssetList().getAsset(HomeSSMetaData.ErrorTab.ERROR_OVERRIDE);
	}

}
