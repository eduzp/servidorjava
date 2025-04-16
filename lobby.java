package dev.eduzp.lobby.cmd;

import dev.eduzp.lobby.cmd.sl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand extends Commands {
   private final List<SubCommand> commands = new ArrayList();

   public LobbyCommand() {
      super("zp", "zlobby");
      this.commands.add(new SetSpawnCommand());
      this.commands.add(new BuildCommand());
      this.commands.add(new NPCPlayCommand());
      this.commands.add(new NPCDeliveryCommand());
      this.commands.add(new GiveCommand());
   }

   public void perform(CommandSender sender, String label, String[] args) {
      if (!sender.hasPermission("cmd.lobby")) {
         sender.sendMessage("§fComando desconhecido.");
      } else if (args.length == 0) {
         this.sendHelp(sender, 1);
      } else {
         try {
            this.sendHelp(sender, Integer.parseInt(args[0]));
         } catch (Exception var7) {
            SubCommand subCommand = (SubCommand)this.commands.stream().filter((sc) -> {
               return sc.getName().equalsIgnoreCase(args[0]);
            }).findFirst().orElse((SubCommand) null);
            if (subCommand == null) {
               this.sendHelp(sender, 1);
               return;
            }

            List<String> list = new ArrayList();
            list.addAll(Arrays.asList(args));
            list.remove(0);
            if (subCommand.onlyForPlayer()) {
               if (!(sender instanceof Player)) {
                  sender.sendMessage("§cEsse comando pode ser utilizado apenas pelos jogadores.");
                  return;
               }

               subCommand.perform((Player)sender, (String[])list.toArray(new String[list.size()]));
            } else {
               subCommand.perform(sender, (String[])list.toArray(new String[list.size()]));
            }
         }

      }
   }

   private void sendHelp(CommandSender sender, int page) {
      List<SubCommand> commands = (List)this.commands.stream().filter((subcommand) -> {
         return sender instanceof Player || !subcommand.onlyForPlayer();
      }).collect(Collectors.toList());
      Map<Integer, StringBuilder> pages = new HashMap();
      int pagesCount = (commands.size() + 6) / 7;

      for(int index = 0; index < commands.size(); ++index) {
         int currentPage = (index + 7) / 7;
         if (!pages.containsKey(currentPage)) {
            pages.put(currentPage, new StringBuilder(" \n§dzLobby - " + currentPage + "/" + pagesCount + "\n \n"));
         }

         ((StringBuilder)pages.get(currentPage)).append("§d/zp " + ((SubCommand)commands.get(index)).getUsage() + " §f- §7" + ((SubCommand)commands.get(index)).getDescription() + "\n");
      }

      StringBuilder sb = (StringBuilder)pages.get(page);
      if (sb == null) {
         sender.sendMessage("§cPágina não encontrada.");
      } else {
         sb.append(" ");
         sender.sendMessage(sb.toString());
      }
   }
}
