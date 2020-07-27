package com.smallaswater.handbag.items.bags;


import cn.nukkit.item.Item;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.items.types.BagType;

/**
 * @author SmallasWater
 */
public class ToSmallBag extends BaseBag {
    public ToSmallBag(String name, Item item) {
        super(name, BagType.TO_SMALL, item);
    }
}
