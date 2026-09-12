package io.lemonjuice.flandre_bot.chat_bot;

public record ChatBotCachedFile(long expiresAt, String fileId) {
    public boolean isValidNow() {
        return System.currentTimeMillis() / 1000 < this.expiresAt - 30;
    }
}
