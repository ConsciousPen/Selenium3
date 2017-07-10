package aaa.modules.example;

import org.testng.annotations.Test;

import aaa.helpers.Groups;
import aaa.modules.BaseTest;

public class TestSingleTest1 extends BaseTest {

	@Test(groups = { Groups.SMOKE, Groups.AUTO_SS })
	public void test1() {
		log.info("TestSingleGroups1");
	}
}
