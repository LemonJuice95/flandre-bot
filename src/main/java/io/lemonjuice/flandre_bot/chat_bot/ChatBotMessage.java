package io.lemonjuice.flandre_bot.chat_bot;

import java.util.List;

public class ChatBotMessage {
    public final Role role;
    public final Body message;

    public ChatBotMessage(Role role, Body message) {
        this.role = role;
        this.message = message;
    }

    public enum Role {
        USER,
        ASSISTANT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public record Body(String text, List<String> fileIds) {
        public Body(String text) {
            this(text, null);
        }
    }
}
