package com.smallaswater.handbag.forms;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.handbag.HandBag;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.utils.Tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SmallasWater
 * Create on 2021/2/19 10:03
 * Package com.smallaswater.handbag.forms
 */
public class WindowsListener implements Listener {


    static LinkedHashMap<Player, Map.Entry<Integer, Item>> clickItem = new LinkedHashMap<>();

    @EventHandler
    public void onWindowEvent(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (event.wasClosed()) {
            return;
        }
        switch (event.getFormID()) {
            case CreateWindow.MENU_ID:
                if (event.getWindow() instanceof FormWindowSimple) {
                    int res = ((FormWindowSimple) event.getWindow()).getResponse().getClickedButtonId();
                    int i = 0;
                    for (Map.Entry<Integer, Item> itemEntry : Tools.getHandBagByInventory(player).entrySet()) {
                        if (i == res) {
                            clickItem.put(player, itemEntry);
                            break;
                        }
                        i++;
                    }
                    CreateWindow.onClickBagMenu(player);
                }
                break;
            case CreateWindow.CLICK_MENU_ID:
                if (event.getWindow() instanceof FormWindowSimple) {
                    switch (((FormWindowSimple) event.getWindow()).getResponse().getClickedButtonId()) {
                        case 0:
                            HandBag.getBag().openHandBag(player, clickItem.get(player).getKey(), clickItem.get(player).getValue());
                            break;
                        case 1:
                            CreateWindow.onMoveHandBag(player);
                            break;
                        case 2:
                            HandBag.getBag().slot.put(player.getName(), clickItem.get(player).getKey());
                            FormWindowCustom simple = new FormWindowCustom("重命名");
                            simple.addElement(new ElementInput("请输入新的名称 [&是颜色符号~]"));
                            player.showFormWindow(simple, 0x25565);
                            break;
                        case 3:
                            BaseBag baseBag = BaseBag.getBaseBagByItem(player, clickItem.get(player).getValue().getNamedTag().getString("configName"), clickItem.get(player).getValue());
                            baseBag.setAutoPickUp(!baseBag.isAutoPickUp());
                            player.getInventory().setItem(clickItem.get(player).getKey(), baseBag.toItem());
                            player.sendMessage(TextFormat.colorize('&', "&e[手提袋] &a自动拾取: " + (baseBag.isAutoPickUp() ? "&a开启" : "&c关闭")));

                            break;
                        default:
                            CreateWindow.sendMenu(player);
                            break;
                    }
                }
                break;
            case 0x25565:
                if (event.getWindow() instanceof FormWindowCustom) {
                    FormWindowCustom custom = ((FormWindowCustom) event.getWindow());
                    String input = custom.getResponse().getInputResponse(0);

                    Item item = player.getInventory().getItem(HandBag.getBag().slot.get(player.getName()));
                    if (item.getNamedTag() != null) {
                        if (item.getNamedTag().contains(BaseBag.NAME_TAG)) {
                            int slot = HandBag.getBag().slot.get(player.getName());
                            if ("".equalsIgnoreCase(input)) {
                                input = "未命名手提袋";
                            }
                            item.setCustomName(input.replace("&", "§"));
                            player.getInventory().setItem(slot, item);
                            player.sendMessage("§6[§7手提袋§6] §2 成功重命名为: §r" + input.replace("&", "§"));
                        }
                    }
                }
                break;
            case CreateWindow.MOVE_MENU_ID:
                if (event.getWindow() instanceof FormWindowCustom) {
                    if (!WindowsListener.clickItem.containsKey(player)) {
                        return;
                    }
                    Map.Entry<Integer, Item> clickItem = WindowsListener.clickItem.get(player);
                    FormResponseData data = ((FormWindowCustom) event.getWindow()).getResponse().getDropdownResponse(0);
                    int id = data.getElementID();
                    Item item = player.getInventory().getItem(id);
                    if (item.getId() == 0) {
                        player.getInventory().setItem(clickItem.getKey(), new ItemBlock(Block.get(0), 0, 0));
                        player.getInventory().setItem(id, clickItem.getValue());
                        player.sendMessage(TextFormat.colorize('&', "&e[手提袋]&a成功移动"));
                    } else {
                        player.sendMessage(TextFormat.colorize('&', "&e[手提袋]&c这里不能放置手提袋!"));
                    }
                }
                break;
            default:
                break;
        }

    }
}
