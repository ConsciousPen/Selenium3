package aaa.toolkit.webdriver.customcontrols.endorsements;

import org.openqa.selenium.By;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class HomeSSEndorsementsMultiAssetList extends PropertyEndorsementsMultiAssetList {

	public HomeSSEndorsementsMultiAssetList(BaseElement<?, ?> parent, By locator,
			Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		endorsementsTab = new EndorsementTab();
	}

	@Override
	public void fill(TestData td) {
		super.fill(td);
	}
}
