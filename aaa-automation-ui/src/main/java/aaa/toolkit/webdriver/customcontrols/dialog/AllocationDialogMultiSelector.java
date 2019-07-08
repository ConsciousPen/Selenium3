package aaa.toolkit.webdriver.customcontrols.dialog;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialogMultiSearch;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialogSearch;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Row;

public class AllocationDialogMultiSelector extends AbstractDialogMultiSearch {
	public static final By POPUP_PARENT_LOCATOR = By.xpath("//div[contains(@class, 'rf-pp-cntr')][parent::body] | //div[@id='policySearchPopup' and not(@style='display: block;') and not(@aria-hidden='true')]");
	private static final By RESULT_TABLE_LOCATOR = By.xpath("//div[contains(@id,'policySearch:policySearchResults')]//table");

	public AllocationDialogMultiSelector(By locator) {
		super(locator);
		this.POPUP_PARENT = new StaticElement(POPUP_PARENT_LOCATOR);
		this.POPUP_PARENT = new StaticElement(locator);
		this.tableSearchResults = new AbstractDialogSearch.ResultTable(this.POPUP_PARENT, RESULT_TABLE_LOCATOR);
	}

	public AllocationDialogMultiSelector(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
		this.tableSearchResults = new AbstractDialogSearch.ResultTable(this.POPUP_PARENT, RESULT_TABLE_LOCATOR);
	}

	public AllocationDialogMultiSelector(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		this.tableSearchResults = new AbstractDialogSearch.ResultTable(this.POPUP_PARENT, RESULT_TABLE_LOCATOR);
	}

	protected void search() {
		(new Button(this.POPUP_PARENT, By.xpath(".//button[@id='policySearch:searchBtn']"))).click();
	}

	protected void select() {
		for (Row row : this.tableSearchResults.getRows()) {
			((CheckBox) row.getCell(1).controls.checkBoxes.getFirst()).setValue(true);
		}
		(new Button(this.POPUP_PARENT, By.xpath(".//button[@id='policySearch:selectBtn']"))).click();
	}
}
