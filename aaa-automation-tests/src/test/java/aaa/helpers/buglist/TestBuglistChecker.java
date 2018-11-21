package aaa.helpers.buglist;

import org.testng.annotations.Test;

public class TestBuglistChecker {

	@Test
	public void cleanBuglist() throws Exception {
		BuglistUtils.removeClosed();
	}
}
