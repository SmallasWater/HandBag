package com.smallaswater.handbag.inventorys;


import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
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
}
