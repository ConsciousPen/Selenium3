package aaa.toolkit.webdriver.customcontrols;

import java.util.List;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public class ActivityInformationMultiAssetList extends MultiAssetList {
	public static AdvancedTable tableActivityInformationList = new AdvancedTable(By.id("policyDataGatherForm:dataGatherView_ListDrivingRecord"));

	public ActivityInformationMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public ActivityInformationMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		((Button) getAssetCollection().get("Add")).click();
	}

	@Override
	protected boolean sectionExists(int index) {
		return tableActivityInformationList.isPresent() && tableActivityInformationList.getRow(index + 1).isPresent();
	}

	@Override
	protected void selectSection(int index) {
		if (sectionExists(index)) {
			tableActivityInformationList.selectRow(index + 1);
		}
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		selectSection(index);
		super.setSectionValue(index, value);
	}
}
