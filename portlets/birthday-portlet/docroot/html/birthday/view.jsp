<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<portlet:resourceURL var="updateCartItemURL">
	<portlet:param name="type" value="month"/>
</portlet:resourceURL>

<a href="${updateCartItemURL}" target="_blank">Invoke Service</a>