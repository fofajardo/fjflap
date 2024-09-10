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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.OverlayLayout;
import javax.swing.border.EmptyBorder;

// import java.applet.Applet;
// import java.applet.AudioClip;

/**
 * The <TT>AboutBox</TT> is the about box for JFLAP.
 * 
 * @author Thomas Finley
 */

public class AboutBox extends JWindow {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new <TT>AboutBox</TT>.
	 * 
	 * @param owner
	 *            the owner of this about box
	 */
	public AboutBox(Frame owner) {
		super(owner);
		getContentPane().setLayout(new OverlayLayout(getContentPane()));
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(3, 3, 3, 3));
		JPanel fullPanel = new JPanel(new BorderLayout());
		fullPanel.setOpaque(false);
		panel.add(fullPanel, BorderLayout.SOUTH);
		getContentPane().add(panel);
		getContentPane().add(new ImageDisplayComponent(IMAGE));
		addMouseListener(new BoxDismisser());
	}

	/**
	 * Returns a label with the appropriate string.
	 * 
	 * @param string
	 *            the string to display
	 * @return a properly created JLabel visible on this frame
	 */
	private static JLabel getLabel(String string) {
		JLabel label = new JLabel(string);
		if (IMAGE != null)
			label.setForeground(Color.black);
		return label;
	}

	/**
	 * Instantiates a new <TT>AboutBox</TT> with no specified owner.
	 */
	public AboutBox() {
		this((Frame) null);
	}

	/**
	 * Displays this about box, and plays the clip.
	 */
	public void displayBox() {
		boolean toPlay = !isVisible();
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension boxSize = getPreferredSize();
		setLocation((screenSize.width - boxSize.width) >> 1,
				(screenSize.height - boxSize.height) >> 1);
		toFront();
		setVisible(true);
	}

	/**
	 * Dismisses this about box, and stops the clip.
	 */
	public void dismissBox() {
		dispose();
		// CLIP.stop();
	}

	/**
	 * This listens for clicks on the box. When it receives them, the box is
	 * dismissed.
	 */
	private class BoxDismisser extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			dismissBox();
		}
	}

	/** The image to display in the about box. */
	private static Image IMAGE = null;

	/** The version string. */
	public static final String VERSION = "7.1";

	/**
	 * Some simple test code for the about box.
	 */
	public static void main(String args[]) {
		AboutBox box = new AboutBox();
		box.displayBox();
	}

	static {
		try {
			IMAGE = Toolkit.getDefaultToolkit().getImage(
					AboutBox.class.getResource("/MEDIA/about.png"));
		} catch (NullPointerException e) {

		}
	}
}
