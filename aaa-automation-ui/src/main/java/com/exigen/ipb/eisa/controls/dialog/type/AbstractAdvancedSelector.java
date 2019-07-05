/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ipb.eisa.controls.dialog.type;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Used when available and selected items are represented as select.<br>
 * Declare with parent = false if control is not presented as popup.<br>
 * @author ikisly
 */
public abstract class AbstractAdvancedSelector extends AbstractStringListElement {
	public Button buttonOpenPopup = new Button(this, By.xpath(".//*[@value='Select' or normalize-space(@value)='select' or text()='Edit Manager'"
			+ " or text()='Select' or text()='select' or contains(@value, 'Copy')]"));
	public TextBox textBoxSearch = new TextBox(actionControlsParent, By.xpath(".//input[contains(@id, 'searchTemplate')]"));
	public Button buttonSearch = new Button(actionControlsParent, By.xpath(".//input[@value='Search']"));
	public StaticElement errorMessage = new StaticElement(actionControlsParent, By.xpath(".//span[contains(@class,'error')]"));
	public Button buttonAdd = new Button(actionControlsParent,
			By.xpath(".//*[(contains(@value,'Add') or contains(text(),'Add')) and not(contains(@value,'Add All')) and not(contains(text(),'Add All')) and not(self::option)]"));
	public Button buttonSave = new Button(actionControlsParent,
			By.xpath(".//*[(contains(@value,'Update') or contains(text(),'Update') or contains(@value,'Save') or contains(text(),'Save') or contains(@value,'Create')) and not(self::option)]"));
	public Button buttonCancel = new Button(actionControlsParent,
			By.xpath(".//*[(contains(@value,'Back') or contains(text(),'Back') or contains(@value,'Cancel') or contains(text(),'Cancel') or contains(@value,'Exit')) and not(self::option)]"));
	protected StaticElement actionControlsParent = isPopupForm ? new StaticElement(By.xpath("//div[contains(@class, 'rf-pp-cntr') " +
			"and .//*[(contains(@value,'Remove') or contains(text(),'Remove'))]][parent::body]")) : new StaticElement(locator);
	protected Button buttonAddAll = new Button(actionControlsParent, By.xpath(".//*[(contains(@value,'Add All') or contains(text(),'Add All')]) and not(self::option)"));
	protected Button buttonRemove = new Button(actionControlsParent, By.xpath(".//*[(contains(@value,'Remove') or contains(text(),'Remove')) and not(self::option)]"));
	protected Button buttonRemoveAll = new Button(actionControlsParent, By.xpath(".//*[(contains(@value,'Remove All') or contains(text(),'Remove All')) and not(self::option)]"));
	private boolean isPopupForm = parent != null;

	public AbstractAdvancedSelector(By locator) {
		super(locator, Waiters.DEFAULT);
	}

	public AbstractAdvancedSelector(By locator, Waiter waitBy) {
		super(locator, waitBy);
	}

	public AbstractAdvancedSelector(BaseElement<?, ?> parent, By locator) {
		super(parent, locator, Waiters.DEFAULT);
	}

	public AbstractAdvancedSelector(BaseElement<?, ?> parent, By locator, Waiter waitBy) {
		super(parent, locator, waitBy);
	}

	protected abstract List<String> getSelectedItems();

	@Override
	public List<String> getRawValue() {
		open();
		List<String> result = getSelectedItems();
		cancel();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRawValue(List<String> value) {
		if (StringUtils.equalsIgnoreCase(value.get(0), "ALL")) {
			addAll();
			excludeValuesIfExist(value);
			save();
			return;
		}

		if (value.isEmpty()) {
			removeAll();
			return;
		}

		List<String> selected = getRawValue();

		if (CollectionUtils.disjunction(selected, value).isEmpty()) {
			return;
		}

		open();
		removeValue((List<String>) CollectionUtils.subtract(selected, value));
		addValue((List<String>) CollectionUtils.subtract(value, selected));
		save();
	}

	protected void open() {
		if (isPopupForm && buttonOpenPopup.isPresent() && buttonOpenPopup.isVisible()) {
			buttonOpenPopup.click();
		}

		if (buttonSearch.isPresent() && buttonSearch.isVisible()) {
			buttonSearch.click();
		}
	}

	protected void save() {
		if (isPopupForm && buttonSave.isPresent() && buttonSave.isVisible()) {
			buttonSave.click();
		}
	}

	protected void cancel() {
		if (isPopupForm && buttonCancel.isPresent() && buttonCancel.isVisible()) {
			buttonCancel.click();
		}
	}

	protected void addAll() {
		open();
		if (buttonAddAll.isPresent() && buttonAddAll.isVisible()) {
			buttonAddAll.click();
		} else {
			addAllOneByOne();
		}
	}

	protected void removeAll() {
		open();
		if (buttonRemoveAll.isPresent() && buttonRemoveAll.isVisible()) {
			buttonRemoveAll.click();
		} else {
			removeAllOneByOne();
		}
		save();
	}

	protected abstract void addAllOneByOne();

	protected abstract void removeAllOneByOne();

	protected abstract void addValue(List<String> value);

	protected abstract void removeValue(List<String> value);

	private void excludeValuesIfExist(List<String> values) {
		List<String> excludedValues = new ArrayList<>();
		for (String value : values) {
			if (StringUtils.containsIgnoreCase(value, "EXCLUDE")) {
				excludedValues.add(value.replaceFirst("EXCLUDE", "").trim());
			}
		}
		if (!excludedValues.isEmpty()) {
			removeValue(excludedValues);
		}
	}
}
