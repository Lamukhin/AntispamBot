package com.lamukhin.AntispamBot.role;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Admins {
    private Set<String> admins = new HashSet<>() {{
        //add("260113861");
    }};

    public Set<String> getSet() {
        return admins;
    }
}
