package com.smallaswater.handbag.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.smallaswater.handbag.forms.CreateWindow;

/**
 * @author SmallasWater
 * Create on 2021/2/19 9:59
 * Package com.smallaswater.handbag.commands
 */
public class HandBagUseCommand extends Command {
    public HandBagUseCommand(String name) {
        super(name);
        this.setDescription("通过GUI使用手提袋");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            CreateWindow.sendMenu((Player) commandSender);
        }
        return true;
    }
}
