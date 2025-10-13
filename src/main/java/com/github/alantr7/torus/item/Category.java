package com.github.alantr7.torus.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class Category {

    public final String id;

    public final String name;

    public ItemStack display;

    public final List<TorusItem> items = new LinkedList<>();

    public static final Category RESOURCES = new Category("resources", "Resources", new ItemStack(Material.STICK));

    public static final Category GENERATORS = new Category("generators", "Generators", new ItemStack(Material.PAPER));

    public static final Category MACHINES = new Category("machines", "Machines", new ItemStack(Material.PAPER));

    public static final Category NETWORK = new Category("network", "Network", new ItemStack(Material.REDSTONE));

    public static final Category COMPONENTS = new Category("components", "Components", new ItemStack(Material.STICK));

    public static final Category STORAGE = new Category("storage", "Storage", new ItemStack(Material.CHEST));

    public static final Category TOOLS = new Category("tools", "Tools", new ItemStack(Material.STICK));

    public Category(String id, String name, ItemStack display) {
        this.id = id;
        this.name = name;
        this.display = display;
    }

}
