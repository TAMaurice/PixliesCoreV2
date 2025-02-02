package net.pixlies.business.util;

import net.pixlies.business.locale.MarketLang;
import net.pixlies.core.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Util {
    public static void sendRestrictMessage(Player player, Player target, String reason) {
        MarketLang.MARKET_PLAYER_RESTRICTED_SENDER.send(
                player,
                "%PLAYER%;" + Rank.getRank(target.getUniqueId()).getColor() + target.getName(),
                "%REASON%;" + reason
        );
        
        if (target.isOnline()) {
            MarketLang.MARKET_PLAYER_RESTRICTED_TARGET.send(
                    target,
                    "%PLAYER%;" + Rank.getRank(player.getUniqueId()).getColor() + player.getName(),
                    "%REASON%;" + reason
            );
            SoundUtil.grandError(target);
        }
    }
    
    public static void sendUnrestrictMessage(Player player, Player target, String reason) {
        MarketLang.MARKET_PLAYER_ALLOWED_SENDER.send(
                player,
                "%PLAYER%;" + Rank.getRank(target.getUniqueId()).getColor() + target.getName()
        );
        
        if (target.isOnline()) {
            MarketLang.MARKET_PLAYER_ALLOWED_TARGET.send(target);
            SoundUtil.grandSuccess(target);
        }
    }
    
    public static void openSign(Player player, List<String> lines) {
        /*
        Sign sign = null;
        for (int y = 255; y > 0; y--) {
            Location loc = new Location(player.getWorld(), player.getLocation().getX(), y, player.getLocation().getZ());
            if (player.getWorld().getBlockAt(loc).getType() != Material.AIR) continue;
            player.getWorld().getBlockAt(loc).setType(Material.BIRCH_WALL_SIGN);
            sign = (Sign) player.getWorld().getBlockAt(loc).getState();
        }

        Sign finalSign = sign;
        assert finalSign != null;
        lines.forEach(line -> finalSign.line(lines.indexOf(line), Component.text(line)));
        sign.update();

        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getDeclaredField("b").get(handle);

            Field tileField = finalSign.getClass().getDeclaredField("ar");
            tileField.setAccessible(true);

            Object tileSign = tileField.get(finalSign);
            Field editable = tileSign.getClass().getDeclaredField("g");
            editable.setAccessible(true);
            editable.set(tileSign, true);

            Field handler = tileSign.getClass().getDeclaredField("h");
            handler.setAccessible(true);
            handler.set(tileSign, handle);

            Object position = Objects.requireNonNull(getNMSClass("BlockPosition$PooledBlockPosition"))
                    .getMethod("d", double.class, double.class, double.class)
                    .invoke(null, finalSign.getX(), finalSign.getY(), finalSign.getZ());

            // PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor((BlockPosition) position);

            connection.getClass().getDeclaredMethod("a", getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
         */
    }
    
    public static Class<?> getNMSClass(String clazz) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
