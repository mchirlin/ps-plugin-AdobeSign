package com.appiancorp.ps.plugins.adobesign;

import java.util.Map;

import org.apache.log4j.Logger;

import com.appiancorp.ps.plugins.adobesign.types.AdobeSignAgreementCreationInfo;
import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.MessageContainer;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.palette.PaletteInfo;
import com.appiancorp.suiteapi.security.external.SecureCredentialsStore;
import com.appiancorp.suiteapi.type.Type;

@PaletteInfo(paletteCategory = "Integration Services", palette = "Connectivity Services")
public class AdobeSignCreateAgreement extends AppianSmartService {

	private static final Logger LOG = Logger.getLogger(AdobeSignCreateAgreement.class);

	private static final String KEY_ADOBESIGN_API_USER = "apiuser";

	private SecureCredentialsStore scs;

	// Inputs
	private String scsKey;
	private String accessToken;
	private AdobeSignAgreementCreationInfo agreementCreationInfo;

	// Ouputs
	private String agreementId;

	public AdobeSignCreateAgreement(SecureCredentialsStore scs) {
		super();

		this.scs = scs;
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
			this.agreementId = asu.createAgreement(agreementCreationInfo);
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
	@Type(namespace = "http://plugins.ps.appiancorp.com/suite/types/", name = "ASIGN_AgreementInfo")
	@Name("agreementCreationInfo")
	public void setAgreementCreationInfo(AdobeSignAgreementCreationInfo val) {
		this.agreementCreationInfo = val;
	}

	@Name("agreementId")
	public String getAgreementId() {
		return this.agreementId;
	}

	private SmartServiceException createException(Throwable t, String key, Object... args) {
		return new SmartServiceException.Builder(getClass(), t).userMessage(key, args).build();
	}
}
