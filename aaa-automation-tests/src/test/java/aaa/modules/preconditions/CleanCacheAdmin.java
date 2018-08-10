package aaa.modules.preconditions;

import org.testng.annotations.Test;
import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;

public class CleanCacheAdmin extends BaseTest {

	@Test(groups = Groups.PRECONDITION)
	public void testCleanCacheAdmin() {
		adminApp().open();
		new CacheManager().goClearCacheManagerTable();
	}
}
