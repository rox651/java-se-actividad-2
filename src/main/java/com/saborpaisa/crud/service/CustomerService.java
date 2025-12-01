package com.saborpaisa.crud.service;

import com.saborpaisa.crud.dao.CustomerDao;
import com.saborpaisa.crud.model.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService() {
        this(new CustomerDao());
    }

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> listCustomers() throws SQLException {
        return customerDao.findAll();
    }

    public Optional<Customer> findCustomer(int id) throws SQLException {
        return customerDao.findById(id);
    }

    public Customer create(Customer customer) throws SQLException {
        validate(customer);
        return customerDao.insert(customer);
    }

    public boolean update(Customer customer) throws SQLException {
        validate(customer);
        return customerDao.update(customer);
    }

    public boolean delete(int id) throws SQLException {
        return customerDao.delete(id);
    }

    private void validate(Customer customer) {
        if (customer.getNombreCompleto() == null || customer.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
    }
}

