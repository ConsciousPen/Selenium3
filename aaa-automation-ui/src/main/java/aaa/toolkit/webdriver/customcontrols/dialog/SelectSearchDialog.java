package aaa.toolkit.webdriver.customcontrols.dialog;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialogSingleSearch;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.AbstractClickableStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

public class SelectSearchDialog extends AbstractDialogSingleSearch {

	private static final By DEFAULT_ERROR_MESSAGE_LOCATOR = By.xpath(".//span[contains(@id, ':noResults')]");
	private static final By DEFAULT_RESULT_TABLE_LOCATOR = By.xpath(".//table[contains(@id, 'SearchTable') or (contains(@id, 'SearchFrom') and not(contains(@id, 'birthDate')))]");
	private static final String DEFAULT_POPUP_OPENER_NAME = "Open";
	private static final String DEFAULT_POPUP_SUBMITTER_NAME = "Submit";
	private static final String DEFAULT_POPUP_CLOSER_NAME = "Close";
	private static final String DEFAULT_POPUP_SEARCH_NAME = "Search";
	private static final String DEFAULT_POPUP_CLEAR_NAME = "Clear";
	//Result Table should be specified in metadata as StaticElement.
	private static final String RESULT_TABLE_NAME = "Result Table";
	private static final String ERROR_POPUP_NAME = "Error Message";
	protected StaticElement errorMessage = new StaticElement(POPUP_PARENT, DEFAULT_ERROR_MESSAGE_LOCATOR);
	protected Table resultTable = new ResultTable(POPUP_PARENT, DEFAULT_RESULT_TABLE_LOCATOR);

	public SelectSearchDialog(By locator) {
		super(locator);
		errorMessage = getAsset(ERROR_POPUP_NAME, StaticElement.class);
		resultTable = getAsset(RESULT_TABLE_NAME, Table.class);
	}

	public SelectSearchDialog(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
		errorMessage = getAsset(ERROR_POPUP_NAME, StaticElement.class);
		resultTable = getAsset(RESULT_TABLE_NAME, Table.class);
	}

	public SelectSearchDialog(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		errorMessage = getAsset(ERROR_POPUP_NAME, StaticElement.class);
		resultTable = getAsset(RESULT_TABLE_NAME, Table.class);
	}

	public StaticElement getErrorMessage() {
		return errorMessage;
	}

	public Table getResultTable() {
		return resultTable;
	}

	@Override
	public void search() {
		AbstractClickableStringElement searchBtn = getAsset(DEFAULT_POPUP_SEARCH_NAME, AbstractClickableStringElement.class);
		if (searchBtn != null) {
			searchBtn.click();
		}
	}

	@Override
	public void select() {
		//need to see table example
	}

	@Override
	public void clear() {
		AbstractClickableStringElement clearBtn = getAsset(DEFAULT_POPUP_CLEAR_NAME, AbstractClickableStringElement.class);
		if (clearBtn != null && clearBtn.isPresent() && clearBtn.isVisible()) {
			clearBtn.click();
		}
	}

	@Override
	public void setRawValue(TestData data) {
		if (data != null && !data.getKeys().isEmpty()) {
			fill(data);
			search();
			select();
		} else {
			cancel();
		}
	}

	@Override
	public void openDialog() {
		AbstractClickableStringElement buttonOpenPopup = getAsset(DEFAULT_POPUP_OPENER_NAME, AbstractClickableStringElement.class);
		if (buttonOpenPopup != null) {
			buttonOpenPopup.click();
		}
	}

	@Override
	public void submit() {
		AbstractClickableStringElement buttonClosePopup = getAsset(DEFAULT_POPUP_SUBMITTER_NAME, AbstractClickableStringElement.class);
		if (buttonClosePopup != null) {
			buttonClosePopup.click();
		}
	}

	public void cancel() {
		AbstractClickableStringElement closeBtn = getAsset(DEFAULT_POPUP_CLOSER_NAME, AbstractClickableStringElement.class);
		if (closeBtn != null) {
			closeBtn.click();
		}
	}
}
