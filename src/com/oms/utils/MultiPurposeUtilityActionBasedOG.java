package com.oms.utils;

import org.w3c.dom.Document;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class MultiPurposeUtilityActionBasedOG{
	/**
	 * Purpose:-   
	 * <OrderNo="" Action="" />
	 * Action List:-
	 * CHANGE_SO_SHIPMENT_TO_ARRIVED
	 * CHANGE_SO_SHIPMENT_TO_COMPLETED
	 * 
	 * CHANGE_DSV_CREATE_CHAINED_ORDER
	 * CHANGE_DSV_PO_TO_ACCEPTED
	 * CHANGE_DSV_PO_TO_SHIPPED
	 * 
	 **/

	public Document businessLogic(YFSEnvironment env, Document doc) {
		Document returnDoc = null;
		YFCDocument yfcDoc = YFCDocument.getDocumentFor(doc);
		YFCElement yfcDocEle = yfcDoc.getDocumentElement();
		String orderNo = yfcDocEle.getAttribute("OrderNo");
		String action = yfcDocEle.getAttribute("Action");

		String ActionList[] = { "CHANGE_SO_SHIPMENT_TO_ARRIVED", "CHANGE_SO_SHIPMENT_TO_COMPLETED",
				"CHANGE_DSV_SO_TO_CREATE_CHAINED_ORDER", "CHANGE_DSV_PO_TO_ACCEPTED", "CHANGE_DSV_PO_TO_SHIPPED" };

		YFCDocument orderYfcDoc = YFCDocument.createDocument("Order");
		YFCElement orderEle = orderYfcDoc.getDocumentElement();
		orderEle.setAttribute("OrderNo", orderNo);
		
		String getOrderListTemplate ="<OrderList>\r\n"
				+ "<Order>\r\n"
				+ "   <Notes>\r\n"
				+ "       <Note/>\r\n"
				+ "   </Notes>\r\n"
				+ "   <OrderLines>\r\n"
				+ "       <OrderLine/>\r\n"
				+ "   </OrderLines>\r\n"
				+ "</Order>\r\n"
				+ "</OrderList>";
		
		String getCompleteOrderDetailsTemplate="<Order SellerOrganizationCode=\"\" DocumentType=\"\" EnterpriseCode=\"\" OrderHeaderKey=\"\" OrderNo=\"\" Status=\"\" OverallStatus=\"\"  MaxOrderStatus=\"\" MaxOrderStatusDesc=\"\" MinOrderStatus=\"\" MinOrderStatusDesc=\"\" >\r\n"
				+ "   <OrderStatuses>\r\n"
				+ "       <OrderStatus/>\r\n"
				+ "   </OrderStatuses>\r\n"
				+ "   <Shipments>\r\n"
				+ "       <Shipment/>\r\n"
				+ "   </Shipments>\r\n"
				+ "   <OrderLines>\r\n"
				+ "       <OrderLine OrderLineKey=\"\" OrderedQty=\"\">\r\n"
				+ "           <OrderStatuses>\r\n"
				+ "               <OrderStatus OrderHeaderKey=\"\" OrderLineKey=\"\" OrderLineScheduleKey=\"\" OrderReleaseKey=\"\" OrderReleaseStatusKey=\"\" PipelineKey=\"\" ReceivingNode=\"\" ShipNode=\"\" Status=\"\" StatusDate=\"\" StatusDescription=\"\" StatusQty=\"\" StatusReason=\"\" TotalQuantity=\"\" />\r\n"
				+ "           </OrderStatuses>\r\n"
				+ "           <ShipmentLines>\r\n"
				+ "               <ShipmentLine />\r\n"
				+ "           </ShipmentLines>\r\n"
				+ "           <Shipnode AcceptanceRequired=\"\" ActivateFlag=\"\" CanShipToAllNodes=\"\" CanShipToOtherAddresses=\"\" ContactAddressKey=\"\" CurrentBolNumber=\"\" DefaultDeclaredValue=\"\" Description=\"\" ExportLicenseExpDate=\"\" ExportLicenseNo=\"\" ExportTaxpayerId=\"\" GenerateBolNumber=\"\" IdentifiedByParentAs=\"\" InterfaceSubType=\"\" InterfaceType=\"\" InventoryTracked=\"\" Inventorytype=\"\" IsItemBasedAllocationAllowed=\"\" Latitude=\"\" Localecode=\"\" Longitude=\"\" MaintainInventoryCost=\"\" NodeType=\"\" OwnerKey=\"\" OwnerType=\"\" PicklistType=\"\" ReceivingNode=\"\" RequiresChangeRequest=\"\" ReturnCenterFlag=\"\" ReturnsNode=\"\" ShipNode=\"\" ShipNodeAddressKey=\"\" ShipNodeClass=\"\" ShipnodeKey=\"\" ShipnodeType=\"\" ShippingNode=\"\" StoreInvConfig=\"\" SupplierKey=\"\" ThreePlNode=\"\" TimeDiff=\"\">\r\n"
				+ "               <ShipNodePersonInfo AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" AlternateEmailID=\"\" Beeper=\"\" City=\"\" Company=\"\" Country=\"\" DayFaxNo=\"\" DayPhone=\"\" Department=\"\" EMailID=\"\" ErrorTxt=\"\" EveningFaxNo=\"\" EveningPhone=\"\" FirstName=\"\" HttpUrl=\"\" IsCommercialAddress=\"\" JobTitle=\"\" LastName=\"\" MiddleName=\"\" MobilePhone=\"\" OtherPhone=\"\" PersonID=\"\" PersonInfoKey=\"\" PreferredShipAddress=\"\" State=\"\" Suffix=\"\" TaxGeoCode=\"\" Title=\"\" UseCount=\"\" VerificationStatus=\"\" ZipCode=\"\"/>\r\n"
				+ "           </Shipnode>\r\n"
				+ "           <ItemDetails CanUseAsServiceTool=\"\" GlobalItemID=\"\" IsItemSuperseded=\"\" ItemGroupCode=\"\" ItemID=\"\" ItemKey=\"\" OrganizationCode=\"Required\" SubCatalogOrganizationCode=\"\" UnitOfMeasure=\"\"/>\r\n"
				+ "       </OrderLine>\r\n"
				+ "   </OrderLines>\r\n"
				+ "   <Shipments>\r\n"
				+ "       <Shipment >\r\n"
				+ "           <ShipmentLines TotalNumberOfRecords=\"\">\r\n"
				+ "               <ShipmentLine >\r\n"
				+ "                   <OrderRelease />\r\n"
				+ "               </ShipmentLine>\r\n"
				+ "           </ShipmentLines>\r\n"
				+ "           <Status Description=\"\" OwnerKey=\"\" ProcessTypeKey=\"\" RequiresCollaboration=\"\" Status=\"\" StatusKey=\"\" StatusName=\"\" StatusType=\"\"/>\r\n"
				+ "           <ShipNode Description=\"\" IdentifiedByParentAs=\"\" OwnerKey=\"\"/>\r\n"
				+ "       </Shipment>\r\n"
				+ "   </Shipments>\r\n"
				+ "</Order>";
		
		Document getOrderListOutDoc = CommonUtils.invoke(env, OmsConstants.GET_ORDER_LIST_API,
				YFCDocument.getDocumentFor(getOrderListTemplate).getDocument(), orderYfcDoc.getDocument());

		String orderHeaderKey = SCXmlUtil.getXpathAttribute(getOrderListOutDoc.getDocumentElement(),
				"//OrderList/Order/@OrderHeaderKey");

		orderEle.setAttribute("OrderHeaderKey", orderHeaderKey);
		orderEle.removeAttribute("OrderNo");

		Document getCompleteOrderDetailsOutDoc = CommonUtils.invoke(env, OmsConstants.GET_COMPLETE_ORDER_DETAILS_API,
				YFCDocument.getDocumentFor(getCompleteOrderDetailsTemplate).getDocument(), orderYfcDoc.getDocument());
		
		String status = SCXmlUtil.getXpathAttribute(getCompleteOrderDetailsOutDoc.getDocumentElement(),
				"//Order/@Status");

		YFCDocument shipmentYDoc = YFCDocument.createDocument("Shipment");
		YFCElement shipmentYDocEle = shipmentYDoc.getDocumentElement();
		
		switch (action) {
		case "CHANGE_SO_SHIPMENT_TO_ARRIVED":

			if ("Shipped".equals(status)) {
				shipmentYDocEle.setAttribute("BaseDropStatus", "1400.100");
				shipmentYDocEle.setAttribute("SellerOrganizationCode", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@SellerOrganizationCode"));
				shipmentYDocEle.setAttribute("ShipNode", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/Shipments/Shipment/@ShipNode"));
				shipmentYDocEle.setAttribute("ShipmentNo", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/Shipments/Shipment/@ShipmentNo"));
				shipmentYDocEle.setAttribute("TransactionId", "SHIPMENT_ARRIVED.0001.ex");

				returnDoc = CommonUtils.invoke(env, OmsConstants.CHANGE_SHIPMENT_STATUS_API,
						/* YFCDocument.getDocumentFor(getOrderListTemplate).getDocument() */"",
						shipmentYDoc.getDocument());
			} else {
				throw new YFSException("Status for performing operation on " + action + " is invalidated",
						"Invalid Status", "Order Status is :" + status + ", Expected Status is Shipped");
			}
			break;
		case "CHANGE_SO_SHIPMENT_TO_COMPLETED":

			if ("Shipped".equals(status)) {
				shipmentYDocEle.setAttribute("BaseDropStatus", "1400.200");
				shipmentYDocEle.setAttribute("SellerOrganizationCode", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@SellerOrganizationCode"));
				shipmentYDocEle.setAttribute("ShipNode", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/Shipments/Shipment/@ShipNode"));
				shipmentYDocEle.setAttribute("ShipmentNo", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/Shipments/Shipment/@ShipmentNo"));
				shipmentYDocEle.setAttribute("TransactionId", "SHIPMENT_COMPLETED.0001.ex");

				returnDoc = CommonUtils.invoke(env, OmsConstants.CHANGE_SHIPMENT_STATUS_API,
						/* YFCDocument.getDocumentFor(getOrderListTemplate).getDocument() */"",
						shipmentYDoc.getDocument());
			} else {
				throw new YFSException("Status for performing operation on " + action + " is invalidated",
						"Invalid Status", "Order Status is :" + status + ", Expected Status is Shipped");
			}
			break;
		case "CHANGE_DSV_SO_TO_CREATE_CHAINED_ORDER":

			if ("Awaiting Chained Order Creation".equals(status)) {
				YFCDocument orderYDoc = YFCDocument.createDocument("Order");
				YFCElement orderYDocEle = orderYDoc.getDocumentElement();
				orderYDocEle.setAttribute("DocumentType", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@DocumentType"));
				orderYDocEle.setAttribute("EnterpriseCode", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@EnterpriseCode"));
				orderYDocEle.setAttribute("OrderHeaderKey", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@OrderHeaderKey"));
				orderYDocEle.setAttribute("OrderNo", SCXmlUtil
						.getXpathAttribute(getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@OrderNo"));

				returnDoc = CommonUtils.invoke(env, OmsConstants.CREATE_CHAINED_ORDER_API,
						/* YFCDocument.getDocumentFor(getOrderListTemplate).getDocument() */"",
						orderYDoc.getDocument());
			} else {
				throw new YFSException("Status for performing operation on " + action + " is invalidated",
						"Invalid Status",
						"Order Status is :" + status + ", Expected Status is: Awaiting Chained Order Creation");
			}

			break;
		case "CHANGE_DSV_PO_TO_ACCEPTED":

			if ("Being Negotiated".equals(status)) {
				YFCDocument orderStatusChangeYDoc = YFCDocument.createDocument("OrderStatusChange");
				YFCElement orderStatusChangeYDocEle = orderStatusChangeYDoc.getDocumentElement();
				orderStatusChangeYDocEle.setAttribute("BaseDropStatus", "1260");
				orderStatusChangeYDocEle.setAttribute("DocumentType", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@DocumentType"));
				orderStatusChangeYDocEle.setAttribute("EnterpriseCode", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@EnterpriseCode"));
				orderStatusChangeYDocEle.setAttribute("OrderHeaderKey", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@OrderHeaderKey"));
				orderStatusChangeYDocEle.setAttribute("OrderNo", SCXmlUtil
						.getXpathAttribute(getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@OrderNo"));
				orderStatusChangeYDocEle.setAttribute("TransactionId", "RECEIVE_ORD_NEGOTIATION.0005");

				returnDoc = CommonUtils.invoke(env, OmsConstants.CHANGE_ORDER_STATUS_API,
						/* YFCDocument.getDocumentFor(getOrderListTemplate).getDocument() */"",
						orderStatusChangeYDoc.getDocument());
			} else {
				throw new YFSException("Status for performing operation on " + action + " is invalidated",
						"Invalid Status", "Order Status is :" + status + ", Expected Status is: Being Negotiated");
			}

			break;

		case "CHANGE_DSV_PO_TO_SHIPPED":

			if ("Released".equals(status)) {
				shipmentYDocEle.setAttribute("Action", "Create");
				shipmentYDocEle.setAttribute("DocumentType", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@DocumentType"));
				shipmentYDocEle.setAttribute("EnterpriseCode", SCXmlUtil.getXpathAttribute(
						getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@EnterpriseCode"));
				shipmentYDocEle.setAttribute("OrderNo", SCXmlUtil
						.getXpathAttribute(getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@OrderNo"));
				YFCElement shipmentLinesEle = shipmentYDocEle.createChild("ShipmentLines");

				YFCDocument getCompleteOrderDetailsOutYDoc = YFCDocument.getDocumentFor(getCompleteOrderDetailsOutDoc);
				YFCNodeList<YFCElement> orderLines = getCompleteOrderDetailsOutYDoc.getElementsByTagName("OrderLine");
				for (YFCElement orderLine : orderLines) {
					// shipmentYDocEle.setAttribute("ShipNode", "Create");

					YFCElement shipmentLineEle = shipmentLinesEle.createChild("ShipmentLine");
					shipmentLineEle.setAttribute("DocumentType", SCXmlUtil.getXpathAttribute(
							getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@DocumentType"));
					// shipmentLineEle.setAttribute("ItemID",
					// SCXmlUtil.getXpathAttribute((Element) orderLine,
					// "//OrderLine/ItemDetails/@ItemID"));
					shipmentLineEle.setAttribute("ItemID",
							orderLine.getElementsByTagName("ItemDetails").item(0).getAttribute("ItemID"));

					shipmentLineEle.setAttribute("OrderHeaderKey", SCXmlUtil.getXpathAttribute(
							getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@OrderHeaderKey"));
					shipmentLineEle.setAttribute("OrderLineKey", orderLine.getAttribute("OrderLineKey"));
					shipmentLineEle.setAttribute("OrderNo", SCXmlUtil
							.getXpathAttribute(getCompleteOrderDetailsOutDoc.getDocumentElement(), "//Order/@OrderNo"));
					// shipmentLineEle.setAttribute("OrderReleaseKey",
					// SCXmlUtil.getXpathAttribute((Element) orderLine,
					// "//orderLine/OrderStatuses/OrderStatus/@OrderReleaseKey"));
					shipmentLineEle.setAttribute("OrderReleaseKey",
							orderLine.getElementsByTagName("OrderStatus").item(0).getAttribute("OrderReleaseKey"));
					shipmentLineEle.setAttribute("ShipNode",
							orderLine.getElementsByTagName("OrderStatus").item(0).getAttribute("ShipNode"));

					shipmentLineEle.setAttribute("Quantity", orderLine.getAttribute("OrderedQty"));
				}
				returnDoc = CommonUtils.invoke(env, OmsConstants.CONFIRM_SHIPMENT_API,
						/* YFCDocument.getDocumentFor(getOrderListTemplate).getDocument() */"",
						shipmentYDoc.getDocument());
			} else {
				throw new YFSException("Status for performing operation on " + action + " is invalidated",
						"Invalid Status", "Order Status is :" + status + ", Expected Status is Released");
			}

			break;
		default:
			throw new YFSException("Action for the operation didnot match", "Invalid Action",
					"provided:" + action + ", Expected Action is " + ActionList[0] + "," + ActionList[1] + ","
							+ ActionList[2] + "," + ActionList[3] + "," + ActionList[4]);
		}
		return returnDoc;
	}
}
