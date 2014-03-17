package org.aksw.autosparql.commons.qald;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.autosparql.commons.qald.uri.Entity;
import org.aksw.autosparql.commons.qald.uri.GoldEntity;
import org.aksw.hawk.module.Module;
import org.apache.commons.lang3.StringUtils;

import com.clearnlp.dependency.DEPTree;

public class Question implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9130793012431486456L;

	public Integer id;
	public Boolean onlydbo;
	public Boolean outOfScope;
	public Boolean aggregation;
	public String answerType;
	public String sparqlQuery;
	public String pseudoSparqlQuery;
	public String pseudoSystemQuery;
	public DEPTree tree;
	public Map<String, String> languageToQuestion = new LinkedHashMap<String, String>();
	public Map<String, List<String>> languageToKeywords = new LinkedHashMap<String, List<String>>();
	public Map<String, List<Entity>> languageToNamedEntites = new LinkedHashMap<String, List<Entity>>();
	public Map<String, List<Entity>> languageToNounPhrases = new LinkedHashMap<String, List<Entity>>();
	public Map<String, List<GoldEntity>> goldEntites = new HashMap<String, List<GoldEntity>>();
	public Map<String, Set<String>> goldenAnswers = new HashMap<String, Set<String>>();
	public List<Module> modules;


	public Question() {

		goldEntites.put("en", new ArrayList<GoldEntity>());
		goldEntites.put("de", new ArrayList<GoldEntity>());
	}

	@Override
	public String toString() {

		String output = String.format("id: %s answerType: %s aggregation: %s onlydbo: %s\n", id, answerType, aggregation, onlydbo);
		for (Map.Entry<String, String> entry : languageToQuestion.entrySet()) {
			output += "\t" + entry.getKey() + "\tQuestion: " + entry.getValue() + "\n";
			output += "\t\tKeywords: " + StringUtils.join(languageToKeywords.get(entry.getKey()), ", ") + "\n";
			output += "\t\tGold-Entities: " + StringUtils.join(goldEntites, ", ") + "\n";
			if (languageToNamedEntites.containsKey(entry.getKey()))
				output += "\t\tEntities: " + StringUtils.join(languageToNamedEntites.get(entry.getKey()), ", ") + "\n";
			if (languageToNounPhrases.containsKey(entry.getKey()))
				output += "\t\tNouns: " + StringUtils.join(languageToNounPhrases.get(entry.getKey()), ", ") + "\n";
		}
		output += "SPARQL: " + pseudoSparqlQuery;
		output += "\tAnswers: " + StringUtils.join(goldenAnswers, ", ") + "\n";

		return output;
	}
}
