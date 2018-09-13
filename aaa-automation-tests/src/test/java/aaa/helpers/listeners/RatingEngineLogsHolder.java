package aaa.helpers.listeners;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.ReflectionHelper;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;

public class RatingEngineLogsHolder {
	private static Logger log = LoggerFactory.getLogger(RatingEngineLogsHolder.class);
	private RatingEngineLog requestLog;
	private RatingEngineLog responseLog;

	public RatingEngineLogsHolder() {
		this("", "", "");
	}

	public RatingEngineLogsHolder(String ratingRequestLogContent, String ratingResponseLogContent, String logId) {
		this.requestLog = new RatingEngineLog(ratingRequestLogContent, logId);
		this.responseLog = new RatingEngineLog(ratingResponseLogContent, logId);
	}

	public RatingEngineLog getRequestLog() {
		return requestLog;
	}

	public void setRequestLog(String requestLogContent) {
		this.requestLog = new RatingEngineLog(requestLogContent, "");
	}

	public void setRequestLog(String requestLogContent, String logSectionId) {
		this.requestLog = new RatingEngineLog(requestLogContent, logSectionId);
	}

	public RatingEngineLog getResponseLog() {
		return responseLog;
	}

	public void setResponseLog(String responseLogContent) {
		this.responseLog = new RatingEngineLog(responseLogContent, "");
	}

	public void setResponseLog(String responseLogContent, String logSectionId) {
		this.responseLog = new RatingEngineLog(responseLogContent, logSectionId);
	}

	public void dumpLogs(String requestLogDestinationPath, String responseLogDestinationPath, boolean archiveLogs) {
		getRequestLog().dump(requestLogDestinationPath, archiveLogs);
		getResponseLog().dump(responseLogDestinationPath, archiveLogs);
	}

	public static final class RatingEngineLog {
		private static final String ARCHIVE_EXTENSION = ".zip";
		private String logSectionId;
		private String logContent;
		private String formattedLogContent;
		private JsonElement jsonElement;
		private Map<String, String> openLFieldsMap;

