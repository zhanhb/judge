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
import com.ckfinder.connector.data.InitCommandEventArgs;
import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControl;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.PathUtils;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;

/**
 * Class to handle <code>Init</code> command.
 */
@Slf4j
public class InitCommand extends XMLCommand {

    /**
     * chars taken to license key.
     */
    private static final int[] LICENSE_CHARS = {11, 0, 8, 12, 26, 2, 3, 25, 1};
    private static final int LICENSE_CHAR_NR = 5;
    private static final int LICENSE_KEY_LENGTH = 34;
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    private String type;

    /**
     * method from super class - not used in this command.
     *
     * @return 0
     */
    @Override
    protected int getDataForXml() {
        return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
    }

    @Override
    protected void createXMLChildNodes(final int errorNum,
            final Element rootElement)
            throws ConnectorException {
        if (errorNum == Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE) {
            createConnectorData(rootElement);
            try {
                createResouceTypesData(rootElement);
            } catch (Exception e) {
                log.error("", e);
            }
            createPluginsData(rootElement);
        }
    }

    /**
     * Creates connector node in XML.
     *
     * @param rootElement root element in XML
     */
    private void createConnectorData(final Element rootElement) {
        // connector info
        Element element = creator.getDocument().createElement("ConnectorInfo");
        element.setAttribute("enabled", String.valueOf(configuration.enabled()));
        element.setAttribute("s", getLicenseName());
        element.setAttribute("c",
                createLicenseKey(configuration.getLicenseKey()));
        element.setAttribute("thumbsEnabled", String.valueOf(
                configuration.getThumbsEnabled()));
        element.setAttribute("uploadCheckImages", configuration.checkSizeAfterScaling() ? "false" : "true");
        if (configuration.getThumbsEnabled()) {
            element.setAttribute("thumbsUrl", configuration.getThumbsURL());
            element.setAttribute("thumbsDirectAccess", String.valueOf(
                    configuration.getThumbsDirectAccess()));
            element.setAttribute("thumbsWidth", String.valueOf(configuration.getMaxThumbWidth()));
            element.setAttribute("thumbsHeight", String.valueOf(configuration.getMaxThumbHeight()));
        }
        element.setAttribute("imgWidth", String.valueOf(configuration.getImgWidth()));
        element.setAttribute("imgHeight", String.valueOf(configuration.getImgHeight()));
        if (configuration.getPlugins().size() > 0) {
            element.setAttribute("plugins", getPlugins());
        }
        rootElement.appendChild(element);
    }

    /**
     * gets plugins names.
     *
     * @return plugins names.
     */
    private String getPlugins() {
        return configuration.getPlugins().stream()
                .filter(item -> item.isEnabled() && !item.isInternal())
                .map(item -> item.getName())
                .collect(Collectors.joining(","));
    }

    /**
     * checks license key.
     *
     * @return license name if key is ok, or empty string if not.
     */
    private String getLicenseName() {
        if (validateLicenseKey(configuration.getLicenseKey())) {
            int index = Constants.CKFINDER_CHARS.indexOf(configuration.getLicenseKey().charAt(0))
                    % LICENSE_CHAR_NR;
            if (index == 1 || index == 4) {
                return configuration.getLicenseName();
            }
        }
        return "";
    }

    /**
     * Creates license key from key in configuration.
     *
     * @param licenseKey license key from configuration
     * @return hashed license key
     */
    private String createLicenseKey(final String licenseKey) {
        if (validateLicenseKey(licenseKey)) {
            StringBuilder sb = new StringBuilder();
            for (int i : LICENSE_CHARS) {
                sb.append(licenseKey.charAt(i));
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * validates license key length.
     *
     * @param licenseKey config license key
     * @return true if has correct length
     */
    private boolean validateLicenseKey(final String licenseKey) {
        return licenseKey != null && licenseKey.length() == LICENSE_KEY_LENGTH;
    }

    /**
     * Creates plugins node in XML.
     *
     * @param rootElement root element in XML
     * @throws ConnectorException when error in event handler occurs.
     */
    public void createPluginsData(final Element rootElement) throws ConnectorException {
        Element element = creator.getDocument().createElement("PluginsInfo");
        rootElement.appendChild(element);
        InitCommandEventArgs args = new InitCommandEventArgs();
        args.setXml(this.creator);
        args.setRootElement(rootElement);
        if (configuration.getEvents() != null) {
            configuration.getEvents().runInitCommand(args, configuration);
        }

    }

    /**
     * Creates plugins node in XML.
     *
     * @param rootElement root element in XML
     * @throws Exception when error occurs
     */
    private void createResouceTypesData(final Element rootElement) throws Exception {
        //resurcetypes
        Element element = creator.getDocument().createElement("ResourceTypes");
        rootElement.appendChild(element);

        Set<String> types;
        if (super.type != null && !super.type.isEmpty()) {
            types = new LinkedHashSet<>();
            types.add(super.type);
        } else {
            types = getTypes();
        }

        for (String key : types) {
            ResourceType resourceType = configuration.getTypes().get(key);
            if (((this.type == null || this.type.equals(key)) && resourceType != null)
                    && getAccessControl().checkFolderACL(key, "/", this.userRole,
                            AccessControl.CKFINDER_CONNECTOR_ACL_FOLDER_VIEW)) {

                Element childElement = creator.getDocument().
                        createElement("ResourceType");
                childElement.setAttribute("name", resourceType.getName());
                childElement.setAttribute("acl", String.valueOf(getAccessControl().checkACLForRole(key, "/", this.userRole)));
                childElement.setAttribute("hash", randomHash(
                        resourceType.getPath()));
                childElement.setAttribute(
                        "allowedExtensions",
                        resourceType.getAllowedExtensions());
                childElement.setAttribute(
                        "deniedExtensions",
                        resourceType.getDeniedExtensions());
                childElement.setAttribute("url", resourceType.getUrl() + "/");
                long maxSize = resourceType.getMaxSize();
                childElement.setAttribute("maxSize", maxSize > 0 ? Long.toString(maxSize) : "0");
                childElement.setAttribute("hasChildren",
                        FileUtils.hasChildren(getAccessControl(), "/", Paths.get(PathUtils.escape(resourceType.getPath())),
                                configuration, resourceType.getName(), this.userRole).toString());
                element.appendChild(childElement);
            }
        }
    }

    /**
     * gets list of types names.
     *
     * @return list of types names.
     */
    private Set<String> getTypes() {
        if (configuration.getDefaultResourceTypes().size() > 0) {
            return configuration.getDefaultResourceTypes();
        } else {
            return configuration.getTypes().keySet();
        }
    }

    /**
     * Gets hash for folders in XML response to avoid cached responses.
     *
     * @param folder folder
     * @return hash value
     */
    private String randomHash(final String folder) {

        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            algorithm.update(folder.getBytes("UTF8"));
            byte[] messageDigest = algorithm.digest();

            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return hexString.substring(0, 15);
        } catch (NoSuchAlgorithmException e) {
            log.error("", e);
            return "";
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    protected boolean mustAddCurrentFolderNode() {
        return false;
    }

    @Override
    protected void getCurrentFolderParam(final HttpServletRequest request) {
        this.currentFolder = null;
    }
}
