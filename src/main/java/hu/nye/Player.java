package hu.nye;

import java.util.Objects;

public class Player {
    private final String name;
    private final char token;

    public Player(String name, char token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public char getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return token == player.token && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, token);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", token=" + token +
                '}';
    }
}