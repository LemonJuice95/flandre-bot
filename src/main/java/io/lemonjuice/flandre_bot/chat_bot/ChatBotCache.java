package io.lemonjuice.flandre_bot.chat_bot;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatBotCache {
    private final int maxSize;
    private final Queue<ChatBotMessage> messages;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ChatBotCache(int maxSize) {
        this.maxSize = maxSize;
        this.messages = new ArrayDeque<>(maxSize + 5);
    }

    public void pushBack(ChatBotMessage... messages) {
        try {
            lock.writeLock().lock();
            this.messages.addAll(Arrays.asList(messages));
            while(this.messages.size() > maxSize) {
                this.messages.poll();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void pushBack(ChatBotMessage message) {
        try {
            lock.writeLock().lock();
            this.messages.add(message);
            while (this.messages.size() > maxSize) {
                this.messages.poll();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<ChatBotMessage> getMessages() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(this.messages);
        } finally {
            lock.readLock().unlock();
        }
    }
}
