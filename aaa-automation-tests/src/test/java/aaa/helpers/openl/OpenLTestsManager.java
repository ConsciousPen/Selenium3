package aaa.helpers.openl;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.sun.jersey.api.client.ClientResponse;
import aaa.config.CsaaTestProperties;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLTest;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLPolicy;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLPolicy;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLPolicy;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLPolicy;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.excel.bind.ExcelUnmarshaller;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.teststoragex.utils.TestNGUtils;

public final class OpenLTestsManager {
	private static final Logger log = LoggerFactory.getLogger(OpenLTestsManager.class);
	private List<OpenLTestInfo<? extends OpenLPolicy>> openLTests;

	public OpenLTestsManager(ITestContext context) {
		XmlSuite parentSuite = TestNGUtils.getRootSuite(context.getSuite());
		List<XmlSuite> openLSuites = getAllChildSuites(parentSuite);
		if (!parentSuite.getTests().isEmpty()) {
			openLSuites.add(parentSuite);
		}
		this.openLTests = getOpenLTests(openLSuites);
	}

	public void updateMocks() {
		MocksCollection commonRequiredMocks = new MocksCollection();
		for (OpenLTestInfo<? extends OpenLPolicy> testInfo : this.openLTests) {
			if (!testInfo.isFailed()) {
				try {
					for (OpenLPolicy policy : testInfo.getOpenLPolicies()) {
						MocksCollection requiredMocks = policy.getRequiredMocks();
						if (requiredMocks != null && !requiredMocks.isEmpty()) {
							log.info("Mocks has been generated for test with policy number {} from \"{}\" file:\n{}", policy.getNumber(), testInfo.getOpenLFilePath(), requiredMocks);
							commonRequiredMocks.addAll(requiredMocks);
						}
					}
				} catch (Throwable e) {
					testInfo.setException(e);
				}
			}
		}

		if (commonRequiredMocks.isEmpty()) {
			log.info("Application server has all required mocks");
		} else {
			log.info("Application server has missed test specific mocks");
			ApplicationMocksManager.updateMocks(commonRequiredMocks);
		}
	}

	@SuppressWarnings("unchecked")
	public <P extends OpenLPolicy> P getOpenLPolicy(String filePath, int policyNumber) {
		return (P) getTestInfo(filePath).getOpenLPolicy(policyNumber);
	}

	public <P extends OpenLPolicy> OpenLTestInfo<P> getTestInfo(ITestContext context) {
		return getTestInfo(getFilePath(context.getCurrentXmlTest()));
	}

