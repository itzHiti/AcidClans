package kz.itzHiti.clans.api;


import org.bukkit.*;

public final class LocationUtil
{
    public static Location toLocation(final String s) {
        Location loc = null;
        try {
            final String[] split = s.split(";");
            final World world = Bukkit.getWorld(split[0]);
            final double x = Double.parseDouble(split[1]);
            final double y = Double.parseDouble(split[2]);
            final double z = Double.parseDouble(split[3]);
            final float yaw = Float.parseFloat(split[4]);
            final float pitch = Float.parseFloat(split[5]);
            loc = new Location(world, x, y, z, yaw, pitch);
        }
        catch (Exception ex) {}
        return loc;
    }

    public static String fromLocation(final Location location) {
        String loc = null;
        try {
            final String world = location.getWorld().getName();
            final double x = location.getX();
            final double y = location.getY();
            final double z = location.getZ();
            final float yaw = location.getYaw();
            final float pitch = location.getPitch();
            loc = world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
        }
        catch (Exception ex) {}
        return loc;
    }
}