package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLForm;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeCaHO6FormTestDataGenerator {
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO29DataFunction =  (openLPolicy) -> {
		if (Boolean.FALSE.equals(openLPolicy.getHasPolicySupportingForm())) {
			List<TestData> tdList = new ArrayList<>();
			tdList.add(DataProviderFactory.dataOf("Action", "Remove"));
			return tdList;
		}
		else 
			return null;
	}; 
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO42CDataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO42C.OFFICE_TYPE.getLabel(), "index=1", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42C.DESCRIPTION_OF_BUSINESS_EQUIPMENT.getLabel(), "test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42C.BUSINESS_EQUIPMENT_OVER_50_000.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42C.FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42C.EMPLOYEES_WORKING_ON_THE_PREMISES.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42C.BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS.getLabel(), "No", 
				HomeCaMetaData.EndorsementTab.EndorsementHO42C.BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS.getLabel(), "No"));
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO43CDataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO6OpenLForm form: openLPolicy.getForms()) {
			if ("HO-43C".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add",
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.DESCRIPTION_OF_BUSINESS_EQUIPMENT.getLabel(), "Test" + instanceNum.toString(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.STREET_ADDRESS_1.getLabel(), "111 Test street", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.CITY.getLabel(), "Beverly Hills", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.STATE.getLabel(), "CA",
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.ZIP_CODE.getLabel(), "90255", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.SECTION_II_TERRITORY.getLabel(), "contains=" + form.getTerritoryCode(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.BUSINESS_EQUIPMENT_OVER_50_000.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.EMPLOYEES_WORKING_ON_THE_PREMISES.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS.getLabel(), "No", 
						HomeCaMetaData.EndorsementTab.EndorsementHO43C.BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS.getLabel(), "No"));
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO61CDataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO6OpenLForm form: openLPolicy.getForms()) {
			if ("HO-61C".equals(form.getFormCode())) {
				if (instanceNum.equals(1)) {
					tdList.add(DataProviderFactory.dataOf("Action", "Add"));
					instanceNum++;					
				} 
				else 
					tdList.add(null);
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO70DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for(HomeCaHO6OpenLForm form: openLPolicy.getForms()) {
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
	
	//TODO clarify Co-applicant class and Form class for HO-71
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO71DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO71.NAME_OF_BUSINESS.getLabel(), "Test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.DESCRIPTION_OF_BUSINESS.getLabel(), "test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.CLASSIFICATION_OCCUPATION.getLabel(), "index=2", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.IS_THE_INSURED_SELF_EMPLOYED_A_PARTNER_IN_THE_BUSINESS.getLabel(), "No"));
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO75DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for(HomeCaHO6OpenLForm form: openLPolicy.getForms()) {
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
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO164CDataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO6OpenLForm form: openLPolicy.getForms()) {
			if ("HO-164C".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add", 
						HomeCaMetaData.EndorsementTab.EndorsementHO164C.MAKE.getLabel(), "Test" + instanceNum.toString(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO164C.MODEL.getLabel(), "Model" + instanceNum.toString(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO164C.HORSEPOWER.getLabel(), "50"));
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO210CDataFunction =  (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Dollar coverageLimit = new Dollar(openLPolicy.getForms().stream().filter(c -> "HO-210C".equals(c.getFormCode())).findFirst().get().getLimit());
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO210C.COVERAGE_LIMIT.getLabel(), coverageLimit.toString().split("\\.")[0]));
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO0455DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO1732DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHO1733DataFunction = (openLPolicy) -> {
		return null;
	};

	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHW0008DataFunction = (openLPolicy) -> {
		//TODO 
		return null;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHW0435DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for (HomeCaHO6OpenLForm form: openLPolicy.getForms()) {
			if ("HW 04 35".equals(form.getFormCode())) {
				if (form.getPercentage().equals(2.0)) {
					tdList.add(DataProviderFactory.dataOf(
							"Action", "Add",
							HomeCaMetaData.EndorsementTab.EndorsementHW0435.LOCATION_TYPE.getLabel(), "contains=Other Building", 
							HomeCaMetaData.EndorsementTab.EndorsementHW0435.STREET_ADDRESS_2.getLabel(), "111 Test street"));
				}
				if (form.getPercentage().equals(0.0)) {
					tdList.add(DataProviderFactory.dataOf(
							"Action", "Add",
							HomeCaMetaData.EndorsementTab.EndorsementHW0435.LOCATION_TYPE.getLabel(), "Residence Premises"));
				}
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHW0461DataFunction = (openLPolicy) -> {
		//TODO	Scheduled Personal Property tab should be filled next
		return null;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHW0495DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHW0528DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHW0528.YEAR.getLabel(), "2017", 
				HomeCaMetaData.EndorsementTab.EndorsementHW0528.MAKE_OR_MODEL.getLabel(), "Other", 
				HomeCaMetaData.EndorsementTab.EndorsementHW0528.SERIAL_NUMBER.getLabel(), "123456"));
		return tdList;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHW0906DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO6OpenLPolicy, List<TestData>> formHW0934DataFunction = (openLPolicy) -> {
		return null;
	};
	
	
	
	public enum Forms {
		HO29(HomeCaMetaData.EndorsementTab.HO_29.getLabel(), "HO-29", formHO29DataFunction), 
		HO42C(HomeCaMetaData.EndorsementTab.HO_42C.getLabel(), "HO-42C", formHO42CDataFunction), 
		HO43C(HomeCaMetaData.EndorsementTab.HO_43C.getLabel(), "HO-43C", formHO43CDataFunction), 
		HO61C(HomeCaMetaData.EndorsementTab.HO_61C.getLabel(), "HO-61C", formHO61CDataFunction), 
		HO70(HomeCaMetaData.EndorsementTab.HO_70.getLabel(), "HO-70", formHO70DataFunction), 
		HO71(HomeCaMetaData.EndorsementTab.HO_71.getLabel(), "HO-71", formHO71DataFunction),
		HO75(HomeCaMetaData.EndorsementTab.HO_75.getLabel(), "HO-75", formHO75DataFunction),
		HO164C(HomeCaMetaData.EndorsementTab.HO_164C.getLabel(), "HO-164C", formHO164CDataFunction),
		HO210C(HomeCaMetaData.EndorsementTab.HO_210C.getLabel(), "HO-210C", formHO210CDataFunction),
		HO_04_55(HomeCaMetaData.EndorsementTab.HO_04_55.getLabel(), "HO 04 55", formHO0455DataFunction), 
		HO_17_32(HomeCaMetaData.EndorsementTab.HO_17_32.getLabel(), "HO 17 32", formHO1732DataFunction), 
		HO_17_33(HomeCaMetaData.EndorsementTab.HO_17_33.getLabel(), "HO 17 33", formHO1733DataFunction), 
		HW_00_08(HomeCaMetaData.EndorsementTab.HW_00_08.getLabel(), "HW 00 08", formHW0008DataFunction),
		HW_04_35(HomeCaMetaData.EndorsementTab.HW_04_35.getLabel(), "HW 04 35", formHW0435DataFunction),
		HW_04_61(HomeCaMetaData.EndorsementTab.HW_04_61.getLabel(), "HW 04 61", formHW0461DataFunction),
		HW_04_95(HomeCaMetaData.EndorsementTab.HW_04_95.getLabel(), "HW 04 95", formHW0495DataFunction), 
		HW_05_28(HomeCaMetaData.EndorsementTab.HW_05_28.getLabel(), "HW 05 28", formHW0528DataFunction), 
		HW_09_06(HomeCaMetaData.EndorsementTab.HW_09_06.getLabel(), "HW 09 06", formHW0906DataFunction), 
		HW_09_34(HomeCaMetaData.EndorsementTab.HW_09_34.getLabel(), "HW 09 34", formHW0934DataFunction);
		private final String metaKey;
		private final String formCode;
		private final Function<HomeCaHO6OpenLPolicy, List<TestData>> testDataFunction; 
		
		Forms(String metaKey, String formCode, Function<HomeCaHO6OpenLPolicy, List<TestData>> testDataFunction) {
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

		public Function<HomeCaHO6OpenLPolicy, List<TestData>> getTestDataFunction() {
			return testDataFunction;
		}
		
		public List<TestData> getTestData(HomeCaHO6OpenLPolicy openLPolicy) {
			List<TestData> tdList = getTestDataFunction().apply(openLPolicy);
			return tdList;
		}
		
	}
		

}
