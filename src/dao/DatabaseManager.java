package dao;

import model.Country;
import model.City;
import model.Language;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "durqcXVM4Q";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found", e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ====================== Country Methods ======================

    public static void addCountry(Country country) throws SQLException {
        String sql = "INSERT INTO countries (id, name, population) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, country.getId());
            stmt.setString(2, country.getName());
            stmt.setInt(3, country.getPopulation());
            stmt.executeUpdate();
        }
    }

    public static List<Country> getAllCountries() throws SQLException {
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT * FROM countries ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                countries.add(new Country(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("population")
                ));
            }
        }
        return countries;
    }

    public static void deleteCountry(int id) throws SQLException {
        String sql = "DELETE FROM countries WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static boolean countryExists(int countryId) throws SQLException {
        String sql = "SELECT 1 FROM countries WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, countryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ====================== City Methods ======================

    public static void addCity(City city) throws SQLException {
        String sql = "INSERT INTO cities (name, population, country_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, city.getName());
            stmt.setInt(2, city.getPopulation());
            stmt.setInt(3, city.getCountryId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    city.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public static List<City> getAllCities() throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT * FROM cities ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("population"),
                        rs.getInt("country_id")
                ));
            }
        }
        return cities;
    }

    public static void deleteCity(int id) throws SQLException {
        String sql = "DELETE FROM cities WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ====================== Language Methods ======================

    public static void addLanguage(Language language) throws SQLException {
        String sql = "INSERT INTO languages (name, country_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, language.getName());
            stmt.setInt(2, language.getCountryId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    language.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public static List<Language> getAllLanguages() throws SQLException {
        List<Language> languages = new ArrayList<>();
        String sql = "SELECT * FROM languages ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                languages.add(new Language(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("country_id")
                ));
            }
        }
        return languages;
    }

    public static void deleteLanguage(int id) throws SQLException {
        String sql = "DELETE FROM languages WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ====================== Helper Methods ======================

    public static List<City> getCitiesByCountry(int countryId) throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT * FROM cities WHERE country_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, countryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cities.add(new City(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("population"),
                            countryId
                    ));
                }
            }
        }
        return cities;
    }

    public static List<Language> getLanguagesByCountry(int countryId) throws SQLException {
        List<Language> languages = new ArrayList<>();
        String sql = "SELECT * FROM languages WHERE country_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, countryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    languages.add(new Language(
                            rs.getInt("id"),
                            rs.getString("name"),
                            countryId
                    ));
                }
            }
        }
        return languages;
    }
}