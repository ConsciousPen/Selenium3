package aaa.main.enums;

public final class DiscountEnum {

	public enum HomeSSDiscounts {

		ZERO_PRIOR_CLAIMS("Claims", "Zero-Prior Claims"),
		AAA_MEMBERSHIP("Affinity", "AAA Membership"),
		AAA_LOYALTY("Affinity", "AAA Loyalty"),
		AAA_AUTO("Multi-Policy", "AAA Auto"),
		MATURE_HOMEOWNER("Policyholder", "Mature Homeowner"),
		NEWER_HOME("Safe Home", "Newer Home"),
		PAID_IN_FULL("Payment Plan", "Paid In Full");

		final String category;
		final String name;

		HomeSSDiscounts(String category, String name) {
			this.category = category;
			this.name = name;
		}

		public String getCategory() {
			return category;
		}
		public String getName() {
			return name;
		}
	}

	//TODO Tests across the framework need refactored to use this enum instead of string literals.

}
