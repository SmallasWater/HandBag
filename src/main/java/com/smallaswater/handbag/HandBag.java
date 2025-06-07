package com.smallaswater.handbag;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.handbag.commands.HandBagUseCommand;
import com.smallaswater.handbag.forms.WindowsListener;
import com.smallaswater.handbag.inventorys.BaseInventory;
import com.smallaswater.handbag.inventorys.lib.AbstractFakeInventory;
import com.smallaswater.handbag.items.BaseBag;
import com.smallaswater.handbag.utils.Tools;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author SmallasWater
 */
public class HandBag extends PluginBase implements Listener {


    private static HandBag bag;

    private final ConcurrentHashMap<String, Long> key = new ConcurrentHashMap<>();

    public LinkedHashMap<String, Integer> slot = new LinkedHashMap<>();

    public static final String PLUGIN_NAME = "&f[&c系统&f]";

    private static final ThreadPoolExecutor THREAD_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static HandBag getBag() {
        return bag;
    }

    @Override
    public void onLoad() {
        bag = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();
        checkServer();

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new WindowsListener(), this);
        this.getServer().getCommandMap().register("handbag", new HandBagUseCommand("hg"));

        //使用Task在所有插件加载完成后注册物品，防止无法使用自定义物品
        this.getServer().getScheduler().scheduleTask(this, () -> {
            LinkedList<BaseBag> baseBags = BaseBag.registerItem(getConfig());
            for (BaseBag baseBag : baseBags) {
                if (!Item.isCreativeItem(baseBag.getItem())) {
                    Item.addCreativeItem(baseBag.getItem());
                }
            }
        });

