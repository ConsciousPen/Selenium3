package aaa.toolkit.webdriver.customcontrols;

import static toolkit.verification.CustomAssertions.assertThat;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import aaa.common.pages.Page;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public class PersonalPropertyMultiAssetList extends MultiAssetList {
	private final By siblingLocator = new ByChained(locator, By.xpath(".//preceding-sibling::div[1]"));

	private Link linkExpandOrCollapseSection = new Link(new ByChained(siblingLocator, By.xpath(".//a[contains(@name, '_expand') or contains(@name, '_collapse')]")), Waiters.AJAX);
	private TextBox totalLimit = new TextBox(new ByChained(siblingLocator, By.xpath(".//input[contains(@id, 'limitAmount')]")), Waiters.AJAX);
	private StaticElement itemizedTotalLimit = new StaticElement(new ByChained(siblingLocator, By.xpath(".//span[contains(@id, 'itemizedTotalLimit')]")));
	private StaticElement count = new StaticElement(new ByChained(siblingLocator, By.xpath(".//span[contains(@id, 'count')]")));
	private Link linkRemoveAll = new Link(new ByChained(siblingLocator, By.xpath(".//a[text()='Remove']")));
	private StaticElement requirementInfo = new StaticElement(new ByChained(siblingLocator, By.xpath(".//table//td[contains(text(), 'require')]")));

	private AdvancedTable tableItemsList = new AdvancedTable(locator);
	private Button buttonAdd = new Button(new ByChained(locator, By.xpath(".//input[@value='Add']")), Waiters.AJAX);
	private Button buttonRemove = new Button(new ByChained(locator, By.xpath(".//input[contains(@value, 'Remove')]")), Waiters.AJAX);

	public PersonalPropertyMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
		addExtraAssets();
	}

	public PersonalPropertyMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		addExtraAssets();
	}

	@Override
	protected void selectSection(int index) {
		index++;
		expandSection();
		tableItemsList.selectRow(index);
	}

	@Override
	protected void addSection(int index, int size) {
		expandSection();
		CustomAssert.assertTrue(String.format("Can't add new row, looks like tables' rows limit [%s] is reached.", count.getValue()), buttonAdd.isPresent() && buttonAdd.isVisible());
		buttonAdd.click();
	}

	@Override
	protected boolean sectionExists(int index) {
		return Integer.parseInt(count.getValue()) >= index + 1;
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		expandSection();
		if (isTableVisible(tableItemsList) && tableItemsList.getRow(index + 1).isPresent()) {
			selectSection(index);
		}
		super.setSectionValue(index, value);
	}

	@Override
	protected TestData getSectionValue(int index) {
		selectSection(index);
		return super.getSectionValue(index);
	}

	public void removeSection(int index) {
		index++;
		expandSection();
		if (index == 0) {
			assertThat(buttonRemove.isPresent() && buttonRemove.isVisible()).isTrue();
			buttonRemove.click();
			Page.dialogConfirmation.confirm();
		} else {
			assertThat(isTableVisible(tableItemsList) && tableItemsList.isVisible()).isTrue();
			tableItemsList.removeRow(index);
		}
	}

	public void removeAll() {
		linkRemoveAll.click();
		Page.dialogConfirmation.confirm();
	}

	public void expandSection() {
		if (!isSectionExpanded()) {
			linkExpandOrCollapseSection.click();
		}
	}

	public void collapseSection() {
		if (isSectionExpanded()) {
			linkExpandOrCollapseSection.click();
		}
	}

	private boolean isTableVisible(Table table) {
		return table.isPresent() && table.isVisible() && table.getHeader().isPresent() && table.getHeader().isVisible();
	}

	private boolean isSectionExpanded() {
		return buttonAdd.isPresent() && buttonAdd.isVisible() || buttonRemove.isPresent() && buttonRemove.isVisible();
	}

	private void addExtraAssets() {
		linkExpandOrCollapseSection.setName(getName() + " Coverage");
		addAsset(linkExpandOrCollapseSection);

		totalLimit.setName("Total Limit");
		addAsset(totalLimit);

		itemizedTotalLimit.setName("Itemized Total Limit");
		addAsset(itemizedTotalLimit);

		count.setName("Count");
		addAsset(count);

		linkRemoveAll.setName("Remove All");
		addAsset(linkRemoveAll);

		buttonAdd.setName("Add");
		addAsset(buttonAdd);

		buttonRemove.setName("Remove");
		addAsset(buttonRemove);

		tableItemsList.setName("List of " + getName());
		addAsset(tableItemsList);

		requirementInfo.setName(getName() + " requirement info");
		addAsset(requirementInfo);
	}
}
