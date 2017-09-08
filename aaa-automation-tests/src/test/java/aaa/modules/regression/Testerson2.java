package aaa.modules.regression;

import aaa.modules.e2e.ScenarioBaseTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Test(groups = "testerson")
public class Testerson2 extends ScenarioBaseTest {

	private String  str = new String();

	@Parameters({"state"})
	@Test()
	public void test012(String test){
		mainApp().open();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		str = ("TEST 012");
		log.info(Thread.currentThread().getId() + " -- "+str + " --- " + test);
	}

	@Parameters({"state"})
	@Test()
	public void test022(String test){
		mainApp().open();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		str = ("TEST 022");
		log.info(Thread.currentThread().getId() + " -- "+str + " --- " + test);
	}

	@Parameters({"state"})
	@Test()
	public void test032(String test){
		mainApp().open();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		str = ("TEST 032");
		log.info(Thread.currentThread().getId() + " -- "+str + " --- " + test);
	}
}
