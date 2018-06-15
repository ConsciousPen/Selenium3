package aaa.main.enums;

public final class DefaultVinVersions {
	private DefaultVinVersions() {
	}

	public enum SignatureSeries {
		SYMBOL_2000("SYMBOL_2000");

		private String id;

		SignatureSeries(String id) {
			set(id);
		}

		public String get() {
			return id;
		}

		public void set(String id) {
			this.id = id;
		}
	}

	public enum CaliforniaSelect {
		SYMBOL_2000("SYMBOL_2000");

		private String id;

		CaliforniaSelect(String id) {
			set(id);
		}

		public String get() {
			return id;
		}

		public void set(String id) {
			this.id = id;
		}
	}

	public enum CaliforniaChoice {
		SYMBOL_2000_CHOICE("SYMBOL_2000_CHOICE");

		private String id;

		CaliforniaChoice(String id) {
			set(id);
		}

		public String get() {
			return id;
		}

		public void set(String id) {
			this.id = id;
		}
	}

}
