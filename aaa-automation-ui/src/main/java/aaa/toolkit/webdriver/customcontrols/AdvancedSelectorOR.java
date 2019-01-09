package aaa.toolkit.webdriver.customcontrols;

import static org.apache.commons.collections.CollectionUtils.disjunction;
import static org.apache.commons.collections.CollectionUtils.subtract;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.controls.AdvancedSelector;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.waiters.Waiter;

public class AdvancedSelectorOR extends AdvancedSelector {

	public AdvancedSelectorOR(By locator) {
		super(locator);
		initElementsForOR();
	}

	public AdvancedSelectorOR(By locator, Waiter waitBy) {
		super(locator, waitBy);
		initElementsForOR();
	}

	public AdvancedSelectorOR(BaseElement<?, ?> parent, By locator) {
		super(parent, locator);
		initElementsForOR();
	}

	public AdvancedSelectorOR(BaseElement<?, ?> parent, By locator, Waiter waitBy) {
		super(parent, locator, waitBy);
		initElementsForOR();
	}

	public StaticElement getAdvancedSelectorText() {
		return new StaticElement(this, By.xpath(".//*[contains(@id, 'outputText')]"));
	}

	@Override
	public void setRawValue(List<String> value) {
		if (value.isEmpty()) {
			removeAll();
			return;
		}

		if (StringUtils.equalsIgnoreCase(value.get(0), "ALL")) {
			addAll();
			excludeValuesIfExist(value);
			save();
			return;
		}

		List<String> selected = getRawValue();

		if (disjunction(selected, value).isEmpty()) {
			return;
		}

		open();
		removeValue((List<String>) subtract(selected, value));
		addValue((List<String>) subtract(value, selected));
		save();
	}

	private void initElementsForOR() {
		boolean isPopupForm = this.parent != null;
		this.actionControlsParent = isPopupForm ? new StaticElement(By.xpath("//div[contains(@class, 'rf-pp-cntr') and .//*[contains(@id, 'remove_shuttle')]][parent::body]")) : new StaticElement(this.locator);

		this.textBoxSearch = new TextBox(this.actionControlsParent, By.xpath(".//input[contains(@id, 'searchTemplate')]"));
		this.buttonSearch = new Button(this.actionControlsParent, By.xpath(".//input[@value='Search']"));
		this.errorMessage = new StaticElement(this.actionControlsParent, By.xpath(".//span[contains(@class,'error')]"));
		this.buttonAdd = new Button(this.actionControlsParent, By.xpath(".//*[contains(@id, 'add_shuttle')]"));
		this.buttonRemove = new Button(this.actionControlsParent, By.xpath(".//*[contains(@id, 'remove_shuttle')]"));
		this.buttonSave = new Button(this.actionControlsParent, By.xpath(".//*[(contains(@value,'Update') or contains(text(),'Update') or contains(@value,'Save') or contains(text(),'Save') or contains(@value,'Create')) and not(self::option)]"));
		this.buttonCancel = new Button(this.actionControlsParent, By.xpath(".//*[(contains(@value,'Back') or contains(text(),'Back') or contains(@value,'Cancel') or contains(text(),'Cancel') or contains(@value,'Exit')) and not(self::option)]"));

		this.listboxAvailableItems = new ListBox(this.actionControlsParent, By.xpath(".//select[contains(@id,'available')]"));
		this.listboxSelectedItems = new ListBox(this.actionControlsParent, By.xpath(".//select[contains(@id,'exist')]"));
	}

	private void excludeValuesIfExist(List<String> values) {
		List<String> excludedValues = values.stream().filter(value -> StringUtils.containsIgnoreCase(value, "EXCLUDE")).map(value -> value.replaceFirst("EXCLUDE", "").trim()).collect(Collectors.toList());
		if (!excludedValues.isEmpty()) {
			removeValue(excludedValues);
		}
	}
}
