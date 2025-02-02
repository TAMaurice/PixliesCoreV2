package net.pixlies.core.commands.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import net.pixlies.core.Main;
import net.pixlies.core.handlers.impl.FreezeHandler;
import net.pixlies.core.localization.Lang;
import net.pixlies.core.ranks.Rank;
import net.pixlies.core.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand extends BaseCommand {

    private final FreezeHandler handler = Main.getInstance().getHandlerManager().getHandler(FreezeHandler.class);

    @CommandAlias("freeze|ss")
    @CommandPermission("pixlies.moderation.freeze")
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void onFreeze(CommandSender sender, @Single String string) {

        Player target = Bukkit.getPlayer(string);
        if (target == null) {
            throw new InvalidCommandArgument(MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", string);
        }

        if (handler.isFrozen(target.getUniqueId())) {
            handler.unfreeze(target);
            Lang.MODERATION_FREEZE_OFF.send(sender, "%PLAYER%;" + Rank.getRank(target.getUniqueId()).getColor() + target.getName());
            Lang.MODERATION_FREEZE_UNFREEZE.send(target);
        } else {
            if (target.hasPermission("pixlies.moderation.freeze.exempt")) {
                Lang.MODERATION_CANNOT_FREEZE.send(sender);
                return;
            }
            handler.freeze(target);
            Lang.MODERATION_FREEZE_ON.send(sender, "%PLAYER%;" + Rank.getRank(target.getUniqueId()).getColor() + target.getName());
            Lang.MODERATION_FREEZE_FREEZE.send(target, "%PLAYER%;" + RankUtils.getRankFromSender(sender).getColor() + sender.getName());
            Lang.MODERATION_FREEZE_MESSAGE.broadcastPermission("pixlies.moderation.silent",
                    "%PLAYER%;" + Rank.getRank(target.getUniqueId()).getColor() + target.getName(),
                    "%EXECUTOR%;" + RankUtils.getRankFromSender(sender).getColor() + sender.getName());
        }
    }

}
