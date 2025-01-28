package model;

public class Language {
    private int id;
    private String name;
    private int countryId;

    public Language(int id, String name, int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public int getCountryId() { return countryId; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCountryId(int countryId) { this.countryId = countryId; }
}