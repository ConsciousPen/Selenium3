package aaa.helpers.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.main.modules.policy.PolicyType;

public class VinUploadHelper {

	private String policyType;
	private String state;
	protected static Logger log = LoggerFactory.getLogger(VinUploadHelper.class);

	public VinUploadHelper(PolicyType policyType, String state) {
		this.policyType = policyType.getShortName();
		this.state = state;
	}

	public String getSpecificUploadFile(String fileType) {
		String defaultFileName = null;
		switch (policyType) {
			case "AutoSS":
				defaultFileName = "%s_%s_SS.xlsx";
				break;
			case "AutoCA":
				defaultFileName = "%s_%s_SELECT.xlsx";
				break;
			case "AutoCAC":
				defaultFileName = "%s_%s_CHOICE.xlsx";
				break;
			default:
				throw new IllegalArgumentException("Name of VIN Table file was not selected correctly");
		}
		return String.format(defaultFileName, fileType, state);
	}

	public String getControlTableFile() {
		String defaultControlFileName = null;
		switch (policyType) {
			case "AutoSS":
				defaultControlFileName = "controlTable_%s_SS.xlsx";
				break;
			case "AutoCA":
				defaultControlFileName = "controlTable_%s_SELECT.xlsx";
				break;
			case "AutoCAC":
				defaultControlFileName = "controlTable_%s_CHOICE.xlsx";
				break;
			default:
				throw new IllegalArgumentException("Name of VIN Table file was not selected correctly");
		}
		return String.format(defaultControlFileName, state);
	}
}
