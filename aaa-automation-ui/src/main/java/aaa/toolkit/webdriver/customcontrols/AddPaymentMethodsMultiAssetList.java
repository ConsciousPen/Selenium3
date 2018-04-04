package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.List;

public class AddPaymentMethodsMultiAssetList extends MultiAssetList {
	public static Button buttonAddUpdateCreditCard = new Button(By.xpath("//a[@id='purchaseForm:addPaymentMethodBtn' or @id='updateForm:addPaymentMethodBtn' or @id='paymentForm:addPaymentMethodButton']"));
	public static JavaScriptButton buttonAddUpdatePaymentMethod = new JavaScriptButton(By.xpath("//input[@id='paymentMethodForm:pciSaveBtn' or @id='paymentMethodEFTForm:eftSaveBtn']"));
	private static Button buttonBack = new Button(By.id("primaryButtonsForm:backButton_footer"), Waiters.AJAX.then(Waiters.SLEEP(1000)).then(Waiters.AJAX));
	public static Table tablePaymentMethods = new Table(By.id("paymentMethodListTable:paymentMethodDataTable"));

	public AddPaymentMethodsMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public AddPaymentMethodsMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void setRawValue(List<TestData> value) {
		buttonAddUpdateCreditCard.click();
		super.setRawValue(value);
		buttonBack.click();
	}

	@Override
	protected boolean sectionExists(int index) {
		return tablePaymentMethods.isPresent() && tablePaymentMethods.getRow(index + 1).isPresent();
	}

	@Override
	protected void addSection(int index, int size) {
		//ignored - there is no special control for adding section
	}

	@Override
	protected void selectSection(int index) {
		if (sectionExists(index)) {
			tablePaymentMethods.getRow(index + 1).getCell("Action").controls.links.get("Edit").click();
		}
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		super.setSectionValue(index, value);
		buttonAddUpdatePaymentMethod.click();
	}
}