	@SuppressWarnings("unchecked")
	public <P extends OpenLPolicy> OpenLTestInfo<P> getTestInfo(String filePath) {
		return (OpenLTestInfo<P>) this.openLTests.stream().filter(t -> Objects.equals(t.getOpenLFilePath(), filePath)).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no OpenLTestInfo object with \"%s\" filePath", filePath)));
	}

	public static String getFilePath(XmlTest test) {
		return FilenameUtils.separatorsToUnix(Paths.get(TestParams.TESTS_DIR.getValue(test), TestParams.TEST_FILENAME.getValue(test)).normalize().toString());
	}

	private List<OpenLTestInfo<? extends OpenLPolicy>> getOpenLTests(List<XmlSuite> openLSuites) {
		List<OpenLTestInfo<? extends OpenLPolicy>> openLTests = new ArrayList<>();

		for (XmlSuite suite : openLSuites) {
			for (XmlTest test : suite.getTests()) {
				//TODO-dchubkov: try to split OpenLPolicy objects creation to multi threads (just several ones due to huge memory consumption)
				OpenLTestInfo<? extends OpenLPolicy> testInfo = new OpenLTestInfo<>();
				try {
					testInfo.setOpenLFileBranch(TestParams.TESTS_BRANCH.getValue(test));
					testInfo.setOpenLFilePath(getFilePath(test));
					testInfo.setOpenLPolicies(getOpenLPolicies(test));
				} catch (Throwable e) {
					testInfo.setException(e);
				}

				openLTests.add(testInfo);
			}
		}
		return openLTests;
	}

	private <P extends OpenLPolicy> List<P> getOpenLPolicies(XmlTest test) throws Throwable {
		String filePath = getFilePath(test);
		File openLFile;

		boolean deleteFileAfterUnmarshalling = false;
		if (Boolean.valueOf(TestParams.LOCAL_TESTS.getValue(test))) {
			openLFile = new File(filePath);
		} else {
			openLFile = downloadOpenLFile(filePath, TestParams.TESTS_BRANCH.getValue(test));
			deleteFileAfterUnmarshalling = true;
		}

		List<P> openLPolicies;
		List<OpenLTest> openLTests;
		List<CellType<?>> cellTypes = Arrays.asList(ExcelCell.INTEGER_TYPE, ExcelCell.DOUBLE_TYPE, ExcelCell.BOOLEAN_TYPE, ExcelCell.LOCAL_DATE_TYPE, ExcelCell.STRING_TYPE, ExcelCell.DOLLAR_CELL_TYPE);
		List<Integer> policyNumbers = parsePolicyNumbers(TestParams.POLICY_NUMBERS.getValue(test));
		Class<P> openLPolicyModel = OpenLPolicyType.of(test).getOpenLPolicyModel();

		log.info("Getting OpenLPolicy objects from \"{}\" file", openLFile);
		try (ExcelUnmarshaller excelUnmarshaller = new ExcelUnmarshaller(openLFile, false, cellTypes)) {
			openLPolicies = excelUnmarshaller.unmarshalRows(openLPolicyModel, policyNumbers);
			openLTests = excelUnmarshaller.unmarshalRows(OpenLTest.class, policyNumbers);
		} finally {
			if (deleteFileAfterUnmarshalling) {
				deleteTempFile(openLFile);
			}
		}

		for (OpenLPolicy policy : openLPolicies) {
			OpenLTest openLTest = openLTests.stream().filter(t -> Objects.equals(t.getPolicy(), policy.getNumber())).findFirst()
					.orElseThrow(() -> new IstfException("There is no test for policy number " + policy.getNumber()));
			Dollar expectedPremium = policy.getTerm() == 6 ? openLTest.getTotalPremium().divide(2) : openLTest.getTotalPremium();
			policy.setExpectedPremium(expectedPremium);
			if (policy.getState() == null) {
				policy.setState(openLTest.getState());
			}
		}

		return openLPolicies;
	}

	private File downloadOpenLFile(String filePath, String branchName) throws Throwable {
		String authString = PropertyProvider.getProperty(CsaaTestProperties.RATING_REPO_USER) + ":" + PropertyProvider.getProperty(CsaaTestProperties.RATING_REPO_PASSWORD);
		String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
		String url = "https://csaa-insurance.aaa.com/bb/rest/api/1.0/projects/PAS/repos/pas-rating/raw/" + filePath + "?at=" + URLEncoder.encode("refs/heads/" + branchName, "UTF-8");

		int inputStreamReadAttempt = 1;
		int maxInputStreamAttemptsNumber = 5;
		while (inputStreamReadAttempt <= maxInputStreamAttemptsNumber) {
			Response response = ClientBuilder.newClient()
					.target(url)
					.request()
					.header("Authorization", "Basic " + encodedAuthString)
					.get();

			if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
				String responseMessage = "unknown";
				try {
					//TODO-dchubkov: make error class instead of String.class for Errors?
					responseMessage = response.readEntity(String.class);
				} catch (RuntimeException ignore) {
				}

				throw new IstfException(String.format("Error occurs while reading file from URL \"%1$s\". Status %2$s, reason \"%3$s\", response mesage: \"%4$s\"",
						url, response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase(), responseMessage));
			}

			File openLFile = new File("src/test/resources/openl/TEMP_" + System.currentTimeMillis() + "_" + FilenameUtils.getName(filePath));
			log.info("Downloading \"{}\" openL file and saving it to \"{}\" temp file", filePath, openLFile);
			if (!openLFile.getParentFile().exists() && !openLFile.getParentFile().mkdirs()) {
				log.warn("Unable to create \"{}\" folder", openLFile.getParentFile());
			}

			try (InputStream is = response.readEntity(InputStream.class)) {
				Files.copy(is, openLFile.toPath());
				return openLFile;
			} catch (IOException e) {
				if ("Premature EOF".equals(e.getMessage()) || "Connection reset".equals(e.getMessage())) {
					inputStreamReadAttempt++;
					if (inputStreamReadAttempt <= maxInputStreamAttemptsNumber) {
						int sleepSeconds = 2 * inputStreamReadAttempt;
						log.warn("There was a \"{}\" error while reading from an input stream, retry attempt #{} of {} max attempts will be performed after {} seconds",
								e.getMessage(), inputStreamReadAttempt, maxInputStreamAttemptsNumber, sleepSeconds);
						deleteTempFile(openLFile);
						try {
							TimeUnit.SECONDS.sleep(sleepSeconds);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

					}
				} else {
					deleteTempFile(openLFile);
					throw new IstfException(String.format("Error occurs while reading from an input stream to a \"%s\" temp file", openLFile), e);
				}
			}
		}

		throw new IstfException(String.format("Reading from an input stream of \"%1$s\" file has been failed after %2$s attempts", filePath, maxInputStreamAttemptsNumber));
	}

	private void deleteTempFile(File openLFile) {
		if (openLFile != null && openLFile.exists() && !openLFile.delete()) {
			log.error("Unable to delete openL temp file: {}", openLFile);
		}
	}

	/**
	 * Get policy numbers list to be tested
	 *
	 * @param value String of policy numbers separated with "," character
	 * @return list of policy numbers to be tested or empty list if {@code policies} argument is empty string.
	 *
	 */
	private List<Integer> parsePolicyNumbers(String value) {
		if (StringUtils.isBlank(value)) {
			return Collections.emptyList();
		}
		String[] policyNumberStrings = value.split(",");
		List<Integer> policyNumbers = new ArrayList<>(policyNumberStrings.length);
		for (String p : policyNumberStrings) {
			int policyNumber;
			try {
				policyNumber = Integer.parseInt(p.trim());
			} catch (NumberFormatException e) {
				throw new IstfException(String.format("Unable get policy number from \"%s\" string.", p), e);
			}
			policyNumbers.add(policyNumber);
		}
		return policyNumbers;
	}

	private List<XmlSuite> getAllChildSuites(XmlSuite parentSuite) {
		List<XmlSuite> openLSuites = new ArrayList<>();
		for (XmlSuite suite : parentSuite.getChildSuites()) {
			if (!suite.getTests().isEmpty()) {
				openLSuites.add(suite);
			}
			openLSuites.addAll(getAllChildSuites(suite));
		}
		return openLSuites;
	}

	private enum TestParams {
		TESTS_DIR("testsDir", null, true, ""),
		LOCAL_TESTS("localTests", null, false, "false"), TESTS_BRANCH("testsBranch", CsaaTestProperties.RATING_REPO_BRANCH, false, "master"),
		TEST_FILENAME("fileName", null, true, ""),
		POLICY_TYPE("policyType", null, true, ""),
		POLICY_NUMBERS("policyNumbers", null, false, "");

		private final String nameInXml;
		private final String nameInConfig;
		private final boolean isMandatory;
		private final String defaultValue;

		TestParams(String nameInXml, String nameInConfig, boolean isMandatory, String defaultValue) {
			this.nameInXml = nameInXml;
			this.nameInConfig = nameInConfig;
			this.isMandatory = isMandatory;
			this.defaultValue = defaultValue;
		}

		public String getNameInXml() {
			return nameInXml;
		}

		public String getNameInConfig() {
			return nameInConfig;
		}

		public boolean isMandatory() {
			return isMandatory;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public String getValue(XmlTest test) {
			String value = test.getParameter(getNameInXml());
			if (StringUtils.isNotBlank(getNameInConfig())) {
				value = PropertyProvider.getProperty(getNameInConfig(), value);
			}

			if (isMandatory()) {
				assertThat(value).as("There is no \"%1$s\" parameter neither in test \"%2$s\" nor in its suite \"%3$s\"", getNameInXml(), test.getName(), test.getSuite().getFileName()).isNotNull();
			}
			if (StringUtils.isBlank(value)) {
				return getDefaultValue();
			}
			return value;
		}
	}

	private enum OpenLPolicyType {
		AUTO_CA_SELECT(PolicyType.AUTO_CA_SELECT.getShortName(), AutoCaSelectOpenLPolicy.class),
		AUTO_CA_CHOICE(PolicyType.AUTO_CA_CHOICE.getShortName(), AutoCaChoiceOpenLPolicy.class),
		AUTO_SS(PolicyType.AUTO_SS.getShortName(), AutoSSOpenLPolicy.class),
		HOME_SS("HomeSS", HomeSSOpenLPolicy.class),
		HOME_SS_HO3(PolicyType.HOME_SS_HO3.getShortName(), HomeSSOpenLPolicy.class),
		HOME_SS_HO4(PolicyType.HOME_SS_HO4.getShortName(), HomeSSOpenLPolicy.class),
		HOME_SS_HO6(PolicyType.HOME_SS_HO6.getShortName(), HomeSSOpenLPolicy.class),
		HOME_SS_DP3(PolicyType.HOME_SS_DP3.getShortName(), HomeSSOpenLPolicy.class),
		HOME_CA_HO3(PolicyType.HOME_CA_HO3.getShortName(), HomeCaHO3OpenLPolicy.class),
		HOME_CA_HO4(PolicyType.HOME_CA_HO4.getShortName(), HomeCaHO4OpenLPolicy.class),
		HOME_CA_HO6(PolicyType.HOME_CA_HO6.getShortName(), HomeCaHO6OpenLPolicy.class),
		HOME_CA_DP3(PolicyType.HOME_CA_DP3.getShortName(), HomeCaDP3OpenLPolicy.class),
		PUP(PolicyType.PUP.getShortName(), PUPOpenLPolicy.class);

		private final String name;
		private final Class<? extends OpenLPolicy> openLPolicyModel;

		OpenLPolicyType(String name, Class<? extends OpenLPolicy> openLPolicyModel) {
			this.name = name;
			this.openLPolicyModel = openLPolicyModel;
		}

		public String getName() {
			return name;
		}

		@SuppressWarnings("unchecked")
		public <P extends OpenLPolicy> Class<P> getOpenLPolicyModel() {
			return (Class<P>) openLPolicyModel;
		}

		public static OpenLPolicyType of(XmlTest test) {
			String typeName = TestParams.POLICY_TYPE.getValue(test);
			OpenLPolicyType type = of(typeName);

			assertThat(type).as("\"%1$s\" parameter has unknown value \"%2$s\" in test \"%3$s\" within suite \"%4$s\". Only these policy type values are allowed: %5$s",
					TestParams.POLICY_TYPE.getNameInXml(), typeName, test.getName(), test.getSuite().getFileName(), Arrays.stream(OpenLPolicyType.values()).map(OpenLPolicyType::getName).collect(Collectors.toList()))
					.isNotNull();
			return type;
		}

		private static OpenLPolicyType of(String name) {
			return Arrays.stream(OpenLPolicyType.values()).filter(type -> type.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		}
	}
}
