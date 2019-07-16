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

		SQL_UPDATE= "update propertyconfigurerentity set value = 'https://soaqa3.tent.trt.csaa.pri/1.1/RetrieveAgreementRelatedDocuments' where propertyname = 'aaaRetrieveAgreementWebClient.endpointUri'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++aaaRetrieveAgreementWebClient++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'https://soaqa3.tent.trt.csaa.pri/1.1/RetrieveDocument' where propertyname = 'aaaRetrieveDocumentWebClient.endpointUri'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++aaaRetrieveDocumentWebClient++++ updated");

		SQL_UPDATE= "Update PropertyConfigurerentity set value = 'TRUE' Where Propertyname = 'abstractDocgenEventListener.enablePasDocAndPasDocGen'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++abstractDocgenEventListener++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'https://soaqa3.tent.trt.csaa.pri/3.1/StandardDocumentService' where propertyname = 'docGenwebClient.endpointUri'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++docGenwebClient++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'http://wiremock-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/as/token.oauth2' where propertyname = 'oauth2TokenStore.authTokenEndpointUrl'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++authTokenEndpointUrl++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'vFS9ez6zISomQXShgJ5Io8mo9psGPHHiPiIdW6bwjJKOf4dbrd2m1AYUuB6HGjqx' where propertyname = 'oauth2TokenStore.clientSecret'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++clientSecret++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'EXTERNAL' where propertyname = 'pasDocEventBuilder.pasDocServerType'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasDocServerType++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'http://pasdoc-qa2.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-doc-rs/generateAdhocDocuments' where propertyname = 'pasdocGenerateAdhocDocsProvider.address'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasdocGenerateAdhocDocsProvider++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'https://pasdoc-dev-phase3.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-doc-rs/generateDocuments' where propertyname = 'pasdocGenerateBatchAndOnlineDocsProvider.address'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasdocGenerateBatchAndOnlineDocsProvider++++ updated");

		SQL_UPDATE= "update propertyconfigurerentity set value = 'https://pasdoc-dev-phase3.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-doc-rs/getAdhocTemplates' where propertyname = 'pasdocRetrieveAdhocDocsProvider.address'";
		DBService.get().executeUpdate(SQL_UPDATE);
		log.info("DB update +++++ PAS DOC PRECONDITIONS ++++pasdocRetrieveAdhocDocsProvider++++ updated");
	}



}
