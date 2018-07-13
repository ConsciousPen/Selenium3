package aaa.helpers.listeners;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;
import aaa.helpers.openl.model.OpenLPolicy;
import toolkit.exceptions.IstfException;

public class RatingEngineLogsHolder {
	private static final String ARCHIVE_EXTENSION = ".zip";
	private static Logger log = LoggerFactory.getLogger(RatingEngineLogsHolder.class);

	private String ratingRequestLogContent;
	private String ratingResponseLogContent;

	public RatingEngineLogsHolder() {
		this("", "");
	}

	public RatingEngineLogsHolder(String ratingRequestLogContent, String ratingResponseLogContent) {
		this.ratingRequestLogContent = ratingRequestLogContent;
		this.ratingResponseLogContent = ratingResponseLogContent;
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

	public <P extends OpenLPolicy> P getRequestPolicyObject(Class<P> openLPolicyModel) {
		//TODO-dchubkov: to be continued and tested...
		P openLPolicyFromRequest;
		log.info("Getting openl policy object of \"{}\" model from rating json request", openLPolicyModel.getSimpleName());
		try {
			JSONJAXBContext jsonContext = new JSONJAXBContext(JSONConfiguration.natural().humanReadableFormatting(true).build(), openLPolicyModel);
			JSONUnmarshaller jsonUnmarshaller = jsonContext.createJSONUnmarshaller();
			openLPolicyFromRequest = jsonUnmarshaller.unmarshalFromJSON(new StringReader(getRatingRequestLogContent()), openLPolicyModel);
		} catch (JAXBException e) {
			throw new IstfException(String.format("Unable to unmarshal rating json request to openl policy object of \"%s\" model", openLPolicyModel.getSimpleName()), e);
		}
		return openLPolicyFromRequest;
	}

	public File dumpRequestLog(String logDestinationPath, boolean archiveLog) {
		return dumpLog(logDestinationPath, getRatingRequestLogContent(), archiveLog);
	}

	public File dumpResponseLog(String logDestinationPath, boolean archiveLog) {
		return dumpLog(logDestinationPath, getRatingResponseLogContent(), archiveLog);
	}

	public void dumpLogs(String requestLogDestinationPath, String responseLogDestinationPath, boolean archiveLogs) {
		dumpRequestLog(requestLogDestinationPath, archiveLogs);
		dumpResponseLog(responseLogDestinationPath, archiveLogs);
	}

	private File dumpLog(String logDestinationPath, String logContent, boolean archiveLog) {
		log.info("Saving log content to the \"{}\" file{}", logDestinationPath, archiveLog ? " with archiving" : "");
		if (StringUtils.isBlank(logContent)) {
			log.error("Log content is empty, saving to the \"{}\" file has been aborted", logDestinationPath);
			return null;
		}

		File logDestination = new File(logDestinationPath);
		if (!logDestination.getParentFile().exists() && !logDestination.getParentFile().mkdirs()) {
			log.error("Unable to create \"{}\" directory, log file saving has been aborted", logDestination.getParentFile());
			return null;
		}

		String formattedLogContent;
		try {
			JsonElement je = new JsonParser().parse(logContent);
			formattedLogContent = new GsonBuilder().setPrettyPrinting().create().toJson(je);
		} catch (JsonParseException e) {
			log.error("Unable to parse and convert log content to pretty json format", e);
			return null;
		}

		try {
			Files.write(Paths.get(logDestinationPath.toString()), formattedLogContent.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			log.error(String.format("Unable to save log content to the \"%s\" file", logDestinationPath), e);
			return null;
		}

		if (archiveLog) {
			File zippedLogDestination = makeZip(logDestination);
			if (!logDestination.delete()) {
				log.warn("Unable to delete \"{}\" original file after archiving", logDestinationPath);
			}
			logDestination = zippedLogDestination;
		}

		return logDestination;
	}

	private File makeZip(File logFile) {
		String archivedLogFilePath = logFile.getAbsolutePath() + ARCHIVE_EXTENSION;

		log.info("Making \"{}\" archive", archivedLogFilePath);
		try (FileOutputStream fos = new FileOutputStream(logFile.getAbsolutePath() + ARCHIVE_EXTENSION);
				ZipOutputStream zipOut = new ZipOutputStream(fos);
				FileInputStream fis = new FileInputStream(logFile)) {

			ZipEntry zipEntry = new ZipEntry(logFile.getName());
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
		} catch (IOException e) {
			log.error(String.format("Unable to create \"%s\" archive", archivedLogFilePath), e);
		}

		return new File(archivedLogFilePath);
	}

}
