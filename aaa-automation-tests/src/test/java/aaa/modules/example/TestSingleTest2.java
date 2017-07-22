package aaa.modules.example;

import org.testng.annotations.Test;
import aaa.helpers.Groups;
import aaa.modules.BaseTest;

public class TestSingleTest2 extends BaseTest {

	@Test(groups = {Groups.SMOKE, Groups.HOME_SS})
	public void test2() {
		log.info("TestSingleGroups2");
	}
}
