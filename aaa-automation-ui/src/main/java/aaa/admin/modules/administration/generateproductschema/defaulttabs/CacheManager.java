package aaa.admin.modules.administration.generateproductschema.defaulttabs;

import static aaa.main.enums.CacheManagerEnums.CacheManagerTableColumns;
import static aaa.main.enums.CacheManagerEnums.CacheManagerTableColumns.CACHE_NAME;
import static aaa.main.enums.CacheManagerEnums.CachedProjectNameTableColumns;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.admin.metadata.administration.AdministrationMetaData;
import aaa.common.DefaultTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.webdriver.controls.composite.table.Table;

public class CacheManager extends DefaultTab {
	public static Table tableCacheManager = new Table(By.xpath("//div[@id='cachesListForm:cacheListTable']//table"));
	public static Table tableСachedProject = new Table(By.xpath("//div[@id='projectListForm:projectListTable']//table"));

	protected static Logger log = LoggerFactory.getLogger(CacheManager.class);

	protected CacheManager() {
		super(AdministrationMetaData.CacheManager.class);
	}

	public static void getToCacheManagerTab() {
		NavigationPage.toMainAdminTab(NavigationEnum.AdminAppMainTabs.ADMINISTRATION.get());
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.CACHE_MANAGER.get());
	}

	public static void goClearCacheManagerTable() {
		getToCacheManagerTab();
		removeAllFromCacheManagerTable();
	}

	public static void clearFromCacheManagerTable() {
		removeAllFromCacheManagerTable();
	}

	public static void clearFromCacheManagerTable(String cacheName) {
		if(tableCacheManager.getRow(CACHE_NAME.get(), cacheName).isPresent()){
			tableCacheManager.getRow(CACHE_NAME.get(), cacheName).getCell(CacheManagerTableColumns.ACTION.get()).controls.links.getFirst().click();
		}else log.info(cacheName + " is not present in range of Cache Name column values : " + tableCacheManager.getColumn(CACHE_NAME.get()).getValue());

	}

	private static void removeAllFromCacheManagerTable() {
		for (int i = tableCacheManager.getRowsCount(); i > 0; i--) {
			tableCacheManager.getRow(i).getCell(CacheManagerTableColumns.ACTION.get()).controls.links.get("Remove").click();
		}
	}

	private static void removeAllFromCachedProjectTable() {
		for (int i = tableСachedProject.getRowsCount(); i > 0; i--) {
			tableСachedProject.getRow(i).getCell(CachedProjectNameTableColumns.ACTION.get()).controls.links.get("Remove").click();
		}
	}
}