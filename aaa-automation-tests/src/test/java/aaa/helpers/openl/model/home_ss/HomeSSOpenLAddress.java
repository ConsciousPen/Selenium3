package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLAddress;

public class HomeSSOpenLAddress extends OpenLAddress {
	private Boolean isRetCommunityPresent;

	public Boolean getRetCommunityPresent() {
		return isRetCommunityPresent;
	}

	public void setRetCommunityPresent(Boolean retCommunityPresent) {
		isRetCommunityPresent = retCommunityPresent;
	}

	@Override
	public String toString() {
		return "HomeSSOpenLAddress{" +
				"isRetCommunityPresent=" + isRetCommunityPresent +
				", number=" + number +
				", state='" + state + '\'' +
				", zip='" + zip + '\'' +
				'}';
	}
}
