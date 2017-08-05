package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;

import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class PaymentAssetList extends AssetList{

	
	
	public PaymentAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}


	protected String assetLocator = "//td[input[contains(@value, '%s')] and not(contains(@class, 'hidden'))]/following-sibling::td[1]";


	@Override
	protected void setRawValue(TestData value) {
		for(String key : value.getKeys()){
			new TextBox(parent, By.xpath(String.format(assetLocator, key)), Waiters.AJAX);
		}
		 
	}


}
