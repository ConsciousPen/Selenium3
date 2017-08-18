package aaa.modules;

import aaa.helpers.ssh.RemoteHelper;
import org.testng.annotations.Test;

public class temp {
	@Test
	public void test(){
		RemoteHelper.uploadFile("src/test/resources/liquibase/20170728_143009_PAS_B_PASHUB_EXGPAS_4017_H.xml", "/home/mp2/pas/sit/PAS_E_PAMSYS_PASHUB_4017_H/inbound/20170728_143009_PAS_B_PASHUB_EXGPAS_4017_H.xml");
	}
}
