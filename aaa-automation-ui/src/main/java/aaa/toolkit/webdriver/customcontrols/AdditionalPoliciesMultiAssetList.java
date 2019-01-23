package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public class AdditionalPoliciesMultiAssetList extends MultiAssetList {
	private static final String ACTIVE_UNDERLYING_POLICIES_SEARCH = "ActiveUnderlyingPoliciesSearch";
	private static final String ACTIVE_UNDERLYING_POLICIES_MANUAL = "ActiveUnderlyingPoliciesManual";
	private static Table underlyingPoliciesList = new Table(By.xpath("//table[@id='policyDataGatherForm:pupPolicyPrefillTable']")); //TODO-dchubkov: change to AdvancedTable ?

	public AdditionalPoliciesMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public AdditionalPoliciesMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		if (size != 0) {
			getAsset("Add", Button.class).click();
		}
	}

	@Override
	protected void selectSection(int index) {
		underlyingPoliciesList.getRow(index).getCell(9).controls.links.get("View/Edit").click(Waiters.AJAX); // change to AdvancedTable.selectRow(index) ?
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		SingleSelectSearchDialog searchDialog = (SingleSelectSearchDialog) getAssetCollection().get(ACTIVE_UNDERLYING_POLICIES_SEARCH);
		boolean containsManual = false;

		AssetList fillManualAssetList = (AssetList) getAssetCollection().get(ACTIVE_UNDERLYING_POLICIES_MANUAL);
		if (value.containsKey(ACTIVE_UNDERLYING_POLICIES_SEARCH) && !value.getTestData(ACTIVE_UNDERLYING_POLICIES_SEARCH).getKeys().isEmpty()) {
			searchDialog = (SingleSelectSearchDialog) getAssetCollection().get(ACTIVE_UNDERLYING_POLICIES_SEARCH);
			searchDialog.fill(value);
			if (value.containsKey(ACTIVE_UNDERLYING_POLICIES_MANUAL) && !value.getTestData(ACTIVE_UNDERLYING_POLICIES_MANUAL).getKeys().isEmpty()) {
				selectSection(underlyingPoliciesList.getRowsCount());
				fillManualAssetList.fill(value);
                containsManual = true;
			}
		} else {
			searchDialog.cancel();
            fillManualAssetList.fill(value);
		}
		if (containsManual) {
			((Button) getAssetCollection().get("Save")).click();
		}
	}

}
