package com.example.mirai.libraries.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class ReflectionUtil {
	public static boolean compareDetachedAndAttachedObjectsForField(Object detachedEntity, Object managedEntity, String fieldName) {
		try {
			Field field = getFieldFromClassHierarchy(detachedEntity, fieldName);
			Method method = getGetterMethod(managedEntity, fieldName);
			if (field == null || method == null) {

				return false;
			}
			if (field.get(detachedEntity) == null && method.invoke(managedEntity) == null) {
				return true;
			}


			return Objects.deepEquals(field.get(detachedEntity), method.invoke(managedEntity));
		}
		catch (Exception e) {

			log.warn(e.getMessage());
		}
		return false;
	}

	public static boolean compareDetachedObjectsForField(Object detachedEntity1, Object detachedEntity2, String fieldName) {
		try {
			Field field1 = getFieldFromClassHierarchy(detachedEntity1, fieldName);
			Field field2 = getFieldFromClassHierarchy(detachedEntity2, fieldName);
			if (field1 != null && field2 != null) {
				return field1.get(detachedEntity1) == null && field2.get(detachedEntity2) == null || Objects.deepEquals(field1.get(detachedEntity1), field2.get(detachedEntity2));
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public static Set<Object> getValueOfAllFieldsOfType(Object object, Class type) {
		List<Field> fields = getAllFieldsFromClassHierarchy(object.getClass());

		Set<Object> values = fields.stream().filter(
				field -> field.getType().getCanonicalName().equals(type.getCanonicalName())
		).map(field -> {
			Object result = null;
			field.setAccessible(true);
			try {
				result = field.get(object);
			}
			catch (IllegalAccessException e) {
				log.warn(e.getMessage());
			}
			return result;
		}).filter(Objects::nonNull).collect(Collectors.toSet());

		List<Field> filteredCollectionFields = fields.stream().filter(
				field -> Collection.class.isAssignableFrom(field.getType()) &&
						type.isAssignableFrom(getActualType(field))
		).collect(Collectors.toList());


		List<Object> valuesFromCollections = new ArrayList<>();
		for (Field collectionField : filteredCollectionFields) {
			collectionField.setAccessible(true);

			try {
				Collection<Object> ss = (Collection<Object>) collectionField.get(object);
				for (Object ssss : ss) {
					valuesFromCollections.add(ssss);
				}
			}
			catch (IllegalAccessException e) {
				log.warn(e.getMessage());
			}

		}
		Set<Object> response = new HashSet<>();
		if (Objects.nonNull(values) && !values.isEmpty())
			response.addAll(values);
		if (Objects.nonNull(valuesFromCollections) && !valuesFromCollections.isEmpty())
			response.addAll(valuesFromCollections);
		return response;
	}

	public static <T> List<T> flattenListOfListsStream(List<List<T>> list) {
		return list.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	public static List<Field> getAllFieldsFromClassHierarchy(Class clazz) {
		return getAllFieldsRec(clazz, new ArrayList<>());
	}

	private static List<Field> getAllFieldsRec(Class clazz, List<Field> list) {
		Class superClazz = clazz.getSuperclass();
		if (superClazz != null) {
			getAllFieldsRec(superClazz, list);
		}
		list.addAll(Arrays.asList(clazz.getDeclaredFields()));
		return list;
	}

	public static List<String> getFieldNamesWithAnnotation(Class clazz, Class annotation) {
		try {
			List<Field> fields = getAllFieldsFromClassHierarchy(clazz);
			List<String> filteredFieldName = new ArrayList<>();

			for (Field field : fields) {
				if (Objects.nonNull(field.getAnnotation(annotation)))
					filteredFieldName.add(field.getName());
			}
			return filteredFieldName;
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static String getSoleFieldNameWithAnnotation(Class clazz, Class annotation) {
		try {
			List<String> fieldNames = getFieldNamesWithAnnotation(clazz, annotation);
			if (fieldNames != null && fieldNames.size() == 1) {
				return fieldNames.get(0);
			}
			throw new ReflectionAssertionException("Found multiple or no field name with specified annotation");
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static String getFieldNameWithTypeAndAnnotation(Class clazz, Class type, Class annotation) {
		try {
			List<Field> fields = getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = new ArrayList<>();

			for (Field field : fields) {
				if (field.getType().isAssignableFrom(type) &&
						Objects.nonNull(field.getAnnotation(annotation)))
					filteredFields.add(field);

			}

			if (filteredFields.size() != 1) {
				return null;
			}
			return filteredFields.get(0).getName();
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	//todo change consumer of this method to directly call methods this method is calling
	public static String getFieldName(Class clazz, Class type) {
		String result = null;
		result = getFieldNameWithType(clazz, type);
		if (Objects.isNull(result)) {
			result = getFieldNameOfCollectionsType(clazz, type);
		}

		return result;
	}

	public static String getFieldNameWithType(Class clazz, Class type) {
		try {
			List<Field> fields = getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = fields.stream().filter(
					field -> field.getType().getCanonicalName().equals(type.getCanonicalName())
			).collect(Collectors.toList());
			if (filteredFields.isEmpty()) {
				return getFieldNameWithAssignableType(clazz, type);
			}
			if (filteredFields.size() != 1) {
				return null;
			}
			return filteredFields.get(0).getName();
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static Object getFieldValueByType(Object object, Class type) {
		String fieldName = ReflectionUtil.getFieldName(object.getClass(), type);
		return getFieldValue(object, fieldName);
	}

	public static String getFieldNameWithAssignableType(Class clazz, Class type) {
		try {
			List<Field> fields = getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = fields.stream().filter(
					field -> field.getType().isAssignableFrom(type)
			).collect(Collectors.toList());
			if (filteredFields.size() != 1) {
				return null;
			}
			return filteredFields.get(0).getName();
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static String getFieldNameOfCollectionsType(Class clazz, Class type) {
		try {
			List<Field> fields = getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = fields.stream().filter(
					field -> Collection.class.isAssignableFrom(field.getType()) &&
							getActualType(field).getCanonicalName().equals(type.getCanonicalName())
			).collect(Collectors.toList());
			if (filteredFields.size() != 1) {
				return null;
			}
			return filteredFields.get(0).getName();
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static String getFieldNameOfCollectionsWithAssignableType(Class clazz, Class type) {
		try {
			List<Field> fields = getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = fields.stream().filter(
					field -> Collection.class.isAssignableFrom(field.getType()) && getActualType(field).isAssignableFrom(type)
			).collect(Collectors.toList());
			if (filteredFields.size() != 1) {
				return null;
			}
			return filteredFields.get(0).getName();
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static String getFieldNameOfCollectionsTypeAndAnnotation(Class clazz, Class type, Class annotation) {
		try {
			List<Field> fields = getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = new ArrayList<>();
			for (Field field : fields) {
				if (Collection.class.isAssignableFrom(field.getType()) &&
						getActualType(field).isAssignableFrom(type) &&
						Objects.nonNull(field.getAnnotation(annotation))) {

					filteredFields.add(field);
				}
			}

			if (filteredFields.size() != 1) {
				return null;
			}
			return filteredFields.get(0).getName();
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static Class getActualType(Field field) {
		Type genericFieldType = field.getGenericType();
		if (genericFieldType instanceof ParameterizedType) {
			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			for (Type fieldArgType : fieldArgTypes) {
				return (Class) fieldArgType;
			}
		}
		return null;
	}

	public static String getRelationshipCardinality(Class entityClass, Class linkedEntityClass) {
		String manyToOneLinkField = getFieldNameWithTypeAndAnnotation(entityClass, linkedEntityClass, ManyToOne.class);
		String oneToManyLinkField = getFieldNameOfCollectionsTypeAndAnnotation(linkedEntityClass, entityClass, OneToMany.class);
		String oneToOneLinkField = getFieldNameWithTypeAndAnnotation(entityClass, linkedEntityClass, OneToOne.class);


		if (Objects.nonNull(oneToManyLinkField) && Objects.nonNull(manyToOneLinkField))
			return "BI-DIRECTIONAL";
		else if (Objects.isNull(oneToManyLinkField) && Objects.nonNull(manyToOneLinkField))
			return "MANY-TO-ONE";
		else if (Objects.nonNull(oneToManyLinkField) && Objects.isNull(manyToOneLinkField))
			return "ONE-TO-MANY";
		else if (Objects.nonNull(oneToOneLinkField))
			return "ONE-TO-ONE";
		else return null;
	}

	public static boolean isAnnotationAppliedOnField(Class clazz, String fieldName, Class annotation) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			Optional<Field> foundField = fields.stream().filter(field -> Objects.nonNull(field.getName()) && field.getName().equals(fieldName)).findFirst();
			if (foundField.isPresent()) {
				Field field = foundField.get();
				return Objects.nonNull(field.getAnnotation(annotation));
			}
			return foundField.isPresent();
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public static void nullifyFieldByName(Object object, String fieldName) throws IllegalAccessException {
		Field field = getFieldFromClassHierarchy(object, fieldName);
		try {
			field.set(object, null);
		}
		catch (IllegalAccessException e) {

			log.warn(e.getMessage());
		}
	}

	public static void nullifyField(Object object, Field field) {
		try {
			field.setAccessible(true);
			field.set(object, null);
		}
		catch (IllegalAccessException e) {
			log.warn(e.getMessage());
		}
	}

	public static void copyField(Object entityCreatedFromRequest, Object managedEntity, String fieldName) {
		try {
			Field field = getFieldFromClassHierarchy(entityCreatedFromRequest, fieldName);
			Method method = getSetterMethod(managedEntity, fieldName);
			if (field == null || method == null) {

				throw new ReflectionAssertionException(null);
			}
			method.invoke(managedEntity, field.get(entityCreatedFromRequest));
		}
		catch (Exception e) {

			throw new ReflectionAssertionException(null);
		}
	}

	public static Set<String> getFieldNamesMatchingRegexp(Class clazz, String regexp) {
		Field[] fields = clazz.getDeclaredFields();
		return Arrays.stream(fields).filter(field -> {
			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = pattern.matcher(field.getName());
			return matcher.find();
		}).map(Field::getName).collect(Collectors.toSet());
	}

	public static Set<Field> getFieldsMatchingRegexp(Object o, String regexp) {
		List<Field> fields = getAllFieldsFromClassHierarchy(o.getClass());
		return fields.stream().filter(field -> {
			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = pattern.matcher(field.getName());
			return matcher.find();
		}).collect(Collectors.toSet());
	}

	public static Set<Field> getFieldsMatchingRegexps(Object o, Set<String> regexps) {
		HashSet<Field> matchedFields = new HashSet<>();
		regexps.stream().forEach(regexp -> matchedFields.addAll(getFieldsMatchingRegexp(o, regexp)));
		return matchedFields;
	}

	public static void nullifyFieldsMatchingRegexps(Object o, Set<String> regexps) {
		Set<Field> fieldSet = getFieldsMatchingRegexps(o, regexps);
		if (fieldSet.isEmpty()) {
			log.info("No matching fields in current entity");
		}
		fieldSet.stream().forEach(field -> {
			try {
				ReflectionUtil.nullifyField(o, field);
			}
			catch (Exception e) {
				log.warn(e.getMessage());
			}
		});
	}

	public static Set<String> getFieldsMatchingRegexpsIfValueIsNull(Object o, Set<String> regexps) {
		Set<Field> fieldSet = getFieldsMatchingRegexps(o, regexps);
		return fieldSet.stream().filter(field -> Objects.isNull(getFieldValue(o, field))).collect(Collectors.toSet()).stream().map(field -> field.getName()).collect(Collectors.toSet());
	}

	public static Set<String> getFieldsMatchingRegexpsIfValueIsCollectionTypeAndEmpty(Object o, Set<String> regexps) {
		Set<Field> fieldSet = getFieldsMatchingRegexps(o, regexps);
		return fieldSet.stream().filter(field -> Collection.class.isAssignableFrom(field.getType()) && (Objects.isNull(getFieldValue(o, field)) || ((Collection) getFieldValue(o, field)).isEmpty())).collect(Collectors.toSet()).stream().map(field -> field.getName()).collect(Collectors.toSet());
	}

	public static List<String> getParametersOfConstructorWithAnnotation(Class viewClass, Class constructorAnnotation) {
		Constructor<?>[] constructors = viewClass.getConstructors();
		List<Constructor> filteredConstructor = new ArrayList<>();
		for (Constructor constructor : constructors) {
			if (constructor.isAnnotationPresent(constructorAnnotation))
				filteredConstructor.add(constructor);
		}

		if (filteredConstructor.size() != 1) {
			throw new ReflectionAssertionException("Exactly 1 mapper allowed");
		}
		return getConstructorParameterNames(filteredConstructor.get(0));
	}

	public static List<String> getParametersOfConstructorWithAnnotation(Class viewClass, Class constructorAnnotation, Class fieldNameAnnotaton) {
		Constructor<?>[] constructors = viewClass.getConstructors();
		List<Constructor> filteredConstructor = new ArrayList<>();
		for (Constructor constructor : constructors) {
			if (constructor.isAnnotationPresent(constructorAnnotation))
				filteredConstructor.add(constructor);
		}

		if (filteredConstructor.size() != 1) {
			throw new ReflectionAssertionException("Exactly 1 mapper allowed");
		}
		return getConstructorParameterNamesWithFieldNameAnnotation(filteredConstructor.get(0), fieldNameAnnotaton);
	}

	public static List<String> getConstructorParameterNames(Constructor c) {
		if (c != null) {
			Parameter[] parameters = c.getParameters();
			return Arrays.stream(parameters).map(Parameter::getName).collect(Collectors.toList());
		}
		else {
			return null;
		}
	}

	public static List<String> getConstructorParameterNamesWithFieldNameAnnotation(Constructor c, Class fieldNameAnnotation) {
		if (c != null) {
			Parameter[] parameters = c.getParameters();
			return Arrays.stream(parameters).map(parameter -> {
				if (Objects.nonNull(parameter.getAnnotation(fieldNameAnnotation)))
					return AnnotationUtils.getValue(parameter.getAnnotation(fieldNameAnnotation)).toString();
				return parameter.getName();
			}).collect(Collectors.toList());
		}
		else {
			return null;
		}
	}

	public static Object getFieldValue(Object o, String fieldName) {
		Field field = getFieldFromClassHierarchy(o, fieldName);
		try {
			return field.get(o);
		}
		catch (Exception e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	public static Object getFieldValue(Object o, Field field) {
		try {
			field.setAccessible(true);
			return field.get(o);
		}
		catch (Exception e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	public static boolean setFieldValue(Object o, String fieldName, Object fieldValue) {
		Field field = getFieldFromClassHierarchy(o, fieldName);
		try {
			field.set(o, fieldValue);
			return true;
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public static boolean addFieldValue(Object obj, String fieldName, Object fieldValue) {
		Field field = getFieldFromClassHierarchy(obj, fieldName);
		if (Objects.isNull(field))
			return false;
		try {
			if (!Collection.class.isAssignableFrom(field.getType())) {
				throw new ReflectionAssertionException("Field Is Not a Collection");
			}

			Collection<Object> actualCollection = (Collection<Object>) field.get(obj);
			actualCollection.add(fieldValue);
			return true;
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	private static Field getFieldFromClassHierarchy(Object o, String fieldName) {
		List<Field> fields = getAllFieldsFromClassHierarchy(o.getClass());
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}

	public static Field getFieldFromClassHierarchy(Class o, String fieldName) {
		List<Field> fields = getAllFieldsFromClassHierarchy(o);
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}

	private static Method getGetterMethod(Object o, String fieldName) {
		Method[] methods = o.getClass().getMethods();
		fieldName = StringUtils.capitalize(fieldName);
		for (Method m : methods) {
			if (m.getName().equals("get" + fieldName) && !m.getName().startsWith("getClass")) {
				return m;
			}
		}
		return null;
	}

	private static Method getSetterMethod(Object o, String fieldName) {
		Method[] methods = o.getClass().getMethods();
		fieldName = StringUtils.capitalize(fieldName);
		for (Method m : methods) {
			if (m.getName().equals("set" + fieldName)) {
				return m;
			}
		}
		return null;
	}

	private static Class getFieldTypeFromClassHierarchy(Class entity, String fieldName) {
		try {
			Field field = getFieldFromClassHierarchy(entity, fieldName);
			Class genericClass = getActualType(field);
			return genericClass == null ? field.getType() : genericClass;
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public static Class getDatatypeOfFieldInClass(Class entity, String fieldName) {
		String[] names = fieldName.split("\\.");
		for (String name : names) {
			entity = ReflectionUtil.getFieldTypeFromClassHierarchy(entity, name);
		}
		return entity;
	}

	public static Object getAnnotationValueByField(Field field, Class annotationClass) {
		return AnnotationUtils.getValue(AnnotationUtils.getAnnotation(field, annotationClass));
	}

	public static Object getAnnotationValueByFieldName(Object o, String fieldName, Class annotationClass) {
		Field field = getFieldFromClassHierarchy(o, fieldName);
		return getAnnotationValueByField(field, annotationClass);
	}

	public static Object getAnnotationValueByFieldName(Class clazz, String fieldName, Class annotationClass) {
		Field field = getFieldFromClassHierarchy(clazz, fieldName);
		return getAnnotationValueByField(field, annotationClass);
	}

	public static Object getAnnotationValueByFieldType(Class clazz, Class fieldType, Class annotationClass) {
		try {
			List<Field> fields = getFieldsOfType(clazz, fieldType);
			if (fields != null && fields.size() == 1) {
				Field field = fields.get(0);
				field.setAccessible(true);
				return getAnnotationValueByField(field, annotationClass);
			}
			else {
				throw new ReflectionAssertionException("Found multiple or no field with specified field type");
			}
		}
		catch (Exception var3) {
			var3.printStackTrace();
			throw new ReflectionAssertionException("Error while getting annotation value by field type");
		}
	}

	public static void setSoleCollectionFieldOfType(Object o, Object v) {
		try {
			List<Field> fields = getFieldOfCollectionsType(o.getClass(), v.getClass());

			if (fields != null && fields.size() == 1) {
				Field field = fields.get(0);
				field.setAccessible(true);
				field.set(o, v);
			}
			else {
				throw new ReflectionAssertionException("Found multiple or no field name with specified collection field type");
			}
		}
		catch (Exception var3) {
			var3.printStackTrace();
			throw new ReflectionAssertionException("Error while setting field value");
		}
	}

	public static List<Field> getFieldOfCollectionsType(Class clazz, Class type) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			return fields.stream().filter(field -> Collection.class.isAssignableFrom(field.getType()) && ReflectionUtil.getActualType(field).getCanonicalName().equals(type.getCanonicalName())).collect(Collectors.toList());
		}
		catch (RuntimeException var4) {
			var4.printStackTrace();
			return null;
		}
		catch (Exception var5) {
			var5.printStackTrace();
			return null;
		}
	}

	public static void setValueOfSoleCollectionFieldOfType(Object o, Object v) {
		Field field = getSoleCollectionFieldOfType(o, v);
		try {
			field.setAccessible(true);
			Collection collection = (Collection) field.get(o);
			if (collection == null) {
				if (field.getType().equals(Set.class))
					collection = HashSet.class.getConstructor().newInstance();
				else if (field.getType().equals(List.class))
					collection = ArrayList.class.getConstructor().newInstance();
				else
					throw new ReflectionAssertionException("NOT ABLE TO CREATE COLLECTION");
				field.set(o, collection);
			}
			collection.add(v);

		}
		catch (Exception var3) {
			var3.printStackTrace();
			throw new ReflectionAssertionException("Error while setting field value of type collection");
		}
	}

	public static void setValueOfSoleFieldOfType(Object o, Object v) {
		try {
			List<Field> fields = getFieldsOfType(o.getClass(), v.getClass());

			if (fields != null && fields.size() == 1) {
				Field field = fields.get(0);
				field.setAccessible(true);
				field.set(o, v);
			}
			else {
				throw new ReflectionAssertionException("Found multiple or no field with specified type");
			}
		}
		catch (Exception var3) {
			var3.printStackTrace();
			throw new ReflectionAssertionException("VALUE NOT SET");
		}
	}

	public static Field getSoleCollectionFieldOfType(Object o, Object v) {
		try {
			List<Field> fieldsOfCollectionType = getFieldsOfCollectionType(o.getClass());
			List<Field> fieldsMatchingActualType = new ArrayList<>();
			for (Field fieldOfCollectionType : fieldsOfCollectionType) {
				Class actualType = ReflectionUtil.getActualType(fieldOfCollectionType);
				if (actualType.equals(v.getClass()))
					fieldsMatchingActualType.add(fieldOfCollectionType);
			}
			if (fieldsMatchingActualType != null && fieldsMatchingActualType.size() == 1) {
				return fieldsMatchingActualType.get(0);
			}
			else {
				return null;
			}
		}
		catch (Exception var3) {
			var3.printStackTrace();
			throw new ReflectionAssertionException("Could not get value of field of specified sole collection type");
		}
	}

	public static Field getSoleFieldOfType(Object o, Object v) {
		try {
			List<Field> fields = getFieldsOfType(o.getClass(), v.getClass());

			if (fields != null && fields.size() == 1) {
				Field field = fields.get(0);
				field.setAccessible(true);
				return field;
			}
			else {
				return null;
			}
		}
		catch (Exception var3) {
			var3.printStackTrace();
			throw new ReflectionAssertionException("Could not get sole field of field type");
		}
	}

	public static Object getValueOfSoleFieldOfType(Object o, Object v) {
		try {
			List<Field> fields = getFieldsOfType(o.getClass(), v.getClass());

			if (fields != null && fields.size() == 1) {
				Field field = fields.get(0);
				field.setAccessible(true);
				return field.get(o);
			}
			else {
				return null;
			}
		}
		catch (Exception var3) {
			var3.printStackTrace();
			throw new ReflectionAssertionException("Could not get value of sole specified field type");
		}
	}

	public static List<Field> getFieldsOfCollectionType(Class clazz) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = new ArrayList();
			Iterator var5 = fields.iterator();

			while (var5.hasNext()) {
				Field field = (Field) var5.next();
				if (Collection.class.isAssignableFrom(field.getType())) {
					filteredFields.add(field);
				}
			}

			return filteredFields;
		}
		catch (RuntimeException var7) {
			var7.printStackTrace();
			return null;
		}
		catch (Exception var8) {
			var8.printStackTrace();
			return null;
		}
	}

	public static List<Field> getFieldsOfType(Class clazz, Class type) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = new ArrayList();
			Iterator var5 = fields.iterator();

			while (var5.hasNext()) {
				Field field = (Field) var5.next();
				if (field.getType().isAssignableFrom(type)) {
					filteredFields.add(field);
				}
			}

			return filteredFields;
		}
		catch (RuntimeException var7) {
			var7.printStackTrace();
			return null;
		}
		catch (Exception var8) {
			var8.printStackTrace();
			return null;
		}
	}

	public static List<Field> getFieldsOfBaseEntityInterfaceType(Class clazz, Class baseEntityInterfaceType) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredFields = new ArrayList();
			Iterator var5 = fields.iterator();

			while (var5.hasNext()) {
				Field field = (Field) var5.next();
				if (baseEntityInterfaceType.isAssignableFrom(field.getType())) {
					filteredFields.add(field);
				}
			}

			return filteredFields;
		}
		catch (RuntimeException var7) {
			var7.printStackTrace();
			return null;
		}
		catch (Exception var8) {
			var8.printStackTrace();
			return null;
		}
	}

	public static List<Class> getFieldTypesWithAnnotation(Class clazz, Class annotation) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			List<Class> filteredFieldType = new ArrayList();
			Iterator var4 = fields.iterator();

			while (var4.hasNext()) {
				Field field = (Field) var4.next();

				if (Objects.nonNull(field.getAnnotation(annotation))) {
					filteredFieldType.add(field.getType());
				}
			}

			return filteredFieldType;
		}
		catch (Exception var6) {
			var6.printStackTrace();
			return null;
		}
	}

	public static List<Field> getFieldsWithAnnotation(Class clazz, Class annotation) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			List<Field> filteredField = new ArrayList();
			Iterator var4 = fields.iterator();

			while (var4.hasNext()) {
				Field field = (Field) var4.next();

				if (Objects.nonNull(field.getAnnotation(annotation))) {
					filteredField.add(field);
				}
			}

			return filteredField;
		}
		catch (Exception var6) {
			var6.printStackTrace();
			return null;
		}
	}

	public static Set<Class> getFieldTypesWithAnyAnnotation(Class clazz, Class[] annotations) {
		try {
			List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(clazz);
			Set<Class> filteredFieldType = new HashSet<>();
			Iterator var4 = fields.iterator();

			while (var4.hasNext()) {
				Field field = (Field) var4.next();
				for (Class annotation : annotations) {
					if (Objects.nonNull(field.getAnnotation(annotation))) {
						filteredFieldType.add(field.getType());
					}
				}
			}

			return filteredFieldType;
		}
		catch (Exception var6) {
			var6.printStackTrace();
			return null;
		}
	}

	public static Object createInstance(Class clazz) {
		try {
			return clazz.getConstructor().newInstance();
		}
		catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Boolean isFieldExist(Class clazz, String fieldName) {
		Set<String> fieldNames = new HashSet<>();
		for (Field field : clazz.getDeclaredFields()) {
			fieldNames.add(field.getName());
		}
		return fieldNames.contains(fieldName);
	}

	public static Object getValueFromObject(Object object, String fieldName) {
		Method method = ReflectionUtil.getGetterMethod(object, fieldName);
		return ReflectionUtils.invokeMethod(method, object);
	}

	public static Object getFieldValueByAnnotationValue(Object o, Class annotationClass, Class annotationValue) {
		List<Field> fields = getAllFieldsFromClassHierarchy(o.getClass());
		for (Field field : fields) {
			if (getAnnotationValueByField(field, annotationClass).equals(annotationValue)) {
				return getFieldValue(o, field);
			}
		}
		return null;
	}

	public static void setValueOfFieldByEntityClassAnnotationValue(Object o, Object fieldValue, Class annotationClass, Class annotationValue) {
		List<Field> fields = getAllFieldsFromClassHierarchy(o.getClass());
		for (Field field : fields) {
			Class value = (Class) getAnnotationValueByField(field, annotationClass);
			if (Objects.nonNull(value) && annotationValue.isAssignableFrom(value)) {
				ReflectionUtil.setFieldValue(o, field.getName(), fieldValue);
				break;
			}
		}
	}

	public static Method getMethod(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		if (method.getDeclaringClass().isInterface()) {
			try {
				method = joinPoint
						.getTarget()
						.getClass()
						.getDeclaredMethod(joinPoint.getSignature().getName(),
								method.getParameterTypes());
			}
			catch (SecurityException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		return method;
	}

	@Getter
	public static class ReflectionAssertionException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private final String message;

		public ReflectionAssertionException(String message) {
			this.message = message;
		}
	}

}

