package com.smallaswater.handbag.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.utils.Tools;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author SmallasWater
 * Create on 2021/2/19 10:00
 * Package com.smallaswater.handbag.froms
 */
public class CreateWindow {

    static final int MENU_ID = 0x541a001;

    static final int CLICK_MENU_ID = 0x541a002;

    static final int MOVE_MENU_ID = 0x541a003;

    //dev_glyph_color 移动物品图标
    //icon_expand 打开箱子

    /**
     * 发送主页菜单
     * */
    public static void sendMenu(Player player){
        FormWindowSimple simple = new FormWindowSimple("仓库","");
        //inventory_icon
        BaseBag bag;
        for(Map.Entry<Integer, Item> itemEntry : Tools.getHandBagByInventory(player).entrySet()){
            bag = BaseBag.getBaseBagByItem(player,itemEntry.getValue().getNamedTag().getString("configName"),itemEntry.getValue());
            simple.addButton(new ElementButton(TextFormat.colorize('&',"&7<#&2"+itemEntry.getKey()+"&7> &r"+itemEntry.getValue().getCustomName()+"\n &7["+Tools.getIntegerOrMaxColor(bag.getInventory().slots.size(),bag.getType().getSize()) +bag.getInventory().slots.size()+"&7/&a"+bag.getType().getSize()+"&7]")
            ,new ElementButtonImageData("path","textures/ui/inventory_icon")));
        }
        if(simple.getButtons().size() == 0){
            simple.setContent("你还没有便携仓库哦~");
        }
        player.showFormWindow(simple,MENU_ID);
    }

    public static void onClickBagMenu(Player player){
        if(!WindowsListener.clickItem.containsKey(player)){
            return;
        }
        Map.Entry<Integer,Item> clickItem = WindowsListener.clickItem.get(player);
        FormWindowSimple simple = new FormWindowSimple("仓库 --- "+clickItem.getValue().getCustomName(),"");
        simple.addButton(new ElementButton("开启背包",new ElementButtonImageData("path",
                "textures/ui/icon_expand")));
        simple.addButton(new ElementButton("移动位置",new ElementButtonImageData("path",
                "textures/ui/dev_glyph_color")));
        simple.addButton(new ElementButton("重命名",new ElementButtonImageData("path",
                "textures/ui/book_metatag_default")));
        simple.addButton(new ElementButton("返回",new ElementButtonImageData("path",
                "textures/ui/refresh_light")));
        player.showFormWindow(simple,CLICK_MENU_ID);
    }

    public static void onMoveHandBag(Player player){
        if(!WindowsListener.clickItem.containsKey(player)){
            return;
        }
        Map.Entry<Integer,Item> clickItem = WindowsListener.clickItem.get(player);
        FormWindowCustom custom = new FormWindowCustom("仓库 --- "+clickItem.getValue().getCustomName()+" --- 移动");
        ArrayList<String> messages = new ArrayList<>();
        int can = 0;
        for(int i = 0;i  < player.getInventory().getSize();i++){
            Item item = player.getInventory().getItem(i);
            if(item.getId() == 0){
                can = i;
                messages.add(TextFormat.colorize('&',"&7<&2"+i+"&7> &e可放置"));
            }else{
                messages.add(TextFormat.colorize('&',"&7<&2"+i+"&7> &c不可放置"));
            }
        }
        custom.addElement(new ElementDropdown("请选择你要放置的位置 【请放置在空位】",messages,can));
        player.showFormWindow(custom,MOVE_MENU_ID);
    }


}
