package com.kotori316.marker;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.yogpc.qp.machines.base.Area;
import com.yogpc.qp.machines.base.IMarker;
import com.yogpc.qp.machines.base.IRemotePowerOn;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoteControlItem extends Item {
    private static final Logger LOGGER = LogManager.getLogger(RemoteControlItem.class);
    public static final String NBT_AREA = "area";
    public static final String NBT_REMOTE_POS = "remote_pos";
    public static final String NAME = "remote_controller";

    public RemoteControlItem() {
        super(new Item.Properties().group(Marker.ITEM_GROUP));
        setRegistryName(Marker.modID, NAME);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        if (Caps.isQuarryModLoaded()) {
            TileEntity tileEntity = context.getWorld().getTileEntity(context.getPos());
            if (tileEntity != null) {
                Optional<LazyOptional<IMarker>> maybeMarker = Caps.markerCapability().map(c -> tileEntity.getCapability(c, context.getFace()).filter(IMarker::hasLink)).filter(LazyOptional::isPresent);
                Optional<LazyOptional<IRemotePowerOn>> maybeRemoteControllable = Caps.remotePowerOnCapability().map(c -> tileEntity.getCapability(c, context.getFace())).filter(LazyOptional::isPresent);
                if (maybeMarker.isPresent()) {
                    if (!context.getWorld().isRemote) {
                        maybeMarker.flatMap(l -> l.map(m -> Area.posToArea(m.min(), m.max())).map(Optional::of).orElse(getArea(stack)))
                            .ifPresent(area -> {
                                setArea(stack, Area.areaToNbt().apply(area)); // Save
                                maybeMarker.ifPresent(l -> l.ifPresent(m -> m.removeFromWorldWithItem().forEach(i -> {
                                    if (context.getPlayer() != null && !context.getPlayer().inventory.addItemStackToInventory(i)) {
                                        context.getPlayer().dropItem(i, false);
                                    }
                                }))); // Drop item

                                getRemotePos(stack)
                                    .map(context.getWorld()::getTileEntity)
                                    .flatMap(t -> Caps.remotePowerOnCapability().map(c -> t.getCapability(c, context.getFace())))
                                    .ifPresent(l -> l.ifPresent(r -> {
                                        LOGGER.debug("Send start request to {} with {}", r, area);
                                        r.setAndStart(area);
                                    }));
                            });
                        LOGGER.debug("New area set: {}", getArea(stack));
                    }
                    return ActionResultType.SUCCESS;
                } else if (maybeRemoteControllable.isPresent()) {
                    if (!context.getWorld().isRemote) {
                        Optional<Area> optionalArea = getArea(stack);
                        if (optionalArea.isPresent()) {
                            maybeRemoteControllable.ifPresent(l -> l.ifPresent(r -> {
                                r.setAndStart(optionalArea.get());
                                LOGGER.debug("Send start request to {} with {}", r, optionalArea.get());
                            }));
                        } else {
                            setRemotePos(stack, tileEntity.getPos());
                        }
                    }
                    return ActionResultType.SUCCESS;
                } else {
                    return super.onItemUseFirst(stack, context);
                }
            } else {
                if (context.isPlacerSneaking() && stack.hasTag()) {
                    stack.removeChildTag(NBT_REMOTE_POS);
                    stack.removeChildTag(NBT_AREA);
                    return ActionResultType.SUCCESS;
                } else {
                    return super.onItemUseFirst(stack, context);
                }
            }
        } else
            return super.onItemUseFirst(stack, context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(areaText(stack));
        tooltip.addAll(remotePosText(stack));
    }


    public static final Function<BlockPos, ITextComponent> convertPosText = p -> new TranslationTextComponent("tooltip.flexiblemarker.remote_pos", p.getX(), p.getY(), p.getZ());
    public static final Function<Area, ITextComponent> convertAreaText = a -> new TranslationTextComponent("tooltip.flexiblemarker.area", a);

    public static List<? extends ITextComponent> areaText(ItemStack stack) {
        return getArea(stack)
            .map(convertAreaText)
            .map(Collections::singletonList)
            .orElse(Collections.emptyList());
    }

    public static Optional<Area> getArea(ItemStack stack) {
        if (Caps.isQuarryModLoaded() && !stack.isEmpty()) {
            return Optional.ofNullable(stack.getChildTag(NBT_AREA))
                .map(Area::areaLoad);
        } else {
            return Optional.empty();
        }
    }

    public static void setArea(ItemStack stack, CompoundNBT areaNBT) {
        stack.setTagInfo(NBT_AREA, areaNBT);
    }

    public static List<? extends ITextComponent> remotePosText(ItemStack stack) {
        return getRemotePos(stack).map(convertPosText)
            .map(Collections::singletonList)
            .orElse(Collections.emptyList());
    }

    public static Optional<BlockPos> getRemotePos(ItemStack stack) {
        if (Caps.isQuarryModLoaded() && !stack.isEmpty())
            return Optional.ofNullable(stack.getTag())
                .filter(t -> t.contains(NBT_REMOTE_POS, Constants.NBT.TAG_LONG))
                .map(t -> t.getLong(NBT_REMOTE_POS))
                .map(BlockPos::fromLong);
        else {
            return Optional.empty();
        }
    }

    public static void setRemotePos(ItemStack stack, BlockPos pos) {
        stack.setTagInfo(NBT_REMOTE_POS, new LongNBT(pos.toLong()));
    }
}