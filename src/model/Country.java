package model;

public class Country {
    private int id;
    private String name;
    private int population;

    public Country(int id, String name, int population) {
        this.id = id;
        this.name = name;
        this.population = population;
    }

    // Сеттер для ID
    public void setId(int id) { this.id = id; }

    // Остальные геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPopulation() { return population; }
}