package aaa.modules.regression.sales.auto_ca.select.functional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

public class VersionsConflictConstants {

	private static final String AVAILABLE = "Available";
	private static final String CURRENT = "Current";

	public static final String SELECT_DVR_QUERY = "select dvr.relationshiptype, dvr.vehicleoid, dvr.driveroid\n" +
			"from policysummary ps join DriverVehicleRelationship dvr on ps.policydetail_id = dvr.policydetail_id \n" +
			"where ps.policynumber = '%1$s' and ps.revisionno = '4'";

	public static final String SELECT_VEHICLE_OID_QUERY = "select bi.manufacturer || ' ' || bi.model || ' ' || bi.modelyear as vehicle, ri.oid, ri.primaryDriverName, ri.primaryDriverOid, ri.manuallyAssignedDriverName, ri.assignedDriverOID  \n" +
			"from policysummary ps join riskitem ri on ri.policydetail_id = ps.policydetail_id\n" +
			"join VehicleBaseInfo bi on ri.baseinfo_id = bi.id where ps.policynumber = '%1$s' and ps.revisionno = '4'";

	public static final String SELECT_DRIVER_OID_QUERY = "select d.firstname || ' ' || d.lastname as name, d.oid from policysummary ps \n" +
			"join driver d on ps.policydetail_id = d.policydetail_id \n" +
			"where ps.policynumber = '%1$s' and ps.revisionno = '4'";

