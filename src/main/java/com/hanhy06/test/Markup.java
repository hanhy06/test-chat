package com.hanhy06.test;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markup {
    public static Text markup(Text str) {
        String content = str.getString();
        MutableText output = Text.literal("");
        Pattern boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher boldMatcher = boldPattern.matcher(content);
        int lastEnd = 0;

        while (boldMatcher.find()) {
            if (boldMatcher.start() > lastEnd) {
                output.append(Text.literal(content.substring(lastEnd, boldMatcher.start())));
            }
            String boldText = boldMatcher.group(1);
            output.append(Text.literal(boldText).setStyle(Style.EMPTY.withBold(true)));
            lastEnd = boldMatcher.end();
        }

        if (lastEnd < content.length()) {
            output.append(Text.literal(content.substring(lastEnd)));
        }

        return output;
    }
}
