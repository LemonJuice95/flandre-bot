package io.lemonjuice.flandre_bot.message;

public class Message {
    public long selfId;
    public long userId;
    public long groupId;
    public long targetId;

    public int time;
    public int messageId;
    public int realId;
    public String realSeq;
    public String type;
    public String subType;
    public Sender sender;

    public int font;
    public String format;

    public String message;
    public String rawMessage;

    public static class Sender {
        public long userId;
        public String nickName;
        public String card;
        public String role;
    }
}
