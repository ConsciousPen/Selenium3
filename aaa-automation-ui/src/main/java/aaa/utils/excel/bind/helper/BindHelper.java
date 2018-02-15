package aaa.utils.excel.bind.helper;

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.exceptions.IstfException;

public class BindHelper {
	public static List<Field> getAllAccessibleFields(Class<?> tableClass, boolean onlyTables) {
		List<Field> fields = new ArrayList<>();
		for (Field field : getAllAccessibleFieldsFromThisAndSuperClasses(tableClass)) {
			if (!field.isAnnotationPresent(ExcelTransient.class)) {
				if (onlyTables && !isTableRowField(field)) {
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

	public static Object getAnnotationDefaultValue(Class<? extends Annotation> annotationClass, String methodName) {
		try {
			return annotationClass.getDeclaredMethod(methodName).getDefaultValue();
		} catch (NoSuchMethodException e) {
			throw new IstfException(String.format("\"%1$s\" annotation does not have \"%2$s\" method.", annotationClass.getName(), methodName), e);
		}
	}

	public static boolean isTableRowField(Field field) {
		boolean isTableField = List.class.equals(field.getType());
		assertThat(!isTableField && field.isAnnotationPresent(ExcelTableElement.class))
				.as("\"%1$s\" annotation should be assigned to the \"%2$s\" type only!", ExcelTableElement.class.getName(), List.class.getName()).isFalse();
		return isTableField;
	}

	public static Class<?> getTableRowType(Field tableRowField) {
		assertThat(List.class.equals(tableRowField.getType())).as("Excel Table field has \"%1$s\" type but should be \"%2$s\"", tableRowField.getType(), List.class.getName()).isTrue();
		ParameterizedType parameterizedType = (ParameterizedType) tableRowField.getGenericType();
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}

	public static <T> T getInstance(Class<T> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IstfException(String.format("Failed to create instance of \"%s\" class.", clazz.getName()), e);
		}
	}

	public static void setFieldValue(Field field, Object classInstance, Object value) {
		if (!field.isAccessible()) {
			//TODO-dchubkov: find appropriate setter method and use it for set value
			field.setAccessible(true);
		}
		try {
			//TODO check set null value to primitive type
			field.set(classInstance, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new IstfException(String.format("Unable set value to the field \"%1$s\" in class \"%2$s\"", field.getName(), classInstance.getClass().getName()), e);
		}
	}
}
