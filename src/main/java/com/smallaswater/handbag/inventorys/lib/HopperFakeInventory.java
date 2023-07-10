package com.smallaswater.handbag.inventorys.lib;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;

/**
 * @author SmallasWater
 * Create on 2021/7/13 11:23
 * Package com.smallaswater.handbag.inventorys.lib
 */
public class HopperFakeInventory extends AbstractFakeInventory{
    private String name;

    public HopperFakeInventory(InventoryType type, InventoryHolder holder, String title) {
        super(type, holder, title);
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
        int y = who.getY() > 250 ? who.getFloorY() - 3 : who.getFloorY() + 3;
        BlockVector3 blockPosition = new BlockVector3((int) who.x, y, (int) who.z);

        placeChest(who, blockPosition);

        return Collections.singletonList(blockPosition);
    }

    void placeChest(Player who, BlockVector3 pos) {
        who.dataPacket(getDefaultPack(BlockID.HOPPER_BLOCK,pos));
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
            throw new RuntimeException("Unable to create NBT for chest");
        }
    }
}
