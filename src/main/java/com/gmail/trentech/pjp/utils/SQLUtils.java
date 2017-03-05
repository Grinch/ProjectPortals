package com.gmail.trentech.pjp.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class SQLUtils {

    protected static String prefix = ConfigManager.get().getConfig().getNode("settings", "sql", "prefix").getString();
    protected static boolean enableSQL = ConfigManager.get().getConfig().getNode("settings", "sql", "enable").getBoolean();
    protected static String url = ConfigManager.get().getConfig().getNode("settings", "sql", "url").getString();
    protected static String username = ConfigManager.get().getConfig().getNode("settings", "sql", "username").getString();
    protected static String password = ConfigManager.get().getConfig().getNode("settings", "sql", "password").getString();
    protected static SqlService sql;

    protected static DataSource getDataSource() throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }

        if (enableSQL) {
            return sql.getDataSource("jdbc:mysql://" + url + "?user=" + username + "&password=" + password);
        } else {
            return sql.getDataSource("jdbc:h2:./config/pjp/data");
        }
    }

    protected static String getPrefix(String table) {
        if (!prefix.equalsIgnoreCase("NONE") && enableSQL) {
            return "`" + prefix + table + "`".toUpperCase();
        }
        return "`" + table + "`".toUpperCase();
    }

    protected static String stripPrefix(String table) {
        if (!prefix.equalsIgnoreCase("NONE") && enableSQL) {
            return table.toUpperCase().replace(prefix.toUpperCase(), "").toUpperCase();
        }
        return table.toUpperCase();
    }

    public static void createTables() {

        try {
            Connection connection = getDataSource().getConnection();

            PreparedStatement statement =
                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + getPrefix("PORTALS") + " (Name TEXT, Data TEXT)");
            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}