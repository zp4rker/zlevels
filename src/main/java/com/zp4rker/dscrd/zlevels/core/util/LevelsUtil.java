package com.zp4rker.dscrd.zlevels.core.util;

import java.util.Random;

/**
 * @author ZP4RKER
 */
public class LevelsUtil {

    public static long xpToNextLevel(int level) {
        // Return using formula
        return 5 * (((long) Math.pow(level, 2)) + 10 * level + 20);
    }

    private static long levelsToXp(int levels) {
        // Set xp to 0
        long xp = 0;
        // Loop through
        for (int level = 0; level <= levels; level++) {
            xp += xpToNextLevel(level);
        }
        // Return xp
        return xp;
    }

    public static int xpToLevels(long totalXp) {
        // Create boolean
        boolean calculating = true;
        // Create level variable
        int level = 0;
        // Loop
        while (calculating) {
            // Get xp for next level
            long xp = levelsToXp(level);
            // Check if total xp is less
            if (totalXp < xp) {
                // End loop
                calculating = false;
            } else {
                // Increment level
                level++;
            }
        }
        // Return level
        return level;
    }

    public static long remainingXp(long totalXp) {
        // Get level
        int level = xpToLevels(totalXp);
        // Check if level 0
        if (level == 0) return totalXp;
        // Get xp for level
        long xp = levelsToXp(level);
        // Return xp minus totalXp
        return totalXp - xp + xpToNextLevel(level);
    }

    public static int randomXp(int min, int max) {
        // Get random instance
        Random random = new Random();
        // Return random int between 10 and 30
        return random.nextInt((max - min) + 1) + min;
    }

}
