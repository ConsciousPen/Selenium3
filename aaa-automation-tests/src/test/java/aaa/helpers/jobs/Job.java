package aaa.helpers.jobs;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Job{

	private List<String> jobFolders;
	private String jobName;

	public Job(String jobName){
		this.jobName = jobName;
		jobFolders = new ArrayList<String>();
	}
	
	public Job(String jobName, List<String> jobFolders){
		this.jobName = jobName;
		this.jobFolders = jobFolders;
	}

	public void addJobFolder(String folderPath){
		jobFolders.add(folderPath);
	}

	public List<String> getJobFolders(){
		return jobFolders;
	}

	public String getJobName(){
		return jobName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("jobFolders", jobFolders)
				.append("jobName", jobName)
				.toString();
	}
}