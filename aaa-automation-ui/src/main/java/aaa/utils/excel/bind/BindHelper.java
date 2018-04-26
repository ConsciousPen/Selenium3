package aaa.utils.excel.bind;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.exceptions.IstfException;

public class BindHelper {
	public static List<Field> getAllAccessibleFields(Class<?> tableClass, boolean onlyTables) {
		List<Field> fields = new ArrayList<>();
		for (Field field : getAllAccessibleFieldsFromThisAndSuperClasses(tableClass)) {
			if (!field.isAnnotationPresent(ExcelTransient.class)) {
				if (onlyTables && !isTableClassField(field)) {
					continue;
				}
				fields.add(field);
			}
		}
		return fields;
	}

	public static List<Field> getAllAccessibleFieldsFromThisAndSuperClasses(Class<?> tableClass) {
		List<Field> accessibleFields = new ArrayList<>();
		for (Class<?> clazz : getThisAndAllSuperClasses(tableClass)) {
			for (Field field : clazz.getDeclaredFields()) {
				boolean isLocalField = Objects.equals(field.getDeclaringClass(), clazz);
				boolean isPublic = Modifier.isPublic(field.getModifiers());
				boolean isProtected = Modifier.isProtected(field.getModifiers());
				boolean isPackagePrivateAndAccessible =
						!Modifier.isPrivate(field.getModifiers()) && !isPublic && !isProtected && field.getDeclaringClass().getPackage().getName().equals(clazz.getPackage().getName());
				boolean isNotHiddenByChildClassField = accessibleFields.stream().noneMatch(f -> Objects.equals(field.getName(), f.getName()));

				if ((isLocalField || isPublic || isProtected || isPackagePrivateAndAccessible) && isNotHiddenByChildClassField) {
					accessibleFields.add(field);
				}
			}
		}
		return accessibleFields;
	}

	public static List<Class<?>> getThisAndAllSuperClasses(Class<?> clazz) {
		List<Class<?>> allSuperClasses = new ArrayList<>();
		allSuperClasses.add(clazz);
		while (clazz.getClasses() != null && !clazz.getSuperclass().equals(Object.class)) {
			clazz = clazz.getSuperclass();
			allSuperClasses.add(clazz);
		}
		return allSuperClasses;
	}

	public static boolean isTableClassField(Field field) {
		return getTableClass(field).isAnnotationPresent(ExcelTableElement.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getTableClass(Field field) {
		if (List.class.equals(field.getType())) {
			return getGenericTypeClass(field);
		}
		return (Class<T>) field.getType();
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getGenericTypeClass(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		return (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}

	public static DateTimeFormatter[] getFormatters(Field field) {
		List<DateTimeFormatter> dateTimeFormatters = new ArrayList<>();
		if (field.isAnnotationPresent(ExcelColumnElement.class)) {
			for (String datePattern : field.getAnnotation(ExcelColumnElement.class).dateFormatPatterns()) {
				try {
					dateTimeFormatters.add(DateTimeFormatter.ofPattern(datePattern));
				} catch (IllegalArgumentException e) {
					throw new IstfException(String.format("Unable to get valid DateTimeFormatter for field \"%1$s\" with date pattern \"%2$s\"", field.getName(), datePattern), e);
				}
			}
		}
		return dateTimeFormatters.toArray(new DateTimeFormatter[dateTimeFormatters.size()]);
	}
}
