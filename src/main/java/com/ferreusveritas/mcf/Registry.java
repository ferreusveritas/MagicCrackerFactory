package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.block.*;
import com.ferreusveritas.mcf.entity.CommandPotionEntity;
import com.ferreusveritas.mcf.entity.ItemDisplayEntity;
import com.ferreusveritas.mcf.item.*;
import com.ferreusveritas.mcf.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Registry {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MCF.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MCF.MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MCF.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MCF.MOD_ID);

    public static void setup(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
    }

    //////////////////////////////////////////////
    /// CARTOGRAPHER
    //////////////////////////////////////////////

    public static final RegistryObject<Block> CARTOGRAPHER_BLOCK = BLOCKS.register("cartographer", () -> new PeripheralBlock(PeripheralType.CARTOGRAPHER));

    public static final RegistryObject<TileEntityType<CartographerTileEntity>> CARTOGRAPHER_TILE_ENTITY = TILE_ENTITIES.register("cartographer",
            () -> TileEntityType.Builder.of(CartographerTileEntity::new, CARTOGRAPHER_BLOCK.get()).build(null));

    //////////////////////////////////////////////
    /// CLAIM
    //////////////////////////////////////////////

    public static final RegistryObject<Block> CLAIM_BLOCK = BLOCKS.register("claim", ClaimBlock::new);

    public static final RegistryObject<Item> CLAIM_ITEM = ITEMS.register("claim", () -> blockItem(CLAIM_BLOCK));

    //////////////////////////////////////////////
    /// SENTINEL
    //////////////////////////////////////////////

    public static final RegistryObject<Block> SENTINEL_BLOCK = BLOCKS.register("sentinel", () -> new PeripheralBlock(PeripheralType.SENTINEL));

    public static final RegistryObject<Item> SENTINEL_ITEM = ITEMS.register("sentinel", () -> blockItem(SENTINEL_BLOCK));

    public static final RegistryObject<TileEntityType<SentinelTileEntity>> SENTINEL_TILE_ENTITY = TILE_ENTITIES.register("sentinel",
            () -> TileEntityType.Builder.of(SentinelTileEntity::new, SENTINEL_BLOCK.get()).build(null));

    //////////////////////////////////////////////
    /// LIGHT
    //////////////////////////////////////////////

    public static final RegistryObject<Block> LIGHT_BLOCK = BLOCKS.register("light", LightBlock::new);

    public static final List<RegistryObject<Item>> LIGHT_ITEMS = Util.make(() -> {
        List<RegistryObject<Item>> items = new ArrayList<>();
        for (int level = 0; level <= 15; level++) {
            items.add(createLightItem(level));
        }
        return items;
    });

    private static RegistryObject<Item> createLightItem(int level) {
        return ITEMS.register("light_" + level, () -> new LightItem(LIGHT_BLOCK.get(), itemProperties(), level));
    }

    public static final RegistryObject<Item> CARTOGRAPHER_ITEM = ITEMS.register("cartographer", () -> blockItem(CARTOGRAPHER_BLOCK));

    //////////////////////////////////////////////
    /// REMOTE
    //////////////////////////////////////////////

    public static final RegistryObject<Block> REMOTE_RECEIVER_BLOCK = BLOCKS.register("remote_receiver", () -> new PeripheralBlock(PeripheralType.REMOTE_RECEIVER));
    public static final RegistryObject<TileEntityType<RemoteReceiverTileEntity>> REMOTE_RECEIVER_TILE_ENTITY = TILE_ENTITIES.register("remote_receiver",
            () -> TileEntityType.Builder.of(RemoteReceiverTileEntity::new, REMOTE_RECEIVER_BLOCK.get()).build(null));
    public static final RegistryObject<Block> TOUCH_BUTTON_BLOCK = BLOCKS.register("touch_button", TouchButtonBlock::new);
    public static final RegistryObject<Block> REMOTE_BUTTON_BLOCK = BLOCKS.register("remote_button", RemoteButtonBlock::new);

    public static final RegistryObject<Item> REMOTE_RECEIVER_ITEM = ITEMS.register("remote_receiver", () -> blockItem(REMOTE_RECEIVER_BLOCK));
    public static final RegistryObject<Item> REMOTE_BUTTON_ITEM = ITEMS.register("remote_button", () -> blockItem(REMOTE_BUTTON_BLOCK));
    public static final RegistryObject<Item> TOUCH_BUTTON_ITEM = ITEMS.register("touch_button", () -> blockItem(TOUCH_BUTTON_BLOCK));

    public static final RegistryObject<UniversalRemote> UNIVERSAL_REMOTE = ITEMS.register("universal_remote", () -> new UniversalRemote(itemProperties()));

    //////////////////////////////////////////////
    /// RINGS
    //////////////////////////////////////////////

    public static final RegistryObject<CommandChunkRing> COMMAND_CHUNK_RING = ITEMS.register("command_chunk_ring", () -> new CommandChunkRing(itemProperties()));
    public static final RegistryObject<CommandBlockRing> COMMAND_BLOCK_RING = ITEMS.register("command_block_ring", () -> new CommandBlockRing(itemProperties()));

    public static final RegistryObject<EntityType<CommandPotionEntity>> COMMAND_POTION_ENTITY = ENTITIES.register(
            "command_potion",
            () -> EntityType.Builder.of(CommandPotionEntity::new, EntityClassification.MISC).build("command_potion")
    );
    public static final RegistryObject<EntityType<ItemDisplayEntity>> ITEM_DISPLAY_ENTITY = ENTITIES.register(
            "item_display",
            () -> EntityType.Builder.of(ItemDisplayEntity::new, EntityClassification.MISC).build("item_display")
    );

    //////////////////////////////////////////////
    /// TERRAFORMER
    //////////////////////////////////////////////

    public static final RegistryObject<Block> TERRAFORMER_BLOCK = BLOCKS.register("terraformer", () -> new PeripheralBlock(PeripheralType.TERRAFORMER));

    public static final RegistryObject<TileEntityType<TerraformerTileEntity>> TERRAFORMER_TILE_ENTITY = TILE_ENTITIES.register("terraformer",
            () -> TileEntityType.Builder.of(TerraformerTileEntity::new, TERRAFORMER_BLOCK.get()).build(null));

    public static final RegistryObject<Item> TERRAFORMER_ITEM = ITEMS.register("terraformer", () -> blockItem(TERRAFORMER_BLOCK));

    //////////////////////////////////////////////
    /// WEB MODEM
    //////////////////////////////////////////////

    public static final RegistryObject<Block> WEB_MODEM_BLOCK = BLOCKS.register("web_modem", () -> new PeripheralBlock(PeripheralType.WEB_MODEM));

    public static final RegistryObject<TileEntityType<WebModemTileEntity>> WEB_MODEM_TILE_ENTITY = TILE_ENTITIES.register("web_modem",
            () -> TileEntityType.Builder.of(WebModemTileEntity::new, WEB_MODEM_BLOCK.get()).build(null));

    public static final RegistryObject<Item> WEB_MODEM_ITEM = ITEMS.register("web_modem", () -> blockItem(WEB_MODEM_BLOCK));

    //////////////////////////////////////////////
    /// MISC
    //////////////////////////////////////////////

    public static final RegistryObject<Block> MAP_GUARD_BLOCK = BLOCKS.register("map_guard", MapGuardBlock::new);
    public static final RegistryObject<Item> MAP_GUARD_ITEM = ITEMS.register("map_guard", () -> new MapGuardItem(MAP_GUARD_BLOCK.get(), itemProperties(), false));
    public static final RegistryObject<Item> LIT_MAP_GUARD_ITEM = ITEMS.register("lit_map_guard", () -> new MapGuardItem(MAP_GUARD_BLOCK.get(), itemProperties(), true));

    public static final RegistryObject<CommandPotion> COMMAND_POTION = ITEMS.register("command_potion", () -> new CommandPotion(itemProperties()));
    public static final RegistryObject<CommandSplashPotion> COMMAND_SPLASH_POTION = ITEMS.register("command_splash_potion", () -> new CommandSplashPotion(itemProperties()));

    private static Item.Properties itemProperties() {
        return new Item.Properties().tab(ItemGroups.MAIN);
    }

    private static BlockItem blockItem(RegistryObject<Block> block) {
        return new BlockItem(block.get(), itemProperties());
    }

}
