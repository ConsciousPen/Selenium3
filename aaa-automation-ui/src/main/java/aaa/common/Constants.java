package aaa.common;

public class Constants {
	public enum States {
		UT("UT"), CA("CA"), AZ("AZ");

		String state;

		private States(String state) {
			this.state = state;
		}

		public String get() {
			return state;
		}
	}
	
	public enum HomePolicyType {
		HO3("HO3"), HO4("HO4"), HO6("HO6"), DP6("DP6");

		String state;

		private HomePolicyType(String state) {
			this.state = state;
		}

		public String get() {
			return state;
		}
	}
}
