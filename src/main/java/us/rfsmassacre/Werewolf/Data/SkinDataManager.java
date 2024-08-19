package us.rfsmassacre.Werewolf.Data;

import org.bukkit.configuration.file.YamlConfiguration;
import us.rfsmassacre.HeavenLib.BaseManagers.DataManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SkinDataManager extends DataManager<Map<String, String>>
{
    public SkinDataManager(WerewolfPlugin instance)
    {
        super(instance, "skins");
    }

    @Override
    protected void storeData(Map<String, String> oldSkins, YamlConfiguration data) throws Exception
    {
        for (Entry<String, String> entry : oldSkins.entrySet())
        {
            data.set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected Map<String, String> loadData(YamlConfiguration data) throws Exception
    {
        Map<String, String> skins = new HashMap<>();
        for (String key : data.getKeys(false))
        {
            skins.put(key, data.getString(key));
        }
        return skins;
    }
}
