package aaa.toolkit.webdriver.customcontrols.dialog;

import java.util.Map;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialogSingleSearch;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.AbstractClickableStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.waiters.Waiters;

public class SingleSelectSearchDialog extends AbstractDialogSingleSearch {

	private static final By RESULT_TABLE_LOCATOR = By.xpath(".//table[contains(@id,'SearchTabel') or contains(@id, 'SearchTable') or contains(@id, 'PriorActivePolicySearch') or (contains(@id, 'SearchFrom') and not(contains(@id, 'birthDate')))]");
	private static final By ERROR_MESSAGE_LOCATOR = By.xpath(".//form[@id='customerSearchFrom']/span[2]");
	public ResultTable tableSearchResults;
	public Button buttonSearch;
	public Button buttonCancel;
	public Button buttonClear;
	public StaticElement labelErrorMessage;
	private static final String CANCEL = "Cancel";

	public SingleSelectSearchDialog(By locator) {
		super(locator);
		initializeControls(locator);
	}

	public SingleSelectSearchDialog(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
		initializeControls(locator);
	}

	public SingleSelectSearchDialog(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		initializeControls(locator);
	}

	// ResultTable tableSearchResults = new ResultTable(POPUP_PARENT,
	// RESULT_TABLE_LOCATOR);

	@Override
	public void search() {
		if (buttonSearch.isPresent() && buttonSearch.isVisible()) {
			buttonSearch.click();
		}
	}

	@Override
	public void select() {
		AbstractClickableStringElement selectControl = getSelectControl();
		if (selectControl != null) {
			selectControl.click();
		}
	}

	@Override
	public void clear() {
		if (buttonClear.isPresent() && buttonClear.isVisible()) {
			buttonClear.click();
		}
	}

	@Override
	public void setRawValue(TestData data) {
		if (data != null && !data.getKeys().isEmpty()) {
			for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {
				entry.getValue().fill(data);
			}
			search();
			select();
		} else {
			if (getAssetCollection().containsKey(CANCEL)) {
				getAsset(CANCEL, AbstractClickableStringElement.class).click();
			} else {
				cancel();
			}
		}
	}

	public void cancel() {

		if (buttonCancel.isPresent()) {
			buttonCancel.click();
		}
	}

	private AbstractClickableStringElement getSelectControl() {
		Row row = tableSearchResults.getRow(1);
		if (row.getCell(1).controls.links.getFirst().isPresent()) {
			return row.getCell(1).controls.links.getFirst();
		}
		if (row.getCell(1).controls.buttons.getFirst().isPresent()) {
			return row.getCell(1).controls.buttons.getFirst();
		}
		if (row.getCell(1).controls.checkBoxes.getFirst().isPresent()) {
			row.getCell(1).controls.checkBoxes.getFirst().setValue(true);
			return null;
		}
		if (row.getCell(5).controls.links.getFirst().isPresent()) {
			return row.getCell(5).controls.links.getFirst();
		}
		if (row.getCell(1).controls.comboBoxes.getFirst().isPresent()) {
			row.getCell(1).controls.listBoxes.getFirst().setValue("index=0");
			return null;
		}
		return null;
	}

	private void initializeControls(By locator){
		this.POPUP_PARENT = new StaticElement(locator);
		tableSearchResults = new ResultTable(POPUP_PARENT, RESULT_TABLE_LOCATOR);
		buttonSearch = new Button(POPUP_PARENT, By.xpath(".//input[@value = 'Search'] | .//button[contains(. , 'Search')]"), Waiters.AJAX);
		buttonCancel = new Button(POPUP_PARENT, By.xpath(".//input[@value = 'cancel' or @value = 'Cancel']"), Waiters.AJAX.then(Waiters.AJAX));
		buttonClear = new Button(POPUP_PARENT, By.xpath(".//input[@value = 'Clear' or @value = 'clear']"));
		labelErrorMessage = new StaticElement(POPUP_PARENT, ERROR_MESSAGE_LOCATOR);
	}

}
