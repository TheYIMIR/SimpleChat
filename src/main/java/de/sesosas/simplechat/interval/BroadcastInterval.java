package de.sesosas.simplechat.interval;

import de.sesosas.simplechat.SimpleChat;
import de.sesosas.simplechat.api.classes.AInterval;
import de.sesosas.simplechat.event.EEventHandler;
import de.sesosas.simplechat.util.CommandResponse;
import de.sesosas.simplechat.util.CurrentConfig;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

public class BroadcastInterval extends AInterval {

    public BroadcastInterval() {
        super("BroadcastInterval");
    }

    @Override
    public void Init(){
        setIntervalTime(CurrentConfig.getLong("Tab.Refresh.Interval.Time"));
    }

    @Override
    public void Run() {
        if(CurrentConfig.getBoolean("Tab.Refresh.Interval.Enable")){
            Bukkit.getScheduler().runTask(SimpleChat.getPlugin(), () -> {
                for(String id : (String[]) CurrentConfig.getList("ids")){
                    EEventHandler.ActiveIDs.put(id, true);
                    if(!EEventHandler.ActiveIDs.get(id)) stopInterval();
                    CustomConfig.setup("", "broadcasts");
                    FileConfiguration con = CustomConfig.get();
                    for(Player player : Bukkit.getOnlinePlayers()){
                        for(String message : con.getStringList(id + ".messages")){
                            if(con.getString(id + ".type").equalsIgnoreCase("ACTIONBAR")){
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(CommandResponse.getBroadcastFormat(message)));
                            }
                            else if(con.getString(id + ".type").equalsIgnoreCase("CHAT")){
                                player.sendMessage(CommandResponse.getBroadcastFormat(message));
                            }
                        }
                    }
                }
            });
        }
    }

}
