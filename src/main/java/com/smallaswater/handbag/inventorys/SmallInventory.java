package com.smallaswater.handbag.inventorys;


import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.protocol.ContainerOpenPacket;

/**
 * @author SmallasWater
 */
public class SmallInventory extends BagInventory implements BaseInventory {


    public SmallInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }

    @Override
    public void onOpen(Player who) {
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.entityId = id;
        pk.type = InventoryType.HOPPER.getNetworkType();
        who.dataPacket(pk);

        super.onOpen(who);
    }


}
