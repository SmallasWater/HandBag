package com.smallaswater.handbag.inventorys;


import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import com.smallaswater.handbag.inventorys.lib.DoubleChestFakeInventory;
import com.smallaswater.handbag.items.BaseBag;

/**
 * @author SmallasWater
 */
public class BigBagInventory extends DoubleChestFakeInventory implements BaseInventory {

    private Player player;

    public BigBagInventory(InventoryHolder holder) {
        super(holder);
        this.setName(((BaseBag) holder).getItem().getCustomName());
    }

    @Override
    public void onClose(Player who) {
        if (holder instanceof BaseBag) {
            ((BaseBag) holder).close();
        }

//        RemoveEntityPacket pk = new RemoveEntityPacket();
//        pk.eid = id;
//        who.dataPacket(pk);

        super.onClose(who);
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return this;
    }
}
