package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bukkitplugin.commands.annotations.CommandHandler;
import com.github.alantr7.bukkitplugin.commands.executor.Evaluator;
import com.github.alantr7.bukkitplugin.commands.executor.ExecutorType;
import com.github.alantr7.bukkitplugin.commands.factory.CommandBuilder;
import com.github.alantr7.bukkitplugin.commands.registry.Command;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.gui.browser.ItemBrowserMainGUI;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.structure.Conductor;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.TorusChunk;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Singleton
public class Commands {

    @com.github.alantr7.bukkitplugin.annotations.generative.Command(name = "torus", description = "")
    public static final String TORUS = "torus";

    @CommandHandler Command give = CommandBuilder.using("torus")
      .parameter("give")
      .parameter("{target}", Evaluator.PLAYER, p -> p.tabComplete(args -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()))
      .parameter("{item}", p -> p.tabComplete((args) -> {
          if (args.length > 2 && !"torus:".startsWith(args[2]) && !args[2].startsWith("torus:")) {
              return TorusPlugin.getInstance().getItemManager().getItemIds().stream().map(id -> id.substring("torus:".length())).toList();
          }
          return TorusPlugin.getInstance().getItemManager().getItemIds();
      }))
      .parameter("{amount}", Evaluator.INTEGER, p -> p.defaultValue(ctx -> 1))
      .permission(Permissions.COMMAND_GIVE)
      .requireMatches(3)
      .executes(ctx -> {
          Player target = (Player) ctx.getArgument("target");
          if (target == null) {
              ctx.respond(ChatColor.RED + "Specified player could not be found.");
              return;
          }

          TorusItem item = TorusPlugin.getInstance().getItemManager().getItemById((String) ctx.getArgument("item"));
          if (item == null) {
              ctx.respond(ChatColor.RED + "Specified item does not exist.");
              return;
          }

          if (ctx.optArgument("amount").isEmpty() || (int) ctx.getArgument("amount") < 1 || (int) ctx.getArgument("amount") > 150) {
              ctx.respond(ChatColor.RED + "Specified amount is not a valid number.");
              return;
          }

          ItemStack stack = item.toItemStack().clone();
          stack.setAmount((int) ctx.getArgument("amount"));

          ((Player) ctx.getExecutor()).getInventory().addItem(stack);
          ctx.respond(ChatColor.YELLOW + "You received 1 x " + ChatColor.GOLD + item.name);
      });

    @CommandHandler Command browse = CommandBuilder.using("torus")
      .parameter("browse")
      .forExecutors(ExecutorType.PLAYER).permission(Permissions.COMMAND_BROWSE)
      .executes(ctx -> {
          new ItemBrowserMainGUI((Player) ctx.getExecutor()).open();
      });

    @CommandHandler Command recipe = CommandBuilder.using("torus")
      .parameter("recipe")
      .parameter("{item}", p -> p.tabComplete((args) -> {
            if (args.length > 2 && !"torus:".startsWith(args[2]) && !args[2].startsWith("torus:")) {
                return TorusPlugin.getInstance().getItemManager().getItemIds().stream().map(id -> id.substring("torus:".length())).toList();
            }
            return TorusPlugin.getInstance().getItemManager().getItemIds();
        }))
      .forExecutors(ExecutorType.PLAYER).permission(Permissions.COMMAND_RECIPE)
      .executes(ctx -> {
          TorusItem item = TorusPlugin.getInstance().getItemManager().getItemById((String) ctx.getArgument("item"));
          if (item == null) {
              ctx.respond(ChatColor.RED + "Specified item does not exist.");
              return;
          }

          if (!item.hasRecipes()) {
              ctx.respond(ChatColor.RED + "This item does not have any recipes.");
              return;
          }

          Keyed recipe = item.getRecipes().iterator().next();
          GUI viewer = TorusPlugin.getInstance().getRecipeManager().createRecipeViewer((Player) ctx.getExecutor(), recipe);

          if (viewer == null) {
              ctx.respond(ChatColor.RED + "This recipe can not be previewed.");
              return;
          }

          viewer.open();
      });

    @CommandHandler Command reload = CommandBuilder.using("torus")
      .parameter("reload")
      .permission(Permissions.COMMAND_RELOAD)
      .executes(ctx -> {
          TorusPlugin.getInstance().getConfigManager().initialize();
          ctx.respond(ChatColor.YELLOW + "Configurations reloaded.");
      });

