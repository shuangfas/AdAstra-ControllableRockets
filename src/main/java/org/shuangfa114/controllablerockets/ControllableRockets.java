package org.shuangfa114.controllablerockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import com.teamresourceful.resourcefulconfig.common.config.ResourcefulConfig;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import org.shuangfa114.controllablerockets.network.NetworkHandler;
import org.shuangfa114.controllablerockets.util.RocketTierInformation;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ControllableRockets.MODID)
public class ControllableRockets {

    public static final String MODID = "controllable_rockets";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Configurator CONFIGURATOR = new Configurator();
    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, MODID);
    public static final RegistryEntry<WeldingToolsItem> WELDING_TOOLS = ITEMS.register("welding_tools", () -> new WeldingToolsItem(new Item.Properties().stacksTo(1)));
    public static final SimpleChannel network = NetworkHandler.getNetWork();


    @SuppressWarnings("removal")
    public ControllableRockets() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        CONFIGURATOR.registerConfig(Config.class);
        ITEMS.init();
        modEventBus.addListener(this::buildContents);
        modEventBus.addListener(this::registerConfigScreen);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        PoseStack poseStack = event.getPoseStack();
        Entity entity = event.getEntity().getVehicle();
        if (entity instanceof Rocket rocket) {
            poseStack.translate(0, RocketTierInformation.getTierByEntity(rocket).renderOffset, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - rocket.getYRot()));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rocket.getXRot()));
        }
    }
    @SuppressWarnings("removal")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerConfigScreen(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) -> {
            ResourcefulConfig config = CONFIGURATOR.getConfig(Config.class);
            return config == null ? null : new ConfigScreen(screen, null, config);
        })));
    }

    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(WELDING_TOOLS);
        }
    }

}

