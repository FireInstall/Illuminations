package ladysnake.illuminations.client;

import org.ladysnake.satin.api.event.EntitiesPreRenderCallback;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.Identifier;

public class Rainbowlluminations {
    // rainbow shader for jeb mobs
    public static final ManagedCoreShader RAINBOW_SHADER = ShaderEffectManager.getInstance().manageCoreShader(Identifier.of(Illuminations.MODID, "jeb"));
    private static final Uniform1f uniformSTime = RAINBOW_SHADER.findUniform1f("Time");
    private static int ticks;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks++);
        EntitiesPreRenderCallback.EVENT.register((camera, frustum, tickDelta) -> uniformSTime.set((ticks + tickDelta) * 0.05f));
    }
}
