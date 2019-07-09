package aaa.toolkit.webdriver.customcontrols;

import aaa.main.metadata.BillingAccountMetaData;
import org.openqa.selenium.By;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.RepeatAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

public class AdvancedAllocationsRepeatAssetList extends RepeatAssetList {

	private static final String tablePolicyInfo = "//input[contains(@id, 'advAllocationForm:subTotalAmount_%s')]//preceding::table[1]";
	private static final String tableAdvancedAllocationsNetAndTaxes = "//input[contains(@id, 'advAllocationForm:netPremiumAmount_%s')]//ancestor::table[1]";
	private static final String tableAdvancedAllocationsFees = "//input[contains(@id, 'advAllocationForm:feeAmount_%s')]//ancestor::table[1]";

	public static final String TOTAL_AMOUNT = "Total Amount";
	public static final String NET_PREMIUM = "Net Premium";
	public static final String POLICY_FEE = "Policy Fee";
	public static final String PLIGA_FEE = "PLIGA Fee";
	public static final String OTHER = "Other";
	public static final String PRODUCT_SUB_TOTAL = "Product Subtotal";

	private static final ByT ADVANCED_ALLOCATIONS_COMMON_LOCATOR = ByT.id("advAllocationForm");
	private static final ByT ADVANCED_ALLOCATIONS_TOTAL_AMOUNT = ByT.id("advAllocationForm:requestedAmount");
	private static final ByT ADVANCED_ALLOCATIONS_NET_PREMIUM = ByT.id("advAllocationForm:netPremiumAmount_%1$s");
	private static final ByT ADVANCED_ALLOCATIONS_POLICY_FEE = ByT.id("advAllocationForm:feeAmount_%1$s_23");
	private static final ByT ADVANCED_ALLOCATIONS_PLIGA_FEE = ByT.id("advAllocationForm:feeAmount_%1$s_21");
	private static final ByT ADVANCED_ALLOCATIONS_OTHER = ByT.id("advAllocationForm:feeAmount_%1$s_20");
	private static final ByT ADVANCED_ALLOCATIONS_PRODUCT_SUBTOTAL = ByT.id("advAllocationForm:subTotalAmount_%1$s");

	public AdvancedAllocationsRepeatAssetList(BaseElement<?, ?> parent, Class<? extends MetaData> metaDataClass) {
		super(parent, ADVANCED_ALLOCATIONS_COMMON_LOCATOR, metaDataClass);
	}

	public AdvancedAllocationsRepeatAssetList(Class<? extends MetaData> metaDataClass) {
		super(ADVANCED_ALLOCATIONS_COMMON_LOCATOR, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		//ignored - there is no special control for adding section
	}

	@Override
	public AssetList getSection(int sectionIndex) {
		AssetList al = super.getSection(sectionIndex);
		addAssetTo(ADVANCED_ALLOCATIONS_TOTAL_AMOUNT, TOTAL_AMOUNT, al, sectionIndex);
		addAssetTo(ADVANCED_ALLOCATIONS_NET_PREMIUM, NET_PREMIUM, al, sectionIndex);
		addAssetTo(ADVANCED_ALLOCATIONS_POLICY_FEE, POLICY_FEE, al, sectionIndex);
		addAssetTo(ADVANCED_ALLOCATIONS_PLIGA_FEE, PLIGA_FEE, al, sectionIndex);
		addAssetTo(ADVANCED_ALLOCATIONS_OTHER, OTHER, al, sectionIndex);
		addAssetTo(ADVANCED_ALLOCATIONS_PRODUCT_SUBTOTAL, PRODUCT_SUB_TOTAL, al, sectionIndex);
		al.applyConfiguration("AdvancedAllocationsActionTabDetails");
		return al;
	}

	@Override
	protected boolean sectionExists(int index) {
		return new Table(By.xpath(String.format(tablePolicyInfo, index))).isPresent();
	}

	private void addAssetTo(ByT locator, String assetName, AssetList assetList, int sectionIndex) {
		TextBox netPremium = new TextBox(locator.format(sectionIndex));
		netPremium.setName(assetName);
		assetList.addAsset(netPremium);
	}

	public Table getTablePolicyInfo(int index) {
		return new Table(By.xpath(String.format(tablePolicyInfo, index)));
	}

	public Table getTableAdvancedAllocationsNetAndTaxes(int index) {
		return new Table(By.xpath(String.format(tableAdvancedAllocationsNetAndTaxes, index)));
	}

	public Table getTableAdvancedAllocationsFees(int index) {
		return new Table(By.xpath(String.format(tableAdvancedAllocationsFees, index)));
	}
}
