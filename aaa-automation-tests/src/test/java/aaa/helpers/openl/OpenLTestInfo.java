package aaa.helpers.openl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.testng.ITestContext;
import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.exceptions.IstfException;

public class OpenLTestInfo<P extends OpenLPolicy> {
	private String openLFilePath;
	private boolean isLocalFile;
	private String openLFileBranch;
	private List<P> openLPolicies;
	private Throwable exception;
	private String customerNumber;
	private ITestContext testContext;

	OpenLTestInfo() {}

	OpenLTestInfo(String openLFilePath, String openLFileBranch, List<P> openLPolicies) {
		this(openLFilePath, false, openLFileBranch, openLPolicies);
	}

	OpenLTestInfo(String openLFilePath, boolean isLocalFile, String openLFileBranch, List<P> openLPolicies) {
		this.openLFilePath = openLFilePath;
		this.isLocalFile = isLocalFile;
		this.openLFileBranch = openLFileBranch;
		this.openLPolicies = new ArrayList<>(openLPolicies);
	}

	public String getOpenLFilePath() {
		return this.openLFilePath;
	}

	void setOpenLFilePath(String openLFilePath) {
		this.openLFilePath = openLFilePath;
	}

	public String getOpenLFileBranch() {
		return openLFileBranch;
	}

	public boolean isLocalFile() {
		return isLocalFile;
	}

	void setLocalFile(boolean isLocalFile) {
		this.isLocalFile = isLocalFile;
	}

	void setOpenLFileBranch(String openLFileBranch) {
		this.openLFileBranch = openLFileBranch;
	}

	public List<P> getOpenLPolicies() {
		return Collections.unmodifiableList(openLPolicies);
	}

	void setOpenLPolicies(List<P> openLPolicies) {
		this.openLPolicies = new ArrayList<>(openLPolicies);
	}

	public boolean isFailed() {
		return this.exception != null;
	}

	public Throwable getException() {
		return this.exception;
	}

	void setException(Throwable exception) {
		this.exception = exception;
	}

	public P getOpenLPolicy(int number) {
		return this.openLPolicies.stream().filter(p -> p.getNumber() == number).findFirst().orElseThrow(() -> new IstfException("There is no policy with number " + number));
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public ITestContext getTestContext() {
		return testContext;
	}

	public void setTestContext(ITestContext testContext) {
		this.testContext = testContext;
	}
}
