/*
 * This file is part of SpaceBukkit (http://spacebukkit.xereo.net/).
 * 
 * SpaceBukkit is free software: you can redistribute it and/or modify it under the terms of the
 * Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license as published by the Creative Common organization,
 * either version 3.0 of the license, or (at your option) any later version.
 * 
 * SpaceRTK is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Attribution-NonCommercial-ShareAlike
 * Unported (CC BY-NC-SA) license for more details.
 * 
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license along with
 * this program. If not, see <http://creativecommons.org/licenses/by-nc-sa/3.0/>.
 */
package me.neatmonster.spacebukkit.plugins;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class PluginsManager {
    public static List<String> pluginsNames = new ArrayList<String>();

    public static File getJAR(final Plugin plugin) {
        Class<?> currentClass = plugin.getClass();
        while (!currentClass.getSimpleName().equalsIgnoreCase("JavaPlugin"))
            currentClass = currentClass.getSuperclass();
        try {
            final Class<?>[] methodArgs = {};
            final Method method = currentClass.getDeclaredMethod("getFile", methodArgs);
            method.setAccessible(true);
            final Object[] classArgs = {};
            return (File) method.invoke(plugin, classArgs);
        } catch (final SecurityException e) {
            e.printStackTrace();
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PluginsManager() {
        new Thread(new PluginsRequester()).start();
        final Configuration configuration = new Configuration(new File("SpaceModule", "jars.yml"));
        configuration.load();
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            final File jar = getJAR(plugin);
            if (jar != null)
                configuration.setProperty(plugin.getDescription().getName().toLowerCase().replace(" ", ""),
                        jar.getName());
        }
        configuration.save();
    }

    public boolean contains(String pluginName) {
        pluginName = pluginName.toLowerCase();
        if (pluginsNames.contains(pluginName))
            return true;
        if (pluginsNames.contains(pluginName.replace(" ", "")))
            return true;
        if (pluginsNames.contains(pluginName.replace(" ", "_")))
            return true;
        if (pluginsNames.contains(pluginName.replace(" ", "-")))
            return true;
        return false;
    }
}