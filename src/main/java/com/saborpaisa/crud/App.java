package com.saborpaisa.crud;

import com.saborpaisa.crud.ui.CustomerForm;

import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerForm().setVisible(true));
    }
}

