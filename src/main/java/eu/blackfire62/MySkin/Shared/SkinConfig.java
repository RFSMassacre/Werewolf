/*
 * Decompiled with CFR 0.139.
 */
package eu.blackfire62.MySkin.Shared;

import java.util.ArrayList;
import java.util.List;

public abstract class SkinConfig {
    public boolean MYSQL_ENABLED = false;
    public String MYSQL_HOST = "localhost";
    public String MYSQL_PORT = "3306";
    public String MYSQL_USERNAME = "username";
    public String MYSQL_PASSWORD = "password";
    public String MYSQL_DATABASE = "database";
    public boolean MYSQL_SINGLE_CONNECTION = false;
    public boolean MYSQL_USE_SSL = true;
    public boolean SKINSEARCH_ENABLED = false;
    public int SKINSEARCH_ROWS = 6;
    public int CONFIG_VERSION = 1;
    public boolean DEFAULTSKINS_ENABLED = false;
    public List<String> DEFAULTSKINS = new ArrayList<String>();
    public boolean BLACKLIST_ENABLED = false;
    public List<String> BLACKLIST = new ArrayList<String>();

    public abstract void load();

    public abstract void save();
}

