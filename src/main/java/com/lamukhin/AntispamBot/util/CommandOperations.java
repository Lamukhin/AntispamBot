package com.lamukhin.AntispamBot.util;

import org.telegram.telegrambots.meta.api.objects.User;

public class CommandOperations {


    public static String invokeFullNameFromUser(User forwardedUser) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(forwardedUser.getFirstName());
        String lastName = forwardedUser.getLastName();
        if (lastName != null) {
            stringBuilder.append(" ");
            stringBuilder.append(lastName);
        }
        return stringBuilder.toString();
    }
}
