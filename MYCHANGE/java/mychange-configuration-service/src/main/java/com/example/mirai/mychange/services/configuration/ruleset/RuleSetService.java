package com.example.mirai.projectname.services.configuration.ruleset;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.example.mirai.projectname.services.configuration.ruleset.models.RuleSet;
import com.example.mirai.projectname.services.configuration.util.Util;

import org.springframework.stereotype.Service;

@Service
public class RuleSetService {
	private final RuleSetRepository ruleSetRepository;

	public RuleSetService(RuleSetRepository ruleSetRepository) {
		this.ruleSetRepository = ruleSetRepository;
	}

	public RuleSet createRuleSet(RuleSet ruleSet) {
		String name = Util.generateIdFromString(ruleSet.getLabel());
		Optional<RuleSet> existingRuleSet = getRuleSet(name);
		if (existingRuleSet.isEmpty()) {
			ruleSet.setName(name);
			return ruleSetRepository.save(ruleSet);
		}
		else
			throw new EntityExistsException();
	}

	public Optional<RuleSet> getRuleSet(String ruleSetId) {
		return ruleSetRepository.findById(ruleSetId);
	}

	public Iterable<RuleSet> getRuleSets() {
		return ruleSetRepository.findAll();
	}

	public void deleteRuleSet(String ruleSetId) {
		ruleSetRepository.deleteById(ruleSetId);
	}

	public RuleSet updateRuleSet(String ruleSetId, RuleSet ruleSet) {
		Optional<RuleSet> existingRuleSet = getRuleSet(ruleSetId);
		if (existingRuleSet.isEmpty())
			throw new EntityNotFoundException();
		else {
			ruleSet.setName(ruleSetId);
			return ruleSetRepository.save(ruleSet);
		}
	}
}
