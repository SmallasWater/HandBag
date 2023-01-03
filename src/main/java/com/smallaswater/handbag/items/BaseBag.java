package com.smallaswater.handbag.items;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.handbag.HandBag;
import com.smallaswater.handbag.inventorys.BagInventory;
import com.smallaswater.handbag.inventorys.BaseInventory;
import com.smallaswater.handbag.inventorys.BigBagInventory;
import com.smallaswater.handbag.inventorys.SmallInventory;
import com.smallaswater.handbag.inventorys.lib.AbstractFakeInventory;
import com.smallaswater.handbag.items.bags.BigBag;
import com.smallaswater.handbag.items.bags.SmallBag;
import com.smallaswater.handbag.items.bags.ToSmallBag;
import com.smallaswater.handbag.items.types.BagType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author SmallasWater
 */
public abstract class BaseBag implements InventoryHolder {

    private Player player;

    private String name;
    private boolean canRemove;

    private boolean autoPickUp = true;
    private Item item;

    private BagType type;

    private boolean sneaking;

    private BaseInventory inventory;


    public final static String NAME_TAG = "bagItems";

    private LinkedList<String> lores = new LinkedList<String>(){{
        add("§r§7\n容量: §e%size%  §7当前:§e %count%");
        add("§r§7\n自动拾取 %key%");
    }};


