package aaa.helpers.openl.testdata_builder;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public abstract class TestDataGenerator<P extends OpenLPolicy> {
	protected static final Logger log = LoggerFactory.getLogger(TestDataGenerator.class);

	private String state;
	private TestData ratingDataPattern;

	public TestDataGenerator(String state) {
		this(state, DataProviderFactory.emptyData());
	}

	public TestDataGenerator(String state, TestData ratingDataPattern) {
		this.state = state;
		this.ratingDataPattern = ratingDataPattern;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	TestData getRatingDataPattern() {
		return ratingDataPattern;
	}

	public void setRatingDataPattern(TestData ratingDataPattern) {
		this.ratingDataPattern = ratingDataPattern;
	}

	public abstract TestData getRatingData(P openLPolicy);

	String getYesOrNo(Boolean value) {
		if (value == null) {
			return null;
		}
		return Boolean.TRUE.equals(value) ? "Yes" : "No";
	}

	String getYesOrNo(String value) {
		if (value == null) {
			return null;
		}
		return "Y".equalsIgnoreCase(value) ? "Yes" : "No";
	}

	String getDollarValue(int value) {
		return getDollarValue(value, true);
	}

	String getDollarValue(int value, boolean excludeZeroHundredths) {
		String dollarValue = new Dollar(value).toString();
		if (excludeZeroHundredths) {
			dollarValue = dollarValue.replaceAll("\\.00", "");
		}
		return dollarValue;
	}

	String getRangedDollarValue(int fromBoundary, int toBoundary) {
		return getRangedDollarValue(fromBoundary, toBoundary, true);
	}

	String getRangedDollarValue(int fromBoundary, int toBoundary, boolean excludeZeroHundredths) {
		return getDollarValue(fromBoundary, excludeZeroHundredths) + "/" + getDollarValue(toBoundary, excludeZeroHundredths);
	}

	String getRandom(String... values) {
		return values[new Random().nextInt(values.length)];
	}
}
