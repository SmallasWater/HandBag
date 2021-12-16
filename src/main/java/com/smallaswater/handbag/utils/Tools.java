package com.smallaswater.handbag.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import com.smallaswater.handbag.items.BaseBag;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SmallasWater
 * Create on 2021/2/19 10:03
 * Package com.smallaswater.handbag.utils
 */
public class Tools {

    public static LinkedHashMap<Integer, Item> getHandBagByInventory(Player player){
        LinkedHashMap<Integer,Item> items = new LinkedHashMap<>();
        for(Map.Entry<Integer,Item> item: player.getInventory().getContents().entrySet()){
            if(isHandBag(item.getValue())){
                items.put(item.getKey(),item.getValue());
            }
        }
        return items;
    }

    public static String getIntegerOrMaxColor(int i,int max){
        double i1 = (double) i;
        double i2 = (double) max;
        double i3 = i1 / i2;
        if(i3 < 0.25){
            return "&7";
        }else if(i3 >= 0.25 && i3 <= 0.5){
            return "&2";
        }else if(i3 > 0.5 && i3 <=0.75){
            return "&6";
        }else{
            return "&4";
        }
    }



    public static boolean isHandBag(Item item){
        if(item == null){
            return false;
        }
        if(item.getNamedTag() != null){
            return item.getNamedTag().contains(BaseBag.NAME_TAG);
        }
        return false;
    }
}
