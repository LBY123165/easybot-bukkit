package com.springwater.easybot.event;

import com.springwater.easybot.Easybot;
import com.springwater.easybot.bridge.packet.PlayerLoginResultPacket;
import com.springwater.easybot.utils.BridgeUtils;
import com.springwater.easybot.utils.GeyserUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            PlayerLoginResultPacket result = Easybot.getClient().login(
                    GeyserUtils.getName(event.getPlayer()),
                    GeyserUtils.getUuid(event.getPlayer()).toString()
            );
            if (result.getKick()) {
                event.setKickMessage(result.getKickMessage());
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            }
        } catch (Exception ex) {
            Easybot.instance.getLogger().severe("处理玩家登录事件遇到异常! " + ex);
            if (!Easybot.instance.getConfig().getBoolean("service.ignore_error")) {
                event.setKickMessage("§c服务器内部异常,请稍后重试!");
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            }
        }
    }

    @EventHandler
    public void reportPlayerOnLogin(PlayerLoginEvent event) {
        String ip = BridgeUtils.getPlayerIp(event.getPlayer());
        new Thread(() -> Easybot.getClient().reportPlayer(
                GeyserUtils.getName(event.getPlayer()),
                GeyserUtils.getUuid(event.getPlayer()).toString(),
                ip
        ), "EasyBot-Thread: ReportPlayerOnLogin " + event.getPlayer().getName()).start();
    }

    @EventHandler
    public void reportPlayer(PlayerJoinEvent event) {
        String ip = BridgeUtils.getPlayerIp(event.getPlayer());
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            Easybot.getClient().reportPlayer(
                    GeyserUtils.getName(event.getPlayer()),
                    GeyserUtils.getUuid(event.getPlayer()).toString(),
                    ip);
        }, "EasyBot-Thread: ReportPlayerOnJoin " + event.getPlayer().getName()).start();
    }
}
