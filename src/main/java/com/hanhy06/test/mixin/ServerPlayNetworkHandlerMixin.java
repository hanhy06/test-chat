package com.hanhy06.test.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hanhy06.test.config.Configs;
import com.hanhy06.test.util.Filter;
import com.hanhy06.test.util.GeminiAPI;
import com.hanhy06.test.util.Markup;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @ModifyVariable(method = "handleDecoratedMessage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private SignedMessage modifyDecoratedMessage(SignedMessage original) {
        String message = original.getContent().getString();
        Text markup = original.getContent();

        if(Configs.enableFilter){
            message = Filter.wordBasedFiltering(message);
        }

        if(Configs.enableAIFilter && Configs.apiProvider.equals("Google")){
            message = Filter.geminiBasedFiltering(message);
        }

        if(Configs.enableMarkup){
            markup = Markup.markup(message);
        }

        return new SignedMessage(
                MessageLink.of(new UUID(0L, 0L)),
                null,
                MessageBody.ofUnsigned(message),
                markup,
                FilterMask.PASS_THROUGH
        );
    }
}
