package aaa.toolkit.webdriver.customcontrols;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import aaa.common.pages.Page;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Cell;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Custom control for table with filters/sorting/selecting/removing and pagination
 */
public class AdvancedTable extends TableWithPages {
	public AdvancedTable(By tableWithPaginationLocator) {
		super(tableWithPaginationLocator);
	}

	public AdvancedTable(By tableLocator, By paginationLocator) {
		super(tableLocator, paginationLocator);
	}

	public AdvancedTable(BaseElement<?, ?> parent, By tableWithPaginationLocator) {
		super(parent, tableWithPaginationLocator);
	}

	public AdvancedTable(BaseElement<?, ?> parent, By tableLocator, By paginationLocator) {
		super(parent, tableLocator, paginationLocator);
	}

	public TestData getFilterValue() {
		TestData td = DataProviderFactory.emptyData();
		for (Map.Entry<String, String> entry : getFiltersMap().entrySet()) {
			td.adjust(entry.getKey(), entry.getValue());
		}
		return td;
	}

	public TableState getState() {
		return new TableState();
	}

	public void setState(TableState ts) {
		if (Objects.isNull(ts)) {
			return;
		}

		if (ts.getFilters().isEmpty()) {
			resetAllFilters();
		} else {
			filterBy(ts.getFilters());
		}

		if (ts.getSelectedRowsPerPage() != null) {
			pagination.setRowsPerPage(ts.getSelectedRowsPerPage());
		}
		if (ts.getSelectedPage() != null) {
			pagination.goToPage(ts.getSelectedPage());
		}

		//TODO-dchubkov: set sort state
	}

	@Override
	protected List<TestData> getRawValue() {
		resetAllFilters();
		return super.getRawValue();
	}

	@Override
	public int getRowsCount() {
		resetAllFilters();
		return super.getRowsCount();
	}

	@Override
	public List<Row> getRows(Map<String, String> query) {
		if (isFilterAvailable(query)) {
			filterBy(query);
		}
		return super.getRows(query);
	}

	@Override
	public Row getRow(String columnName, String cellValueInColumn) {
		return getRowWithFilter(
				() -> super.getRow(columnName, cellValueInColumn),
				() -> filterBy(columnName, cellValueInColumn),
				() -> isFilterAvailable(columnName) && !getFilterValue(columnName).equals(cellValueInColumn));
	}

	@Override
	public Row getRowContains(String columnName, String cellValueInColumn) {
		return getRowWithFilter(
				() -> super.getRowContains(columnName, cellValueInColumn),
				() -> filterBy(columnName, cellValueInColumn),
				() -> isFilterAvailable(columnName) && !getFilterValue(columnName).equals(cellValueInColumn));
	}

	@Override
	public Row getRowContains(Map<String, String> query) {
		return getRowWithFilter(() -> super.getRowContains(query), () -> filterBy(query), () -> isFilterAvailable(query));
	}

	@Override
	public Row getRow(Map<String, String> query) {
		return getRowWithFilter(() -> super.getRow(query), () -> filterBy(query), () -> isFilterAvailable(query));
	}

	public void filterBy(String columnName, String cellValueInColumn) {
		TextBox tb = getFilterTextBoxByHeadersCell(getHeader().getCell(columnName));
		filterBy(tb, cellValueInColumn);
	}

	public void filterBy(Integer index, String cellValueInColumn) {
		TextBox tb = getFilterTextBoxByHeadersCell(getHeader().getCell(index));
		filterBy(tb, cellValueInColumn);
	}

	public void filterBy(Map<String, String> query) {
		for (Map.Entry<String, String> entry : query.entrySet()) {
			TextBox tb = getFilterTextBoxByHeadersCell(getHeader().getCell(entry.getKey()));
			filterBy(tb, entry.getValue());
		}
	}

	public void resetAllFilters() {
		for (int index = 1; index <= getHeader().getCellsCount(); index++) {
			if (isFilterAvailable(index) && !getFilterValue(index).isEmpty()) {
				getFilterTextBoxByHeadersCell(getHeader().getCell(index)).setValue("");
			}
		}
	}

	public boolean isFilterAvailable(String columnNmae) {
		TextBox tb = getFilterTextBoxByHeadersCell(getHeader().getCell(columnNmae));
		return isFilterAvailable(tb);
	}

	public boolean isFilterAvailable(int index) {
		TextBox tb = getFilterTextBoxByHeadersCell(getHeader().getCell(index));
		return isFilterAvailable(tb);
	}

	public boolean isFilterAvailable(Map<String, String> query) {
		return query.entrySet().stream().allMatch(entry -> isFilterAvailable(entry.getKey()));
	}

	public String getFilterValue(String columnName) {
		TextBox tb = getFilterTextBoxByHeadersCell(getHeader().getCell(columnName));
		return tb.getValue();
	}

