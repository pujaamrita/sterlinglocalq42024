
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fcl.ibm.common.constants.FCLConstant;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;

public class Test implements FCLConstant {

	public static void main(String[] args) throws Exception {
// TODO Auto-generated method stub

		String str = "<Shipment EnterpriseCode=\"FCL-RETAIL\" DocumentType=\"0001\" ShipNode=\"DC_904\" OrderNo=\"Y200027483\" ReleaseNo=\"1\" ActualShipmentDate=\"20211119\">\r\n"
				+ " <Extn ExtnRouteId=\"899127\" ExtnRouteNo=\"3799\" ExtnEndOfTruckInd=\"Y\" ExtnCurrentCount=\"1\" ExtnTotalCount=\"1\" ExtnCarrierCode=\"\"/>\r\n"
				+ " <Containers>\r\n"
				+ " <Container ContainerNo=\"0001191602\" ContainerType=\"SP\" ParentContainerNo=\"\" RoutingCode=\"3799\">\r\n"
				+ " <ContainerDetails>\r\n" + " <ContainerDetail Quantity=\"2\">\r\n"
				+ " <ShipmentLine SubLineNo=\"1\" ReleaseNo=\"1\" PrimeLineNo=\"1\" OrderNo=\"Y200027483\" DocumentType=\"0001\"/>\r\n"
				+ " </ContainerDetail>\r\n" + " </ContainerDetails>\r\n" + " </Container>\r\n" + " </Containers>\r\n"
				+ " <ShipmentLines>\r\n"
				+ " <ShipmentLine ItemID=\"212217\" ProductClass=\"GOOD\" UnitOfMeasure=\"EACH\" DocumentType=\"0001\" OrderNo=\"Y200027483\" PrimeLineNo=\"1\" Quantity=\"2\" ReleaseNo=\"1\" SubLineNo=\"1\" NetWeight=\"0.90\" NetWeightUOM=\"KG\">\r\n"
				+ " <Extn ExtnShipper=\"S08675521\" ExtnPriyaActualUOM=\"EA\"/>\r\n" + " </ShipmentLine>\r\n"
				+ " </ShipmentLines>\r\n" + "</Shipment>";

		Document doc = SCXmlUtil.createFromString(str);
// validateMessage(doc);

		String str1 = "<Shipment EnterpriseCode=\"FCL-RETAIL\" DocumentType=\"0001\" ShipNode=\"DC_904\" OrderNo=\"Y200027483\" ReleaseNo=\"1\" ActualShipmentDate=\"20211119\">\r\n"
				+ " <Extn ExtnRouteId=\"899127\" ExtnRouteNo=\"3799\" ExtnEndOfTruckInd=\"Y\" ExtnCurrentCount=\"1\" ExtnTotalCount=\"1\" ExtnCarrierCode=\"\"/>\r\n"
				+ " <Containers>\r\n"
				+ " <Container ContainerNo=\"0001191602\" ContainerType=\"SP\" ParentContainerNo=\"\" RoutingCode=\"3799\">\r\n"
				+ " <ContainerDetails>\r\n" + " <ContainerDetail Quantity=\"2\">\r\n"
				+ " <ShipmentLine SubLineNo=\"1\" ReleaseNo=\"1\" PrimeLineNo=\"1\" OrderNo=\"Y200027483\" DocumentType=\"0001\"/>\r\n"
				+ " </ContainerDetail>\r\n" + " </ContainerDetails>\r\n" + " </Container>\r\n" + " </Containers>\r\n"
				+ " <ShipmentLines>\r\n"
				+ " <ShipmentLine ItemID=\"212217\" ProductClass=\"GOOD\" UnitOfMeasure=\"EACH\" DocumentType=\"0001\" OrderNo=\"Y200027483\" PrimeLineNo=\"1\" Quantity=\"2\" ReleaseNo=\"1\" SubLineNo=\"1\" NetWeight=\"0.90\" NetWeightUOM=\"KG\">\r\n"
				+ " <Extn ExtnShipper=\"S08675521\" ExtnPriyaActualUOM=\"EA\"/>\r\n" + " </ShipmentLine>\r\n"
				+ " </ShipmentLines>\r\n" + "</Shipment>";

		Document getOrderDetailsOutput = SCXmlUtil.createFromString(str1);
		validateMessage(doc, getOrderDetailsOutput);

	}

