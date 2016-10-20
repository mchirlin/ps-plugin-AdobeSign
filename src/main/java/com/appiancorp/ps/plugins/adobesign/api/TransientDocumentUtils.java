/*
 *  Copyright 2016 Adobe Systems Incorporated. All rights reserved.
 *  This file is licensed to you under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License. You may obtain a copy
 *  of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 *  OF ANY KIND, either express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 *
 */
package com.appiancorp.ps.plugins.adobesign.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.adobe.sign.api.TransientDocumentsApi;
import com.adobe.sign.model.transientDocuments.TransientDocumentResponse;
import com.adobe.sign.utils.ApiException;
import com.appiancorp.ps.plugins.adobesign.AdobeSignUtils;

public class TransientDocumentUtils {

	private final static TransientDocumentsApi transientDocumentsApi = new TransientDocumentsApi();

	/**
	 * Uploads a document and obtains the document ID. The document uploaded
	 * through this call is referred to as transient since it is available only
	 * for 7 days after the upload. The returned transient document ID can be
	 * used to refer to the document in api calls like create agreements where
	 * uploaded file needs to be referred. The transient document request is a
	 * multipart request consisting of three parts - filename, mime type and the
	 * file stream. You can only upload one file at a time in this request.
	 * 
	 * @param path
	 *            path containing the file
	 * @param fileName
	 *            name of the file
	 * @return TransientDocumentResponse
	 * @throws IOException
	 */
	public static TransientDocumentResponse createTransientDocument(String path, String fileName) throws ApiException, IOException {
		try {
			// Create a file object
			File file = new File(path);

			String contentType = Files.probeContentType(Paths.get(path));

			// Make API call to create transient document.
			TransientDocumentResponse transientDocumentResponse = transientDocumentsApi.createTransientDocument(AdobeSignUtils.getHeaderParams(),
					file.getAbsoluteFile(), fileName, contentType);
			return transientDocumentResponse;
		} catch (ApiException e) {
			AdobeSignUtils.logException(Errors.CREATE_TRANSIENT_DOCUMENT, e);
			return null;
		}
	}
}