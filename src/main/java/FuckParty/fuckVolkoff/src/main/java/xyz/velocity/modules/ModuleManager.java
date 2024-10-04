package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.reflections.Reflections;
import xyz.velocity.VelocityFeatures;

import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleManager {

    public static final Object2ObjectOpenHashMap<String, AbstractModule> moduleList = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<String, AbstractModule> placeholders = new Object2ObjectOpenHashMap<>();
    private Set<Class<? extends AbstractModule>> classes;

    public ModuleManager() {

        Reflections reflection = new Reflections();
        this.classes = new LinkedHashSet<>();

        for (Class<?> aClass : reflection.getTypesAnnotatedWith(java.lang.Module.class)) {
            try {
                this.classes.add((Class<? extends AbstractModule>) aClass);
            } catch (ClassCastException ignored) {
                ignored.printStackTrace();
            } catch (Throwable err) {
                err.printStackTrace();
            }
        }

        loadModules();

        VelocityFeatures.registerEvent(new GlobalDamageModifier());
    }

    private void loadModules() {

        this.classes.forEach(aClass -> {

            try {
                AbstractModule module = aClass.newInstance();

                this.moduleList.put(module.getName(), module);

                if (module.isEnabled()) module.onEnable();
            } catch (Throwable e) {
                e.printStackTrace();
            }

        });

    }

}
