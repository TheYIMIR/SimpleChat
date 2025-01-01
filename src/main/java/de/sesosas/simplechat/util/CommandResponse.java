package de.sesosas.simplechat.util;

import de.sesosas.simplechat.SimpleChat;

public class CommandResponse {
    public static String getFormat(String text){
        return SimpleChat.getPlugin().config.getString("Prefix.Commands") + " §f" + text;
    }

    public static String getBroadcastFormat(String text){
        return SimpleChat.getPlugin().config.getString("Prefix.Broadcast") + " §f" + text;
    }
}
