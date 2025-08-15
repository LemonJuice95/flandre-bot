package io.lemonjuice.flandre_bot;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;

@Log4j2
public class WatchDog implements Runnable {
    private volatile boolean flag = true;
    private int noResponseCounter = 0;

    @Override
    public void run() {
        Thread.currentThread().setName("WatchDog");
        while(true) {
            if(this.flag) {
                this.noResponseCounter = 0;
                this.flag = false;
            } else {
                this.noResponseCounter++;
                log.warn("[WatchDog] 未收到心跳信息，次数: {}", this.noResponseCounter);
                if(this.noResponseCounter >= 5) {
                    log.fatal("[WatchDog] 多次心跳丢失，正在停止应用...");
                    int exitCode = SpringApplication.exit(FlandreBot.getApplicationContext(), () -> 0);
                    System.exit(exitCode);
                }
            }
            try {
                Thread.sleep(32000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public synchronized void onHeartBeat() {
        this.flag = true;
//        log.info("[WatchDog] 心跳");
    }
}
