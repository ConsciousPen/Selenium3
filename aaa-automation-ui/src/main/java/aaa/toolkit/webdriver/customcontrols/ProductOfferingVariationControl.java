package aaa.toolkit.webdriver.customcontrols;

import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ProductOfferingTab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Custom control for filling Product Offering Tab
 */
public class ProductOfferingVariationControl extends AssetList {
	public static final String BASE_PREMIUM = "Base Premium";
	public static final String SUBTOTAL = "Subtotal (Endorsement)";
	public static final String TOTAL_PREMIUM = "Total Premium";

	private Button btnAddAdditionalVariation;
	private StaticElement labelVariationTitleSelected;
	private StaticElement basePremium;
	private StaticElement subtotal;
	private StaticElement totalPremium;

	public ProductOfferingVariationControl(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);

		btnAddAdditionalVariation = new ProductOfferingTab().btnAddAdditionalVariation;
		labelVariationTitleSelected = new StaticElement(new ByChained(locator, By.xpath(".//span[@class='variationTitleSelected']")), Waiters.NONE);
		basePremium = new StaticElement(new ByChained(locator, By.xpath(".//tr[count(//span[text()='Base Premium']/ancestor::tr/preceding-sibling::*)+1]")), Waiters.NONE);
		subtotal = new StaticElement(new ByChained(locator, By.xpath(".//tr[count(//span[text()='Subtotal (Endorsement)']/ancestor::tr/preceding-sibling::*)+1]")), Waiters.NONE);
		totalPremium = new StaticElement(new ByChained(locator, By.xpath(".//tr[count(//span[text()='Total Premium']/ancestor::tr/preceding-sibling::*)+1]")), Waiters.NONE);

		basePremium.setName(BASE_PREMIUM);
		subtotal.setName(SUBTOTAL);
		totalPremium.setName(TOTAL_PREMIUM);
		addAsset(basePremium);
		addAsset(subtotal);
		addAsset(totalPremium);
	}

	@Override
	protected TestData getRawValue() {
		TestData returnData = super.getRawValue();
		for (String premium : Arrays.asList(BASE_PREMIUM, SUBTOTAL, TOTAL_PREMIUM)) {
			if (getName().equals(returnData.getValue(premium))) {
				// if premium static element is missed its locator points to the variations header.
				// Therefore if its value equals to getName() then we should mask it in order to not return confusing test data
				returnData.mask(premium);
			}
		}
		return returnData;
	}

	@Override
	protected void setRawValue(TestData data) {
		if (data.containsKey(HomeSSMetaData.ProductOfferingTab.VariationControls.REMOVE_VARIATION.getLabel())) {
			removeVariation();
			return;
		}
		if (data.containsKey(HomeSSMetaData.ProductOfferingTab.VariationControls.SELECT_VARIATION.getLabel())) {
			selectVariation();
			data.mask(HomeSSMetaData.ProductOfferingTab.VariationControls.SELECT_VARIATION.getLabel());
		}

		super.setRawValue(data);
	}

	public void addVariation() {
		while (!isPresent() && btnAddAdditionalVariation.isPresent() && btnAddAdditionalVariation.isEnabled()) {
			btnAddAdditionalVariation.click();
		}
		this.verify.present(String.format("Unable to add %s product offering variation.", getName()));
	}

	public void removeVariation() {
		getAsset(HomeSSMetaData.ProductOfferingTab.VariationControls.REMOVE_VARIATION).click();
		Page.dialogConfirmation.confirm();
	}

	public void selectVariation() {
		if (!isVariationSelected()) {
			getAsset(HomeSSMetaData.ProductOfferingTab.VariationControls.SELECT_VARIATION).click();
		}
	}

	public void restoreDefaults() {
		getAsset(HomeSSMetaData.ProductOfferingTab.VariationControls.RESTORE_DEFAULTS).click();
	}

	public Boolean isVariationSelected() {
		return labelVariationTitleSelected.isPresent() && labelVariationTitleSelected.isVisible() && "(Selected)".equals(labelVariationTitleSelected.getValue());
	}
}
