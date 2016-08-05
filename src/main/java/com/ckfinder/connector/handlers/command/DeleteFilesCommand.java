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
import com.ckfinder.connector.data.FilePostParam;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControl;
import com.ckfinder.connector.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;

/**
 * Class used to handle <code>DeleteFiles</code> command.
 */
@Slf4j
public class DeleteFilesCommand extends XMLCommand implements IPostCommand {

    private List<FilePostParam> files;
    private int filesDeleted;
    private boolean addDeleteNode;

    @Override
    protected void createXMLChildNodes(int errorNum, Element rootElement) throws ConnectorException {
        if (creator.hasErrors()) {
            Element errorsNode = creator.getDocument().createElement("Errors");
            creator.addErrors(errorsNode);
            rootElement.appendChild(errorsNode);
        }

        if (this.addDeleteNode) {
            createDeleteFielsNode(rootElement);
        }
    }

    /**
     * Adds file deletion node in XML response.
     *
     * @param rootElement root element in XML response
     */
    private void createDeleteFielsNode(Element rootElement) {
        Element element = creator.getDocument().createElement("DeleteFiles");
        element.setAttribute("deleted", String.valueOf(this.filesDeleted));
        rootElement.appendChild(element);
    }

    /**
     * Prepares data for XML response.
     *
     * @return error code or 0 if action ended with success.
     */
    @Override
    protected int getDataForXml() {

        this.filesDeleted = 0;

        this.addDeleteNode = false;

        if (!checkIfTypeExists(this.type)) {
            this.type = null;
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
        }

        for (FilePostParam fileItem : this.files) {
            if (!FileUtils.checkFileName(fileItem.getName())) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (configuration.getTypes().get(fileItem.getType()) == null) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (fileItem.getFolder() == null || fileItem.getFolder().isEmpty()
                    || Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(
                    fileItem.getFolder()).find()) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (FileUtils.checkIfDirIsHidden(fileItem.getFolder(), this.configuration)) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (FileUtils.checkIfFileIsHidden(fileItem.getName(), this.configuration)) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (FileUtils.checkFileExtension(fileItem.getName(), this.configuration.getTypes().get(fileItem.getType())) == 1) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;

            }

            if (!getAccessControl().checkFolderACL(fileItem.getType(), fileItem.getFolder(), this.userRole,
                    AccessControl.CKFINDER_CONNECTOR_ACL_FILE_DELETE)) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
            }

            Path file = Paths.get(configuration.getTypes().get(fileItem.getType()).getPath() + fileItem.getFolder(), fileItem.getName());

            try {
                this.addDeleteNode = true;
                if (!Files.exists(file)) {
                    creator.appendErrorNodeChild(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_FILE_NOT_FOUND,
                            fileItem.getName(), fileItem.getFolder(), fileItem.getType());
                    continue;
                }

                if (FileUtils.delete(file)) {
                    Path thumbFile = Paths.get(configuration.getThumbsPath(),
                            fileItem.getType() + this.currentFolder, fileItem.getName());
                    this.filesDeleted++;

                    try {
                        FileUtils.delete(thumbFile);
                    } catch (Exception exp) {
                        // No errors if we are not able to delete the thumb.
                    }
                } else { //If access is denied, report error and try to delete rest of files.
                    creator.appendErrorNodeChild(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
                            fileItem.getName(), fileItem.getFolder(), fileItem.getType());
                }
            } catch (SecurityException e) {
                log.error("", e);
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED;

            }
        }
        if (creator.hasErrors()) {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_DELETE_FAILED;
        } else {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
        }
    }

    /**
     * Initializes parameters for command handler.
     *
     * @param request current response object
     * @param configuration connector configuration object
     * @throws ConnectorException when initialization parameters can't be loaded
     * for command handler.
     */
    @Override
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    protected void initParams(HttpServletRequest request, IConfiguration configuration) throws ConnectorException {
        super.initParams(request, configuration);
        this.files = new ArrayList<>();
        getFilesListFromRequest(request);
    }

    /**
     * Gets list of files from request.
     *
     * @param request current request object
     */
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    private void getFilesListFromRequest(HttpServletRequest request) {
        int i = 0;
        String paramName = "files[" + i + "][name]";
        while (request.getParameter(paramName) != null) {
            FilePostParam file = new FilePostParam();
            file.setName(request.getParameter(paramName));
            file.setFolder(request.getParameter("files[" + i + "][folder]"));
            file.setOptions(request.getParameter("files[" + i + "][options]"));
            file.setType(request.getParameter("files[" + i + "][type]"));
            this.files.add(file);
            paramName = "files[" + (++i) + "][name]";
        }
    }

}
