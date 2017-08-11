package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Row;

import java.util.Arrays;
import java.util.List;

/**
 * Control for filling ErrorTabs table which extends FillableTable.
 *
 * This FillableErrorTable also uses <b>Link</b> as searchable control type class in addition to <b>StaticElement</b> to allow search row in table with Error code which has a link type.
 * To be able to click on this Code link you should define CLICK_CODE_TD_KEY key in test data:
 * <br>
 * TestData example using search:
 * <pre>
 *      ErrorsOverride: [{
 *      	Code: '200103',
 *      	Override: 'true',
 *      	Duration: Life,
 *      	ClickCode: 'true'
 *      }]
 * </pre>
 */
public class FillableErrorTable extends FillableTable {
	public static final String CLICK_CODE_TD_KEY = "ClickCode";

	public FillableErrorTable(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	public FillableErrorTable(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	@Override
	protected void fillRow(Row row, TestData rowData) {
		super.fillRow(row, rowData);
		if (rowData.containsKey(CLICK_CODE_TD_KEY)) {
			row.getCell("Code").controls.links.getFirst().click();
		}
	}


	@Override
	protected List<Class<? extends BaseElement>> getSearchableControlsList() {
		return Arrays.asList(StaticElement.class, Link.class);
	}
}