	public String getFilterValue(Integer index) {
		TextBox tb = getFilterTextBoxByHeadersCell(getHeader().getCell(index));
		return tb.getValue();
	}

	public void sortBy(String columnName) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("sortBy(String columnName) is not implemented yet.");
	}

	public void sortBy(Integer index) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("sortBy(Integer index) is not implemented yet.");
	}

	public void removeRow(int index) {
		getRow(index).getCell(getColumnsCount()).controls.links.get("Remove").click();
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
	}

	public boolean isRowSelected(int index) {
		return new StaticElement(new ByChained(getLocator(), getRow(index).getLocator(), By.xpath(".//td[position()=1 or position()=2]/span[@class='textBold']"))).isPresent();
	}

	public void selectRow(int index) {
		if (!isRowSelected(index)) {
			getRow(index).getCell(getColumnsCount()).controls.links.get("View/Edit").click();
		}
	}
	//TODO-dchubkov: implement isRowSelected(...),  removeRow(...) and selectRow(...) by other arguments like in getRaw(...) methods

	public boolean isEmpty() {
		return "No records found.".equals(getRow(1).getCell(1).getValue());
	}

	private void filterBy(TextBox filterTextBox, String value) {
		CustomAssert.assertTrue(String.format("Can't find filter textbox by \"%s\" locator.", filterTextBox.getLocator()), filterTextBox.isPresent() && filterTextBox.isVisible());
		CustomAssert.assertTrue(String.format("Unable to set \"%1$s\" value to disabled filter textbox with \"%2$s\" locator.", value, filterTextBox.getLocator()), filterTextBox.isEnabled());
		filterTextBox.setValue(value);
	}


	private Row getRowWithFilter(Supplier<Row> getRowSupplier, Runnable filterBy, BooleanSupplier isFilterAvailable) {
		if (isFilterAvailable.getAsBoolean()) {
			filterBy.run();
			return getRowSupplier.get();
		}
		return getRowWithNavigation(getRowSupplier);
	}

	private Map<String, String> getFiltersMap() {
		Map<String, String> filters = new LinkedHashMap<>();
		List<String> columnNames = getHeader().getValue();
		columnNames.removeAll(Arrays.asList("", null));

		for (String columnName : columnNames) {
			if (isFilterAvailable(columnName)) {
				String value = getFilterValue(columnName);
				if (!value.isEmpty()) {
					filters.put(columnName, value);
				}
			}
		}
		return filters;
	}

	private boolean isFilterAvailable(TextBox tb) {
		return tb.isPresent() && tb.isVisible() && tb.isEnabled();
	}

	private TextBox getFilterTextBoxByHeadersCell(Cell cell) {
		return new TextBox(new ByChained(getLocator(), cell.getLocator(), By.xpath(".//input")), Waiters.AJAX);
	}

	public class TableState {
		private Integer selectedRowsPerPage;
		private Integer selectedPage;
		private Map<String, String> filters;
		//TODO-dchubkov: add sort state

		public TableState() {
			this(null, null, null);
		}

		public TableState(Integer selectedPage) {
			this(null, selectedPage, null);
		}

		public TableState(Integer selectedRowsPerPage, Integer selectedPage) {
			this(selectedRowsPerPage, selectedPage, null);
		}

		public TableState(Integer selectedRowsPerPage, Integer selectedPage, Map<String, String> filters) {
			if (selectedRowsPerPage == null && pagination.hasPageOptionsSelector()) {
				this.selectedRowsPerPage = pagination.getSelectedRowsPerPage();
			} else {
				this.selectedRowsPerPage = selectedRowsPerPage;
			}
			this.selectedPage = selectedPage == null ? pagination.getSelectedPage() : selectedPage;
			this.filters = filters == null ? getFiltersMap() : filters;
		}

		public Integer getSelectedRowsPerPage() {
			return selectedRowsPerPage;
		}

		public void setSelectedRowsPerPage(int selectedRowsPerPage) {
			this.selectedRowsPerPage = selectedRowsPerPage;
		}

		public Integer getSelectedPage() {
			return selectedPage;
		}

		public void setSelectedPage(int selectedPage) {
			this.selectedPage = selectedPage;
		}

		public Map<String, String> getFilters() {
			return filters;
		}

		public void setFilters(Map<String, String> filters) {
			this.filters = filters;
		}
	}


	public final AdvancedTable.Verify verify = this.new Verify();
	/**
	 * Extended tables verifier class for AdvancedTable
	 */
	public class Verify extends Table.Verify {
		public void empty() {
			empty(true);
		}

		public void empty(boolean expectedValue) {
			String assertMessage = String.format("Table with locator [%1$s] is%2$s empty.", getLocator(), expectedValue? " not" : "");
			if (expectedValue) {
				CustomAssert.assertTrue(assertMessage, isEmpty());
			} else {
				CustomAssert.assertFalse(assertMessage, isEmpty());
			}
		}
	}

}
