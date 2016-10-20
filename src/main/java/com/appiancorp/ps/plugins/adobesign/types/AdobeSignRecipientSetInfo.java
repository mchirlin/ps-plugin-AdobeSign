package com.appiancorp.ps.plugins.adobesign.types;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlRootElement(namespace = "http://plugins.ps.appiancorp.com/suite/types/", name = "component")
@XmlType(namespace = "http://plugins.ps.appiancorp.com/suite/types/", name = AdobeSignRecipientSetInfo.LOCAL_PART, propOrder = { "recipientSetName",
		"recipientSetRole", "emails" })
public class AdobeSignRecipientSetInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = "urn:appiancorp:ps:plugins";
	public static final String LOCAL_PART = "ASIGN_RecipientSetInfo";
	public static final QName QNAME = new QName(NAMESPACE, LOCAL_PART);

	private String recipientSetName;
	private String recipientSetRole;
	private List<String> emails;

	@XmlElement
	public String getRecipientSetName() {
		return recipientSetName;
	}

	@XmlElement
	public String getRecipientSetRole() {
		return recipientSetRole;
	}

	@XmlElement
	public List<String> getEmails() {
		return emails;
	}
}
