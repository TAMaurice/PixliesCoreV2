package net.pixlies.nations.nations.interfaces;

import com.mongodb.client.model.Filters;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.pixlies.core.entity.user.User;
import net.pixlies.nations.Nations;
import net.pixlies.nations.locale.NationsLang;
import net.pixlies.nations.nations.Nation;
import net.pixlies.nations.nations.chunk.NationChunk;
import net.pixlies.nations.nations.chunk.NationChunkType;
import net.pixlies.nations.nations.interfaces.profile.ChatType;
import net.pixlies.nations.nations.ranks.NationRank;
import net.pixlies.nations.nations.relations.Relation;
import net.pixlies.nations.utils.NationTextUtils;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A Morphia-serializable Object to store all important information about the players
 * Nation information.
 *
 * @author MickMMars
 * @author Dynmie
 */
@Data
public class NationProfile {

    private static final Nations instance = Nations.getInstance();

    // -------------------------------------------------------------------------------------------------
    //                                              DATA
    // -------------------------------------------------------------------------------------------------

    // Player
    private boolean loaded = false;
    private final String uuid;
    private long lastLogin = System.currentTimeMillis();
    private boolean hasJoinedBefore = false;

    // Nations
    private @Nullable String nationId;
    private @Nullable String nationRank;
    private @Getter(AccessLevel.NONE) String profileChatType = ChatType.GLOBAL.name();

    // Economy
    private @Getter @Setter double balance = 0.00;
    
    // Local
    private @Getter(AccessLevel.NONE) boolean autoClaim = false;

    // -------------------------------------------------------------------------------------------------
    //                                          CONSTRUCTOR
    // -------------------------------------------------------------------------------------------------

    public NationProfile(UUID uniqueId) {
        this.uuid = uniqueId.toString();
    }

    // -------------------------------------------------------------------------------------------------
    //                                            METHODS
    // -------------------------------------------------------------------------------------------------

    public boolean attemptClaim(Player player, Nation nation) {
        if (isInNation() && getNation().getNationId().equals(nation.getNationId())) {
            if (NationChunk.getClaimAt(player.getWorld().getName(), player.getChunk().getX(), player.getChunk().getZ()) != null) {
                return false;
            }

            NationChunk chunk = new NationChunk(
                    nation.getNationId(),
                    player.getWorld().getName(),
                    player.getChunk().getX(),
                    player.getChunk().getZ()
            );
            chunk.claim(true);
            nation.save();

            NationsLang.NATION_CLAIM_MESSAGE.send(player,
                    "%LOCATION%;" + NationTextUtils.getChunkLocationFormatted(chunk.getX(), chunk.getZ()),
                    "%NATION%;" + nation.getName()
            );

            nation.getOnlineMembersAsPlayer().forEach((p) -> {
                if (player.equals(p)) return;
                NationsLang.NATION_CLAIM_MESSAGE.send(p,
                        "%PLAYER%;" + player.getName(),
                        "%LOCATION%;" + NationTextUtils.getChunkLocationFormatted(chunk.getX(), chunk.getZ())
                );
            });
            return true;
        }

        User user = User.get(player.getUniqueId());
        boolean staffCondition = user.isBypassing() && player.hasPermission("nations.staff.forceclaim");


        // :: /nation claim one <NATION>
        if (!staffCondition) {
            NationsLang.NATION_NO_PERMISSION.send(player);
            return false;
        }

        if (nation == null) {
            NationsLang.NATION_DOES_NOT_EXIST.send(player);
            return false;
        }

        if (NationChunk.getClaimAt(player.getWorld().getName(), player.getChunk().getX(), player.getChunk().getZ()) != null) {
            NationsLang.NATION_CLAIM_ALREADY_CLAIMED.send(player);
            return false;
        }

        NationChunk chunk = new NationChunk(
                nation.getNationId(),
                player.getWorld().getName(),
                player.getChunk().getX(),
                player.getChunk().getZ(),
                NationChunkType.NORMAL,
                new ArrayList<>()
        );
        chunk.claim(true);
        nation.save();

        NationsLang.NATION_CLAIM_MESSAGE.send(player,
                "%LOCATION%;" + NationTextUtils.getChunkLocationFormatted(chunk.getX(), chunk.getZ()),
                "%NATION%;" + nation.getName()
        );

        nation.getOnlineMembersAsPlayer().forEach((p) -> {
            if (player.equals(p)) return;
            NationsLang.NATION_CLAIM_MESSAGE.send(p,
                    "%PLAYER%;" + player.getName(),
                    "%LOCATION%;" + NationTextUtils.getChunkLocationFormatted(chunk.getX(), chunk.getZ())
            );
        });

        return true;
    }

    public boolean isNationLeader() {
        Nation nation = getNation();
        if (nation == null) return false;
        return nation.getLeaderUUID().toString().equals(uuid);
    }

    public @Nullable NationRank getRank() {
        Nation nation = getNation();
        if (nation == null) return null;

        return getNation().getRanks().getOrDefault(nationRank, NationRank.getNewbieRank());
    }

