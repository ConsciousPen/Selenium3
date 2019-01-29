package aaa.modules.adt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.ssh.Ssh;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public class BatchXmlsProcessingTest {
	protected static Logger log = LoggerFactory.getLogger(BatchXmlsProcessingTest.class);
	
	private String doc_host = "aws2aaadoc02.corevelocity.csaa.cloud";	
	private String ssh_user = "qauser";
	private String ssh_password = "qauser";
	
	//Folders on SFTP
	private String REMOTE_DOCGEN_FOLDER = "/home/mp2/pas/sit/PAS_B_EXGPAS_DCMGMT_6500_D/outbound/"; 
	
	//Local folders 
	private String LOCAL_DOCGEN_FOLDER = "src/test/resources/adt/xml/"; 
	private String LOCAL_TXT_FOLDER = "src/test/resources/adt/txt/";
	
	//Remote folder on SFTP aws2aaadoc02.corevelocity.csaa.cloud
	private String REMOTE_FOLDER = "/Deloitte/docTesting/Batch/XML/";
	
	private File policyNumbersFile;
	
	@Test
	public void BatchXmlProcessingTest() {
		createTempFolder(LOCAL_DOCGEN_FOLDER);
		createTempFolder(LOCAL_TXT_FOLDER);
		
		RemoteHelper.get().clearFolder(REMOTE_DOCGEN_FOLDER);

		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		
		RemoteHelper.get().downloadBatchFiles(REMOTE_DOCGEN_FOLDER, new File(LOCAL_DOCGEN_FOLDER)).closeSession();

		//get policy numbers from xmls
		createFileWithPolicyNumbers();
		
		uploadFilesToRemoteHost(LOCAL_DOCGEN_FOLDER);
		uploadFilesToRemoteHost(LOCAL_TXT_FOLDER);
		
		deleteTempFolder(LOCAL_DOCGEN_FOLDER);
		deleteTempFolder(LOCAL_TXT_FOLDER);		
	}
	
	public void uploadFilesToRemoteHost(String source) {
		Ssh ssh = new Ssh(doc_host, ssh_user, ssh_password);
		File directory = new File(source);
		File[] files = directory.listFiles(File::isFile);
		if (files != null && files.length != 0) {
			for (File file : files) {
				ssh.putFile(source + file.getName(), REMOTE_FOLDER + file.getName());
			}
		}
		ssh.closeSession();
	}
	
	public void createFileWithPolicyNumbers() {
		try {
			policyNumbersFile = new File(LOCAL_TXT_FOLDER + getAppHostName() + "_PoliciesNumbers.txt"); 
			policyNumbersFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(policyNumbersFile));	
			
			File directory = new File(LOCAL_DOCGEN_FOLDER);
			File[] files = directory.listFiles(File::isFile);
			for (File file : files) {
				if (file != null && file.length() != 0) {
					try {
						log.info("Processing file: " + file.getName());
						getPolicyNumbersFromXml(LOCAL_DOCGEN_FOLDER + file.getName(), bw); 
					} catch (IOException e) {
						//e.printStackTrace();
						throw new AssertionError("Can't process file: " + file.getName());
					}
				}
			}
			bw.close();
			
		} catch (IOException e) {
			throw new AssertionError("File for policies numbers wasn't created");
		}		
	}
	
	public void getPolicyNumbersFromXml(String fileName, BufferedWriter bwFile) throws IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		Document document;
		try {
			document = docFactory.newDocumentBuilder().parse(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AssertionError("Can't parse file: " + fileName);
		}	
		
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//StandardDocumentRequest/DocumentPackages/DocumentPackage/PackageIdentifier");
			NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET); 
			for (int i = 0; i < nodes.getLength(); i++) 
			{
				//log.info(nodes.item(i).getTextContent());
				bwFile.append(nodes.item(i).getTextContent());
				bwFile.newLine();
			}			
		} catch (XPathExpressionException e) {
			throw new AssertionError("Error getting policy number in xml file: " + fileName, e);
		}
	}

	public String getAppHostName() {
		String app_host = PropertyProvider.getProperty(TestProperties.APP_HOST); 
		app_host = app_host.substring(0, 12);
		return app_host;
	}
	
	public void createTempFolder(String path) {
		File folder = new File(path); 
		if (folder.mkdirs()) {
			log.info("ADT temp folder " + path + " created");
		}
		else {
			log.info("ADT temp folder " + path + " doesn't exist");
		}
	}
	
	public void deleteTempFolder(String path) {
		File folder = new File(path);
		if (folder.delete()) {
			log.info("ADT temp folder " + path + " deleted");
		}
		else {
			File[] files = folder.listFiles(File::isFile);
			for (File file : files)
                file.delete();
			folder.delete();
			log.info("ADT temp folder " + path + " deleted with all files");
		}
	}
}
