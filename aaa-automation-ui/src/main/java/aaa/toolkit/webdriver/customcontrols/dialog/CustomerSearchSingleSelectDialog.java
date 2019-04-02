package aaa.toolkit.webdriver.customcontrols.dialog;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.AbstractClickableStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.waiters.Waiters;

public class CustomerSearchSingleSelectDialog extends SingleSelectSearchDialog {
	public CustomerSearchSingleSelectDialog(By locator) {
		super(locator);
	}

	public CustomerSearchSingleSelectDialog(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public CustomerSearchSingleSelectDialog(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	public void select() {
		AbstractClickableStringElement selectControl = getSelectControl();
		if (selectControl != null) {
			selectControl.click(Waiters.SLEEP(5000));
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
}
