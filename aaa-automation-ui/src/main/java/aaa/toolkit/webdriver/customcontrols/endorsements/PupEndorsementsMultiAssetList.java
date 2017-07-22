package aaa.toolkit.webdriver.customcontrols.endorsements;

import org.openqa.selenium.By;
import aaa.main.modules.policy.pup.defaulttabs.EndorsementsTab;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class PupEndorsementsMultiAssetList extends PropertyEndorsementsMultiAssetList {

	public PupEndorsementsMultiAssetList(BaseElement<?, ?> parent, By locator,
			Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
		endorsementsTab = new EndorsementsTab();
	}
}
