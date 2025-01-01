package de.sesosas.simplechat.api.utils;

import de.sesosas.simplechat.SimpleChat;
import de.sesosas.simplechat.util.CurrentConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class StringUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9A-Fa-f])");

    private static String ph(String text) {
        return "[" + text + "]";
    }

    public static String Convert(String text, Player player) {
        if (text == null) return null;

        String result = text;

        result = convertBannedWords(result);

        if(result.startsWith("scs.format.")){
            result = ensureCapitalization(result);
            result = result.replace("scs.format.", "");
        }

        result = result.replace(ph("player_name"), player.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        result = result.replace(ph("player_health"), df.format(Objects.requireNonNull(player).getHealth()));
        result = result.replace(ph("player_food"), df.format(player.getFoodLevel()));
        result = result.replace(ph("player_xp"), df.format(player.getExp()));
        result = result.replace(ph("player_lvl"), Integer.toString(player.getLevel()));
        result = result.replace(ph("player_gamemode"), player.getGameMode().toString());

        result = hex(result);

        return result;
    }

    public static String hex(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }

        return customTranslateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static String ensureCapitalization(String text) {
        Pattern capitalizePattern = Pattern.compile("\\[#(?i)(cap)\\((\\w+)\\)]|\\[#(?i)(capall)\\((\\w+)\\)]");
        Matcher matcher = capitalizePattern.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String capitalized = null;

            if (matcher.group(1) != null) {
                String word = matcher.group(2);
                capitalized = word.substring(0, 1).toUpperCase() + word.substring(1);
            } else if (matcher.group(3) != null) {
                String word = matcher.group(4);
                capitalized = word.toUpperCase();
            }

            matcher.appendReplacement(buffer, capitalized);
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }


    public static String customTranslateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String convertBannedWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] words = text.split(" ");
        StringBuilder censoredText = new StringBuilder();

        for (String word : words) {
            if (containsBannedWord(word)) {
                censoredText.append(censorWord(word));
            } else {
                censoredText.append(word);
            }
            censoredText.append(" ");
        }

        return censoredText.toString().trim();
    }

    public static boolean containsBannedWord(String text){
        return Objects.requireNonNull(SimpleChat.getPlugin().config.getList("Chat.BannedWords")).contains(text);
    }

    public static String censorWord(String word) {
        if (word.length() <= 2) {
            StringBuilder fullyCensored = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                fullyCensored.append("*");
            }
            return fullyCensored.toString();
        }

        char firstChar = word.charAt(0);
        char lastChar = word.charAt(word.length() - 1);
        StringBuilder censoredMiddle = new StringBuilder();
        for (int i = 0; i < word.length() - 2; i++) {
            censoredMiddle.append("*");
        }

        return firstChar + censoredMiddle.toString() + lastChar;
    }
}
