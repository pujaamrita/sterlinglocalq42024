package com.oms.order.ue;

import org.w3c.dom.Document;

import com.oms.utils.CommonUtils;
import com.oms.utils.OmsConstants;
import com.oms.utils.OmsLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

public class BeforeCreateOrderUEImpl implements YFSBeforeCreateOrderUE{

	@Override
	public String beforeCreateOrder(YFSEnvironment arg0, String arg1) throws YFSUserExitException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document beforeCreateOrder(YFSEnvironment env, Document arg1) throws YFSUserExitException {

		YFCDocument ydoc = YFCDocument.getDocumentFor(arg1);
		YFCElement ydocEle=ydoc.getDocumentElement();

		YFCDocument yfcOrder = YFCDocument.createDocument("Order");
		YFCElement orderEle = yfcOrder.getDocumentElement();
		
		/***OrderName holds parent sales orderNo,which would be not available when xml was use to create quote salesOrder, 
		 * and hence all the below mapping would be skipped, as for quote salesOrder below attribute mapping is not required.
		 */
		if (!ydocEle.hasAttribute("OrderName")){
			return arg1;
		}
		/**OrderName holds parent salesOrder no*/
		orderEle.setAttribute("OrderNo", ydocEle.getAttribute("OrderName"));
		orderEle.setAttribute("EnterpriseCode", "IBM-Japan");

		String getOrderListTemplate = "<OrderList>\n"
				+ "   <Order>\n"
				+ "      <OrderDates>\n"
				+ "         <OrderDate />\n"
				+ "      </OrderDates>\n"
				+ "      <OrderLines>\n"
				+ "         <OrderLine>\n"
				+ "            <Item />\n"
				+ "            <ItemDetails>\n"
				+ "               <PrimaryInformation />\n"
				+ "            </ItemDetails>\n"
				+ "            <OrderDates>\n"
				+ "               <OrderDate />\n"
				+ "            </OrderDates>\n"
				+ "         </OrderLine>\n"
				+ "      </OrderLines>\n"
				+ "      <PersonInfoShipTo />\n"
				+ "      <PersonInfoBillTo />\n"
				+ "   </Order>\n"
				+ "</OrderList>";

		Document returnDoc = CommonUtils.invoke(env, OmsConstants.GET_ORDER_LIST_API, YFCDocument.getDocumentFor(getOrderListTemplate).getDocument(),
				yfcOrder.getDocument());
		YFCElement getOrderListEle = YFCDocument.getDocumentFor(returnDoc).getDocumentElement();
		YFCElement golOrderEle = getOrderListEle.getChildElement("Order");

		YFCNodeList<YFCElement> OrderLines = ydocEle.getElementsByTagName(OmsLiterals.ELE_ORDER_LINE);
		YFCNodeList<YFCElement> golOrderLines = golOrderEle.getElementsByTagName(OmsLiterals.ELE_ORDER_LINE);

		for (YFCElement OrderLine : OrderLines) 
		{
			for (YFCElement golOrderLine : golOrderLines) {
				if (golOrderLine.getElementsByTagName("Item").item(0).getAttribute("ItemID")
						.equals(OrderLine.getElementsByTagName("Item").item(0).getAttribute("ItemID"))){
					String ItemType = golOrderLine.getElementsByTagName("PrimaryInformation").item(0)
							.getAttribute("ItemType");

					switch (ItemType) {
					case "Hardware":
						ydocEle.setAttribute("AllocationRuleID", "SCH_JAPAN");
						OrderLine.setAttribute("DeliveryMethod", OmsConstants.DELIVERY_METHOD_SHP);
						OrderLine.setAttribute("FulfillmentType", OmsConstants.DELIVERY_METHOD_SHP);
						break;

					case "Subscription":
					case "Service(Subscription Sales)":
					case "Service":

						break;
					default:
						throw new YFSException("ItemType of the line didnot match", "Invalid ItemType", "provided:"
								+ ItemType + ", Expected ItemType is [ Hardware,Service,Service(Subscription Sales) ]");
						// break;
					}
				}
			}
		}
		return arg1;
	}
}
