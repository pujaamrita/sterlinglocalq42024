package com.oms.order;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.oms.utils.CommonUtils;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class CreateSalesOrderInvoice {

	private static YFCLogCategory logger = (YFCLogCategory) YFCLogCategory.instance(CreateSalesOrderInvoice.class);

	/**
	 * Creating order invoice, during on success of creation of sales order(Regular) 
	 * and Monthly subscription orders(not creating for Quote & SubcriptionContract)
	 * @param env
	 * @param inDoc createOrder_ON_Success event output xml
	 * 
	 * Sample input xml for createOrderInvoice API call
	 * <Order DocumentType="0001" IgnoreStatusCheck="Y" IgnoreTransactionDependencies="Y" 
	 * LightInvoice="N" OrderHeaderKey="2024061007245251950" OrderNo="Y100000602"
	 *  TransactionId="CREATE_ORDER_INVOICE">
	 *	<OrderLines>
		<OrderLine  PrimeLineNo="1" Quantity="1" SubLineNo="1"/>
		</OrderLines>
		</Order>
	 */
	public void createOrderInvoice(YFSEnvironment env, Document inDoc){

		if(logger.isDebugEnabled()){
			logger.debug("Entering createOrderInvoice method with input: "+SCXmlUtil.getString(inDoc));
		} 
		//taking imp data from inDoc
		Element inDocOrderEle = inDoc.getDocumentElement();
		String orderType = inDocOrderEle.getAttribute("OrderType");
		//only doing for Normal order & Monthly subscription orders
		if(!YFCCommon.isVoid(orderType)  && 
				(orderType.equals("Sales(Regular)") || orderType.equalsIgnoreCase("MonthlyRenewal"))){
			Element orderLinesIndoc = SCXmlUtil.getChildElement(inDocOrderEle, "OrderLines");
			ArrayList<Element> nOrderLineList = SCXmlUtil.getChildren(orderLinesIndoc, "OrderLine");

			//preparing API input
			Document createOrderInvoiceDoc = SCXmlUtil.createDocument("Order");
			Element  createOrderInvoiceEle = createOrderInvoiceDoc.getDocumentElement();
			//setting header attributes
			createOrderInvoiceEle.setAttribute("DocumentType", "0001");
			createOrderInvoiceEle.setAttribute("IgnoreStatusCheck", "Y");
			createOrderInvoiceEle.setAttribute("IgnoreTransactionDependencies", "Y");
			createOrderInvoiceEle.setAttribute("LightInvoice", "N");
			createOrderInvoiceEle.setAttribute("OrderHeaderKey", inDocOrderEle.getAttribute("OrderHeaderKey"));
			createOrderInvoiceEle.setAttribute("OrderNo", inDocOrderEle.getAttribute("OrderNo"));
			createOrderInvoiceEle.setAttribute("TransactionId", "CREATE_ORDER_INVOICE");

			//orderLine details
			Element orderLines = SCXmlUtil.createChild(createOrderInvoiceEle, "OrderLines");
			//for each input orderLine prepare new input for api
			for(Element orderLineIn : nOrderLineList){

				Element orderLine =  SCXmlUtil.createChild(orderLines, "OrderLine");
				orderLine.setAttribute("PrimeLineNo", orderLineIn.getAttribute("PrimeLineNo"));
				//as no Quantity attribute so taking this from status Quntity
				orderLine.setAttribute("Quantity", orderLineIn.getAttribute("StatusQuantity"));
				orderLine.setAttribute("SubLineNo", orderLineIn.getAttribute("SubLineNo"));
			}

			if(logger.isDebugEnabled()){
				logger.debug("Input for createOrderInvoice input is: "+SCXmlUtil.getString(createOrderInvoiceDoc));
			}

			Document orderInvoiceOutDoc = CommonUtils.invoke(env, "createOrderInvoice", createOrderInvoiceDoc);

			if(logger.isDebugEnabled()){
				logger.debug("Output of createOrderInvoice is: "+SCXmlUtil.getString(orderInvoiceOutDoc));
			} 

		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Not doing order invoicing as its of Type Quote ");
			}
		}
	}	
}
