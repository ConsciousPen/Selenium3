
package aaa.soap.aaaCSPolicyRate.exigeninsurance.aaa_csa_lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AAAOccupation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AAAOccupation">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Accountant"/>
 *     &lt;enumeration value="Agent"/>
 *     &lt;enumeration value="Architect"/>
 *     &lt;enumeration value="Broadcaster"/>
 *     &lt;enumeration value="BusinessOwner"/>
 *     &lt;enumeration value="CALicensedPhy"/>
 *     &lt;enumeration value="CPA"/>
 *     &lt;enumeration value="CivilService"/>
 *     &lt;enumeration value="Columnist"/>
 *     &lt;enumeration value="Dentist"/>
 *     &lt;enumeration value="Driver"/>
 *     &lt;enumeration value="Editor"/>
 *     &lt;enumeration value="Educator"/>
 *     &lt;enumeration value="Engineer"/>
 *     &lt;enumeration value="Entertainer"/>
 *     &lt;enumeration value="FarmOwner"/>
 *     &lt;enumeration value="Farmer"/>
 *     &lt;enumeration value="FinancialCVR"/>
 *     &lt;enumeration value="Fire Fighter"/>
 *     &lt;enumeration value="ForeignDiplomat"/>
 *     &lt;enumeration value="ForeignNational"/>
 *     &lt;enumeration value="FulltimeClergy"/>
 *     &lt;enumeration value="GolfProf"/>
 *     &lt;enumeration value="HealthCareProf"/>
 *     &lt;enumeration value="HomeMaint"/>
 *     &lt;enumeration value="Homemaker"/>
 *     &lt;enumeration value="InHomeDayCare"/>
 *     &lt;enumeration value="Insurance"/>
 *     &lt;enumeration value="Laborer"/>
 *     &lt;enumeration value="LawEnforcement"/>
 *     &lt;enumeration value="Legal"/>
 *     &lt;enumeration value="Manager"/>
 *     &lt;enumeration value="Manufacturing"/>
 *     &lt;enumeration value="Military"/>
 *     &lt;enumeration value="NewsReporter"/>
 *     &lt;enumeration value="OfficeAdmin"/>
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="OutHomeDayCare"/>
 *     &lt;enumeration value="Paramedic"/>
 *     &lt;enumeration value="Paramedic/EMT"/>
 *     &lt;enumeration value="Pharmacist"/>
 *     &lt;enumeration value="Pilot"/>
 *     &lt;enumeration value="PolOfficial"/>
 *     &lt;enumeration value="PoliceOfficer"/>
 *     &lt;enumeration value="ProfAthlete"/>
 *     &lt;enumeration value="ProfDriver"/>
 *     &lt;enumeration value="ProfJournalist"/>
 *     &lt;enumeration value="ProfLecturer"/>
 *     &lt;enumeration value="ProfPilot"/>
 *     &lt;enumeration value="Professional"/>
 *     &lt;enumeration value="Programmer"/>
 *     &lt;enumeration value="Publisher"/>
 *     &lt;enumeration value="RadioTVPers"/>
 *     &lt;enumeration value="Real Estate"/>
 *     &lt;enumeration value="RestFoodservice"/>
 *     &lt;enumeration value="Retired"/>
 *     &lt;enumeration value="SalesInside"/>
 *     &lt;enumeration value="SalesOutside"/>
 *     &lt;enumeration value="Scientist"/>
 *     &lt;enumeration value="ScriptWriter"/>
 *     &lt;enumeration value="Self Employed"/>
 *     &lt;enumeration value="SelfEmplContr"/>
 *     &lt;enumeration value="SelfEmplMaint"/>
 *     &lt;enumeration value="Student"/>
 *     &lt;enumeration value="Teacher"/>
 *     &lt;enumeration value="TeamManager"/>
 *     &lt;enumeration value="TeamOwner"/>
 *     &lt;enumeration value="Telecaster"/>
 *     &lt;enumeration value="Unemployed"/>
 *     &lt;enumeration value="Veterinarian"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AAAOccupation", namespace = "http://www.exigeninsurance.com/data/AAA_CSA_Lookup/1.0")
@XmlEnum
public enum AAAOccupation {

