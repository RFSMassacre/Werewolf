package us.rfsmassacre.HeavenLib.BaseManagers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public abstract class YamlStorage<T>
{
    protected JavaPlugin plugin;
    protected File folder;
    protected String folderName;

    /**
     * Constructor for YamlManager.
     * @param plugin Plugin where files will be for.
     * @param folderName Name of folder.
     */
    public YamlStorage(JavaPlugin plugin, String folderName)
    {
        this.plugin = plugin;
        this.folderName = folderName;
        this.folder = new File(plugin.getDataFolder().getPath() + "/" + folderName);
        if (!folder.exists())
        {
            folder.mkdirs();
        }
    }

    /**
     * Read from file and convert into whatever data or object needed.
     * @param fileName Name of file.
     * @return Data or object read from the file.
     */
    public T read(String fileName)
    {
        File file = getFile(fileName);
        return read(file);
    }

    public T read(File file)
    {
        if (file.exists())
        {
            return load(YamlConfiguration.loadConfiguration(file));
        }

        return null;
    }

    /**
     * Read object from file asynchronously.
     *
     * @param fileName Name of file.
     * @param callback Runnable that accepts object.
     */
    public void readAsync(String fileName, Consumer<T> callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(read(fileName)));
    }

    public void readAsync(File file, Consumer<T> callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(read(file)));
    }

    /**
     * Copy a new file with format.
     * @param fileName Name of file.
     * @param overwrite Make new file over already existing file.
     */
    public void copy(String fileName, boolean overwrite)
    {
        //Do nothing. There is no default storage.
    }

    /**
     * Write data of object into the file.
     * @param fileName Name of file.
     * @param t Generic type.
     */
    public void write(String fileName, T t)
    {
        try
        {
            save(t).save(getFile(fileName));
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Write object to file asynchronously.
     *
     * @param fileName Name of file.
     * @param t Generic type.
     */
    public void writeAsync(String fileName, T t)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> write(fileName, t));
    }

    /**
     * Delete specified file.
     * @param fileName Name of file.
     */
    public void delete(String fileName)
    {
        File file = getFile(fileName);
        if (file.exists())
        {
            file.delete();
        }
    }

    /**
     * Delete specified file asynchronously.
     *
     * @param fileName Name of file.
     */
    public void deleteAsync(String fileName)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> delete(fileName));
    }

    /**
     * Retrieve file object from file name.
     * @param fileName Name of file.
     * @return File object.
     */
    public File getFile(String fileName)
    {
        return new File(folder.getPath() + "/" + fileName + (fileName.endsWith(".yml") ? "" : ".yml"));
    }

    public File[] getFiles()
    {
        return folder.listFiles();
    }

    public abstract T load(YamlConfiguration configuration);

    public abstract YamlConfiguration save(T t);
}
