package aaa.modules.adt;

import java.io.File;

import org.testng.annotations.Test;

import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.ssh.Ssh;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public class BatchXmlsProcessingTest { 
	private String doc_host = "aws2aaadoc02.corevelocity.csaa.cloud";	
	private String ssh_user = PropertyProvider.getProperty(TestProperties.SSH_USER);
	private String ssh_password = PropertyProvider.getProperty(TestProperties.SSH_PASSWORD);
	
	//Folders on SFTP
	private String DOCGEN_FOLDER = "/home/mp2/pas/sit/PAS_B_EXGPAS_DCMGMT_6500_D/outbound/"; 
	
	//Local folder
	private String LOCAL_DOCGEN_FOLDER = "src/test/resources/adt/xmls/"; 
	
	//Remote folder on SFTP aws2aaadoc02.corevelocity.csaa.cloud
	private String REMOTE_FOLDER = "/Deloitte/docTesting/Batch/XML/";
	
	@Test
	public void BatchXmlProcessingTest() {
		
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);	
		
		RemoteHelper.downloadBatchFiles(DOCGEN_FOLDER, new File(LOCAL_DOCGEN_FOLDER)); 
		RemoteHelper.closeSession();
		
		//get policy numbers from xmls
		
		uploadBatchFiles();
	}
	
	public void uploadBatchFiles() {
		Ssh ssh = new Ssh(doc_host, ssh_user, ssh_password); 
		File directory = new File(LOCAL_DOCGEN_FOLDER);
		File[] files = directory.listFiles(File::isFile);
		if (files != null && files.length != 0) {
			for (File file : files) {
				ssh.putFile(LOCAL_DOCGEN_FOLDER + file.getName(), REMOTE_FOLDER + file.getName());
			}
		}		
		ssh.closeSession();
	}

}
