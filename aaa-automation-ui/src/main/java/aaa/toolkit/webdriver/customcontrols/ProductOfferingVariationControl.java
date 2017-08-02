package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import aaa.common.components.Dialog;
import aaa.main.modules.policy.home_ss.defaulttabs.ProductOfferingTab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

//TODO-dchubkov: add javadoc
public class ProductOfferingVariationControl extends AssetList {
	private Button btnAddAdditionalVariation = new ProductOfferingTab().btnAddAdditionalVariation;
	private Dialog confirmationDialog = new Dialog(By.xpath("//div[contains(@id, 'policyDataGatherForm:QuoteVariation3_ConfirmDialog_container')]"));
	private Link linkSelectVariation;
	private Link linkRemoveVariation;
	private Link linkRestoreDefaults;
	private StaticElement labelVariationTitleSelected;

	public ProductOfferingVariationControl(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		linkSelectVariation = new Link(new ByChained(locator, By.xpath(".//input[@value='Select variation']")), Waiters.AJAX);
		linkRemoveVariation = new Link(new ByChained(locator, By.xpath(".//input[@value='Remove variation']")), Waiters.AJAX);
		linkRestoreDefaults = new Link(new ByChained(locator, By.xpath(".//input[@value='Restore defaults']")), Waiters.AJAX);
		labelVariationTitleSelected = new StaticElement(new ByChained(locator, By.xpath(".//span[@class='variationTitleSelected']")), Waiters.NONE);
		addExtraAssets();
	}

	@Override
	protected void setRawValue(TestData data) {
		while (!this.isPresent() && btnAddAdditionalVariation.isPresent() && btnAddAdditionalVariation.isEnabled()) {
			btnAddAdditionalVariation.click();
		}
		this.verify.present(getName() + " product offering variation is absent, unable to fill values.");
		super.setRawValue(data);
	}

	public void selectVariation() {
		linkSelectVariation.click();
	}

	public void removeVariation() {
		linkRemoveVariation.click();
		confirmationDialog.confirm();
	}

	public void restoreDefaults() {
		linkRestoreDefaults.click();
	}

	public Boolean isVariationSelected() {
		return labelVariationTitleSelected.isPresent() && labelVariationTitleSelected.isVisible() && "(Selected)".equals(labelVariationTitleSelected.getValue());
	}

	private void addExtraAssets() {
		TextBox coverageALimit = new TextBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageA_limitAmount_attributes_AAACoverageA_limitAmount_limitAmount')]")), Waiters.NONE);
		ComboBox coverageBPercent = new ComboBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageB_additionalLimitAmount_attributes_AAACoverageB_additionalLimitAmount_additionalLimitAmount')]")), Waiters.AJAX);
		TextBox coverageBLimit = new TextBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageB_limitAmount_attributes_AAACoverageB_limitAmount_limitAmount")), Waiters.NONE);
		TextBox coverageCLimit = new TextBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageC_limitAmount_attributes_AAACoverageC_limitAmount_limitAmount')]")), Waiters.AJAX);
		ComboBox coverageDPercent = new ComboBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageD_additionalLimitAmount_attributes_AAACoverageD_additionalLimitAmount_additionalLimitAmount')]")), Waiters.AJAX);
		TextBox coverageDLimit = new TextBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageD_limitAmount_attributes_AAACoverageD_limitAmount_limitAmount")), Waiters.NONE);
		ComboBox coverageELimit = new ComboBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageE_limitAmount_attributes_AAACoverageE_limitAmount_limitAmount')]")), Waiters.AJAX);
		ComboBox coverageFLimit = new ComboBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAACoverageF_limitAmount_attributes_AAACoverageF_limitAmount_limitAmount')]")), Waiters.AJAX);
		ComboBox deductible = new ComboBox(new ByChained(locator, By.xpath(".//input[contains(@id, 'AAAPropertyDeductible_limitAmount_attributes_AAAPropertyDeductible_limitAmount_limitAmount')]")), Waiters.AJAX);
		StaticElement basePremium = new StaticElement(new ByChained(locator, By.xpath(".//tr[@class, 'variationCoveragesTotalsRow variationCoveragesTotalsRowDisplay']")), Waiters.NONE);
		StaticElement subtotal = new StaticElement(new ByChained(locator, By.xpath(".//tr[@class, 'variationTotalsRow variationTotalsRowDisplay']")), Waiters.NONE);
		StaticElement totalPremium = new StaticElement(new ByChained(locator, By.xpath(".//tr[@class, 'variationTotalsRow variationTotalsRowDisplay']")), Waiters.NONE);

		coverageALimit.setName("Coverage A");
		coverageBPercent.setName("Coverage B Percent");
		coverageBLimit.setName("Coverage B Limit");
		coverageCLimit.setName("Coverage C");
		coverageDPercent.setName("Coverage D Percent");
		coverageDLimit.setName("Coverage D Limit");
		coverageELimit.setName("Coverage E");
		coverageFLimit.setName("Coverage F");
		deductible.setName("Deductible");
		basePremium.setName("Base Premium");
		subtotal.setName("Subtotal (Endorsement)");
		totalPremium.setName("Total Premium");

		addAsset(coverageALimit);
		addAsset(coverageBPercent);
		addAsset(coverageBLimit);
		addAsset(coverageCLimit);
		addAsset(coverageDPercent);
		addAsset(coverageDLimit);
		addAsset(coverageELimit);
		addAsset(coverageFLimit);
		addAsset(deductible);
		addAsset(basePremium);
		addAsset(subtotal);
		addAsset(totalPremium);
	}
}