    /**
     * Get the nation chat type
     *
     * @return nation chat type
     */
    public ChatType getChatType() {
        try {
            return ChatType.valueOf(profileChatType);
        } catch (IllegalArgumentException e) {
            return ChatType.GLOBAL;
        }
    }

    public void setChatType(ChatType chatType) {
        this.profileChatType = chatType.name();
    }

    public void setNation(Nation nation) {
        nationId = nation.getNationId();
    }

    public Nation getNation() {
        return Nation.getFromId(nationId);
    }

    public boolean isAutoClaiming() {
        if (!isInNation()) {
            if (autoClaim) {
                autoClaim = false;
            }
            return false;
        }
        return autoClaim;
    }

    public void setAutoClaiming(boolean value) {
        if (!isInNation()) {
            autoClaim = false;
            return;
        }
        autoClaim = value;
    }

    public Relation getRelationTo(@NotNull Nation toMatch) {
        Nation nation = getNation();
        if (nation == null) return Relation.OTHER;

        return nation.getRelationTo(toMatch);
    }

    public Relation getRelationTo(@NotNull NationProfile toMatchProfile) {
        Nation nation = getNation();
        if (nation == null) return Relation.OTHER;

        Nation toMatch = toMatchProfile.getNation();
        if (toMatch == null) return Relation.OTHER;

        return nation.getRelationTo(toMatch);
    }

    /**
     * Check if the profile is in a nation.
     *
     * @return True if the profile is indeed in a nation.
     */
    public boolean isInNation() {
        return getNation() != null;
    }

    /**
     * Removes the nation information from a user.
     */
    public void leaveNation(boolean saveNation) {

        if (!isInNation()) return;
        Nation nation = getNation();
        if (nation == null) return;

        nationId = null;
        nationRank = null;
        profileChatType = ChatType.GLOBAL.name();

        nation.getMembers().remove(uuid);

        if (saveNation) {
            nation.save();
        }

    }

    public UUID getUniqueId() {
        return UUID.fromString(uuid);
    }

    /**
     * Async backup
     */
    public void save() {
        instance.getServer().getScheduler().runTaskAsynchronously(instance, this::backup);
    }

    /**
     * Non sync backup
     */
    public void backup() {
        if (instance.getMongoManager().getNationProfileCollection().find(Filters.eq("uuid", uuid)).first() == null) {
            instance.getMongoManager().getNationProfileCollection().insertOne(toDocument());
        }
        instance.getMongoManager().getNationProfileCollection().replaceOne(Filters.eq("uuid", uuid), toDocument());
    }

    public Document toDocument() {
        Document document = new Document();

        document.put("uuid", uuid);
        document.put("lastLogin", lastLogin);

        document.put("nationId", nationId);
        document.put("nationRank", nationRank);
        document.put("profileChatType", profileChatType);

        document.put("balance", balance);
        return document;
    }

    public void loadFromDocument(Document document) {
        lastLogin = document.getLong("lastLogin") == null ? lastLogin : document.getLong("lastLogin");

        nationId = document.getString("nationId") == null ? nationId : document.getString("nationId");
        nationRank = document.getString("nationRank") == null ? nationRank : document.getString("nationRank");
        profileChatType = document.getString("profileChatType") == null ? profileChatType : document.getString("profileChatType");

        balance = document.get("balance", 0.00);
    }

    public void load(boolean cache) {
        Document document = instance.getMongoManager().getNationProfileCollection().find(Filters.eq("uuid", uuid)).first();
        if (document == null) {
            backup();
            loaded = true;
            if (cache) {
                instance.getMongoManager().getProfileCache().put(getUniqueId(), this);
            }
            return;
        }
        loadFromDocument(document);
        hasJoinedBefore = true;
        loaded = true;

        if (cache) {
            instance.getMongoManager().getProfileCache().put(getUniqueId(), this);
        }
    }

    // -------------------------------------------------------------------------------------------------
    //                                          STATIC METHODS
    // -------------------------------------------------------------------------------------------------

    public static @NotNull NationProfile get(UUID uuid) {
        if (!instance.getMongoManager().getProfileCache().containsKey(uuid)) {
            instance.getMongoManager().getProfileCache().put(uuid, new NationProfile(uuid));
        }
        return instance.getMongoManager().getProfileCache().get(uuid);
    }

    public static NationProfile getLoadDoNotCache(UUID uuid) {
        if (instance.getMongoManager().getProfileCache().containsKey(uuid)) {
            NationProfile profile = instance.getMongoManager().getProfileCache().get(uuid);
            if (!profile.isLoaded()) {
                profile.load(false);
            }
        }
        NationProfile profile = new NationProfile(uuid);
        profile.load(false);
        return profile;
    }

    public static void loadAllOnlineUsers() {
        for (Player player : instance.getServer().getOnlinePlayers()) {
            NationProfile profile = NationProfile.get(player.getUniqueId());
            if (!profile.isLoaded() && player.isOnline()) {
                profile.load(true);
            }
        }
    }

}
