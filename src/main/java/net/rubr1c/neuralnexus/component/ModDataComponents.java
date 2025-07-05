package net.rubr1c.neuralnexus.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.codec.LearnerModelData;
import net.rubr1c.neuralnexus.item.custom.LearnerModelItem;

import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, NeuralNexus.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> LIVING_ENTITY =
            register("living_entity", builder -> builder.networkSynchronized(ResourceLocation.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LearnerModelData>> MODEL =
            register("learner_model", builder -> builder.networkSynchronized(LearnerModelData.STREAM_CODEC));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
