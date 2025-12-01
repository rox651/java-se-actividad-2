package com.saborpaisa.crud.ui;

import com.saborpaisa.crud.model.Customer;
import com.saborpaisa.crud.service.CustomerService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class CustomerForm extends JFrame {

    private final CustomerService service = new CustomerService();

    private final JTextField txtId = new JTextField(10);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDocumento = new JTextField(15);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(15);
    private final JTextField txtFecha = new JTextField(10);
    private final JTextField txtPuntos = new JTextField(5);
    private final JCheckBox chkFrecuente = new JCheckBox("¿Es frecuente?");

    private final JButton btnInsert = new JButton("Insertar");
    private final JButton btnUpdate = new JButton("Actualizar");
    private final JButton btnDelete = new JButton("Eliminar");
    private final JButton btnSelect = new JButton("Buscar ID");
    private final JButton btnClear = new JButton("Limpiar");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Documento", "Correo", "Teléfono", "Frecuente", "Fecha", "Puntos"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public CustomerForm() {
        super("CRUD Clientes - Unidad 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(buildFormPanel(), BorderLayout.WEST);
        add(buildTablePanel(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        txtId.setEditable(false);
        txtId.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateInsertButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateInsertButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateInsertButtonState();
            }
        });
        updateInsertButtonState();
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.ipadx = 5;
        gbc.ipady = 5;

        panel.add(label("ID (auto)"), gbc);
        gbc.gridy++;
        panel.add(txtId, gbc);

        gbc.gridy++;
        panel.add(label("Nombre completo"), gbc);
        gbc.gridy++;
        panel.add(txtNombre, gbc);

        gbc.gridy++;
        panel.add(label("Documento"), gbc);
        gbc.gridy++;
        panel.add(txtDocumento, gbc);

        gbc.gridy++;
        panel.add(label("Correo"), gbc);
        gbc.gridy++;
        panel.add(txtCorreo, gbc);

        gbc.gridy++;
        panel.add(label("Teléfono"), gbc);
        gbc.gridy++;
        panel.add(txtTelefono, gbc);

        gbc.gridy++;
        panel.add(label("Fecha inscripción (YYYY-MM-DD)"), gbc);
        gbc.gridy++;
        panel.add(txtFecha, gbc);

        gbc.gridy++;
        panel.add(label("Puntos acumulados"), gbc);
        gbc.gridy++;
        panel.add(txtPuntos, gbc);

        gbc.gridy++;
        panel.add(chkFrecuente, gbc);

        gbc.gridy++;
        panel.add(buildButtonRow(), gbc);

        panel.setPreferredSize(new Dimension(320, 500));
        return panel;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new GridLayout(3, 2, 8, 8));
        row.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnInsert.addActionListener(e -> handleInsert());

        btnUpdate.addActionListener(e -> handleUpdate());

        btnDelete.addActionListener(e -> handleDelete());

        btnSelect.addActionListener(e -> handleSelect());

        btnClear.addActionListener(e -> clearForm());

        row.add(btnInsert);
        row.add(btnUpdate);
        row.add(btnDelete);
        row.add(btnSelect);
        row.add(btnClear);
        row.add(new JLabel());
        return row;
    }

    private JScrollPane buildTablePanel() {
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    populateFormFromRow(row);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Clientes en BD"));
        return scrollPane;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private void handleInsert() {
        try {
            Customer customer = buildCustomerFromForm(null);
            service.create(customer);
            showMessage("Cliente insertado con ID " + customer.getId());
            refreshTable();
            clearForm();
        } catch (NumberFormatException ex) {
            showError("Error al insertar: formato numérico inválido (" + ex.getMessage() + ")");
        } catch (SQLException | IllegalArgumentException | DateTimeParseException ex) {
            showError("Error al insertar: " + ex.getMessage());
        }
    }

    private void handleUpdate() {
        String idText = txtId.getText();
        if (idText.isBlank()) {
            showError("Seleccione un cliente para actualizar");
            return;
        }
        try {
            Customer customer = buildCustomerFromForm(Integer.parseInt(idText));
            boolean updated = service.update(customer);
            if (updated) {
                showMessage("Cliente actualizado");
                refreshTable();
            } else {
                showError("No se encontró el cliente");
            }
        } catch (NumberFormatException ex) {
            showError("Error al actualizar: formato numérico inválido (" + ex.getMessage() + ")");
        } catch (SQLException | IllegalArgumentException | DateTimeParseException ex) {
            showError("Error al actualizar: " + ex.getMessage());
        }
    }

    private void handleDelete() {
        String idText = txtId.getText();
        if (idText.isBlank()) {
            showError("Seleccione un cliente para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar cliente " + idText + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean deleted = service.delete(Integer.parseInt(idText));
            if (deleted) {
                showMessage("Cliente eliminado");
                refreshTable();
                clearForm();
            } else {
                showError("No se encontró el cliente");
            }
        } catch (SQLException | NumberFormatException ex) {
            showError("Error al eliminar: " + ex.getMessage());
        }
    }

    private void handleSelect() {
        String idText = JOptionPane.showInputDialog(this, "Ingrese ID de cliente:");
        if (idText == null || idText.isBlank()) {
            return;
        }
        try {
            Optional<Customer> customer = service.findCustomer(Integer.parseInt(idText));
            if (customer.isPresent()) {
                populateForm(customer.get());
            } else {
                showError("No se encontró el cliente");
            }
        } catch (SQLException | NumberFormatException ex) {
            showError("Error al buscar: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            try {
                List<Customer> customers = service.listCustomers();
                for (Customer customer : customers) {
                    tableModel.addRow(new Object[]{
                            customer.getId(),
                            customer.getNombreCompleto(),
                            customer.getDocumentoIdentidad(),
                            customer.getCorreo(),
                            customer.getTelefono(),
                            customer.isEsFrecuente(),
                            customer.getFechaInscripcion(),
                            customer.getPuntosAcumulados()
                    });
                }
            } catch (SQLException e) {
                showError("No fue posible cargar los clientes: " + e.getMessage());
            }
        });
    }

    private void clearForm() {
        txtId.setText("");
        txtNombre.setText("");
        txtDocumento.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtFecha.setText("");
        txtPuntos.setText("");
        chkFrecuente.setSelected(false);
        table.clearSelection();
        updateInsertButtonState();
    }

    private void populateFormFromRow(int row) {
        txtId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtNombre.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtDocumento.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtCorreo.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtTelefono.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        Object frecuente = tableModel.getValueAt(row, 5);
        if (frecuente instanceof Boolean b) {
            chkFrecuente.setSelected(b);
        } else {
            chkFrecuente.setSelected(Boolean.parseBoolean(String.valueOf(frecuente)));
        }
        Object fecha = tableModel.getValueAt(row, 6);
        txtFecha.setText(fecha == null ? "" : fecha.toString());
        txtPuntos.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private void populateForm(Customer customer) {
        txtId.setText(customer.getId() == null ? "" : customer.getId().toString());
        txtNombre.setText(customer.getNombreCompleto());
        txtDocumento.setText(customer.getDocumentoIdentidad());
        txtCorreo.setText(customer.getCorreo());
        txtTelefono.setText(customer.getTelefono());
        txtFecha.setText(customer.getFechaInscripcion() == null ? "" : customer.getFechaInscripcion().toString());
        txtPuntos.setText(String.valueOf(customer.getPuntosAcumulados()));
        chkFrecuente.setSelected(customer.isEsFrecuente());
        updateInsertButtonState();
    }

    private Customer buildCustomerFromForm(Integer id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setNombreCompleto(txtNombre.getText());
        customer.setDocumentoIdentidad(txtDocumento.getText());
        customer.setCorreo(txtCorreo.getText());
        customer.setTelefono(txtTelefono.getText());
        customer.setEsFrecuente(chkFrecuente.isSelected());
        String fechaText = txtFecha.getText();
        if (!fechaText.isBlank()) {
            customer.setFechaInscripcion(LocalDate.parse(fechaText));
        }
        String puntosText = txtPuntos.getText();
        customer.setPuntosAcumulados(puntosText.isBlank() ? 0 : Integer.parseInt(puntosText));
        return customer;
    }

    private void updateInsertButtonState() {
        boolean hasId = !txtId.getText().isBlank();
        btnInsert.setEnabled(!hasId);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

