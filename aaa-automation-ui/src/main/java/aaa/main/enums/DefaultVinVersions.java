package aaa.main.enums;

public final class DefaultVinVersions {
	private DefaultVinVersions() {
	}

	public enum DefaultVersions {
		SignatureSeries("SYMBOL_2000"),
		CaliforniaSelect("SYMBOL_2000"),
		SYMBOL_2017("SYMBOL_2017"),
		CaliforniaChoice("SYMBOL_2000_CHOICE");

		private String id;

		DefaultVersions(String id) {
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
