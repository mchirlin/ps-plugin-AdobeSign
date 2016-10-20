package com.appiancorp.ps.plugins.adobesign;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.appiancorp.services.ServiceContext;
import com.appiancorp.services.WebServiceContextFactory;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.process.ProcessDesignService;
import com.appiancorp.suiteapi.process.ProcessModel;
import com.appiancorp.suiteapi.process.ProcessStartConfig;
import com.appiancorp.suiteapi.process.ProcessVariable;
import com.appiancorp.suiteapi.servlet.AppianServlet;
import com.appiancorp.suiteapi.type.AppianType;
import com.appiancorp.suiteapi.type.NamedTypedValue;
import com.appiancorp.suiteapi.type.TypedValue;

public class AdobeSignCallbackHandler extends AppianServlet {
	private static final long serialVersionUID = 1138L;

	private static final Logger LOG = Logger.getLogger(AdobeSignCallbackHandler.class);

	private static final String PARAM_DOCUMENT_KEY = "documentKey";
	private static final String PARAM_STATUS = "status";
	private static final String PARAM_EVENT_TYPE = "eventType";

	private static final String STATUS_SIGNED = "SIGNED";
	private static final String EVENT_TYPE_ESIGNED = "ESIGNED";

	private static final String PV_DOCUMENT_KEY = "documentKey";
	private static final String PV_STATUS = "status";
	private static final String PV_EVENT_TYPE = "eventType";

	// -------------------------------------------------------------------------
	private void buildPage(PrintWriter out, String title, String[] messages) {
		out.write("<html>" + "<head>" + "<title>" + title + "</title>" + "<style type=\"text/css\">" + "body {"
				+ "font-family: Verdana, Helvetica, Sans-Serif;" + "color: #336699;" + "font-size: 12px;" + "}" + "</style>" + "</head>" + "<body>");

		for (String message : messages) {
			out.write("<p>" + message + "<p/>");
		}
		out.write("<br/></body></html>");
		out.flush();
	}

	// -------------------------------------------------------------------------
	@Override
	public void init() throws ServletException {
		LOG.info("Initializing AdobeSignCallbackHandler servlet");
	}

	// -------------------------------------------------------------------------
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// set up where we can write too
		PrintWriter out = res.getWriter();
		res.setContentType("text/html;charset=UTF-8");

		// get our parameters
		String paramDocumentKey = req.getParameter(PARAM_DOCUMENT_KEY);
		String paramStatus = req.getParameter(PARAM_STATUS);
		String paramEventType = req.getParameter(PARAM_EVENT_TYPE);

		LOG.error(String.format("Recieved a callback from Adobe Sign with a status of %s, event type of %s for document %s", paramStatus,
				paramEventType, paramDocumentKey));

		// has a document been signed?
		Boolean signed = (paramStatus.equals(STATUS_SIGNED) && paramEventType.equals(EVENT_TYPE_ESIGNED)) ? true : false;
		String processModelUUID = null;
		if (signed) {

			// get service context
			String username = req.getUserPrincipal().getName();
			ServiceContext sc = WebServiceContextFactory.getServiceContext(username);

			// get the Appian process designer service
			ProcessDesignService pds = ServiceLocator.getProcessDesignService(sc);

			ProcessVariable[] pvs = { new ProcessVariable(new NamedTypedValue(PV_DOCUMENT_KEY, (long) AppianType.STRING, paramDocumentKey)),
					new ProcessVariable(new NamedTypedValue(PV_EVENT_TYPE, (long) AppianType.STRING, paramEventType)),
					new ProcessVariable(new NamedTypedValue(PV_STATUS, (long) AppianType.STRING, paramStatus)) };

			ProcessStartConfig processStartConfig = new ProcessStartConfig(pvs);

			TypedValue constantShowAdobeSignStatusUUID = null;
			try {
				constantShowAdobeSignStatusUUID = pds.evaluateExpression("cons!OAUTH_CALLBACK_PROCESS_UUID");
			} catch (Exception e) {
				String title = "Adobe Sign Callback Handler failed";
				String caption = title + " - Exception calling pds.evaluateExpression";
				LOG.error(caption);

				String[] error = { caption };
				buildPage(out, title, error);

				LOG.error(e, e);

				return;
			}

			// do we have a constant?
			if (constantShowAdobeSignStatusUUID == null) {
				String title = "Adobee Sign Callback Handler failed";
				String caption = title + " - OAUTH_CALLBACK_PROCESS_UUID not found";
				LOG.error(caption);

				String[] error = { caption };
				buildPage(out, title, error);

				return;
			}
			processModelUUID = (String) constantShowAdobeSignStatusUUID.getValue();

			if ((processModelUUID == null) || (processModelUUID.length() == 0)) {
				String title = "Adobe Sign Callback Handler failed";
				String caption = title + " - OAUTH_CALLBACK_PROCESS_UUID is empty";
				LOG.error(caption);

				String[] error = { caption };
				buildPage(out, title, error);

				return;
			}

			try {
				ProcessModel pm = pds.getProcessModelByUuid(processModelUUID);

				pds.initiateProcess(pm.getId(), processStartConfig);
			} catch (Exception e) {
				String title = "Adobe Sign Callback Handler failed";
				String caption = title + " - Exception calling pds.getProcessModelByUuid";
				LOG.error(caption);

				String[] error = { caption };
				buildPage(out, title, error);

				LOG.error(e, e);

				return;
			}
		}

		// this isn't really needed, but nice to have for testing...
		String title = "Adobe Sign Callback Handler success";
		String caption = title + " - details";
		String[] message = { "Document Key: " + paramDocumentKey, "Status: " + paramStatus, "Event Type: " + paramEventType,
				"Signed?: " + signed.toString() };
		buildPage(out, caption, message);

		out.flush();
		out.close();
	}

	// -------------------------------------------------------------------------
	@Override
	public void destroy() {
		LOG.debug("Shutting down AdobeSignCallbackHandler servlet");
	}
}
