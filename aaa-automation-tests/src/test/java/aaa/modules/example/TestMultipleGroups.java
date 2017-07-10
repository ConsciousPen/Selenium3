package aaa.modules.example;

import org.testng.annotations.Test;

import aaa.helpers.Groups;
import aaa.modules.BaseTest;

/**
 * Rule 1: all Test classes should start from "Test" prefix. 
 * Rule 2: To run tests using maven and groups it is necessary to specify also "groups.xml" test suite. 
 * Example: mvn clean install -DsuiteFile=src/test/resources/testsuites/groups.xml -Dgroups=groupName 
 * or use profile
 * mvn clean install -Pgroups -Dgroups=Auto_SS
 * 
 * Rule 3: When TestNG running groups it initialize ALL tests inside the package specified and after that it selects the necessary tests.
 *   
 * 
 */
public class TestMultipleGroups extends BaseTest {

	@Test(groups = { Groups.SMOKE, Groups.AUTO_SS })
	public void test1() {
		log.info("TestMultipleGroups1");
	}

	@Test(groups = { Groups.SMOKE, Groups.AUTO_CA })
	public void test2() {
		log.info("TestMultipleGroups2");
	}

	@Test(groups = { Groups.SMOKE, Groups.HOME_SS })
	public void test3() {
		log.info("TestMultipleGroups3");
	}

	@Test(groups = { Groups.SMOKE, Groups.HOME_CA })
	public void test4() {
		log.info("TestMultipleGroups4");
	}

	@Test(groups = { Groups.SMOKE, Groups.PUP })
	public void test5() {
		log.info("TestMultipleGroups5");
	}

}
