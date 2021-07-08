package com.example.mirai.libraries.entity.service.helper.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.util.ReflectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ServiceHelper {
	public static HashSet<EntityLink<BaseEntityInterface>> getEntityLinkSet(Class[] linkClasses, List<BaseEntityInterface> linkObjects) {
		HashSet<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
		int count = 0;
		if (linkClasses == null) {
			return entityLinkSet;
		}
		while (count < linkClasses.length) {
			for (Object linkObject : linkObjects) {
				if (linkClasses[count] == linkObject.getClass() || linkClasses[count].isAssignableFrom(linkObject.getClass())) {
					entityLinkSet.add(new EntityLink<BaseEntityInterface>(((BaseEntityInterface) linkObject).getId(), (Class<BaseEntityInterface>) linkObject.getClass()));
				}
			}
			count++;
		}
		return entityLinkSet;
	}

	public static boolean isEntityLinkToCorrectId(AggregateInterface aggregate, String fieldName, Object entity, ArrayList<Long> ids) {
		// TODO: Add logic to accommodate checks on multiple links
		Class[] links = (Class[]) ReflectionUtil.getAnnotationValueByFieldName(aggregate, fieldName, LinkTo.class);
		if (links == null || links.length == 0)
			return true;
		for (Class link : links) {
			Long linkId = ((BaseEntityInterface) ReflectionUtil.getFieldValue(entity, ReflectionUtil.getFieldName(entity.getClass(), link))).getId();
			if (!ids.contains(linkId) && link != entity.getClass()) {
				return false;
			}
		}
		return true;
	}

	public static void updateJSONIgnoreFields(BaseEntityInterface readInst, BaseEntityInterface updatedInst) {
		List<String> fieldNames = ReflectionUtil.getFieldNamesWithAnnotation(readInst.getClass(), JsonIgnore.class);
		Iterator<String> iterator = fieldNames.iterator();
		while (iterator.hasNext()) {
			String fieldName = iterator.next();
			ReflectionUtil.setFieldValue(updatedInst, fieldName, ReflectionUtil.getFieldValue(readInst, fieldName));
		}
	}
}
