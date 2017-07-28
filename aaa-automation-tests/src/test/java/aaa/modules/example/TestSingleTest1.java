package aaa.modules.example;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.helpers.Groups;
import aaa.modules.BaseTest;
import toolkit.utils.datetime.DateTimeUtils;

public class TestSingleTest1 extends BaseTest {

	@Test(groups = {Groups.SMOKE, Groups.AUTO_SS})
	public void test1() {
		log.info("TestSingleGroups1");
		log.info(TimeSetterUtil.getInstance().getCurrentTime().toString());
		log.info(DateTimeUtils.getCurrentDateTime().toString());
	}
}
