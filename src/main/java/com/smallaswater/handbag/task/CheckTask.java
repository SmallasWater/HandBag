package com.smallaswater.handbag.task;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.PluginTask;
import com.smallaswater.handbag.HandBag;
import com.smallaswater.handbag.utils.Tools;

/**
 * @author LT_Name
 */
public class CheckTask extends PluginTask<HandBag> {

    public CheckTask(HandBag owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for (Player player : this.owner.getServer().getOnlinePlayers().values()) {
            for (Item item : player.getInventory().slots.values()) {
                if (Tools.isHandBag(item) && item.getCount() > 1) {
                    item.setCount(1);
                }
            }
        }
    }

}
