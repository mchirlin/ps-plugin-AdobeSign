package com.appiancorp.ps.plugin.adobesign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.adobe.sign.model.agreements.AgreementCreationInfo;
import com.adobe.sign.model.agreements.AgreementCreationResponse;
import com.adobe.sign.model.agreements.AgreementDocuments;
import com.adobe.sign.model.agreements.Document;
import com.adobe.sign.model.agreements.DocumentCreationInfo;
import com.adobe.sign.model.agreements.FileInfo;
import com.adobe.sign.model.agreements.RecipientSetInfo;
import com.adobe.sign.model.transientDocuments.TransientDocumentResponse;
import com.adobe.sign.utils.ApiException;
import com.appiancorp.ps.plugins.adobesign.AdobeSignUtils;
import com.appiancorp.ps.plugins.adobesign.api.AgreementUtils;
import com.appiancorp.ps.plugins.adobesign.api.FileUtils;
import com.appiancorp.ps.plugins.adobesign.api.TransientDocumentUtils;
import com.appiancorp.ps.plugins.adobesign.api.AgreementUtils.DocumentIdentifierType;

//import org.apache.log4j.Logger;

public class AdobeSignTest {

	@Test
	public void testCreateAgreement() throws ApiException, IOException {
		AdobeSignUtils as = new AdobeSignUtils("3AAABLblqZhDtc4fPo0KjAlL16c5dt6Ngcf6Jmo_xJ1KV6dwWBaAFiJOovT4y0GZa9E1aT-deFRLGLcav9Q0u2y_UrjqTOEhk",
				"email:matt.edmond@appian.com");
		TransientDocumentResponse tdr = TransientDocumentUtils.createTransientDocument("C:/TestFiles/Sample.pdf", "Sample");

		// Get the id of the transient document.
		String transientDocumentId = tdr.getTransientDocumentId();

		List<String> recipientSetEmailList = new ArrayList<String>();
		recipientSetEmailList.add("michael.chirlin@appian.com");
		recipientSetEmailList.add("matt.edmond@appian.com");

		String recipientSetName = "guys";

		// Get recipient set info
		List<RecipientSetInfo> recipientSetInfos = AgreementUtils.getRecipientSetInfoWithRecipientSetName(recipientSetEmailList, recipientSetName);

		// Get file info and create a list of file info
		FileInfo fileInfo = AgreementUtils.getFileInfo(transientDocumentId, DocumentIdentifierType.TRANSIENT_DOCUMENT_ID);
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
		fileInfos.add(fileInfo);

		// Get document creation info using library document id
		DocumentCreationInfo documentCreationInfo = AgreementUtils.getDocumentCreationInfo("Yay", fileInfos, recipientSetInfos);

		// Get agreement creation info
		AgreementCreationInfo agreementCreationInfo = AgreementUtils.getAgreementCreationInfo(documentCreationInfo, null);

		// Make API call to create agreement
		AgreementCreationResponse agreementCreationResponse = AgreementUtils.agreementsApi.createAgreement(AdobeSignUtils.getHeaderParams(),
				agreementCreationInfo);

		System.out.println("AgreementId: " + agreementCreationResponse.getAgreementId());
	}

	@Test
	public void testGetAgreementDocuments() throws ApiException, IOException {
		AdobeSignUtils as = new AdobeSignUtils("3AAABLblqZhBikKIzhJovOzHAEM-BtOIQwi0r8ty4atYIx8xVM7ZSi9BJzHL5xlGy5KJYJZI5UWafUZDIB-8lzefam9i8zZNY",
				"email:matt.edmond@appian.com");

		// Get agreement ID
		String agreementId = AgreementUtils.getAgreementId("[DEMO USE ONLY] Yay");

		// Fetch list of documents associated with the specified agreement.
		AgreementDocuments agreementDocuments = AgreementUtils.getAllDocuments(agreementId);
		List<Document> agreementDocumentsList = agreementDocuments.getDocuments();

		// Save all the documents in files.
		for (Document document : agreementDocumentsList) {
			// Download all the documents of the given agreement
			byte[] docStream = AgreementUtils.downloadDocuments(agreementId, document.getDocumentId());

			// Generate a running file name for storing locally.
			String fileName = document.getName() + "_" + System.currentTimeMillis() + ".pdf";

			// Save the documents to file.
			if (docStream != null) {
				FileUtils.saveToFile(docStream, "C:/TestFiles/AgreementDocuments/", fileName);
			}
		}
	}
}
