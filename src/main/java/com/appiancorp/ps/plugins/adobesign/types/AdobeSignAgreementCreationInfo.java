package com.appiancorp.ps.plugins.adobesign.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlRootElement(namespace = "http://plugins.ps.appiancorp.com/suite/types/", name = "component")
@XmlType(namespace = "http://plugins.ps.appiancorp.com/suite/types/", name = AdobeSignAgreementCreationInfo.LOCAL_PART, propOrder = { "callbackUrl",
		"ccs", "daysUntilSigningDeadline", "externalId", "fileInfos", "locale", "message", "name", "recipientSetInfos", "reminderFrequency",
		"signatureFlow", "signatureType" })
public class AdobeSignAgreementCreationInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = "urn:appiancorp:ps:plugins";
	public static final String LOCAL_PART = "ASIGN_AgreementInfo";
	public static final QName QNAME = new QName(NAMESPACE, LOCAL_PART);

	private String callbackUrl;
	private List<String> ccs = new ArrayList<String>();
	private Integer daysUntilSigningDeadline;
	private String externalId;
	private List<AdobeSignFileInfo> fileInfos = new ArrayList<AdobeSignFileInfo>();
	private String locale;
	private String message;
	private String name;
	private List<AdobeSignRecipientSetInfo> recipientSetInfos = new ArrayList<AdobeSignRecipientSetInfo>();
	private String reminderFrequency;
	private String signatureFlow;
	private String signatureType;

	@XmlElement
	public String getCallbackUrl() {
		return callbackUrl;
	}

	@XmlElement
	public List<String> getCcs() {
		return ccs;
	}

	@XmlElement
	public Integer getDaysUntilSigningDeadline() {
		return daysUntilSigningDeadline;
	}

	@XmlElement
	public String getExternalId() {
		return externalId;
	}

	@XmlElement
	public List<AdobeSignFileInfo> getFileInfos() {
		return fileInfos;
	}

	@XmlElement
	public String getLocale() {
		return locale;
	}

	@XmlElement
	public String getMessage() {
		return message;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	@XmlElement
	public List<AdobeSignRecipientSetInfo> getRecipientSetInfos() {
		return recipientSetInfos;
	}

	@XmlElement
	public String getReminderFrequency() {
		return reminderFrequency;
	}

	@XmlElement
	public String getSignatureFlow() {
		return signatureFlow;
	}

	@XmlElement
	public String getSignatureType() {
		return signatureType;
	}
}
