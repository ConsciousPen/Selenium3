package aaa.toolkit.webdriver.customcontrols;

import aaa.main.metadata.policy.AutoCaMetaData;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

import java.util.Map;

public class AgencyAutoCaAssetList extends AssetList {
	public AgencyAutoCaAssetList(By locator) {
		super(locator);
	}

	public AgencyAutoCaAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public AgencyAutoCaAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void setRawValue(TestData data) {
		for (Map.Entry<String, BaseElement<?, ?>> entry : getAssetCollection().entrySet()) {
			entry.getValue().fill(data);
		}
		if (data.containsKey(AutoCaMetaData.GeneralTab.PolicyInformation.AGENT.getLabel())) {
			ComboBox agent = getAsset(AutoCaMetaData.GeneralTab.PolicyInformation.AGENT);
			String value = agent.getValue();
			agent.setValueByRegex("(?!(" + value + ")$).*");
			agent.setValue(value);
		}
	}
}
