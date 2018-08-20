package aaa.modules.regression.sales.auto_ss.functional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

public class VersionsConflictConstants {

	private static final String AVAILABLE = "Available";
	private static final String CURRENT = "Current";

	//Values that are not in Test Data, but are used in comparison
	static final Multimap<String, String> predefinedExpectedValues = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information
			//TODO should be deleted/updated when Report tab is fixed
			.putAll("Named Insured Information (VIFirstName VI VILastName).Residential Address", "VII residence address 1, VII residence address 2, Red Rock, AZ, 85245", "VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			.putAll("Named Insured Information (VIIFirstName VII VIILastName).Prior Address", "VI prior address 1, VI prior address 2, Phoenix, AZ, 85085", "VII prior address 1, VII prior address 2, Red Rock, AZ, 85245")
			.putAll("Named Insured Information (VIFirstName VI VILastName).Mailing Address", "VII mailing address 1, VII mailing address 2, Red Rock, AZ, 85245", "VI mailing address 1, VI mailing address 2, Phoenix, AZ, 85085")
			//Policy Information
			.putAll("Policy Information.Override ASD Level","true","false")
			.putAll("Policy Information.Agency of Record","500023745","500001406")
			.putAll("Policy Information.Agent of Record","500034992","500012749")
			.putAll("Policy Information.Agent Number","500034992","500012749")
			//Current Currier Information
			.putAll("Current Carrier Information.Months with Carrier","12","4")
			//Vehicle Information
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Year","1998","2003")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Make","DODG","MERZ")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Model","CARAVAN","SL500R")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Series","GRAND CARAVAN SE/SPORT","SL500R")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Body Style","SPORT VAN","ROADSTER")
			//TODO should be return to TD when True/False vs Yes/No is fixed
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Salvaged?", "true","false")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Is the vehicle used in any commercial business operations?", "","false")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Air Bags","Both Front","Both Front and Side")
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Alternative Fuel Vehicle","true","false")
			//Garaging address
			.putAll("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Garaging Address", "VII garaging address 1, VII garaging address 2, Red Rock, AZ, 85245","VI residence address 1, VI residence address 2, Phoenix, AZ, 85085")
			.build();

	//mapping of expected Component.Attribute to TD attributes
	static final Multimap<String, String> uiFieldsToTDMapping = ImmutableListMultimap.<String, String>builder()
			//Named Insured Information section (all other fields are mapped from MultiMap predefinedExpectedValues)
			//TODO should be deleted when Report tab is fixed
			.put("Driver Information (VIFirstName VI VILastName).First Name", "First Name")
			.put("Driver Information (VIFirstName VI VILastName).Middle Name", "Middle Name")
			.put("Driver Information (VIFirstName VI VILastName).Last Name", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName).Suffix", "Suffix")
			.put("Driver Information (VIFirstName VI VILastName).Base Date", "Base Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName).Prefix", "Prefix")
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
			.put("AAA Products Owned.Override Type", "Override Type")
			.put("AAA Products Owned.Member Since Date", "Member Since Date")
			.put("AAA Products Owned.Policy #", "Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Life Policy #")
			.put("AAA Products Owned.Policy #", "Home Motorcycle Policy #")
			.put("AAA Products Owned.Policy #", "Renters Policy #")
			.put("AAA Products Owned.Policy #", "Condo Policy #")
			.put("AAA Products Owned.Policy #", "PUP Motorcycle Policy #")
			//Contact Information
			.put("Contact Information.Home Phone Number", "Home Phone Number")
			.put("Contact Information.Work Phone Number", "Work Phone Number")
			.put("Contact Information.Mobile Phone Number", "Mobile Phone Number")
			.put("Contact Information.Preferred Phone #", "Preferred Phone #")
			.put("Contact Information.Email", "Email")
			//Policy Information
			.put("Policy Information.Source of Business","Source of Business")
			.put("Policy Information.Source Policy #","Source Policy #")
			//TODO should be returned when Yes/NO vs true/false is fixed
			//.put("Policy Information.Override ASD Level","Override ASD Level")
			.put("Policy Information.Advance Shopping Discount Override","Advance Shopping Discount Override")
			.put("Policy Information.ASD Overriden By","ASD Overriden By")
			.put("Policy Information.Channel Type","Channel Type")
			.put("Policy Information.Agency","Agency")
			.put("Policy Information.Sales Channel","Sales Channel")
			.put("Policy Information.Agent","Agent")
			.put("Policy Information.Authorized by","Authorized by")
			.put("Policy Information.TollFree Number","TollFree Number")
			.put("Policy Information.Suppress Print","Suppress Print")
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
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER).Anti-theft", "Anti-theft")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> namedInsuredInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Claims Report Order.First Name", CURRENT)
					.put("AAA Claims Report Order.Last Name", CURRENT)
					.put("AAAMvr Report Order.First Name",CURRENT)
					.put("AAAMvr Report Order.Last Name",CURRENT)
					.put("Driver Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Prefix", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Middle Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Last Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Suffix", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Social Security Number", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Base Date",CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residential Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Move-In Date", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Mailing Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residence", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> namedInsuredInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Prefix")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Move-In Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Prior Address")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Residence")
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> namedInsuredInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.put("AAA Claims Report Order", "First Name")
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
			.put("Named Insured Information (VIFirstName VI VILastName)", "Social Security Number")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Residential Address")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Mailing Address")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> aaaProductOwnedManual = ArrayListMultimap.create(
					ImmutableListMultimap.<String, String>builder()
							.put("AAA Membership Order.Last Name", CURRENT)
							.put("AAA Membership Order.Membership Number", CURRENT)
							.put("AAA Membership Order.Member Since", CURRENT)
							.put("AAA Membership Order.Order Date", CURRENT)
							.put("AAA Membership Order.Receipt Date", CURRENT)
							.put("AAA Membership Order.Status", CURRENT)
							.put("AAA Membership Order.Membership Total Years", CURRENT)
							.put("AAA Products Owned.Current AAA Member", CURRENT)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.put("AAA Products Owned.Policy #", AVAILABLE)
							.build());

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> aaaProductOwnedAutomatic = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Products Owned.Current AAA Member", CURRENT)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.put("AAA Products Owned.Policy #", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> aaaProductOwnedVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.put("AAA Products Owned", "Policy #")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> aaaProductOwnedVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("AAA Products Owned", "Current AAA Member")
			.put("AAA Products Owned", "Membership Number")
			//TODO Should be return after implementation story - Clean up for AAA Product Owned
			/*.put("AAA Products Owned", "Override Type")
			.put("AAA Products Owned", "Member Since Date")*/
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> contactInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAAPolicy Issue Summary.Insured Email", AVAILABLE)
					.put("Contact Information.Home Phone Number", CURRENT)
					.put("Contact Information.Email", AVAILABLE)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> contactInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("AAAPolicy Issue Summary", "Insured Email")
			.put("Contact Information", "Email")
			.build();

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> contactInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("Contact Information", "Home Phone Number")
			.put("Contact Information", "Work Phone Number")
			.put("Contact Information", "Mobile Phone Number")
			.put("Contact Information", "Preferred Phone #")

			.build();

	protected static final ArrayListMultimap<String, String> policyInformationManual = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Override ASD Level", AVAILABLE)
					.put("Policy Information.Channel Type", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.build());

	protected static final ArrayListMultimap<String, String> policyInformationAutomatic = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Policy Information.Source of Business", CURRENT)
					.put("Policy Information.Advance Shopping Discount Override", AVAILABLE)
					.put("Policy Information.Channel Type", CURRENT)
					.put("Policy Information.TollFree Number", AVAILABLE)
					.build());


	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> policyInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Override ASD Level")
			.put("Policy Information","Advance Shopping Discount Override")
			.put("Policy Information","ASD Overriden By")
			.put("Policy Information","Authorized by")
			.put("Policy Information","TollFree Number")
			.put("Policy Information","Suppress Print")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> policyInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			.put("Policy Information","Source of Business")
			.put("Policy Information","Source Policy #")
			.put("Policy Information","Channel Type")
			.put("Policy Information","Agency")
			.put("Policy Information","Agency of Record")
			.put("Policy Information","Sales Channel")
			.put("Policy Information","Agent")
			.put("Policy Information","Agent of Record")
			.put("Policy Information","Agent Number")
			.build();

	/**
	 *  Maps full attribute path on conflict page to version to select (Current, Available).
	 */
	protected static final ArrayListMultimap<String, String> driverInformation = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("AAA Claims Report Order.First Name", CURRENT)
					.put("AAA Claims Report Order.Last Name", CURRENT)
					.put("AAAMvr Report Order.First Name",CURRENT)
					.put("AAAMvr Report Order.Last Name",CURRENT)
					.put("Driver Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Driver Information (VIIFirstName VII VIILastName).Middle Name", AVAILABLE)
					.put("Driver Information (VIIFirstName VII VIILastName).Last Name", CURRENT)
					.put("Driver Information (VIIFirstName VII VIILastName).Suffix", AVAILABLE)
					.put("Driver Information (VIIFirstName VII VIILastName).Base Date",CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Prefix", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).First Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Middle Name", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Last Name", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Suffix", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Social Security Number", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Base Date",CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residential Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Move-In Date", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Prior Address", AVAILABLE)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Mailing Address", CURRENT)
					.put("Named Insured Information (VIIFirstName VII VIILastName).Residence", AVAILABLE)
					//.put("Vehicle Information (2008, ACURA, MDX, WAGON 4 DOOR).Garaging Address", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page  Rolled on/OOSE
	static final Multimap<String, String> driverInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Driver Information (VIIFirstName VII VIILastName)", "Middle Name")
			.put("Driver Information (VIIFirstName VII VIILastName)", "Suffix")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Prefix")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Middle Name")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Suffix")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Move-In Date")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Prior Address")
			.put("Named Insured Information (VIIFirstName VII VIILastName)", "Residence")
			.build();

	//all components/attributes that should be on Comparison page Rolled on/Renewal or Endorsement
	static final Multimap<String, String> driverInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			//TODO should be deleted/updated when Report tab is fixed
			//start
			.put("AAA Claims Report Order", "First Name")
			.put("AAA Claims Report Order", "Last Name")
			.put("AAAMvr Report Order", "First Name")
			.put("AAAMvr Report Order", "Last Name")
			//end
			.put("Driver Information (VIFirstName VI VILastName)", "First Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Last Name")
			.put("Driver Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "First Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Last Name")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Social Security Number")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Base Date")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Residential Address")
			.put("Named Insured Information (VIFirstName VI VILastName)", "Mailing Address")
			//.put("Vehicle Information (2008, ACURA, MDX, WAGON 4 DOOR)", "Garaging Address")
			.build();


	protected static final ArrayListMultimap<String, String> vehicleInformationManual = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Usage", CURRENT)
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Garaging Address", AVAILABLE)
					.build());

	protected static final ArrayListMultimap<String, String> vehicleInformationAutomatic = ArrayListMultimap.create(
			ImmutableListMultimap.<String, String>builder()
					.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN).Usage", CURRENT)
					.build());

	//all components/attributes that should be on Comparison page Rolled on/OOSE
	static final Multimap<String, String> vehicleInformationVersion2 = ImmutableListMultimap.<String, String>builder()
			.put("Vehicle Information (1998, DODGE, CARAVAN, SPORT VAN)", "Garaging Address")
			.build();

	//all components/attributes that should be on Comparison page Renewal or Endorsement
	static final Multimap<String, String> vehicleInformationVersion1 = ImmutableListMultimap.<String, String>builder()
			//Vehicle Information
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Usage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "VIN")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Year")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Make")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Model")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Series")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Body Style")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Existing Damage")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Existing Damage Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Salvaged?")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Is the vehicle used in any commercial business operations?")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Business Use Description")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Miles One-way to Work or School")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Air Bags")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Anti-theft")
			.put("Vehicle Information (2003, MERCEDES-BENZ, SL500R, ROADSTER)", "Alternative Fuel Vehicle")
			.build();

}