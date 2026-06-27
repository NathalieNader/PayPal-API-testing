package com.paypal.subscription.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
    public static String get(String Key){
        return DOTENV.get(Key);
    }
}

