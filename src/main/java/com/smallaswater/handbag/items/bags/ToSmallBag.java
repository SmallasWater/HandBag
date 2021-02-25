package com.smallaswater.handbag.items.bags;


import cn.nukkit.Player;
import cn.nukkit.item.Item;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.items.types.BagType;

/**
 * @author SmallasWater
 */
public class ToSmallBag extends BaseBag {
    public ToSmallBag(Player player,String name, Item item) {
        super(player,name, BagType.TO_SMALL, item);
    }
}
