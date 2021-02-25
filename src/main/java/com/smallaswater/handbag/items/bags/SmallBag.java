package com.smallaswater.handbag.items.bags;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.items.types.BagType;

/**
 * @author SmallasWater
 */
public class SmallBag extends BaseBag {

    public SmallBag(Player player,String name, Item item) {
        super(player,name,BagType.SMALL, item);
    }
}
