package cy.jdkdigital.treetap.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ColoredParticleType extends ParticleType<ColoredParticleType> implements ParticleOptions
{
    private float[] color = null;

    private final MapCodec<ColoredParticleType> codec = MapCodec.unit(this::getType);
    private final StreamCodec<RegistryFriendlyByteBuf, ColoredParticleType> streamCodec = StreamCodec.unit(this);

    @Override
    public MapCodec<ColoredParticleType> codec() {
        return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ColoredParticleType> streamCodec() {
        return streamCodec;
    }

    public ColoredParticleType() {
        super(false);
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    @Nullable
    public float[] getColor() {
        return this.color;
    }

    @Nonnull
    @Override
    public ColoredParticleType getType() {
        return this;
    }
}