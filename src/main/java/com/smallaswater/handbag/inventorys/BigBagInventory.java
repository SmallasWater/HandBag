package com.smallaswater.handbag.inventorys;


import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import com.smallaswater.handbag.inventorys.lib.DoubleChestFakeInventory;
import com.smallaswater.handbag.items.BaseBag;

/**
 * @author SmallasWater
 */
public class BigBagInventory extends DoubleChestFakeInventory implements BaseInventory{

    public long id;

    public BigBagInventory(InventoryHolder holder) {
        super(holder);
        this.setName(((BaseBag) holder).getItem().getCustomName());
    }



    @Override
    public Inventory getInventory() {
        return this;
    }
}
