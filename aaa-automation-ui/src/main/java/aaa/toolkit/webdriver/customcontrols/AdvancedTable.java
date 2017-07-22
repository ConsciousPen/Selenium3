package aaa.toolkit.webdriver.customcontrols;

import java.util.ArrayList;
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
import aaa.common.components.Pagination;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Cell;
import toolkit.webdriver.controls.composite.table.NoRow;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Custom control for table with filters/sorting and pagination
 */
public class AdvancedTable extends Table {
	private static final By TABLE_LOCATOR = By.xpath(".//table");
	private static final By PAGINATION_LOCATOR = By.xpath(".//div[contains(@class, 'ui-paginator')]");
	private Pagination pagination;

	public AdvancedTable(By tableWithPaginationLocator) {
		this(null, new ByChained(tableWithPaginationLocator, TABLE_LOCATOR), new ByChained(tableWithPaginationLocator, PAGINATION_LOCATOR));
	}

	public AdvancedTable(By tableWithPaginationLocator, By paginationLocator) {
		this(null, tableWithPaginationLocator, paginationLocator);
	}

	public AdvancedTable(BaseElement<?, ?> parent, By tableWithPaginationLocator) {
		this(parent, new ByChained(tableWithPaginationLocator, TABLE_LOCATOR), new ByChained(tableWithPaginationLocator, PAGINATION_LOCATOR));
	}

	public AdvancedTable(BaseElement<?, ?> parent, By tableLocator, By paginationLocator) {
		super(parent, tableLocator);
		pagination = new Pagination(paginationLocator);
	}

	public TestData getFilterValue() {
		TestData td = DataProviderFactory.emptyData();
		for (Map.Entry<String, String> entry : getFiltersMap().entrySet()) {
			td.adjust(entry.getKey(), entry.getValue());
		}
		return td;
	}

	public Pagination getPagination() {
		return pagination;
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

		if (ts.getSelectedRowsPerPage() != null && pagination.getSelectedRowsPerPage() != ts.getSelectedRowsPerPage()) {
			pagination.setRowsPerPage(ts.getSelectedRowsPerPage());
		}
		if (ts.getSelectedPage() != null && pagination.getSelectedPage() != ts.getSelectedPage()) {
			pagination.goToPage(ts.getSelectedPage());
		}

		//TODO-dchubkov: set sort state
	}

	@Override
	protected List<TestData> getRawValue() {
		return getRawValue(false);
	}

	@Override
	public Row getRow(int rowIndex) {
		int rowOnPageIndex = goToPageWithRow(rowIndex);
		return super.getRow(rowOnPageIndex);
	}

	@Override
	public int getRowsCount() {
		return getRowsCount(false);
	}

	@Override
	public List<Row> getRows(Map<String, String> query) {
		if (isFilterAvailable(query)) {
			filterBy(query);
			return super.getRows(query);
		} else {
			return getRowsWithNavigation(() -> super.getRows(query));
		}
	}

