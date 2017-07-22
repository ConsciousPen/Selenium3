package aaa.toolkit.webdriver.customcontrols;

import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.RepeatAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

/**
 * Control for filling "Premium & Coverages" tab.
 * "Waive Liability" RadioGroup and "Vehicle Coverage" StaticElement are logically belongs to the same section with declared controls in MetaData but physically located outside of section tables.
 * Because of this they are added separately with custom locators in overridden getSection() method.
 * Also sections on UI starts from 1 index therefore index is incremented in overridden getSection() and sectionExists() methods.
 */
public class DetailedVehicleCoveragesRepeatAssetList extends RepeatAssetList {
	private static final ByT VEHICLE_COMMON_LOCATOR = ByT.id("policyDataGatherForm:vehicle_detail_%1$s");
	private static final ByT WAIVE_LIABILITY_COMMON_LOCATOR = ByT.id("policyDataGatherForm:waive_Liability_%1$s");
	private static final ByT VEHICLE_COVERAGE_COMMON_LOCATOR = ByT.id("policyDataGatherForm:subtotalVehiclePremium_%1$s");

	public DetailedVehicleCoveragesRepeatAssetList(BaseElement<?, ?> parent, Class<? extends MetaData> metaDataClass) {
		super(parent, VEHICLE_COMMON_LOCATOR, metaDataClass);
	}

	public DetailedVehicleCoveragesRepeatAssetList(Class<? extends MetaData> metaDataClass) {
		super(VEHICLE_COMMON_LOCATOR, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		//ignored - there is no special control for adding section
	}

	@Override
	public AssetList getSection(int sectionIndex) {
		sectionIndex++; //sections on UI starts from 1
		AssetList al = super.getSection(sectionIndex);
		RadioGroup waiveLiability = new RadioGroup(WAIVE_LIABILITY_COMMON_LOCATOR.format(sectionIndex));
		waiveLiability.setName("Waive Liability");
		al.addAsset(waiveLiability);

		StaticElement vehicleCoverage = new StaticElement(VEHICLE_COVERAGE_COMMON_LOCATOR.format(sectionIndex));
		vehicleCoverage.setName("Vehicle Coverage");
		al.addAsset(vehicleCoverage);

		return al;
	}

	@Override
	public boolean sectionExists(int index) {
		return super.sectionExists(++index);
	}
}
