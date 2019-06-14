package aaa.modules.preconditions;

import aaa.helpers.constants.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;

public class PasDocPreconditions {
	private static Logger log = LoggerFactory.getLogger(PasDocPreconditions.class);
	private String SQL_UPDATE;
	private static final String DOCUMENT_DOWNLOAD_DIRECTORY = System.getProperty("pasdoc.dir") + PropertyProvider.getProperty("test.downloadfiles.location");

	/**
	 * Pre conditions to update pasdoc end points
	 */

	@Test(groups = Groups.PRECONDITION)
	public void updateRetrieveAgreement() {

		SQL_UPDATE= "UPDATE PropertyConfigurerentity set value = 'http://sit-soaservices.tent.trt.csaa.pri:42000/1.1/RetrieveAgreementRelatedDocuments' Where Propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++aaaRetrieveAgreementWebClient++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'http://sit-soaservices.tent.trt.csaa.pri:42000/1.1/RetrieveDocument' Where Propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++aaaRetrieveDocumentWebClient++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'TRUE' Where Propertyname = 'abstractDocgenEventListener.enablePasDocAndPasDocGen'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++abstractDocgenEventListener++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'https://soaqa1.tent.trt.csaa.pri/3.1/StandardDocumentService' Where Propertyname = 'docGenwebClient.endpointUri'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++docGenwebClient++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'http://wiremock-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/as/token.oauth2' Where Propertyname = 'oauth2TokenStore.authTokenEndpointUrl'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++authTokenEndpointUrl++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'vFS9ez6zISomQXShgJ5Io8mo9psGPHHiPiIdW6bwjJKOf4dbrd2m1AYUuB6HGjqx' Where Propertyname = 'oauth2TokenStore.clientSecret'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++clientSecret++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'EXTERNAL' Where Propertyname = 'pasDocEventBuilder.pasDocServerType'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasDocServerType++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'http://pasdoc-dev.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-doc-rs/generateAdhocDocuments' Where Propertyname = 'pasdocGenerateAdhocDocsProvider.address'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasdocGenerateAdhocDocsProvider++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'http://pasdoc-dev.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-doc-rs/generateDocuments' Where Propertyname = 'pasdocGenerateBatchAndOnlineDocsProvider.address'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasdocGenerateBatchAndOnlineDocsProvider++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'http://pasdoc-dev.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-doc-rs/getAdhocTemplates' Where Propertyname = 'pasdocRetrieveAdhocDocsProvider.address'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasdocRetrieveAdhocDocsProvider++++ updated");
	}



}
