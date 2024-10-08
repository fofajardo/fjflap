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





package gui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import gui.environment.RegularEnvironment;
import gui.environment.tag.CriticalTag;
import gui.regular.ConvertToAutomatonPane;

/**
 * This class initiates the conversion of a regular expression to a
 * nondeterministic finite state automaton.
 * 
 * @author Thomas Finley
 */

public class REToFSAAction extends RegularAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a <CODE>REToFSAAction</CODE>.
	 * 
	 * @param environment
	 *            the environment which is home to the regular expression to
	 *            convert
	 */
	public REToFSAAction(RegularEnvironment environment) {
		super("Convert to NFA", null, environment);
	}

	/**
	 * This begins the process of converting a regular expression to an NFA.
	 * 
	 * @param event
	 *            the event to process
	 */
	public void actionPerformed(ActionEvent event) {
		// JFrame frame = Universe.frameForEnvironment(environment);
		try {
			getExpression().asCheckedString();
		} catch (UnsupportedOperationException e) {
			JOptionPane.showMessageDialog(getEnvironment(), e.getMessage(),
					"Illegal Expression", JOptionPane.ERROR_MESSAGE);
			return;
		}
		ConvertToAutomatonPane pane = new ConvertToAutomatonPane(
				getEnvironment());
		getEnvironment().add(pane, "Convert RE to NFA", new CriticalTag() {
		});
		getEnvironment().setActive(pane);
	}
}
