package aaa.toolkit.webdriver.customcontrols.endorsements;

import org.openqa.selenium.By;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class HomeCAEndorsementsMultiAssetList extends PropertyEndorsementsMultiAssetList {

	public HomeCAEndorsementsMultiAssetList(BaseElement<?, ?> parent, By locator,
			Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		endorsementsTab = new EndorsementTab();
	}
}
