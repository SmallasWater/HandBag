package com.smallaswater.handbag.inventorys;


import cn.nukkit.Player;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import com.smallaswater.handbag.items.BaseBag;

/**
 * @author SmallasWater
 */
public class BagInventory extends ContainerInventory implements BaseInventory{

    public long id;


    public BagInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }



    @Override
    public void onOpen(Player who) {
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.entityId = id;
        who.dataPacket(pk);

        super.onOpen(who);
    }

    @Override
    public String getTitle() {
        if(holder instanceof BaseBag) {
            return ((BaseBag) holder).getItem().getCustomName();
        }
        return "无~~";
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

    @Override
    public Inventory getInventory() {
        return this;
    }
}
