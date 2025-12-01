package com.saborpaisa.crud.dao;

import com.saborpaisa.crud.config.DatabaseConfig;
import com.saborpaisa.crud.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDao {

    private static final String BASE_SELECT = """
            SELECT id,
                   nombre_completo,
                   documento_identidad,
                   correo,
                   telefono,
                   es_frecuente,
                   fecha_inscripcion,
                   puntos_acumulados,
                   created_at
            FROM customer
            """;

    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(BASE_SELECT + " ORDER BY id")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        }
        return customers;
    }

    public Optional<Customer> findById(int id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(BASE_SELECT + " WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        }
    }

    public Customer insert(Customer customer) throws SQLException {
        String sql = """
                INSERT INTO customer (nombre_completo, documento_identidad, correo, telefono,
                                      es_frecuente, fecha_inscripcion, puntos_acumulados)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id, created_at
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(customer, statement);
            statement.setInt(7, customer.getPuntosAcumulados());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                customer.setId(rs.getInt("id"));
                customer.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            }
            return customer;
        }
    }

    public boolean update(Customer customer) throws SQLException {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("El cliente debe tener ID para actualizar");
        }
        String sql = """
                UPDATE customer
                SET nombre_completo = ?,
                    documento_identidad = ?,
                    correo = ?,
                    telefono = ?,
                    es_frecuente = ?,
                    fecha_inscripcion = ?,
                    puntos_acumulados = ?
                WHERE id = ?
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(customer, statement);
            statement.setInt(7, customer.getPuntosAcumulados());
            statement.setInt(8, customer.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private void fillStatement(Customer customer, PreparedStatement statement) throws SQLException {
        statement.setString(1, customer.getNombreCompleto());
        statement.setString(2, customer.getDocumentoIdentidad());
        statement.setString(3, customer.getCorreo());
        statement.setString(4, customer.getTelefono());
        statement.setBoolean(5, customer.isEsFrecuente());
        if (customer.getFechaInscripcion() != null) {
            statement.setObject(6, customer.getFechaInscripcion());
        } else {
            statement.setNull(6, java.sql.Types.DATE);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setNombreCompleto(rs.getString("nombre_completo"));
        customer.setDocumentoIdentidad(rs.getString("documento_identidad"));
        customer.setCorreo(rs.getString("correo"));
        customer.setTelefono(rs.getString("telefono"));
        customer.setEsFrecuente(rs.getBoolean("es_frecuente"));
        Instant createdAt = rs.getTimestamp("created_at").toInstant();
        customer.setCreatedAt(createdAt);
        java.sql.Date fecha = rs.getDate("fecha_inscripcion");
        if (fecha != null) {
            customer.setFechaInscripcion(fecha.toLocalDate());
        }
        customer.setPuntosAcumulados(rs.getInt("puntos_acumulados"));
        return customer;
    }
}

