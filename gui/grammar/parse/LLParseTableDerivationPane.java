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





package gui.grammar.parse;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import grammar.Grammar;
import grammar.parse.LLParseTable;
import gui.SplitPaneFactory;
import gui.TableTextSizeSlider;
import gui.environment.GrammarEnvironment;
import gui.grammar.GrammarTable;

/**
 * This is the view for the derivation of a LL parse table from a grammar.
 * 
 * @author Thomas Finley
 */

public class LLParseTableDerivationPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new derivation pane for a grammar environment.
	 * 
	 * @param environment
	 *            the grammar environment
	 */
	public LLParseTableDerivationPane(GrammarEnvironment environment) {
		super(new BorderLayout());
		Grammar g = environment.getGrammar();
		JPanel right = new JPanel(new BorderLayout());

		JLabel description = new JLabel();
		right.add(description, BorderLayout.NORTH);

		// FirstFollowModel ffmodel = new FirstFollowModel(g);
		FirstFollowTable fftable = new FirstFollowTable(g);
		fftable.getColumnModel().getColumn(0).setPreferredWidth(30);
		fftable.getFFModel().setCanEditFirst(true);
		fftable.getFFModel().setCanEditFollow(true);

		LLParseTable parseTableModel = new LLParseTable(g) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int r, int c) {
				return controller.step != LLParseDerivationController.FINISHED
						&& super.isCellEditable(r, c);
			}
		};
		parseTable = new LLParseTablePane(parseTableModel);
		parseTable.getColumnModel().getColumn(0).setPreferredWidth(30);

		JSplitPane rightSplit = SplitPaneFactory.createSplit(environment,
				false, 0.5, new JScrollPane(fftable), new JScrollPane(
						parseTable));
		right.add(rightSplit, BorderLayout.CENTER);
		right.add(new TableTextSizeSlider(fftable, JSlider.HORIZONTAL), BorderLayout.NORTH);
		right.add(new TableTextSizeSlider(parseTable, JSlider.HORIZONTAL), BorderLayout.SOUTH);
		controller = new LLParseDerivationController(g, environment, fftable,
				parseTable, description);

		GrammarTable table = new GrammarTable(
				new gui.grammar.GrammarTableModel(g) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public boolean isCellEditable(int r, int c) {
						return false;
					}
				});
		JPanel grammarHolder = new JPanel();
		grammarHolder.setLayout(new BorderLayout());
		grammarHolder.add(new TableTextSizeSlider(table, JSlider.HORIZONTAL), BorderLayout.NORTH);
		grammarHolder.add(table, BorderLayout.CENTER);
		JSplitPane pane = SplitPaneFactory.createSplit(environment, true, 0.3,
				grammarHolder, right);
		this.add(pane, BorderLayout.CENTER);

		// Make the tool bar.
		JToolBar toolbar = new JToolBar();
		toolbar.add(controller.doSelectedAction);
		toolbar.add(controller.doStepAction);
		toolbar.add(controller.doAllAction);
		toolbar.addSeparator();
		toolbar.add(controller.nextAction);
		toolbar.addSeparator();
		toolbar.add(controller.parseAction);
		this.add(toolbar, BorderLayout.NORTH);
	}

	void makeParseUneditable() {
		editable = false;
		try {
			parseTable.getCellEditor().stopCellEditing();
		} catch (NullPointerException e) {
		}

	}

	private LLParseDerivationController controller;

	private LLParseTablePane parseTable;

	private boolean editable;
}
