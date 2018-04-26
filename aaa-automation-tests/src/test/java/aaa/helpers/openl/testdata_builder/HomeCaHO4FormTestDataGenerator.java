package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLForm;
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
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO42DataFunction = (openLPolicy) -> {
		//TODO clarify Territory code for this form
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO42.OFFICE_TYPE.getLabel(), "index=1", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42.DESCRIPTION_OF_BUSINESS_EQUIPMENT.getLabel(), "test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42.BUSINESS_EQUIPMENT_OVER_50_000.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42.FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42.EMPLOYEES_WORKING_ON_THE_PREMISES.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42.BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42.BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42.IS_BUSINESS_CONDUCTED_OR_IS_THERE_EQUIPMENT_STORED.getLabel(), "No"));
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO43DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HO-43".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add",
						HomeCaMetaData.EndorsementTab.EndorsementHO43.DESCRIPTION_OF_BUSINESS_EQUIPMENT.getLabel(), "Test" + instanceNum.toString(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.STREET_ADDRESS_1.getLabel(), "111 Test street", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.CITY.getLabel(), "Beverly Hills", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.STATE.getLabel(), "CA",
						HomeCaMetaData.EndorsementTab.EndorsementHO43.ZIP_CODE.getLabel(), "90255", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.SECTION_II_TERRITORY.getLabel(), "contains=" + form.getTerritoryCode(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.BUSSINESS_EQUIPMENT_OVER_$5000.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.FOOT_TRAFIC_EXCEEDING_2_CUSTOMERS.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.EMPLOYEES_WORKING_ON_PREMISES.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.BUSSINESS_INVOLVING_HAZZARDOUS_SITUATIONS.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.BUSSINESS_INVOLVING_THE_MANUFACTORING.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43.IS_BUSINESS_CONDUCTED.getLabel(), "No"));
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO51DataFunction =  (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Dollar coverageLimit = new Dollar(openLPolicy.getForms().stream().filter(c -> "HO-51".equals(c.getFormCode())).findFirst().get().getLimit());
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO51.COVERAGE_LIMIT.getLabel(), coverageLimit.toString().split("\\.")[0]));
		return tdList;
	}; 
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO70DataFunction =  (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HO-70".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add",
						HomeCaMetaData.EndorsementTab.EndorsementHO70.NUMBER_OF_FAMILY_UNITS.getLabel(), form.getNumOfFamilies(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.ZIP_CODE.getLabel(), "90255", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.STREET_ADDRESS_1.getLabel(), "111 Test street", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.CITY.getLabel(), "Beverly Hills", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.STATE.getLabel(), "CA", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.SECTION_II_TERRITORY.getLabel(), "index=2"));
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO71DataFunction =  (openLPolicy) -> {
		//TODO clarify Class
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO71.NAME_OF_BUSINESS.getLabel(), "Test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.DESCRIPTION_OF_BUSINESS.getLabel(), "test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.CLASSIFICATION_OCCUPATION.getLabel(), "index=2", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.IS_THE_INSURED_SELF_EMPLOYED_A_PARTNER_IN_THE_BUSINESS.getLabel(), "No"));
		return tdList;
	}; 
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO75DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HO-75".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add", 
						HomeCaMetaData.EndorsementTab.EndorsementHO75.BOAT_TYPE.getLabel(), form.getFormClass(),
						HomeCaMetaData.EndorsementTab.EndorsementHO75.HORSEPOWER.getLabel(), "1000", 
						HomeCaMetaData.EndorsementTab.EndorsementHO75.LENGTH.getLabel(), "30", 
						HomeCaMetaData.EndorsementTab.EndorsementHO75.MAXIMUM_SPEED.getLabel(), "250"));
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO90DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO164DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HO-164".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add", 
						HomeCaMetaData.EndorsementTab.EndorsementHO164.MAKE.getLabel(), "Test" + instanceNum.toString(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO164.MODEL.getLabel(), "Model" + instanceNum.toString(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO164.HORSEPOWER.getLabel(), "50"));
				instanceNum++;
			}
		}
		return tdList;
	};  	
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO210DataFunction =  (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Dollar coverageLimit = new Dollar(openLPolicy.getForms().stream().filter(c -> "HO-210".equals(c.getFormCode())).findFirst().get().getLimit());
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO210.COVERAGE_LIMIT.getLabel(), coverageLimit.toString().split("\\.")[0]));
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHARIDataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HARI".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add", 
						HomeCaMetaData.EndorsementTab.EndorsementHARI.NUMBER_OF_FAMILY_UNITS.getLabel(), "index=1", 
						HomeCaMetaData.EndorsementTab.EndorsementHARI.ZIP_CODE.getLabel(), "90255", 
						HomeCaMetaData.EndorsementTab.EndorsementHARI.STREET_ADDRESS_1.getLabel(), "111 Test street", 
						HomeCaMetaData.EndorsementTab.EndorsementHARI.CITY.getLabel(), "Beverly Hills", 
						HomeCaMetaData.EndorsementTab.EndorsementHARI.STATE.getLabel(), "CA", 
						HomeCaMetaData.EndorsementTab.EndorsementHARI.SECTION_II_TERRITORY.getLabel(), "contains=" + form.getTerritoryCode()));
				instanceNum++;
			}
		}
		return tdList;
	}; 
	
	
	public enum Forms {
		HO29(HomeCaMetaData.EndorsementTab.HO_29.getLabel(), "HO-29", formHO29DataFunction), 
		HO42(HomeCaMetaData.EndorsementTab.HO_42.getLabel(), "HO-42", formHO42DataFunction), 
		HO43(HomeCaMetaData.EndorsementTab.HO_43.getLabel(), "HO-43", formHO43DataFunction), 
		HO51(HomeCaMetaData.EndorsementTab.HO_51.getLabel(), "HO-51", formHO51DataFunction), 
		HO70(HomeCaMetaData.EndorsementTab.HO_70.getLabel(), "HO-70", formHO70DataFunction), 
		HO71(HomeCaMetaData.EndorsementTab.HO_71.getLabel(), "HO-71", formHO71DataFunction), 
		HO75(HomeCaMetaData.EndorsementTab.HO_75.getLabel(), "HO-75", formHO75DataFunction), 
		HO90(HomeCaMetaData.EndorsementTab.HO_90.getLabel(), "HO-90", formHO90DataFunction), 
		HO164(HomeCaMetaData.EndorsementTab.HO_164.getLabel(), "HO-164", formHO164DataFunction), 
		HO210(HomeCaMetaData.EndorsementTab.HO_210.getLabel(), "HO-210", formHO210DataFunction), 
		HARI(HomeCaMetaData.EndorsementTab.HARI.getLabel(), "HARI", formHARIDataFunction);
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
