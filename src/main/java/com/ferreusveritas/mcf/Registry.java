package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.block.*;
import com.ferreusveritas.mcf.block.entity.*;
import com.ferreusveritas.mcf.entity.CommandPotionEntity;
import com.ferreusveritas.mcf.entity.ItemDisplayEntity;
import com.ferreusveritas.mcf.item.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registry {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MCF.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MCF.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MCF.MOD_ID);
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

    public static final RegistryObject<BlockEntityType<CartographerBlockEntity>> CARTOGRAPHER_TILE_ENTITY = TILE_ENTITIES.register("cartographer",
            () -> BlockEntityType.Builder.of(CartographerBlockEntity::new, CARTOGRAPHER_BLOCK.get()).build(null));

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

    public static final RegistryObject<BlockEntityType<SentinelBlockEntity>> SENTINEL_TILE_ENTITY = TILE_ENTITIES.register("sentinel",
            () -> BlockEntityType.Builder.of(SentinelBlockEntity::new, SENTINEL_BLOCK.get()).build(null));

    public static final RegistryObject<Item> CARTOGRAPHER_ITEM = ITEMS.register("cartographer", () -> blockItem(CARTOGRAPHER_BLOCK));

    //////////////////////////////////////////////
    /// REMOTE
    //////////////////////////////////////////////

    public static final RegistryObject<Block> REMOTE_RECEIVER_BLOCK = BLOCKS.register("remote_receiver", () -> new PeripheralBlock(PeripheralType.REMOTE_RECEIVER));
    public static final RegistryObject<BlockEntityType<RemoteReceiverBlockEntity>> REMOTE_RECEIVER_TILE_ENTITY = TILE_ENTITIES.register("remote_receiver",
            () -> BlockEntityType.Builder.of(RemoteReceiverBlockEntity::new, REMOTE_RECEIVER_BLOCK.get()).build(null));
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
            () -> EntityType.Builder.of(CommandPotionEntity::new, MobCategory.MISC).build("command_potion")
    );
    public static final RegistryObject<EntityType<ItemDisplayEntity>> ITEM_DISPLAY_ENTITY = ENTITIES.register(
            "item_display",
            () -> EntityType.Builder.of(ItemDisplayEntity::new, MobCategory.MISC).build("item_display")
    );

    //////////////////////////////////////////////
    /// TERRAFORMER
    //////////////////////////////////////////////

    public static final RegistryObject<Block> TERRAFORMER_BLOCK = BLOCKS.register("terraformer", () -> new PeripheralBlock(PeripheralType.TERRAFORMER));

    public static final RegistryObject<BlockEntityType<TerraformerBlockEntity>> TERRAFORMER_TILE_ENTITY = TILE_ENTITIES.register("terraformer",
            () -> BlockEntityType.Builder.of(TerraformerBlockEntity::new, TERRAFORMER_BLOCK.get()).build(null));

    public static final RegistryObject<Item> TERRAFORMER_ITEM = ITEMS.register("terraformer", () -> blockItem(TERRAFORMER_BLOCK));

    //////////////////////////////////////////////
    /// WEB MODEM
    //////////////////////////////////////////////

    public static final RegistryObject<Block> WEB_MODEM_BLOCK = BLOCKS.register("web_modem", () -> new PeripheralBlock(PeripheralType.WEB_MODEM));

    public static final RegistryObject<BlockEntityType<WebModemBlockEntity>> WEB_MODEM_TILE_ENTITY = TILE_ENTITIES.register("web_modem",
            () -> BlockEntityType.Builder.of(WebModemBlockEntity::new, WEB_MODEM_BLOCK.get()).build(null));

    public static final RegistryObject<Item> WEB_MODEM_ITEM = ITEMS.register("web_modem", () -> blockItem(WEB_MODEM_BLOCK));

    //////////////////////////////////////////////
    /// MISC
    //////////////////////////////////////////////

    public static final RegistryObject<Block> MAP_GUARD_BLOCK = BLOCKS.register("map_guard", MapGuardBlock::new);
    public static final RegistryObject<MapGuardItem> MAP_GUARD_ITEM = ITEMS.register("map_guard", () -> new MapGuardItem(MAP_GUARD_BLOCK.get(), itemProperties(), false));
    public static final RegistryObject<MapGuardItem> LIT_MAP_GUARD_ITEM = ITEMS.register("lit_map_guard", () -> new MapGuardItem(MAP_GUARD_BLOCK.get(), itemProperties(), true));

    public static final RegistryObject<CommandPotion> COMMAND_POTION = ITEMS.register("command_potion", () -> new CommandPotion(itemProperties()));
    public static final RegistryObject<CommandSplashPotion> COMMAND_SPLASH_POTION = ITEMS.register("command_splash_potion", () -> new CommandSplashPotion(itemProperties()));

    private static Item.Properties itemProperties() {
        return new Item.Properties().tab(CreativeModeTabs.MAIN);
    }

    private static BlockItem blockItem(RegistryObject<Block> block) {
        return new BlockItem(block.get(), itemProperties());
    }

}
