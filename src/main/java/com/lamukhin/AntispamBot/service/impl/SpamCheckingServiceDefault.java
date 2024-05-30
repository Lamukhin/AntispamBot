package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//@Service
public class SpamCheckingServiceDefault implements SpamCheckingService {

    private final Logger log = LoggerFactory.getLogger(SpamCheckingServiceDefault.class);

    private final ExecutorService executorService =
            Executors.newFixedThreadPool(10, r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });

    private Set<String> dictionary = new HashSet<>();

    @Override
    public void checkUpdate(Update update, TelegramLongPollingEngine engine) {
        AtomicInteger atomicCounter = new AtomicInteger();
        dictionary.add("sex");
        if ((update.hasMessage())&&(update.getMessage().hasText())) {
            String incomeMessage = update.getMessage().getText();
            incomeMessage = incomeMessage.replaceAll("[^a-zA-Zа-яА-Я0-9\s]", "");
            String[] wordsOfMessage = incomeMessage.split(" ");
            List<Search> tasks = new ArrayList<>();
            for (String s : wordsOfMessage) {
                Search search = new Search(s);
                tasks.add(search);
            }

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    tasks.get(0),
                    executorService
            );



            try {
                Boolean hasFound = executorService.invokeAny(tasks);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            log.warn("atomicCounter {}", atomicCounter.get());
            if (atomicCounter.get() >= 3){
                //условно бан
                var send = DeleteMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .messageId(update.getMessage().getMessageId())
                        .build();
                engine.executeNotException(send);

            }

        }
    }

    private class Search implements Runnable{
        private final String word;
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        private Search(String word){
            this.word = word;
        }
        @Override
        public void run() {
            try {
                if (dictionary.contains(word)){
                    log.warn("IT IS SPAM! Word \"{}\" is banned", word);
                }
            } catch (Exception ex){
                log.error("Exception during executing the search callable method: {}", ex.getMessage());
            }
        }
    }

    //me smart, me dymatb
    @PreDestroy
    private void destroyExecutorService(){
        executorService.shutdown();
    }
}
