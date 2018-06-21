package aaa.admin.modules.administration.generateproductschema.defaulttabs;

import org.openqa.selenium.By;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.common.DefaultTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.webdriver.controls.composite.table.Table;

public class CacheManager extends DefaultTab {
	public static Table tableCacheManager = new Table(By.xpath("//div[@id='cachesListForm:cacheListTable']//table"));

	protected CacheManager() {
		super(AdministrationMetaData.CacheManager.class);
	}

	public static void clearCache() {
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.CACHE_MANAGER.get());
		int rowsCount = tableCacheManager.getRowsCount();
		for (int i = rowsCount; i > 0; i--) {
			tableCacheManager.getRow(i).getCell("Action").controls.links.get("Remove").click();
		}
	}
}