package kz.itzHiti.clans;

import java.util.*;

public class Request
{
    private String player1;
    private String player2;
    private long time;
    public static ArrayList<Request> requests;

    public Request(final String player1, final String player2, final long time) {
        this.player1 = player1;
        this.player2 = player2;
        this.time = time;
    }

    public String getPlayer1() {
        return this.player1;
    }

    public String getPlayer2() {
        return this.player2;
    }

    public long getTime() {
        return this.time;
    }

    public void remove() {
        Request.requests.remove(this);
    }

    public static Request isTo(final String pl) {
        for (final Request req : Request.requests) {
            if (req.getPlayer2().equals(pl)) {
                return req;
            }
        }
        return null;
    }

    public static Request isFrom(final String pl) {
        for (final Request req : Request.requests) {
            if (req.getPlayer1().equals(pl)) {
                return req;
            }
        }
        return null;
    }

    public static void send(final String player1, final String player2) {
        Request.requests.add(new Request(player1, player2, System.currentTimeMillis()));
    }

    static {
        Request.requests = new ArrayList<Request>();
    }
}
