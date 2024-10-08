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





package gui.grammar.convert;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import automata.Automaton;
import automata.Transition;
import grammar.Grammar;
import grammar.Production;
import gui.SplitPaneFactory;
import gui.TableTextSizeSlider;
import gui.editor.ArrowNontransitionTool;
import gui.editor.EditorPane;
import gui.editor.Tool;
import gui.editor.ToolBox;
import gui.editor.TransitionTool;
import gui.environment.Environment;
import gui.viewer.AutomatonDrawer;
import gui.viewer.AutomatonPane;
import gui.viewer.SelectionDrawer;

/**
 * This is a graphical component to aid in the conversion of a context free
 * grammar to some form of pushdown automaton.
 * 
 * @author Thomas Finley
 */

public class ConvertPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a <CODE>ConvertPane</CODE>.
	 * 
	 * @param grammar
	 *            the grammar to convert
	 * @param automaton
	 *            a "starting automaton" that may already have some start points
	 *            predefined
	 * @param productionsToTransitions
	 *            the mapping of productions to transitions, which should be one
	 *            to one
	 * @param env
	 *            the environment to which this pane will be added
	 */
	public ConvertPane(Grammar grammar, Automaton automaton,
			Map<Production, Transition> productionsToTransitions, Environment env) {
		this.grammar = grammar;
		this.automaton = automaton;

		this.setLayout(new BorderLayout());
		JSplitPane split = SplitPaneFactory.createSplit(env, true, .4, null,
				null);
		this.add(split, BorderLayout.CENTER);

		grammarViewer = new GrammarViewer(grammar);
		this.add(new TableTextSizeSlider(grammarViewer, JSlider.HORIZONTAL), BorderLayout.NORTH);
		JScrollPane scroller = new JScrollPane(grammarViewer);
		split.setLeftComponent(scroller);
		// Create the right view.

		automatonDrawer = new SelectionDrawer(automaton);
		EditorPane ep = new EditorPane(automatonDrawer, new ToolBox() {
			public java.util.List<Tool> tools(AutomatonPane view,
					AutomatonDrawer drawer) {
				LinkedList<Tool> tools = new LinkedList<>();
				tools.add(new ArrowNontransitionTool(view, drawer));
				tools.add(new TransitionTool(view, drawer));
				return tools;
			}
		});
		// Create the controller device.
		ConvertController controller = new ConvertController(grammarViewer,
				automatonDrawer, productionsToTransitions, this);
		controlPanel(ep.getToolBar(), controller);
		split.setRightComponent(ep);
		editorPane = ep;
	}

	/**
	 * Initializes the control objects in the editor pane's tool bar.
	 * 
	 * @param controller
	 *            the controller object
	 */
	private void controlPanel(JToolBar bar, final ConvertController controller) {
		bar.addSeparator();
		bar.add(new AbstractAction("Show All") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				controller.complete();
			}
		});
		bar.add(new AbstractAction("Create Selected") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				controller.createForSelected();
			}
		});
		bar.add(new AbstractAction("Done?") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				controller.isDone();
			}
		});
		bar.add(new AbstractAction("Export") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				controller.export();
			}
		});
	}

	/**
	 * 
	 * /** Returns the editor pane.
	 * 
	 * @return the editor pane
	 */
	public EditorPane getEditorPane() {
		return editorPane;
	}

	/** The grammar that this convertpane holds. */
	private Grammar grammar;

	/** The grammar viewer. */
	private GrammarViewer grammarViewer;

	/** The automaton selection drawer. */
	private SelectionDrawer automatonDrawer;

	/** The automaton. */
	private Automaton automaton;

	/** The editor pane. */
	private EditorPane editorPane;
}
