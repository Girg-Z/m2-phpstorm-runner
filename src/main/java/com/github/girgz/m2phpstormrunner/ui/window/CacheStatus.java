package com.github.girgz.m2phpstormrunner.ui.window;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class CacheStatus extends DialogWrapper {
    private final Map<String, Boolean> cacheStatusMap;
    private JPanel panel1;
    private JTable table1;

    public CacheStatus(Map<String, Boolean> cacheStatusMap) {
        super(true); // use current window as parent
        this.cacheStatusMap = cacheStatusMap;
        setTitle("Cache Management");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }

    private void createUIComponents() {
        String[] header = {"Cache Type", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(cacheStatusMapToModelArray(cacheStatusMap), header);
        table1 = new JBTable(model);
        table1.setDefaultEditor(Object.class, null);

        table1.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table1.getColumn("Action").setCellEditor(new ButtonEditor());
    }

    private String[][] cacheStatusMapToModelArray(Map<String, Boolean> cacheStatusMap) {
        String[][] result = new String[cacheStatusMap.size()][3];

        int i = 0;
        for (var entry : cacheStatusMap.entrySet()) {
            result[i] = new String[]{
                    entry.getKey(),
                    entry.getValue() ? "Enabled" : "Disabled",
                    entry.getValue() ? "Disable" : "Enable"
            };
            i++;
        }
        return result;
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    private static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;

        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == "Enable") ? "Disable" : "Enable";
            button.setText(label);
            return button;
        }

        public Object getCellEditorValue() {
            JOptionPane.showMessageDialog(button, "Click!");
            return label;
        }
    }
}
