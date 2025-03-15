package com.hanhy06.test;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class Markup {
    public static Text markup(Text str) {
        String content = str.getString();
        // 결과를 누적할 Text
        MutableText output = Text.literal("");

        // 현재까지 쌓인 문자를 잠시 저장할 버퍼
        StringBuilder buffer = new StringBuilder();

        // 마크업 상태(볼드, 이탤릭, 언더라인) 토글용
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;

        int i = 0;
        while (i < content.length()) {
            // 먼저 "**"인지 확인
            if (i + 1 < content.length() && content.substring(i, i + 2).equals("**")) {
                // 현재까지 쌓인 문자를 이전 상태로 출력
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
                // 볼드 토글
                bold = !bold;
                i += 2;

                // 그 다음 "__"인지 확인
            } else if (i + 1 < content.length() && content.substring(i, i + 2).equals("__")) {
                // 현재까지 쌓인 문자를 이전 상태로 출력
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
                // 언더라인 토글
                underline = !underline;
                i += 2;

                // 단일 '_'인지 확인
            } else if (content.charAt(i) == '_') {
                // 현재까지 쌓인 문자를 이전 상태로 출력
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
                // 이탤릭 토글
                italic = !italic;
                i++;

                // 위 세 가지 케이스가 아니면 일반 문자
            } else {
                buffer.append(content.charAt(i));
                i++;
            }
        }

        // 마지막으로 남아있는 버퍼가 있으면 마저 출력
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


