package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.KnownUrls;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class AbstractMenuController {

    private static final Map<String, String> NAV_MENU = new HashMap<>();

    static {
        NAV_MENU.put(KnownUrls.SERVICE, "Services");
        NAV_MENU.put(KnownUrls.CONSUME, "Request API");
        NAV_MENU.put(KnownUrls.REVOKE, "Revoke service");
        NAV_MENU.put(KnownUrls.HOME, "Home");
    }

    private final Map<String, String> menu;

    public AbstractMenuController(String... knownUrls) {
        this.menu = NAV_MENU.entrySet()
                .stream()
                .filter(e -> Arrays.stream(knownUrls).anyMatch(knownUrl -> e.getKey().equals(knownUrl)))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e, m) -> {
                            throw new IllegalArgumentException("Invalid key" + e);
                        },
                        () -> new TreeMap<>((o1, o2) -> {
                            if (KnownUrls.HOME.equals(o1)) return -1;
                            if (KnownUrls.HOME.equals(o2)) return 1;
                            return o1.compareTo(o2);
                        })));
    }

    protected void addMenuOption(String link) {
        menu.put(link, NAV_MENU.getOrDefault(link, ""));
    }

    protected void addMenu(Model model) {
        model.addAttribute("menu", menu);
    }
}
