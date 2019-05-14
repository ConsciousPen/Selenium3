package aaa.helpers.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Job{

	private List<String> jobFolders;
	private Map<String, String> jobParameters;
	private String jobName;

	public Job(String jobName){
		this.jobName = jobName;
		jobFolders = new ArrayList<String>();
	}
	
	public Job(String jobName, List<String> jobFolders){
		this.jobName = jobName;
		this.jobFolders = jobFolders;
	}

	public Job(String jobName, Map<String, String> jobParameters){
		this(jobName, jobParameters, new ArrayList<>());
	}

	public Job(String jobName, Map<String, String> jobParameters, List<String> jobFolders){
		this.jobName = jobName;
		this.jobParameters = jobParameters;
		this.jobFolders = jobFolders;
	}

	public void addJobFolder(String folderPath){
		jobFolders.add(folderPath);
	}

	public List<String> getJobFolders(){
		return jobFolders;
	}

	public Map<String, String> getJobParameters() {
		return jobParameters;
	}

	public Job setJobParameters(Map<String, String> jobParameters) {
		this.jobParameters = jobParameters;
		return this;
	}

	public Job addJobParameter(String key, String value){
		if(jobParameters == null){
			jobParameters = new HashMap<>();
		}
		jobParameters.put(key, value);
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("jobFolders", jobFolders)
				.append("jobName", jobName)
				.toString();
	}

	public String getJobName(){
		return jobName;
	}
}