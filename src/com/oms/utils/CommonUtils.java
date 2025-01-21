package com.oms.utils;

import java.rmi.RemoteException;
import java.time.LocalDate;

import org.w3c.dom.Document;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

import net.sf.ezmorph.primitive.AbstractIntegerMorpher;

public class CommonUtils {
	private static YFCLogCategory logger = (YFCLogCategory) YFCLogCategory.instance(CommonUtils.class);
	static YIFApi api;

	public static YFSEnvironment createEnvironment(String userID, String progID) {
		YFCDocument doc;
		YFSEnvironment resultEnv = null;

		try {
			doc = YFCDocument.createDocument("YFSEnvironment");
			api = YIFClientFactory.getInstance().getApi();
			YFCElement elem = doc.getDocumentElement();
			elem.setAttribute("userId", userID);
			elem.setAttribute("progId", progID);
			resultEnv = api.createEnvironment(doc.getDocument());
		} catch (RemoteException e) {
			logger.error(e);
			throw new YFCException(e);
		} catch (YIFClientCreationException e) {
			logger.error(e);
			throw new YFCException(e);
		}
		return resultEnv;
	}

	public static Document invoke(YFSEnvironment env, String apiName, Document doc) {

		Document returnDoc = null;
		try {
			api = YIFClientFactory.getInstance().getApi();
			returnDoc = api.invoke(env, apiName, doc);
		} catch (YIFClientCreationException e) {
			// TODO Auto-generated catch block
			throw new YFSException(e.getMessage(), "Error While calling OOB API " + apiName,
					"Input:-" + SCXmlUtil.getString(doc));

		} catch (YFSException e) {
			// TODO Auto-generated catch block
			throw new YFSException(e.getMessage(), e.getErrorCode(), e.getErrorDescription());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			throw new YFSException(e.getMessage(), "Error While calling OOB API " + apiName,
					"Input:-" + SCXmlUtil.getString(doc));
		}
		return returnDoc;
	}

	public static Document invoke(YFSEnvironment env, String apiName, Document templateName, Document inDoc) {
		Document returnDoc = null;

		try {
			api = YIFClientFactory.getInstance().getApi();
			if (YFCCommon.isVoid(templateName)) {
				invoke(env, apiName, inDoc);
			} else {
				env.setApiTemplate(apiName, templateName);
				returnDoc = api.invoke(env, apiName, inDoc);
			}

		} catch (YFCException | YFSException | RemoteException | YIFClientCreationException e) {
			logger.error(e);
			throw new YFCException(e);
		} finally {
			env.clearApiTemplate(apiName);
		}
		return returnDoc;
	}

	public static Document invoke(YFSEnvironment env, String apiName, String templateName, Document inDoc) {
		Document returnDoc;
		try {
			env.setApiTemplate(apiName, templateName);
			returnDoc = invoke(env, apiName, inDoc);
		} catch (YFCException e) {
			logger.error(e);
			throw new YFCException(e);
		} finally {
			env.clearApiTemplate(apiName);
		}
		return returnDoc;
	}

	public static Document executeFlow(YFSEnvironment env, String serviceName, Document inDoc) {

		Document returnDoc = null;
		try {
			api = YIFClientFactory.getInstance().getApi();
			returnDoc = api.executeFlow(env, serviceName, inDoc);
		} catch (YFSException e) {
			logger.error(e);
			throw new YFCException(e);
		} catch (RemoteException e) {
			logger.error(e);
			throw new YFCException(e);
		} catch (YIFClientCreationException e) {
			logger.error(e);
			throw new YFCException(e);
		}
		return returnDoc;
	}

