package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.function.BiFunction;
import java.util.function.Function;

//import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLForm;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class HomeCAFormTestDataGenerator {
	private static List<String> includedForms = new ArrayList<>();
	//TODO add included forms to list
	static {
		includedForms.add("HO-57"); 
		includedForms.add("HO_90");
	}
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO29DataFunction =  (openlForm) -> DataProviderFactory.emptyData();
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO210DataFunction =  (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHO210.COVERAGE_LIMIT.getLabel(), openlForm.getLimit().toString().split("\\.")[0]);
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO40DataFunction = (openlForm) -> DataProviderFactory.emptyData();
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO42DataFunction = (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHO42.OFFICE_TYPE.getLabel(), "index=1", 
			HomeCaMetaData.EndorsementTab.EndorsementHO42.DESCRIPTION_OF_BUSINESS_EQUIPMENT.getLabel(), "test", 
			HomeCaMetaData.EndorsementTab.EndorsementHO42.BUSINESS_EQUIPMENT_OVER_50_000.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO42.FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO42.EMPLOYEES_WORKING_ON_THE_PREMISES.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO42.BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO42.BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO42.IS_BUSINESS_CONDUCTED_OR_IS_THERE_EQUIPMENT_STORED.getLabel(), false);
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO43DataFunction = (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHO43.DESCRIPTION_OF_BUSINESS_EQUIPMENT.getLabel(), "test", 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.STREET_ADDRESS_1.getLabel(), "111 Test street", 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.CITY.getLabel(), "", 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.STATE.getLabel(), "CA",
			HomeCaMetaData.EndorsementTab.EndorsementHO43.ZIP_CODE.getLabel(), "90255", 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.SECTION_II_TERRITORY.getLabel(), "contains=" + openlForm.getTerritoryCode(), 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.BUSSINESS_EQUIPMENT_OVER_$5000.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.FOOT_TRAFIC_EXCEEDING_2_CUSTOMERS.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.EMPLOYEES_WORKING_ON_PREMISES.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.BUSSINESS_INVOLVING_HAZZARDOUS_SITUATIONS.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.BUSSINESS_INVOLVING_THE_MANUFACTORING.getLabel(), false, 
			HomeCaMetaData.EndorsementTab.EndorsementHO43.IS_BUSINESS_CONDUCTED.getLabel(), false);
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO44DataFunction = (openlForm) -> DataProviderFactory.emptyData();
	private static Function<HomeCaHO3OpenLForm, TestData> formHO57DataFunction = (openlForm) -> DataProviderFactory.emptyData();
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO70DataFunction =  (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHO70.NUMBER_OF_FAMILY_UNITS.getLabel(), openlForm.getNumOfFamilies(), 
			HomeCaMetaData.EndorsementTab.EndorsementHO70.ZIP_CODE.getLabel(), "90255", 
			HomeCaMetaData.EndorsementTab.EndorsementHO70.STREET_ADDRESS_1.getLabel(), "111 Test street", 
			HomeCaMetaData.EndorsementTab.EndorsementHO70.CITY.getLabel(), "", 
			HomeCaMetaData.EndorsementTab.EndorsementHO70.STATE.getLabel(), "CA", 
			HomeCaMetaData.EndorsementTab.EndorsementHO70.SECTION_II_TERRITORY.getLabel(), "index=2");
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO71DataFunction =  (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHO71.NAME_OF_BUSINESS.getLabel(), "Test", 
			HomeCaMetaData.EndorsementTab.EndorsementHO71.DESCRIPTION_OF_BUSINESS.getLabel(), "test", 
			HomeCaMetaData.EndorsementTab.EndorsementHO71.CLASSIFICATION_OCCUPATION.getLabel(), "index=2", 
			HomeCaMetaData.EndorsementTab.EndorsementHO71.IS_THE_INSURED_SELF_EMPLOYED_A_PARTNER_IN_THE_BUSINESS.getLabel(), false);
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO75DataFunction =  (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHO75.BOAT_TYPE.getLabel(), openlForm.getFormClass(),
			HomeCaMetaData.EndorsementTab.EndorsementHO75.HORSEPOWER.getLabel(), "1000", 
			HomeCaMetaData.EndorsementTab.EndorsementHO75.LENGTH.getLabel(), "30", 
			HomeCaMetaData.EndorsementTab.EndorsementHO75.MAXIMUM_SPEED.getLabel(), "250");
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO90DataFunction = (openlForm) -> DataProviderFactory.emptyData();
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHO164DataFunction = (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHO164.MAKE.getLabel(), "test", 
			HomeCaMetaData.EndorsementTab.EndorsementHO164.MODEL.getLabel(), "test", 
			HomeCaMetaData.EndorsementTab.EndorsementHO164.HORSEPOWER.getLabel(), "50");
	
	private static Function<HomeCaHO3OpenLForm, TestData> formHARIDataFunction = (openlForm) -> DataProviderFactory.dataOf(
			HomeCaMetaData.EndorsementTab.EndorsementHARI.NUMBER_OF_FAMILY_UNITS.getLabel(), "index=1", 
			HomeCaMetaData.EndorsementTab.EndorsementHARI.ZIP_CODE.getLabel(), "90255", 
			HomeCaMetaData.EndorsementTab.EndorsementHARI.STREET_ADDRESS_1.getLabel(), "111 Test street", 
			HomeCaMetaData.EndorsementTab.EndorsementHARI.CITY.getLabel(), "", 
			HomeCaMetaData.EndorsementTab.EndorsementHARI.STATE.getLabel(), "CA", 
			HomeCaMetaData.EndorsementTab.EndorsementHARI.SECTION_II_TERRITORY.getLabel(), "contains=" + openlForm.getTerritoryCode());

	public static boolean isFormAdded(String formCode) {
		return false;
	}
	
	public static TestData getFormTestData(HomeCaHO3OpenLForm openLForm) {
		return getFormEnum(openLForm.getFormCode()).getTestData(openLForm);
	}
	
	public static String getFormMetaKey(String formCode) {
		return getFormEnum(formCode).getMetaKey();
	}
	
	private static Forms getFormEnum(String formCode) {
		return Arrays.stream(Forms.values()).filter(f -> f.getFormCode().equals(formCode)).findFirst().orElseThrow(() -> new IstfException("There is no Form enum with form code: " + formCode));
	}
	
	public enum Forms {
		HO29(HomeCaMetaData.EndorsementTab.HO_29.getLabel(), "HO-29", formHO29DataFunction), 
		HO210(HomeCaMetaData.EndorsementTab.HO_210.getLabel(), "HO-210", formHO210DataFunction), 
		HO40(HomeCaMetaData.EndorsementTab.HO_40.getLabel(), "HO-40", formHO40DataFunction),
		HO42(HomeCaMetaData.EndorsementTab.HO_42.getLabel(), "HO-42", formHO42DataFunction), 
		HO43(HomeCaMetaData.EndorsementTab.HO_43.getLabel(), "HO-43", formHO43DataFunction), 
		HO44(HomeCaMetaData.EndorsementTab.HO_44.getLabel(), "HO-44", formHO44DataFunction),
		HO57(HomeCaMetaData.EndorsementTab.HO_57.getLabel(), "HO-57", formHO57DataFunction),
		HO70(HomeCaMetaData.EndorsementTab.HO_70.getLabel(), "HO-70", formHO70DataFunction), 
		HO71(HomeCaMetaData.EndorsementTab.HO_71.getLabel(), "HO-71", formHO71DataFunction), 
		HO75(HomeCaMetaData.EndorsementTab.HO_75.getLabel(), "HO-75", formHO75DataFunction), 
		HO90(HomeCaMetaData.EndorsementTab.HO_90.getLabel(), "HO-90", formHO90DataFunction),
		HO164(HomeCaMetaData.EndorsementTab.HO_164.getLabel(), "HO-164", formHO164DataFunction), 
		HARI(HomeCaMetaData.EndorsementTab.HARI.getLabel(), "HARI", formHARIDataFunction);
		private final String metaKey;
		private final String formCode;
		private final Function<HomeCaHO3OpenLForm, TestData> testDataFunction; 
		
		Forms(String metaKey, String formCode, Function<HomeCaHO3OpenLForm, TestData> testDataFunction) {
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

		public Function<HomeCaHO3OpenLForm, TestData> getTestDataFunction() {
			return testDataFunction;
		}
		
		public TestData getTestData(HomeCaHO3OpenLForm openLForm) {
			TestData td = getTestDataFunction().apply(openLForm);
			td.adjust("Action", "Add");
			return DataProviderFactory.dataOf(getMetaKey(), td);
		}
	}

}
