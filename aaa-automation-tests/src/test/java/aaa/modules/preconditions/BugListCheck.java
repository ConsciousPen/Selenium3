package aaa.modules.preconditions;

import org.testng.annotations.Test;
import toolkit.exceptions.IstfException;
import toolkit.utils.buglist.BugList;

public class BugListCheck {

	@Test
	public void checkBugList() {
		try {
			BugList.getBugs("FakeTest", "FakeMessage");
		} catch (Exception e) {
			throw new IstfException("There is an error in buglist.yaml, please check and fix", e);
		}
	}
}
