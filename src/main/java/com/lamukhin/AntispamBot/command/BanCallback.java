package com.lamukhin.AntispamBot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;

import java.util.HashSet;
import java.util.Set;

//@Component
@CommandNames("/yes")
//TODO: доработать либу wdeath, чтобы была возможность работать с сессиями в группах
public class BanCallback {

    public static final String NAME = "judgement";
    private final Logger log = LoggerFactory.getLogger(BanCallback.class);


    private Set<Long> admins = new HashSet<>();

    @CommandFirst
    public void judge(TelegramLongPollingEngine engine,
                      @ParamName("userId") Long userId,
                      CommandContext context){
        admins.add(260113861l);
        log.warn("Забанили чела с айди");
        String data = (String) context.getData();
        if ((admins.contains(userId))&&(data.startsWith("y"))){
            //banim
            String getId = data.split(" ")[1];
            log.warn("Забанили чела с айди{}", getId);

        }

    }
}
