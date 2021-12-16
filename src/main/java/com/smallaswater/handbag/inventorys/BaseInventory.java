package com.smallaswater.handbag.inventorys;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import com.smallaswater.handbag.items.BaseBag;

/**
 * @author SmallasWater
 */
public interface BaseInventory extends InventoryHolder {

    /**
     * 正在使用的玩家
     * @param player  玩家
     * */
    void setPlayer(Player player);
    /**
     * 正在使用的玩家
     * @return 玩家
     * */
    Player getPlayer();


}