    @CommandHandler Command logStructureIds = CommandBuilder.using("torus")
      .parameter("debug")
      .parameter("log_structure_ids")
      .permission(Permissions.COMMAND_DEBUG)
      .executes(ctx -> {
          TorusPlugin.getInstance().getStructureRegistry().getStructuresIds().forEach(entry -> {
              ctx.respond(String.format("%2d: %s", entry.getValue(), entry.getKey()));
          });
      });

    @CommandHandler Command inspectStructure = CommandBuilder.using("torus")
      .parameter("debug")
      .parameter("inspect_structure")
      .forExecutors(ExecutorType.PLAYER).permission(Permissions.COMMAND_DEBUG)
      .executes(ctx -> {
          Player player = (Player) ctx.getExecutor();
          Block block = player.getTargetBlockExact(5);
          if (block == null) {
              ctx.respond("Not looking at any structure.");
              return;
          }

          TorusWorld world = TorusPlugin.getInstance().getWorldManager().getWorld(player.getWorld());
          if (world == null) {
              ctx.respond("Structures aren't allowed in this world.");
              return;
          }

          StructureInstance structure = world.getStructure(new BlockLocation(block.getLocation()));
          if (structure == null) {
              ctx.respond("Not looking at any structure.");
              return;
          }

          ctx.respond("\n");
          ctx.respond(ChatColor.GOLD + "Structure ID: " + ChatColor.RESET + structure.structure.id);
          ctx.respond(ChatColor.GOLD + "Owner ID: " + structure.getOwnerId());
          ctx.respond(ChatColor.GOLD + "Connectors: (" + structure.getConnectors().size() + ")");
          for (Connector connector : structure.getConnectors()) {
              ctx.respond("  - " + ChatColor.YELLOW + connector.getComponent().absoluteLocation + ":");
              ctx.respond("    - Matter: " + ChatColor.GRAY + connector.matter);
              ctx.respond("    - Flow: " + ChatColor.GRAY + connector.getFlowDirection());
              ctx.respond("    - Connections: " + ChatColor.GRAY + connector.getConnections());
          }
      });

    @CommandHandler Command inspectNetwork = CommandBuilder.using("torus")
      .parameter("debug")
      .parameter("inspect_network")
      .forExecutors(ExecutorType.PLAYER).permission(Permissions.COMMAND_DEBUG)
      .executes(ctx -> {
          Player player = (Player) ctx.getExecutor();
          Block block = player.getTargetBlockExact(5);
          if (block == null) {
              ctx.respond("Not looking at any structure.");
              return;
          }

          TorusWorld world = TorusPlugin.getInstance().getWorldManager().getWorld(player.getWorld());
          if (world == null) {
              ctx.respond("Structures aren't allowed in this world.");
              return;
          }

          BlockLocation blockLocation = new BlockLocation(block.getLocation());
          StructureInstance structure = world.getStructure(blockLocation);
          if (structure == null) {
              ctx.respond("Not looking at any structure.");
              return;
          }

          for (Connector connector : structure.getConnectors()) {
              ctx.respond(connector.getComponent().name + "  " + connector.getComponent().absoluteLocation + " (" + connector.matter + ")" + ":");
              for (Connector.Connection connection : connector.networkConnections) {
                  ctx.respond("   - " + connection.structure.structure.name + " (" + connection.connector.getComponent().absoluteLocation + ")");
              }
          }

          if (structure instanceof Conductor conductor) {
              ctx.respond("Conductor nodes:");
              for (BlockLocation loc : conductor.getConnectedNodes()) {
                ctx.respond("  - " + (loc.getStructure() != null ? loc.getStructure().structure.name : "Null?"));
              }
          }
      });

    @CommandHandler Command inspectChunk = CommandBuilder.using("torus")
      .parameter("debug")
      .parameter("inspect_chunk")
      .forExecutors(ExecutorType.PLAYER).permission(Permissions.COMMAND_DEBUG)
      .executes(ctx -> {
          Player player = (Player) ctx.getExecutor();
          TorusChunk chunk = TorusPlugin.getInstance().getWorldManager().getWorld(player.getWorld()).getChunk(
            new BlockLocation(player.getLocation())
          );

          if (chunk == null) {
              ctx.respond("This chunk does not have any structures.");
              return;
          }

          ctx.respond("Chunk position: " + chunk.position);
          ctx.respond("Chunk size: " + chunk.getSize() + " bytes");
          ctx.respond("Structures: " + chunk.getStructures().size());
          ctx.respond("Occupations: " + chunk.getOccupations().size());
      });

}