    @XmlEnumValue("Accountant")
    ACCOUNTANT("Accountant"),
    @XmlEnumValue("Agent")
    AGENT("Agent"),
    @XmlEnumValue("Architect")
    ARCHITECT("Architect"),
    @XmlEnumValue("Broadcaster")
    BROADCASTER("Broadcaster"),
    @XmlEnumValue("BusinessOwner")
    BUSINESS_OWNER("BusinessOwner"),
    @XmlEnumValue("CALicensedPhy")
    CA_LICENSED_PHY("CALicensedPhy"),
    CPA("CPA"),
    @XmlEnumValue("CivilService")
    CIVIL_SERVICE("CivilService"),
    @XmlEnumValue("Columnist")
    COLUMNIST("Columnist"),
    @XmlEnumValue("Dentist")
    DENTIST("Dentist"),
    @XmlEnumValue("Driver")
    DRIVER("Driver"),
    @XmlEnumValue("Editor")
    EDITOR("Editor"),
    @XmlEnumValue("Educator")
    EDUCATOR("Educator"),
    @XmlEnumValue("Engineer")
    ENGINEER("Engineer"),
    @XmlEnumValue("Entertainer")
    ENTERTAINER("Entertainer"),
    @XmlEnumValue("FarmOwner")
    FARM_OWNER("FarmOwner"),
    @XmlEnumValue("Farmer")
    FARMER("Farmer"),
    @XmlEnumValue("FinancialCVR")
    FINANCIAL_CVR("FinancialCVR"),
    @XmlEnumValue("Fire Fighter")
    FIRE_FIGHTER("Fire Fighter"),
    @XmlEnumValue("ForeignDiplomat")
    FOREIGN_DIPLOMAT("ForeignDiplomat"),
    @XmlEnumValue("ForeignNational")
    FOREIGN_NATIONAL("ForeignNational"),
    @XmlEnumValue("FulltimeClergy")
    FULLTIME_CLERGY("FulltimeClergy"),
    @XmlEnumValue("GolfProf")
    GOLF_PROF("GolfProf"),
    @XmlEnumValue("HealthCareProf")
    HEALTH_CARE_PROF("HealthCareProf"),
    @XmlEnumValue("HomeMaint")
    HOME_MAINT("HomeMaint"),
    @XmlEnumValue("Homemaker")
    HOMEMAKER("Homemaker"),
    @XmlEnumValue("InHomeDayCare")
    IN_HOME_DAY_CARE("InHomeDayCare"),
    @XmlEnumValue("Insurance")
    INSURANCE("Insurance"),
    @XmlEnumValue("Laborer")
    LABORER("Laborer"),
    @XmlEnumValue("LawEnforcement")
    LAW_ENFORCEMENT("LawEnforcement"),
    @XmlEnumValue("Legal")
    LEGAL("Legal"),
    @XmlEnumValue("Manager")
    MANAGER("Manager"),
    @XmlEnumValue("Manufacturing")
    MANUFACTURING("Manufacturing"),
    @XmlEnumValue("Military")
    MILITARY("Military"),
    @XmlEnumValue("NewsReporter")
    NEWS_REPORTER("NewsReporter"),
    @XmlEnumValue("OfficeAdmin")
    OFFICE_ADMIN("OfficeAdmin"),
    @XmlEnumValue("Other")
    OTHER("Other"),
    @XmlEnumValue("OutHomeDayCare")
    OUT_HOME_DAY_CARE("OutHomeDayCare"),
    @XmlEnumValue("Paramedic")
    PARAMEDIC("Paramedic"),
    @XmlEnumValue("Paramedic/EMT")
    PARAMEDIC_EMT("Paramedic/EMT"),
    @XmlEnumValue("Pharmacist")
    PHARMACIST("Pharmacist"),
    @XmlEnumValue("Pilot")
    PILOT("Pilot"),
    @XmlEnumValue("PolOfficial")
    POL_OFFICIAL("PolOfficial"),
    @XmlEnumValue("PoliceOfficer")
    POLICE_OFFICER("PoliceOfficer"),
    @XmlEnumValue("ProfAthlete")
    PROF_ATHLETE("ProfAthlete"),
    @XmlEnumValue("ProfDriver")
    PROF_DRIVER("ProfDriver"),
    @XmlEnumValue("ProfJournalist")
    PROF_JOURNALIST("ProfJournalist"),
    @XmlEnumValue("ProfLecturer")
    PROF_LECTURER("ProfLecturer"),
    @XmlEnumValue("ProfPilot")
    PROF_PILOT("ProfPilot"),
    @XmlEnumValue("Professional")
    PROFESSIONAL("Professional"),
    @XmlEnumValue("Programmer")
    PROGRAMMER("Programmer"),
    @XmlEnumValue("Publisher")
    PUBLISHER("Publisher"),
    @XmlEnumValue("RadioTVPers")
    RADIO_TV_PERS("RadioTVPers"),
    @XmlEnumValue("Real Estate")
    REAL_ESTATE("Real Estate"),
    @XmlEnumValue("RestFoodservice")
    REST_FOODSERVICE("RestFoodservice"),
    @XmlEnumValue("Retired")
    RETIRED("Retired"),
    @XmlEnumValue("SalesInside")
    SALES_INSIDE("SalesInside"),
    @XmlEnumValue("SalesOutside")
    SALES_OUTSIDE("SalesOutside"),
    @XmlEnumValue("Scientist")
    SCIENTIST("Scientist"),
    @XmlEnumValue("ScriptWriter")
    SCRIPT_WRITER("ScriptWriter"),
    @XmlEnumValue("Self Employed")
    SELF_EMPLOYED("Self Employed"),
    @XmlEnumValue("SelfEmplContr")
    SELF_EMPL_CONTR("SelfEmplContr"),
    @XmlEnumValue("SelfEmplMaint")
    SELF_EMPL_MAINT("SelfEmplMaint"),
    @XmlEnumValue("Student")
    STUDENT("Student"),
    @XmlEnumValue("Teacher")
    TEACHER("Teacher"),
    @XmlEnumValue("TeamManager")
    TEAM_MANAGER("TeamManager"),
    @XmlEnumValue("TeamOwner")
    TEAM_OWNER("TeamOwner"),
    @XmlEnumValue("Telecaster")
    TELECASTER("Telecaster"),
    @XmlEnumValue("Unemployed")
    UNEMPLOYED("Unemployed"),
    @XmlEnumValue("Veterinarian")
    VETERINARIAN("Veterinarian");
    private final String value;

    AAAOccupation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AAAOccupation fromValue(String v) {
        for (AAAOccupation c: AAAOccupation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
