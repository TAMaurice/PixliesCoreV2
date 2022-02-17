package net.pixlies.nations.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.pixlies.core.entity.User;
import net.pixlies.core.localization.Lang;
import net.pixlies.nations.Nations;
import net.pixlies.nations.interfaces.NationProfile;
import net.pixlies.nations.nations.Nation;
import net.pixlies.nations.nations.customization.GovernmentType;
import net.pixlies.nations.nations.customization.Ideology;
import net.pixlies.nations.nations.customization.NationConstitution;
import net.pixlies.nations.nations.customization.Religion;
import net.pixlies.nations.nations.ranks.NationRank;
import net.pixlies.nations.utils.NationUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CommandAlias("nation|nations|n|faction|factions|country|countries")
public class NationCommand extends BaseCommand {

    private static final Nations instance = Nations.getInstance();

    @Default
    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Description("Create a nation")
    public void onCreate(Player player, String name) {
        User user = User.get(player.getUniqueId());

        // CHECKS IF USER IS IN NATION ALREADY
        if (NationProfile.isInNation(user)) {
            Lang.ALREADY_IN_NATION.send(player);
            return;
        }

        // CHECKS IF NATION WITH SAME NAME ALREADY EXISTS
        if (Nation.getNationNames().contains(name)) {
            Lang.NATION_NAME_ALREADY_EXISTS.send(player, "%NAME%;" + name);
            return;
        }

        String id = RandomStringUtils.randomAlphanumeric(7);

        // CHECKS IF NATION WITH SAME ID ALREADY EXISTS
        if (instance.getNationManager().getNations().containsKey(id)) {
            Lang.NATION_ID_ALREADY_EXISTS.send(player, "%NAME%;" + name);
            return;
        }

        // CHECKS IF NATION NAME IS ALPHANUMERIC
        if (!NationUtils.nameValid(name)) {
            Lang.NATION_NAME_INVALID.send(player);
            return;
        }

        final List<Integer> ncValues = new ArrayList<>();
        for (NationConstitution nc : NationConstitution.values()) {
            ncValues.add(nc.getDefaultValue());
        }

        Nation nation = new Nation(
                id,
                name,
                NationUtils.randomDesc(),
                player.getUniqueId(),
                System.currentTimeMillis(),
                0.0,
                0.0,
                GovernmentType.UNITARY,
                Ideology.TRIBAL,
                Religion.SECULAR,
                ncValues,
                new HashMap<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        nation.create();
        Lang.NATION_FORMED.broadcast("%NATION%;" + nation.getName(), "%PLAYER%;" + player.getName());

        nation.addMember(user, NationRank.leader().getName());

        // TODO: open nation creation menu
    }

    @Subcommand("disband")
    @Description("Disband a nation")
    public void onDisband(Player player, @Optional String name) {
        User user = User.get(player.getUniqueId());

        // STAFFMODE FORCE DISBAND
        if (user.getSettings().isInStaffMode() && player.hasPermission("nations.staff.forcedisband")) {
            // TODO: force disband a nation
        }

        // CHECK IF PLAYER IS THE LEADER OF THEIR NATION
        NationProfile profile = (NationProfile) user.getExtras().get("nationsProfile");
        if (profile.getNationRank().equals(NationRank.leader().getName())) {
            // TODO: nation disbanding sequence
            // TODO: add confirmation
        } else {
            Lang.NATION_NO_PERMISSION.send(player);
        }
    }

}
