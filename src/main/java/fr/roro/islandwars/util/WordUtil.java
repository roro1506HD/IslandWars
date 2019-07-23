package fr.roro.islandwars.util;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class WordUtil {

    public static boolean isVowel(char character) {
        return "aeiouyâäàêëèéïîôöûüù".indexOf(Character.toLowerCase(character)) != -1;
    }

    public static boolean isVowel(String word) {
        if(isVowel(word.charAt(0)))
            return true;

        return word.charAt(0) == 'h' && isVowel(word.charAt(1));
    }

}
