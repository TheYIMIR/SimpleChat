package de.sesosas.simplechat.util;

import de.sesosas.simplechat.SimpleChat;
import de.sesosas.simplechat.api.utils.StringUtil;
import org.bukkit.entity.Player;

public class ChatLayout {
    public static String getChatLayout(Player player, String msg, boolean staff){
        String layout;
        if(staff){
            layout = CurrentConfig.getString("Prefix.Staff") + " " + CurrentConfig.getString("Chat.Staff.Layout");
        }
        else {
            layout = SimpleChat.getPlugin().config.getString("Chat.Normal.Layout");
        }
        return StringUtil.Convert(layout + CurrentConfig.getString("Chat.Seperator") + " " + msg, player);
    }
}