		private RatingEngineLog(String logContent, String logSectionId) {
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
		 * Builds and returns Map of openL fields and values from rating json request/response where key is path to the OpenL field and value - openL field value itself
		 *
		 * @return Map of openL fields and values from rating json request/response where key is path to the OpenL field and value - openL field value itself
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
							this.jsonElement = new JsonNull();
						} finally {
							try {
								reader.close();
							} catch (IOException e) {
								log.error(jsonReaderCloseErrorMessage, e);
							}
						}
					} else {
						log.error(parseErrorMessage, syntaxEx);
						this.jsonElement = new JsonNull();
					}
				} catch (JsonParseException parseEx) {
					log.error(parseErrorMessage, parseEx);
					this.jsonElement = new JsonNull();
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

		@SuppressWarnings("unchecked")
		public <P extends OpenLPolicy> P getOpenLPolicyObject(Class<P> openLPolicyClass) {
			return (P) getOpenLFieldValue(openLPolicyClass, getJsonElement().getAsJsonObject().getAsJsonObject("policy"));
		}

		private Object getFieldValue(Field openLField, JsonObject jsonObject) {
			//TODO-dchubkov: add messages to exceptions
			Object fieldValue = null;

			if (openLField.isAnnotationPresent(ExcelTransient.class) ||
					openLField.isAnnotationPresent(ExcelColumnElement.class) && openLField.getAnnotation(ExcelColumnElement.class).isPrimaryKey()) {
				return null;
			}

			String fieldName = openLField.getName();
			if (ReflectionHelper.isTableClassField(openLField)) {
				Class<?> tableClass = ReflectionHelper.getFieldType(openLField);
				if (List.class.isAssignableFrom(openLField.getType())) {
					List<Object> fieldValueList = new ArrayList<>();
					for (JsonElement je : jsonObject.getAsJsonArray(fieldName)) {
						Object value = getOpenLFieldValue(tableClass, je.getAsJsonObject());
						fieldValueList.add(value);
					}
					fieldValue = fieldValueList;
				} else {
					fieldValue = getOpenLFieldValue(tableClass, jsonObject.getAsJsonObject(fieldName));
				}

			} else {
				if (jsonObject.isJsonNull()) {
					return null;
				}

				JsonElement je = null;
				if (openLField.isAnnotationPresent(ExcelColumnElement.class) && openLField.getAnnotation(ExcelColumnElement.class).ignoreCase()) {
					for (Map.Entry<String, JsonElement> jsonElementEntry : jsonObject.entrySet()) {
						if (jsonElementEntry.getKey().equalsIgnoreCase(fieldName)) {
							je = jsonElementEntry.getValue();
							break;
						}
					}
				} else {
					je = jsonObject.get(fieldName);
				}
				CustomAssertions.assertThat(je).as("fieldName = %s", fieldName).isNotNull();

				if (je.isJsonNull()) {
					return null;
				}
				//TODO-dchubkov: to be refactored...
				switch (openLField.getType().getName()) {
					case "java.lang.String":
						fieldValue = je.getAsString();
						break;
					case "java.lang.Boolean":
					case "java.lang.boolean":
						fieldValue = je.getAsBoolean();
						break;
					case "java.lang.Integer":
					case "java.lang.int":
						fieldValue = je.getAsInt();
						break;
					case "java.lang.Double":
					case "java.lang.double":
						fieldValue = je.getAsDouble();
						break;
					case "java.lang.Float":
					case "java.lang.float":
						fieldValue = je.getAsFloat();
						break;
					case "java.lang.Short":
					case "java.lang.short":
						fieldValue = je.getAsShort();
						break;
					case "java.lang.Byte":
					case "java.lang.byte":
						fieldValue = je.getAsByte();
						break;
					case "java.lang.Number":
						fieldValue = je.getAsNumber();
						break;
					//TODO-dchubkov: to be refactored Dates...
					case "java.time.LocalDate":
						if (je.isJsonPrimitive() && ((JsonPrimitive) je).isNumber()) {
							long epochMilli = je.getAsLong();
							fieldValue = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDate();
						} else {
							List<String> dateTimeFormatterPatterns = new ArrayList<>();
							if (openLField.isAnnotationPresent(ExcelColumnElement.class) && !ArrayUtils.isEmpty(openLField.getAnnotation(ExcelColumnElement.class).dateFormatPatterns())) {
								dateTimeFormatterPatterns = Arrays.asList(openLField.getAnnotation(ExcelColumnElement.class).dateFormatPatterns());
							} else {
								dateTimeFormatterPatterns.add("MM/dd/yyyy");
								dateTimeFormatterPatterns.add("yyyyMMdd");
							}

							for (String pattern : dateTimeFormatterPatterns) {
								try {
									DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
									fieldValue = LocalDate.parse(je.getAsString(), dateTimeFormatter);
									break;
								} catch (DateTimeParseException ignore) {
								}
							}
							CustomAssertions.assertThat(fieldValue).as(">>>>>>>>>>>> fieldValue = %s", fieldValue).isNotNull();
						}

						break;
					//TODO-dchubkov: to be refactored Dates...
					case "java.time.LocalDateTime":
						if (je.isJsonPrimitive() && ((JsonPrimitive) je).isNumber()) {
							long epochMilli = je.getAsLong();
							fieldValue = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
						} else {
							List<String> dateTimeFormatterPatterns = new ArrayList<>();
							if (openLField.isAnnotationPresent(ExcelColumnElement.class) && !ArrayUtils.isEmpty(openLField.getAnnotation(ExcelColumnElement.class).dateFormatPatterns())) {
								dateTimeFormatterPatterns = Arrays.asList(openLField.getAnnotation(ExcelColumnElement.class).dateFormatPatterns());
							} else {
								dateTimeFormatterPatterns.add("MM/dd/yyyy HHmmss");
							}
							for (String pattern : dateTimeFormatterPatterns) {
								try {
									DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
									fieldValue = LocalDateTime.parse(je.getAsString(), dateTimeFormatter);
									break;
								} catch (DateTimeParseException ignore) {
								}
							}
							CustomAssertions.assertThat(fieldValue).as(">>>>>>>>>>>> fieldValue = %s", fieldValue).isNotNull();
						}
						break;
					default:
						throw new IstfException(">>>>>>>>>>>>>>>>>>");
				}

			}
			//TODO-dchubkov: add support for multi columns fields

			return fieldValue;
		}

		private Object getOpenLFieldValue(Class<?> tableClass, JsonObject jsonObject) {
			//Class<?> tableClass = ReflectionHelper.getFieldType(openLField);
			List<Field> tableColumnsFields = ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(tableClass);

			Object tableClassInstance = ReflectionHelper.getInstance(tableClass);
			for (Field tableColumnField : tableColumnsFields) {
				Object value = getFieldValue(tableColumnField, jsonObject);
				ReflectionHelper.setFieldValue(tableColumnField, tableClassInstance, value);
			}
			return tableClassInstance;
		}

		private Map<String, String> getOpenLFieldsMap(JsonElement je, String parentOpenLFieldPath) {
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

		private File makeZip(File logDestination) {
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
}
