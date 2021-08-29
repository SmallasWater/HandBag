package com.smallaswater.handbag.inventorys;


import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import com.smallaswater.handbag.HandBag;
import com.smallaswater.handbag.inventorys.lib.DoubleChestFakeInventory;
import com.smallaswater.handbag.items.BaseBag;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author SmallasWater
 */
public class BigBagInventory extends DoubleChestFakeInventory implements BaseInventory{


    public BigBagInventory(InventoryHolder holder) {
        super(holder);
        this.setName(((BaseBag) holder).getItem().getCustomName());
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);


    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.entityId = id;
        pk.type = InventoryType.DOUBLE_CHEST.getNetworkType();
        who.dataPacket(pk);
    }

    @Override
    public void onClose(Player who) {
        if(holder instanceof BaseBag){
            ((BaseBag) holder).close();
        }
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        who.dataPacket(pk);

        super.onClose(who);
    }

    private Player player;

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
