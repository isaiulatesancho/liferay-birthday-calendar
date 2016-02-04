package com.rivetlogic.birthday.portlet;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Portlet implementation class BirthdayPortlet
 */
public class BirthdayPortlet extends MVCPortlet {
 
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException ,PortletException {
		super.doView(renderRequest, renderResponse);
	}
	
	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException ,IOException {
		super.render(request, response);
	}
	
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws IOException, PortletException {
		try {
			doServeResource(request, response);
		} catch (Exception e) {
			//LOGGER.error("Error on serveResource.", e);
		}
	}
	
	private void doServeResource(ResourceRequest request, ResourceResponse response) throws Exception{
		String type = ParamUtil.getString(request,
				PortletConstants.TYPE);
		if (null != type && !type.isEmpty()) {
			switch (type) {
				case PortletConstants.GET_BY_DAY:
					getBirthdaysByDay(request, response);
					break;
				case PortletConstants.GET_BY_MONTH:
					getBirthdaysByMonth(request, response);
					break;
			}
		} else {
			printJsonResponse(String.format(PortletConstants.ERROR_BAD_PARAMETER_VALUE, PortletConstants.TYPE, type),
					String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
					response);
		}
	}
	
	
	private void getBirthdaysByDay(ResourceRequest request, ResourceResponse response) throws Exception{
		Date currentDate = getDateParam(request, response, "currentDate");
		int start = ParamUtil.getInteger(request, "start");
		int end = ParamUtil.getInteger(request, "end");
		System.out.println(String.format("By DAY. currentdate: %S. start: %S. end: %S.", currentDate, start, end));
	}
	
	private Date getDateParam(ResourceRequest request, ResourceResponse response, String param) throws Exception{
		String dateStr = ParamUtil.getString(request, param, null);
		System.out.println(String.format("Date param: %s. Value: %s.", param, dateStr));
		if(null == dateStr || dateStr.isEmpty()){
			printJsonResponse(String.format("Missing required parameter %s", "currentDate"), String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), response);
			throw new Exception(String.format("Missing required parameter %s", "currentDate"));
		}
		Date dateParam = null;
		SimpleDateFormat format = new SimpleDateFormat(PortletConstants.DATE_FORMAT);
		try{
        	dateParam = format.parse(dateStr);  
		}catch(ParseException e){
			printJsonResponse(String.format("Bad value for date parameter %s. Expected format: %s.", "currentDate", PortletConstants.DATE_FORMAT_SPECIFICATION), String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), response);
			throw new Exception(String.format("Bad value for date parameter %s. Expected format: %s.", "currentDate",  PortletConstants.DATE_FORMAT_SPECIFICATION));
		}
		
		return dateParam;
	}
	
	//TODO
	/*
	 * Check if Liferay has its permissions properly setup.
	 * If the dynamicQuery method has permission validations.
	 * */
	private void getBirthdaysByMonth(ResourceRequest request, ResourceResponse response){
		try{
		 DynamicQuery usersBirthdayQuery = DynamicQueryFactoryUtil.forClass(
				 Contact.class, PortletClassLoaderUtil.getClassLoader());
		 DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		 Date date1 = dateFormat.parse("1980/09/26");
		 Date date2 = dateFormat.parse("2016/10/26");
		 usersBirthdayQuery.add(PropertyFactoryUtil.forName("birthday").between(date1, date2));
		 List<Contact> contacts = ContactLocalServiceUtil.dynamicQuery(usersBirthdayQuery);
		 if(null != contacts){
			 for(Contact contact:contacts){
				 System.out.println(String.format("Name: %S. Email: %S. User birthday: %S.", contact.getFullName(), contact.getEmailAddress(), contact.getBirthday()));
			 }
		 }
		}catch(Exception e){
			LOGGER.error("Error retrieving users", e);
			 printJsonResponse("Error while retrieving users", String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), response);
		}
	}
	
	private void printJsonResponse(String jsonStr, String statusCode,
			ResourceResponse response) {
		if (null == statusCode)
			statusCode = String.valueOf(HttpServletResponse.SC_OK);
		response.setProperty(ResourceResponse.HTTP_STATUS_CODE, statusCode);
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			LOGGER.error(e);
		}
		if (null != out && !out.checkError()) {
			response.setContentType(ContentTypes.APPLICATION_JSON);
			out.print(jsonStr);
			out.flush();
			out.close();
		}
	}
	
	private static final Log LOGGER = LogFactoryUtil.getLog(BirthdayPortlet.class);
}
