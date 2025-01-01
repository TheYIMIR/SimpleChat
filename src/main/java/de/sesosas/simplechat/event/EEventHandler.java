package de.sesosas.simplechat.event;

import de.sesosas.simplechat.SimpleChat;
import de.sesosas.simplechat.util.ChatLayout;
import de.sesosas.simplechat.util.CommandResponse;
import de.sesosas.simplechat.util.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class EEventHandler implements Listener {

     public static HashMap<String, Boolean> ActiveIDs = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        String configPath = event.getPlayer().getUniqueId().toString();
        CustomConfig cf = new CustomConfig().setup(configPath);
        if(cf.isEmpty() || cf.exist(configPath)){
            FileConfiguration con = cf.get();
            if(con.get("Staff") == null) con.set("Staff", false);
            if(con.get("Mute") == null) con.set("Mute", false);

            try {
                con.save(new File(Bukkit.getServer().getPluginManager()
                        .getPlugin(SimpleChat.getPlugin().getName()).getDataFolder(), configPath + ".yml"));
            } catch (IOException e) {
                Bukkit.getLogger().severe("Error saving animations configuration.");
                e.printStackTrace();
            }
        }
    }

    public HashMap<Player, List<String>> messageList = new HashMap<>();
    public HashMap<Player, String> msgListID = new HashMap<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String layout = "";
        CustomConfig.setup(event.getPlayer());
        FileConfiguration con = CustomConfig.get();
        if(con.getBoolean("BroadcastEditor.Use")){
            if(event.getMessage().equalsIgnoreCase("done")){
                CustomConfig.setup("","broadcasts");
                FileConfiguration con1 = CustomConfig.get();
                con1.set(msgListID.get(event.getPlayer()) + ".messages", messageList.get(event.getPlayer()));
                CustomConfig.save();
                messageList.remove(event.getPlayer());
                event.getPlayer().sendMessage(CommandResponse.getFormat("Saved and started broadcast with id: " + msgListID.get(event.getPlayer())));
                List<String> ids = con1.getStringList("ids");
                ids.add(msgListID.get(event.getPlayer()));
                con1.set("ids", ids);
                if(!ActiveIDs.get(msgListID.get(event.getPlayer()))){
                    runStartBroadCast(con1.getLong(msgListID.get(event.getPlayer()) + ".interval"), con1.getString(msgListID.get(event.getPlayer())));
                }
                else{
                    ActiveIDs.remove(msgListID.get(event.getPlayer()));
                    ActiveIDs.put(msgListID.get(event.getPlayer()), true);

                }
                msgListID.remove(event.getPlayer());
                CustomConfig.save();
                CustomConfig.setup(event.getPlayer());
                FileConfiguration con2 = CustomConfig.get();
                con2.set("BroadcastEditor.Use", false);
            }
            else{
                List<String> msgl = messageList.get(event.getPlayer());
                msgl.add(event.getMessage());
                messageList.put(event.getPlayer(), msgl);
            }
        }
        else{
            con.set("BroadCastEditor.Step", 0);
            if(con.getBoolean("Staff")){
                event.setCancelled(true);
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(player.hasPermission("lpchatsystem.staff")){
                        layout = ChatLayout.getChatLayout(event.getPlayer(), event.getMessage(), true);
                        player.sendMessage(layout);
                    }
                }
            }
            else{
                if(con.getBoolean("Mute")){
                    event.setCancelled(true);
                }
                else{
                    layout = ChatLayout.getChatLayout(event.getPlayer(), event.getMessage(), false);
                    event.setFormat(layout);
                }
            }
        }
    }

}
