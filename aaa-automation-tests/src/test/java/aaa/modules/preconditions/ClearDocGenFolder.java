package aaa.modules.preconditions;

import aaa.helpers.docgen.DocGenHelper;
import org.testng.annotations.Test;

public class ClearDocGenFolder {

	@Test
	public void clearDocGenFolder() {
		DocGenHelper.clearDocGenFolders();
	}
}
