/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.adempiere.pos.search;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.compiere.grid.ed.VDate;
import org.compiere.minigrid.ColumnInfo;
import org.compiere.minigrid.IDColumn;
import org.compiere.model.PO;
import org.adempiere.pos.POSTextField;
import org.adempiere.pos.VPOS;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CLabel;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	POS Query Ticket
 *	
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Jose A.Gonzalez, Conserti.
 *  author $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 *  @author Dixon Martinez, ERPCYA 
 *  @author Susanne Calderón Schöningh, Systemhaus Westfalia
 *  @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 *  @author victor.perez@e-evolution.com , http://www.e-evolution.com
 *  <li> Implement best practices
 *  @version $Id: QueryTicket.java,v 0.9 $
 *  @version $Id: QueryProduct.java,v 1.1 jjanke Exp $
 *  @version $Id: QueryProduct.java,v 2.0 2015/09/01 00:00:00 scalderon
 * 
 */

public class QueryTicket extends POSQuery {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7713957495649128816L;
	/**
	 * 	Constructor
	 */
	public QueryTicket (VPOS posPanel) {
		super(posPanel);
	}	//	PosQueryProduct

	/**	Search Fields		*/
	private POSTextField 	fieldDocumentNo;
	private VDate 			fieldDateFrom;
	private VDate 			fieldDateTo;
	private CCheckBox 		fieldProcessed;
	/**	Internal Variables	*/
	private int 			orderId;
	
	
	static final private String DOCUMENTNO      = "DocumentNo";
	static final private String BPARTNERID      = "C_BPartner_ID";
	static final private String GRANDTOTAL      = "GrandTotal";
	static final private String OPENAMT         = "OpenAmt";
	static final private String PAID            = "IsPaid";
	static final private String PROCESSED       = "Processed";
	static final private String INVOICED       	= "IsInvoiced";
	static final private String DATEORDEREDFROM = "From";
	static final private String DATEORDEREDTO   = "To";
	static final private String QUERY           = "Query";

	/**	Table Column Layout Info			*/
	private static ColumnInfo[] columnInfos = new ColumnInfo[] {
		new ColumnInfo(" ", "C_Order_ID", IDColumn.class),
		new ColumnInfo(Msg.translate(Env.getCtx(), DOCUMENTNO), DOCUMENTNO, String.class),
		new ColumnInfo(Msg.translate(Env.getCtx(), BPARTNERID), BPARTNERID, String.class),
		new ColumnInfo(Msg.translate(Env.getCtx(), GRANDTOTAL), GRANDTOTAL, BigDecimal.class),
		new ColumnInfo(Msg.translate(Env.getCtx(), OPENAMT), OPENAMT, BigDecimal.class),
		new ColumnInfo(Msg.translate(Env.getCtx(), PAID), PAID, Boolean.class), 
		new ColumnInfo(Msg.translate(Env.getCtx(), PROCESSED), PROCESSED, Boolean.class), 
		new ColumnInfo(Msg.translate(Env.getCtx(), INVOICED), INVOICED, Boolean.class)
	};

