package io.lemonjuice.flandre_bot.commands.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.api.auth.MaiMaiProberAuthApi;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

@Log4j2
public class GroupBindDivingFishCommand extends SimpleGroupCommandRunner {
    public GroupBindDivingFishCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    protected String getCommandBody() {
        return "/水鱼授权";
    }

    @Override
    protected boolean needAtFirst() {
        return true;
    }

    @Override
    public void apply() {
        try {
            JSONObject result = MaiMaiProberAuthApi.bindRequest(this.command.userId);
            String url = result.getString("verification_uri_complete");
            int duration = result.getInt("expires_in");
            this.command.getContext().replyWithText(String.format("芙兰看看……好了！在%d秒内点击下面的链接就可以了哦~\n%s", duration, url));
        } catch (Exception e) {
            log.error("发起水鱼授权请求失败！", e);
            this.command.getContext().replyWithText("出错了！抱歉……联系一下bot管理员吧~");
        }
    }
}
