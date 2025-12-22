package com.nsteuerberg.personal_bot.utils.common;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Colors {
    public static Color getRandomColor() {
        return new Color(
                ThreadLocalRandom.current().nextInt(256), // Componente R
                ThreadLocalRandom.current().nextInt(256), // Componente G
                ThreadLocalRandom.current().nextInt(256)  // Componente B
        );
    }
}
