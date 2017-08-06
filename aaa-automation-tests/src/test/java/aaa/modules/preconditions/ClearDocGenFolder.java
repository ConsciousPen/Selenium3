package aaa.modules.preconditions;

import org.testng.Assert;
import org.testng.annotations.Test;

import aaa.helpers.ssh.RemoteHelper;


public class ClearDocGenFolder {

	@Test
	public void clearDocGenFolder() {
		try {
			RemoteHelper.clearFolder("/home/mp2/pas/sit/PAS_B_EXGPAS_DCMGMT_6500_D/outbound/");
			RemoteHelper.clearFolder("/home/DocGen/");
			RemoteHelper.clearFolder("/home/DocGen/Batch/");
		} catch (Exception e) {
			Assert.fail("Clearing doc gen folder failed: \n", e);
		}
	}
}
