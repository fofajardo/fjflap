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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import automata.Automaton;
import automata.NondeterminismDetector;
import automata.NondeterminismDetectorFactory;
import automata.State;
import gui.editor.ArrowDisplayOnlyTool;
import gui.environment.Environment;
import gui.environment.tag.CriticalTag;
import gui.viewer.AutomatonPane;
import gui.viewer.SelectionDrawer;

/**
 * This is the action used to highlight nondeterministic states.
 * 
 * @author Thomas Finley
 */

public class NondeterminismAction extends AutomatonAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new <CODE>NondeterminismAction</CODE>.
	 * 
	 * @param automaton
	 *            the automaton that input will be simulated on
	 * @param environment
	 *            the environment object that we shall add our simulator pane to
	 */
	public NondeterminismAction(Automaton automaton, Environment environment) {
		super("Highlight Nondeterminism", null);
		this.automaton = automaton;
		this.environment = environment;
	}

	/**
	 * Performs the action.
	 */
	public void actionPerformed(ActionEvent e) {
		SelectionDrawer drawer = new SelectionDrawer(automaton);
		NondeterminismDetector d = NondeterminismDetectorFactory
				.getDetector(automaton);
		State[] nd = d.getNondeterministicStates(automaton);
		for (int i = 0; i < nd.length; i++)
			drawer.addSelected(nd[i]);
		AutomatonPane ap = new AutomatonPane(drawer);
		NondeterminismPane pane = new NondeterminismPane(ap);
		environment.add(pane, "Nondeterminism", new CriticalTag() {
		});
		environment.setActive(pane);
	}

	/**
	 * This action is only applicable to automaton objects.
	 * 
	 * @param object
	 *            the object to test for being an automaton
	 * @return <CODE>true</CODE> if this object is an instance of a subclass
	 *         of <CODE>Automaton</CODE>, <CODE>false</CODE> otherwise
	 */
	public static boolean isApplicable(Object object) {
		return object instanceof Automaton;
	}

	/**
	 * A class that exists to make integration with the help system feasible.
	 */
	private class NondeterminismPane extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NondeterminismPane(AutomatonPane ap) {
			super(new BorderLayout());
			ap.addMouseListener(new ArrowDisplayOnlyTool(ap, ap.getDrawer()));
			add(ap, BorderLayout.CENTER);
			add(new JLabel("Nondeterministic states are highlighted."),
					BorderLayout.NORTH);
		}
	}

	/** The automaton this simulate action runs simulations on! */
	private Automaton automaton;

	/** The environment that the simulation pane will be put in. */
	private Environment environment;
}
