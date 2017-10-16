package aaa.modules.bct;

public enum BctType {
	BATCH_TEST("BatchTest"),
	ONLINE_TEST("OnlineTest");

	private String bctType;

	BctType(String t) {
		bctType = t;
	}

	public String getName() {
			return bctType;
		}
}
