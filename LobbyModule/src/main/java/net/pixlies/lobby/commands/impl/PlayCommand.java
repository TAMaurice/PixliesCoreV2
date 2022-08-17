package net.pixlies.lobby.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.pixlies.core.localization.Lang;
import net.pixlies.core.utils.CC;
import net.pixlies.lobby.Lobby;
import net.pixlies.lobby.managers.QueueManager;
import org.bukkit.entity.Player;

public class PlayCommand extends BaseCommand {

    private static final QueueManager manager = Lobby.getInstance().getQueueManager();

    @CommandAlias("play|join|queue|joinserver")
    @CommandCompletion("@empty")
    @Syntax("<server>")
    public void onPlay(Player player, @Single String server) {

        manager.addPlayerToQueue(player, server);

    }

}


