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




package gui;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import gui.action.BatchMultipleSimulateAction;
import gui.action.MultipleSimulateAction;

public class JTableExtender extends JTable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTableExtender(TableModel model, MultipleSimulateAction mult){
		super(model);
		myMultSimAct = mult;
	}
	
	public JTableExtender(TableModel model, BatchMultipleSimulateAction mult){
		super(model);
		myMultSimAct = mult;
	}
	
	
	public void changeSelection (int row, int column, boolean toggle, boolean extend) {
		 super.changeSelection (row, column, toggle, extend);
		 myMultSimAct.viewAutomaton(this);
		 }
	
	private MultipleSimulateAction myMultSimAct;
}
