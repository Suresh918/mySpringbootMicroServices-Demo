package com.example.mirai.projectname.services.configuration.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.example.mirai.projectname.services.configuration.form.models.Field;
import com.example.mirai.projectname.services.configuration.form.models.FieldGroup;
import com.example.mirai.projectname.services.configuration.form.models.Form;
import com.example.mirai.projectname.services.configuration.form.models.Properties;
import com.example.mirai.projectname.services.configuration.form.models.Help;

import org.springframework.stereotype.Service;

@Service
public class FormService {
	private final FormRepository formRepository;

	public FormService(FormRepository formRepository) {
		this.formRepository = formRepository;
	}

	public Form createForm(Form form) {
		String name = form.getName().toLowerCase();
		Optional<Form> existingForm = getForm(name);
		if (existingForm.isEmpty()) {
			form.setName(name);
			return formRepository.save(form);
		}
		else
			throw new EntityExistsException();
	}

	public Optional<Form> getForm(String formId) {
		return formRepository.findById(formId);
	}

	public Iterable<Form> getForms() {
		return formRepository.findAll();
	}

	public void deleteForm(String formId) {
		formRepository.deleteById(formId);
	}

	public Form updateForm(String formId, Form form) {
		Optional<Form> existingForm = getForm(formId);
		if (existingForm.isEmpty())
			throw new EntityNotFoundException();
		else {
			form.setName(formId);
			return formRepository.save(form);
		}
	}

	public Form updateHelp(String formId, String fieldId, Help help) {
		synchronized (this) {
			Optional<Form> optionalForm = formRepository.findById(formId);
			if (optionalForm.isPresent()) {
				Form form = optionalForm.get();
				if (form.getFields() != null) {
					Optional<Field> optionalField = Arrays.stream(form.getFields()).filter(field -> field.getName().equals(fieldId)).findFirst();
					if (optionalField != null) {
						Field field = optionalField.get();
						Properties properties = field.getProperties();
						if (properties == null)
							properties = new Properties();
						properties.setHelp(help);
						return formRepository.save(form);
					}
				}
			}
			throw new EntityNotFoundException();
		}
	}


	public List<FieldGroup> getFieldGroupsByGroup(String formId) {
		List<FieldGroup> fieldGroups = new ArrayList<FieldGroup>();
		List<Field> fieldslist = null;
		Optional<Form> optionalForm = formRepository.findById(formId);
		if (optionalForm.isPresent()) {
			Form form = optionalForm.get();
			if (form.getFields() != null) {
				fieldslist = Arrays.asList(form.getFields());
				fieldslist.stream().forEach(field -> {
					String group = field.getProperties().getGroup();
					Optional<FieldGroup> optionalFieldGroup = fieldGroups.stream()
							.filter(fieldGroup -> fieldGroup.getName().equals(group)).findFirst();
					if (optionalFieldGroup.isPresent()) {
						optionalFieldGroup.get().getFields().add(field);
					}
					else {
						//create new FieldGroup
						FieldGroup newfieldGroup = new FieldGroup();
						newfieldGroup.setName(group);
						newfieldGroup.getFields().add(field);
						fieldGroups.add(newfieldGroup);
					}
				});
			}
		}
		return fieldGroups;
	}
}
