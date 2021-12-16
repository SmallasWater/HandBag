package com.smallaswater.handbag.inventorys;


import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import com.smallaswater.handbag.HandBag;
import com.smallaswater.handbag.inventorys.lib.HopperFakeInventory;
import com.smallaswater.handbag.items.BaseBag;

/**
 * @author SmallasWater
 */
public class SmallInventory extends HopperFakeInventory implements BaseInventory {

    private Player player;

    public SmallInventory(InventoryHolder holder, InventoryType type) {
        super(type,holder,null);
        this.setName(((BaseBag) holder).getItem().getCustomName());
    }

    @Override
    public void onOpen(Player who) {
//        ContainerOpenPacket pk = new ContainerOpenPacket();
//        pk.windowId = who.getWindowId(this);
//        pk.entityId = id;
//        pk.type = InventoryType.HOPPER.getNetworkType();
//        who.dataPacket(pk);

        super.onOpen(who);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

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

//        RemoveEntityPacket pk = new RemoveEntityPacket();
//        pk.eid = id;
//        who.dataPacket(pk);

        super.onClose(who);
    }

    @Override
    public Inventory getInventory() {
        return this;
    }


    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
