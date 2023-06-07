package net.jayugg.end_aspected.particle;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EndAspected.MOD_ID);

    public static final RegistryObject<BasicParticleType> VOID_CHARGE = PARTICLES.register("void_charge", () -> new BasicParticleType(true));

    public static final RegistryObject<BasicParticleType> VOID_CHARGE_POP = PARTICLES.register("void_charge_pop", () -> new BasicParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }

}