	//Values that are not in Test Data, but are used in comparison
	static final Multimap<String, String> PREDEFINED_EXPECTED_VALUES = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Residential Address", "VII residence address 1, VII residence address 2, Culver City, Los Angeles, CA, 90232", "VI residence address 1, VI residence address 2, HUNTINGTON PARK, Los Angeles, CA, 90255")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Prior Address", "VI prior address 1, VI prior address 2, HUNTINGTON PARK, CA, 90255", "VII prior address 1, VII prior address 2, Culver City, CA, 90232")
			.putAll("Named Insureds.VIIFirstName VII VIILastName.Mailing Address", "VII mailing address 1, VII mailing address 2, Culver City, Los Angeles, CA, 90232", "VI mailing address 1, VI mailing address 2, HUNTINGTON PARK, Los Angeles, CA, 90255")
			//AAA Products Owned
			.putAll("AAA Products Owned.Override Type","Term","Life")
			.putAll("Reports.AAA Membership report.Order Date", "","")
			.putAll("Reports.AAA Membership report.Receipt Date", "", "")
			.putAll("Reports.AAA Membership report.Status", "Active","")
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
			//data gather data
			.putAll("Drivers.VIFirstName VI VILastName.Age", "18","55")
			.putAll("Drivers.VIFirstName VI VILastName.Permit Before License", "true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Date First Licensed", "06/20/2018","07/20/2018")
			.putAll("Drivers.VIFirstName VI VILastName.Licensed in US/Canada for 18 or More Months?", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Total Years Driving Experience", "3","38")
			.putAll("Drivers.VIFirstName VI VILastName.New Driver Course Completed", "true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Mature driver course completed within 36 months", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.ADB Coverage", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Good Driver Discount", "false","true")
			.putAll("Drivers.VIFirstName VI VILastName.Smoker: Cigarettes, cigars or pipes", "true","false")
			.putAll("Drivers.VIFirstName VI VILastName.Financial Responsibility Filling Needed", "false","true")
			//endorsement/renewal data
			.putAll("Drivers.NBFirstName NB NBLastName.Age", "18","55")
			.putAll("Drivers.NBFirstName NB NBLastName.Permit Before License", "true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Date First Licensed", "06/20/2018","07/20/2018")
			.putAll("Drivers.NBFirstName NB NBLastName.Licensed in US/Canada for 18 or More Months?", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Total Years Driving Experience", "3","38")
			.putAll("Drivers.NBFirstName NB NBLastName.New Driver Course Completed", "true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Mature driver course completed within 36 months", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.ADB Coverage", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Good Driver Discount", "false","true")
			.putAll("Drivers.NBFirstName NB NBLastName.Smoker: Cigarettes, cigars or pipes", "true","false")
			.putAll("Drivers.NBFirstName NB NBLastName.Financial Responsibility Filling Needed", "false","true")
			//Vehicle Information
			.putAll("Vehicles.1998, DODGE, CARAVAN.Year","1998","2003")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Make","DODG","MERZ")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Model","CARAVAN","SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Series","GRAND CARAVAN SE/SPORT","SL500R")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Body Style","SPORT VAN","ROADSTER")
			.putAll("Vehicles.1998, DODGE, CARAVAN.BI Symbol", "X","D")
			.putAll("Vehicles.1998, DODGE, CARAVAN.PD Symbol", "X","D")
			.putAll("Vehicles.1998, DODGE, CARAVAN.UM Symbol", "X","D")
			.putAll("Vehicles.1998, DODGE, CARAVAN.MP Symbol", "X","D")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Comp Symbol", "12","60")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Coll Symbol", "12","60")
			//TODO should be return to TD when True/False vs Yes/No is fixed
			.putAll("Vehicles.1998, DODGE, CARAVAN.Salvaged?", "true","false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is the vehicle used in any commercial business operations?", "","false")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Anti-theft", "NONE","STD")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Air Bags","000E","000K")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Alternative Fuel Vehicle","true","false")
			//Garaging address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Is Garaging different from Residential?", "false", "true")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Garaging Address", "NB residence address 1, NB residence address 2, Bell, CA, 90201","VII garaging address 1, VII garaging address 2, Culver City, CA, 90232")
			//Usage Based Insurance
			.putAll("Vehicles.1998, DODGE, CARAVAN.Enroll in Usage Based Insurance?", "false","true")
			//Ownership address
			.putAll("Vehicles.1998, DODGE, CARAVAN.Ownership Type", "Financed","Owned")
			.putAll("Vehicles.1998, DODGE, CARAVAN.First Name", "LI011","")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Vehicle Ownership Address", "PO BOX 674, MINNEAPOLIS, MN, 55440-0674","")
			//Additional Interest Information
			.putAll("Additional Interest Information (First Name version2, Second Name version2).First Name", "First Name version1", "First Name version2")
			.putAll("Additional Interest Information (First Name version2, Second Name version2).Second Name", "Second Name version1", "Second Name version2")
			.putAll("Additional Interest Information (First Name version2, Second Name version2).Additional Interest Address", "interest address 1, VI interest address 2, Phoenix, AZ, 85085", "interest address 1, VII interest address 2, Red Rock, AZ, 85245")
			//Add Components
			.putAll("Named Insureds.Insured Principal (VIFirstName VI VILastName)","Insured Principal (VIFirstName VI VILastName)","")
			.putAll("Named Insureds.Insured Principal (VIIFirstName VII VIILastName)","Insured Principal (VIIFirstName VII VIILastName)","")
			.putAll("Drivers.Driver (New Driver Version1)","Driver (New Driver Version1)","")
			.putAll("Drivers.Driver (New Driver Version2)","Driver (New Driver Version2)","")
			.putAll("Named Insureds.NBFirstName NB NBLastName.AAA Clue Order","AAA Clue Order","")
			.putAll("Vehicles.Vehicle (1998, DODGE, CARAVAN)","Vehicle (1998, DODGE, CARAVAN)","")
			.putAll("Vehicles.Vehicle (2003, MERCEDES-BENZ, SL500R)","Vehicle (2003, MERCEDES-BENZ, SL500R)","")
			.putAll("Drivers.NBFirstName NB NBLastName.Driving Activities.Activity Information (Accident (Property Damage Only), 05/25/2018, Not included in Rating)","Activity Information (Accident (Property Damage Only), 05/25/2018, Not included in Rating)","")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.Are there any additional interest(s)?", "true", "false")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.First Name", "First Name version1", "")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.Second Name", "Second Name version1", "")
			.putAll("Vehicles.2008, ACURA, MDX.Additional Interest.Additional Interest Information.Additional Interest Address", "interest address 1, VI interest address 2, Phoenix, AZ, 85085","")
			.putAll("Named Insureds.NBFirstName NB NBLastName.AAA Clue Order.Order Date","","")
			.putAll("Named Insureds.NBFirstName NB NBLastName.AAA Clue Order.Receipt Date","","")
			//Assignment Tab Information
			.putAll("Vehicles.1998, DODGE, CARAVAN.System Rated Driver", "New Driver Version1","New Driver Version2")
			.putAll("Vehicles.1998, DODGE, CARAVAN.Manually Rated Driver", "New Driver Version1","New Driver Version2")
			.putAll("Drivers.Driver (Third Driver Version2)","","Driver (Third Driver Version2)")
			.putAll("Drivers.Driver (Second Driver Version1)","","Driver (Second Driver Version1)")
			.putAll("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)","","Vehicle (2011, CHEVROLET, EXPRESS VAN)")
			.putAll("Vehicles.Vehicle (2011, MERCEDES-BENZ, G55AMG)","","Vehicle (2011, MERCEDES-BENZ, G55AMG)")
			.putAll("Named Insureds.Insured Principal (Second VI Insured)","","Insured Principal (Second VI Insured)")
			.putAll("Vehicles.2008, ACURA, MDX.System Rated Driver", "NBFirstName NBLastName", "Second Driver Version1")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> UI_FIELDS_TO_TD_MAPPING = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap PREDEFINED_EXPECTED_VALUES)
			.put("Drivers.VIIFirstName VII VIILastName.First Name", "First Name")
			.put("Drivers.VIIFirstName VII VIILastName.Middle Name", "Middle Name")
			.put("Drivers.VIIFirstName VII VIILastName.Last Name", "Last Name")
			.put("Drivers.VIIFirstName VII VIILastName.Suffix", "Suffix")
			.put("Drivers.VIIFirstName VII VIILastName.Base Date", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName.Title", "Title")
			.put("Named Insureds.VIIFirstName VII VIILastName.First Name", "First Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Middle Name", "Middle Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Last Name", "Last Name")
			.put("Named Insureds.VIIFirstName VII VIILastName.Suffix", "Suffix")
			.put("Named Insureds.VIIFirstName VII VIILastName.Social Security Number", "Social Security Number")
			.put("Named Insureds.VIIFirstName VII VIILastName.Base Date", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName.Move-In Date", "Move-In Date")
			.put("Named Insureds.VIIFirstName VII VIILastName.Residence", "Residence")
			//AAA Products Owned section
			.put("AAA Products Owned.Current AAA Member", "Current AAA Member")
			.put("AAA Products Owned.Membership Number", "Membership Number")
			//we verify it as predefined because during NB we have Term as default
			//.put("AAA Products Owned.Override Type", "Override Type")
			.put("AAA Products Owned.Member Since Date", "Member Since Date")
			.put("AAA Products Owned.Motorcycle Policy #", "Motorcycle Policy #")
			.put("AAA Products Owned.Life Policy #", "Life Policy #")
			.put("AAA Products Owned.Home Policy #", "Home Motorcycle Policy #")
			.put("AAA Products Owned.Renters Policy #", "Renters Policy #")
			.put("AAA Products Owned.Condo Policy #", "Condo Policy #")
			.put("AAA Products Owned.PUP Policy #", "PUP Motorcycle Policy #")
			.put("Reports.AAA Membership report.Membership No.", "Membership Number")
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
			.put("Drivers.VIFirstName VI VILastName.Date of Birth", "Date of Birth")
			.put("Drivers.VIFirstName VI VILastName.Gender", "Gender")
			.put("Drivers.VIFirstName VI VILastName.Marital Status", "Marital Status")
			.put("Drivers.VIFirstName VI VILastName.Occupation", "Occupation")
			.put("Drivers.VIFirstName VI VILastName.Age First Licensed", "Age First Licensed")
			.put("Drivers.VIFirstName VI VILastName.License Type", "License Type")
			.put("Drivers.VIFirstName VI VILastName.First US/Canada License Date", "First US/Canada License Date")
			.put("Drivers.VIFirstName VI VILastName.License State", "License State")
			.put("Drivers.VIFirstName VI VILastName.License #", "License #")
			.put("Drivers.VIFirstName VI VILastName.Employee Benefit Type", "Employee Benefit Type")
			.put("Drivers.VIFirstName VI VILastName.Most Recent GPA", "Most Recent GPA")
			.put("Drivers.VIFirstName VI VILastName.New Driver Course Completion Date", "New Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName.New Driver Certification Number", "New Driver Certification Number")
			.put("Drivers.VIFirstName VI VILastName.Mature Driver Course Completion Date", "Mature Driver Course Completion Date")
			.put("Drivers.VIFirstName VI VILastName.Filling Type", "Filling Type")
			//endorsement/renewal data
			.put("Drivers.NBFirstName NB NBLastName.Date of Birth", "Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName.Gender", "Gender")
			.put("Drivers.NBFirstName NB NBLastName.Marital Status", "Marital Status")
			.put("Drivers.NBFirstName NB NBLastName.Occupation", "Occupation")
			.put("Drivers.NBFirstName NB NBLastName.Age First Licensed", "Age First Licensed")
			.put("Drivers.NBFirstName NB NBLastName.License Type", "License Type")
			.put("Drivers.NBFirstName NB NBLastName.First US/Canada License Date", "First US/Canada License Date")
			.put("Drivers.NBFirstName NB NBLastName.License State", "License State")
			.put("Drivers.NBFirstName NB NBLastName.License #", "License #")
			.put("Drivers.NBFirstName NB NBLastName.Employee Benefit Type", "Employee Benefit Type")
			.put("Drivers.NBFirstName NB NBLastName.Most Recent GPA", "Most Recent GPA")
			.put("Drivers.NBFirstName NB NBLastName.New Driver Course Completion Date", "New Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName.New Driver Certification Number", "New Driver Certification Number")
			.put("Drivers.NBFirstName NB NBLastName.Mature Driver Course Completion Date", "Mature Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName.Filling Type", "Filling Type")
			//Vehicle Information
			.put("Vehicles.1998, DODGE, CARAVAN.Usage", "Usage")
			.put("Vehicles.1998, DODGE, CARAVAN.VIN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage", "Existing Damage")
			.put("Vehicles.1998, DODGE, CARAVAN.Existing Damage Description", "Existing Damage Description")
			//TODO should be returned when Yes/NO vs true/false is fixed
			/*			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged", "Salvaged")
						.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "Is the vehicle used in any commercial business operations?")*/
			.put("Vehicles.1998, DODGE, CARAVAN.Business Use Description", "Business Use Description")
			.put("Vehicles.1998, DODGE, CARAVAN.Miles One-way to Work or School", "Miles One-way to Work or School")
			.put("Vehicles.1998, DODGE, CARAVAN.Odometer Reading", "Odometer Reading")
			.put("Vehicles.1998, DODGE, CARAVAN.Odometer Reading Date", "Odometer Reading Date")
			.put("Vehicles.1998, DODGE, CARAVAN.Customer Declared Annual Miles", "Customer Declared Annual Miles")
			.put("Vehicles.1998, DODGE, CARAVAN.Anti-theft Recovery Device", "Anti-theft Recovery Device")
			.put("Vehicles.1998, DODGE, CARAVAN.Primary Use", "Primary Use")
			//Assignment
			.put("Vehicles.1998, DODGE, CARAVAN.Primary Driver", "Primary Driver")
			.put("Vehicles.2008, ACURA, MDX.Primary Driver", "Primary Driver")
			.put("Vehicles.2008, ACURA, MDX.Manually Rated Driver", "Manually Rated Driver")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> NAMED_INSURED_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Drivers.VIIFirstName VII VIILastName.Reports.A A A Claims Report Order.First Name", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.A A A Claims Report Order.Last Name", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAAMvr Report Order.First Name",CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAAMvr Report Order.Last Name",CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.A A A Claims Report Order.Name on License", CURRENT)
					.put("Drivers.VIIFirstName VII VIILastName.Reports.AAAMvr Report Order.Name on License",CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Title", AVAILABLE)
					.put("Named Insureds.VIIFirstName VII VIILastName.First Name", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Base Date",CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Residential Address", CURRENT)
					.put("Named Insureds.VIIFirstName VII VIILastName.Move-In Date", AVAILABLE)
					.put("Named Insureds.VIIFirstName VII VIILastName.Mailing Address", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> NAMED_INSURED_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Named Insureds.VIIFirstName VII VIILastName", "Title")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Move-In Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Prior Address")
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> NAMED_INSURED_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers.VIIFirstName VII VIILastName", "First Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Middle Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Last Name")
			.put("Drivers.VIIFirstName VII VIILastName", "Suffix")
			.put("Drivers.VIIFirstName VII VIILastName", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "First Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Middle Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Last Name")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Suffix")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Base Date")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Residential Address")
			.put("Named Insureds.VIIFirstName VII VIILastName", "Mailing Address")
			.put("Vehicles.2008, ACURA, MDX", "Primary Driver")
			.put("Vehicles.2008, ACURA, MDX", "System Rated Driver")
			.build();

	//Maps to select added Named insureds in endorsement and OOSE
	protected static final ArrayListMultimap<String, String> ADD_NAMED_INSURED_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Named Insureds.Insured Principal (VIFirstName VI VILastName)", AVAILABLE)
					.put("Named Insureds.Insured Principal (VIIFirstName VII VIILastName)", CURRENT)
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_NAMED_INSURED_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Email")
			.put("Named Insureds", "Insured Principal (VIFirstName VI VILastName)")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_NAMED_INSURED_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Named Insureds","Insured Principal (VIIFirstName VII VIILastName)")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> AAA_PRODUCT_OWNED_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Motorcycle Policy #", AVAILABLE)
					.put("AAA Products Owned.Life Policy #", CURRENT)
					.put("AAA Products Owned.Home Policy #", AVAILABLE)
					.put("AAA Products Owned.Renters Policy #", CURRENT)
					.put("AAA Products Owned.Condo Policy #", AVAILABLE)
					.put("AAA Products Owned.PUP Policy #", CURRENT)
					.build());

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> AAA_PRODUCT_OWNED_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Motorcycle Policy #", AVAILABLE)
					.put("AAA Products Owned.Life Policy #", CURRENT)
					.put("AAA Products Owned.Home Policy #", AVAILABLE)
					.put("AAA Products Owned.Renters Policy #", CURRENT)
					.put("AAA Products Owned.Condo Policy #", AVAILABLE)
					.put("AAA Products Owned.PUP Policy #", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> AAA_PRODUCT_OWNED_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Motorcycle Policy #")
			.put("AAA Products Owned", "Home Policy #")
			.put("AAA Products Owned", "Condo Policy #")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> AAA_PRODUCT_OWNED_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Reports.AAA Membership report", "Membership No.")
			.put("Reports.AAA Membership report", "Order Date")
			.put("Reports.AAA Membership report", "Receipt Date")
			.put("Reports.AAA Membership report", "Status")
			.put("AAA Products Owned", "Current AAA Member")
			.put("AAA Products Owned", "Membership Number")
			.put("AAA Products Owned", "Override Type")
			.put("AAA Products Owned", "Life Policy #")
			.put("AAA Products Owned", "Renters Policy #")
			.put("AAA Products Owned", "PUP Policy #")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> CONTACT_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
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
					.put("Current Carrier Information.Home Phone Number", CURRENT)
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
					.put("Policy Information.Channel Type", CURRENT)
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
					.put("CSAAEEndorsement Form", AVAILABLE)
					.put("CSAAEEndorsement Form", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Date of Birth", CURRENT)
					.put("Drivers.NBFirstName NB NBLastName.Forms.ADBEndorsement Form", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Forms.ADBEndorsement Form", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Forms.ADBEndorsement Form", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Forms.CIPCS22Endorsement Form", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Forms.CIPCS22Endorsement Form", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Forms.CIPCS22Endorsement Form", AVAILABLE)
					.put("Forms.CSAAEEndorsement Form", CURRENT)
					.put("Forms.CSAAEEndorsement Form", CURRENT)
					.put("Forms.ENOCCEndorsement Form", AVAILABLE)
					.put("Forms.ENOCCEndorsement Form", AVAILABLE)
					.put("Forms.ENOCCEndorsement Form", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.A A A Claims Report Order.Date of Birth", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.A A A Claims Report Order.License Number", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.A A A Claims Report Order.License State", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.A A A Claims Report Order.Gender", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.Date of Birth", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.License Number", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.License State", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.Gender", AVAILABLE)
					.build());

	protected static final ArrayListMultimap<String, String> DRIVER_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Drivers.NBFirstName NB NBLastName.Reports.A A A Claims Report Order.Date of Birth", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.A A A Claims Report Order.License Number", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.A A A Claims Report Order.License State", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.Date of Birth", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.License Number", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Reports.AAAMvr Report Order.License State", AVAILABLE)
					.put("Drivers.NBFirstName NB NBLastName.Date of Birth", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> DRIVER_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> DRIVER_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers.NBFirstName NB NBLastName", "Date of Birth")
			.put("Drivers.NBFirstName NB NBLastName", "Age")
			.put("Drivers.NBFirstName NB NBLastName", "Gender")
			.put("Drivers.NBFirstName NB NBLastName", "Marital Status")
			.put("Drivers.NBFirstName NB NBLastName", "Occupation")
			.put("Drivers.NBFirstName NB NBLastName", "Age First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Permit Before License")
			.put("Drivers.NBFirstName NB NBLastName", "Total Years Driving Experience")
			.put("Drivers.NBFirstName NB NBLastName", "Date First Licensed")
			.put("Drivers.NBFirstName NB NBLastName", "Licensed in US/Canada for 18 or More Months?")
			.put("Drivers.NBFirstName NB NBLastName", "First US/Canada License Date")
			.put("Drivers.NBFirstName NB NBLastName", "License State")
			.put("Drivers.NBFirstName NB NBLastName", "License #")
			.put("Drivers.NBFirstName NB NBLastName", "Employee Benefit Type")
			.put("Drivers.NBFirstName NB NBLastName", "ADB Coverage")
			.put("Drivers.NBFirstName NB NBLastName", "Most Recent GPA")
			.put("Drivers.NBFirstName NB NBLastName", "Good Driver Discount")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Course Completed")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "New Driver Certification Number")
			.put("Drivers.NBFirstName NB NBLastName", "Mature driver course completed within 36 months")
			.put("Drivers.NBFirstName NB NBLastName", "Mature Driver Course Completion Date")
			.put("Drivers.NBFirstName NB NBLastName", "Smoker: Cigarettes, cigars or pipes")
			.put("Drivers.NBFirstName NB NBLastName", "Financial Responsibility Filling Needed")
			.put("Drivers.NBFirstName NB NBLastName", "Filling Type")
			.build();

	protected static final ArrayListMultimap<String, String> ADD_DRIVER_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.put("Reports.AAA Clue Order.Order Date", CURRENT)
					.put("Reports.AAA Clue Order.Receipt Date", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_DRIVER_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Drivers", "Driver (New Driver Version2)")
			.put("Reports", "AAA Clue Order")
			.put("Reports.AAA Clue Order","Order Date")
			.put("Reports.AAA Clue Order","Receipt Date")
			.put("Reports.AAA Clue Order","Select")
			.build();


	protected static final ArrayListMultimap<String, String> VEHICLE_INFORMATION_MANUAL = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("LSOPCEndorsement Form", AVAILABLE)
					.put("Vehicles.1998, DODGE, CARAVAN.VIN", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Garaging Address", AVAILABLE)
					.put("Vehicles.1998, DODGE, CARAVAN.Ownership Type", CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> VEHICLE_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Vehicles.1998, DODGE, CARAVAN.VIN", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Garaging Address", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> VEHICLE_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicles.1998, DODGE, CARAVAN", "Is Garaging different from Residential?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Garaging Address")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> VEHICLE_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information section
			.put("Vehicles.1998, DODGE, CARAVAN", "VIN")
			.put("Vehicles.1998, DODGE, CARAVAN", "Year")
			.put("Vehicles.1998, DODGE, CARAVAN", "Make")
			.put("Vehicles.1998, DODGE, CARAVAN", "Model")
			.put("Vehicles.1998, DODGE, CARAVAN", "Series")
			.put("Vehicles.1998, DODGE, CARAVAN", "Body Style")
			.put("Vehicles.1998, DODGE, CARAVAN", "Primary Use")
			.put("Vehicles.1998, DODGE, CARAVAN", "Is the vehicle used in any commercial business operations?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Business Use Description")
			.put("Vehicles.1998, DODGE, CARAVAN", "Air Bags")
			.put("Vehicles.1998, DODGE, CARAVAN", "Anti-theft")
			.put("Vehicles.1998, DODGE, CARAVAN", "Alternative Fuel Vehicle")
			.put("Vehicles.1998, DODGE, CARAVAN", "Anti-theft Recovery Device")
			.put("Vehicles.1998, DODGE, CARAVAN", "Existing Damage")
			.put("Vehicles.1998, DODGE, CARAVAN", "Salvaged?")
			.put("Vehicles.1998, DODGE, CARAVAN", "Miles One-way to Work or School")
			.put("Vehicles.1998, DODGE, CARAVAN", "Odometer Reading")
			.put("Vehicles.1998, DODGE, CARAVAN", "Odometer Reading Date")
			.put("Vehicles.1998, DODGE, CARAVAN", "Customer Declared Annual Miles")
			//Ownership address
			.put("Vehicles.1998, DODGE, CARAVAN", "Ownership Type")
			.put("Vehicles.1998, DODGE, CARAVAN", "First Name")
			.put("Vehicles.1998, DODGE, CARAVAN", "Vehicle Ownership Address")
			//Technical data
			.put("Vehicles.1998, DODGE, CARAVAN", "BI Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "PD Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "UM Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "MP Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Comp Symbol")
			.put("Vehicles.1998, DODGE, CARAVAN", "Coll Symbol")
			.build();

	protected static final ArrayListMultimap<String, String> ADD_VEHICLE_INFORMATION_AUTOMATIC = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Work Phone Number", AVAILABLE)
					.put("Contact Information.Mobile Phone Number", CURRENT)
					.put("Contact Information.Preferred Phone #", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/endorsement
	static final Multimap<String, String> ADD_VEHICLE_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Email")
			.put("Vehicles", "Vehicle (2003, MERCEDES-BENZ, SL500R)")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ADD_VEHICLE_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")
			.put("Vehicles", "Vehicle (1998, DODGE, CARAVAN)")
			.build();


	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> ASSIGNMENT = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("AAADriver Vehicle Relationship", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Primary Driver", CURRENT)
					.put("Vehicles.1998, DODGE, CARAVAN.Manually Rated Driver", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ASSIGNMENT_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicles.1998, DODGE, CARAVAN", "System Rated Driver")
			.put("Vehicles.1998, DODGE, CARAVAN", "Manually Rated Driver")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> ASSIGNMENT_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicles.1998, DODGE, CARAVAN", "Primary Driver")
			.build();

	//components/attributes that should be on comparision page Rolled on/OOSE for Removing components
	static final Multimap<String,String> REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_2 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers","Driver (Third Driver Version2)")
			.put("Vehicles","Vehicle (2011, MERCEDES-BENZ, G55AMG)")
			.build();

	//components/attributes that should be on comparision page Rolled on/Endorsement for Removing components
	static final Multimap<String,String> REMOVE_DRIVER_VEHICLE_INFORMATION_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers","Driver (Second Driver Version1)")
			.put("Drivers","Driver (Second Driver Version1)")
			.put("Vehicles","Vehicle (2011, CHEVROLET, EXPRESS VAN)")
			.put("Vehicles","Vehicle (2011, CHEVROLET, EXPRESS VAN)")
			.build();

	protected static final ArrayListMultimap<String, String> REMOVE_DRIVER_VEHICLE_INFORMATION = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAADriver Vehicle Relationship", CURRENT)
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("Drivers.Driver (Second Driver Version1)",CURRENT)
					.put("Drivers.Driver (Second Driver Version1)",CURRENT)
					.put("Drivers.Driver (Third Driver Version2)",AVAILABLE)
					.put("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)",CURRENT)
					.put("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)",CURRENT)
					.put("Vehicles.Vehicle (2011, MERCEDES-BENZ, G55AMG)",AVAILABLE)
					.build());

	//components/attributes that should be on comparision page Rolled on/Endorsement for Removing components
	static final Multimap<String,String> REMOVE_NAMED_INSUREDS_RENEWAL_VERSION_1 = ImmutableListMultimap.<String, String>builder()
			.put("Named Insureds","Insured Principal (Second VI Insured)")
			.build();

	protected static final ArrayListMultimap<String, String> REMOVE_NAMED_INSURED = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.build());

	protected static final ArrayListMultimap<String, String> ATOMIC_MERGE_SCENARIO1 = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAADriver Vehicle Relationship", CURRENT)
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("Drivers.Driver (Second Driver Version1)",AVAILABLE)
					.build());

	//components/attributes that should be on comparision page Rolled on/Endorsement for Atomic Merge Scenario1
	static final Multimap<String,String> ATOMIC_MERGE_SCENARIO1_VERSION1 = ImmutableListMultimap.<String, String>builder()
			.put("Drivers","Driver (Second Driver Version1)")
			.put("Vehicles.2008, ACURA, MDX","Primary Driver")
			.put("Vehicles.2008, ACURA, MDX","System Rated Driver")
			.put("Vehicles.2008, ACURA, MDX","Manually Rated Driver")
			.build();

	protected static final ArrayListMultimap<String, String> ATOMIC_MERGE_SCENARIO2 = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAADriver Vehicle Relationship", CURRENT)
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("Drivers.Driver (Second Driver Version1)",CURRENT)
					.put("Vehicles.Vehicle (1998, DODGE, CARAVAN)",CURRENT)
					.put("Vehicles.2008, ACURA, MDX.Primary Driver",AVAILABLE)
					.put("Vehicles.2008, ACURA, MDX.Manually Rated Driver",AVAILABLE)
					.build());

	//components/attributes that should be on comparision page Rolled on/Endorsement for Atomic Merge Scenario1
	static final Multimap<String,String> ATOMIC_MERGE_SCENARIO2_VERSION1 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicles.2008, ACURA, MDX","Primary Driver")
			.put("Vehicles.2008, ACURA, MDX","System Rated Driver")
			.put("Vehicles.2008, ACURA, MDX","Manually Rated Driver")
			.build();

	protected static final ArrayListMultimap<String, String> ATOMIC_MERGE_SCENARIO3 = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAADriver Vehicle Relationship", CURRENT)
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("Drivers.Driver (Second Driver Version1)",CURRENT)
					.put("Drivers.Driver (Second Driver Version1)",CURRENT)
					.put("Drivers.Driver (Third Driver Version2)",AVAILABLE)
					.put("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)",AVAILABLE)
					.put("Vehicles.Vehicle (2011, CHEVROLET, EXPRESS VAN)",AVAILABLE)
					.put("Vehicles.Vehicle (2011, MERCEDES-BENZ, G55AMG)",CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> ATOMIC_MERGE_MANUAL_SCENARIO4 = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAADriver Vehicle Relationship", CURRENT)
					.put("AAADriver Vehicle Relationship", AVAILABLE)
					.put("Drivers.Driver (New Driver Version1)",CURRENT)
					.put("Drivers.Driver (New Driver Version2)",CURRENT)
					.put("Drivers.Driver (New Driver Version2)",CURRENT)
					.build());

	protected static final ArrayListMultimap<String, String> ATOMIC_MERGE_AUTOMATIC_SCENARIO4 = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Drivers.Driver (New Driver Version1)",CURRENT)
					.put("Drivers.Driver (New Driver Version2)",CURRENT)
					.build());

	//components/attributes that should be on comparision page Rolled on/Endorsement for Atomic Merge Scenario1
	static final Multimap<String,String> ATOMIC_MERGE_SCENARIO4_VERSION1 = ImmutableListMultimap.<String, String>builder()
			.build();
}