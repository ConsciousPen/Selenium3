package aaa.modules.regression;

import aaa.modules.e2e.ScenarioBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


@Test(groups = "testerson")
public class Testerson extends ScenarioBaseTest {

	public String  str = new String();

	@Parameters({"state"})
	@Test()
	public void test01(String test){
		mainApp().open();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		str = ("TEST 01");
		//dTimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		log.info(Thread.currentThread().getId() + " -- "+str + " --- " + test);
	}

	@Parameters({"state"})
	@Test()
	public void test02(String test){
		mainApp().open();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		str = ("TEST 02");
		//TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2));
		log.info(Thread.currentThread().getId() + " -- "+str + " --- " + test);
	}

	@Parameters({"state"})
	@Test()
	public void test03(String test){
		mainApp().open();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		str = ("TEST 03");
		//TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(3));
		log.info(Thread.currentThread().getId() + " -- "+str + " --- " + test);
	}
}
