package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Random;

public class EnchantUtil {

    private static final ScriptEngineManager mgr = new ScriptEngineManager();
    private static final ScriptEngine engine = mgr.getEngineByName("JavaScript");
    private static Random random = new Random();

    public static Object2ObjectOpenHashMap<String, PotionEffect> preloadedPotions = new Object2ObjectOpenHashMap<>();

    public static String toRomanNumerals(Integer i) {
        if(i > 9999) {
            return i + "";
        }
        String value = String.valueOf(i);
        String finalString = "";
        int size = value.length() - 1;
        String placeOne = "";
        String placeTwo = "";
        String placeThree = "";
        String placeFour = "";
        StringBuilder sb = new StringBuilder();
        for(int check = 0; check <= size; check++) {
            String r = value.charAt(check) + "";
            int mod = Integer.parseInt(r) % 5;
            int div = Integer.parseInt(r) / 5;
            if(check == 0 && value.length() == 4) {
                if(mod == 4) {
                    if(Integer.parseInt(r) == 4) {
                        placeFour = "IV10^3";
                    }else {
                        placeFour = "IX10^3";
                    }
                    sb.append(placeFour);
                    finalString = sb.toString();
                    continue;
                }
                StringBuilder sbM = new StringBuilder();
                if(div == 1) {
                    placeFour = sbM.append("V10^3").toString();
                }
                for(int add = 0; add < mod; add++) {
                    placeFour = sbM.append("M").toString();
                }
                sb.append(placeFour);
                finalString = sb.toString();
            }
            if((check == 1 && value.length() == 4) || (check == 0 && value.length() == 3)) {
                if(mod == 4) {
                    if(Integer.parseInt(r) == 4) {
                        placeThree = "CD";
                    }else {
                        placeThree = "CM";
                    }
                    sb.append(placeThree);
                    finalString = sb.toString();
                    continue;
                }
                StringBuilder sbC = new StringBuilder();
                if(div == 1) {
                    placeThree = sbC.append("D").toString();
                }
                for(int add = 0; add < mod; add++) {
                    placeThree =  sbC.append("C").toString();
                }
                sb.append(placeThree);
                finalString = sb.toString();
            }
            if((check == 2 && value.length() == 4) || (check == 1 && value.length() == 3) || (check == 0 && value.length() == 2)) {
                if(mod == 4) {
                    if(Integer.parseInt(r) == 4) {
                        placeTwo = "XL";
                    }else {
                        placeTwo = "XC";
                    }
                    sb.append(placeTwo);
                    finalString = sb.toString();
                    continue;
                }
                StringBuilder sbX = new StringBuilder();
                if(div == 1) {
                    placeTwo = sbX.append("L").toString();
                }
                for(int add = 0; add < mod; add++) {
                    placeTwo = sbX.append("X").toString();
                }
                sb.append(placeTwo);
                finalString = sb.toString();
            }
            if((check == 3 && value.length() == 4) || (check == 2 && value.length() == 3) || (check == 1 && value.length() == 2) || (check == 0 && value.length() == 1)) {
                if(mod == 4) {
                    if(Integer.parseInt(r) == 4) {
                        placeOne = "IV";
                    }else {
                        placeOne = "IX";
                    }
                    sb.append(placeOne);
                    finalString = sb.toString();
                    continue;
                }
                StringBuilder sbI = new StringBuilder();
                if(div == 1) {
                    placeOne = sbI.append("V").toString();
                }
                for(int add = 0; add < mod; add++) {
                    placeOne = sbI.append("I").toString();
                }
                sb.append(placeOne);
                finalString = sb.toString();

            }
        }
        return finalString;
    }

    public static PotionEffect deserializePotion(String potion) {
        String[] split = potion.split(":");

        PotionEffectType type = PotionEffectType.getByName(split[0]);
        try {
            int durationOrAmplifier = (int) engine.eval(split[1]);

            if (split.length == 2) {
                return new PotionEffect(type, 999999999, durationOrAmplifier);
            } else {
                int amplifier = (int) engine.eval(split[2]);

                return new PotionEffect(type, durationOrAmplifier, amplifier);
            }
        } catch (ScriptException exc) {
            exc.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static PotionEffect deserializePotion(String potion, int duration) {
        String[] split = potion.split(":");

        PotionEffectType type = PotionEffectType.getByName(split[0]);
        try {
            int durationOrAmplifier = (int) engine.eval(split[1]);

            if (split.length == 2) {
                return new PotionEffect(type, duration, durationOrAmplifier);
            } else {
                int amplifier = (int) engine.eval(split[2]);

                return new PotionEffect(type, durationOrAmplifier, amplifier);
            }
        } catch (ScriptException exc) {
            exc.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static PotionEffect getEffect(String effect) {
        if (preloadedPotions.containsKey(effect)) return preloadedPotions.get(effect);

        PotionEffect potionEffect = EnchantUtil.deserializePotion(effect);
        preloadedPotions.put(effect, potionEffect);

        return potionEffect;
    }

    public static PotionEffect getEffect(String effect, int duration) {
        if (preloadedPotions.containsKey(effect)) return preloadedPotions.get(effect);

        PotionEffect potionEffect = EnchantUtil.deserializePotion(effect, duration);
        preloadedPotions.put(effect, potionEffect);

        return potionEffect;
    }

    public static double getRandomDouble() {
        double min = 0.0;
        double max = 100.0;

        return min + (max - min) * random.nextDouble();
    }

    public static double getRandomDouble(int min, int max) {
        return min + (max - min) * random.nextDouble();
    }

}
