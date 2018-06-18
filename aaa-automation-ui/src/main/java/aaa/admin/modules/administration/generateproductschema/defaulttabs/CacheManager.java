package aaa.admin.modules.administration.generateproductschema.defaulttabs;

import static aaa.main.enums.CacheManagerEnums.CacheManagerTableColumns.ACTION;
import static aaa.main.enums.CacheManagerEnums.CacheManagerTableColumns.CACHE_MANAGER;
import org.openqa.selenium.By;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.common.DefaultTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.webdriver.controls.composite.table.Table;

public class CacheManager extends DefaultTab {
	public static Table tableCacheManager = new Table(By.xpath("//div[@id='cachesListForm:cacheListTable']//table"));
	public static Table tableСachedProject = new Table(By.xpath("//div[@id='projectListForm:projectListTable']//table"));

	protected CacheManager() {
		super(AdministrationMetaData.CacheManager.class);
	}

	public static void goClearCacheManagerTable() {
		getToCacheManagerTab();
		removeAllFromCacheManagerTable();
	}

	public static void clearFromCacheManagerTable() {
		removeAllFromCacheManagerTable();
	}

	public static void clearFromCacheManagerTable(String cacheName) {
		try {
			tableCacheManager.getRow(CACHE_MANAGER.get(),cacheName).getCell(ACTION.get()).click();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void getToCacheManagerTab() {
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.CACHE_MANAGER.get());
	}

	private static void removeAllFromCacheManagerTable() {
		for (int i = tableCacheManager.getRowsCount(); i > 0; i--) {
			tableCacheManager.getRow(i).getCell(ACTION.get()).controls.links.get("Remove").click();
		}
	}
}