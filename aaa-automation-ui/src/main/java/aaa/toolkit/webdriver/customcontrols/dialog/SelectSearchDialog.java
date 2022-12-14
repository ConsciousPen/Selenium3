package aaa.toolkit.webdriver.customcontrols.dialog;

import java.util.Map;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialogSingleSearch;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.AbstractClickableStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class SelectSearchDialog extends AbstractDialogSingleSearch {

	private static final By DEFAULT_ERROR_MESSAGE_LOCATOR = By.xpath(".//span[contains(@id, ':noResults')]");
	private static final String DEFAULT_POPUP_OPENER_NAME = "Open";
	private static final String DEFAULT_POPUP_SUBMITTER_NAME = "Submit";
	private static final String DEFAULT_POPUP_CLOSER_NAME = "Close";
	private static final String DEFAULT_POPUP_SEARCH_NAME = "Search";
	private static final String DEFAULT_POPUP_CLEAR_NAME = "Clear";
	private static final String DEFAULT_POPUP_CANCEL_NAME = "Cancel";
	//Result Table should be specified in metadata as StaticElement.
	private static final String RESULT_TABLE_NAME = "Result Table";
	private static final String ERROR_POPUP_NAME = "Error Message";
	protected StaticElement errorMessage = new StaticElement(POPUP_PARENT, DEFAULT_ERROR_MESSAGE_LOCATOR);
	protected FillableTable resultTable;

	public SelectSearchDialog(By locator) {
		super(locator);
	}

	public SelectSearchDialog(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
		initiateControls();
	}

	public SelectSearchDialog(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		initiateControls();
	}

	public StaticElement getErrorMessage() {
		return errorMessage;
	}

	public FillableTable getResultTable() {
		return resultTable;
	}

	@Override
	public void search() {
		if (getAssetCollection().containsKey(DEFAULT_POPUP_SEARCH_NAME)) {
			AbstractClickableStringElement searchBtn = getAsset(DEFAULT_POPUP_SEARCH_NAME, AbstractClickableStringElement.class);
			if (searchBtn != null) {
				searchBtn.click();
			}
		}
	}

	@Override
	public void select() {
		//Fillable Table can be filled from test data.
	}

	@Override
	public void clear() {
		if (getAssetCollection().containsKey(DEFAULT_POPUP_CLEAR_NAME)) {
			AbstractClickableStringElement clearBtn = getAsset(DEFAULT_POPUP_CLEAR_NAME, AbstractClickableStringElement.class);
			if (clearBtn != null && clearBtn.isPresent() && clearBtn.isVisible()) {
				clearBtn.click();
			}
		}
	}

	@Override
	public void setRawValue(TestData data) {
		if (data != null && !data.getKeys().isEmpty()) {
			for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {
				entry.getValue().fill(data);
			}
			search();
			if(!data.containsKey(RESULT_TABLE_NAME)) {
				select();
			}
			if(!data.containsKey(DEFAULT_POPUP_SUBMITTER_NAME)) {
				submit();
			}
		} else {
			cancel();
		}
	}

	@Override
	public void openDialog() {
		if (getAssetCollection().containsKey(DEFAULT_POPUP_OPENER_NAME)) {
			AbstractClickableStringElement buttonOpenPopup = getAsset(DEFAULT_POPUP_OPENER_NAME, AbstractClickableStringElement.class);
			if (buttonOpenPopup != null) {
				buttonOpenPopup.click();
			}
		}
	}

	@Override
	public void submit() {
		if (getAssetCollection().containsKey(DEFAULT_POPUP_SUBMITTER_NAME)) {
			AbstractClickableStringElement buttonSubmit = getAsset(DEFAULT_POPUP_SUBMITTER_NAME, AbstractClickableStringElement.class);
			if (buttonSubmit != null) {
				buttonSubmit.click();
			}
		}
	}

	public void close() {
		if (getAssetCollection().containsKey(DEFAULT_POPUP_CLOSER_NAME)) {
			AbstractClickableStringElement closeBtn = getAsset(DEFAULT_POPUP_CLOSER_NAME, AbstractClickableStringElement.class);
			if (closeBtn != null) {
				closeBtn.click();
			}
		}
	}

	public void cancel() {
		if (getAssetCollection().containsKey(DEFAULT_POPUP_CANCEL_NAME)) {
			AbstractClickableStringElement closeBtn = getAsset(DEFAULT_POPUP_CANCEL_NAME, AbstractClickableStringElement.class);
			if (closeBtn != null) {
				closeBtn.click();
			}
		}
	}

	private void initiateControls() {
		if (getAssetCollection().containsKey(ERROR_POPUP_NAME)) {
			errorMessage = getAsset(ERROR_POPUP_NAME, StaticElement.class);
		}

		if (getAssetCollection().containsKey(RESULT_TABLE_NAME)) {
			resultTable = getAsset(RESULT_TABLE_NAME, FillableTable.class);
		}
	}
}
