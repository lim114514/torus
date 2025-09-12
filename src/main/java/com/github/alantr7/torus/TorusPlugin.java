package com.github.alantr7.torus;

import com.github.alantr7.bukkitplugin.BukkitPlugin;
import com.github.alantr7.bukkitplugin.annotations.generative.JavaPlugin;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocate;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocations;

@JavaPlugin(name = "Torus")
@Relocations(@Relocate(from = "com.github.alantr7.bukkitplugin", to = "com.github.alantr7.torus.bpf"))
public class TorusPlugin extends BukkitPlugin {

    @Override
    protected void onPluginEnable() {

    }

    @Override
    protected void onPluginDisable() {

    }

}
