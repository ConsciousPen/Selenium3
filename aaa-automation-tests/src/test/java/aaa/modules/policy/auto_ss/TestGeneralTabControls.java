/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import java.util.Arrays;
import org.testng.annotations.Test;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;

/**
 * @author Dmitry Chubkov
 * @name General tab controls check for AutoSS product, CO state
 * @scenario
 * 1. Initiate AutoSS quote creation
 * 2. Go to General Tab
 * 3. Verify Dropdown Values on General tab
 * 4. Verify that there is no Motorcycle option in 'AAA Products Owned' section
 * 5. Select option "Yes" For all available products owned - Life, Home, Renters, Condo.
 * 6. Do not provide policy numbers for any of those current AAA policies.
 * 7. Verify field TollFree Number visible
 * 8. Select any option other than "None" for 'Adversely Impacted' field.
 * 9. Verify dropdown visible
 *
 * @details
 */
public class TestGeneralTabControls extends AutoSSBaseTest {

	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testGeneralTabControls() {
		GeneralTab gTab = new GeneralTab();

		mainApp().open();

		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), GeneralTab.class, true);

		//Verify Dropdown Values on General tab
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Own Home", "Own Condo", "Own Mobile Home", "Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other", ""));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Yes", "No", "Membership Pending"));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.SOURCE_OF_BUSINESS.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("New Business", "Spin", "Split", "Rewrite", "Book Roll"));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Standard", "Named Non Owner"));

		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("Annual", "Semi-annual"));

		//Verify that there is no Motorcycle option in 'AAA Products Owned' section
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE).verify.present(false);

		//Select option "Yes" For all available products owned - Life, Home, Renters, Condo.
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE).setValue("Yes");
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.HOME).setValue("Yes");
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.RENTERS).setValue("Yes");
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CONDO).setValue("Yes");

		//Verify field TollFree Number visible
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.TOLLFREE_NUMBER).verify.present();

		//Select any option other than "None" for 'Adversely Impacted' field.
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).setRandomValueExcept("None");

		//Verify dropdown visible
		gTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.ADVERSELY_IMPACTED).verify.present();
	}
}
