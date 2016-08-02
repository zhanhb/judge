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
import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControl;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.PathUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all command handlers.
 */
public abstract class Command {

    /**
     * Connector configuration.
     */
    protected IConfiguration configuration;
    protected String userRole;
    protected String currentFolder;
    protected String type;

    @Getter(AccessLevel.PROTECTED)
    @Setter
    private AccessControl accessControl;

    /**
     * standard constructor.
     */
    public Command() {
        configuration = null;
        userRole = null;
        currentFolder = null;
        type = null;
    }

    /**
     * Runs command. Initialize, sets response and execute command.
     *
     * @param request request
     * @param response response
     * @param configuration connector configuration
     * @param params additional execute parameters.
     * @throws ConnectorException when error occurred.
     */
    public void runCommand(final HttpServletRequest request,
            final HttpServletResponse response,
            final IConfiguration configuration,
            final Object... params) throws ConnectorException {
        this.initParams(request, configuration, params);
        try {
            setResponseHeader(response, request.getServletContext());
            execute(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED, e);
        }
    }

    /**
     * initialize params for command handler.
     *
     * @param request request
     * @param configuration connector configuration
     * @param params execute additional params.
     * @throws ConnectorException to handle in error handler.
     */
    protected void initParams(final HttpServletRequest request,
            final IConfiguration configuration, final Object... params)
            throws ConnectorException {
        if (configuration != null) {
            this.configuration = configuration;
            this.userRole = (String) request.getSession().getAttribute(
                    this.configuration.getUserRoleName());

            getCurrentFolderParam(request);

            if (checkConnector(request) && checkParam(this.currentFolder)) {
                this.currentFolder = PathUtils.escape(this.currentFolder);
                if (!checkHidden()) {
                    if ((this.currentFolder == null || this.currentFolder.isEmpty())
                            || checkIfCurrFolderExists(request)) {
                        this.type = request.getParameter("type");
                    }
                }
            }
        }
    }

    /**
     * check if connector is enabled and checks authentication.
     *
     * @param request current request.
     * @return true if connector is enabled and user is authenticated
     * @throws ConnectorException when connector is disabled
     */
    protected boolean checkConnector(final HttpServletRequest request)
            throws ConnectorException {
        if (!configuration.enabled() || !configuration.checkAuthentication(request)) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_CONNECTOR_DISABLED, false);
        }
        return true;
    }

    /**
     * Checks if current folder exists.
     *
     * @param request current request object
     * @return {@code true} if current folder exists
     * @throws ConnectorException if current folder doesn't exist
     */
    protected boolean checkIfCurrFolderExists(final HttpServletRequest request)
            throws ConnectorException {
        String tmpType = request.getParameter("type");
        if (tmpType != null) {
            if (checkIfTypeExists(tmpType)) {
                Path currDir = Paths.get(
                        configuration.getTypes().get(tmpType).getPath()
                        + this.currentFolder);
                if (!Files.exists(currDir) || !Files.isDirectory(currDir)) {
                    throw new ConnectorException(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_FOLDER_NOT_FOUND,
                            false);
                } else {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if type of resource provided as parameter exists.
     *
     * @param type name of the resource type to check if it exists
     * @return {@code true} if provided type exists, {@code false} otherwise.
     */
    protected boolean checkIfTypeExists(final String type) {
        ResourceType testType = configuration.getTypes().get(type);
        return testType != null;
    }

    /**
     * checks if current folder is hidden.
     *
     * @return false if isn't.
     * @throws ConnectorException when is hidden
     */
    protected boolean checkHidden() throws ConnectorException {
        if (FileUtils.checkIfDirIsHidden(this.currentFolder, configuration)) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST,
                    false);
        }
        return false;
    }

    /**
     * executes command and writes to response.
     *
     * @param out response output stream
     * @throws ConnectorException when error occurs
     * @throws java.io.IOException
     */
    public abstract void execute(final OutputStream out)
            throws ConnectorException, IOException;

    /**
     * sets header in response.
     *
     * @param response servlet response
     * @param sc servlet context
     */
    public abstract void setResponseHeader(final HttpServletResponse response,
            final ServletContext sc);

    /**
     * check request for security issue.
     *
     * @param reqParam request param
     * @return true if validation passed
     * @throws ConnectorException if validation error occurs.
     */
    protected boolean checkParam(final String reqParam)
            throws ConnectorException {
        if (reqParam == null || reqParam.isEmpty()) {
            return true;
        }
        if (Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(reqParam).find()) {
            throw new ConnectorException(
                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME,
                    false);
        }

        return true;
    }

    /**
     * gets current folder request param or sets default value if it's not set.
     *
     * @param request request
     */
    protected void getCurrentFolderParam(final HttpServletRequest request) {
        String currFolder = request.getParameter("currentFolder");
        if (currFolder == null || currFolder.isEmpty()) {
            this.currentFolder = "/";
        } else {
            this.currentFolder = PathUtils.addSlashToBeginning(PathUtils.addSlashToEnd(currFolder));
        }
    }

    /**
     * If string provided as parameter is null, this method converts it to empty
     * string.
     *
     * @param s string to check and convert if it is null
     * @return empty string if parameter was {@code null} or unchanged string if
     * parameter was nonempty string.
     */
    protected String nullToString(String s) {
        return s == null ? "" : s;
    }

}
