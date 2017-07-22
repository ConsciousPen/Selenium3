package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class ErrorTab extends Tab{
	
	public Table tblErrorsList = new Table(By.id("errorsForm:msgList"));
	public Button btnOverride = new Button(By.id("errorsForm:overrideRules"));
	public Button btnCancel = new Button(By.id("errorsForm:back"));
	
	public ErrorTab() {
		super(HomeSSMetaData.ErrorTab.class);
	}

	@Override
	public Tab submitTab() {
		btnOverride.click();
		new BindTab().submitTab();
		return this;
	}
	
	public Tab cancel(){
		btnCancel.click();
		return this;
	}
}
