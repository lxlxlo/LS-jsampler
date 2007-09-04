/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2007 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2
 *   as published by the Free Software Foundation.
 *
 *   JSampler is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with JSampler; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *   MA  02111-1307  USA
 */

package org.jsampler.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.jsampler.CC;
import org.jsampler.JSPrefs;

import org.linuxsampler.lscp.DbDirectoryInfo;
import org.linuxsampler.lscp.DbInstrumentInfo;

import org.linuxsampler.lscp.event.InstrumentsDbAdapter;
import org.linuxsampler.lscp.event.InstrumentsDbEvent;


/**
 *
 * @author Grigor Iliev
 */
public abstract class AbstractInstrumentsDbTable extends JTable {
	private final DefaultCellEditor nameEditor;
	private final JTextField tfEditor = new JTextField();
	
	private DbDirectoryTreeNode directoryNode;
	private final InstrumentsDbTableRowSorter rowSorter;
	
	private String createdDirectoryName = null;
	
	private InstrumentsDbTableView view = null;
	
	/**
	 * Creates a new instance of <code>AbstractInstrumentsDbTable</code>
	 */
	public AbstractInstrumentsDbTable() {
		this(new InstrumentsDbTableModel());
	}
	
	/**
	 * Creates a new instance of <code>AbstractInstrumentsDbTable</code>
	 */
	public AbstractInstrumentsDbTable(InstrumentsDbTableModel model) {
		super(model);
		setView(CC.getViewConfig().getInstrumentsDbTableView());
		
		String s;
		InstrumentsDbTableModel m = getModel();
		
		s = "DbInstrumentsTable.ShowSizeColumn";
		m.setShowSizeColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowFormatFamilyColumn";
		m.setShowFormatFamilyColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowFormatVersionColumn";
		m.setShowFormatVersionColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowIsDrumColumn";
		m.setShowIsDrumColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowCreatedColumn";
		m.setShowCreatedColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowModifiedColumn";
		m.setShowModifiedColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowProductColumn";
		m.setShowProductColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowArtistsColumn";
		m.setShowArtistsColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowInstrumentFileColumn";
		m.setShowInstrumentFileColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowInstrumentNrColumn";
		m.setShowInstrumentNrColumn(preferences().getBoolProperty(s));
		s = "DbInstrumentsTable.ShowKeywordsColumn";
		m.setShowKeywordsColumn(preferences().getBoolProperty(s));
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
		
		rowSorter = new InstrumentsDbTableRowSorter(getModel());
		setRowSorter(rowSorter);
		
		putClientProperty("JTable.autoStartsEdit", false);
		
		nameEditor = new DefaultCellEditor(tfEditor);
		nameEditor.setClickCountToStart(5);
		
		if(m.getShowDummyColumn()) {
			TableColumn dummy;
			dummy = getColumnModel().getColumn(getModel().getDummyColumnIndex());
			dummy.setPreferredWidth(10);
		}
	}
	
	public InstrumentsDbTableModel
	getModel() { return (InstrumentsDbTableModel)super.getModel(); }
	
	public TableCellEditor
	getCellEditor(int row, int column) {
		if(column == 0) return nameEditor;
		return super.getCellEditor(row, column);
	}
	
	public DbDirectoryTreeNode
	getSelectedDirectoryNode() {
		int idx = getSelectedRow();
		if(idx == -1) return null;
		idx = convertRowIndexToModel(idx);
		return getModel().getDirectoryNode(idx);
	}
	
	/**
	 * Selects the specified directory.
	 * The current selection is not altered if missing directory is specified.
	 * @param dir The name of the directory to select.
	 */
	public void
	setSelectedDirectory(String dir) {
		int i = getModel().getDirectoryRowIndex(dir);
		if(i == -1) return;
		i = convertRowIndexToView(i);
		if(i == -1) return;
		
		getSelectionModel().setSelectionInterval(i, i);
	}
	
	/**
	 * Selects the specified instrument.
	 * The current selection is not altered if missing instrument is specified.
	 * @param instr The name of the instrument to select.
	 */
	public void
	setSelectedInstrument(String instr) {
		int i = getModel().getInstrumentRowIndex(instr);
		if(i == -1) return;
		i = convertRowIndexToView(i);
		if(i == -1) return;
		getSelectionModel().setSelectionInterval(i, i);
	}
	
	/**
	 * Gets all selected directories.
	 */
	public DbDirectoryInfo[]
	getSelectedDirectories() {
		int[] rows = getSelectedRows();
		if(rows.length == 0) return new DbDirectoryInfo[0];
		
		DbDirectoryTreeNode dir;
		Vector<DbDirectoryInfo> v = new Vector<DbDirectoryInfo>();
		for(int i : rows) {
			dir = getModel().getDirectoryNode(convertRowIndexToModel(i));
			if(dir != null) v.add(dir.getInfo());
		}
		
		return v.toArray(new DbDirectoryInfo[v.size()]);
	}
	
