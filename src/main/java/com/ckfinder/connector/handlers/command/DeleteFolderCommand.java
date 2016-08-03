/*
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2015, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
 */
package com.ckfinder.connector.handlers.command;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControl;
import com.ckfinder.connector.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;

/**
 * Class to handle <code>DeleteFolder</code> command.
 */
@Slf4j
public class DeleteFolderCommand extends XMLCommand implements IPostCommand {

    @Override
    protected void initParams(HttpServletRequest request,
            IConfiguration configuration, Object... params)
            throws ConnectorException {

        super.initParams(request, configuration, params);
    }

    @Override
    protected void createXMLChildNodes(int errorNum, Element rootElement)
            throws ConnectorException {
    }

    /**
     * @return error code or 0 if ok. Deletes folder and thumb folder.
     */
    @Override
    protected int getDataForXml() {

        if (!checkIfTypeExists(this.type)) {
            this.type = null;
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
        }

        if (!getAccessControl().checkFolderACL(this.type,
                this.currentFolder,
                this.userRole,
                AccessControl.CKFINDER_CONNECTOR_ACL_FOLDER_DELETE)) {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
        }
        if (this.currentFolder.equals("/")) {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
        }

        if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
        }

        Path dir = Paths.get(configuration.getTypes().get(this.type).getPath()
                + this.currentFolder);

        try {
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_FOLDER_NOT_FOUND;
            }

            if (FileUtils.delete(dir)) {
                Path thumbDir = Paths.get(configuration.getThumbsPath(),
                        this.type
                        + this.currentFolder);
                FileUtils.delete(thumbDir);
            } else {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
            }
        } catch (SecurityException e) {
            log.error("", e);
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;
        }

        return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
    }
}