	/**
	 * 	Set up Panel
	 */
	@Override
	protected void init() {
		setTitle(Msg.translate(Env.getCtx(), "C_Order_ID"));
		//	North
		parameterPanel.setLayout(new MigLayout("fill","", "[50][50][]"));
		parameterPanel.setBorder(new TitledBorder(Msg.getMsg(ctx, QUERY)));
		
		CLabel labelDocument = new CLabel(Msg.translate(ctx, DOCUMENTNO));
		parameterPanel.add (labelDocument, " growy");
		fieldDocumentNo = new POSTextField("", posPanel.getKeyboard());
		labelDocument.setLabelFor(fieldDocumentNo);
		parameterPanel.add(fieldDocumentNo, "h 30, w 200");
		fieldDocumentNo.addActionListener(this);
		//
		CLabel labelDateFrom = new CLabel(Msg.translate(ctx, DATEORDEREDFROM));
		parameterPanel.add (labelDateFrom, "growy");
		fieldDateFrom = new VDate();
		fieldDateFrom.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		labelDateFrom.setLabelFor(fieldDateFrom);
		parameterPanel.add(fieldDateFrom, "h 30, w 200");
		fieldDateFrom.addVetoableChangeListener(this);
		
		// Date To
		CLabel labelDateTo = new CLabel(Msg.translate(ctx, DATEORDEREDTO));
		parameterPanel.add (labelDateTo, "growy");
		fieldDateTo = new VDate();
		fieldDateTo.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		labelDateTo.setLabelFor(fieldDateTo);
		parameterPanel.add(fieldDateTo, "h 30, w 200");
		fieldDateTo.addVetoableChangeListener(this);
		
		fieldProcessed = new CCheckBox(Msg.translate(ctx, PROCESSED));
		fieldProcessed.setSelected(false);
		fieldProcessed.addActionListener(this);
		parameterPanel.add(fieldProcessed, "");
		//	
		posTable.prepareTable (columnInfos, "C_Order",
				"C_POS_ID = " + posPanel.getC_POS_ID()
				, false, "C_Order");
		posTable.growScrollbars();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() {
				fieldDocumentNo.requestFocus();
			}
		});

		pack();
		refresh();
	}	//	init
	
	/**
	 * 
	 */
	@Override
	public void reset() {
		fieldProcessed.setSelected(false);
		fieldDocumentNo.setText(null);
		fieldDateFrom.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		fieldDateTo.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		refresh();
	}
	
	/**
	 * Clean Values
	 * @return void
	 */
	private void cleanValues() {
		orderId = -1;
	}
	
	/**
	 * 	Set/display Results
	 *	@param results results
	 */
	public void setResultsFromArray(Properties ctx, boolean processed, String doc, Timestamp dateFrom, Timestamp dateTo) {
		StringBuffer sql = new StringBuffer();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try  {
			sql.append(" SELECT o.C_Order_ID, o.DocumentNo, ")
				.append(" b.Name, o.GrandTotal, ")
				.append(" COALESCE(SUM(invoiceopen(i.C_Invoice_ID, 0)), o.GrandTotal - SUM(p.PayAmt), o.GrandTotal) AS InvoiceOpen, ")
			    .append(" COALESCE(i.IsPaid, CASE WHEN o.GrandTotal - SUM(p.PayAmt) = 0 THEN 'Y' ELSE 'N' END) IsPaid, ")
			    .append(" o.Processed, ")
			    .append(" CASE WHEN COALESCE(COUNT(i.C_Invoice_ID), 0) > 0 THEN 'Y' ELSE 'N' END")
				.append(" FROM C_Order o ")
				.append(" INNER JOIN C_BPartner b ON (o.C_BPartner_ID = b.C_BPartner_ID)")
				.append(" LEFT JOIN C_invoice   i ON (i.C_Order_ID = o.C_Order_ID)")
				.append(" LEFT JOIN C_Payment   p ON (p.C_Order_ID = o.C_Order_ID)")
				.append(" WHERE  o.DocStatus <> 'VO'")
				.append(" AND o.C_POS_ID = ?")
				.append(" AND o.Processed= ?");
			if (doc != null && !doc.equalsIgnoreCase(""))
				sql.append(" AND (o.DocumentNo LIKE '%" + doc + "%' OR  i.DocumentNo LIKE '%" + doc + "%')");
			if ( dateFrom != null ) {
				if ( dateTo != null && !dateTo.equals(dateFrom))
					sql.append(" AND o.DateOrdered >= ? AND o.DateOrdered <= ?");						
				else
					sql.append(" AND o.DateOrdered = ? ");	
			}
			//	Group By
			sql.append(" GROUP BY o.C_Order_ID, o.DocumentNo, b.Name, o.GrandTotal, o.Processed, i.IsPaid ");
			sql.append(" ORDER BY o.Updated");
			int i = 1;			
			preparedStatement = DB.prepareStatement(sql.toString(), null);
			//	POS
			preparedStatement.setInt(i++, posPanel.getC_POS_ID());
			//	Processed
			preparedStatement.setString(i++, processed? "Y": "N");
			//	Date From and To
			if (dateFrom != null) {				
				preparedStatement.setTimestamp(i++, dateFrom);
				if (dateTo != null 
						&& !dateTo.equals(dateFrom)) {
					preparedStatement.setTimestamp(i++, dateTo);
				}
			}
			//	
			resultSet = preparedStatement.executeQuery();
			posTable.loadTable(resultSet);
			int rowNo = posTable.getRowCount();
			if (rowNo > 0) {
				posTable.setRowSelectionInterval(0, 0);
				if(rowNo == 1) {
					select();
				}
			}
		} catch(Exception e) {
			logger.severe("QueryTicket.setResults: " + e + " -> " + sql);
		} finally {
			DB.close(resultSet);
			DB.close(preparedStatement);
		}
	}	//	setResults

	/**
	 * 	Enable/Set Buttons and set ID
	 */
	protected void select() {
		cleanValues();
		int row = posTable.getSelectedRow();
		boolean enabled = row != -1;
		if (enabled)
		{
			Integer ID = posTable.getSelectedRowKey();
			if (ID != null)
			{
				orderId = ID.intValue();
			}
		}
		logger.info("ID=" + orderId);
	}	//	enableButtons

	/**
	 * 	Close.
	 * 	Set Values on other panels and close
	 */
	@Override
	protected void close() {
		select();	
		dispose();
	}	//	close
	
	@Override
	public void refresh() {
		cleanValues();
		setResultsFromArray(ctx, fieldProcessed.isSelected(), fieldDocumentNo.getText(),
				fieldDateFrom.getTimestamp(), fieldDateTo.getTimestamp());
	}
	
	@Override
	public void setResults(PO[] results) {
		//	
	}
	
	@Override
	protected void cancel() {
		cleanValues();
		dispose();
	}

	@Override
	public int getRecord_ID() {
		return orderId;
	}

	@Override
	public String getValue() {
		return null;
	}
}	//	QueryTicket