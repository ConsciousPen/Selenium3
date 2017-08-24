package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Control for filling GenerateOnDemandDocumentActionTabs table which extends FillableTable.
 */
public class FillableDocumentsTable extends FillableTable {
	public FillableDocumentsTable(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	public FillableDocumentsTable(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	@Override
	public Table getTable() {
		return super.getTable().applyConfiguration("FillableDocumentsTable");
	}
}
