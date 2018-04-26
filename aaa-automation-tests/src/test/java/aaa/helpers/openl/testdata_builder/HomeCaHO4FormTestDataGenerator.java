package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeCaHO4FormTestDataGenerator {
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO29DataFunction =  (openLPolicy) -> {
		if (Boolean.FALSE.equals(openLPolicy.getHasPolicySupportingForm())) {
			List<TestData> tdList = new ArrayList<>();
			tdList.add(DataProviderFactory.dataOf("Action", "Remove"));
			return tdList;
		}
		else 
			return null;
	}; 
	
	public enum Forms {
		HO29(HomeCaMetaData.EndorsementTab.HO_29.getLabel(), "HO-29", formHO29DataFunction);
		private final String metaKey;
		private final String formCode;
		private final Function<HomeCaHO4OpenLPolicy, List<TestData>> testDataFunction; 
		
		Forms(String metaKey, String formCode, Function<HomeCaHO4OpenLPolicy, List<TestData>> testDataFunction) {
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

		public Function<HomeCaHO4OpenLPolicy, List<TestData>> getTestDataFunction() {
			return testDataFunction;
		}
		
		public List<TestData> getTestData(HomeCaHO4OpenLPolicy openLPolicy) {
			List<TestData> tdList = getTestDataFunction().apply(openLPolicy);
			return tdList;
		}
		
	}

}
