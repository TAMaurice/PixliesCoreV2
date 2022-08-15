package net.pixlies.nations.locale;

import lombok.Getter;
import net.pixlies.core.entity.user.User;
import net.pixlies.core.utils.CC;
import net.pixlies.nations.Nations;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum NationsLang {

    // NATIONS
    NATION_NAME_INVALID(NationsLang.NATION),
    NATION_FORMED(NationsLang.NATION),
    ALREADY_IN_NATION(NationsLang.NATION),
    NATION_NAME_ALREADY_EXISTS(NationsLang.NATION),
    NATION_ID_ALREADY_EXISTS(NationsLang.NATION),
    NATION_NO_PERMISSION(NationsLang.NATION),
    NATION_DISBANDED(NationsLang.NATION),
    NATION_DISBAND_CONFIRM(NationsLang.NATION),
    NATION_DISBAND_CANCELLED(NationsLang.NATION),
    NATION_NO_NATION_TO_DISBAND(NationsLang.NATION),
    NATION_MISSING_ARG(NationsLang.NATION),
    NATION_DOES_NOT_EXIST(NationsLang.NATION),
    NATION_RENAME(NationsLang.NATION),
    NOT_IN_NATION(NationsLang.NATION),

    NATION_WILDERNESS_TITLE(NationsLang.NATION),
    NATION_WILDERNESS_SUBTITLE(NationsLang.NATION),
    NATION_SPAWN_TITLE(NationsLang.NATION),
    NATION_SPAWN_SUBTITLE(NationsLang.NATION),
    NATION_WARP_TITLE(NationsLang.NATION),
    NATION_WARP_SUBTITLE(NationsLang.NATION),
    NATION_WARZONE_TITLE(NationsLang.NATION),
    NATION_WARZONE_SUBTITLE(NationsLang.NATION),

    NATION_SAME_TITLE_COLOUR(NationsLang.NATION),
    NATION_ALLY_TITLE_COLOUR(NationsLang.NATION),
    NATION_OTHER_TITLE_COLOUR(NationsLang.NATION),
    NATION_SUBTITLE_COLOUR(NationsLang.NATION),

    NATION_CLAIM_MESSAGE(NationsLang.NATION),
    NATION_CLAIM_MESSAGE_ANNOUNCE(NationsLang.NATION),
    NATION_CLAIM_ALREADY_CLAIMED(NationsLang.NATION),
    NATION_CLAIM_AUTO_ENABLED(NationsLang.NATION),
    NATION_CLAIM_AUTO_DISABLED(NationsLang.NATION),

    CHUNK_LOCATION_FORMAT("");

    public static final String NATION = "§x§2§F§D§1§E§5§lN§x§3§4§C§6§D§8§lA§x§3§7§B§A§C§A§lT§x§3§5§B§0§B§F§lI§x§3§3§A§3§B§0§lO§x§3§2§9§9§A§5§lN§8 | §7";

    private final String prefix;
    private @Getter @NotNull Map<String, String> languages = new HashMap<>();

    NationsLang(String prefix) {
        this.prefix = CC.format(prefix);
    }

    public String get(CommandSender sender) {
        if (sender instanceof Player player) {
            User user = User.get(player.getUniqueId());
            String lang = user.getLang();
            if (languages.containsKey(lang)) {
                return prefix + CC.format(languages.get(user.getLang()));
            } else {
                return get();
            }
        }
        return get();
    }

    public String get(CommandSender sender, String... placeholders) {
        String text = get(sender);
        for (String s : placeholders) {
            String[] pSplit = s.split(";");
            text = text.replace(pSplit[0], pSplit[1]);
        }
        return text;
    }

    public String get() {
        return prefix + CC.format(languages.get("ENG"));
    }

    public String getRaw(String language) {
        if (languages.get(language) == null) return "";
        return languages.get(language);
    }

    public void send(CommandSender sender) {
        sender.sendMessage(get(sender));
    }

    public void broadcast(String... placeholders) {
        broadcastPermission(null, placeholders);
    }

    public void broadcastPermission(String permission, String... placeholders) {
        if (permission == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                send(player, placeholders);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(permission)) {
                    send(player, placeholders);
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage(get(null, placeholders));
    }

    public void kickPlayer(Player player) {
        player.kickPlayer(get(player));
    }

    public void kickPlayer(Player player, String... placeholders) {
        player.kickPlayer(get(player, placeholders));
    }

    public void setLanguage(Map<String, String> languages) {
        this.languages = languages;
    }

    /**
     * SEND MESSAGE TO PLAYER WITH PLACEHOLDERS
     * @param sender the command sender to send to
     * @param placeholders get declared by writing TOREPLACE;REPLACED
     */
    public void send(CommandSender sender, String... placeholders) {
        sender.sendMessage(get(sender, placeholders));
    }

    public static void load() {
        int loaded = 0;
        File folder = new File(Nations.getInstance().getDataFolder().getAbsolutePath() + "/languages/");

        File[] files = folder.listFiles();
        if (files == null) {
            Nations.getInstance().getLogger().warning("Failed to load any languages.");
            return;
        }

        for (File file : files) {
            if (!file.getName().endsWith(".yml"))
                continue;
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            String langName = file.getName().replace("LANG_", "").replace(".yml", "").toUpperCase();
            for (NationsLang l : values()) {
                Map<String, String> map = new HashMap<>(l.languages);
                map.put(langName, cfg.getString(l.name()));
                l.setLanguage(map);
            }
            loaded++;
        }
        Nations.getInstance().getLogger().info( "Loaded " + loaded + " languages.");
    }

}