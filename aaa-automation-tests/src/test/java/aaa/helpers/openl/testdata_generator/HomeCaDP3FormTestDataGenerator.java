package aaa.helpers.openl.testdata_generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class HomeCaDP3FormTestDataGenerator {
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDL2482DataFunction = openLPolicy -> {
		//TODO clarify form
		//List<TestData> tdList = new ArrayList<>();
		//tdList.add(DataProviderFactory.dataOf("Action", "Add"));
		return null;
	};

	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDP0418DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		Dollar coverageLimit = new Dollar(openLPolicy.getForms().stream().filter(c -> "DP 04 18".equals(c.getFormCode())).findFirst().get().getLimit());
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementDP0418.COVERAGE_LIMIT.getLabel(), coverageLimit.toString()));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDP0471DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		Double coverageLimit = openLPolicy.getForms().stream().filter(c -> "DP 04 71".equals(c.getFormCode())).findFirst().get().getLimit();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementDP0471.COVERAGE_LIMIT.getLabel(), "contains=" + coverageLimit.toString().split("\\.")[0]));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDP0473DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf("Action", "Add"));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDP0475DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf("Action", "Add"));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDP0495DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf("Action", "Add"));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDW0420DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		Double coverageLimit = openLPolicy.getForms().stream().filter(c -> "DW 04 20".equals(c.getFormCode())).findFirst().get().getLimit();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementDW0420.ADDITIONAL_COVERAGE_LIMIT.getLabel(), "contains=" + coverageLimit.toString().split("\\.")[0]));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDW0421DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		Dollar coverageLimit = new Dollar(openLPolicy.getForms().stream().filter(c -> "DW 04 21".equals(c.getFormCode())).findFirst().get().getLimit());
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementDW0421.DESCRIPTION_OF_STRUCTURE.getLabel(), "Test", 
				HomeCaMetaData.EndorsementTab.EndorsementDW0421.LIMIT_OF_LIABILITY.getLabel(), coverageLimit.toString()));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDW0925DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add", 
				HomeCaMetaData.EndorsementTab.EndorsementDW0925.REASON_FOR_VACANCY.getLabel(), "Test reason", 
				HomeCaMetaData.EndorsementTab.EndorsementDW0925.LENGTH_OF_VACANCY.getLabel(), "10"));
		return tdList;
	};
	
	private static Function<HomeCaDP3OpenLPolicy, List<TestData>> formDW0463DataFunction = openLPolicy -> {
		List<TestData> tdList = new ArrayList<>();
		Dollar coverageLimit = new Dollar(openLPolicy.getForms().stream().filter(c -> "DW 04 63".equals(c.getFormCode())).findFirst().get().getLimit());
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementDW0463.COVERAGE_LIMIT.getLabel(), coverageLimit.toString().split("\\.")[0]));
		return tdList;
	};
	
	public static List<TestData> getFormTestData(HomeCaDP3OpenLPolicy openLPolicy, String formCode) {
		return getFormEnum(formCode).getTestData(openLPolicy);
	}
	
	public static String getFormMetaKey(String formCode) {
		return getFormEnum(formCode).getMetaKey();
	}
	
	private static Forms getFormEnum(String formCode) {
		return Arrays.stream(Forms.values()).filter(f -> f.getFormCode().equals(formCode)).findFirst().orElseThrow(() -> new IstfException("There is no Form enum with form code: " + formCode));
	}
	
	public enum Forms {
		DL_24_82(HomeCaMetaData.EndorsementTab.DL_24_82.getLabel(), "DL 24 82", formDL2482DataFunction), 
		DP_04_18(HomeCaMetaData.EndorsementTab.DP_04_18.getLabel(), "DP 04 18", formDP0418DataFunction),
		DP_04_71(HomeCaMetaData.EndorsementTab.DP_04_71.getLabel(), "DP 04 71", formDP0471DataFunction),
		DP_04_73(HomeCaMetaData.EndorsementTab.DP_04_73.getLabel(), "DP 04 73", formDP0473DataFunction),
		DP_04_75(HomeCaMetaData.EndorsementTab.DP_04_75.getLabel(), "DP 04 75", formDP0475DataFunction),
		DP_04_95(HomeCaMetaData.EndorsementTab.DP_04_95.getLabel(), "DP 04 95", formDP0495DataFunction),
		DW_04_20(HomeCaMetaData.EndorsementTab.DW_04_20.getLabel(), "DW 04 20", formDW0420DataFunction),
		DW_04_21(HomeCaMetaData.EndorsementTab.DW_04_21.getLabel(), "DW 04 21", formDW0421DataFunction),
		DW_09_25(HomeCaMetaData.EndorsementTab.DW_09_25.getLabel(), "DW 09 25", formDW0925DataFunction),
		DW_04_63(HomeCaMetaData.EndorsementTab.DW_04_63.getLabel(), "DW 04 63", formDW0463DataFunction);
		private final String metaKey;
		private final String formCode;
		private final Function<HomeCaDP3OpenLPolicy, List<TestData>> testDataFunction; 
		
		Forms(String metaKey, String formCode, Function<HomeCaDP3OpenLPolicy, List<TestData>> testDataFunction) {
			this.metaKey = metaKey;
			this.formCode = formCode;
			this.testDataFunction = testDataFunction;
		}
		
		public String getMetaKey() {
			return metaKey;
		}

		public String getFormCode() {
			return formCode;
		}

		public Function<HomeCaDP3OpenLPolicy, List<TestData>> getTestDataFunction() {
			return testDataFunction;
		}
		
		public List<TestData> getTestData(HomeCaDP3OpenLPolicy openLPolicy) {
			List<TestData> tdList = getTestDataFunction().apply(openLPolicy);
			return tdList;
		}
		
	}
}
