package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.reflections.Reflections;
import xyz.velocity.modules.customenchants.annotations.EnchantItem;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.items.AbstractEnchantItem;

public class ItemManager {

    public static final Object2ObjectMap<String, AbstractEnchantItem> itemList = new Object2ObjectOpenHashMap<>();
    private ObjectSet<Class<? extends AbstractEnchantItem>> classes;

    public ItemManager() {

        Reflections reflection = new Reflections();
        this.classes = new ObjectLinkedOpenHashSet<>();

        for (Class<?> aClass : reflection.getTypesAnnotatedWith(EnchantItem.class)) {
            try {
                this.classes.add((Class<? extends AbstractEnchantItem>) aClass);
            } catch (ClassCastException ignored) {

            } catch (Throwable err) {
                err.printStackTrace();
            }
        }

        loadItems();

    }

    private void loadItems() {

        this.classes.forEach(aClass -> {

            try {
                AbstractEnchantItem item = aClass.newInstance();

                this.itemList.put(item.getName(), item);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        });

        CustomEnchantConfig.getInstance().saveConfig();

    }

}
