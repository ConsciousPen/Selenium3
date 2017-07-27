package aaa.toolkit.webdriver.customcontrols;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import aaa.common.components.Pagination;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.table.NoRow;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Custom control for table with pagination
 */
public class TableWithPages extends Table {
	protected static final By TABLE_LOCATOR = By.xpath(".//table");
	protected static final By PAGINATION_LOCATOR = By.xpath(".//div[contains(@class, 'ui-paginator')]");
	protected Pagination pagination;

	public TableWithPages(By tableWithPaginationLocator) {
		this(null, new ByChained(tableWithPaginationLocator, TABLE_LOCATOR), new ByChained(tableWithPaginationLocator, PAGINATION_LOCATOR));
	}

	public TableWithPages(By tableWithPaginationLocator, By paginationLocator) {
		this(null, tableWithPaginationLocator, paginationLocator);
	}

	public TableWithPages(BaseElement<?, ?> parent, By tableWithPaginationLocator) {
		this(parent, new ByChained(tableWithPaginationLocator, TABLE_LOCATOR), new ByChained(tableWithPaginationLocator, PAGINATION_LOCATOR));
	}

	public TableWithPages(BaseElement<?, ?> parent, By tableLocator, By paginationLocator) {
		super(parent, tableLocator);
		pagination = new Pagination(paginationLocator);
	}

	public Pagination getPagination() {
		return pagination;
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
		return getRowsWithNavigation(() -> super.getRows(query));
	}

	@Override
	public List<Row> getRows() {
		return getRowsWithNavigation(super::getRows);
	}

	@Override
	public Row getRow(String columnName, String cellValueInColumn) {
		return getRowWithNavigation(() -> super.getRow(columnName, cellValueInColumn));
	}

	@Override
	public Row getRowContains(String columnName, String cellValueInColumn) {
		return getRowWithNavigation(() -> super.getRowContains(columnName, cellValueInColumn));
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
		return getRowWithNavigation(() -> super.getRowContains(query));
	}

	@Override
	public Row getRow(Map<String, String> query) {
		return getRowWithNavigation(() -> super.getRow(query));
	}

	public int getRowsCount(final boolean returnToInitialState) {
		getPagination().goToFirstPage();
		int rowsCount = super.getRowsCount();
		while (pagination.hasNextPage()) {
			pagination.goToNextPage();
			rowsCount += super.getRowsCount();
		}
		return rowsCount;
	}

	public List<TestData> getValue(boolean returnToInitialState) {
		return getRawValue(returnToInitialState);
	}

	protected List<TestData> getRawValue(final boolean returnToInitialState) {
		List<TestData> data = new ArrayList<>();
		getPagination().goToFirstPage();
		data.addAll(super.getRawValue());
		while (pagination.hasNextPage()) {
			pagination.goToNextPage();
			data.addAll(super.getRawValue());
		}
		return data;
	}

	/**
	 * Navigates to the page with needed row number and return new index of this row on selected page
	 *
	 * @param rowIndex row to be found on page
	 * @return index of needed row on selected page
	 */
	protected int goToPageWithRow(int rowIndex) {
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

	protected int getRowsPerPage() {
		if (pagination.hasPageOptionsSelector()) {
			return pagination.getSelectedRowsPerPage();
		} else if (pagination.getPagesCount() > 1) {
			pagination.goToFirstPage();
		}
		return super.getRowsCount();
	}

	protected Row getRowWithNavigation(Supplier<Row> getRowSupplier) {
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

	protected List<Row> getRowsWithNavigation(Supplier<List<Row>> getRowSupplier) {
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
}
