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





package automata.fsa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import automata.Automaton;
import automata.AutomatonSimulator;
import automata.Configuration;
import automata.State;
import automata.Transition;
import debug.EDebug;

/**
 * The FSA step by state simulator object simulates the behavior of a finite
 * state automaton. It takes an FSA object and an input string and runs the
 * machine on the input. It simulates the machine's behavior by stepping one
 * state at a time, even if there are surrounding lambda transitions that the
 * path could explore without reading more input.
 * 
 * @author Ryan Cavalcante
 */

public class FSAStepByStateSimulator extends AutomatonSimulator {
	/**
	 * Creates an FSA step by state simulator for the given automaton.
	 * 
	 * @param automaton
	 *            the machine to simulate
	 */
	public FSAStepByStateSimulator(Automaton automaton) {
		super(automaton);
	}

	/**
	 * Returns an FSAConfiguration object that represents the initial
	 * configuration of the FSA, before any input has been processed. This
	 * method returns an array of length one, since the closure of the initial
	 * state is not taken.
	 * 
	 * @param input
	 *            the input string.
	 */
	public Configuration[] getInitialConfigurations(String input) {
		Configuration[] configs = new Configuration[1];
		configs[0] = new FSAConfiguration(myAutomaton.getInitialState(), null,
				input, input);
		return configs;
	}
	
	/**
	 * Simulates one step for a particular configuration, adding all possible
	 * configurations reachable in one step to set of possible configurations.
	 * 
	 * @param config
	 *            the configuration to simulate the one step on.
	 */
	public ArrayList<Configuration> stepConfiguration(Configuration config) {
		ArrayList<Configuration> list = new ArrayList<>();
		FSAConfiguration configuration = (FSAConfiguration) config;
		/** get all information from configuration. */
		String unprocessedInput = configuration.getUnprocessedInput();
		String totalInput = configuration.getInput();
		State currentState = configuration.getCurrentState();
		Transition[] transitions = myAutomaton
				.getTransitionsFromState(currentState);
		for (int k = 0; k < transitions.length; k++) {
			FSATransition transition = (FSATransition) transitions[k];
			/** get all information from transition. */
			String transLabel = transition.getLabel();
			if (isTransitionList(transLabel)){
				processElementList(list, configuration, unprocessedInput, totalInput,
						transition);
			} else {
				processElement(list, configuration, unprocessedInput,
						totalInput, transition, transLabel);
			}
		}
		return list;
	}

	protected boolean isTransitionList(String transitionLabel) {
		return (transitionLabel.indexOf("[") != -1) &&
				(transitionLabel.indexOf("]") != -1);
	}
	
	/**
	 * Adds all possible configurations from a transition list.
	 * @param list
	 * 		      the list containing possible configurations.
	 * @param configuration
	 *            the configuration to simulate the one step on.
	 * @param unprocessedInput
	 * 			  the unprocessed input.
	 * @param totalInput
	 * 			  the total input.
	 * @param transition
	 * 			  the transition that emanates from a state.
	 */
	protected void processElementList(
			ArrayList<Configuration> list,
			FSAConfiguration configuration,
			String unprocessedInput,
			String totalInput,
			FSATransition transition) {
		String transitionLabel = transition.getLabel();
		// XXX: This will ignore values outside the square brackets.
		transitionLabel = transitionLabel.substring(
				transitionLabel.indexOf("[") + 1,
				transitionLabel.indexOf("]"));
		HashSet<String> transitionList = new HashSet<String>(
				Arrays.asList(transitionLabel.split(",")));
		// Iterate through all elements of the list in the transition label.
		for (String element : transitionList) {
			// Block implicit lambda values.
			if (element.isEmpty()) {
				continue;
			// Process range values.
			} else if (element.contains("..")) {
				String[] transitionRange = element.split("\\.\\.");
				boolean isRange = false;
				String first = transitionRange[0].strip();
				String last = transitionRange[1].strip();
				// Treat it as a range iff after splitting, the array only
				// has two elements and both elements contain only a single
				// character.
				isRange = transitionRange.length == 2 &&
						first.length() == 1 &&
						last.length() == 1;
				if (isRange) {
					int start = first.charAt(0);
					int end = last.charAt(0);
					// Process all characters within the range.
					for (int i = start; i<= end; i++) {
						String currentChar = Character.toString((char)i);
						EDebug.print("RA: " + currentChar);
						processElement(list, configuration, unprocessedInput,
								totalInput, transition, currentChar);
					}
					continue;
				}
				EDebug.print(element + " is not a transition range.");
			}
			// Process normal string values.
			EDebug.print(element);
			processElement(list, configuration, unprocessedInput,
					totalInput, transition, element);
		}
	}

	/**
	 * Adds a configuration to the set of possible configurations.
	 * @param list
	 * 		      the list containing possible configurations.
	 * @param configuration
	 *            the configuration to simulate the one step on.
	 * @param unprocessedInput
	 * 			  the unprocessed input.
	 * @param totalInput
	 * 			  the total input.
	 * @param transition
	 * 			  the transition that emanates from a state.
	 * @param element
	 * 			  the transition label or an element from a list or range.
	 */
	private void processElement(
			ArrayList<Configuration> list,
			FSAConfiguration configuration,
			String unprocessedInput,
			String totalInput,
			FSATransition transition,
			String element) {
		if (unprocessedInput.startsWith(element)) {
			String input = "";
			if (element.length() < unprocessedInput.length()) {
				input = unprocessedInput.substring(element.length());
			}
			State toState = transition.getToState();
			FSAConfiguration configurationToAdd = new FSAConfiguration(
					toState, configuration, totalInput, input);
			list.add(configurationToAdd);
		}
	}

	/**
	 * Returns true if the simulation of the input string on the automaton left
	 * the machine in a final state. If the entire input string is processed and
	 * the machine is in a final state, return true.
	 * 
	 * @return true if the simulation of the input string on the automaton left
	 *         the machine in a final state.
	 */
	public boolean isAccepted() {
		Iterator<Configuration> it = myConfigurations.iterator();
		while (it.hasNext()) {
			FSAConfiguration configuration = (FSAConfiguration) it.next();
			State currentState = configuration.getCurrentState();
			if (configuration.getUnprocessedInput().equals("")
					&& myAutomaton.isFinalState(currentState)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Runs the automaton on the input string.
	 * 
	 * @param input
	 *            the input string to be run on the automaton
	 * @return true if the automaton accepts the input
	 */
	public boolean simulateInput(String input) {
		/** clear the configurations to begin new simulation. */
		myConfigurations.clear();
		Configuration[] initialConfigs = getInitialConfigurations(input);
		for (int k = 0; k < initialConfigs.length; k++) {
			FSAConfiguration initialConfiguration = (FSAConfiguration) initialConfigs[k];
			myConfigurations.add(initialConfiguration);
		}
		while (!myConfigurations.isEmpty()) {
			if (isAccepted())
				return true;
			ArrayList<Configuration> configurationsToAdd = new ArrayList<>();
			Iterator<Configuration> it = myConfigurations.iterator();
			while (it.hasNext()) {
				FSAConfiguration configuration = (FSAConfiguration) it.next();
				ArrayList<Configuration> configsToAdd = stepConfiguration(configuration);
				configurationsToAdd.addAll(configsToAdd);
				/**
				 * Remove configuration since just stepped from that
				 * configuration to all reachable configurations.
				 */
				it.remove();
			}
			myConfigurations.addAll(configurationsToAdd);
		}
		return false;
	}

}
