package com.appiancorp.ps.plugins.adobesign;

//import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.adobe.sign.api.AgreementsApi;
import com.adobe.sign.api.TransientDocumentsApi;
import com.adobe.sign.model.agreements.AgreementCreationInfo;
import com.adobe.sign.model.agreements.AgreementCreationResponse;
import com.adobe.sign.model.agreements.AgreementDocuments;
import com.adobe.sign.model.agreements.DocumentCreationInfo;
import com.adobe.sign.model.agreements.DocumentCreationInfo.ReminderFrequencyEnum;
import com.adobe.sign.model.agreements.DocumentCreationInfo.SignatureFlowEnum;
import com.adobe.sign.model.agreements.DocumentCreationInfo.SignatureTypeEnum;
import com.adobe.sign.model.agreements.FileInfo;
import com.adobe.sign.model.agreements.RecipientInfo;
import com.adobe.sign.model.agreements.RecipientSetInfo;
import com.adobe.sign.model.transientDocuments.TransientDocumentResponse;
import com.adobe.sign.utils.ApiException;
import com.appiancorp.ps.plugins.adobesign.api.AgreementUtils;
import com.appiancorp.ps.plugins.adobesign.api.FileUtils;
import com.appiancorp.ps.plugins.adobesign.api.TransientDocumentUtils;
import com.appiancorp.ps.plugins.adobesign.types.AdobeSignAgreementCreationInfo;
import com.appiancorp.ps.plugins.adobesign.types.AdobeSignFileInfo;
import com.appiancorp.ps.plugins.adobesign.types.AdobeSignRecipientSetInfo;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.Document;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class AdobeSignUtils {

	public final static TransientDocumentsApi transientDocumentsApi = new TransientDocumentsApi();
	public final static AgreementsApi agreementsApi = new AgreementsApi();

	private final static String ACCESS_TOKEN_KEY = "Access-Token";
	private final static String X_API_USER_KEY = "x-api-user";

	public static String accessToken;
	public static String apiUser;

	private static final Logger LOG = Logger.getLogger(AdobeSignUtils.class);

	/**
	 * Constructor, sets the access token and apiUser
	 */
	public AdobeSignUtils(String accessToken, String apiUser) {
		AdobeSignUtils.accessToken = accessToken;
		AdobeSignUtils.apiUser = apiUser;
	}

	public String createAgreement(AdobeSignAgreementCreationInfo agreementInfo) throws ApiException {
		AgreementCreationInfo agreementCreationInfo = buildAgreementCreationInfo(agreementInfo);
		AgreementCreationResponse agreementCreationResponse = AgreementUtils.createAgreement(agreementCreationInfo);

		return agreementCreationResponse.getAgreementId();
	}

	private AgreementCreationInfo buildAgreementCreationInfo(AdobeSignAgreementCreationInfo asai) {
		DocumentCreationInfo documentCreationInfo = buildDocumentCreationInfo(asai);

		AgreementCreationInfo agreementCreationInfo = new AgreementCreationInfo();
		agreementCreationInfo.setDocumentCreationInfo(documentCreationInfo);
		// TODO Options

		return agreementCreationInfo;
	}

	private DocumentCreationInfo buildDocumentCreationInfo(AdobeSignAgreementCreationInfo asai) {
		DocumentCreationInfo dci = new DocumentCreationInfo();

		dci.setCallbackInfo(asai.getCallbackUrl());
		dci.setCcs(asai.getCcs());
		dci.setDaysUntilSigningDeadline(asai.getDaysUntilSigningDeadline());
		// TODO ExternalId
		dci.setFileInfos(buildFileInfos(asai.getFileInfos()));
		dci.setLocale(asai.getLocale());
		dci.setMessage(asai.getMessage());
		dci.setName(asai.getName());
		dci.setRecipientSetInfos(buildRecipientSetInfos(asai.getRecipientSetInfos()));
		dci.setReminderFrequency(buildReminderFrequencyEnum(asai.getReminderFrequency()));
		// TODO SecurityOptions
		dci.setSignatureFlow(buildSignatureFlow(asai.getSignatureFlow()));
		dci.setSignatureType(buildSignatureType(asai.getSignatureType()));
		// TODO VaultingOptions

		return dci;
	}

	private List<RecipientSetInfo> buildRecipientSetInfos(List<AdobeSignRecipientSetInfo> asrsi) {
		List<RecipientSetInfo> rsiList = new ArrayList<RecipientSetInfo>();

		for (AdobeSignRecipientSetInfo as : asrsi) {
			RecipientSetInfo rsi = new RecipientSetInfo();
			rsi.setRecipientSetName(as.getRecipientSetRole());

			List<RecipientInfo> riList = new ArrayList<RecipientInfo>();
			for (String email : as.getEmails()) {
				RecipientInfo ri = new RecipientInfo();
				ri.setEmail(email);
				riList.add(ri);
			}
			rsi.setRecipientSetMemberInfos(riList);
			rsiList.add(rsi);
		}

		return rsiList;
	}

	private List<FileInfo> buildFileInfos(List<AdobeSignFileInfo> asfi) {
		List<FileInfo> fiList = new ArrayList<FileInfo>();

		for (AdobeSignFileInfo as : asfi) {
			FileInfo fi = new FileInfo();
			// TODO Setup documentUrl
			// fi.setDocumentURL(as.getDocumentUrl());
			fi.setLibraryDocumentId(as.getLibraryDocumentId());
			fi.setLibraryDocumentName(as.getLibraryDocumentName());
			fi.setTransientDocumentId(as.getTransientDocumentId());
		}

		return fiList;
	}

	private ReminderFrequencyEnum buildReminderFrequencyEnum(String reminderFrequency) {
		ReminderFrequencyEnum rfe;
		switch (reminderFrequency) {
			case "DAILY":
				rfe = ReminderFrequencyEnum.DAILY_UNTIL_SIGNED;
				break;
			case "WEEKLY":
				rfe = ReminderFrequencyEnum.WEEKLY_UNTIL_SIGNED;
				break;
			default:
				rfe = ReminderFrequencyEnum.WEEKLY_UNTIL_SIGNED;
				break;
		}
		return rfe;
	}

	private SignatureFlowEnum buildSignatureFlow(String signatureFlow) {
		SignatureFlowEnum sfe;
		switch (signatureFlow) {
			case "PARALLEL":
				sfe = SignatureFlowEnum.PARALLEL;
				break;
			case "SENDER_SIGNATURE_NOT_REQUIRED":
				sfe = SignatureFlowEnum.SENDER_SIGNATURE_NOT_REQUIRED;
				break;
			case "SENDER_SIGNS_FIRST":
				sfe = SignatureFlowEnum.SENDER_SIGNS_FIRST;
				break;
			case "SENDER_SIGNS_LAST":
				sfe = SignatureFlowEnum.SENDER_SIGNS_LAST;
				break;
			case "SENDER_SIGNS_ONLY":
				sfe = SignatureFlowEnum.SENDER_SIGNS_ONLY;
				break;
			case "SEQUENTIAL":
				sfe = SignatureFlowEnum.SEQUENTIAL;
				break;
			default:
				sfe = SignatureFlowEnum.SEQUENTIAL;
				break;
		}
		return sfe;
	}

	private SignatureTypeEnum buildSignatureType(String signatureType) {
		SignatureTypeEnum ste;
		switch (signatureType) {
			case "ESIGN":
				ste = SignatureTypeEnum.ESIGN;
				break;
			case "WRITTEN":
				ste = SignatureTypeEnum.WRITTEN;
				break;
			default:
				ste = SignatureTypeEnum.ESIGN;
				break;
		}
		return ste;
	}

	public String[] createTransientDocuments(Long[] documentIds, ContentService cs) throws Exception {
		List<String> transientDocumentIdList = new ArrayList<String>();

		// For each of the documents
		for (Long documentId : documentIds) {
			Document doc = AppianUtils.getDocumentFromAppian(cs, documentId);
			String path = AppianUtils.getInternalFilePath(cs, doc);
			TransientDocumentResponse tdr = TransientDocumentUtils.createTransientDocument(path, doc.getName());

			transientDocumentIdList.add(tdr.getTransientDocumentId());
		}

		return transientDocumentIdList.toArray(new String[transientDocumentIdList.size()]);
	}

	public Long[] getAgreementDocuments(String agreementId, Long folderId, ContentService cs) throws Exception {
		List<Long> docIdList = new ArrayList<Long>();
		AgreementDocuments agreementDocuments = AgreementUtils.getAllDocuments(agreementId);

		for (com.adobe.sign.model.agreements.Document ad : agreementDocuments.getDocuments()) {
			Document doc = AppianUtils.createDocumentInAppian(cs, ad.getName(), folderId, null, ad.getName(), null);
			// Download all the documents of the given agreement
			byte[] docStream = AgreementUtils.downloadDocuments(agreementId, ad.getDocumentId());

			// Save the documents to file.
			if (docStream != null) {
				String fullPath = AppianUtils.getInternalFilePath(cs, doc);
				String pathDir = FilenameUtils.getFullPath(fullPath);
				String fileName = FilenameUtils.getName(fullPath);
				FileUtils.saveToFile(docStream, pathDir, fileName);
			}
			docIdList.add(doc.getId());
		}

		return docIdList.toArray(new Long[docIdList.size()]);
	}

	public static MultivaluedMap getHeaderParams() {
		MultivaluedMap headers = new MultivaluedMapImpl();
		// Add headers
		headers.put(ACCESS_TOKEN_KEY, AdobeSignUtils.accessToken);
		headers.put(X_API_USER_KEY, AdobeSignUtils.apiUser);

		return headers;
	}

	public static void logException(String error, Exception e) throws ApiException {
		LOG.error(error, e);
		System.err.println(error);
		throw new ApiException(error);
	}
}
