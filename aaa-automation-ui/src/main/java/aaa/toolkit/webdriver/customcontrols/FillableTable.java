package aaa.toolkit.webdriver.customcontrols;

import java.util.*;

import org.openqa.selenium.By;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.ListBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.collection.Controls;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Control for filling tables (e.g. on Report Tabs) with different types of inner controls.
 * Pass list of TestData input where each TestData represents row in table to be used for searching and interaction with inner controls.
 * If key from test data is associated with any element from searchable control types list (StaticElement by default) in appropriate MetaData class then this <b>key</b> will be used as a <b>column name</b>
 * and <b>value</b> as a <b>cell</b> value for searching appropriate row in table. This searchable list can be set by overriding <b>getSearchableControlsList()</b> method.
 * If there are several keys with associated StaticElement controls then all of them will be used as a query for row search.
 * All other control types will be used for filling appropriate data or click.
 * If there are no keys with associated StaticElement controls then rows will be filled one by one according to the order in provided TestData list.
 *<br>
 * TestData example using search:
 * <pre>
 *      ReportsTab: {
 *          AAAMembershipReport: ['@AAAMembershipReportRow1', '@AAAMembershipReportRow2', '@AAAMembershipReportRow3'],
 *      }
 *
 *      AAAMembershipReportRow1: {
 *          Last Name: 'Test',
 *          Status: 'Not started',
 *          Report: 'Order report'
 *      }
 *</pre>
 * In this example <b>Last Name</b> and <b>Status</b> are associated with StaticElements in MetaData class therefore in report table row
 * will be searched by <b>Column name</b>=<b>Value in cell</b> query: "Last Name"="Test" <b>AND</b> "Status"="Not started". If row exists then remaining controls will be used for interaction,
 * in this particular case if "Report" has "Link" control type class then: <b>Order report</b> link will be clicked under <b>Report</b> column on searched row.
 *<br>
 * TestData example without StaticElement(s):
 * <pre>
 *      ReportsTab: {
 *          AAAMembershipReport: [{Report: 'Order report}, {Report: 'Order report}, {Report: 'Order report}],
 *      }
 *</pre>
 * In this example "Order report" will be clicked for each row from 1st up to 3rd.
 * Also test data value can be in "contains=someValue" format which means search row which contains "someValue" in appropriate cell.
 *<br>
 * If you additionally need to interact with any control outside the FillableTable then declare it in appropriate MetaData class with locator and hasParent=false argument.
 *<br>
 * E.g.: <pre>"public static final AssetDescriptor<RadioGroup> CUSTOMER_AGREEMENT = declare("Customer Agreement", RadioGroup.class, Waiters.AJAX, <b>false</b>, <b>By.xpath("//table[@id='policyDataGatherForm:customerRadio']")</b>);"</pre>
 *<br>
 * In case when column has no header name with column number such as "column=1" should be used for control in metadata and testdata.
 */
public class FillableTable extends AbstractContainer<List<TestData>, List<TestData>> {
	protected Table fillableTable = new Table(getLocator());

	public FillableTable(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	public FillableTable(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public Table getTable() {
		return this.fillableTable;
	}

	@Override
	protected List<TestData> getRawValue() {
		return fillableTable.getValue();
	}

	@Override
	protected void setRawValue(List<TestData> testDataList) {
		int rowToFillIndex = 1;

		for (TestData rowData : testDataList) {
			if (rowData.equals(DataProviderFactory.emptyData())) {
				rowToFillIndex++;
				continue;
			}

			boolean searchRowByContains = false;
			Map<String, String> searchRowQuery = new HashMap<>(rowData.getKeys().size());

			// Find all searchable controls to build search row query.
			for (String columnName : rowData.getKeys()) {
				BaseElement<?, ?> control = getAssetCollection().get(columnName);
				if (control != null && control.getParent() != null && isSearchableControl(control)) {
					// If TestData value has "contains=SomeValue" format then search row which contains "SomeValue"
					String[] value = rowData.getValue(columnName).split("=");
					if (value.length == 2 && "contains".equalsIgnoreCase(value[0])) {
						searchRowQuery.put(columnName, value[1]);
						searchRowByContains = true;
					} else {
						searchRowQuery.put(columnName, value[0]);
					}
					rowData.mask(columnName);
				}
			}

			Row fillableRow;
			String findRowByAssertionMessage;

			if (searchRowQuery.isEmpty()) {
				fillableRow = fillableTable.getRow(rowToFillIndex);
				findRowByAssertionMessage = "by row number: " + rowToFillIndex;
			} else {
				if (searchRowByContains) {
					fillableRow = fillableTable.getRowContains(searchRowQuery);
					findRowByAssertionMessage = "by contains query: " + searchRowQuery.entrySet();
				} else {
					fillableRow = fillableTable.getRow(searchRowQuery);
					findRowByAssertionMessage = "by query: " + searchRowQuery.entrySet();
				}
			}
			fillableRow.verify.present(String.format("Can't find row in \"%1$s\" fillable table %2$s.", getName(), findRowByAssertionMessage));
			fillRow(fillableRow, rowData);

			rowToFillIndex++;
		}
	}

	/**
	 * Interact with controls within found row
	 *
	 * @param row row with controls to be interacted
	 * @param rowData test data to be used while filling/clicking controls inside the row
	 */
	protected void fillRow(Row row, TestData rowData) {
		for (String assetName : rowData.getKeys()) {
			BaseElement<?, ?> control = getAssetCollection().get(assetName);
			if (control == null) {
				return;
			}

			if (control.getParent() == null) {
				control.fill(rowData);
			} else {
				String value = rowData.getValue(assetName);
				Controls innerControls;
				if (assetName.startsWith("column=")) {
					innerControls = row.getCell(Integer.valueOf(assetName.replace("column=", ""))).controls;
				} else {
					innerControls = row.getCell(assetName).controls;
				}
				if (control instanceof RadioGroup) {
					innerControls.radioGroups.getFirst().setValue(value);
				} else if (control instanceof TextBox) {
					innerControls.textBoxes.getFirst().setValue(value);
				} else if (control instanceof CheckBox) {
					innerControls.checkBoxes.getFirst().setValue(Boolean.valueOf(value));
				} else if (control instanceof ComboBox) {
					innerControls.comboBoxes.getFirst().setValue(value);
				} else if (control instanceof ListBox) {
					innerControls.listBoxes.getFirst().setValue(value);
				} else if (control instanceof Button) {
					innerControls.buttons.get(value).click();
				} else if (control instanceof Link) {
					innerControls.links.get(value).click();
				}
			}
		}
	}

	@Override
	public void fill(TestData td) {
		if (td.containsKey(name) && !td.getTestDataList(name).isEmpty()) {
			setValue(td.getTestDataList(name));
		}
	}

	@Override
	public TestData.Type testDataType() {
		return TestData.Type.TESTDATA;
	}

	@Override
	protected List<TestData> normalize(Object rawValue) {
		if (rawValue instanceof TestData) {
			return ((TestData) rawValue).getTestDataList(name);
		}
		throw new IllegalArgumentException("Value " + rawValue + " has incorrect type " + rawValue.getClass());
	}

	/**
 	 * @return list of control types to be used as Rows search query in Table
	 */
	protected List<Class<? extends BaseElement>> getSearchableControlsList() {
		return Collections.singletonList(StaticElement.class);
	}

	private boolean isSearchableControl(BaseElement<?, ?> control) {
		for (Class<? extends BaseElement> searchableControlType : getSearchableControlsList()) {
			if (searchableControlType.isAssignableFrom(control.getClass())) {
				return true;
			}
		}
		return false;
	}
}
