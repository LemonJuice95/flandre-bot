package io.lemonjuice.flandre_bot.chat_bot;

public class ChatBotMessage {
    public final Role role;
    public final String message;

    public ChatBotMessage(Role role, String message) {
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
}
