package aaa.modules.regression.sales.helper;

import java.util.Arrays;
import java.util.List;
import toolkit.db.DBService;

public class MoratoriumQueries {

	static List<String> deleteMoratoriumByIdQueries = Arrays.asList(
			"delete from MORATORIUMRULECOVSELECTION mrulecovsel\n"
					+ "where mrulecovsel.RULE_ID in (\n"
					+ "select mrule.ID\n"
					+ "FROM MORATORIUM m\n"
					+ "LEFT JOIN MORATORIUMRULE mrule\n"
					+ "ON m.id = mrule.MORATORIUM_ID\n"
					+ "where m.id='%s')",
			"delete from MORATORIUMQUOTETRANSACTION mquotetrans\n"
					+ "where mquotetrans.APPLIANCE_ID in (\n"
					+ "select mquoteapp.id\n"
					+ "from MORATORIUMQUOTEAPPLIANCE mquoteapp\n"
					+ "where mquoteapp.RULE_ID = (\n"
					+ "select mrule.ID\n"
					+ "FROM MORATORIUM m\n"
					+ "LEFT JOIN MORATORIUMRULE mrule\n"
					+ "ON m.id = mrule.MORATORIUM_ID\n"
					+ "where m.id='%s'))",
			"delete from MORATORIUMQUOTEAPPLIANCE mquoteapp\n"
					+ "where mquoteapp.RULE_ID in (\n"
					+ "select mrule.ID\n"
					+ "FROM MORATORIUM m\n"
					+ "LEFT JOIN MORATORIUMRULE mrule\n"
					+ "ON m.id = mrule.MORATORIUM_ID\n"
					+ "where m.id='%s')",
			"delete from MORATORIUMPOLICYTRANSACTION mpolicytrans\n"
					+ "where mpolicytrans.RULE_ID in\n"
					+ "(select mrule.id \n"
					+ "from  MORATORIUM m, MORATORIUMRULE mrule\n"
					+ "where m.id = mrule.MORATORIUM_ID\n"
					+ "and m.id='%s')",
			"delete from EXPOSURECAP exposurecap\n"
					+ "where exposurecap.RULE_ID in \n"
					+ "(select mrule.id \n"
					+ "from  MORATORIUM m, MORATORIUMRULE mrule\n"
					+ "where m.id = mrule.MORATORIUM_ID\n"
					+ "and m.id='%s')",
			"delete from MORATORIUMRULE mrule\n"
					+ "where mrule.MORATORIUM_ID = '%s'",
			"delete from MORATORIUMAGENCYSELECTION magency\n"
					+ "where magency.MORATORIUM_ID = '%s'",
			"delete from MORATORIUMAGENTSELECTION magent\n"
					+ "where magent.MORATORIUM_ID = '%s'",
			"delete from MORATORIUMPOLICYFORMSELECTION mpolicy\n"
					+ "where mpolicy.MORATORIUM_ID = '%s'",
			"delete from MORATORIUMPRODUCTSELECTION mproduct\n"
					+ "where mproduct.MORATORIUM_ID = '%s'",
			"delete from MORATORIUM m\n"
					+ "where m.ID = '%s'"
	);


	public static String findMoratoriumIdByName(String name){
		String findMoratoriumIdByNameQuery = String.format("SELECT ID FROM moratorium WHERE name = '%s'", name);
		String moratorium_id = DBService.get().getValue(findMoratoriumIdByNameQuery).get();
		return moratorium_id;
	}

	public static void deleteMoratoriumById(String name){
		for (String element : deleteMoratoriumByIdQueries){
			String query = String.format(element, findMoratoriumIdByName(name));
			DBService.get().executeUpdate(query);
		}
	}

	public static String insertLookupEntry(String zipCode, String city){
		return String.format(INSERT_ZIP_IN_LOOKUPVALUE_TABLE_QUERY, zipCode, city);
	}

	public static final String INSERT_ZIP_IN_LOOKUPVALUE_TABLE_QUERY = "declare\n"
			+ "  l_lookupId number;\n"
			+ "  l_exst number;  \n"
			+ "begin \n"
			+ "    SELECT id into l_lookupId from lookuplist where lookupname = 'AAAMoratoriumGeographyLocationInfo';\n"
			+ "      SELECT count(1) into l_exst FROM lookupvalue \n"
			+ "        where lookuplist_id = l_lookupId\n"
			+ "        and POSTALCODE = '%1$s';\n"
			+ "  if l_exst = 0\n"
			+ "  then\n"
			+ "    INSERT INTO lookupvalue (DTYPE, CODE, DISPLAYVALUE, countrycd, POSTALCODE, lookuplist_id, statecd, city) \n"
			+ "        VALUES ('AAAMoratoriumGeographyLocationInfoLookupValue', 'US-AZ-%2$s-%1$s', 'US-AZ-%2$s-%1$s', 'US', '%1$s', l_lookupId, 'AZ', '%2$s');\n"
			+ "  end if;\n"
			+ "end;";


}

