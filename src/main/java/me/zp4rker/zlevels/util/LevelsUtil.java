package me.zp4rker.zlevels.util;

import me.zp4rker.zlevels.db.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author ZP4RKER
 */
public class LevelsUtil {

    public static long xpToNextLevel(int level) {
        return 5 * (((long) Math.pow(level, 2)) + 10 * level + 20);
    }

    private static long levelsToXp(int levels) {
        long xp = 0;

        for (int level = 0; level <= levels; level++) {
            xp += xpToNextLevel(level);
        }

        return xp;
    }

    public static int xpToLevels(long totalXp) {
        boolean calculating = true;
        int level = 0;

        while (calculating) {
            long xp = levelsToXp(level);

            if (totalXp < xp) {
                calculating = false;
            } else {
                level++;
            }
        }

        return level;
    }

    public static long remainingXp(long totalXp) {
        int level = xpToLevels(totalXp);

        if (level == 0) return totalXp;

        long xp = levelsToXp(level);

        return totalXp - xp + xpToNextLevel(level);
    }

    public static int randomXp(int min, int max) {
        Random random = new Random();

        return random.nextInt((max - min) + 1) + min;
    }

    public static int getPageCount() {
        return UserData.getAllData().size() / 10;
    }

    public static List<UserData> getPage(int index) {
        List<UserData> wholeList = UserData.getAllData();

        index = (index - 1) * 10;

        List<UserData> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if (i == index * 10 && i > 0) {
                dataList.add(wholeList.get(index - 1));
                continue;
            }
            dataList.add(wholeList.get(index));
            index++;
        }

        return dataList;
    }

}
