package io.lemonjuice.flandre_bot.commands.group.func;

import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("dice")
public class GroupDiceCommand extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile("/rd\\s+(\\S+)");
    private static final Pattern standard = Pattern.compile("^(\\d+)?d\\d+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern fudge2 = Pattern.compile("^(\\d+)?dF(.2)?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern fudge1 = Pattern.compile("^(\\d+)?dF.1$", Pattern.CASE_INSENSITIVE);

    private static final MessagePattern messagePattern = new MessagePattern.Builder()
            .nextNode(AtNode.atBot())
            .nextNode(new RegexNode(commandPattern))
            .build();

    public GroupDiceCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return messagePattern.matcher(this.command.message.trim()).matches();
    }

    @Override
    public void apply() {
        String expression = this.getExpression();
        String result = getResult(expression);
        if(result.isEmpty()) {
            this.command.getContext().replyWithText("诶？骰子表达式好像不对……\n再检查一下吧~");
        } else if(result.equals("too_much")) {
            this.command.getContext().replyWithText("骰子数量太多啦，芙兰会数不过来的……");
        } else {
            this.command.getContext().replyWithText(NicknameManager.getNickname(this.command.userId) + "投掷骰子的结果为：\n" + expression + "=" + result);
        }
    }

    private String getExpression() {
        String message = this.command.message.getSegments().get(1).toString();
        Matcher matcher = commandPattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String getResult(String expression) {
        if(expression.isEmpty()) {
            return "";
        }
        Random random = ThreadLocalRandom.current();

        //优先匹配百分骰
        if(expression.equalsIgnoreCase("d%")) {
            return String.valueOf(random.nextInt(100) + 1);
        }

        //标准骰
        if(standard.matcher(expression).matches()) {
            return this.standardDice(expression, random);
        }

        //fudge/fate骰2
        if(fudge2.matcher(expression).matches()) {
            return this.fudgeDice(expression, random, 2);
        }

        //fudge/fate骰1
        if(fudge1.matcher(expression).matches()) {
            return this.fudgeDice(expression, random, 1);
        }

        return "";
    }

    private String fudgeDice(String expression, Random random, int mode) {
        List<Integer> dice = List.of(1, 1, 0, 0, -1, -1);
        if(mode == 1) {
            dice = List.of(1, 0, 0, 0, 0, -1);
        }

        int diceNum;
        try {
            diceNum = expression.split("dF").length == 0 || expression.split("dF")[0].isEmpty() ? 4 : Integer.parseInt(expression.split("dF")[0]);
        } catch (NumberFormatException e) {
            return "";
        }
        if(diceNum > 999) {
            return "too_much";
        }

        StringBuilder result = new StringBuilder();
        if(diceNum == 1) {
            result.append(dice.get(random.nextInt(dice.size())));
        }
        if(diceNum > 1) {
            int sum = 0;
            result.append("[");
            for(int i = 1; i < diceNum; i++) {
                int value = dice.get(random.nextInt(dice.size()));
                sum += value;
                result.append(value).append(", ");
            }
            int value = dice.get(random.nextInt(dice.size()));
            sum += value;
            result.append(value).append("]=");
            result.append(sum);
        }

        return result.toString();
    }

    private String standardDice(String expression, Random random) {
        String[] params = expression.toLowerCase().split("d");
        StringBuilder result = new StringBuilder();
        if(params.length == 2) {

            if(params[0].isEmpty()) {
                params[0] = "1";
            }
            int diceNum;
            int bound;

            try {
                diceNum = Integer.parseInt(params[0]);
                bound = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                return "";
            }

            if(diceNum > 999) { return "too_much"; }
            if(bound <= 0) { return ""; }
            if(diceNum > 1) {
                result.append("[");
                int sum = 0;
                for (int i = 1; i < diceNum; i++) {
                    int value = random.nextInt(bound) + 1;
                    sum += value;
                    result.append(value).append(", ");
                }
                int value = random.nextInt(bound) + 1;
                sum += value;
                result.append(value).append("]=");
                result.append(sum);
            }
            if(diceNum == 1) {
                int value = random.nextInt(bound) + 1;
                result.append(value);
            }
        }
        return result.toString();
    }


}
