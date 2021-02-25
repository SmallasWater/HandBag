package com.smallaswater.handbag.items.bags;


import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import com.smallaswater.handbag.inventorys.BigBagInventory;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.items.types.BagType;


/**
 * @author SmallasWater
 */
public class BigBag extends BaseBag {

    public BigBag(Player player,String name, Item item) {
        super(player,name,BagType.BIG, item);
    }





}
