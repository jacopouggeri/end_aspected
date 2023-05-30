package net.jayugg.end_aspected.block.tree;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTreeDecorators {
    private static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, EndAspected.MOD_ID);
    public static final RegistryObject<TreeDecoratorType<VoidVineTreeDecorator>> VOID_LEAVE_VINE = TREE_DECORATORS.register("void_leave_vine", () -> new TreeDecoratorType<>(VoidVineTreeDecorator.CODEC));

    public static void register(IEventBus eventBus) {
        TREE_DECORATORS.register(eventBus);
    }
}
