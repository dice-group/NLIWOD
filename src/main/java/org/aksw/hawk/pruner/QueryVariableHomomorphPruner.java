package org.aksw.hawk.pruner;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.Node_Variable;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

public class QueryVariableHomomorphPruner {
	Logger log = LoggerFactory.getLogger(QueryVariableHomomorphPruner.class);
	Set<String> reducedQueries = Sets.newHashSet();

	public boolean queryHasNotBeenHandled(ParameterizedSparqlString query) {
		// reduce number of variables
		if (reducedQueries.add(reduce(query).toString())) {
			// did not exist before
			return true;
		} else {
			return false;
		}
	}

	public void reset() {
		reducedQueries = Sets.newHashSet();

	}

	private ParameterizedSparqlString reduce(ParameterizedSparqlString query) {
		// identify vars
		try {
			List<Element> elements = ((ElementGroup) query.asQuery().getQueryPattern()).getElements();
			Set<String> variables = Sets.newHashSet();
			for (Element elem : elements) {
				if (elem instanceof ElementPathBlock) {
					ElementPathBlock pathBlock = (ElementPathBlock) elem;
					for (TriplePath triple : pathBlock.getPattern().getList()) {
						if (triple.getSubject().isVariable()) {
							variables.add(triple.getSubject().getName());
						}
						if (triple.getPredicate().isVariable()) {
							variables.add(triple.getPredicate().getName());
						}
						if (triple.getObject().isVariable()) {
							variables.add(triple.getObject().getName());
						}
					}
				}
			}
			// replace vars with a new signature starting from 0
			int parameterCounter = 0;
			HashMap<String, String> variableMap = Maps.newHashMap();
			for (String var : variables) {
				variableMap.put(var, "a" + parameterCounter);
				parameterCounter++;
			}
			// replace vars in query
			for (Element elem : elements) {
				if (elem instanceof ElementPathBlock) {
					ElementPathBlock pathBlock = (ElementPathBlock) elem;
					for (TriplePath triple : pathBlock.getPattern().getList()) {
						if (triple.getSubject().isVariable()) {
							String name = triple.getSubject().getName();
							query.setParam(name, new Node_Variable(variableMap.get(name)));
						}
						if (triple.getPredicate().isVariable()) {
							String name = triple.getPredicate().getName();
							query.setParam(name, new Node_Variable(variableMap.get(name)));
						}
						if (triple.getObject().isVariable()) {
							String name = triple.getObject().getName();
							query.setParam(name, new Node_Variable(variableMap.get(name)));
						}
					}
				}
			}
		} catch (QueryParseException e) {
			log.error(query.toString(), e);
		}
		return query;
	}

	public static void main(String args[]) {
		List<ParameterizedSparqlString> queries = Lists.newArrayList();
		String queryString = "SELECT ?a0 WHERE {?a0 a <http://dbpedia.org/ontology/City>. ?a1 <http://dbpedia.org/ontology/birthPlace> ?a0. }";
		queries.add(new ParameterizedSparqlString(queryString));
		queryString = "SELECT ?a0 WHERE {?a0 a <http://dbpedia.org/ontology/City>. ?a2 <http://dbpedia.org/ontology/birthPlace> ?a0. }";
		queries.add(new ParameterizedSparqlString(queryString));
		queryString = "SELECT ?a0 WHERE {?a0 a <http://dbpedia.org/ontology/City>. ?a2 <http://dbpedia.org/ontology/deathPlace> ?a0. }";
		queries.add(new ParameterizedSparqlString(queryString));

		QueryVariableHomomorphPruner qvhPruner = new QueryVariableHomomorphPruner();
		System.out.println(queries.size());
		for (ParameterizedSparqlString q : queries) {
			if (qvhPruner.queryHasNotBeenHandled(q)) {
				System.out.println("Keep: " + q);
			} else {
				System.out.println("Delete: " + q);
			}
		}
	}

}
