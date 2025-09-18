package io.lemonjuice.flandre_bot.events;

import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.request.GroupRequestEvent;
import io.lemonjuice.flandre_bot_framework.model.request.GroupRequest;

@EventSubscriber
public class RequestEvents {
    @SubscribeEvent
    public void onGroupInvite(GroupRequestEvent.Invite event) {
        GroupRequest request = event.getRequest();
        if(request.userId == 1582017385L) {
            request.accept();
        } else {
            request.deny("芙兰不想被别人拉进群诶……");
        }
    }
}
