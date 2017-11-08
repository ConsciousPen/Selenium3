package aaa.modules.regression.service.helper;


import org.testng.annotations.Test;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class Preconditions {

	private static final String DXP_CONFIG_CHECK = "select AUTHOR,description,PROPERTYNAME,VALUE\n"+
			"from PROPERTYCONFIGURERENTITY\n"+
			"where propertyname = 'aaaPFRestfulEndorsementActionProvider.pfRestfulWorkspaceEnabled'";

	private static final String DXP_CONFIG_INSERT = "select AUTHOR,description,PROPERTYNAME,VALUE\n"+
			"from PROPERTYCONFIGURERENTITY\n"+
			"where propertyname = 'aaaPFRestfulEndorsementActionProvider.pfRestfulWorkspaceEnabled'";

	public static void dxpConfigurationCheck() {
		CustomAssert.assertTrue("DXP configuration is not ON. Please insert the configuration", DBService.get().getValue(DXP_CONFIG_CHECK).isPresent());
	}


	@Test
	@TestInfo(isAuxiliary = true)
	public static void eValuePriorBiCurrentBiConfigCheck() {
		DBService.get().executeUpdate(DXP_CONFIG_INSERT);
	}

}

