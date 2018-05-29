package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLForm;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

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
		List<TestData> tdList = new ArrayList<>();
		String territoryCode = openLPolicy.getForms().stream().filter(c -> "HO-42".equals(c.getFormCode())).findFirst().get().getTerritoryCode(); 
		String officeType; 
		if (territoryCode.equals("Office")) {
			officeType = "Incidental Office";
		}
		else {
			officeType = "Professional Instruction";
		}
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO42.OFFICE_TYPE.getLabel(), officeType, 
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
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO58DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO59DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO60DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO61DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HO-61".equals(form.getFormCode())) {
				if (instanceNum.equals(1)) {
					tdList.add(DataProviderFactory.dataOf("Action", "Add"));
					instanceNum++;					
				} 
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO61CDataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
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
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO70DataFunction =  (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HO-70".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add",
						HomeCaMetaData.EndorsementTab.EndorsementHO70.NUMBER_OF_FAMILY_UNITS.getLabel(), form.getNumOfFamilies(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.ZIP_CODE.getLabel(), "90255", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.STREET_ADDRESS_1.getLabel(), "11" + instanceNum.toString() + " Test street", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.CITY.getLabel(), "Beverly Hills", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.STATE.getLabel(), "CA", 
						HomeCaMetaData.EndorsementTab.EndorsementHO70.SECTION_II_TERRITORY.getLabel(), "index=2"));
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO71DataFunction =  (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>(); 
		String formClass = openLPolicy.getForms().stream().filter(f -> "HO-71".equals(f.getFormCode())).findFirst().get().getFormClass(); 
		Boolean punishmentSurcharge = openLPolicy.getForms().stream().filter(f -> "HO-71".equals(f.getFormCode())).findFirst().get().getHasCorporalPunishmentSurcharge();
		TestData td = DataProviderFactory.dataOf(
				"Action", "Add",
				HomeCaMetaData.EndorsementTab.EndorsementHO71.NAME_OF_BUSINESS.getLabel(), "Test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.DESCRIPTION_OF_BUSINESS.getLabel(), "test", 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.CLASSIFICATION_OCCUPATION.getLabel(), getClassificationOccupation_HO71(formClass), 
				HomeCaMetaData.EndorsementTab.EndorsementHO71.IS_THE_INSURED_SELF_EMPLOYED_A_PARTNER_IN_THE_BUSINESS.getLabel(), "No"); 
		if (formClass.equals("Class C") || formClass.equals("Class D")) {
			td.adjust(DataProviderFactory.dataOf(
					HomeCaMetaData.EndorsementTab.EndorsementHO71.TEACHERS_EXTENDED_LIABILITY.getLabel(), getYesOrNo(punishmentSurcharge)));
		}
		tdList.add(td);
		return tdList;
	}; 
	
	private static String getClassificationOccupation_HO71(String className) {
		switch (className) {
		case "Class A": 
			return "Office clerical";
		case "Class B": 
			return "Sales";
		case "Class C": 
			return "Teacher - athletic/physical training, labratory/manual training"; 
		case "Class D": 
			return "Teacher - Other";
		default: 
			return "index=2";
		}
	}
	
	private static String getYesOrNo(Boolean value) {
		if (value == null) {
			return null;
		}
		return Boolean.TRUE.equals(value) ? "Yes" : "No";
	}
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO75DataFunction = (openLPolicy) -> {
		List<TestData> tdList = new ArrayList<>();
		Integer instanceNum = 1;
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if ("HO-75".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", "Add", 
						HomeCaMetaData.EndorsementTab.EndorsementHO75.BOAT_TYPE.getLabel(), form.getFormClass(),
						HomeCaMetaData.EndorsementTab.EndorsementHO75.HORSEPOWER.getLabel(), "1000", 
						HomeCaMetaData.EndorsementTab.EndorsementHO75.LENGTH.getLabel(), "3" + instanceNum.toString(), 
						HomeCaMetaData.EndorsementTab.EndorsementHO75.MAXIMUM_SPEED.getLabel(), "250"));
				instanceNum++;
			}
		}
		return tdList;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO76DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO77DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO78DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO79DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO80DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO81DataFunction = (openLPolicy) -> {
		return null;
	};
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO82DataFunction = (openLPolicy) -> {
		return null;
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
	
	private static Function<HomeCaHO4OpenLPolicy, List<TestData>> formHO177DataFunction = (openLPolicy) -> {
		return null;
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
	
	public static List<TestData> getFormTestData(HomeCaHO4OpenLPolicy openLPolicy, String formCode) {
		return getFormEnum(formCode).getTestData(openLPolicy);
	}
	
	public static String getFormMetaKey(String formCode) {
		return getFormEnum(formCode).getMetaKey();
	}
	
	private static Forms getFormEnum(String formCode) {
		return Arrays.stream(Forms.values()).filter(f -> f.getFormCode().equals(formCode)).findFirst().orElseThrow(() -> new IstfException("There is no Form enum with form code: " + formCode));
	}
	
	public enum Forms {
		HO29(HomeCaMetaData.EndorsementTab.HO_29.getLabel(), "HO-29", formHO29DataFunction), 
		HO42(HomeCaMetaData.EndorsementTab.HO_42.getLabel(), "HO-42", formHO42DataFunction), 
		HO43(HomeCaMetaData.EndorsementTab.HO_43.getLabel(), "HO-43", formHO43DataFunction), 
		HO51(HomeCaMetaData.EndorsementTab.HO_51.getLabel(), "HO-51", formHO51DataFunction), 
		HO58(HomeCaMetaData.EndorsementTab.HO_58.getLabel(), "HO-58", formHO58DataFunction), 
		HO59(HomeCaMetaData.EndorsementTab.HO_59.getLabel(), "HO-59", formHO59DataFunction), 
		HO60(HomeCaMetaData.EndorsementTab.HO_60.getLabel(), "HO-60", formHO60DataFunction), 
		HO61(HomeCaMetaData.EndorsementTab.HO_61.getLabel(), "HO-61", formHO61DataFunction), 
		HO61C(HomeCaMetaData.EndorsementTab.HO_61C.getLabel(), "HO-61C", formHO61CDataFunction), 
		HO70(HomeCaMetaData.EndorsementTab.HO_70.getLabel(), "HO-70", formHO70DataFunction), 
		HO71(HomeCaMetaData.EndorsementTab.HO_71.getLabel(), "HO-71", formHO71DataFunction), 
		HO75(HomeCaMetaData.EndorsementTab.HO_75.getLabel(), "HO-75", formHO75DataFunction), 
		HO76(HomeCaMetaData.EndorsementTab.HO_76.getLabel(), "HO-76", formHO76DataFunction), 
		HO77(HomeCaMetaData.EndorsementTab.HO_77.getLabel(), "HO-77", formHO77DataFunction), 
		HO78(HomeCaMetaData.EndorsementTab.HO_78.getLabel(), "HO-78", formHO78DataFunction), 
		HO79(HomeCaMetaData.EndorsementTab.HO_79.getLabel(), "HO-79", formHO79DataFunction), 
		HO80(HomeCaMetaData.EndorsementTab.HO_80.getLabel(), "HO-80", formHO80DataFunction), 
		HO81(HomeCaMetaData.EndorsementTab.HO_81.getLabel(), "HO-81", formHO81DataFunction), 
		HO82(HomeCaMetaData.EndorsementTab.HO_82.getLabel(), "HO-82", formHO82DataFunction), 
		HO90(HomeCaMetaData.EndorsementTab.HO_90.getLabel(), "HO-90", formHO90DataFunction), 
		HO164(HomeCaMetaData.EndorsementTab.HO_164.getLabel(), "HO-164", formHO164DataFunction), 
		HO177(HomeCaMetaData.EndorsementTab.HO_177.getLabel(), "HO-177", formHO177DataFunction), 
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