	/**
	 * Gets all selected instruments.
	 */
	public DbInstrumentInfo[]
	getSelectedInstruments() {
		int[] rows = getSelectedRows();
		if(rows.length == 0) return new DbInstrumentInfo[0];
		
		DbInstrumentInfo instr;
		Vector<DbInstrumentInfo> v = new Vector<DbInstrumentInfo>();
		for(int i : rows) {
			instr = getModel().getInstrument(convertRowIndexToModel(i));
			if(instr != null) v.add(instr);
		}
		
		return v.toArray(new DbInstrumentInfo[v.size()]);
	}
	
	public boolean
	editCellAt(int row, int column) {
		if(!super.editCellAt(row, column)) return false;
		
		Component c = getEditorComponent();
		if(c != null) c.requestFocusInWindow();
		
		return true;
	}
	
	/**
	 * Gets the directory node, which
	 * content is represented by this table.
	 */
	public DbDirectoryTreeNode
	getParentDirectoryNode() { return directoryNode; }
	
	/**
	 * Sets the directory node, which
	 * content will be represented by this table.
	 */
	public void
	setParentDirectoryNode(DbDirectoryTreeNode node) {
		getModel().setParentDirectoryNode(node);
		
		if(directoryNode != null) directoryNode.removeInstrumentsDbListener(getHandler());
		directoryNode = node;
		if(directoryNode != null) directoryNode.addInstrumentsDbListener(getHandler());
	}
	
	/**
	 * Gets the name of the directory created by this frontend.
	 * This method is used to determine the directories created
	 * by this frontend.
	 * @return The name of the directory created by this frontend.
	 */
	public String
	getCreatedDirectoryName() { return createdDirectoryName; }
	
	/**
	 * Sets the name of the created by this frontend.
	 * @param name The name of the directory created by this frontend.
	 */
	public void
	setCreatedDirectoryName(String name) { createdDirectoryName = name; }
	
	public Object
	getLeadObject() {
		if(getSelectionModel().isSelectionEmpty()) return null;
		int idx = getSelectionModel().getLeadSelectionIndex();
		if(idx < 0) return null;
		idx = this.convertRowIndexToModel(idx);
		
		return getModel().getValueAt(idx, 0);
	}
	
	/** Sets the view to be used for retrieving UI information. */
	public void
	setView(InstrumentsDbTableView view) {
		this.view = view;
	}
	
	/** Gets the view used to retrieve UI information. */
	public InstrumentsDbTableView
	getView() { return view; }
	
	private JSPrefs
	preferences() { return CC.getViewConfig().preferences(); }
		
	public void
	loadColumnWidths() {
		InstrumentsDbTableModel m = getModel();
		TableColumnModel tcm = getColumnModel();
		
		for(int i = 0; i < m.getColumnCount(); i++) {
			switch(m.getColumnType(i)) {
			case NAME:
				String s = "DbInstrumentsTable.nameColumnWidth";
				int w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case SIZE:
				s = "DbInstrumentsTable.sizeColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case FORMAT_FAMILY:
				s = "DbInstrumentsTable.formatFamilyColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case FORMAT_VERSION:
				s = "DbInstrumentsTable.formatVersionColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case IS_DRUM:
				s = "DbInstrumentsTable.isDrumColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case CREATED:
				s = "DbInstrumentsTable.createdColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case MODIFIED:
				s = "DbInstrumentsTable.modifiedColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case PRODUCT:
				s = "DbInstrumentsTable.productColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case ARTISTS:
				s = "DbInstrumentsTable.artistsColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case INSTRUMENT_FILE:
				s = "DbInstrumentsTable.instrumentFileColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case INSTRUMENT_NR:
				s = "DbInstrumentsTable.instrumentNrColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case KEYWORDS:
				s = "DbInstrumentsTable.keywordsColumnWidth";
				w = preferences().getIntProperty(s);
				if(w > 0) tcm.getColumn(i).setPreferredWidth(w);
				break;
			case DUMMY:
				tcm.getColumn(i).setPreferredWidth(10);
				break;
			}
		}
	}
	
	public void
	saveColumnWidths() {
		InstrumentsDbTableModel m = getModel();
		TableColumnModel tcm = getColumnModel();
		
		for(int i = 0; i < m.getColumnCount(); i++) {
			switch(m.getColumnType(i)) {
			case NAME:
				String s = "DbInstrumentsTable.nameColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case SIZE:
				s = "DbInstrumentsTable.sizeColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case FORMAT_FAMILY:
				s = "DbInstrumentsTable.formatFamilyColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case FORMAT_VERSION:
				s = "DbInstrumentsTable.formatVersionColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case IS_DRUM:
				s = "DbInstrumentsTable.isDrumColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case CREATED:
				s = "DbInstrumentsTable.createdColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case MODIFIED:
				s = "DbInstrumentsTable.modifiedColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case PRODUCT:
				s = "DbInstrumentsTable.productColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case ARTISTS:
				s = "DbInstrumentsTable.artistsColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case INSTRUMENT_FILE:
				s = "DbInstrumentsTable.instrumentFileColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case INSTRUMENT_NR:
				s = "DbInstrumentsTable.instrumentNrColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			case KEYWORDS:
				s = "DbInstrumentsTable.keywordsColumnWidth";
				preferences().setIntProperty(s, tcm.getColumn(i).getWidth());
				break;
			
			}
		}
	}
	
