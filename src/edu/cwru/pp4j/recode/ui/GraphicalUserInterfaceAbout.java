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

/**
 * User interface about window and content.
 * 
 * @author Sean Maxwell
 */
public class GraphicalUserInterfaceAbout extends javax.swing.JFrame {

    /**
     * Creates new form GraphicalUserInterfaceAbout
     */
    public GraphicalUserInterfaceAbout() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About");

        jLabel2.setText("<html>\n<center><strong>PhosphoRecoderSE</strong></center>\n<pre>\nCopyright 2018 Case Western Reserve University\n \nPermission is hereby granted, free of charge, to any person obtaining a copy \nof this software and associated documentation files (the \"Software\"), to deal\nin the Software without restriction, including without limitation the rights \nto use, copy, modify, merge, publish, distribute, sublicense, and/or sell \ncopies of the Software, and to permit persons to whom the Software is \nfurnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in \nall copies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR \nIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, \nFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE \nAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER \nLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\nOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\nSOFTWARE.</pre>\n<hr />\n<br/>\n<center><strong>MS-GF+</strong></center> <pre> <em>Publications:</em> <ul>   <li>MS-GF+: Universal Database Search Tool for Mass Spectrometry, <br/>          Sangtae Kim, Pavel A. Pevzner,Nat Commun. 2014 Oct 31;5:5277.<br/>         doi: 10.1038/ncomms6277.<br/>         http://www.ncbi.nlm.nih.gov/pubmed/?term=25358478</li>   <li>Spectral Probabilities and Generating Functions of Tandem Mass<br/>          Spectra: A Strike against Decoy Databases, Sangtae Kim, Nitin Gupta <br/>          and Pavel Pevzner, J Proteome Res. 2008 Aug;7(8):3354-63. <br/>          doi: 10.1021/pr8001244.<br/>         http://www.ncbi.nlm.nih.gov/pubmed/?term=18597511</li> </ul>  <em>Documentation and Binaries:</em><br/> <a href=\"https://omics.pnl.gov/software/ms-gf\">https://omics.pnl.gov/software/ms-gf</a><br/> <br/> Source Code:<br/> <a href=\"https://github.com/sangtaekim/msgfplus\">https://github.com/sangtaekim/msgfplus</a><br/> <br/> <em>Disclaimer</em><br/> These programs are primarily designed to run on Windows machines. Please use <br/> them at your own risk. This material was prepared as an account of work<br/> sponsored by an agency of the United States Government. Neither the United <br/> States Government nor the United States Department of Energy, nor Battelle, nor<br/> any of their employees, makes any warranty, express or implied, or assumes any<br/> legal liability or responsibility for the accuracy, completeness, or usefulness or any<br/> information, apparatus, product, or process disclosed, or represents that its use<br/> would not infringe privately owned rights.<br/> <br/> Portions of this research were supported by the NIH National Center for Research<br/> Resources (Grant RR018522), the W.R. Wiley Environmental Molecular Science<br/> Laboratory (a national scientific user facility sponsored by the U.S. Department of<br/> Energy's Office of Biological and Environmental Research and located at PNNL),<br/> and the National Institute of Allergy and Infectious Diseases (NIH/DHHS through<br/> interagency agreement Y1-AI-4894-01). PNNL is operated by Battelle Memorial<br/> Institute for the U.S. Department of Energy under contract DE-AC05-76RL0 1830.<br/> <br/> We would like your feedback about the usefulness of the tools and information<br/> provided by the Resource. Your suggestions on how to increase their value to you<br/> will be appreciated. Please e-mail any comments to proteomics@pnl.gov<br/>  </pre> </html>");
        jScrollPane1.setViewportView(jLabel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(GraphicalUserInterfaceAbout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphicalUserInterfaceAbout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphicalUserInterfaceAbout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphicalUserInterfaceAbout.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphicalUserInterfaceAbout().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
