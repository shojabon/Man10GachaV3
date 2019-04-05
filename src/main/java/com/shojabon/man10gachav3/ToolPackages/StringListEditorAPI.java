package com.shojabon.man10gachav3.ToolPackages;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StringListEditorAPI {
    Listener listener = new Listener();
    JavaPlugin plugin;
    Player p;
    ArrayList<String> list;
    int currentSelect = 0;
    BiFunction<ArrayList<String>, Player, String> okFunction;
    Function<ArrayList<String>, String> cancelFunction;
    public StringListEditorAPI(Player p, ArrayList<String> list, BiFunction<ArrayList<String>, Player, String> okFunction, Function<ArrayList<String>, String> cancelFunction){
        p.closeInventory();
        this.p = p;
        this.okFunction = okFunction;
        this.cancelFunction = cancelFunction;
        this.list = list;
        if(list == null){
            this.list = new ArrayList<>();
        }
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        renderText(currentSelect);
    }

    private void renderText(int selecting) {
        for (int i = 0; i < 20; i++) {
            p.sendMessage(" ");
        }
        for (int i = 0; i < list.size(); i++) {
            String message = list.get(i);
            String asta = "   ";
            if (i == selecting){
                asta = " * ";
            }
            IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a("[\"\",{\"text\":\"\",\"bold\":true,\"color\":\"yellow\"}," +
                    "{\"text\":\"" + asta + "\",\"bold\":true,\"color\":\"light_purple\"}," +
                    "{\"text\":\"[X]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/delete " + i + "\"}," +
                    "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"メッセージを消去する\"}}," +
                    "{\"text\":\"[U]\",\"bold\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/up " + i + "\"}," +
                    "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"テキストをに上げる\"}}," +
                    "{\"text\":\"[D]\",\"bold\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/down " + i + "\"}," +
                    "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"テキストを下げる\"}}," +
                    "{\"text\":\"[E]\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/edit " + i + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"テキストを編集する\"}}," +
                    "{\"text\":\"" + message.substring(0, Math.min(message.replaceAll("§", "").length(), 10)) + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + message + "\"}}]");
            PacketPlayOutChat packet = new PacketPlayOutChat(component, ChatMessageType.CHAT);
            ((CraftPlayer) (p)).getHandle().playerConnection.sendPacket(packet);
        }
        ((CraftPlayer) (p)).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("[\"\",{\"text\":\"【決定】\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/accept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"クリックして決定\"}},{\"text\":\" \"},{\"text\":\"【キャンセル】\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cancel\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"クリックしてキャンセル\"}},{\"text\":\" \"},{\"text\":\"【新規項目】\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/new\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"クリックして新規項目作成\"}}]")));
    }

    private void close(){
        HandlerList.unregisterAll(listener);
    }

    class Listener implements org.bukkit.event.Listener
    {

        @EventHandler
        public void onCommand(PlayerCommandPreprocessEvent e){
            if(e.getPlayer() != p) return;
            String c = e.getMessage().substring(1);
            e.setCancelled(true);
            String[] args = c.split(" ");
            if(args[0].equalsIgnoreCase("edit")){
                currentSelect = Integer.parseInt(args[1]);
                renderText(Integer.parseInt(args[1]));
                return;
            }
            if(args[0].equalsIgnoreCase("down")){
                if(Integer.parseInt(args[1]) == list.size()-1)return;
                String sub = list.get(Integer.parseInt(args[1]) + 1);
                list.set(Integer.parseInt(args[1]) + 1, list.get(Integer.parseInt(args[1])));
                list.set(Integer.parseInt(args[1]), sub);
                renderText(currentSelect);
                return;
            }
            if(args[0].equalsIgnoreCase("up")){
                if(Integer.parseInt(args[1]) == 0)return;
                String sub = list.get(Integer.parseInt(args[1]) - 1);
                list.set(Integer.parseInt(args[1]) - 1, list.get(Integer.parseInt(args[1])));
                list.set(Integer.parseInt(args[1]), sub);
                renderText(currentSelect);
                return;
            }
            if(args[0].equalsIgnoreCase("delete")){
                list.remove(Integer.parseInt(args[1]));
                renderText(currentSelect);
                return;
            }
            if(args[0].equalsIgnoreCase("new")){
                list.add("");
                renderText(currentSelect);
                return;
            }
            if(args[0].equalsIgnoreCase("cancel")){
                String res = cancelFunction.apply(list);
                close();
                if(res != null){
                    for(int i = 0;i < 20;i++){
                        p.sendMessage(" ");
                    }
                }
                return;
            }
            if(args[0].equalsIgnoreCase("accept")){
                String res = okFunction.apply(list, e.getPlayer());
                close();
                if(res != null){
                    for(int i = 0;i < 20;i++){
                        p.sendMessage(" ");
                    }
                }
                return;
            }
            list.set(currentSelect, c.replaceAll("&", "§"));
            renderText(currentSelect);
        }


    }
}
