package com.appiancorp.ps.plugins.adobesign.types;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlRootElement(namespace = "http://plugins.ps.appiancorp.com/suite/types/", name = "component")
@XmlType(namespace = "http://plugins.ps.appiancorp.com/suite/types/", name = AdobeSignFileInfo.LOCAL_PART, propOrder = { "documentUrl", "libraryDocumentId",
		"libraryDocumentName", "transientDocumentId" })
public class AdobeSignFileInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = "urn:appiancorp:ps:plugins";
	public static final String LOCAL_PART = "ASIGN_FileInfo";
	public static final QName QNAME = new QName(NAMESPACE, LOCAL_PART);

	private String documentUrl;
	private String libraryDocumentId;
	private String libraryDocumentName;
	private String transientDocumentId;

	@XmlElement
	public String getDocumentUrl() {
		return documentUrl;
	}

	@XmlElement
	public String getLibraryDocumentId() {
		return libraryDocumentId;
	}

	@XmlElement
	public String getLibraryDocumentName() {
		return libraryDocumentName;
	}

	@XmlElement
	public String getTransientDocumentId() {
		return transientDocumentId;
	}
}
