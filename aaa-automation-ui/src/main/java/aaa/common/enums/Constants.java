package aaa.common.enums;

public class Constants {

	public enum UserGroups {
		QA("QA"),
		E34("E34"),
		F35("F35"),
		G36("G36"),
		L41("L41");
		String data;

		UserGroups(String data) {
			this.data = data;
		}

		public String get() {
			return data;
		}
	}

	public static final class States {
		public static final String AZ = "AZ";
		public static final String CA = "CA";
		public static final String CO = "CO";
		public static final String CT = "CT";
		public static final String DC = "DC";
		public static final String DE = "DE";
		public static final String ID = "ID";
		public static final String IN = "IN";
		public static final String KS = "KS";
		public static final String KY = "KY";
		public static final String MD = "MD";
		public static final String MT = "MT";
		public static final String NJ = "NJ";
		public static final String NV = "NV";
		public static final String NY = "NY";
		public static final String OH = "OH";
		public static final String OK = "OK";
		public static final String OR = "OR";
		public static final String PA = "PA";
		public static final String SD = "SD";
		public static final String UT = "UT";
		public static final String VA = "VA";
		public static final String WV = "WV";
		public static final String WY = "WY";
	}
}
