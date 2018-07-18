package aaa.modules.regression.sales.auto_ss.functional;

import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class TestVersionsComparisonConstants {

	//fields that have DB values, but not UI values on Comparison page
	static final Map<String, String> comparisonPageDifferentValues = ImmutableMap.<String, String>builder()
			//.put("Own Home", "OWNHME")
			.build();

	//TODO refactor it by taking values from UI
	//values that we don't have in Test Date, but that are used in comparison
	static final Multimap<String, String> predefinedExpectedValues = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.putAll("AAA Claims Report Order.First Name", "V2FirstName", "V1FirstName")
			.putAll("AAA Claims Report Order.Last Name", "V2LastName", "V1LastName")
			.putAll("AAAMvr Report Order.Order Date", "", "07/11/2018")
			.putAll("AAAMvr Report Order.Receipt Date", "", "07/11/2018")
			.putAll("AAAMvr Report Order.Status", "", "Clear")
			.putAll("AAAMvr Report Order.First Name", "V2FirstName", "V1FirstName")
			.putAll("AAAMvr Report Order.Last Name", "V2LastName", "V1LastName")
			.putAll("AAAMvr Report Order.ChoicePoint License Status", "", "1 VALID")
			//end
			.putAll("Named Insured Information (V1FirstName V1 V1LastName).Residential Address", "V2 residence address 1, V2 residence address 2, Red Rock, AZ, 85245", "V1 residence address 1, V1 residence address 2, Phoenix, AZ, 85085")
			.putAll("Named Insured Information (V1FirstName V1 V1LastName).Prior Address", "V2 prior address 1, V2 prior address 2, Red Rock, AZ, 85245", "V1 prior address 1, V1 prior address 2, Phoenix, AZ, 85085")
			.putAll("Named Insured Information (V1FirstName V1 V1LastName).Mailing Address", "V2 mailing address 1, V2 mailing address 2, Red Rock, AZ, 85245", "V1 mailing address 1, V1 mailing address 2, Phoenix, AZ, 85085")
			.putAll("Vehicle Information (2011, CHEVROLET, EXPRESS VAN, SPORT VAN).Garaging Address", "V2 residence address 1, V2 residence address 2, Red Rock, AZ, 85245", "V1 residence address 1, V1 residence address 2, Phoenix, AZ, 85085")
			//AAA Product Owned
			.putAll("AAA Membership Order.Member Since", "2005-01-01", "")
			.putAll("AAA Membership Order.Order Date", "2018-10-11", "")
			.putAll("AAA Membership Order.Receipt Date", "2018-10-11", "")
			.putAll("AAA Membership Order.Status", "Active", "")
			.putAll("AAA Membership Order.Membership Total Years", "3", "")
			.putAll("AAA Membership Order.Last Name", "Smith", "")
			.putAll("AAA Membership Order.Membership Number", "4290023667710001", "")
			.putAll("AAA Products Owned.Current AAA Member", "Yes", "Override")
			.putAll("AAA Products Owned.Membership Number", "4290023667710001", "")
			.putAll("AAA Products Owned.Membership Number", "Smith", "")
			//Vehicle Information
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Usage","Business","WorkCommute")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Make","BMW","HYUNDAI")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Model","X5","SONATA")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Series","X5 M","SONATA GLS")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Body Style","WAGON 4 DOOR","SEDAN 4 DOOR")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Existing Damage Description","","test ppa")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Is the vehicle used in any commercial business operations?","false","")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Business Use Description","test business","")
			.putAll("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).VIN Stub","5YMGY0C5&C","5NPEB4AC&C")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> attributesToTDMapping = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap predefinedExpectedValues)
			//TODO should be deleted when Report tab is fixed
			.put("Driver Information (V1FirstName V1 V1LastName).First Name", "First Name")
			.put("Driver Information (V1FirstName V1 V1LastName).Middle Name", "Middle Name")
			.put("Driver Information (V1FirstName V1 V1LastName).Last Name", "Last Name")
			.put("Driver Information (V1FirstName V1 V1LastName).Suffix", "Suffix")
			.put("Driver Information (V1FirstName V1 V1LastName).Base Date", "Base Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Prefix", "Prefix")
			.put("Named Insured Information (V1FirstName V1 V1LastName).First Name", "First Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Middle Name", "Middle Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Last Name", "Last Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Suffix", "Suffix")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Social Security Number", "Social Security Number")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Base Date", "Base Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Move-In Date", "Move-In Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName).Residence", "Residence")
			//AAA Product Owned
			.put("AAA Products Owned.Policy #", "Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Life Policy #")
			.put("AAA Products Owned.Policy #", "Home Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Renters Policy #")
			.put("AAA Products Owned.Policy #", "Condo Policy #")
			.put("AAA Products Owned.Policy #", "PUP Motorcycle Policy #")
			//Vehicle Information
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).VIN","VIN")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Existing Damage","Existing Damage")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR).Anti-theft","Anti-theft")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for data gather comparison
	static final Multimap<String, String> dataGatherNamedInsuredInformation = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.put("AAA Claims Report Order", "First Name")
			.put("AAA Claims Report Order", "Last Name")
			.put("AAAMvr Report Order", "Order Date")
			.put("AAAMvr Report Order", "Receipt Date")
			.put("AAAMvr Report Order", "Status")
			.put("AAAMvr Report Order", "First Name")
			.put("AAAMvr Report Order", "Last Name")
			.put("AAAMvr Report Order", "ChoicePoint License Status")
			//end
			.put("Driver Information (V1FirstName V1 V1LastName)", "First Name")
			.put("Driver Information (V1FirstName V1 V1LastName)", "Middle Name")
			.put("Driver Information (V1FirstName V1 V1LastName)", "Last Name")
			.put("Driver Information (V1FirstName V1 V1LastName)", "Suffix")
			.put("Driver Information (V1FirstName V1 V1LastName)", "Base Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Prefix")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "First Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Middle Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Last Name")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Suffix")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Social Security Number")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Base Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Residential Address")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Move-In Date")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Prior Address")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Mailing Address")
			.put("Named Insured Information (V1FirstName V1 V1LastName)", "Residence")
			.put("Vehicle Information (2011, CHEVROLET, EXPRESS VAN, SPORT VAN)", "Garaging Address")
			.build();

	//all components/attributes that should be on Comparison page for Named Insured Information section for endorsement/renewal comparison
	public static final Multimap<String, String> endorsementRenewalNamedInsuredInformation;
	static {
		Multimap<String, String> endorsementModified = ArrayListMultimap.create(dataGatherNamedInsuredInformation);
		endorsementModified.remove("AAAMvr Report Order", "Order Date");
		endorsementModified.remove("AAAMvr Report Order", "Receipt Date");
		endorsementModified.remove("AAAMvr Report Order", "Status");
		endorsementModified.remove("AAAMvr Report Order", "ChoicePoint License Status");
		endorsementRenewalNamedInsuredInformation = ImmutableListMultimap.copyOf(endorsementModified);
	}

	//all components/attributes that should be on Comparison page for AAA Product Owned section
	static final Multimap<String, String> aaaProductOwned = ImmutableListMultimap.<String, String>builder()
			.put("AAA Membership Order", "Last Name")
			.put("AAA Membership Order", "Membership Number")
			.put("AAA Membership Order", "Member Since")
			.put("AAA Membership Order", "Order Date")
			.put("AAA Membership Order", "Receipt Date")
			.put("AAA Membership Order", "Status")
			.put("AAA Membership Order", "Membership Total Years")
			.put("AAA Products Owned", "Current AAA Member")
			.put("AAA Products Owned", "Membership Number")
			.put("AAA Products Owned", "Last name")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.build();

	//all components/attributes that should be on Comparison page for Vehicle Information section
	static final Multimap<String, String> vehicleInformation = ImmutableListMultimap.<String, String>builder()
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
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "Anti-theft")
			.put("Vehicle Information (2012, HYUNDAI, SONATA, SEDAN 4 DOOR)", "VIN Stub")
			.build();

}