	public static String getDate(String requestFor, String subscriptionTenure, String comparingStartDate,
			String comparingEndDate) {
		String returnDate = null;
		LocalDate startDate = null, endDate = null;
		LocalDate date = LocalDate.now();

		switch (requestFor) {
		case "currentDate":
			returnDate = date.toString();
			break;

		case "futureDate":
			if (YFCCommon.isVoid(subscriptionTenure)) {
				subscriptionTenure = "30";
			}
			returnDate = date.plusDays(Integer.valueOf(subscriptionTenure)).toString();
			break;

		case "compareAndGetValidDate":
			LocalDate dateTobeCompared = LocalDate.parse(comparingEndDate);
			if (date.compareTo(dateTobeCompared) > 0) {
				/**
				 * EndDate would be compared with the currentDate to take the decision on
				 * noOfDays Renewal date comes after dateTobeCompared Subscription still
				 * exists,then renew from the actual endDate for the provided tenure
				 */
				throw new YFCException("Invalid Dates",
						dateTobeCompared + " is lesser than the current Date, hence unable to renew");
			}

			else if (date.compareTo(dateTobeCompared) < 0) {
				/**
				 * EndDate would be compared with the currentDate to take the decision on
				 * noOfDays Renewal date comes before dateTobeCompared Subscription is already
				 * ended, then renew from the actual endDate for the provided tenure
				 */
				returnDate = dateTobeCompared.plusDays(date.getDayOfMonth()).toString();

			} else {
				/**
				 * EndDate would be compared with the currentDate to take the decision on
				 * noOfDays Renewal date will be equal to the dateTobeCompared Subscription is
				 * ending on the same day , then renew from the actual endDate for the provided
				 * tenure
				 */
				throw new YFCException("Invalid Dates",
						dateTobeCompared + " is Equal to the current Date, hence unable to renew");
			}
			break;

		case "compare":

			startDate = null;
			endDate = null;

			if (!YFCCommon.isVoid(comparingStartDate)) {
				startDate = LocalDate.parse(comparingStartDate);
			}
			if (!YFCCommon.isVoid(comparingEndDate)) {
				endDate = LocalDate.parse(comparingEndDate);
			}

			if (!YFCCommon.isVoid(startDate) && startDate.compareTo(endDate) > 0) {
				/**
				 * EndDate would be compared with the startDate to take the decision. startDate
				 * comes after endDate, it means subscription has been expired.
				 */
				returnDate = "false";
			}

			else if (!YFCCommon.isVoid(startDate) && startDate.compareTo(endDate) < 0) {
				/**
				 * EndDate would be compared with the startDate to take the decision startDate
				 * comes before dateTobeCompared Subscription is already ended, then renew from
				 * the actual endDate for the provided tenure
				 */
				returnDate = "true";

			} else if (!YFCCommon.isVoid(startDate) && startDate.compareTo(endDate) == 0) {
				/**
				 * EndDate would be compared with the currentDate to take the decision on
				 * noOfDays Renewal date will be equal to the dateTobeCompared Subscription is
				 * ending on the same day , then renew from the actual endDate for the provided
				 * tenure
				 */
				returnDate = "true";
			}

			else if (date.compareTo(endDate) > 0) {
				/**
				 * EndDate would be compared with the startDate to take the decision. startDate
				 * comes after endDate, it means subscription has been expired.
				 */
				returnDate = "false";
			}

			else if (date.compareTo(endDate) < 0) {
				/**
				 * EndDate would be compared with the startDate to take the decision. startDate
				 * comes after endDate, it means subscription has been expired.
				 */
				returnDate = "true";
			} else if (date.compareTo(endDate) == 0) {
				returnDate = "false";
			}
			break;
			
		case "calendarYearDays":
			if (date.isLeapYear())
				returnDate = date.plusDays(Integer.valueOf(OmsConstants.Leap_Year)).toString();
			else
				returnDate = date.plusDays(Integer.valueOf(OmsConstants.Non_Leap_Year)).toString();
			break;
			
		case "calendarMonthDays":
			returnDate = date.plusDays(date.getDayOfMonth()).toString();
			break;
			
		case "forNextMonth":
			startDate = null;
			endDate = null;
			int renewForTenure = 0;

			if (!YFCCommon.isVoid(subscriptionTenure)) {
				renewForTenure = Integer.valueOf(subscriptionTenure);
			}
			if (!YFCCommon.isVoid(comparingStartDate)) {
				startDate = LocalDate.parse(comparingStartDate);
				returnDate = startDate.plusMonths(renewForTenure).toString();
			} else if (!YFCCommon.isVoid(comparingEndDate)) {
				endDate = LocalDate.parse(comparingEndDate);
			}
		default:
			break;
		}
		return returnDate;
	}
}
