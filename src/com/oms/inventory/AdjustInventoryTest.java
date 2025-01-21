package com.oms.inventory;

import org.w3c.dom.Document;

import com.oms.utils.CommonUtils;
import com.oms.utils.OmsConstants;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class AdjustInventoryTest {
	public static final YFCLogCategory LOGGER = YFCLogCategory.instance(AdjustInventoryTest.class);

	public Document businessLogic(YFSEnvironment env, Document doc) {
		/**
		 * ServiceName:-AdjustInventoryOGSyncService
		 * Purpose:- To adjust the inventory for the provided details, when
		 * "AdjustmentType", "ADJUSTMENT" it increase the inventory.
		 * Input:-
		 * <Items>
		 * <Item ItemID="item-01" OrganizationCode="" ShipNode="" Quantity="" ETA="">
		 * </Item>
		 * </Items>
		 * 
		 **/
		YFCDocument adjInvitems = YFCDocument.createDocument("Items");
		YFCElement adjInvitemEle = adjInvitems.getDocumentElement().createChild("Item");
		adjInvitemEle.setAttribute("AdjustmentType", "ADJUSTMENT");
		adjInvitemEle.setAttribute("Availability", "TRACK");

		YFCNodeList<YFCElement> inputItems = YFCDocument.getDocumentFor(doc).getElementsByTagName("Item");
		for (YFCElement inputItem : inputItems) {
			adjInvitemEle.setAttribute("ItemID", inputItem.getAttribute("ItemID"));
			adjInvitemEle.setAttribute("OrganizationCode", inputItem.getAttribute("OrganizationCode"));
			adjInvitemEle.setAttribute("ShipNode", inputItem.getAttribute("ShipNode"));
			adjInvitemEle.setAttribute("Quantity", inputItem.getAttribute("Quantity"));
			adjInvitemEle.setAttribute("ETA", inputItem.getAttribute("ETA"));
		}

		adjInvitemEle.setAttribute("ProductClass", "Good");
		adjInvitemEle.setAttribute("SupplyType", "ONHAND");
		adjInvitemEle.setAttribute("UnitOfMeasure", "PIECES");

		Document returnDoc = CommonUtils.invoke(env, OmsConstants.ADJUST_INVENTORY_API, /*
																						 * YFCDocument.getDocumentFor(
																						 * getOrderListTemplate).
																						 * getDocument()
																						 */"",
				adjInvitems.getDocument());
		return returnDoc;

	}

}
