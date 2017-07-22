package aaa.modules.example;

import org.testng.annotations.Test;
import aaa.helpers.Groups;
import aaa.modules.BaseTest;

public class TestSingleTest3 extends BaseTest {

	@Test(groups = {Groups.SMOKE, Groups.HOME_CA})
	public void test3() {
		log.info("TestSingleGroups3");
	}
}
