package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.generative.Permission;

public class Permissions {

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus give")
    public static final String COMMAND_GIVE = "torus.command.give";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus browse")
    public static final String COMMAND_BROWSE = "torus.command.browse";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus recipe")
    public static final String COMMAND_RECIPE = "torus.command.recipe";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus reload")
    public static final String COMMAND_RELOAD = "torus.command.reload";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus debug")
    public static final String COMMAND_DEBUG = "torus.command.debug";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for getting an item by left clicking it in the browser GUI")
    public static final String BROWSE_GUI_GET_ITEM = "torus.browser.item.get";

}
