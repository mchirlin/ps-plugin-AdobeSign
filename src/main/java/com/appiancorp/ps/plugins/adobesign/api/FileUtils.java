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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.adobe.sign.utils.ApiException;
import com.appiancorp.ps.plugins.adobesign.AdobeSignUtils;

public class FileUtils {
	private static final Logger LOG = Logger.getLogger(FileUtils.class);

	/**
	 * Saves bytes to the specified location with the specified name.
	 *
	 * @param fileData
	 *            Data bytes to be saved to disk.
	 * @param dirPath
	 *            Folder location where the file is to be saved; must end in a
	 *            path separator.
	 * @param fileName
	 *            File name with which the file is to be saved.
	 * @throws Exception
	 */
	public static void saveToFile(byte[] fileData, String dirPath, String fileName) throws ApiException {
		BufferedOutputStream outStream = null;
		try {
			// Print file name.
			LOG.info("Saving result in '" + fileName + "'.");

			// Create file and write data into the file.
			outStream = new BufferedOutputStream(new FileOutputStream(dirPath + fileName));
			outStream.write(fileData, 0, fileData.length);
			LOG.info("Successfully saved document in '" + dirPath + "'.");
		} catch (IOException e) {
			AdobeSignUtils.logException(Errors.FILE_NOT_SAVED, e);
		} finally {
			if (outStream != null) try {
				outStream.close();
			} catch (IOException e) {
				AdobeSignUtils.logException(Errors.FILE_NOT_CLOSED, e);
			}
		}
	}
}