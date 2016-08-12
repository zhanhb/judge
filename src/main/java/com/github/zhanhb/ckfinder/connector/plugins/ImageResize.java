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
package com.github.zhanhb.ckfinder.connector.plugins;

import com.github.zhanhb.ckfinder.connector.configuration.Events;
import com.github.zhanhb.ckfinder.connector.configuration.Plugin;

public class ImageResize extends Plugin {

    @Override
    public void registerEventHandlers(Events.Builder builder) {
        builder.beforeExecuteCommandEventHandler(() -> new ImageResizeCommad(getPluginInfo()))
                .beforeExecuteCommandEventHandler(ImageResizeInfoCommand::new)
                .initCommandEventHandler(() -> new ImageResizeInitCommandEventHandler(getPluginInfo()));
    }

}
