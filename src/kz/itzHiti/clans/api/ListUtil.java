package kz.itzHiti.clans.api;

import java.util.*;

public final class ListUtil
{
    public static ArrayList<String> toList(final String s) {
        final ArrayList<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
        return list;
    }
}
