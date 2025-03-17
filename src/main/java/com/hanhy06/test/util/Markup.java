package com.hanhy06.test.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class Markup {
    public static Text markup(Text str) {
        String content = str.getString();
        MutableText output = Text.literal("");

        StringBuilder buffer = new StringBuilder();

        boolean bold = false;
        boolean italic = false;
        boolean underline = false;

        int i = 0;
        while (i < content.length()) {
            if (i + 1 < content.length() && content.substring(i, i + 2).equals("**")) {
                if (buffer.length() > 0) {
                    output.append(Text.literal(buffer.toString())
                                    .setStyle(Style.EMPTY
                                            .withBold(bold)
                                            .withItalic(italic)
                                            .withUnderline(underline)
                                    )
                    );
                    buffer.setLength(0);
                }
                bold = !bold;
                i += 2;
            } else if (i + 1 < content.length() && content.substring(i, i + 2).equals("__")) {
                if (buffer.length() > 0) {
                    output.append(Text.literal(buffer.toString())
                                    .setStyle(Style.EMPTY
                                            .withBold(bold)
                                            .withItalic(italic)
                                            .withUnderline(underline)
                                    )
                    );
                    buffer.setLength(0);
                }
                underline = !underline;
                i += 2;
            } else if (content.charAt(i) == '_') {
                if (buffer.length() > 0) {
                    output.append(
                            Text.literal(buffer.toString())
                                    .setStyle(Style.EMPTY
                                            .withBold(bold)
                                            .withItalic(italic)
                                            .withUnderline(underline)
                                    )
                    );
                    buffer.setLength(0);
                }
                italic = !italic;
                i++;
            } else {
                buffer.append(content.charAt(i));
                i++;
            }
        }

        if (buffer.length() > 0) {
            output.append(
                    Text.literal(buffer.toString())
                            .setStyle(Style.EMPTY
                                    .withBold(bold)
                                    .withItalic(italic)
                                    .withUnderline(underline)
                            )
            );
        }

        return output;
    }
}


