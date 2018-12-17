package aaa.modules.preconditions;

import org.testng.annotations.Test;
import aaa.helpers.ssh.RemoteHelper;

public class ClearFolders {

	@Test
	public void clearFolders() {
		RemoteHelper.get().clearFolder("/home/ipb/import", true, true);
		RemoteHelper.get().clearFolder("/home/ipb/result", true, true);
		RemoteHelper.get().clearFolder("/home/mp2", true, true);
	}
}
