package io.lemonjuice.flandre_bot;

import io.lemonjuice.flan_mai_plugin.MaiPluginInit;
import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import io.lemonjuice.flandre_bot.network.WSReconnect;
import io.lemonjuice.flandre_bot.reference.NetworkRefs;
import io.lemonjuice.flandre_bot.resources.ResourceRegister;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class Start implements CommandLineRunner {

    @Override
    public void run(String... args) {
        MaiPluginInit.init();
        ConfigInit.initConfig();
        ResourceRegister.init();
        WSClientCore.startWatchDog();
        if(!WSClientCore.connect(NetworkRefs.WS_URL, NetworkRefs.WS_TOKEN)) {
            Thread.startVirtualThread(new WSReconnect());
        }
        SQLCore.connect(NetworkRefs.SQL_URL, NetworkRefs.SQL_USERNAME, NetworkRefs.SQL_PASSWORD);
        NicknameManager.init();
    }
}
