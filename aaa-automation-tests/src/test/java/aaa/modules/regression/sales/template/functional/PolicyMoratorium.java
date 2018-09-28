package aaa.modules.regression.sales.template.functional;

import aaa.admin.metadata.product.MoratoriumMetaData;
import aaa.admin.modules.product.moratorium.defaulttabs.AddMoratoriumTab;
import aaa.admin.pages.product.MoratoriumPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.utils.datetime.DateTimeUtils;

public class PolicyMoratorium extends PolicyBaseTest {

	protected static final String INSERT_ZIP_IN_LOOKUPVALUE_TABLE_QUERY = "declare\n"
			+ "  l_lookupId number;\n"
			+ "  l_exst number;  \n"
			+ "begin \n"
			+ "    SELECT id into l_lookupId from lookuplist where lookupname = 'AAAMoratoriumGeographyLocationInfo';\n"
			+ "      SELECT count(1) into l_exst FROM lookupvalue \n"
			+ "        where lookuplist_id = l_lookupId\n"
			+ "        and POSTALCODE = '%1$s';\n"
			+ "  if l_exst = 0\n"
			+ "  then\n"
			+ "    INSERT INTO lookupvalue (DTYPE, CODE, DISPLAYVALUE, countrycd, POSTALCODE, lookuplist_id, statecd, city) \n"
			+ "        VALUES ('AAAMoratoriumGeographyLocationInfoLookupValue', 'US-AZ-%2$s-%1$s', 'US-AZ-%2$s-%1$s', 'US', '%1$s', l_lookupId, 'AZ', '%2$s');\n"
			+ "  end if;\n"
			+ "end;";

	protected static String insertLookupEntry(String zipCode, String city) {
		return String.format(INSERT_ZIP_IN_LOOKUPVALUE_TABLE_QUERY, zipCode, city);
	}

	protected void expireMoratorium(String moratoriumName) {
		adminApp().open();
		NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.PRODUCT.get());
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.PRODUCT_MORATORIUM.get());
		//find moratorium by name and set expiration date = today
		MoratoriumPage.assetListSearch.getAsset(MoratoriumMetaData.SearchMetaData.MORATORIUM_NAME).setValue(moratoriumName);
		MoratoriumPage.buttonSearch.click();
		MoratoriumPage.tableSearchResult.getRow(2).getCell("Actions").controls.links.getFirst().click();
		new AddMoratoriumTab().getAssetList().getAsset(MoratoriumMetaData.AddMoratoriumTab.EXPIRATION_DATE).setValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
		AddMoratoriumTab.buttonSave.click();
		adminApp().close();
	}

}