	public void
	saveColumnsVisibleState() {
		InstrumentsDbTableModel m = getModel();
		
		String s = "DbInstrumentsTable.ShowSizeColumn";
		preferences().setBoolProperty(s, m.getShowSizeColumn());
		s = "DbInstrumentsTable.ShowFormatFamilyColumn";
		preferences().setBoolProperty(s, m.getShowFormatFamilyColumn());
		s = "DbInstrumentsTable.ShowFormatVersionColumn";
		preferences().setBoolProperty(s, m.getShowFormatVersionColumn());
		s = "DbInstrumentsTable.ShowIsDrumColumn";
		preferences().setBoolProperty(s, m.getShowIsDrumColumn());
		s = "DbInstrumentsTable.ShowCreatedColumn";
		preferences().setBoolProperty(s, m.getShowCreatedColumn());
		s = "DbInstrumentsTable.ShowModifiedColumn";
		preferences().setBoolProperty(s, m.getShowModifiedColumn());
		s = "DbInstrumentsTable.ShowProductColumn";
		preferences().setBoolProperty(s, m.getShowProductColumn());
		s = "DbInstrumentsTable.ShowArtistsColumn";
		preferences().setBoolProperty(s, m.getShowArtistsColumn());
		s = "DbInstrumentsTable.ShowInstrumentFileColumn";
		preferences().setBoolProperty(s, m.getShowInstrumentFileColumn());
		s = "DbInstrumentsTable.ShowInstrumentNrColumn";
		preferences().setBoolProperty(s, m.getShowInstrumentNrColumn());
		s = "DbInstrumentsTable.ShowKeywordsColumn";
		preferences().setBoolProperty(s, m.getShowKeywordsColumn());
	}
	
	/*public void
	columnMarginChanged(ChangeEvent e) {
		if(isEditing()) removeEditor();
		TableColumn resizingColumn = null;
		if(getTableHeader() != null) resizingColumn = getTableHeader().getResizingColumn();
		if (resizingColumn != null && autoResizeMode == AUTO_RESIZE_OFF) {
			resizingColumn.setPreferredWidth(resizingColumn.getWidth());
		}
		setAutoResizeMode();
		resizeAndRepaint();
	}
	
	public boolean
	getScrollableTracksViewportWidth() {
		return getPreferredSize().width < getParent().getWidth();
	}
	
	private void
	setAutoResizeMode() {
		Container c = getParent();
		if (!(c instanceof JViewport)) return;
		
		JViewport vp = (JViewport)c;
		int w = vp.getWidth();
		TableColumn dummy = getColumnModel().getColumn(getModel().getDummyColumnIndex());
		int i = w - (getColumnModel().getTotalColumnWidth() - dummy.getWidth());
		if(i > 5) {
			if(getAutoResizeMode() != AUTO_RESIZE_LAST_COLUMN)
				setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
		} else {
			if(getAutoResizeMode() != AUTO_RESIZE_OFF)
				setAutoResizeMode(AUTO_RESIZE_OFF);
		}
	}*/
	
	private class InstrumentsDbTableRowSorter extends TableRowSorter<InstrumentsDbTableModel> {
		InstrumentsDbTableRowSorter(InstrumentsDbTableModel model) {
			super(model);
		}
		
		public Comparator<?>
		getComparator(int column) {
			Comparator c = getModel().getComparator(column);
			if(c != null) return c;
			
			return super.getComparator(column);
		}
		
		protected boolean
		useToString(int column) {
			return getModel().getComparator(column) == null;
		}
		
		public boolean
		isSortable(int column) {
			return getModel().isSortable(column);
		}
	}
	
	private final EventHandler eventHandler = new EventHandler();
	
	private EventHandler
	getHandler() { return eventHandler; }
	
	private class EventHandler extends InstrumentsDbAdapter {
		
		/**
		 * Invoked when the number of instrument
		 * directories in a specific directory has changed.
		 */
		public void
		directoryCountChanged(InstrumentsDbEvent e) {
			String rd = getModel().getRenamedDirectory();
			int idx = getModel().getDirectoryRowIndex(rd);
			if(idx != -1) {
				setSelectedDirectory(rd);
				getModel().setRenamedDirectory(null);
			}
			
			idx = getModel().getDirectoryRowIndex(getCreatedDirectoryName());
			if(idx != -1) {
				idx = convertRowIndexToView(idx);
				if(idx != -1) {
					getSelectionModel().setSelectionInterval(idx, idx);
					editCellAt(idx, 0);
					Component c = nameEditor.getComponent();
					if(c instanceof JTextField) ((JTextField)c).selectAll();
					setCreatedDirectoryName(null);
				}
			}
		}
		
		/**
		 * Invoked when the number of instruments
		 * in a specific directory has changed.
		 */
		public void
		instrumentCountChanged(InstrumentsDbEvent e) {
			String ri = getModel().getRenamedInstrument();
			int idx = getModel().getInstrumentRowIndex(ri);
			if(idx != -1) {
				setSelectedInstrument(ri);
				getModel().setRenamedInstrument(null);
			}
		}
	}
}