package de.sesosas.simplechat;

import de.sesosas.simplechat.api.classes.AInterval;
import de.sesosas.simplechat.api.utils.ThreadUtil;
import de.sesosas.simplechat.event.EEventHandler;
import de.sesosas.simplechat.util.CustomConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class SimpleChat extends JavaPlugin {

    public FileConfiguration config = getConfig();
    public static SimpleChat plugin;

    public static SimpleChat getPlugin(){
        return plugin;
    }


    @Override
    public void onEnable() {
        plugin = this;

        config.addDefault("Prefix.Commands", "&e[&cLPChatSystem&e]");
        config.addDefault("Prefix.Staff", "&e[&cStaffChat&e]");
        config.addDefault("Prefix.Broadcast", "&e[&cBroadcast&e]");
        config.addDefault("Chat.Normal.Layout", "%luckperms_prefix% %player_name% %luckperms_suffix%");
        config.addDefault("Chat.Staff.Layout", "%player_name%");
        config.addDefault("Chat.Seperator", "&f:");
        List<String> bwords = new ArrayList<>();
        bwords.add("ass");
        bwords.add("dick");
        bwords.add("asshole");
        config.addDefault("Chat.BannedWords", bwords);
        config.addDefault("Chat.Functions.Mute", true);
        config.addDefault("Chat.Functions.Staff", true);
        config.options().copyDefaults(true);
        saveConfig();

        getCommand("lpchatsystem-reload").setExecutor(new ReloadCommand());
        getCommand("clearchat").setExecutor(new ClearCommand());
        getCommand("mute").setExecutor(new MuteCommand());
        getCommand("staff").setExecutor(new StaffCommand());
        getServer().getPluginManager().registerEvents(new EEventHandler(), this);

        CustomConfig.setup("", "broadcasts");
        FileConfiguration con = CustomConfig.get();
        for(String id : con.getStringList("ids")){
            ActiveIDs.put(id, true);
            runStartBroadCast(con.getLong(id+".interval"), id);
        }
    }

    @Override
    public void onDisable() {
        ThreadUtil.forceShutdown();
        AInterval.stopAllIntervals();
    }
}
