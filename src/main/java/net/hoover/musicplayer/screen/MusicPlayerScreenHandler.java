package net.hoover.musicplayer.screen;

import net.hoover.musicplayer.block.entity.MusicPlayerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class MusicPlayerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final MusicPlayerBlockEntity blockEntity;

    public MusicPlayerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(5));
    }

    public MusicPlayerScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.MUSIC_PLAYER_SCREEN_HANDLER, syncId);
        checkSize((Inventory)blockEntity, 54);
        this.inventory = ((Inventory)blockEntity);
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((MusicPlayerBlockEntity) blockEntity);

        int rows = 6;
        int j;
        int k;
        for (j = 0; j < rows; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        addPlayerInventory(playerInventory, rows);
        addPlayerHotbar(playerInventory, rows);

        addProperties(arrayPropertyDelegate);
    }

    public boolean isPlaying() {
        return propertyDelegate.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int progressArrowSize = 26;
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public boolean toShuffle() {
        return this.propertyDelegate.get(2) > 0;
    }

    public boolean toAutoplay() {
        return this.propertyDelegate.get(3) > 0;
    }
    public boolean toPause() {
        return this.propertyDelegate.get(4) > 0;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (!((!getCursorStack().isEmpty() || actionType == SlotActionType.SWAP) && slotIndex >= inventory.size() / 2 && slotIndex < inventory.size())) {
            super.onSlotClick(slotIndex, button, actionType, player);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < inventory.size() ? !this.insertItem(itemStack2, inventory.size(), this.slots.size(), true) : !this.insertItem(itemStack2, 0, inventory.size() / 2, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot.id < inventory.size() / 2;
    }

    private void addPlayerInventory(PlayerInventory playerInventory, int rows) {
        int i = (rows - 4) * 18;
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory, int rows) {
        int i = (rows - 4) * 18;
        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
        }
    }
}