        this.getLogger().info("手提包加载成功...");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Item hand = event.getItem();
        if (hand == null) {
            return;
        }
        if (Tools.isHandBag(hand)) {
            event.setCancelled();
            openHandBag(event.getPlayer(), event.getPlayer().getInventory().getHeldItemIndex(), hand);
        }
    }

    /**
     * 获取玩家背包内所有的手提包
     *
     * @param player 玩家
     * @return 手提包与相应的位置
     */
    public LinkedHashMap<Integer, BaseBag> getAllBag(Player player) {
        LinkedHashMap<Integer, BaseBag> baseBagLinkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Item> itemEntry : player.getInventory().getContents().entrySet()) {
            BaseBag bag = BaseBag.getBaseBagByItem(player, itemEntry.getValue().getNamedTag().getString("configName"), itemEntry.getValue());
            if (bag == null) {
                continue;
            }
            baseBagLinkedHashMap.put(itemEntry.getKey(), bag);
        }
        return baseBagLinkedHashMap;
    }

    /**
     * 向手提袋存放物品
     *
     * @param item   存放的物品
     * @param player 玩家
     * @param target 手提袋物品
     * @param slot   手提袋位置
     * @return 是否存放成功
     */
    public static boolean saveItemToHandBag(Player player, Item item, Item target, int slot) {
        BaseBag bag = BaseBag.getBaseBagByItem(player, target.getNamedTag().getString("configName"), target);
        if (bag == null) {
            return false;
        }
        if (bag.getInventory().canAddItem(item)) {
            bag.getInventory().addItem(item);
            player.getInventory().setItem(slot, bag.toItem());
            return true;
        }
        return false;
    }

    /**
     * 自动往手提包存物品
     *
     * @param player 玩家
     * @param item   存放的物品
     * @return 无法存放的物品
     */
    public static Item[] addItemToHandBag(Player player, boolean pickUp, Item... item) {
        BaseBag bag;
        ArrayList<Item> items = new ArrayList<>(Arrays.asList(item));
        Iterator<Item> slotItem = items.iterator();
        LinkedHashMap<Integer, Item> handbags = Tools.getHandBagByInventory(player);
        if (handbags.size() == 0) {
            return item;
        }
        P:
        for (Map.Entry<Integer, Item> handBag : handbags.entrySet()) {
            bag = BaseBag.getBaseBagByItem(player, handBag.getValue().getNamedTag().getString("configName"), handBag.getValue());
            if (bag == null) {
                continue;
            }
            if (handBag.getValue().getCount() > 1) {
                continue;
            }
            if (pickUp && !bag.isAutoPickUp()) {
                continue;
            }
            if (bag.getInventory().slots.size() == bag.getType().getSize()) {
                continue;
            }
            while (slotItem.hasNext()) {
                Item i = slotItem.next();
                if (bag.getInventory().canAddItem(i)) {
                    bag.getInventory().addItem(i);
                    slotItem.remove();
                } else {
                    continue P;
                }
            }
            player.getInventory().setItem(handBag.getKey(), bag.toItem());
        }
        return items.toArray(new Item[0]);
    }


    @EventHandler
    public void onPickUp(InventoryPickupItemEvent event) {
        if (event.getInventory() instanceof PlayerInventory) {
            PlayerInventory inventory = (PlayerInventory) event.getInventory();

            if (Tools.isHandBag(event.getItem().getItem())) {
                return;
            }
            Item[] items = addItemToHandBag((Player) inventory.getHolder(), true, event.getItem().getItem());
            if (items.length == 0) {
                event.getItem().close();
                event.setCancelled();
            } else {
                event.getItem().getItem().setCount(items[0].getCount());
            }
        }
    }

    private final ConcurrentHashMap<Player, Long> timing = new ConcurrentHashMap<>();

    public void openHandBag(Player player, int index, Item hand) {
        if (!Tools.isHandBag(hand)) {
            return;
        }

        if (!timing.containsKey(player)) {
            timing.put(player, System.currentTimeMillis());
        } else {
            if (System.currentTimeMillis() - timing.get(player) < 2000) {
                player.sendMessage("§6[§7手提袋§6] §c 开启过于频繁");
                return;
            } else {
                timing.put(player, System.currentTimeMillis());
            }
        }

        if (hand.getCount() > 1) {
            player.sendMessage("§6[§7手提袋§6] §c 堆叠状态无法开启");
            return;
        }

        //高度检查，避免无法生成假箱子
        int minY = 0;
        int maxY = 256;
        if ("PowerNukkitX".equalsIgnoreCase(Server.getInstance().getCodename()) && player.getLevel().getDimension() == 0) {
            minY = -64;
            maxY = 384;
        }
        if (player.getY() >= maxY || player.getY() <= minY) {
            player.sendMessage("§6[§7手提袋§6] §c 当前位置无法开启");
            return;
        }

        if (AbstractFakeInventory.OPEN.containsKey(player)) {
            AbstractFakeInventory fakeInventory = AbstractFakeInventory.OPEN.get(player);
            fakeInventory.close(player);
            /*fakeInventory.clearAll();
            fakeInventory.blockPositions.remove(player.getName());*/
        }

        BaseBag bag = BaseBag.getBaseBagByItem(player, hand.getNamedTag().getString("configName"), hand);
        if (bag == null) {
            return;
        }
        if (key.containsKey(player.getName())) {
            player.sendMessage("§6[§7手提袋§6] §c 检测到恶意刷物品行为");
            player.kick("§c恶意利用手提袋刷物品");
            key.remove(player.getName());
            return;
        }
        if (bag.getInventory().blockPositions.containsKey(player)) {
            return;
        }
        if (bag.getPlayer() == null) {
            bag.setPlayer(player);
            bag.getInventory().sendContents(player);
            bag.getInventory().setContents(bag.toSlotByItem(hand
                    .getNamedTag().getCompound(BaseBag.NAME_TAG)));
        }
        slot.put(player.getName(), index);
        if (player.isSneaking() && bag.isSneaking()) {
            FormWindowCustom simple = new FormWindowCustom("重命名");
            simple.addElement(new ElementInput("请输入新的名称 [&是颜色符号~]"));
            player.showFormWindow(simple, 0x25565);
            return;
        }
        AbstractFakeInventory inventory = bag.getInventory();
        this.key.put(player.getName(), inventory.id);
        player.level.addSound(player, Sound.RANDOM_CHESTOPEN);
        if (player.addWindow(inventory) == -1) {
            inventory.close(player);
            bag.close();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() instanceof BaseInventory) {
            Player player = event.getPlayer();
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder instanceof BaseBag) {
                player.level.addSound(player, Sound.RANDOM_CHESTCLOSED);
                this.saveSolt(player, ((BaseBag) holder).getItem());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (key.containsKey(player.getName())) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = key.get(player.getName());
            player.dataPacket(pk);
            key.remove(player.getName());
        }
    }

    @EventHandler
    public void onItemChange(InventoryTransactionEvent event) {
        InventoryTransaction transaction = event.getTransaction();
        for (InventoryAction action : transaction.getActions()) {
            Item item = action.getSourceItem();
            for (Inventory inventory : transaction.getInventories()) {
                if (inventory instanceof PlayerInventory) {
                    if (item.getNamedTag() != null) {
                        if (item.getNamedTag().contains(BaseBag.NAME_TAG)) {
                            InventoryHolder player1 = inventory.getHolder();
                            if (player1 instanceof Player) {
                                if (key.containsKey(((Player) player1).getName())) {
                                    event.setCancelled();
                                    return;
                                }
                            }
                        }
                    }
                }
                if (inventory instanceof BaseInventory) {
                    Player player = ((BaseInventory) inventory).getPlayer();
                    if (item.getNamedTag() != null) {
                        if (item.getNamedTag().contains(BaseBag.NAME_TAG)) {
                            event.setCancelled();
                            return;
                        }
                    }
                    InventoryHolder holder = inventory.getHolder();
                    if (holder instanceof BaseBag) {
                        //saveSolt(player, ((BaseBag) holder).getItem());
                    }
                }
            }
        }
    }

    public static String CORE_NAME = "";

    private void checkServer(){
        boolean ver = false;
        //双核心兼容
        CORE_NAME = "Nukkit";
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT_PM1E");
            ver = true;
            CORE_NAME = "Nukkit PM1E";
        } catch (ClassNotFoundException | NoSuchFieldException ignore) {

        }
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            CORE_NAME = c.getField("NUKKIT").get(c).toString();

            ver = true;

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {

        }

        AbstractFakeInventory.IS_PM1E = ver;
        if(ver){
            Server.getInstance().enableExperimentMode = true;
            Server.getInstance().forceResources = true;
        }
        sendMessageToConsole("&e当前核心为 "+CORE_NAME);
    }

    private void saveSolt(Player player, Item holder) {
        THREAD_POOL.execute(() -> {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Tools.isHandBag(holder)) {
                BaseBag bag = BaseBag.getBaseBagByItem(player, holder.getNamedTag().getString("configName"), holder);
                Item i = bag.toItem();
                if (!HandBag.getBag().slot.containsKey(player.getName())) {
                    return;
                }
                int slot = HandBag.getBag().slot.get(player.getName());
                if (bag.getInventory().getContents().isEmpty()) {
                    if (bag.isCanRemove()) {
                        Item r = player.getInventory().getItem(slot);
                        player.getInventory().removeItem(r);
                        return;
                    }
                }
                if (player.getInventory().setItem(slot, i)) {
                    this.slot.remove(player.getName());
                    this.key.remove(player.getName());
                }
            }
        });
    }

    public static void sendMessageToObject(String msg, Object o){
        String message = TextFormat.colorize('&',PLUGIN_NAME+" &r"+msg);
        if(o != null){
            if(o instanceof Player){
                if(((Player) o).isOnline()) {
                    ((Player) o).sendMessage(message);
                    return;
                }
            }
            if(o instanceof EntityHuman){
                message = ((EntityHuman) o).getName()+"->"+message;
            }
        }
        Server.getInstance().getLogger().info(message);

    }

    public static void sendMessageToConsole(String msg){
        sendMessageToObject(msg,null);
    }
}
