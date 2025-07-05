package net.rubr1c.neuralnexus.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.items.ItemStackHandler;
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
            register("living_entity", builder -> builder.persistent(ResourceLocation.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LearnerModelData>> MODEL =
            register("learner_model", builder -> builder.persistent(LearnerModelData.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> NBT =
            register("nbt_data", builder -> builder.persistent(CompoundTag.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PERCENT =
            register("percent", builder -> builder.persistent(Codec.intRange(0, 100)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BOOL =
            register("bool", builder -> builder.persistent(Codec.BOOL));



    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
