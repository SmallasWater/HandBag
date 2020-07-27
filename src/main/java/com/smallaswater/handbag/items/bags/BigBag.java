package com.smallaswater.handbag.items.bags;


import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import com.smallaswater.handbag.inventorys.BigBagInventory;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.items.types.BagType;


/**
 * @author SmallasWater
 */
public class BigBag extends BaseBag {

    public BigBag(String name, Item item) {
        super(name,BagType.BIG, item);
        this.inventory = new BigBagInventory(this);
        if(item.getNamedTag().contains(BaseBag.NAME_TAG)) {
            this.inventory.getInventory().setContents(
                    toSlotByItem(getItem()
                            .getNamedTag().getCompound(BaseBag.NAME_TAG))
            );
        }
    }




}
