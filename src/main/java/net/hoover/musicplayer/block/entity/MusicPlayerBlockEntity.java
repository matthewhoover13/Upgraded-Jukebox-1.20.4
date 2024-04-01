package net.hoover.musicplayer.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.hoover.musicplayer.item.ModItems;
import net.hoover.musicplayer.screen.MusicPlayerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class MusicPlayerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private boolean isPlaying = false;

    protected final PropertyDelegate propertyDelegate;
    private int songProgress = 0;
    private int songLength = 0;

    public MusicPlayerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUSIC_PLAYER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MusicPlayerBlockEntity.this.songProgress;
                    case 1 -> MusicPlayerBlockEntity.this.songLength;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MusicPlayerBlockEntity.this.songProgress = value;
                    case 1 -> MusicPlayerBlockEntity.this.songLength = value;

                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Music Player");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("music_player.progress", songProgress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        songProgress = nbt.getInt("music_player.progress");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MusicPlayerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        if (isOutputSlotEmptyOrReceivable()) {
            if (this.isPlayable()) {
                if (!isPlaying) {
                    startPlaying();
                }
                this.increaseSongProgress();
                markDirty(world, pos, state);
                if (hasSongFinished()) {
                    this.finishSong();
                    this.resetSong();
                }
            }
            else {
                this.resetSong();
            }
        }
        else {
            this.resetSong();
            markDirty(world, pos, state);
        }
    }

    private void resetSong() {
        this.songProgress = 0;
        stopPlaying();
    }

    private void finishSong() {
        ItemStack result = new ItemStack(getStack(INPUT_SLOT).getItem());
        this.removeStack(INPUT_SLOT, 1);
        this.setStack(OUTPUT_SLOT, new ItemStack(result.getItem(), getStack(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasSongFinished() {
        return songProgress >= songLength;
    }

    private void increaseSongProgress() {
        songProgress++;
    }

    private boolean isPlayable() {
        ItemStack result = new ItemStack(ModItems.TEST_MUSIC_DISC);
        boolean hasInput = getStack(INPUT_SLOT).isIn(ItemTags.MUSIC_DISCS);

        return hasInput && canInsertAmountIntoOutputSlot(result) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    public void startPlaying() {
        //this.recordStartTick = this.tickCount;
        this.isPlaying = true;
        this.songLength = getSongLength() + 20;
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.world.syncWorldEvent(null, WorldEvents.JUKEBOX_STARTS_PLAYING, this.getPos(), Item.getRawId(this.getStack(INPUT_SLOT).getItem()));
        this.markDirty();
    }

    private void stopPlaying() {
        this.isPlaying = false;
        this.world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.world.syncWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, this.getPos(), 0);
        this.markDirty();
    }

    private int getSongLength() {
        if (getStack(INPUT_SLOT).isEmpty() || !getStack(INPUT_SLOT).isIn(ItemTags.MUSIC_DISCS)) {
            return 0;
        }
        MusicDiscItem item = (MusicDiscItem)getStack(INPUT_SLOT).getItem();
        return item.getSongLengthInTicks();
    }
}
