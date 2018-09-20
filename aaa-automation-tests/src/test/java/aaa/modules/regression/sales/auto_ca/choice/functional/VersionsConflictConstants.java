package aaa.modules.regression.sales.auto_ca.choice.functional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

public class VersionsConflictConstants {

	private static final String AVAILABLE = "Available";
	private static final String CURRENT = "Current";

	//Values that are not in Test Data, but are used in comparison
	static final Multimap<String, String> PREDEFINED_EXPECTED_VALUES = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			.putAll("Named Insured Information (VIFirstName VI VILastName).Residential Address", "VII residence address 1, VII residence address 2, Culver City, Los Angeles, CA, 90232", "VI residence address 1, VI residence address 2, HUNTINGTON PARK, Los Angeles, CA, 90255")
			.putAll("Named Insured Information (VIIFirstName VII VIILastName).Prior Address", "VI prior address 1, VI prior address 2, HUNTINGTON PARK, CA, 90255", "VII prior address 1, VII prior address 2, Culver City, CA, 90232")
			.putAll("Named Insured Information (VIFirstName VI VILastName).Mailing Address", "VII mailing address 1, VII mailing address 2, Culver City, Los Angeles, CA, 90232", "VI mailing address 1, VI mailing address 2, HUNTINGTON PARK, Los Angeles, CA, 90255")
			//AAA Products Owned
			.putAll("AAA Products Owned.Override Type","Term","Life")
			//Policy Information
			.putAll("Policy Information.Agency Location","400001105","")
			.putAll("Policy Information.Agency of Record","400001005","400000092")
			.putAll("Policy Information.Agent","400010108","400004092")
			.putAll("Policy Information.Agent of Record","400010108","400004092")
			.putAll("Policy Information.Agent Number","400010108","400004092")
			//Current Currier Information
			.putAll("Current Carrier Information.Months with Carrier","12","4")
			//Third Party
			.putAll("Policy Information.Third Party Designee Address","VII third party address 1, VII third party address 2, Culver City, CA, 90232","VI third party address 1, VI third party address 2, HUNTINGTON PARK, CA, 90255")
			//Driver Tab
			.putAll("Driver Information (NBFirstName NB NBLastName).Age", "18","55")
			.putAll("Driver Information (NBFirstName NB NBLastName).Permit Before License", "true","false")
			.putAll("Driver Information (NBFirstName NB NBLastName).Date First Licensed", "06/20/2018","07/20/2018")
			.putAll("Driver Information (NBFirstName NB NBLastName).Licensed in US/Canada for 18 or More Months?", "false","true")
			.putAll("Driver Information (NBFirstName NB NBLastName).Total Years Driving Experience", "3","38")
			.putAll("Driver Information (NBFirstName NB NBLastName).New Driver Course Completed", "true","false")
			.putAll("Driver Information (NBFirstName NB NBLastName).Mature driver course completed within 36 months", "false","true")
			.putAll("Driver Information (NBFirstName NB NBLastName).Good Driver Discount", "false","true")
			.putAll("Driver Information (NBFirstName NB NBLastName).Smoker: Cigarettes, cigars or pipes", "true","false")
			.putAll("Driver Information (NBFirstName NB NBLastName).Financial Responsibility Filling Needed", "false","true")
			//TODO return when Activity section is done
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Claim Points","","0")
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Violation points","0","")
			//.putAll("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Include in Points and/or Tier?","false","true")
			//Vehicle Information
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Year","1998","2003")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Make","DODG","MERZ")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Model","CARAVAN","SL500R")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Series","GRAND CARAVAN SE/SPORT","SL500R")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Body Style","SPORT VAN","ROADSTER")
			//TODO should be return to TD when True/False vs Yes/No is fixed
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged?", "true","false")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "","false")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Anti-theft", "NONE","STD")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Air Bags","000E","000K")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Alternative Fuel Vehicle","true","false")
			//Garaging address
			.putAll("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Is Garaging different from Residential?", "false", "true")
			.putAll("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Garaging Address", "NB residence address 1, NB residence address 2, Bell, CA, 90201","VII garaging address 1, VII garaging address 2, Culver City, CA, 90232")
			//Ownership address
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Ownership Type", "Financed","Owned")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).First Name", "LI011","")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Vehicle Ownership Address", "PO BOX 674, VII ownership address 2, Bell, CA, 55440-0674","")
			//Additional Interest Information
			.putAll("Additional Interest Information (First Name version2, Second Name version2).First Name", "First Name version1", "First Name version2")
			.putAll("Additional Interest Information (First Name version2, Second Name version2).Second Name", "Second Name version1", "Second Name version2")
			.putAll("Additional Interest Information (First Name version2, Second Name version2).Additional Interest Address", "interest address 1, VI interest address 2, Phoenix, AZ, 85085", "interest address 1, VII interest address 2, Red Rock, AZ, 85245")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> UI_FIELDS_TO_TD_MAPPING = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap PREDEFINED_EXPECTED_VALUES)
			.put("Driver Information (VIFirstName VI VILastName).First Name", "First Name")
			.put("Driver Information (VIFirstName VI VILastName).Middle Name", "Middle Name")
			.put("Driver Information (VIFirstName VI VILastName).Last Name", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName).Suffix", "Suffix")
			.put("Driver Information (VIFirstName VI VILastName).Base Date", "Base Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName).Title", "Title")
			.put("Named Insured Information (VIFirstName VI VILastName).First Name", "First Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Middle Name", "Middle Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Last Name", "Last Name")
			.put("Named Insured Information (VIFirstName VI VILastName).Suffix", "Suffix")
			.put("Named Insured Information (VIFirstName VI VILastName).Social Security Number", "Social Security Number")
			.put("Named Insured Information (VIFirstName VI VILastName).Base Date", "Base Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName).Move-In Date", "Move-In Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName).Residence", "Residence")
			//AAA Products Owned section
			.put("AAA Products Owned.Current AAA Member", "Current AAA Member")
			.put("AAA Products Owned.Membership Number", "Membership Number")
			//we verify it as predefined because during NB we have Term as default
			//.put("AAA Products Owned.Override Type", "Override Type")
			.put("AAA Products Owned.Policy #", "Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Life Policy #")
			.put("AAA Products Owned.Policy #", "Home Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Renters Policy #")
			.put("AAA Products Owned.Policy #", "Condo Policy #")
			//Contact Information
			.put("Contact Information.Home Phone Number", "Home Phone Number")
			.put("Contact Information.Work Phone Number", "Work Phone Number")
			.put("Contact Information.Mobile Phone Number", "Mobile Phone Number")
			.put("Contact Information.Preferred Phone #", "Preferred Phone #")
			.put("Contact Information.Email", "Email")
			//Current Carrier Information
			//TODO Should be return after implementation story - Clean up for Current Carrier
			//.put("Current Carrier Information.More than 6 months Total Insurance Experience","More than 6 months Total Insurance Experience")
			.put("Current Carrier Information.Agent Entered Current/Prior Carrier","Agent Entered Current/Prior Carrier")
			//TODO Should be return after implementation story - Clean up for Current Carrier
			//.put("Current Carrier Information.Agent Entered Other Carrier","Agent Entered Other Carrier")
			.put("Current Carrier Information.Agent Entered Inception Date","Agent Entered Inception Date")
			.put("Current Carrier Information.Agent Entered Expiration Date","Agent Entered Expiration Date")
			.put("Current Carrier Information.Agent Entered Policy Number","Agent Entered Policy Number")
			.put("Current Carrier Information.Days Lapsed","Days Lapsed")
			.put("Current Carrier Information.Months with Carrier","Months with Carrier")
			.put("Current Carrier Information.Agent Entered BI Limits","Agent Entered BI Limits")
			//Policy Information
			.put("Policy Information.Source of Business","Source of Business")
			.put("Policy Information.Source Policy #","Source Policy #")
			.put("Policy Information.Channel Type","Channel Type")
			.put("Policy Information.Agency","Agency")
			.put("Policy Information.Sales Channel","Sales Channel")
			//.put("Policy Information.Agent","Agent")
			.put("Policy Information.Authorized by","Authorized by")
			.put("Policy Information.Language Preference","Language Preference")
			.put("Policy Information.TollFree Number","TollFree Number")
			.put("Policy Information.Suppress Print","Suppress Print")
			//Third Party
			.put("Policy Information.Third Party Designee Name","Name")
			//Driver Information
			.put("Driver Information (NBFirstName NB NBLastName).Date of Birth", "Date of Birth")
			.put("Driver Information (NBFirstName NB NBLastName).Gender", "Gender")
			.put("Driver Information (NBFirstName NB NBLastName).Marital Status", "Marital Status")
			.put("Driver Information (NBFirstName NB NBLastName).Occupation", "Occupation")
			.put("Driver Information (NBFirstName NB NBLastName).Age First Licensed", "Age First Licensed")
			.put("Driver Information (NBFirstName NB NBLastName).License Type", "License Type")
			.put("Driver Information (NBFirstName NB NBLastName).First US/Canada License Date", "First US/Canada License Date")
			.put("Driver Information (NBFirstName NB NBLastName).License State", "License State")
			.put("Driver Information (NBFirstName NB NBLastName).License #", "License #")
			.put("Driver Information (NBFirstName NB NBLastName).Employee Benefit Type", "Employee Benefit Type")
			.put("Driver Information (NBFirstName NB NBLastName).Most Recent GPA", "Most Recent GPA")
			.put("Driver Information (NBFirstName NB NBLastName).New Driver Course Completion Date", "New Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName).New Driver Certification Number", "New Driver Certification Number")
			.put("Driver Information (NBFirstName NB NBLastName).Mature Driver Course Completion Date", "Mature Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName).Filling Type", "Filling Type")
			//TODO return when Activity section is done
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Type", "Type")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Description", "Description")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Occurrence Date", "Occurrence Date")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Loss Payment Amount", "Loss Payment Amount")
			//TODO should be returned after implementing story for Clean up for Activity Information
			//.put("Activity Information (Hit and Run, 05/10/2020, Not included in Rating).Include in Points and/or Tier?", "Include in Points and/or Tier?")
			//.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Not Included in Points and/or Tier - Reason Codes", "Not Included in Points and/or Tier - Reason Codes")
			//Vehicle Information
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Usage", "Usage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).VIN", "VIN")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Existing Damage", "Existing Damage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Existing Damage Description", "Existing Damage Description")
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged", "Salvaged")
						.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "Is the vehicle used in any commercial business operations?")*/
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Business Use Description", "Business Use Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Miles One-way to Work or School", "Miles One-way to Work or School")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Odometer Reading", "Odometer Reading")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Odometer Reading Date", "Odometer Reading Date")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Customer Declared Annual Miles", "Customer Declared Annual Miles")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Anti-theft Recovery Device", "Anti-theft Recovery Device")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Primary Use", "Primary Use")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> NAMED_INSURED_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("A A A Claims Report Order.First Name", CURRENT)
					.put("A A A Claims Report Order.Last Name", CURRENT)
					.put("AAAMvr Report Order.First Name",CURRENT)
					.put("AAAMvr Report Order.Last Name",CURRENT)
					.put("Driver Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Title", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Middle Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Last Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Suffix", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Base Date",CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residential Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Move-In Date", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Mailing Address", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> NAMED_INSURED_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("A A A Claims Report Order.First Name", CURRENT)
					.put("A A A Claims Report Order.Last Name", CURRENT)
					.put("AAAMvr Report Order.First Name",CURRENT)
					.put("AAAMvr Report Order.Last Name",CURRENT)
					.put("Driver Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Title", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Middle Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Last Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Suffix", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Base Date",CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residential Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Move-In Date", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Mailing Address", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> NAMED_INSURED_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Title")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Move-In Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Prior Address")
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> NAMED_INSURED_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.put("A A A Claims Report Order", "First Name")
			.put("AAAMvr Report Order", "First Name")
			//end
			.put("Driver Information (VIFirstName VI VILastName)", "First Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Middle Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Suffix")
			.put("Driver Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "First Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Middle Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Last Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Suffix")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Residential Address")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Mailing Address")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> AAA_PRODUCT_OWNED_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAAMembership Order.Member Since", CURRENT)
					.put("AAAMembership Order.Order Date", CURRENT)
					.put("AAAMembership Order.Receipt Date", CURRENT)
					.put("AAAMembership Order.Status", CURRENT)
					.put("AAAMembership Order.Membership Total Years", CURRENT)
					.put("AAAMembership Order.Total ERS Usage", CURRENT)
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.build());

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> AAA_PRODUCT_OWNED_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.build());


	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> AAA_PRODUCT_OWNED_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> AAA_PRODUCT_OWNED_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("AAAMembership Order", "Receipt Date")
			.put("AAA Products Owned", "Current AAA Member")
			.put("AAA Products Owned", "Membership Number")
			.put("AAA Products Owned", "Override Type")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> CONTACT_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAAPolicy Issue Summary.Insured Email", AVAILABLE)
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CONTACT_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Email")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CONTACT_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> CURRENT_CARRIER_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> CURRENT_CARRIER_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.build();

	protected static final ArrayListMultimap<String, String> POLICY_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Channel Type", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.put("Policy Information.Language Preference", AVAILABLE)
					.build());

	protected static final ArrayListMultimap<String, String> POLICY_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Agency", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.build());


	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> POLICY_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Authorized by")
			.put("Policy Information","TollFree Number")
			.put("Policy Information","Language Preference")
			.put("Policy Information","Suppress Print")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> POLICY_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Source of Business")
			.put("Policy Information","Source Policy #")
			.put("Policy Information","Channel Type")
			.put("Policy Information","Agency")
			.put("Policy Information","Agency of Record")
			.put("Policy Information","Agency Location")
			.put("Policy Information","Sales Channel")
			.put("Policy Information","Agent")
			.put("Policy Information","Agent of Record")
			.put("Policy Information","Agent Number")
			.build();


	protected static final ArrayListMultimap<String, String> THIRD_PARTY_DESIGNEE = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Third Party Designee Name", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> THIRD_PARTY_DESIGNEE_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> THIRD_PARTY_DESIGNEE_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Third Party Designee Name")
			.put("Policy Information","Third Party Designee Address")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> DRIVER_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("A A A Claims Report Order.Date of Birth", AVAILABLE)
					.put("A A A Claims Report Order.License Number", AVAILABLE)
					.put("A A A Claims Report Order.Gender", AVAILABLE)
					.put("CIPCS22Endorsement Form", AVAILABLE)
					.put("AAAMvr Report Order.Date of Birth", AVAILABLE)
					.put("AAAMvr Report Order.License Number", AVAILABLE)
					.put("AAAMvr Report Order.Gender", AVAILABLE)
					//TODO return when Activity section is done
					/*					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Type", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Description", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Occurrence Date", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Loss Payment Amount", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Claim Points", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Violation points", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Include in Points and/or Tier?", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Not Included in Points and/or Tier - Reason Codes", AVAILABLE)*/
					.put("Driver Information (NBFirstName NB NBLastName).Date of Birth", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> DRIVER_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					//TODO return when Activity section is done
					.put("A A A Claims Report Order.Date of Birth", AVAILABLE)
					.put("A A A Claims Report Order.License Number", AVAILABLE)
					.put("AAAMvr Report Order.Date of Birth", AVAILABLE)
					.put("AAAMvr Report Order.License Number", AVAILABLE)
					/*					.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Type", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Description", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Occurrence Date", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Loss Payment Amount", AVAILABLE)
										.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating).Not Included in Points and/or Tier - Reason Codes", AVAILABLE)*/
					.put("Driver Information (NBFirstName NB NBLastName).Date of Birth", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> DRIVER_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("A A A Claims Report Order", "Automobile Death Benefit")
			.put("AAAMvr Report Order", "Automobile Death Benefit")
			.put("CIPCS22Endorsement Form", "Automobile Death Benefit")
			//TODO return when Activity section is done
			/*			.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Type")
						.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Description")
						.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Occurrence Date")
						.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Loss Payment Amount")
						.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Claim Points")
						.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Violation points")
						.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Include in Points and/or Tier?")
						.put("Activity Information (Comprehensive Claim, 06/20/2018, Included in Rating)", "Not Included in Points and/or Tier - Reason Codes")*/
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> DRIVER_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Driver Information (NBFirstName NB NBLastName)", "Date of Birth")
			.put("Driver Information (NBFirstName NB NBLastName)", "Age")
			.put("Driver Information (NBFirstName NB NBLastName)", "Gender")
			.put("Driver Information (NBFirstName NB NBLastName)", "Marital Status")
			.put("Driver Information (NBFirstName NB NBLastName)", "Occupation")
			.put("Driver Information (NBFirstName NB NBLastName)", "Age First Licensed")
			.put("Driver Information (NBFirstName NB NBLastName)", "Permit Before License")
			.put("Driver Information (NBFirstName NB NBLastName)", "Total Years Driving Experience")
			.put("Driver Information (NBFirstName NB NBLastName)", "Date First Licensed")
			.put("Driver Information (NBFirstName NB NBLastName)", "Licensed in US/Canada for 18 or More Months?")
			.put("Driver Information (NBFirstName NB NBLastName)", "First US/Canada License Date")
			.put("Driver Information (NBFirstName NB NBLastName)", "License #")
			.put("Driver Information (NBFirstName NB NBLastName)", "Employee Benefit Type")
			.put("Driver Information (NBFirstName NB NBLastName)", "Most Recent GPA")
			.put("Driver Information (NBFirstName NB NBLastName)", "Good Driver Discount")
			.put("Driver Information (NBFirstName NB NBLastName)", "New Driver Course Completed")
			.put("Driver Information (NBFirstName NB NBLastName)", "New Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName)", "New Driver Certification Number")
			.put("Driver Information (NBFirstName NB NBLastName)", "Mature driver course completed within 36 months")
			.put("Driver Information (NBFirstName NB NBLastName)", "Mature Driver Course Completion Date")
			.put("Driver Information (NBFirstName NB NBLastName)", "Smoker: Cigarettes, cigars or pipes")
			.put("Driver Information (NBFirstName NB NBLastName)", "Financial Responsibility Filling Needed")
			.put("Driver Information (NBFirstName NB NBLastName)", "Filling Type")
			.build();


	protected static final ArrayListMultimap<String, String> VEHICLE_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).VIN", CURRENT)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Garaging Address", AVAILABLE)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Ownership Type", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> VEHICLE_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).VIN", CURRENT)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Garaging Address", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> VEHICLE_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN)", "Is Garaging different from Residential?")
			.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN)", "Garaging Address")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> VEHICLE_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "VIN")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Year")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Make")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Model")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Series")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Body Style")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Primary Use")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Is the vehicle used in any commercial business operations?")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Business Use Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Air Bags")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Anti-theft")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Alternative Fuel Vehicle")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Anti-theft Recovery Device")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Salvaged?")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Miles One-way to Work or School")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Odometer Reading")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Odometer Reading Date")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Customer Declared Annual Miles")
			//Ownership address
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Ownership Type")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "First Name")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Vehicle Ownership Address")
			.build();
}