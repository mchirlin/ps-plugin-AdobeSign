package com.appiancorp.ps.plugins.adobesign;

import java.util.Map;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.DocumentDataType;
import com.appiancorp.suiteapi.knowledge.FolderDataType;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.MessageContainer;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.palette.PaletteInfo;
import com.appiancorp.suiteapi.security.external.SecureCredentialsStore;

@PaletteInfo(paletteCategory = "Integration Services", palette = "Connectivity Services")
public class AdobeSignGetAgreement extends AppianSmartService {

	private static final Logger LOG = Logger.getLogger(AdobeSignGetAgreement.class);

	private static final String KEY_ADOBESIGN_API_USER = "apiuser";

	private SecureCredentialsStore scs;
	private ContentService cs;

	// Inputs
	private String scsKey;
	private String accessToken;
	private String agreementId;
	private Long folderId;

	// Ouputs
	private Long[] documents;

	public AdobeSignGetAgreement(ContentService cs, SecureCredentialsStore scs) {
		super();

		this.scs = scs;
		this.cs = cs;
	}

	@Override
	public void run() throws SmartServiceException {
		Map<String, String> credentials = null;
		try {
			credentials = this.scs.getSystemSecuredValues(scsKey);
		} catch (Exception e) {
			LOG.error(e, e);

			throw createException(e, "error.scs");
		}

		String apiUser = credentials.get(KEY_ADOBESIGN_API_USER);

		AdobeSignUtils asu = new AdobeSignUtils(accessToken, apiUser);

		try {
			this.documents = asu.getAgreementDocuments(agreementId, folderId, cs);
		} catch (Exception e) {
			LOG.error(e, e);

			throw createException(e, "error.api");
		}
	}

	public void onSave(MessageContainer messages) {
	}

	public void validate(MessageContainer messages) {
	}

	@Input(required = Required.ALWAYS)
	@Name("scsKey")
	public void setScsKey(String val) {
		this.scsKey = val;
	}

	@Input(required = Required.ALWAYS)
	@Name("accessToken")
	public void setAccessToken(String val) {
		this.accessToken = val;
	}

	@Input(required = Required.ALWAYS)
	@Name("agreementId")
	public void setAgreementId(String val) {
		this.agreementId = val;
	}

	@Input(required = Required.ALWAYS)
	@FolderDataType
	@Name("folderId")
	public void setFolderId(Long val) {
		this.folderId = val;
	}

	@Name("documents")
	@DocumentDataType
	public Long[] getDocuments() {
		return this.documents;
	}

	private SmartServiceException createException(Throwable t, String key, Object... args) {
		return new SmartServiceException.Builder(getClass(), t).userMessage(key, args).build();
	}
}
