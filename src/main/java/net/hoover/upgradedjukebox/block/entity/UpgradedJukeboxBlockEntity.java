package net.hoover.upgradedjukebox.block.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.hoover.upgradedjukebox.networking.ModMessages;
import net.hoover.upgradedjukebox.screen.UpgradedJukeboxScreenHandler;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class UpgradedJukeboxBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;
    private boolean isPlaying = false;
    private boolean toShuffle = true;
    private boolean paused = false;
    private boolean toLoop = false;

    protected final PropertyDelegate propertyDelegate;
    private int songProgress = 0;
    private int songLength = 0;
    private int skipCooldown = 0;

    public UpgradedJukeboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UPGRADED_JUKEBOX_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> UpgradedJukeboxBlockEntity.this.songProgress;
                    case 1 -> UpgradedJukeboxBlockEntity.this.songLength;
                    case 2 -> UpgradedJukeboxBlockEntity.this.toShuffle ? 1 : 0;
                    case 3 -> UpgradedJukeboxBlockEntity.this.paused ? 1 : 0;
                    case 4 -> UpgradedJukeboxBlockEntity.this.toLoop ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> UpgradedJukeboxBlockEntity.this.songProgress = value;
                    case 1 -> UpgradedJukeboxBlockEntity.this.songLength = value;
                    case 2 -> UpgradedJukeboxBlockEntity.this.toShuffle = value > 0;
                    case 3 -> UpgradedJukeboxBlockEntity.this.paused = value > 0;
                    case 4 -> UpgradedJukeboxBlockEntity.this.toLoop = value > 0;
                }
            }

            @Override
            public int size() {
                return 5;
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
        return Text.translatable("block.upgradedjukebox.upgraded_jukebox");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("upgraded_jukebox.progress", songProgress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        songProgress = nbt.getInt("upgraded_jukebox.progress");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new UpgradedJukeboxScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        if (isOutputSlotEmptyOrReceivable()) {
            if (this.isPlayable()) {
                if (!isPlaying && !paused) {
                    startPlaying();
                }
                if (skipCooldown <= 1) {
                    ++skipCooldown;
                }
                if (!paused) {
                    this.increaseSongProgress();
                }
                markDirty(world, pos, state);
                if (hasSongFinished()) {
                    if (!this.toLoop) {
                        this.finishSong();
                    }
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
        shiftItemsToEmptySlots();
        if (!paused && isPlaylistFinished()) {
            autoPlayDiscs(toShuffle);
        }
    }

    public void skipCurrentSong() {
        if (skipCooldown >= 1) {
            this.finishSong();
            this.resetSong();
        }
    }

    public void setShuffle(boolean toShuffle) {
        this.toShuffle = toShuffle;
    }

    public void setPaused(boolean toPause) {
        this.paused = toPause;
        if (getStack(INPUT_SLOT).getItem() instanceof MusicDiscItem) {
            Identifier packetId = paused ? ModMessages.RECEIVE_PAUSE_PACKET_ID : ModMessages.RECEIVE_RESUME_PACKET_ID;
            for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) world)) {
                ServerPlayNetworking.send(player, packetId, PacketByteBufs.create().writeIdentifier(((MusicDiscItem) getStack(INPUT_SLOT).getItem()).getSound().getId()).writeEnumConstant(SoundCategory.RECORDS).writeBlockPos(pos));
            }
        }
    }

    public void setLoop(boolean toLoop) {
        this.toLoop = toLoop;
    }


    private int getOutputSlot() {
        for (int i = inventory.size() / 2; i < inventory.size(); ++i) {
            if (getStack(i).isEmpty()) {
                return i;
            }
        }
        return inventory.size() - 1;
    }

    public void resetSong() {
        this.songProgress = 0;
        this.skipCooldown = 0;
        stopPlaying();
    }

    private void finishSong() {
        this.setStack(getOutputSlot(), this.removeStack(INPUT_SLOT));
        shiftItemsToEmptySlots();
        if (!paused && isPlaylistFinished()) {
            autoPlayDiscs(toShuffle);
        }
    }

    private void shiftItemsToEmptySlots() {
        int firstEmptySlot = -1;
        int i = 0;
        while (i < inventory.size() / 2) {
            if (firstEmptySlot > -1) {
                if (getStack(i) != ItemStack.EMPTY) {
                    this.setStack(firstEmptySlot, this.removeStack(i));
                    i = firstEmptySlot;
                    firstEmptySlot = -1;
                }
            } else {
                if (getStack(i) == ItemStack.EMPTY) {
                    firstEmptySlot = i;
                }
            }
            ++i;
        }
        firstEmptySlot = -1;
        i = inventory.size() / 2;
        while (i < inventory.size()) {
            if (firstEmptySlot >= inventory.size() / 2) {
                if (getStack(i) != ItemStack.EMPTY) {
                    this.setStack(firstEmptySlot, this.removeStack(i));
                    i = firstEmptySlot;
                    firstEmptySlot = -1;
                }
            } else {
                if (getStack(i) == ItemStack.EMPTY) {
                    firstEmptySlot = i;
                }
            }
            ++i;
        }
    }

    private boolean isPlaylistFinished() {
        return getStack(INPUT_SLOT).isEmpty() && !getStack(INPUT_SLOT + (inventory.size() / 2)).isEmpty();
    }

    private void autoPlayDiscs(boolean shuffle) {
        if (shuffle) {
            while (hasOutput()) {
                int i = inventory.size() / 2;
                int n = getNumItemsInOutput();
                if (n > 1) {
                    Random random = new Random();
                    i = random.nextInt(getNumItemsInOutput() + inventory.size() / 2);
                }
                this.setStack(inventory.size() / 2 - 1, this.removeStack(i));
                shiftItemsToEmptySlots();
            }
        } else {
            for (int i = inventory.size() / 2; i < inventory.size(); ++i) {
                this.setStack(i - inventory.size() / 2, this.removeStack(i));
            }
        }
    }

    private boolean hasOutput() {
        return !getStack(inventory.size() / 2).isEmpty();
    }

    private int getNumItemsInOutput() {
        int count = 0;
        for (int i = inventory.size() / 2; i < inventory.size(); ++i) {
            if (!getStack(i).isEmpty()) {
                ++count;
            }
        }
        return count;
    }

    private boolean hasSongFinished() {
        return songProgress >= songLength;
    }

    private void increaseSongProgress() {
        ++songProgress;
    }

    private boolean isPlayable() {
        ItemStack result = new ItemStack(getStack(INPUT_SLOT).getItem(), getStack(INPUT_SLOT).getCount());
        boolean hasInput = getStack(INPUT_SLOT).isIn(ItemTags.MUSIC_DISCS);

        return hasInput && canInsertAmountIntoOutputSlot(result) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(getOutputSlot()).getItem() == item || this.getStack(getOutputSlot()).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(getOutputSlot()).getCount() + result.getCount() <= getStack(getOutputSlot()).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(getOutputSlot()).isEmpty() || this.getStack(getOutputSlot()).getCount() < this.getStack(getOutputSlot()).getMaxCount();
    }

    public void startPlaying() {
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
