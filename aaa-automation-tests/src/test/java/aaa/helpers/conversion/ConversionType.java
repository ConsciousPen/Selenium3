package aaa.helpers.conversion;

import com.exigen.ipb.eisa.utils.batchjob.Job;
import aaa.helpers.jobs.BatchJob;

public enum ConversionType {

	HDES("src/test/resources/conversion/HDES/", "/ipb/import/CaFunctional/import/", "/ipb/import/CaFunctional/response/", BatchJob.aaaImportPolicyHomeCaHdesAsyncTaskJob),
	SIS("src/test/resources/conversion/SIS/", "/ipb/import/Regression/Sis/import/", "/ipb/import/Regression/Sis/response/", BatchJob.aaaImportPolicyHomeCaSisAsyncTaskJob),
	FOXPRO("src/test/resources/conversion/FoxPro/", "/ipb/import/Regression/FoxPro/import/", "/ipb/import/Regression/FoxPro/response/", BatchJob.aaaImportMiniPupPolicyAsyncTaskJob),
	MAIG("src/test/resources/conversion/MAIG/", "/ipb/import/Deloitte/", "/ipb/result/Deloitte/", BatchJob.importMiniPolicyAsyncTaskJob);

	String localTemplatesFolderPath;
	String remoteImportFolderPath;
	String remoteResponseFolderPath;
	Job job;

	ConversionType(String local, String remoteImport, String remoteResponse, Job job) {
		this.localTemplatesFolderPath = local;
		this.remoteImportFolderPath = remoteImport;
		this.remoteResponseFolderPath = remoteResponse;
		this.job = job;
	}

	public String getRemoteImportFolder() {
		return remoteImportFolderPath;
	}

	public String getRemoteResponseFolder() {
		return remoteResponseFolderPath;
	}

	public String getLocalTemplatesFolder() {
		return localTemplatesFolderPath;
	}
	public Job getJob() {
		return job;
	}

}
