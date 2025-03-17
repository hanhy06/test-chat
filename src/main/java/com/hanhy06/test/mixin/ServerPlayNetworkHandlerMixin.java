package com.hanhy06.test.mixin;

import com.hanhy06.test.util.Markup;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @ModifyVariable(method = "handleDecoratedMessage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private SignedMessage modifyDecoratedMessage(SignedMessage original) {
        return new SignedMessage(
                MessageLink.of(new UUID(0L, 0L)),
                null,
                original.signedBody(),
                Markup.markup(original.getContent()),
                FilterMask.PASS_THROUGH
        );
    }
}