	public static void validateMessage(Document input, Document getOrderDetailsOutput) throws Exception {

		try {
			Element shipment = input.getDocumentElement();
			String rootElement = shipment.getNodeName();
			if (E_SHIPMENT.equalsIgnoreCase(rootElement) && !YFCCommon.isVoid(shipment.getAttribute(A_ORDER_NO))) {
				Document getOrderDetailsInput = SCXmlUtil.createDocument(E_ORDER);
				Element orderElement = getOrderDetailsInput.getDocumentElement();
				orderElement.setAttribute(A_ENTERPRISE_CODE, shipment.getAttribute(A_ENTERPRISE_CODE));
				orderElement.setAttribute(A_DOCUMENT_TYPE, shipment.getAttribute(A_DOCUMENT_TYPE));
				orderElement.setAttribute(A_ORDER_NO, shipment.getAttribute(A_ORDER_NO));

				NodeList shipmentLines = SCXmlUtil.getXpathNodes(shipment, "//ShipmentLines/ShipmentLine");
				for (int i = 0; i < shipmentLines.getLength(); i++) {
					Element shipmentLine = (Element) shipmentLines.item(i);
					String primeLineNo = shipmentLine.getAttribute(A_PRIME_LINE_NO);
					String shipNode = shipment.getAttribute(E_SHIP_NODE);
					String shipmentUOM = shipmentLine.getAttribute(A_UNIT_OF_MEASURE);
					String orderLineUOM = SCXmlUtil.getXpathAttribute(getOrderDetailsOutput.getDocumentElement(),
							"//OrderLines/OrderLine[@PrimeLineNo='" + primeLineNo + "']/Item/@UnitOfMeasure");
					String orderLineItem = SCXmlUtil.getXpathAttribute(getOrderDetailsOutput.getDocumentElement(),
							"//OrderLines/OrderLine[@PrimeLineNo='" + primeLineNo + "']/Item/@ItemID");
					String shipmentItem = shipmentLine.getAttribute(A_ITEM_ID);
//                    LOGGER.debug("substring : " + shipNode.substring(0,3));
					if (shipNode != null && "DC_".equalsIgnoreCase(shipNode.substring(0, 3))) {
						if (YFCCommon.isVoid(primeLineNo)) {
//                            LOGGER.debug("PrimeLine No missing");
							shipmentLine.setAttribute(REASON_CODE, "PrimeLine No missing ");
//                            isEmailRequired = true;
						} else if (shipmentUOM != null && !shipmentUOM.equalsIgnoreCase(orderLineUOM)) {
//                            LOGGER.debug("UOM Mismatch");
							shipmentLine.setAttribute(REASON_CODE, "UOM Mismatch");
//                            isEmailRequired = true;
						} else if (orderLineItem != null && !orderLineItem.equalsIgnoreCase(shipmentItem)) {
//                            LOGGER.debug("ItemID mismatch " + shipmentItem + " : " + orderLineItem);
							shipmentLine.setAttribute(REASON_CODE,
									"ItemID mismatch " + shipmentItem + " : " + orderLineItem);
//                            isEmailRequired = true;
						} else {
							String lineStatus = SCXmlUtil.getXpathAttribute(getOrderDetailsOutput.getDocumentElement(),
									"//OrderLines/OrderLine[@PrimeLineNo='" + primeLineNo + "']/@Status");
							String minLineStatus = SCXmlUtil.getXpathAttribute(
									getOrderDetailsOutput.getDocumentElement(),
									"//OrderLines/OrderLine[@PrimeLineNo='" + primeLineNo + "']/@MinLineStatus");
							if ("3700".equalsIgnoreCase(minLineStatus) || ("9000".equalsIgnoreCase(minLineStatus))) {
								shipmentLine.setAttribute(REASON_CODE,
										"Orderline already " + lineStatus + ". Line can't be modified.");
								shipment.setAttribute("IsProcessingRequired", "N");
//                                isEmailRequired = true;
							}
						}
					}
				}
			}

		} catch (Exception exception) {
//            LOGGER.debug("Exception occurred in FclValidateShipmentMessage.validateMessage " + exception);
		}
		System.out.println(SCXmlUtil.getString(input));

	}
}