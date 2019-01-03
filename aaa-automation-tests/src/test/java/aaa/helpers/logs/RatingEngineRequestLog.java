package aaa.helpers.logs;

import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.utils.excel.bind.ReflectionHelper;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;

public final class RatingEngineRequestLog extends RatingEngineLog {
	RatingEngineRequestLog(String logContent, String logSectionId) {
		super(logContent, logSectionId);
	}

	@SuppressWarnings("unchecked")
	public <P extends OpenLPolicy> P getOpenLPolicyObject(Class<P> openLPolicyClass) {
		log.info("Getting {} openl policy object from log content", openLPolicyClass.getSimpleName());
		String policyMemberName = openLPolicyClass.isAssignableFrom(HomeSSOpenLPolicy.class) ? "p" : "policy";
		JsonObject policyJsonObject = getJsonElement().getAsJsonObject().getAsJsonObject(policyMemberName);
		CustomAssertions.assertThat(policyJsonObject).as("Unable to get policy json object form log content by \"%s\" member name", policyMemberName).isNotNull();
		return (P) getOpenLObject(openLPolicyClass, policyJsonObject);
	}

	private Object getOpenLObject(Class<?> openLClass, JsonObject jsonObject) {
		Object openLObject = ReflectionHelper.getInstance(openLClass);
		for (Field field : ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(openLClass)) {
			Object fieldValue = getOpenLFieldValue(field, jsonObject);
			ReflectionHelper.setFieldValue(field, openLObject, fieldValue);
		}
		return openLObject;
	}

	private Object getOpenLFieldValue(Field openLField, JsonObject jsonObject) {
		if (jsonObject.isJsonNull()) {
			return null;
		}

		if (openLField.isAnnotationPresent(ExcelTransient.class) ||
				openLField.isAnnotationPresent(ExcelColumnElement.class) && openLField.getAnnotation(ExcelColumnElement.class).isPrimaryKey()) {
			return null;
		}

		String openLFieldName = getJsonMemberName(openLField, jsonObject);
		JsonElement jsonElement = jsonObject.get(openLFieldName);
		if (jsonElement.isJsonNull()) {
			return null;
		}

		Object openLFieldValue;
		try {
			if (ReflectionHelper.isTableClassField(openLField)) {
				Class<?> fieldType = ReflectionHelper.getFieldType(openLField);
				if (List.class.isAssignableFrom(openLField.getType())) {
					List<Object> openLFieldValuesList = new ArrayList<>();
					for (JsonElement je : jsonElement.getAsJsonArray()) {
						Object openLObject = getOpenLObject(fieldType, je.getAsJsonObject());
						openLFieldValuesList.add(openLObject);
					}
					openLFieldValue = openLFieldValuesList;
				} else {
					openLFieldValue = getOpenLObject(fieldType, jsonElement.getAsJsonObject());
				}

			} else {
				openLFieldValue = getFieldValue(openLField, jsonElement);
			}
		} catch (Throwable e) {
			throw new IstfException(String.format("Ubable to get value of \"%1$s\" field from JsonElement: %2$s and set it to \"%3$s\" field", openLFieldName, jsonElement, openLField), e);
		}

		return openLFieldValue;
	}

	private String getJsonMemberName(Field openLField, JsonObject jsonObject) {
		String fieldName = openLField.getName();
		if (openLField.isAnnotationPresent(ExcelColumnElement.class) && !openLField.getAnnotation(ExcelColumnElement.class).name().equals(ExcelColumnElement.DEFAULT_COLUMN_NAME)) {
			fieldName = openLField.getAnnotation(ExcelColumnElement.class).name();
		}
		boolean ignoreCase = openLField.isAnnotationPresent(ExcelColumnElement.class) ? openLField.getAnnotation(ExcelColumnElement.class).ignoreCase() : ExcelColumnElement.DEFAULT_CASE_IGNORED;

		for (String memberName : jsonObject.keySet()) {
			if (ignoreCase ? memberName.equalsIgnoreCase(fieldName) : memberName.equals(fieldName)) {
				return memberName;
			}
		}
		throw new IstfException(String.format("There is no member name \"%1$s\" in the \"%2$s\" JsonObject%3$s", fieldName, jsonObject, ignoreCase ? " with case ignored" : ""));
	}

