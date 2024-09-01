package com.springwater.easybot.event;

import com.springwater.easybot.Easybot;
import com.springwater.easybot.bridge.packet.PlayerLoginResultPacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        try{
            PlayerLoginResultPacket result = Easybot.getClient().login(event.getPlayer().getName(), event.getPlayer().getUniqueId().toString());
            if(result.getKick()){
                event.setKickMessage(result.getKickMessage());
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            }
        }catch (Exception ex){
            Easybot.instance.getLogger().severe("处理玩家登录事件遇到异常! " + ex);
            if(!Easybot.instance.getConfig().getBoolean("service.ignore_error")){
                event.setKickMessage("§c服务器内部异常,请稍后重试!");
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            }
        }
    }

    @EventHandler
    public void reportPlayer(PlayerLoginEvent event){
        new Thread(() -> Easybot.getClient().reportPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId().toString()), "EasyBot-Thread: ReportPlayer " + event.getPlayer().getName()).start();
    }
}
