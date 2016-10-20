package com.appiancorp.ps.plugins.adobesign;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.Document;

public class AppianUtils {
	private static final Logger LOG = Logger.getLogger(AppianUtils.class);

	public static Document getDocumentFromAppian(ContentService cs, Long documentId) throws Exception {
		Document[] docs = (Document[]) cs.download(documentId, ContentConstants.VERSION_CURRENT, Boolean.valueOf(false));
		if (docs.length > 0) {
			LOG.debug("Document size in k : " + docs[0].getSizeInKB());
			LOG.debug("Document : " + docs[0]);
			LOG.debug("Document Id : " + documentId);

			return docs[0];
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param cs
	 *            ContentService
	 * @param fileUrl
	 *            Url of file that's being received
	 * @param destinationFolder
	 *            Destination folder to create document in
	 * @param destinationDocument
	 *            Destination document to overwrite
	 * @param documentName
	 *            Document name
	 * @param documentDescription
	 *            Document description
	 * @return
	 * @throws Exception
	 */
	public static Document createDocumentInAppian(ContentService cs, String filePath, Long destinationFolder, Long destinationDocument,
			String documentName, String documentDescription) throws Exception {

		String fileName = FilenameUtils.getBaseName(filePath);
		String fileExt = FilenameUtils.getExtension(filePath);

		String docName = FilenameUtils.getBaseName(documentName);
		String docExt = FilenameUtils.getExtension(documentName);

		// Default Document Name and Extension
		if (StringUtils.isBlank(docName)) {
			docName = fileName;
		}
		if (StringUtils.isBlank(docExt)) {
			docExt = fileExt;
		}

		// Default Document Description
		if (StringUtils.isBlank(documentDescription)) {
			// NOOP;
		}

		LOG.debug("File Path : " + filePath);
		LOG.debug("Doc Name : " + docName);
		LOG.debug("Doc Extension : " + docExt);

		Document d = null;
		Long documentId = null;

		// Overwrite existing document?
		if (destinationDocument != null) {
			documentId = destinationDocument;

			// Get document that will be overwritten
			d = getDocumentFromAppian(cs, documentId);
			d.setName(docName);
			d.setExtension(docExt);
			d.setDescription(documentDescription);

			cs.createVersion(d, ContentConstants.UNIQUE_NONE);
		} else {
			// Create document object
			d = new Document(destinationFolder, docName, docExt);
			d.setDescription(documentDescription);
			d.setState(ContentConstants.STATE_PUBLISHED);

			// Create document using Appian ContentService
			documentId = cs.create(d, ContentConstants.UNIQUE_NONE);
			d.setId(documentId);
		}

		// String internalFilePath = getInternalFilePath(cs, d);
		// String internalFileName;
		//
		// internalFileName = FilenameUtils.getName(internalFilePath);
		// internalFilePath = FilenameUtils.getFullPath(internalFilePath);
		//
		// LOG.debug("Internal File Name " + internalFileName);
		// LOG.debug("Internal File Path " + internalFilePath);

		return d;
	}

	public static String getInternalFilePath(ContentService cs, Document d) throws Exception {
		String path = cs.getInternalFilename(d.getId());

		if (StringUtils.isBlank(path)) {
			path = d.getInternalFilename();
		}

		return path;
	}
}