	private Object getFieldValue(Field openLField, JsonElement jsonElement) {
		Object openLFieldValue;
		switch (openLField.getType().getName()) {
			case "java.lang.String":
				openLFieldValue = jsonElement.getAsString();
				break;
			case "java.lang.Boolean":
			case "boolean":
				openLFieldValue = jsonElement.getAsBoolean();
				break;
			case "java.lang.Integer":
			case "int":
				openLFieldValue = jsonElement.getAsInt();
				break;
			case "java.lang.Double":
			case "double":
				openLFieldValue = jsonElement.getAsDouble();
				break;
			case "java.lang.Float":
			case "float":
				openLFieldValue = jsonElement.getAsFloat();
				break;
			case "java.lang.Short":
			case "short":
				openLFieldValue = jsonElement.getAsShort();
				break;
			case "java.lang.Byte":
			case "byte":
				openLFieldValue = jsonElement.getAsByte();
				break;
			case "java.lang.Number":
				openLFieldValue = jsonElement.getAsNumber();
				break;
			case "java.lang.Numbers":
				openLFieldValue = jsonElement.getAsNumber();
				break;
			case "com.exigen.ipb.etcsa.utils.Dollar":
				openLFieldValue = new Dollar(jsonElement.getAsString());
				break;
			case "java.time.LocalDate":
				openLFieldValue = getDateFieldValue(jsonElement, openLField, false);
				break;
			case "java.time.LocalDateTime":
				openLFieldValue = getDateFieldValue(jsonElement, openLField, true);
				break;
			default:
				throw new IstfException(String.format("Unable to get value for field \"%1$s\" of unknown type \"%2$s\"", openLField.getName(), openLField.getType().getName()));
		}
		return openLFieldValue;
	}

	private Object getDateFieldValue(JsonElement je, Field openLField, boolean includeTime) {
		Object openLDateValue = null;
		if (je.isJsonPrimitive() && ((JsonPrimitive) je).isNumber()) {
			long epochMilli = je.getAsLong();
			ZonedDateTime zonedDateTime = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault());
			openLDateValue = includeTime ? zonedDateTime.toLocalDateTime() : zonedDateTime.toLocalDate();
		} else {
			List<String> dateTimeFormatterPatterns = new ArrayList<>();
			if (openLField.isAnnotationPresent(ExcelColumnElement.class) && !ArrayUtils.isEmpty(openLField.getAnnotation(ExcelColumnElement.class).dateFormatPatterns())) {
				dateTimeFormatterPatterns = Arrays.asList(openLField.getAnnotation(ExcelColumnElement.class).dateFormatPatterns());
			} else {
				dateTimeFormatterPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				dateTimeFormatterPatterns.add("MM/dd/yyyy HHmmss");
				dateTimeFormatterPatterns.add("MM/dd/yyyy");
				dateTimeFormatterPatterns.add("yyyyMMdd");
			}

			String dateString = je.getAsString();
			for (String pattern : dateTimeFormatterPatterns) {
				try {
					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
					openLDateValue = includeTime ? LocalDateTime.parse(dateString, dateTimeFormatter) : LocalDate.parse(dateString, dateTimeFormatter);
					break;
				} catch (DateTimeParseException ignore) {
				}
			}
			CustomAssertions.assertThat(openLDateValue).as("Unable to parse %1$s value from \"%2$s\" string for \"%3$s\" openl field",
					includeTime ? LocalDateTime.class.getSimpleName() : LocalDate.class.getSimpleName(), dateString, openLField.getName()).isNotNull();
		}

		return openLDateValue;
	}
}
