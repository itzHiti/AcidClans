package kz.itzHiti.clans.mysql;

import org.bukkit.*;
import net.md_5.bungee.api.*;
import kz.itzHiti.clans.*;
import org.bukkit.ChatColor;

import java.sql.*;

public class MySQL
{
    private String table;
    private String password;
    private int port;
    private String username;
    private String host;
    private Connection connection;

    public MySQL(final String host, final String username, final int port, final String password, final String table) {
        this.host = host;
        this.username = username;
        this.port = port;
        this.password = password;
        this.table = table;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex2) {
            final ReflectiveOperationException ex = ex2;
            final ReflectiveOperationException e = ex;
            e.printStackTrace();
        }
        this.execute("CREATE TABLE IF NOT EXISTS `AcidClans` (`id` int NOT NULL AUTO_INCREMENT, `clanname` varchar(255) NOT NULL UNIQUE, `owner` varchar(255) NOT NULL, `welcome` varchar(255) NOT NULL, `data` varchar(255) NOT NULL, `members` varchar(255) NOT NULL, `moders` varchar(255) NOT NULL,`bank` INT NOT NULL, `home` varchar(255) NOT NULL, PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8 COLLATE utf8_bin AUTO_INCREMENT=0");
        this.getConnection();
    }

    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed() && !this.connection.isValid(20)) {
                return this.connection;
            }
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.table + "?autoReconnect=true", this.username, this.password);
        }
        catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Manager.getInstance().getName() + "] MySQL error in the config.yml!");
        }
        return this.connection;
    }

    public void execute(final String query) {
        try {
            final PreparedStatement statement = this.getConnection().prepareStatement(query);
            statement.execute();
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            this.getConnection().close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

