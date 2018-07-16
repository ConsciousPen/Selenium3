package aaa.helpers.listeners;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
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

		String formattedLogContent = convertToPrettyJsonFormat(logContent);
		if (formattedLogContent == null) {
			log.warn("Converting to the pretty json format has been failed, log content will be saved as it is");
			formattedLogContent = logContent;
		}

		try {
			Files.write(logDestination.toPath(), formattedLogContent.getBytes(), StandardOpenOption.CREATE);
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

	private String convertToPrettyJsonFormat(String logContent) {
		String formattedLogContent;
		String parseErrorMessage = "Error occurs while parsing log content";
		JsonReader reader = new JsonReader(new StringReader(logContent));
		JsonElement je;

		log.info("Converting log content to the pretty json format");
		try {
			je = new JsonParser().parse(reader);
		} catch (JsonSyntaxException syntaxEx) {
			if (syntaxEx.getMessage().contains("Use JsonReader.setLenient(true) to accept malformed JSON")) {
				printJsonErrorAndLogContent(parseErrorMessage, syntaxEx, logContent);
				log.info("Trying to parse log with enabled \"setLenient(true)\" in JsonReader", logContent);
				reader.setLenient(true);
				try {
					je = new JsonParser().parse(reader);
				} catch (JsonParseException parseEx) {
					printJsonErrorAndLogContent(parseErrorMessage + " with enabled \"setLenient(true)\" in JsonReader", parseEx, logContent);
					return null;
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						log.error("Error occurs while closing JsonReader", e);
					}
				}
			} else {
				printJsonErrorAndLogContent(parseErrorMessage, syntaxEx, logContent);
				return null;
			}
		} catch (JsonParseException parseEx) {
			printJsonErrorAndLogContent(parseErrorMessage, parseEx, logContent);
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				log.error("Error occurs while closing JsonReader", e);
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			formattedLogContent = gson.toJson(je);
		} catch (JsonIOException e) {
			printJsonErrorAndLogContent("Error occurs while converting log content to pretty json format", e, logContent);
			return null;
		}

		return formattedLogContent;
	}

	private void printJsonErrorAndLogContent(String errorMessage, Throwable t, String logContent) {
		log.error(errorMessage, t);
		log.info("Log content is:\n{}\n", logContent);
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
