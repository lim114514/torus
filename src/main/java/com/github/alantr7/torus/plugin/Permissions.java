package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.generative.Permission;

public class Permissions {

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for getting an item by left clicking it in the browser GUI")
    public static final String BROWSE_GUI_GET_ITEM = "torus.browser.item.get";

}
