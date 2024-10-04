package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.reflections.Reflections;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.annotations.Item;
import xyz.velocity.modules.customitems.items.AbstractItem;

public class ItemManager {

    public static final Object2ObjectMap<String, AbstractItem> itemList = new Object2ObjectOpenHashMap<>();
    private ObjectSet<Class<? extends AbstractItem>> classes;

    public ItemManager() {

        Reflections reflection = new Reflections();
        this.classes = new ObjectLinkedOpenHashSet<>();

        for (Class<?> aClass : reflection.getTypesAnnotatedWith(Item.class)) {
            try {
                this.classes.add((Class<? extends AbstractItem>) aClass);
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
                AbstractItem item = aClass.newInstance();

                this.itemList.put(item.getName(), item);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        });

        PartnerItemsConfig.getInstance().saveConfig();

    }

}
