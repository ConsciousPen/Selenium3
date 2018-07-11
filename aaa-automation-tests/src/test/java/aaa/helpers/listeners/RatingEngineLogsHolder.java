package aaa.helpers.listeners;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.xml.XmlTest;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;
import aaa.helpers.openl.OpenLTestsManager;
import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.exceptions.IstfException;
import toolkit.utils.logging.CustomLogger;

public class RatingEngineLogsHolder {
	private static final String LOG_FILE_POSTFIX = ".log";
	private static final String OPENL_LOGS_ROOT_FOLDER_PATH = CustomLogger.getLogDirectory() + File.separator + "openl";
	private static Logger log = LoggerFactory.getLogger(RatingEngineLogsHolder.class);

	private String ratingRequestLogContent;
	private String ratingResponseLogContent;
	private File ratingRequestLog;
	private File ratingResponseLog;

	public RatingEngineLogsHolder() {
		this("", "", null, null);
	}

	public RatingEngineLogsHolder(String ratingRequestLogContent, String ratingResponseLogContent, File ratingRequestLog, File ratingResponseLog) {
		this.ratingRequestLogContent = ratingRequestLogContent;
		this.ratingResponseLogContent = ratingResponseLogContent;
		this.ratingRequestLog = ratingRequestLog;
		this.ratingResponseLog = ratingResponseLog;
	}

	public String getRatingRequestLogContent() {
		return ratingRequestLogContent;
	}

	public void setRatingRequestLogContent(String ratingRequestLogContent) {
		this.ratingRequestLogContent = ratingRequestLogContent;
	}

	public String getRatingResponseLogContent() {
		return ratingResponseLogContent;
	}

	public void setRatingResponseLogContent(String ratingResponseLogContent) {
		this.ratingResponseLogContent = ratingResponseLogContent;
	}

	public File getRatingRequestLog() {
		return ratingRequestLog;
	}

	public void setRatingRequestLog(File ratingRequestLog) {
		this.ratingRequestLog = ratingRequestLog;
	}

	public File getRatingResponseLog() {
		return ratingResponseLog;
	}

	public void setRatingResponseLog(File ratingResponseLog) {
		this.ratingResponseLog = ratingResponseLog;
	}

	public <P extends OpenLPolicy> P getRequestPolicyObject(Class<P> policyObjectModel) {
		//TODO-dchubkov: to be tested...
		P openLPolicyFromRequest;
		try {
			JSONJAXBContext jsonContext = new JSONJAXBContext(JSONConfiguration.natural().humanReadableFormatting(true).build(), policyObjectModel);
			JSONUnmarshaller jsonUnmarshaller = jsonContext.createJSONUnmarshaller();
			openLPolicyFromRequest = jsonUnmarshaller.unmarshalFromJSON(new StringReader(getRatingRequestLogContent()), policyObjectModel);
		} catch (JAXBException e) {
			throw new IstfException("Unable to unmarshal..............", e);
		}
		return openLPolicyFromRequest;
	}

	public File dumpRequestLog(XmlTest openLTest, int openLPolicyNumber, boolean archiveLog) {
		File logDestination = Paths.get(OPENL_LOGS_ROOT_FOLDER_PATH, OpenLTestsManager.getFilePath(openLTest), "_" + openLPolicyNumber + "_request" + LOG_FILE_POSTFIX)
				.normalize().toFile();
		return dumpRequestLog(logDestination, archiveLog);
	}

	public File dumpRequestLog(File logDestination, boolean archiveLog) {
		return dumpLog(logDestination, getRatingRequestLogContent(), archiveLog);
	}

	public File dumpResponseLog(XmlTest openLTest, int openLPolicyNumber, boolean archiveLog) {
		File logDestination = Paths.get(OPENL_LOGS_ROOT_FOLDER_PATH, OpenLTestsManager.getFilePath(openLTest), "_" + openLPolicyNumber + "_response" + LOG_FILE_POSTFIX)
				.normalize().toFile();
		return dumpRequestLog(logDestination, archiveLog);
	}

	public File dumpResponseLog(File logDestination, boolean archiveLog) {
		return dumpLog(logDestination, getRatingResponseLogContent(), archiveLog);
	}

	public void dumpLogs(XmlTest openLTest, int openLPolicyNumber, boolean archiveLog) {
		dumpRequestLog(openLTest, openLPolicyNumber, archiveLog);
		dumpRequestLog(openLTest, openLPolicyNumber, archiveLog);
	}

	private File dumpLog(File logDestination, String logContent, boolean archiveLog) {
		if (logDestination.getParentFile().mkdirs()) {
			log.info("Directory \"{}\" was created", logDestination.getAbsolutePath());
		}
		try {
			Files.write(Paths.get(logDestination.toString()), logContent.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new IstfException(".........................");
		}

		if (archiveLog) {
			logDestination = makeZip(logDestination);
		}
		return logDestination;
	}

	private File makeZip(File log) {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		URI uri = URI.create("jar:file:" + log.toPath() + ".zip");
		Path zippedLogPath = null;

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
			//Path externalTxtFile = Paths.get("/codeSamples/zipfs/SomeTextFile.txt");
			Path pathInZipfile = zipfs.getPath(log.getName());
			// copy a file into the zip file
			zippedLogPath = Files.copy(log.toPath(), pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IstfException(".........................");
		}

		return zippedLogPath.toFile();
	}
}
