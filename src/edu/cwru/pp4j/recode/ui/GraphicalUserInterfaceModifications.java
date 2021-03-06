/* Copyright 2018 Case Western Reserve University
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.cwru.pp4j.recode.ui;

import java.io.PipedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * User interface to edit modifications in the file format required by 
 * (Phospho-)MSGF+.
 * 
 * @author Sean Maxwell
 */
public class GraphicalUserInterfaceModifications extends javax.swing.JFrame {
    private PipedOutputStream out;
    private HashMap<String,ModDbEntry> modDb;
    
    public class ModDbEntry {
        public String name;
        public String aa;
        public String composition;
        public String variable;
        public String position;
        
        public ModDbEntry(String compArg, String aaArg, String varArg, String posArg, String nameArg) {
            name = nameArg;
            aa = aaArg;
            composition = compArg;
            variable = varArg;
            position = posArg;
        }
    }
    
    private boolean loadDefaultModificationsDatabase() {
        try(BufferedReader br = new BufferedReader(new FileReader("data/default-mods.txt"))) {
            String line;
            while((line=br.readLine()) != null) {
                String[] tokens = line.split("\t");
                modDb.put(tokens[0], new ModDbEntry(tokens[1],tokens[2],tokens[3],tokens[4],tokens[5]));
            }
            br.close();
            return true;
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
    }
    
    private void updateForm(String modId) {
        ModDbEntry modification = modDb.get(modId);
        modificationsAminoAcidTextField.setText(modification.aa);
        modificationsPositionDropDown.setSelectedItem(modification.position);
        modificationsIsVariableCheckbox.setSelected(modification.variable.equals("opt"));
        modificationsCompositionTextField.setText(modification.composition);
        modificationsNameTextField.setText(modification.name);
    }
    
    /**
     * Creates new form GraphicalUserInterfaceModifications.
     * @param pos Stream to write configured modification to before close.
     */
    public GraphicalUserInterfaceModifications(PipedOutputStream pos) {
        modDb = new HashMap<>();
        out = pos;
        
        initComponents();
      
        loadDefaultModificationsDatabase();
        modificationPredefinedDropDown.removeAllItems();
        for(String item : modDb.keySet()) {
            modificationPredefinedDropDown.addItem(item);
        }
        
        updateForm("Phosphorylation");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modificationPredefinedDropDown = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        modificationsAminoAcidTextField = new javax.swing.JTextField();
        modificationsCompositionTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        modificationsIsVariableCheckbox = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        modificationsPositionDropDown = new javax.swing.JComboBox();
        modificationsOkButton = new javax.swing.JButton();
        modificationsCancelButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        modificationsNameTextField = new javax.swing.JTextField();

        setTitle(" Modification Properties");

        modificationPredefinedDropDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificationPredefinedDropDownActionPerformed(evt);
            }
        });

        jLabel1.setText("Amino acids");

        jLabel3.setText("Composition");

        jLabel2.setText("Variable?");

        jLabel4.setText("Position");

        modificationsPositionDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any", "N-term", "C-term" }));

        modificationsOkButton.setText("OK");
        modificationsOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificationsOkButtonActionPerformed(evt);
            }
        });

        modificationsCancelButton.setText("Cancel");
        modificationsCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificationsCancelButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Name");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(modificationPredefinedDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(modificationsOkButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(modificationsAminoAcidTextField)
                            .addComponent(modificationsCompositionTextField)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(modificationsIsVariableCheckbox)
                                    .addComponent(modificationsPositionDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(modificationsCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 131, Short.MAX_VALUE))
                            .addComponent(modificationsNameTextField))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(modificationPredefinedDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(modificationsAminoAcidTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(modificationsCompositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(modificationsIsVariableCheckbox))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(modificationsPositionDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(modificationsNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modificationsOkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modificationsCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void modificationPredefinedDropDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificationPredefinedDropDownActionPerformed
        String selectedMod = (String)modificationPredefinedDropDown.getSelectedItem();
        updateForm(selectedMod);
    }//GEN-LAST:event_modificationPredefinedDropDownActionPerformed

    private void modificationsOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificationsOkButtonActionPerformed
        String variable = modificationsIsVariableCheckbox.isSelected() ? "opt" : "fix";
        String response = modificationsCompositionTextField.getText();
        response += ","+modificationsAminoAcidTextField.getText();
        response += ","+variable;
        response += ","+modificationsPositionDropDown.getSelectedItem().toString();
        response += ","+modificationsNameTextField.getText();
        response += "\n";
        try {
            out.write(response.getBytes());
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }   
        this.setVisible(false);
    }//GEN-LAST:event_modificationsOkButtonActionPerformed

    private void modificationsCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificationsCancelButtonActionPerformed
        try {
            out.write("none\n".getBytes());
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
        this.setVisible(false);
    }//GEN-LAST:event_modificationsCancelButtonActionPerformed

    /* Create and display the form */
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphicalUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphicalUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphicalUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphicalUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               (new GraphicalUserInterfaceModifications(null)).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox modificationPredefinedDropDown;
    private javax.swing.JTextField modificationsAminoAcidTextField;
    private javax.swing.JButton modificationsCancelButton;
    private javax.swing.JTextField modificationsCompositionTextField;
    private javax.swing.JCheckBox modificationsIsVariableCheckbox;
    private javax.swing.JTextField modificationsNameTextField;
    private javax.swing.JButton modificationsOkButton;
    private javax.swing.JComboBox modificationsPositionDropDown;
    // End of variables declaration//GEN-END:variables
}
