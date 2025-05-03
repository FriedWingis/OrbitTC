package me.friedwingis.plugin.orbittc.struct;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.friedwingis.plugin.orbittc.OrbitTC;
import me.friedwingis.plugin.orbittc.utils.Serialization;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Copyright FriedWingis - 2023
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
@AllArgsConstructor
public class ConnectDatabase {
    private final OrbitTC plugin;

    private Connection getSQLConnection() {
        Connection connection;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://");
            return connection;
        } catch (final SQLException | ClassNotFoundException ex) {
            plugin.getLogger().severe("SQL exception on initialize, could not connect to server. Check credentials?");
            ex.printStackTrace();
        }

        return null;
    }

    public void attemptTableCreate(final String table) {
        CompletableFuture.runAsync(() -> {
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = getSQLConnection();
                ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (USER CHAR(36), VALUE TEXT)");

                ps.executeUpdate();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null)
                        ps.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void createNewEntry(final String table, final UUID uuid, final TebexPackage tebexPackage) {
        CompletableFuture.runAsync(() -> {
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = getSQLConnection();
                ps = conn.prepareStatement("INSERT INTO " + table + " VALUES (?,?)");

                ps.setString(1, uuid.toString());
                ps.setString(2, Serialization.serialize(tebexPackage));

                ps.executeUpdate();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null)
                        ps.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void deleteEntry(final String table, final UUID uuid, final TebexPackage tebexPackage) {
        CompletableFuture.runAsync(() -> {
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = getSQLConnection();
                ps = conn.prepareStatement("DELETE FROM " + table + " WHERE USER=? AND VALUE=?");

                ps.setString(1, uuid.toString());
                ps.setString(2, Serialization.serialize(tebexPackage));

                ps.executeUpdate();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null)
                        ps.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public CompletableFuture<List<TebexPackage>> getPackagesFrom(final String table, final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final List<TebexPackage> packages = Lists.newArrayList();

            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = getSQLConnection();
                ps = conn.prepareStatement("SELECT VALUE FROM " + table + " WHERE USER=?");
                ps.setString(1, uuid.toString());

                final ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    packages.add(Serialization.deserialize(rs.getString("VALUE")));
                }

                return packages;
            } catch (final SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null)
                        ps.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return packages;
        });
    }
}
