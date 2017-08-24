package aaa.helpers.docgen;

import aaa.helpers.ssh.RemoteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.verification.CustomAssert;

import java.io.File;

public class DocGenHelper {
	public static final String DOCGEN_SOURCE_FOLDER = "/home/DocGen/";
	public static final String DOCGEN_BATCH_SOURCE_FOLDER = DOCGEN_SOURCE_FOLDER + "Batch/";
	public static final String DOCGEN_DESTINATION_FOLDER = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "xmls" + File.separator;
	public static final String JOB_GENERATION_DOCGEN_FOLDER = "/home/mp2/pas/sit/PAS_B_EXGPAS_DCMGMT_6500_D/outbound/";

	private static Logger log = LoggerFactory.getLogger(DocGenHelper.class);

	public static void downloadFilesByPolicy(String policyNum) {
		downloadFilesByPolicy(policyNum, true);
	}

	public static void downloadFilesByPolicy(String policyNum, boolean removeAfterDownload) {
		downloadFilesByPolicy(DOCGEN_SOURCE_FOLDER, DOCGEN_DESTINATION_FOLDER, policyNum, removeAfterDownload);
	}

	public static void downloadFilesByPolicy(String sourceFolder, String destinationFolder, String policyNum, boolean removeAfterDownload) {
		String searchCommandTemplate = "cd %s; find . -type f -iname '*.xml' -print | xargs grep -li '%s'";
		String searchCommand = String.format(searchCommandTemplate, sourceFolder, policyNum);
		String result = RemoteHelper.executeCommand(searchCommand);
		CustomAssert.assertFalse("Files not found for policy: " + policyNum + ", path: " + sourceFolder, result.isEmpty());

		String[] files = result.split("\n");
		String file;
		for (String f : files) {
			file = f.substring(f.indexOf('/') + 1).trim();
			RemoteHelper.downloadFile(sourceFolder + file, destinationFolder + file);
			if (removeAfterDownload) {
				RemoteHelper.removeFile(sourceFolder + file);
			}
		}
	}

	public static boolean downloadDocumentFiles(String policyNum, String formID, String xPathInfo) {
		return downloadDocumentFiles(policyNum, formID, xPathInfo, true);
	}

	public static boolean downloadDocumentFiles(String policyNum, String formID, String xPathInfo, boolean removeAfterDownload) {
		return downloadDocumentFiles(DOCGEN_SOURCE_FOLDER, DOCGEN_DESTINATION_FOLDER, policyNum, formID, xPathInfo, true);
	}

	//TODO-dchubkov: move common lines from this and downloadFilesByPolicy() methods to separate private method
	public static boolean downloadDocumentFiles(String sourceFolder, String destinationFolder, String policyNum, String formID, String xPathInfo, boolean removeAfterDownload) {
		String searchCommandTemplate = "cd %s; find . -type f -iname '*.xml' -print | xargs grep -li '%s' | xargs grep -li '%s' | xargs grep -li '%s'";
		String searchCommand = String.format(searchCommandTemplate, sourceFolder, policyNum, formID, xPathInfo);
		String result = RemoteHelper.executeCommand(searchCommand);

		if (result.isEmpty()) {
			return false;
		}

		String[] files = result.split("\n");
		String file;
		for (String f : files) {
			file = f.substring(f.indexOf('/') + 1).trim();
			RemoteHelper.downloadFile(sourceFolder + file, destinationFolder + file);
			if (removeAfterDownload) {
				RemoteHelper.removeFile(sourceFolder + file);
			}
		}
		return true;
	}

	//TODO-dchubkov: move & refactor verifyDocumentsGeneratedByJob() methods from old framework
}