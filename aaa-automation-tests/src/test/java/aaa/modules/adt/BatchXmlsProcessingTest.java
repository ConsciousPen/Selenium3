package aaa.modules.adt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
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
	private String DOCGEN_FOLDER = "/home/mp2/pas/sit/PAS_B_EXGPAS_DCMGMT_6500_D/outbound/"; 
	
	//Local folder
	private String LOCAL_DOCGEN_FOLDER = "src/test/resources/adt/xmls/"; 
	private String LOCAL_TXT_FOLDER = "src/test/resources/adt/txt/";
	
	//Remote folder on SFTP aws2aaadoc02.corevelocity.csaa.cloud
	private String REMOTE_FOLDER = "/Deloitte/docTesting/Batch/XML/";
	
	private File policyNumbersFile;
	
	@Test
	public void BatchXmlProcessingTest() {
		
		RemoteHelper.clearFolder(DOCGEN_FOLDER);
		
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);	
		
		RemoteHelper.downloadBatchFiles(DOCGEN_FOLDER, new File(LOCAL_DOCGEN_FOLDER)); 
		RemoteHelper.closeSession();
		
		//get policy numbers from xmls
		createFileWithPolicyNumbers();
		
		uploadFiles(LOCAL_DOCGEN_FOLDER);
		uploadFiles(LOCAL_TXT_FOLDER);
	}
	
	public void uploadFiles(String source) {
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
			if (files != null && files.length != 0) {
				for (File file : files) {
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
}
