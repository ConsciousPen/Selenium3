package aaa.toolkit.webdriver.customcontrols.dialog;

import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialogMultiSearch;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class DialogMultiSelectorParentDiv extends AbstractDialogMultiSearch {
	public static final By POPUP_PARENT_LOC = By.xpath("//div[contains(@class, 'rf-pp-cntr')][parent::div]");

	public DialogMultiSelectorParentDiv(By locator) {
		super(locator);
		this.POPUP_PARENT = new StaticElement(POPUP_PARENT_LOC);
		this.POPUP_PARENT = new StaticElement(locator);
	}

	public DialogMultiSelectorParentDiv(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public DialogMultiSelectorParentDiv(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	protected void search() {
		(new Button(By.xpath("//button[@id='policySearch:searchBtn']"))).click();
	}

	protected void select() {
		((CheckBox)this.tableSearchResults.getRow(1).getCell(1).controls.checkBoxes.getFirst()).setValue(true);
		(new Button(By.xpath("//button[@id='policySearch:selectBtn']"))).click();
	}

	public void fill(TestData td) {
		if (td.containsKey(this.name)) {
			this.openDialog();
			this.setValue(this.getValueToFill(td));
			this.submit();
		}

	}
}
