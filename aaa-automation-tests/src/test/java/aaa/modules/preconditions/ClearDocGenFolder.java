package aaa.modules.preconditions;

import aaa.helpers.docgen.DocGenHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import aaa.helpers.ssh.RemoteHelper;


public class ClearDocGenFolder {

	@Test
	public void clearDocGenFolder() {
		try {
			RemoteHelper.clearFolder(DocGenHelper.JOB_GENERATION_DOCGEN_FOLDER);
			RemoteHelper.clearFolder(DocGenHelper.DOCGEN_SOURCE_FOLDER);
			RemoteHelper.clearFolder(DocGenHelper.DOCGEN_BATCH_SOURCE_FOLDER);
		} catch (Exception e) {
			Assert.fail("Clearing doc gen folder failed: \n", e);
		}
	}
}