	@Override
	public List<Row> getRows() {
		return getRowsWithNavigation(super::getRows);
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
	public Row getRow(Integer index, String cellValueInColumn) {
		return getRowWithNavigation(() -> super.getRow(index, cellValueInColumn));
	}

	@Override
	public Row getRowContains(Integer index, String cellValueInColumn) {
		return getRowWithNavigation(() -> super.getRowContains(index, cellValueInColumn));
	}

	@Override
	public Row getRowContains(Map<String, String> query) {
		return getRowWithFilter(() -> super.getRowContains(query), () -> filterBy(query), () -> isFilterAvailable(query));
	}

	@Override
	public Row getRow(Map<String, String> query) {
		return getRowWithFilter(() -> super.getRow(query), () -> filterBy(query), () -> isFilterAvailable(query));
	}

	public int getRowsCount(final boolean returnToInitialState) {
		TableState initialState = resetFiltersMaximizeTableAndGoToFirstPage(returnToInitialState);
		int rowsCount = super.getRowsCount();
		while (pagination.hasNextPage()) {
			pagination.goToNextPage();
			rowsCount += super.getRowsCount();
		}
		setState(initialState);
		return rowsCount;
	}

	public List<TestData> getValue(boolean returnToInitialState) {
		return getRawValue(returnToInitialState);
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

	private boolean isFilterAvailable(Map<String, String> query) {
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

	protected List<TestData> getRawValue(final boolean returnToInitialState) {
		List<TestData> data = new ArrayList<>();
		TableState initialState = resetFiltersMaximizeTableAndGoToFirstPage(returnToInitialState);
		data.addAll(super.getRawValue());
		while (pagination.hasNextPage()) {
			pagination.goToNextPage();
			data.addAll(super.getRawValue());
		}
		setState(initialState);
		return data;
	}

	private void filterBy(TextBox filterTextBox, String value) {
		CustomAssert.assertTrue(String.format("Can't find filter textbox by \"%s\" locator.", filterTextBox.getLocator()), filterTextBox.isPresent() && filterTextBox.isVisible());
		CustomAssert.assertTrue(String.format("Unable to set \"%1$s\" value to disabled filter textbox with \"%2$s\" locator.", value, filterTextBox.getLocator()), filterTextBox.isEnabled());
		filterTextBox.setValue(value);
	}

	/**
	 * Navigates to the page with needed row number and return new index of this row on selected page
	 *
	 * @param rowIndex row to be found on page
	 * @return index of needed row on selected page
	 */
	private int goToPageWithRow(int rowIndex) {
		if (pagination.hasPageOptionsSelector()) {
			pagination.setMaxRowsPerPage();
		}
		int rowsPerPage = getRowsPerPage();
		int navigationPageNumber = rowIndex / rowsPerPage + (rowIndex % rowsPerPage == 0 ? 0 : 1);
		if (pagination.getSelectedPage() != navigationPageNumber) {
			pagination.goToPage(navigationPageNumber);
		}
		return rowIndex - (navigationPageNumber - 1) * rowsPerPage;
	}

	private int getRowsPerPage() {
		if (pagination.hasPageOptionsSelector()) {
			return pagination.getSelectedRowsPerPage();
		} else if (pagination.getPagesCount() > 1) {
			pagination.goToFirstPage();
		}
		return super.getRowsCount();
	}

	private TableState resetFiltersMaximizeTableAndGoToFirstPage(boolean returnInitialState) {
		TableState state = returnInitialState ? getState() : null;
		resetAllFilters();
		if (pagination.hasPageOptionsSelector()) {
			pagination.setMaxRowsPerPage();
		}
		if (pagination.getSelectedPage() > 1) {
			pagination.goToFirstPage();
		}
		return state;
	}

	private Row getRowWithFilter(Supplier<Row> getRowSupplier, Runnable filterBy, BooleanSupplier isFilterAvailable) {
		if (isFilterAvailable.getAsBoolean()) {
			filterBy.run();
			return getRowSupplier.get();
		} else {
			return getRowWithNavigation(getRowSupplier);
		}
	}

	private Row getRowWithNavigation(Supplier<Row> getRowSupplier) {
		if (pagination.hasPageOptionsSelector()) {
			pagination.setMaxRowsPerPage();
		}
		Row row = getRowSupplier.get();
		while (row instanceof NoRow && pagination.hasNextPage()) {
			pagination.goToNextPage();
			row = getRowSupplier.get();
		}
		return row;
	}

	private List<Row> getRowsWithNavigation(Supplier<List<Row>> getRowSupplier) {
		if (pagination.hasPageOptionsSelector()) {
			pagination.setMaxRowsPerPage();
		}
		List<Row> rows = new ArrayList<>(getRowSupplier.get());
		while (pagination.hasNextPage()) {
			pagination.goToNextPage();
			rows.addAll(getRowSupplier.get());
		}
		return rows;
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

	//TODO-dchubkov: add new Verify methods

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
}
