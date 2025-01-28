package model;

public class City {
    private int id;
    private String name;
    private int population;
    private int countryId;

    public City(int id, String name, int population, int countryId) {
        this.id = id;
        this.name = name;
        this.population = population;
        this.countryId = countryId;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPopulation() { return population; }
    public int getCountryId() { return countryId; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPopulation(int population) { this.population = population; }
    public void setCountryId(int countryId) { this.countryId = countryId; }
}