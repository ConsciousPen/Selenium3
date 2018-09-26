package aaa.helpers.logs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import toolkit.exceptions.IstfException;

public class RatingEngineLog {
	private static final String ARCHIVE_EXTENSION = ".zip";
	protected static Logger log = LoggerFactory.getLogger(RatingEngineLog.class);

	private String logSectionId;
	private String logContent;
	private String formattedLogContent;
	private JsonElement jsonElement;
	private Map<String, String> openLFieldsMap;

	RatingEngineLog(String logContent, String logSectionId) {
		if (logContent == null) {
			throw new IstfException("Unable to create RatingEngineLog object with null log content");
		}
		this.logContent = logContent;
		this.logSectionId = logSectionId;
	}

	/**
	 * Returns rating engine log section id separated by "--------------------------------------"
	 *
	 * @return rating engine log section id
	 */
	public String getLogSectionId() {
		return logSectionId;
	}

	/**
	 * Returns rating engine log content
	 *
	 * @return log content as it is
	 */
	public String getLogContent() {
		return logContent;
	}

	/**
	 * Returns rating engine log content in pretty json format
	 *
	 * @return rating engine log content in pretty json format
	 */
	public String getFormattedLogContent() {
		if (this.formattedLogContent == null) {
			log.info("Getting log content in pretty json format");
			JsonElement je = getJsonElement();
			if (!je.isJsonNull()) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				try {
					this.formattedLogContent = gson.toJson(je);
				} catch (JsonIOException e) {
					log.error("Error occurs while converting log content to pretty json format, returning raw log instead of formatted version", e);
					this.formattedLogContent = getLogContent();
				}
			} else {
				log.warn("Unable to get pretty json format from 'null' JsonElement, returning raw log content instead of formatted version");
				this.formattedLogContent = getLogContent();
			}
		}

		return this.formattedLogContent;
	}

	/**
	 * Saves rating engine log to the filesystem
	 *
	 * @param logDestinationPath rating log file absolute path where it should be saved
	 * @param archiveLog if true - make zip archive of rating log file
	 *
	 * @return File object of saved log
	 */
	public File dump(String logDestinationPath, boolean archiveLog) {
		log.info("Saving log content to the \"{}\" file{}", logDestinationPath, archiveLog ? " with archiving" : "");
		if (StringUtils.isBlank(getLogContent())) {
			log.error("Log content is empty, saving to the \"{}\" file has been aborted", logDestinationPath);
			return null;
		}

		File logDestination = new File(logDestinationPath);
		if (!logDestination.getParentFile().exists() && !logDestination.getParentFile().mkdirs()) {
			log.error("Unable to create \"{}\" directory, log file saving has been aborted", logDestination.getParentFile());
			return null;
		}

		String formattedLogContent = getFormattedLogContent();
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

	/**
	 * Builds and returns Map of OpenL fields and values from rating json request/response where key is path to the OpenL field and value - OpenL field value itself
	 *
	 * @return Map of OpenL fields and values from rating json request/response where key is path to the OpenL field and value - OpenL field value itself
	 */
	public Map<String, String> getOpenLFieldsMap() {
		if (this.openLFieldsMap == null) {
			log.info("Getting OpenL fields paths and values map from log");
			this.openLFieldsMap = getOpenLFieldsMap(getJsonElement(), null);
		}
		return Collections.unmodifiableMap(this.openLFieldsMap);
	}

	/**
	 * Returns JsonElement object from rating json request/response
	 *
	 * @return JsonElement object from rating json request/response
	 */
	public JsonElement getJsonElement() {
		if (this.jsonElement == null) {
			String parseErrorMessage = "Error occurs while parsing log content";
			String jsonReaderCloseErrorMessage = "Error occurs while closing JsonReader";

			log.info("Getting root Json Element object from log content");
			JsonReader reader = new JsonReader(new StringReader(getLogContent()));
			try {
				this.jsonElement = new JsonParser().parse(reader);
			} catch (JsonSyntaxException syntaxEx) {
				if (syntaxEx.getMessage().contains("Use JsonReader.setLenient(true) to accept malformed JSON")) {
					log.error(parseErrorMessage, syntaxEx);
					log.info("Trying to parse log with enabled \"setLenient(true)\" in JsonReader", getLogContent());
					reader.setLenient(true);
					try {
						this.jsonElement = new JsonParser().parse(reader);
					} catch (JsonParseException parseEx) {
						log.error(String.format("%s with enabled \"setLenient(true)\" in JsonReader", parseErrorMessage), parseEx);
						this.jsonElement = JsonNull.INSTANCE;
					} finally {
						try {
							reader.close();
						} catch (IOException e) {
							log.error(jsonReaderCloseErrorMessage, e);
						}
					}
				} else {
					log.error(parseErrorMessage, syntaxEx);
					this.jsonElement = JsonNull.INSTANCE;
				}
			} catch (JsonParseException parseEx) {
				log.error(parseErrorMessage, parseEx);
				this.jsonElement = JsonNull.INSTANCE;
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					log.error(jsonReaderCloseErrorMessage, e);
				}
			}
		}

		return this.jsonElement;
	}

	protected Map<String, String> getOpenLFieldsMap(JsonElement je, String parentOpenLFieldPath) {
		Map<String, String> openLFieldsMap = new LinkedHashMap<>();

		if (je.isJsonNull() && parentOpenLFieldPath != null) {
			openLFieldsMap.put(parentOpenLFieldPath, String.valueOf(je.getAsJsonNull()));
		} else if (je.isJsonPrimitive() && parentOpenLFieldPath != null) {
			String value = String.valueOf(je.getAsJsonPrimitive());
			value = StringUtils.removeStart(value, "\"");
			value = StringUtils.removeEnd(value, "\"");
			openLFieldsMap.put(parentOpenLFieldPath, value);
		} else if (je.isJsonObject()) {
			for (Map.Entry<String, JsonElement> childElement : je.getAsJsonObject().entrySet()) {
				String openLFieldPath = parentOpenLFieldPath != null ? parentOpenLFieldPath + "." + childElement.getKey() : childElement.getKey();
				openLFieldsMap.putAll(getOpenLFieldsMap(childElement.getValue(), openLFieldPath));
			}
		} else if (je.isJsonArray()) {
			for (int i = 0; i < je.getAsJsonArray().size(); i++) {
				String openLFieldPath = parentOpenLFieldPath != null ? parentOpenLFieldPath + "[" + i + "]" : "[" + i + "]";
				openLFieldsMap.putAll(getOpenLFieldsMap(je.getAsJsonArray().get(i), openLFieldPath));
			}
		}
		return openLFieldsMap;
	}

	protected File makeZip(File logDestination) {
		String archivedLogFilePath = logDestination.getAbsolutePath() + ARCHIVE_EXTENSION;

		log.info("Making \"{}\" archive", archivedLogFilePath);
		try (FileOutputStream fos = new FileOutputStream(logDestination.getAbsolutePath() + ARCHIVE_EXTENSION);
				ZipOutputStream zipOut = new ZipOutputStream(fos);
				FileInputStream fis = new FileInputStream(logDestination)) {

			ZipEntry zipEntry = new ZipEntry(logDestination.getName());
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
