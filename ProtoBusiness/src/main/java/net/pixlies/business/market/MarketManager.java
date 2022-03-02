package net.pixlies.business.market;

import lombok.Getter;
import net.pixlies.business.ProtoBusiness;
import net.pixlies.core.entity.User;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the saving of orders.
 *
 * @author vPrototype_
 */
public class MarketManager {

    private static final ProtoBusiness instance = ProtoBusiness.getInstance();

    @Getter private final Map<String, OrderBook> books = new HashMap<>(); // ID, OrderBook

    public MarketManager() {
        loadAll();
    }

    public void backupAll() {
        for (OrderBook book : books.values()) {
            book.backup();
        }
    }

    public void loadAll() {
        for (OrderBook book : instance.getMongoManager().getDatastore().find(OrderBook.class).iterator().toList()) {
            if (book.getBookId() != null) {
                books.put(book.getBookId(), book);
            }
        }
    }

    public void resetBooks() {
        // Reset stats.yml
        instance.getStats().set("market.buyOrders", 0);
        instance.getStats().set("market.sellOrders", 0);
        instance.getStats().set("market.moneySpent", 0);
        instance.getStats().set("market.moneyGained", 0);

        // Clear all orders
        for (OrderBook book : books.values()) {
            book.getBuyOrders().clear();
            book.getSellOrders().clear();
            book.getQueue().clear();
            book.save();
        }
    }

    public void resetPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        User user = User.get(uuid);

        // Reset player stats
        user.getStats().setBuyOrdersMade(0);
        user.getStats().setSellOrdersMade(0);
        user.getStats().setMoneySpent(0);
        user.getStats().setMoneyGained(0);

        // Clear orders of that player
        for (OrderBook book : books.values()) {
            book.getQueue().removeIf(order -> order.getPlayerUUID() == uuid);
            book.getBuyOrders().removeIf(order -> order.getPlayerUUID() == uuid);
            book.getSellOrders().removeIf(order -> order.getPlayerUUID() == uuid);
            book.save();
        }

        // TODO reset money
    }

}