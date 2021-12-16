package com.smallaswater.handbag.thread;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.PluginTask;
import com.smallaswater.handbag.HandBag;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.utils.Tools;

import java.util.Map;

/**
 * 检测nbt是否一致
 * @author LT_Name
 */
public class CheckTask extends PluginTask<HandBag> {

    public CheckTask(HandBag owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        BaseBag bag;
        Item n;
        for (Player player : this.owner.getServer().getOnlinePlayers().values()) {
            for (Map.Entry<Integer,Item> item : player.getInventory().getContents().entrySet()) {
                if (Tools.isHandBag(item.getValue())) {
                    bag = BaseBag.getBaseBagByItem(player,item.getValue().getNamedTag().getString("configName"),item.getValue());
                    n = bag.toItem();
                    if(!item.getValue().equals(n,true,true)){
                        player.getInventory().setItem(item.getKey(),n);
                    }

                }
            }
        }
    }

}