    protected BaseBag(Player player,String name,BagType type, Item item){
        this.player = player;
        this.name = name;
        this.item = item;
        this.type = type;

        if(type == BagType.TO_SMALL){
            this.inventory = new SmallInventory(this, InventoryType.HOPPER);
        }else if(type == BagType.SMALL){
            this.inventory = new BagInventory(this, InventoryType.CHEST);
        }else{
            this.inventory = new BigBagInventory(this);
        }
        this.inventory.setPlayer(player);
        CompoundTag tag = item.getNamedTag();
        if(tag.contains(BaseBag.NAME_TAG)) {
            this.inventory.getInventory().setContents(
                    toSlotByItem(getItem()
                            .getNamedTag().getCompound(BaseBag.NAME_TAG))
            );
            if(player != null){
                this.inventory.getInventory().sendContents(player);
            }
        }
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCanRemove() {
        return canRemove;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

    public Item getItem() {
        return item;
    }

    public boolean isAutoPickUp() {
        return autoPickUp;
    }

    public void setAutoPickUp(boolean autoPickUp) {
        this.autoPickUp = autoPickUp;
    }

    public BagType getType() {
        return type;
    }

    @Override
    public AbstractFakeInventory getInventory() {
        return (AbstractFakeInventory) inventory.getInventory();
    }



    public static Item stringToItem(String str){
        return Item.fromString(str);
        //String[] s = str.split(":");
        //return Item.get(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
    }


    public void close() {

        if(item.getNamedTag() != null) {
            CompoundTag tag = toCompoundTagBySlot(inventory.getInventory().getContents());
            this.item.getNamedTag().putCompound(NAME_TAG,tag);
        }
    }


    public static BaseBag getBaseBagByItem(Player player,Item target,Item item){
        if(target.getNamedTag() == null){
            return null;
        }
        return getBaseBagByItem(player,target.getNamedTag().getString("configName"), item);
    }

    public static BaseBag getBaseBagByItem(Player player,String name,Item item){
        BaseBag baseBag = null;
        if(item.getNamedTag() != null){
            if(item.getNamedTag().contains(NAME_TAG)){
                if("small".equalsIgnoreCase(item.getNamedTag().getString("bagType"))) {
                    baseBag = new SmallBag(player,name,item);
                }else if("big".equalsIgnoreCase(item.getNamedTag().getString("bagType"))){
                    baseBag = new BigBag(player,name,item);
                }else{
                    baseBag = new ToSmallBag(player,name, item);
                }
                baseBag.setAutoPickUp(item.getNamedTag().getBoolean("auto-pickup"));
                baseBag.setCanRemove(item.getNamedTag().getBoolean("remove"));
                baseBag.setSneaking(item.getNamedTag().getBoolean("sneaking-rename"));
            }

        }
        return baseBag;
    }

    private CompoundTag toCompoundTagBySlot(Map<Integer, Item> slot){
        CompoundTag tag = new CompoundTag();
        for(int i: slot.keySet()){
            Item item = slot.get(i);
            if(item.hasCompoundTag()){
                if(item.getNamedTag().contains(NAME_TAG)){
                    continue;
                }
            }
            tag.putCompound(i + "", NBTIO.putItemHelper(item));
        }
        return tag;

    }



    public Map<Integer,Item> toSlotByItem(CompoundTag tag){
        LinkedHashMap<Integer,Item> items = new LinkedHashMap<>();
        if(NAME_TAG.equalsIgnoreCase(tag.getName())){
            for(int i = 0;i < type.getSize();i++){
                if(tag.contains(i+"")) {
                    items.put(i,NBTIO.getItemHelper(tag.getCompound(i+"")));
                }

            }
        }
        return items;

    }

    public Item toItem(){
        if(!item.hasCustomName()) {
            item.setCustomName(HandBag.getBag().getConfig().getString(item.getNamedTag().getString("configName")+".name"));
        }

        List<String> list = HandBag.getBag().getConfig().getStringList(item.getNamedTag().getString("configName")+".lore");
        LinkedList<String> lores = new LinkedList<>();
        if(list.size() == 0){
            list = this.lores;
        }
        String pick = autoPickUp?"&a开启":"&c关闭";
        for(String s:list){
            lores.add(TextFormat.colorize('&',s.replace("%key%",pick)
                    .replace("%size%",inventory.getInventory().getSize()+"")
                    .replace("%count%",inventory.getInventory().getContents().size()+"")));
        }
        CompoundTag tag = item.getNamedTag();
        tag.putBoolean("auto-pickup",autoPickUp);
        tag.putCompound(NAME_TAG,toCompoundTagBySlot(inventory.getInventory().getContents()));
        item.setCompoundTag(tag);
        item.setLore(lores.toArray(new String[0]));
        if(!item.hasEnchantments()) {
            item.addEnchantment(Enchantment.get(0));
        }
        return item;
    }

    public static LinkedList<BaseBag> registerItem(Config config){
        Map<String,Object> maps = config.getAll();
        LinkedList<BaseBag> bags = new LinkedList<>();
        for(String name:maps.keySet()){
            BaseBag bag;
            Map map = (Map) maps.get(name);
            Item item = stringToItem(map.get("item").toString());
            item.setCustomName(map.get("name").toString());
            item.addEnchantment(Enchantment.get(0));
            if(!item.getNamedTag().contains(NAME_TAG)){
                item.getNamedTag().putCompound(NAME_TAG,new CompoundTag());
            }
            item.getNamedTag().putString("bagType",map.get("size").toString());
            boolean remove = false;
            if(map.containsKey("remove")){
                remove = Boolean.valueOf(map.get("remove").toString());
            }
            boolean reset = false;
            if(map.containsKey("sneaking-rename")){
                reset = Boolean.valueOf(map.get("sneaking-rename").toString());
            }
            boolean pickup = true;
            if(map.containsKey("auto-pickup")){
                pickup = Boolean.valueOf(map.get("auto-pickup").toString());
            }
            item.getNamedTag().putBoolean("auto-pickup",pickup);
            item.getNamedTag().putBoolean("sneaking-rename",reset);
            item.getNamedTag().putBoolean("remove",remove);
            item.getNamedTag().putString("configName",name);
            if(map.get("size").toString().equalsIgnoreCase(BagType.SMALL.getName())) {
                item.setLore(toLore(name,BagType.SMALL.getSize(),pickup));
                bag = new SmallBag(null,name,item);
            }else if(map.get("size").toString().equalsIgnoreCase(BagType.BIG.getName())) {
                item.setLore(toLore(name,BagType.BIG.getSize(),pickup));
                bag = new BigBag(null,name,item);
            }else{
                item.setLore(toLore(name,BagType.TO_SMALL.getSize(),pickup));
                bag = new ToSmallBag(null,name,item);
            }
            bag.setAutoPickUp(pickup);
            bag.setCanRemove(item.getNamedTag().getBoolean("remove"));
            bag.setSneaking(item.getNamedTag().getBoolean("sneaking-rename"));
            bags.add(bag);

        }

        return bags;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    private static String[] toLore(String name, int size,boolean autoPickUp) {
        List<String> list = HandBag.getBag().getConfig().getStringList(name+".lore");
        LinkedList<String> lore = new LinkedList<>();
        for (String s : list) {
            lore.add(TextFormat.colorize('&',s.replace("%size%", size +"")
                    .replace("%count%", "0")
                    .replace("%key%",autoPickUp?"&a开启":"&c关闭"))
            );
        }
        return lore.toArray(new String[0]);
    }



}

