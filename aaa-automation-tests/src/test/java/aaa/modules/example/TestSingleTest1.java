package aaa.modules.example;

import java.time.LocalDateTime;

import org.testng.annotations.Test;

import aaa.helpers.Groups;
import aaa.modules.BaseTest;
import toolkit.utils.datetime.DateTimeUtils;

public class TestSingleTest1 extends BaseTest {

	@Test(groups = {Groups.SMOKE, Groups.AUTO_SS})
	public void test1() {
		log.info("TestSingleGroups1");
		LocalDateTime.parse("08/02/2017", DateTimeUtils.MM_DD_YYYY);
	}
}
