package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by drzzm32 on 2019.2.23
 */
public abstract class AbsCargoLoco extends AbsLimLoco implements ILockableContainer, ILootContainer {

    private NonNullList<ItemStack> minecartContainerItems;
    public boolean dropContentsWhenDead;
    private ResourceLocation lootTable;
    private long lootTableSeed;
    public IItemHandler itemHandler;

    public AbsCargoLoco(World world) {
        super(world);
        this.minecartContainerItems = NonNullList.withSize(getSizeInventory() + 9, ItemStack.EMPTY);
        this.dropContentsWhenDead = true;
        this.itemHandler = new InvWrapper(this);
    }

    public AbsCargoLoco(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.minecartContainerItems = NonNullList.withSize(getSizeInventory() + 9, ItemStack.EMPTY);
        this.dropContentsWhenDead = true;
        this.itemHandler = new InvWrapper(this);
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.CHEST), 1, 0.0F);
        }
    }

    @Override
    public int getSizeInventory() {
        return 54;
    }

    @Override
    public Type getType() {
        return Type.CHEST;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    @Override
    public String getGuiID() {
        return "minecraft:chest";
    }

    @Override
    public Container createContainer(InventoryPlayer inventoryPlayer, EntityPlayer player) {
        this.addLoot(player);
        return new ContainerChest(inventoryPlayer, this, player);
    }

    @Override
    public boolean isEmpty() {
        Iterator var1 = this.minecartContainerItems.iterator();

        ItemStack itemstack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemstack = (ItemStack)var1.next();
        } while(itemstack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        this.addLoot((EntityPlayer)null);
        return (ItemStack)this.minecartContainerItems.get(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        this.addLoot((EntityPlayer)null);
        return ItemStackHelper.getAndSplit(this.minecartContainerItems, i, j);
    }

    public ItemStack removeStackFromSlot(int i) {
        this.addLoot((EntityPlayer)null);
        ItemStack itemstack = (ItemStack)this.minecartContainerItems.get(i);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.minecartContainerItems.set(i, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        this.addLoot((EntityPlayer)null);
        this.minecartContainerItems.set(i, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.isDead) {
            return false;
        } else {
            return player.getDistanceSq(this) <= 64.0D;
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    @Nullable
    public Entity changeDimension(int i, ITeleporter teleporter) {
        this.dropContentsWhenDead = false;
        return super.changeDimension(i, teleporter);
    }

    @Override
    public void setDead() {
        if (this.dropContentsWhenDead) {
            InventoryHelper.dropInventoryItems(this.world, this, this);
        }

        super.setDead();
    }

    @Override
    public void setDropItemsWhenDead(boolean dropItemsWhenDead) {
        this.dropContentsWhenDead = dropItemsWhenDead;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        if (this.lootTable != null) {
            tagCompound.setString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                tagCompound.setLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ItemStackHelper.saveAllItems(tagCompound, this.minecartContainerItems);
        }

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.minecartContainerItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (tagCompound.hasKey("LootTable", 8)) {
            this.lootTable = new ResourceLocation(tagCompound.getString("LootTable"));
            this.lootTableSeed = tagCompound.getLong("LootTableSeed");
        } else {
            ItemStackHelper.loadAllItems(tagCompound, this.minecartContainerItems);
        }

    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand))) {
            return true;
        } else if (player.isSneaking()) {
            return false;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                if (
                        stack.getItem() instanceof Item74HC04 || stack.getItem() instanceof Item1N4148 ||
                        stack.getItem() instanceof ItemNTP8Bit || stack.getItem() instanceof ItemNTP32Bit
                ) {
                    return true;
                }
            }

            if (!this.world.isRemote) {
                player.displayGUIChest(this);
            }

            return true;
        }
    }

    @Override
    public int getField(int i) {
        return 0;
    }

    @Override
    public void setField(int i, int j) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public void setLockCode(LockCode lockCode) {
    }

    @Override
    public LockCode getLockCode() {
        return LockCode.EMPTY_CODE;
    }

    public void addLoot(@Nullable EntityPlayer player) {
        if (this.lootTable != null) {
            LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(this.lootTable);
            this.lootTable = null;
            Random random;
            if (this.lootTableSeed == 0L) {
                random = new Random();
            } else {
                random = new Random(this.lootTableSeed);
            }

            LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer)this.world)).withLootedEntity(this);
            if (player != null) {
                lootcontext$builder.withLuck(player.getLuck()).withPlayer(player);
            }

            loottable.fillInventory(this, random, lootcontext$builder.build());
        }

    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this.itemHandler : super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public void clear() {
        this.addLoot((EntityPlayer)null);
        this.minecartContainerItems.clear();
    }

    public void setLootTable(ResourceLocation location, long seed) {
        this.lootTable = location;
        this.lootTableSeed = seed;
    }

    @Override
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

}
