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

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus use_preset")
    public static final String COMMAND_USE_PRESET = "torus.command.use_preset";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus export_model")
    public static final String COMMAND_EXPORT_MODEL = "torus.command.export_model";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for using /torus debug")
    public static final String COMMAND_DEBUG = "torus.command.debug";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for getting an item by left clicking it in the browser GUI")
    public static final String BROWSE_GUI_GET_ITEM = "torus.browser.item.get";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for breaking structures belonging to other players")
    public static final String STRUCTURE_BREAK_OTHERS = "torus.structure.break.others";

    @Permission(allowed = Permission.Allowed.OP, description = "Permission for getting update availability message")
    public static final String UPDATE_AVAILABLE_NOTIFICATION = "torus.update.message";

}
