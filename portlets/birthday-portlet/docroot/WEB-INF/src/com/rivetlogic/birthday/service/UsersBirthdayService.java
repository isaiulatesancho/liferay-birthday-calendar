package com.rivetlogic.birthday.service;
import com.liferay.portal.model.Contact;

import java.util.Date;
import java.util.List;
public interface UsersBirthdayService {
	public List<Contact> getBirthdaysByDay(Date currentdate, int start, int end);
}
