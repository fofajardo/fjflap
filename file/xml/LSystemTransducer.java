/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */





package file.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import file.ParseException;
import grammar.Grammar;
import grammar.Production;
import grammar.UnboundGrammar;
import grammar.lsystem.LSystem;
/**
 * This transducer is the codec for {@link grammar.lsystem.LSystem} objects.
 * 
 * @author Thomas Finley
 */

public class LSystemTransducer extends AbstractTransducer {
	/**
	 * Returns the type this transducer recognizes, "lsystem".
	 * 
	 * @return the string "lsystem"
	 */
	public String getType() {
		return "lsystem";
	}

	/**
	 * Given a list of objects, this converts it to a space delimited string.
	 * 
	 * @param list
	 *            the list to convert to a string
	 * @return a string containing the elements of the list
	 */
	private static String listAsString(List<String> list) {
		Iterator<String> it = list.iterator();
		if (!it.hasNext())
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(it.next());
		while (it.hasNext()) {
			sb.append(' ');
			sb.append(it.next());
		}
		return sb.toString();
	}


	/**
	 * Returns the productions representing the rewriting rules for a given
	 * node.
	 * 
	 * @param node
	 *            the node the encapsulates a production
	 */
	private static Production[] createRules(Node node) {
		Map<String, String> e2t = elementsToText(node);
		String left = (String) e2t.get(RULE_LEFT_NAME);
		if (left == null)
			left = "";
		NodeList list = ((Element) node).getElementsByTagName(RULE_RIGHT_NAME);
		Production[] p = new Production[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			String right = containedText(list.item(i));
			p[i] = new Production(left, right == null ? "" : right);
		}
		return p;
	}

	/**
	 * Returns an element that encodes a given rewriting rule.
	 * 
	 * @param document
	 *            the document to create the element in
	 * @param lsystem
	 *            the L-system we are encoding
	 * @param left
	 *            the replacement predicate we are replacing
	 * @return an element that encodes a production
	 */
	public static Element createRuleElement(Document document, LSystem lsystem,
			String left) {
		Element re = createElement(document, RULE_NAME, null, null);
		re.appendChild(createElement(document, RULE_LEFT_NAME, null, left));
		List<List<String>> replacements = lsystem.getReplacements(left);
		lsystem.getReplacements(left);
		for (int i = 0; i < replacements.size(); i++) {
			re.appendChild(createElement(document, RULE_RIGHT_NAME, null,
					listAsString(replacements.get(i))));
		}
		return re;
	}

	/**
	 * Reads the axiom from the DOM and returns it as a string.
	 * 
	 * @param document
	 *            the DOM document to read the axiom from
	 * @return the string that contains the axiom
	 */
	private String readAxiom(Document document) {
		NodeList list = document.getDocumentElement().getElementsByTagName(
				AXIOM_NAME);
		if (list.getLength() < 1)
			throw new ParseException("No axiom specified in the document!");
		String axiom = containedText(list.item(list.getLength() - 1));
		if (axiom == null)
			axiom = "";
		return axiom;
	}

	/**
	 * Reads the rewriting rules from the DOM and returns it as a grammar.
	 * 
	 * @param document
	 *            the DOM document to read rewriting rules from
	 * @return the grammar that holds these results
	 */
	private Grammar readGrammar(Document document) {
		Grammar g = new UnboundGrammar();
		NodeList list = document.getDocumentElement().getElementsByTagName(
				RULE_NAME);
		for (int i = 0; i < list.getLength(); i++) {
			Production[] p = createRules(list.item(i));
			for (int j = 0; j < p.length; j++)
				g.addProduction(p[j]);
		}
		return g;
	}

	/**
	 * Reads the parameters from the DOM document and returns it as a map from
	 * parameter names to values.
	 * 
	 * @param document
	 *            the DOM document to read parameters from
	 * @return the mapping of parameter names to values
	 */
	private Map<Object, Object> readParameters(Document document) {
		Map<Object, Object> p = new HashMap<>();
		NodeList list = document.getDocumentElement().getElementsByTagName(
				PARAMETER_NAME);
		for (int i = 0; i < list.getLength(); i++) {
			Map<String, String> e2t = elementsToText(list.item(i));
			String name = (String) e2t.get(PARAMETER_NAME_NAME), value = (String) e2t
					.get(PARAMETER_VALUE_NAME);
			if (name == null)
				continue;
			if (value == null)
				value = "";
			p.put(name, value);
		}
		return p;
	}

	/**
	 * Given a document, this will return the corresponding L-system encoded in
	 * the DOM document.
	 * 
	 * @param document
	 *            the DOM document to convert
	 * @return the {@link grammar.lsystem.LSystem} instance
	 */
	public java.io.Serializable fromDOM(Document document) {
		String axiom = readAxiom(document);
		Grammar rules = readGrammar(document);
		Map<Object, Object> parameters = readParameters(document);
		return new LSystem(axiom, rules, parameters);
	}

	/**
	 * Given a JFLAP L-system, this will return the corresponding DOM encoding
	 * of the structure.
	 * 
	 * @param structure
	 *            the JFLAP L-system to encode
	 * @return a DOM document instance
	 */
	public Document toDOM(java.io.Serializable structure) {
		LSystem lsystem = (LSystem) structure;
		Document doc = newEmptyDocument();
		Element se = doc.getDocumentElement();
		// Add the axiom.
		se.appendChild(createComment(doc, COMMENT_AXIOM));
		se.appendChild(createElement(doc, AXIOM_NAME, null,
				listAsString(lsystem.getAxiom())));
		// Add the rewriting rules.
		Set<String> symbols = lsystem.getSymbolsWithReplacements();
		Iterator<String> it = symbols.iterator();
		if (it.hasNext())
			se.appendChild(createComment(doc, COMMENT_RULE));
		while (it.hasNext())
			se.appendChild(createRuleElement(doc, lsystem, (String) it.next()));
		// Add the parameters.
		Map<Object, Object> parameters = lsystem.getValues();
		Iterator<Object> it2 = parameters.keySet().iterator();
		if (it2.hasNext())
			se.appendChild(createComment(doc, COMMENT_PARAMETER));
		while (it2.hasNext()) {
			String name = (String) it2.next();
			String value = (String) parameters.get(name);
			Element pe = createElement(doc, PARAMETER_NAME, null, null);
			pe.appendChild(createElement(doc, PARAMETER_NAME_NAME, null, name));
			pe.appendChild(createElement(doc, PARAMETER_VALUE_NAME, null, value));
			se.appendChild(pe);
		}
		// Return the completed document.
		return doc;
	}

	/** The tag name for the axiom. */
	public static final String AXIOM_NAME = "axiom";

	/** The tag name for a rewriting rule. */
	public static final String RULE_NAME = "production";

	/** The tag name for the left of a rewriting rule. */
	public static final String RULE_LEFT_NAME = "left";

	/** The tag name for a right of a rewriting rule. */
	public static final String RULE_RIGHT_NAME = "right";

	/** The tag name for one of the drawing parameters. */
	public static final String PARAMETER_NAME = "parameter";

	/** The tag name for the parameter name. */
	public static final String PARAMETER_NAME_NAME = "name";

	/** The tag name for the parameter value. */
	public static final String PARAMETER_VALUE_NAME = "value";

	/** The comment for the axiom. */
	private static final String COMMENT_AXIOM = "The L-system axiom.";

	/** The comment for the list of rewriting rules. */
	private static final String COMMENT_RULE = "The rewriting rules.";

	/** The comment for the list of parameters. */
	private static final String COMMENT_PARAMETER = "The drawing parameters.";
}
