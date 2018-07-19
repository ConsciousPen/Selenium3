package aaa.modules.regression.sales.auto_ss.functional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

public class TestVersionsConflictConstants {

	private static final String AVAILABLE = "Available";
	private static final String CURRENT = "Current";

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> namedInsuredInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Claims Report Order.First Name", CURRENT)
					.put("AAA Claims Report Order.Last Name", CURRENT)
					.put("AAA Membership Order.Order Date", AVAILABLE)
					.put("AAA Membership Order.Receipt Date", AVAILABLE)
					.put("AAAMvr Report Order.First Name",CURRENT)
					.put("AAAMvr Report Order.Last Name",CURRENT)
					.put("Driver Information (V2FirstName V2 V2LastName).First Name", CURRENT)
					.put("Driver Information (V2FirstName V2 V2LastName).Middle Name", AVAILABLE)
					.put("Driver Information (V2FirstName V2 V2LastName).Last Name", CURRENT)
					.put("Driver Information (V2FirstName V2 V2LastName).Suffix", AVAILABLE)
					.put("Driver Information (V2FirstName V2 V2LastName).Base Date",CURRENT)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Prefix", AVAILABLE)
					.put("Named Insured Information (V2FirstName V2 V2LastName).First Name", CURRENT)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Middle Name", AVAILABLE)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Last Name", CURRENT)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Suffix", AVAILABLE)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Social Security Number", CURRENT)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Base Date",CURRENT)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Residential Address", CURRENT)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Move-In Date", AVAILABLE)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Prior Address", AVAILABLE)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Mailing Address", CURRENT)
					.put("Named Insured Information (V2FirstName V2 V2LastName).Residence", AVAILABLE)
					.put("Vehicle Information (2011, CHEVROLET, EXPRESS VAN, SPORT VAN).Garaging Address", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> vehicleInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Membership Order.Order Date", CURRENT)
					.put("AAA Membership Order.Receipt Date", CURRENT)
					.put("Vehicle Information (2012, BMW, X5, WAGON 4 DOOR).Usage", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page
	static final Multimap<String, String> namedInsuredInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Driver Information (V2FirstName V2 V2LastName)", "Middle Name")
			.put("Driver Information (V2FirstName V2 V2LastName)", "Suffix")
			.put("Named Insured Information (V2FirstName V2 V2LastName)", "Prefix")
			.put("Named Insured Information (V2FirstName V2 V2LastName)", "Middle Name")
			.put("Named Insured Information (V2FirstName V2 V2LastName)", "Suffix")
			.put("Named Insured Information (V2FirstName V2 V2LastName)", "Move-In Date")
			.put("Named Insured Information (V2FirstName V2 V2LastName)", "Prior Address")
			.put("Named Insured Information (V2FirstName V2 V2LastName)", "Residence")
			.build();

	//all components/attributes that should be on Comparison page
	static final Multimap<String, String> namedInsuredInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.put("AAA Claims Report Order", "First Name")
			.put("AAA Claims Report Order", "Last Name")
			.put("AAAMvr Report Order", "First Name")
			.put("AAAMvr Report Order", "Last Name")
			//end
			.put("Driver Information (V1FirstName V1 V1LastName)", "First Name")
			.put("Driver Information (V1FirstName V1 V1LastName)", "Last Name")
			.put("Driver Information (V1FirstName V1 V1LastName)", "Base Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "First Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Last Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Social Security Number")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Base Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Residential Address")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Mailing Address")
			.put("Vehicle Information (2011, CHEVROLET, EXPRESS VAN, SPORT VAN)", "Garaging Address")
			.build();

	//all components/attributes that should be on Comparison page
	static final Multimap<String, String> vehicleInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.build();

	//all components/attributes that should be on Comparison page
	static final Multimap<String, String> vehicleInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Usage")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "VIN")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Make")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Model")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Series")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Body Style")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Existing Damage")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Existing Damage Description")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Is the vehicle used in any commercial business operations?")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Business Use Description")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Miles One-way to Work or School")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Anti-theft")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "VIN Stub")
			.build();

	//Values that are not in Test Data, but are used in comparison
	static final Multimap<String, String> predefinedExpectedValues = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.putAll("AAA Claims Report Order.First Name", "V2FirstName", "V1FirstName")
			.putAll("AAA Claims Report Order.Last Name", "V2LastName", "V1LastName")
			.putAll("AAAMvr Report Order.Order Date", "", "06/21/2018")//todo should be taken as effective date
			.putAll("AAAMvr Report Order.Receipt Date", "", "06/21/2018") //todo should be taken as effective date
			.putAll("AAAMvr Report Order.Status", "", "Clear")
			.putAll("AAAMvr Report Order.First Name", "V2FirstName", "V1FirstName")
			.putAll("AAAMvr Report Order.Last Name", "V2LastName", "V1LastName")
			.putAll("AAAMvr Report Order.ChoicePoint License Status", "", "1 VALID")
			//end
			.putAll("Named Insured Information (V1FirstName V1 V1LastName).Residential Address", "V2 residence address 1, V2 residence address 2, Red Rock, AZ, 85245", "V1 residence address 1, V1 residence address 2, Phoenix, AZ, 85085")
			.putAll("Named Insured Information (V2FirstName V2 V2LastName).Prior Address", "V1 prior address 1, V1 prior address 2, Phoenix, AZ, 85085", "V2 prior address 1, V2 prior address 2, Red Rock, AZ, 85245")
			.putAll("Named Insured Information (V1FirstName V1 V1LastName).Mailing Address", "V2 mailing address 1, V2 mailing address 2, Red Rock, AZ, 85245", "V1 mailing address 1, V1 mailing address 2, Phoenix, AZ, 85085")
			.putAll("Vehicle Information (2011, CHEVROLET, EXPRESS VAN, SPORT VAN).Garaging Address", "V2 residence address 1, V2 residence address 2, Red Rock, AZ, 85245", "V1 residence address 1, V1 residence address 2, Phoenix, AZ, 85085")
			//Current Carrier Information
			.putAll("Current Carrier Information.Days Lapsed", "1004", "607")
			.putAll("Current Carrier Information.Months with Carrier", "3", "4")
			.putAll("Policy Information.Agency of Record", "500001406", "500001001")
			.putAll("Policy Information.Agent of Record", "500012749", "500013268")
			.putAll("Policy Information.Agent Number", "500012749", "500013268")
			.putAll("Activity Information (Speeding, 05/31/2018, Not included in Rating).Claim Points", "", "0")
			.putAll("Activity Information (Speeding, 05/31/2018, Not included in Rating).Violation points", "0", "")
			//Vehicle Information
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Usage","Business","WorkCommute")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Make","BMW","HYUNDAI")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Model","X5","SONATA")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Series","X5 M","SONATA GLS")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Body Style","WAGON 4 DOOR","SEDAN 4 DOOR")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Existing Damage Description","","test ppa")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Is the vehicle used in any commercial business operations?","false","")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Business Use Description","test business","")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Miles One-way to Work or School","","100")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).VIN Stub","5YMGY0C5&C","5NPEB4AC&C")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> uiFieldsToTDMapping = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap predefinedExpectedValues)
			//TODO should be deleted when Report tab is fixed
			.put("Driver Information (V1FirstName V1 V1LastName).First Name", "First Name")
			.put("Driver Information (V2FirstName V2 V2LastName).Middle Name", "Middle Name")
			.put("Driver Information (V1FirstName V1 V1LastName).Last Name", "Last Name")
			.put("Driver Information (V2FirstName V2 V2LastName).Suffix", "Suffix")
			.put("Driver Information (V1FirstName V1 V1LastName).Base Date", "Base Date")
			.put("Named Insured Information (V2FirstName V2 V2LastName).Prefix", "Prefix")
			.put("Named Insured Information (V1FirstName V1 V1LastName).First Name", "First Name")
			.put("Named Insured Information (V2FirstName V2 V2LastName).Middle Name", "Middle Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Last Name", "Last Name")
			.put("Named Insured Information (V2FirstName V2 V2LastName).Suffix", "Suffix")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Social Security Number", "Social Security Number")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Base Date", "Base Date")
			.put("Named Insured Information (V2FirstName V2 V2LastName).Move-In Date", "Move-In Date")
			.put("Named Insured Information (V2FirstName V2 V2LastName).Residence", "Residence")
			//Vehicle Information
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).VIN","VIN")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Existing Damage","Existing Damage")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Anti-theft","Anti-theft")
			.build();

}
