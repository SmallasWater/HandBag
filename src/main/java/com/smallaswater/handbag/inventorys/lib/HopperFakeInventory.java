package com.smallaswater.handbag.inventorys.lib;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;


/**
 * 本类引用 SupermeMortal 的 FakeInventories 插件
 * @author SupermeMortal*/
public class HopperFakeInventory extends AbstractFakeInventory {

    private String name;

    public Player player;

    public HopperFakeInventory(InventoryType type, InventoryHolder holder, String title) {
        super(type, holder, title);
    }


    public void setPlayer(Player player) {
        this.player = player;
    }


    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<BlockVector3> onOpenBlock(Player who) {
        Position np = who.getSide(who.getDirection().rotateY().rotateY(),4) ;

        BlockVector3 blockPosition = new BlockVector3((int) np.x, ((int) np.y) + 2, (int) np.z);

        placeChest(who, blockPosition);

        return Collections.singletonList(blockPosition);
    }

    void placeChest(Player who, BlockVector3 pos) {
        UpdateBlockPacket updateBlock = new UpdateBlockPacket();
        if(IS_PM1E){
            updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(who.protocol,BlockID.HOPPER_BLOCK, 0);

        }else{
            updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(BlockID.HOPPER_BLOCK, 0);
        }

        updateBlock.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        updateBlock.x = pos.x;
        updateBlock.y = pos.y;
        updateBlock.z = pos.z;

        who.dataPacket(updateBlock);

        BlockEntityDataPacket blockEntityData = new BlockEntityDataPacket();
        blockEntityData.x = pos.x;
        blockEntityData.y = pos.y;
        blockEntityData.z = pos.z;
        blockEntityData.namedTag = getNbt(pos, getName());

        who.dataPacket(blockEntityData);
    }

    private static byte[] getNbt(BlockVector3 pos, String name) {
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.HOPPER)
                .putInt("x", pos.x)
                .putInt("y", pos.y)
                .putInt("z", pos.z)
                .putString("CustomName", name == null ? "Hopper" : name);

        try {
            return NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create NBT for hopper");
        }
    }

    @Override
    public void close(Player who) {
        super.close(who);
    }
}
