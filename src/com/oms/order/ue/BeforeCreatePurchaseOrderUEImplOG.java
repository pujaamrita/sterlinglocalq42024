package com.oms.order.ue;

import org.w3c.dom.Document;

import com.oms.utils.CommonUtils;
import com.oms.utils.OmsConstants;
import com.oms.utils.OmsLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

public class BeforeCreatePurchaseOrderUEImplOG implements YFSBeforeCreateOrderUE{
	
	/**
	 * Purpose:- 
	 * 			Notes will be copied from Sales order to ProcuremetPO on Successful creation of Procurement Order.
	 * 
	 * How to Test:-
	 * 	            Create SO , adding notes with reason CUSTOMER_TO_IBM , then schedule it
	 *              Procurement PO should get created and validate the same Note on PO with reason Code IBM_TO_SUPPLIER.
	 **/

	@Override
	public String beforeCreateOrder(YFSEnvironment arg0, String arg1) throws YFSUserExitException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document beforeCreateOrder(YFSEnvironment env, Document doc) throws YFSUserExitException {
		
		YFCDocument yfcDoc= YFCDocument.getDocumentFor(doc);
		YFCElement  yfcDocEle = yfcDoc.getDocumentElement();
		YFCElement yfcDocEleNotes=yfcDocEle.createChild("Notes");

		YFCDocument orderYfcDoc = YFCDocument.createDocument("Order");
		YFCElement orderEle = orderYfcDoc.getDocumentElement();

		String getCompleteOrderDetailsTemplate = "<OrderList>\r\n"
				+ "   <Order>\r\n"
				+ "       <Notes>\r\n"
				+ "           <Note/>\r\n"
				+ "       </Notes>\r\n"
				+ "       <OrderLines>\r\n"
				+ "           <OrderLine/>\r\n"
				+ "       </OrderLines>\r\n"
				+ "   </Order>\r\n"
				+ "</OrderList>";
	
		YFCNodeList<YFCElement> purchaseOrderLines 	= 	yfcDoc.getElementsByTagName("OrderLine");
		for (YFCElement purchaseOrderLine : purchaseOrderLines) {

			String chainedFromOrderHeaderKey = purchaseOrderLine.getAttribute("ChainedFromOrderHeaderKey");
			orderEle.setAttribute("OrderHeaderKey", chainedFromOrderHeaderKey);

			Document getCompleteParentSalesOrderDetailsDoc = CommonUtils.invoke(env,
					OmsConstants.GET_ORDER_LIST_API,
					YFCDocument.getDocumentFor(getCompleteOrderDetailsTemplate).getDocument(),
					orderYfcDoc.getDocument());

			/**
			 * As question to IBM from Customer must be asked to the Supplier once we create the Procurement Order with other reason code.
			 */
			
			YFCNodeList<YFCElement> notes = YFCDocument.getDocumentFor(getCompleteParentSalesOrderDetailsDoc)
					.getElementsByTagName("Note");
			for (YFCElement note : notes) {

				if (OmsConstants.CUST_TO_IBM.equals(note.getAttribute("ReasonCode"))) {

					note.setAttribute("ReasonCode", OmsConstants.IBM_TO_SUPPLIER);
					note.setAttribute("ContactUser", env.getUserId());
					note.setAttribute("Createuserid", env.getUserId());
					note.removeAttribute("AuditTransactionId");
					note.removeAttribute("NotesKey");
					note.removeAttribute("Createprogid");
					note.removeAttribute("Modifyuserid");
					note.removeAttribute("Createts");
					note.removeAttribute("Modifyts");
					note.removeAttribute("TableKey");
					note.removeAttribute("TableName");
					note.removeAttribute("Lockid");
					note.removeAttribute("Modifyprogid");
					note.removeAttribute("Priority");
					note.removeAttribute("SequenceNo");
					note.removeAttribute("CustomerSatIndicator");
					note.removeAttribute("Tranid");
					note.removeAttribute("isHistory");
					
					YFCElement appendPurchaseOrderNoteEle = yfcDoc.importNode(note, true);
					yfcDocEleNotes.appendChild((YFCNode) appendPurchaseOrderNoteEle);
					//break; 
				}
			}
			break; // This would copy the notes only once.
		}
		return doc;
	}
}