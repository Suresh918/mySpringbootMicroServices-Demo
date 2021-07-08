package com.example.mirai.libraries.core.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseProperty {
	Set<String> unreadablePropertyRegexps;

	Set<String> readablePropertyRegexps;

	Set<String> unupdatablePropertyRegexps;

	Set<String> updatablePropertyRegexps;
}
