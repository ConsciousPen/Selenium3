package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class AddAutoViolationsClaimsMultiAssetList extends MultiInstanceAfterAssetList {

	public AddAutoViolationsClaimsMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public AddAutoViolationsClaimsMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		if (index > 0) {
			((Button) getAssetCollection().get("Add Violation/Claim Information")).click();
		}
	}

}
