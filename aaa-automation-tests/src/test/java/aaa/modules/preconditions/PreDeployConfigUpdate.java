package aaa.modules.preconditions;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import aaa.config.CsaaTestProperties;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;

public class PreDeployConfigUpdate implements EvalueInsertSetupPreConditions {

	private void insertConfigForRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CONFIGURATION_PER_STATE_INSERT, state));
	}

	private void insertConfigForLimitsRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT, state));
		DBService.get().executeUpdate(String.format(EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT, state));
	}

	@Test
	public void propertyConfigureEntityUpdate() {
		final String HOST = PropertyProvider.getProperty(CsaaTestProperties.APP_HOST);
		List<String> servers = Arrays.asList("aws2aaawas17", "aws2aaawas19", "aws2aaawas21", "aws2aaawas31", "aws2aaawas34", "aws2aaawas36");
		/*final String LAST_PAYMENT_METHOD_QUERY = "update propertyconfigurerentity\n"
				+ "set value = '%s/payments/lastTransactionInfo/retrieveByPolicyInfo'\n"
				+ "where propertyname = 'lastPaymentService.lastPaymentServiceUrl'";*/
		//DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCE_API_SERVICE_UPDATE, PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/policy/preferences"));
		//	DBService.get().executeUpdate(String.format(LAST_PAYMENT_METHOD_QUERY, PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE)));
		//	DBService.get().executeUpdate(PENDING_REFUND_CONFIGURATION_UPDATE);
		DBService.get().executeUpdate(String.format("update propertyconfigurerentity set value = '%s' where propertyname = 'oauth2TokenStore.authTokenEndpointUrl'", PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/as/token.oauth2"));
		DBService.get().executeUpdate(String.format("update propertyconfigurerentity set value = '%s' where propertyname = 'censusBlockServiceClient.serviceUrl'", PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/eads/census-block"));

		if (servers.stream().anyMatch(s -> HOST.contains(s))) {
			DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCE_API_SERVICE_UPDATE, PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/" + PropertyProvider.getProperty(CsaaTestProperties.APP_HOST) + "/policy/preferences"));
			DBService.get().executeUpdate(String.format(LAST_PAYMENT_METHOD_STUB_POINT_UPDATE_WIREMOCK, PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE), PropertyProvider.getProperty(CsaaTestProperties.APP_HOST)));
			DBService.get().executeUpdate(PENDING_REFUND_CONFIGURATION_UPDATE);
			if (!PropertyProvider.getProperty(CsaaTestProperties.SCRUM_ENVS_SSH, false)) {
				DBService.get().executeUpdate(String.format(DXP_AUTHENTICATION_PARAMETERS_INSERT, "test", "DXP wiremock authentication parameters for AWS", "restOAuth2RemoteTokenServices.checkTokenEndpointUrl", PropertyProvider.getProperty(CsaaTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/as/token.oauth2"));
				DBService.get().executeUpdate(String.format(DXP_AUTHENTICATION_PARAMETERS_INSERT, "test", "DXP wiremock authentication parameters for AWS", "restOAuth2RemoteTokenServices.clientId", "cc_PAS"));
				DBService.get().executeUpdate(String.format(DXP_AUTHENTICATION_PARAMETERS_INSERT, "test", "DXP wiremock authentication parameters for AWS", "restOAuth2RemoteTokenServices.clientSecret", "vFS9ez6zISomQXShgJ5Io8mo9psGPHHiPiIdW6bwjJKOf4dbrd2m1AYUuB6HGjqx"));
				DBService.get().executeUpdate(String.format(DXP_AUTHENTICATION_PARAMETERS_INSERT, "test", "DXP wiremock authentication parameters for AWS", "restOAuth2RemoteTokenServices.grantType", "urn:pingidentity.com:oauth2:grant_type:validate_bearer"));
			}
			List<String> configForStatesLimits = Arrays.asList(
					"MD"
					, "DC"
					, "OR");
			for (String configForStatesLimit : configForStatesLimits) {
				insertConfigForLimitsRegularStates(configForStatesLimit);
				insertConfigForRegularStates(configForStatesLimit);
			}

		}
	}
}
