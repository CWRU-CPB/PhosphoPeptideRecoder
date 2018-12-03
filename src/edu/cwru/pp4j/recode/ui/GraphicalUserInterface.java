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

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.SwingWorker;
import javax.swing.ListModel;

import edu.cwru.pp4j.recode.converters.Mzid2Tdv;
import edu.cwru.pp4j.recode.recoding.RecodeConfig;
import edu.cwru.pp4j.recode.recoding.PhosphorylationSiteRecoder;
import edu.cwru.pp4j.recode.proteins.Fasta;
import edu.cwru.pp4j.recode.modsites.ModificationSiteDatabaseImporter;
import edu.cwru.pp4j.recode.phosmsgf.PhosMsgfConfig;
import edu.cwru.pp4j.recode.phosmsgf.PhosMsgfCommand;
import edu.cwru.pp4j.recode.phosmsgf.PhosMsgfParamEnum;

import edu.ucsd.msjava.msdbsearch.BuildSA;

import java.awt.event.ItemEvent;

/**
 * A basic graphical user interface for  data import, recoding and search.
 * 
 * @author Sean Maxwell
 */
public class GraphicalUserInterface extends javax.swing.JFrame {
    private Integer THREADS = 0;
    public PipedInputStream terminalInput;
    private UncloseablePipedOutputStream threadOutput;
    public PipedInputStream modificationsInput;
    public PipedOutputStream modificationsOutput;
    public GraphicalUserInterfaceModifications modificationsUI;
    public GraphicalUserInterfaceTerminal terminalUI;
    
    private String removeExtension(String s) {
        return s.lastIndexOf('.') > s.lastIndexOf(File.separatorChar) ? s.substring(0,s.lastIndexOf('.')) : s;
    }
    
    private class ProteinSequenceDatabaseFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".fasta") && !name.contains("revCat"));
        }
    }
    
    private class ModificationSiteDatabaseFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".mv.db");
        }
    }
    
    private class ProteinSequenceDatabaseFileChooserFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".fasta") || f.getName().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return("Protein sequence files");
        }
        
    }
    
    private class ModificationSiteDatabaseFileChooserFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return("Modification site files");
        }
        
    }
    
    private class SpectrumFileChooserFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().matches(".*(\\.mzML|\\.mzXML|\\.mgf|\\.ms2|\\.pkl|_dta\\.txt)");
        }

        @Override
        public String getDescription() {
            return("Spectrum files");
        }
        
    }
    
    private class BgProteinSequenceDatabaseImporter extends SwingWorker<String,Object> {
        private final String path;
        private final javax.swing.JLabel status;
        BgProteinSequenceDatabaseImporter(String s, javax.swing.JLabel jl) {
            path = s;
            status = jl;
        }
        
        @Override
        protected String doInBackground() throws Exception {
            String result = "";
            importProteinProgressBar.setVisible(true);
            importProteinProgressBar.setIndeterminate(true);
            importProteinDatabaseButton.setEnabled(false);
            
            try {
                Fasta db = new Fasta(path);
                Path source = Paths.get(path);
                Path destination = Paths.get("./data/protDb/"+source.getFileName().toString());
                Files.copy(source,destination);  
                loadProteinDatabases();
                result = "Import Complete";
            }
            catch(Exception e) {
                result = "Import failed: "+e.getMessage();
            }
            
            importProteinProgressBar.setVisible(false);
            importProteinProgressBar.setIndeterminate(false);
            importProteinDatabaseButton.setEnabled(true);
            return(result);
        }
        
        @Override
        protected void done() {
            try {
                status.setText(get());
            }
            catch(Exception e) {
                status.setText("Import failed: multiple errors");
            }
        }
    }
    
    private class BgModificationSiteDatabaseImporter extends SwingWorker<String,Object> {
        private final String path;
        private final javax.swing.JLabel status;
        BgModificationSiteDatabaseImporter(String s, javax.swing.JLabel jl) {
            path = s;
            status = jl;
        }
        
        @Override
        protected String doInBackground() throws Exception {
            String result = "";
            importModSitesProgressBar.setVisible(true);
            importModSitesProgressBar.setIndeterminate(true);
            importModificationSiteDatabaseButton.setEnabled(false);
                
            try {
                Path source = Paths.get(path);
                Path destination = Paths.get("./data/modDb/"+removeExtension(source.getFileName().toString()));
                ModificationSiteDatabaseImporter.importDatabase(source.toString(),destination.toString());
                loadModificationSiteDatabases();
                result = "Import Complete";
            }
            catch(Exception e) {
                result = "Import failed: "+e.getMessage();
            }
            
            importModSitesProgressBar.setIndeterminate(false);
            importModSitesProgressBar.setVisible(false);
            importModificationSiteDatabaseButton.setEnabled(true);
            return(result);
        }
        
        @Override
        protected void done() {
            try {
                status.setText(get());
            }
            catch(Exception e) {
                status.setText("Import failed: multiple errors");
            }
        }
    }
    
    private class BgRecoder extends SwingWorker<String,RecodeConfig> {
        private final RecodeConfig config;
        BgRecoder(String s, RecodeConfig cfg) {
            config = cfg;
        }
        
        @Override
        protected String doInBackground() throws Exception {
            String result = "";
            recodeProgressBar.setVisible(true);
            recodeProgressBar.setIndeterminate(true);
            recodeButton.setText("Re-coding...");
            recodeButton.setEnabled(false);
                
            try {
                PhosphorylationSiteRecoder recoder = new PhosphorylationSiteRecoder();
                recoder.recode(config);
                loadRecodedDatabases();
                result = "Re-code Complete";
            }
            catch(Exception e) {
                result = "Re-code Failed: "+e.getMessage();
            }
            
            recodeProgressBar.setVisible(false);
            recodeProgressBar.setIndeterminate(false);
            recodeButton.setText("Re-code");
            recodeButton.setEnabled(true);
            return(result);
        }
        
        @Override
        protected void done() {
            try {
                statusLabel.setText(get());
            }
            catch(Exception e) {
                statusLabel.setText("Re-code Failed: multiple errors");
            }
        }
    }
    
    private class BgMsgfRunner extends SwingWorker<String,PhosMsgfConfig> {
        PhosMsgfConfig msgfConfig;
        BgMsgfRunner(String s, PhosMsgfConfig cfg) {
            msgfConfig = cfg;
        }
        
        @Override
        protected String doInBackground() throws Exception {            
            /* Check existence of suffix-array processed database */
            File dbFile = new File(msgfConfig.getConfigValue(PhosMsgfParamEnum.SEQUENCEDATABASE));
            File saFile = new File(removeExtension(dbFile.getPath())+".canno");
            if(!saFile.exists()) {
                statusLabel.setText("Indexing database for faster searching...");
                System.out.printf("Building suffix array for PhosMS-GF+\n");
                BuildSA.buildSA(dbFile, dbFile.getParentFile(), 2);
            }
            else {
                System.out.printf("Using existing SA index %s\n",saFile.getPath());
            }
            
            try {
                int i = 1;
                int n = msgfConfig.getSpectra().size();
                for(String s : msgfConfig.getSpectra()) {
                    statusLabel.setText(String.format("Searching spectrum %d of %d...",i,n));
                    msgfConfig.setSpectrum(s);
                                        
                    /* 
                     * Calling MSGF within this JVM was causing stability
                     * issues, so we transitioned to running it in a 
                     * secondary JVM 
                     *
                     */
                    ExternalProcess ep = new ExternalProcess();
                    String command = PhosMsgfCommand.compile(msgfConfig);
                    threadOutput.write((command+"\n").getBytes());
                    ep.runCmd(command, threadOutput);
                    
                    Mzid2Tdv converter = new Mzid2Tdv();
                    threadOutput.write("Extracting peptides at q<=0.01\n".getBytes());
                    converter.convert(msgfConfig.getOutputFile(), msgfConfig.getOutputDir()+"/", 1, 100, 0.01, 1, false, null, true);
                    threadOutput.write("Extracting peptides at q<=0.02\n".getBytes());
                    converter.convert(msgfConfig.getOutputFile(), msgfConfig.getOutputDir()+"/", 1, 100, 0.02, 1, false, null, true);
                    threadOutput.write("Extracting peptides at q<=0.05\n".getBytes());
                    converter.convert(msgfConfig.getOutputFile(), msgfConfig.getOutputDir()+"/", 1, 100, 0.05, 1, false, null, true);
                    
                    i++;
                }
                threadOutput.write("Search Complete\n".getBytes());
                statusLabel.setText("Search Complete");
            }
            catch(Exception e) {
                e.printStackTrace(System.out);
                threadOutput.write(StackFormatter.escape(e).getBytes());
                statusLabel.setText("Search failed");
            }
            
            return "OK";
        }
        
        @Override
        protected void done() {
            startButton.setEnabled(true);
            progressBar.setVisible(false);
        }
    }
    
    private class BgModificationAdder extends SwingWorker<String,Object> {
        
        BgModificationAdder(String s, Object o) {

        }
        
        @Override
        protected String doInBackground() throws Exception {
            String response = "";
            int c;
            while((c = modificationsInput.read()) != 10) {
                response += String.format("%c", c);
            }
            return(response);
        }
        
        @Override
        protected void done() {
            try {
                String modLine = get();
                System.out.printf("Read %s\n",modLine);
                if(!modLine.equals("none")) {
                    DefaultListModel lm = (DefaultListModel)searchModificationsList.getModel();
                    for(int i=0;i<lm.getSize();i++) {
                        if(lm.getElementAt(i).equals(modLine)) {
                            return;
                        }
                    }
                    lm.addElement(modLine);
                }
            }
            catch(Exception e) {
                statusLabel.setText("Internal Error: Adding modification failed");
            }
        }
    }
    
    /**
     * Creates new form GraphicalUserInterface
     */
    public GraphicalUserInterface() {
        initComponents();
        
        /* Hide the recode progress bar */
        recodeProgressBar.setVisible(false);
        importModSitesProgressBar.setVisible(false);
        importProteinProgressBar.setVisible(false);
        progressBar.setVisible(false);
        
        /* Add a better list model to the auto-generated spectrum JLlist */
        searchSpectrumFileList.setModel(new DefaultListModel());
        
        /* Load data sources */
        loadProteinDatabases();
        loadModificationSiteDatabases();
        loadRecodedDatabases();
        
        /* Get available cores n. On machines with n<4, we limit n to 1,
         * and for n>=4 we set n = n-2 */
        THREADS = Runtime.getRuntime().availableProcessors();
        if(THREADS >= 4) {
            THREADS = THREADS - 2;
        }
        else {
            THREADS = 1;
        }
        
        /* Configure terminal to communicate asynchronously */
        try {
            terminalInput = new PipedInputStream();
            threadOutput = new UncloseablePipedOutputStream();
            threadOutput.connect(terminalInput);

            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    terminalUI = new GraphicalUserInterfaceTerminal(terminalInput);
                }
            });
        }
        catch(Exception e) {
            System.out.printf("Could not redirect IO: "+e.getMessage());
            e.printStackTrace(System.out);
        }
        
        /* Configure modifications interface to communicate asynchronously */
        try {
            modificationsInput = new PipedInputStream();
            modificationsOutput = new UncloseablePipedOutputStream();
            modificationsOutput.connect(modificationsInput);

            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    modificationsUI = new GraphicalUserInterfaceModifications(modificationsOutput);
                }
            });
        }
        catch(Exception e) {
            System.out.printf("Could create modifications UI: "+e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    private void loadProteinDatabases() {
        /* Empty the list from the Recod tab */
        recodeProteinDatabaseCombo.removeAllItems();
        recodeProteinDatabaseCombo.addItem("--Select One--");
        searchProteinDatabaseCombo.removeAllItems();
        searchProteinDatabaseCombo.addItem("--Select One--");
        
        File protDbDir = new File("./data/protDb/");
        String[] files = protDbDir.list(new ProteinSequenceDatabaseFileFilter());
        DefaultListModel listModel = new DefaultListModel();
        for(String file : files) {
            listModel.addElement(file);
            recodeProteinDatabaseCombo.addItem(file);
            searchProteinDatabaseCombo.addItem(file);
        }
        proteinSequenceDatabaseList.setModel(listModel);
    }
    
    private void loadModificationSiteDatabases() {
        /* Empty the list from the Recode tab */
        recodeModificationDatabaseCombo.removeAllItems();
        recodeModificationDatabaseCombo.addItem("--Select One--");
        
        File protDbDir = new File("./data/modDb/");
        String[] files = protDbDir.list(new ModificationSiteDatabaseFileFilter());
        DefaultListModel listModel = new DefaultListModel();
        for(String file : files) {
            file = file.replace(".mv.db", "");
            listModel.addElement(file);
            recodeModificationDatabaseCombo.addItem(file);
        }
        modificationSiteDatabaseList.setModel(listModel);
    }
    
    private void loadRecodedDatabases() {
        /* Empty the list from the MS-GF tab */
        searchRecodedDatabaseCombo.removeAllItems();
        searchRecodedDatabaseCombo.addItem("--Select One--");
        
        File recDbDir = new File("./data/recDb/");
        String[] files = recDbDir.list(new ProteinSequenceDatabaseFileFilter());
        DefaultListModel listModel = new DefaultListModel();
        for(String file : files) {
            listModel.addElement(file);
            searchRecodedDatabaseCombo.addItem(file);
        }
        recodeRecodedDatabaseList.setModel(listModel);
    }
    
    private boolean deleteFile(String file, String dir, boolean force) {
        int r = JOptionPane.OK_OPTION;
        if(!force) {
            r = JOptionPane.showConfirmDialog(null,String.format("Are you sure you want to delete %s?",file), "Confirm Delete", JOptionPane.YES_NO_OPTION);
        }
        
        if(r == JOptionPane.OK_OPTION) {
            Path p = Paths.get(dir+file);
            try {
                Files.delete(p);
                return true;
            }
            catch(Exception e) {
                statusLabel.setText("Error deleting file "+file+" : "+e.getMessage());
            }
        }
        return false;
    }
    
    private void notifyUser(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        tabbedPanel = new javax.swing.JTabbedPane();
        phosMSGFPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        searchFragmentationMethodCombo = new javax.swing.JComboBox<String>();
        startButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        searchPrecursorMassTolerance = new javax.swing.JTextField();
        searchRecodedDatabaseCombo = new javax.swing.JComboBox<String>();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        searchProteinDatabaseCombo = new javax.swing.JComboBox<String>();
        jLabel20 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        searchPeptideLengthMinTextField = new javax.swing.JTextField();
        searchProtocolCombo = new javax.swing.JComboBox<String>();
        searchMs2DetectorTypeCombo = new javax.swing.JComboBox<String>();
        searchOutputNameTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        searchProteaseCombo = new javax.swing.JComboBox<String>();
        jLabel18 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        searchPeptideLengthMaxTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        searchPrecursorMassToleranceUnitsCombo = new javax.swing.JComboBox<String>();
        searchPrecursorChargeMaxCombo = new javax.swing.JComboBox<String>();
        searchPrecursorChargeMinCombo = new javax.swing.JComboBox<String>();
        jLabel30 = new javax.swing.JLabel();
        searchIsotopErrorRangeHigh = new javax.swing.JComboBox<String>();
        searchIsotopErrorRangeLow = new javax.swing.JComboBox<String>();
        jLabel22 = new javax.swing.JLabel();
        maxMissedCleavagesCombo = new javax.swing.JComboBox();
        progressBar = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        searchSpectrumFileList = new javax.swing.JList<String>();
        searchRemoveSpectrumButton = new javax.swing.JButton();
        searchAddSpectrumButton = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        modificationsAddButton = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        searchMaxModsDropDown = new javax.swing.JComboBox();
        jScrollPane6 = new javax.swing.JScrollPane();
        searchModificationsList = new javax.swing.JList();
        searchModificationsRemoveButton = new javax.swing.JButton();
        recodePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        recodeRecodedDatabaseList = new javax.swing.JList();
        deleteRecodedDatabaseButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        recodeProteinDatabaseCombo = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        recodeModificationDatabaseCombo = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        recodeMinPeptideLengthTextField = new javax.swing.JTextField();
        recodeMaxPeptideLengthTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        recodeMissedCleavagesCombo = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        recodeProteaseCombo = new javax.swing.JComboBox();
        recodeProteaseStrictCheckBox = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        recodeMaxModificationsCombo = new javax.swing.JComboBox();
        recodeButton = new javax.swing.JButton();
        recodeProgressBar = new javax.swing.JProgressBar();
        jLabel10 = new javax.swing.JLabel();
        recodeOutputFileName = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        recodeDigestOnlyCheckbox = new javax.swing.JCheckBox();
        databasePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        proteinSequenceDatabaseList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        modificationSiteDatabaseList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        importProteinDatabaseButton = new javax.swing.JButton();
        importModificationSiteDatabaseButton = new javax.swing.JButton();
        deleteModificationSiteDatabaseButton = new javax.swing.JButton();
        deleteProteinDatabaseButton = new javax.swing.JButton();
        importModSitesProgressBar = new javax.swing.JProgressBar();
        importProteinProgressBar = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileMenuExitItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editShowTerminalMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuAboutItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PhosphoRecoderSE");
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(statusLabel)
                .addGap(0, 29, Short.MAX_VALUE))
        );

        jLabel21.setText("Precursor Charge (Min/Max)");

        searchFragmentationMethodCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Auto", "CID", "TOF", "HCD" }));

        startButton.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        startButton.setText("Search");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        jLabel11.setText("Re-coded Peptide Databases");

        searchPrecursorMassTolerance.setText("20");

        searchRecodedDatabaseCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--Select One--" }));
        searchRecodedDatabaseCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchRecodedDatabaseComboItemStateChanged(evt);
            }
        });

        jLabel16.setText("Isotope Error Range");

        jLabel19.setText("Protocol");

        searchProteinDatabaseCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--Select One--" }));
        searchProteinDatabaseCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchProteinDatabaseComboItemStateChanged(evt);
            }
        });

        jLabel20.setText("Protease");

        jLabel13.setText("Peptide Length (Min/Max)");

        searchPeptideLengthMinTextField.setText("6");

        searchProtocolCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Auto", "Phosphorylation", "iTRAQ", "iTRAQPhospho", "TMT", "Standard" }));

        searchMs2DetectorTypeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Low-res LCQ/LTQ", "Orbitrap/FTICR", "TOF", "Q-Exactive" }));

        jLabel12.setText("Full Protein Databases");

        searchProteaseCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Non-specific", "Trypsin", "Chymotrypsin", "Lys-C", "Lys-N", "glutamyl endopeptidase", "Arg-C", "Asp-N", "alphaLP", "No cleavage" }));
        searchProteaseCombo.setSelectedIndex(1);

        jLabel18.setText("MS2 Detector Type");

        jLabel15.setText("Precursor Mass Tolerance");

        searchPeptideLengthMaxTextField.setText("40");

        jLabel14.setText("Output Name");

        jLabel17.setText("Fragmentation Method");

        searchPrecursorMassToleranceUnitsCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ppm", "Da" }));

        searchPrecursorChargeMaxCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        searchPrecursorChargeMaxCombo.setSelectedIndex(2);

        searchPrecursorChargeMinCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        searchPrecursorChargeMinCombo.setSelectedIndex(1);

        jLabel30.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel30.setText("Search Parameters");

        searchIsotopErrorRangeHigh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5" }));
        searchIsotopErrorRangeHigh.setSelectedIndex(6);
        searchIsotopErrorRangeHigh.setPreferredSize(new java.awt.Dimension(53, 27));

        searchIsotopErrorRangeLow.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5" }));
        searchIsotopErrorRangeLow.setSelectedIndex(5);
        searchIsotopErrorRangeLow.setPreferredSize(new java.awt.Dimension(53, 27));

        jLabel22.setText("Max Missed Cleavages");

        maxMissedCleavagesCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unlimited", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        progressBar.setIndeterminate(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(searchRecodedDatabaseCombo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(searchProteinDatabaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(searchIsotopErrorRangeLow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(31, 31, 31)
                                    .addComponent(searchIsotopErrorRangeHigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(searchMs2DetectorTypeCombo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(searchProtocolCombo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(searchFragmentationMethodCombo, 0, 82, Short.MAX_VALUE)
                                            .addComponent(searchPrecursorMassTolerance))
                                        .addComponent(searchPrecursorChargeMinCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(searchPrecursorChargeMaxCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchPrecursorMassToleranceUnitsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchPeptideLengthMinTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(searchPeptideLengthMaxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchProteaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(searchOutputNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(maxMissedCleavagesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(searchRecodedDatabaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchProteinDatabaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchOutputNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(maxMissedCleavagesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(searchPrecursorChargeMinCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchPrecursorChargeMaxCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(searchPrecursorMassTolerance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchPrecursorMassToleranceUnitsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(searchIsotopErrorRangeHigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchIsotopErrorRangeLow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchFragmentationMethodCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(searchMs2DetectorTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(searchProtocolCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(searchPeptideLengthMinTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchPeptideLengthMaxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchProteaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        jScrollPane5.setViewportView(searchSpectrumFileList);

        searchRemoveSpectrumButton.setText("-");
        searchRemoveSpectrumButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchRemoveSpectrumButtonActionPerformed(evt);
            }
        });

        searchAddSpectrumButton.setText("+");
        searchAddSpectrumButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchAddSpectrumButtonActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel28.setText("Modifications");

        jLabel27.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel27.setText("Spectrum Files");

        modificationsAddButton.setText("+");
        modificationsAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificationsAddButtonActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel32.setText("Max #");

        searchMaxModsDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        searchMaxModsDropDown.setSelectedIndex(1);

        searchModificationsList.setModel(new javax.swing.DefaultListModel());
        searchModificationsList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchModificationsListFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchModificationsListFocusLost(evt);
            }
        });
        searchModificationsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                searchModificationsListValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(searchModificationsList);

        searchModificationsRemoveButton.setText("-");
        searchModificationsRemoveButton.setEnabled(false);
        searchModificationsRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchModificationsRemoveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(searchAddSpectrumButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(searchRemoveSpectrumButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modificationsAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchModificationsRemoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel32)
                        .addGap(4, 4, 4)
                        .addComponent(searchMaxModsDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                        .addGap(3, 3, 3)))
                .addGap(19, 19, 19))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(searchAddSpectrumButton)
                    .addComponent(searchRemoveSpectrumButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(modificationsAddButton)
                    .addComponent(jLabel32)
                    .addComponent(searchMaxModsDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchModificationsRemoveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6))
        );

        javax.swing.GroupLayout phosMSGFPanelLayout = new javax.swing.GroupLayout(phosMSGFPanel);
        phosMSGFPanel.setLayout(phosMSGFPanelLayout);
        phosMSGFPanelLayout.setHorizontalGroup(
            phosMSGFPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(phosMSGFPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        phosMSGFPanelLayout.setVerticalGroup(
            phosMSGFPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(phosMSGFPanelLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11))
            .addGroup(phosMSGFPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPanel.addTab("Search", phosMSGFPanel);

        jLabel3.setText("Re-coded Peptide Sequence Databases");

        recodeRecodedDatabaseList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        recodeRecodedDatabaseList.setToolTipText("Previously re-coded databases");
        jScrollPane3.setViewportView(recodeRecodedDatabaseList);

        deleteRecodedDatabaseButton.setText("Delete");
        deleteRecodedDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRecodedDatabaseButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Protein Sequence Database");

        recodeProteinDatabaseCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--Select One--" }));
        recodeProteinDatabaseCombo.setToolTipText("Protein sequence database to digest and recode");

        jLabel5.setText("Modification Site Database");

        recodeModificationDatabaseCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--Select One--" }));
        recodeModificationDatabaseCombo.setToolTipText("Modification site database to use for re-coding of modification sites");

        jLabel6.setText("Peptide Length (Min/Max)");

        recodeMinPeptideLengthTextField.setText("6");
        recodeMinPeptideLengthTextField.setToolTipText("Minimum length of peptides that will be recoded");

        recodeMaxPeptideLengthTextField.setText("40");
        recodeMaxPeptideLengthTextField.setToolTipText("Maximum length of peptides that will be recoded");

        jLabel7.setText("Missed Cleavages");

        recodeMissedCleavagesCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));
        recodeMissedCleavagesCombo.setSelectedIndex(2);
        recodeMissedCleavagesCombo.setToolTipText("Maximum number of missed cleavages to use for theoretical digest");

        jLabel8.setText("Protease");

        recodeProteaseCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AspN", "AspN/N->D", "Chymotrypsin", "GluC", "LysC", "Pepsin, pH=1.3", "Pepsin, pH=2.0", "Trypsin", "Non-specific" }));
        recodeProteaseCombo.setSelectedIndex(7);
        recodeProteaseCombo.setToolTipText("Protease name to use for theoretical digest");

        recodeProteaseStrictCheckBox.setText("Strict?");
        recodeProteaseStrictCheckBox.setToolTipText("If true, uses verbatim Expasy cleavage rules. If false uses relaxed rules that omit exceptions.");

        jLabel9.setText("Maximum #Modifications");

        recodeMaxModificationsCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));
        recodeMaxModificationsCombo.setSelectedIndex(2);
        recodeMaxModificationsCombo.setToolTipText("Maximum number of concurrent modifications to re-code on a peptide");

        recodeButton.setText("Re-code");
        recodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recodeButtonActionPerformed(evt);
            }
        });

        jLabel10.setText("Output Name");

        jLabel31.setText("Digest Only (No recode)");

        javax.swing.GroupLayout recodePanelLayout = new javax.swing.GroupLayout(recodePanel);
        recodePanel.setLayout(recodePanelLayout);
        recodePanelLayout.setHorizontalGroup(
            recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, recodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(recodeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(recodeProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                        .addComponent(recodeProteinDatabaseCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(recodeModificationDatabaseCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(recodePanelLayout.createSequentialGroup()
                            .addComponent(recodeMinPeptideLengthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(recodeMaxPeptideLengthTextField))
                        .addComponent(recodeMissedCleavagesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(recodePanelLayout.createSequentialGroup()
                            .addComponent(recodeProteaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(recodeProteaseStrictCheckBox))
                        .addComponent(recodeMaxModificationsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(recodeOutputFileName))
                    .addComponent(recodeDigestOnlyCheckbox))
                .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(recodePanelLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteRecodedDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(recodePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3)))
                .addContainerGap())
        );
        recodePanelLayout.setVerticalGroup(
            recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(recodePanelLayout.createSequentialGroup()
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(recodeProteinDatabaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(recodeModificationDatabaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(recodeMinPeptideLengthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(recodeMaxPeptideLengthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(recodeMissedCleavagesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(recodeProteaseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(recodeProteaseStrictCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(recodeMaxModificationsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(recodeOutputFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(recodeDigestOnlyCheckbox))
                        .addGap(26, 26, 26)
                        .addGroup(recodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(recodeButton)
                            .addComponent(recodeProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 45, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteRecodedDatabaseButton)
                .addContainerGap())
        );

        tabbedPanel.addTab("Recode", recodePanel);

        proteinSequenceDatabaseList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(proteinSequenceDatabaseList);

        jLabel1.setText("Protein Sequence Databases");

        modificationSiteDatabaseList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(modificationSiteDatabaseList);

        jLabel2.setText("Modification Site Databases");

        importProteinDatabaseButton.setText("Import");
        importProteinDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importProteinDatabaseButtonActionPerformed(evt);
            }
        });

        importModificationSiteDatabaseButton.setText("Import");
        importModificationSiteDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importModificationSiteDatabaseButtonActionPerformed(evt);
            }
        });

        deleteModificationSiteDatabaseButton.setText("Delete");
        deleteModificationSiteDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteModificationSiteDatabaseButtonActionPerformed(evt);
            }
        });

        deleteProteinDatabaseButton.setText("Delete");
        deleteProteinDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProteinDatabaseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout databasePanelLayout = new javax.swing.GroupLayout(databasePanel);
        databasePanel.setLayout(databasePanelLayout);
        databasePanelLayout.setHorizontalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(databasePanelLayout.createSequentialGroup()
                        .addComponent(importProteinDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteProteinDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importProteinProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34)
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(databasePanelLayout.createSequentialGroup()
                        .addComponent(importModificationSiteDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteModificationSiteDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(importModSitesProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        databasePanelLayout.setVerticalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(importProteinDatabaseButton)
                        .addComponent(importModificationSiteDatabaseButton)
                        .addComponent(deleteModificationSiteDatabaseButton)
                        .addComponent(deleteProteinDatabaseButton))
                    .addComponent(importModSitesProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(importProteinProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabbedPanel.addTab("Data Sources", databasePanel);

        fileMenu.setText("File");

        fileMenuExitItem.setText("Exit");
        fileMenuExitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuExitItemActionPerformed(evt);
            }
        });
        fileMenu.add(fileMenuExitItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Tools");

        editShowTerminalMenuItem.setText("Messages");
        editShowTerminalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editShowTerminalMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(editShowTerminalMenuItem);

        jMenuBar1.add(editMenu);

        helpMenu.setText("Help");

        helpMenuAboutItem.setText("About");
        helpMenuAboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuAboutItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuAboutItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabbedPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tabbedPanel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importProteinDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importProteinDatabaseButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ProteinSequenceDatabaseFileChooserFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File("."));
        
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File selected = fc.getSelectedFile();
            System.out.printf("Importing %s\n",selected.getPath());
            (new BgProteinSequenceDatabaseImporter(selected.getPath(),statusLabel)).execute();
        }
    }//GEN-LAST:event_importProteinDatabaseButtonActionPerformed

    private void importModificationSiteDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importModificationSiteDatabaseButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ModificationSiteDatabaseFileChooserFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File("."));
        
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File selected = fc.getSelectedFile();
            System.out.printf("Importing modification site database %s\n",selected.getPath());
            (new BgModificationSiteDatabaseImporter(selected.getPath(),statusLabel)).execute();
        }
    }//GEN-LAST:event_importModificationSiteDatabaseButtonActionPerformed
    
    private void deleteModificationSiteDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteModificationSiteDatabaseButtonActionPerformed
        Object selection = modificationSiteDatabaseList.getSelectedValue();
        if(selection == null) { return; }
        if(deleteFile(selection.toString()+".mv.db","./data/modDb/",false) && deleteFile(selection.toString()+".trace.db","./data/modDb/",true)) {
            loadModificationSiteDatabases();
        }
    }//GEN-LAST:event_deleteModificationSiteDatabaseButtonActionPerformed

    private void deleteProteinDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProteinDatabaseButtonActionPerformed
        Object selection = proteinSequenceDatabaseList.getSelectedValue();
        if(selection == null) { return; }
        if(deleteFile(selection.toString(),"./data/protDb/",false)) {
            /* If we deleted the plain text FASTA, then try to delete any index
             * files that may have also been generated. These may all fail,
             * but the list is exhaustive, so the directory should be clean
             * afterwords. */
            String baseName = removeExtension(selection.toString());
            deleteFile(baseName+".canno","./data/protDb/",true);
            deleteFile(baseName+".cnlcp","./data/protDb/",true);
            deleteFile(baseName+".csarr","./data/protDb/",true);
            deleteFile(baseName+".cseq","./data/protDb/",true);
            deleteFile(baseName+".revCat.fasta","./data/protDb/",true);
            deleteFile(baseName+".revCat.canno","./data/protDb/",true);
            deleteFile(baseName+".revCat.cnlcp","./data/protDb/",true);
            deleteFile(baseName+".revCat.csarr","./data/protDb/",true);
            deleteFile(baseName+".revCat.cseq","./data/protDb/",true);
            loadProteinDatabases();
        }
    }//GEN-LAST:event_deleteProteinDatabaseButtonActionPerformed

    private void recodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recodeButtonActionPerformed
        try {
            String modificationDb = recodeModificationDatabaseCombo.getSelectedItem().toString();
            String proteinDb = recodeProteinDatabaseCombo.getSelectedItem().toString();
            Integer maxPepLength = Integer.parseInt(recodeMaxPeptideLengthTextField.getText());
            Integer minPepLength = Integer.parseInt(recodeMinPeptideLengthTextField.getText());
            Integer missedCleavages = Integer.parseInt(recodeMissedCleavagesCombo.getSelectedItem().toString());
            Integer maxMod = Integer.parseInt(recodeMaxModificationsCombo.getSelectedItem().toString());
            String outputName = recodeOutputFileName.getText();
            String proteaseName = recodeProteaseCombo.getSelectedItem().toString();
            boolean strictProtease = recodeProteaseStrictCheckBox.getModel().isSelected();
            boolean digestOnly = recodeDigestOnlyCheckbox.getModel().isSelected();
        
            /* Validate protein sequence database */
            if(proteinDb.equals("--Select One--")) {
                notifyUser("You must select a protein sequence database");
                return;
            }
            
            /* Validate modification site database */
            if(!digestOnly && modificationDb.equals("--Select One--")) {
                notifyUser("You must select a modification site database");
                return;
            }
            
            /* Validate output name */
            if(outputName == null || outputName.equals("")) {
                notifyUser("You must enter a name for the re-coded database");
                return;
            }
            
            /* Format output name to safe file name */
            outputName = outputName.replace(" ", "_");
            outputName = outputName.replace(File.separator,"");
            
            RecodeConfig recodeConfig = new RecodeConfig();
            recodeConfig.
                setDatabase("./data/protDb/"+proteinDb).
                setModSiteDatabase("./data/modDb/"+modificationDb).
                setOutputName("./data/recDb/"+outputName+".fasta").
                setMissedCleavages(missedCleavages).
                setProtease(proteaseName).
                setMaxModifications(maxMod).
                setMinPeptideLength(minPepLength).
                setMaxPeptideLength(maxPepLength).
                setStrictDigest(strictProtease).
                setDigestOnly(digestOnly);
            
            /* Save the parameters associated with this recoded database */
            RecodeConfig.save(recodeConfig, "./data/recDb/"+outputName+".config");
            
            (new BgRecoder(null,recodeConfig)).execute();
        }
        catch(Exception e) {
            statusLabel.setText("Re-code failed : "+e.getMessage());
            try {
                threadOutput.write(StackFormatter.escape(e).getBytes());
            }
            catch(Exception e2) {
                e.printStackTrace(System.err);
                e2.printStackTrace(System.err);
            }
        }
        
    }//GEN-LAST:event_recodeButtonActionPerformed

    private void deleteRecodedDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRecodedDatabaseButtonActionPerformed
        Object selection = recodeRecodedDatabaseList.getSelectedValue();
        if(selection == null) { return; }
        if(deleteFile(selection.toString(),"./data/recDb/",false)) {
            /* If we deleted the plain text FASTA, then try to delete any index
             * files that may have also been generated. These may all fail,
             * but the list is exhaustive, so the directory should be clean
             * afterwords. */
            String baseName = removeExtension(selection.toString());
            deleteFile(baseName+".canno","./data/recDb/",true);
            deleteFile(baseName+".cnlcp","./data/recDb/",true);
            deleteFile(baseName+".csarr","./data/recDb/",true);
            deleteFile(baseName+".cseq","./data/recDb/",true);
            deleteFile(baseName+".revCat.fasta","./data/recDb/",true);
            deleteFile(baseName+".revCat.canno","./data/recDb/",true);
            deleteFile(baseName+".revCat.cnlcp","./data/recDb/",true);
            deleteFile(baseName+".revCat.csarr","./data/recDb/",true);
            deleteFile(baseName+".revCat.cseq","./data/recDb/",true);
            loadRecodedDatabases();
        }
    }//GEN-LAST:event_deleteRecodedDatabaseButtonActionPerformed

    private void searchRecodedDatabaseComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchRecodedDatabaseComboItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED && searchRecodedDatabaseCombo.getSelectedItem() != null) {
            String item = searchRecodedDatabaseCombo.getSelectedItem().toString();
            searchProteinDatabaseCombo.setEnabled(item.equals("--Select One--"));
            searchPeptideLengthMinTextField.setEnabled(false);
            searchPeptideLengthMaxTextField.setEnabled(false);
            maxMissedCleavagesCombo.setEnabled(false);
        }
    }//GEN-LAST:event_searchRecodedDatabaseComboItemStateChanged

    private void searchProteinDatabaseComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchProteinDatabaseComboItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED && searchProteinDatabaseCombo.getSelectedItem() != null) {
            String item = searchProteinDatabaseCombo.getSelectedItem().toString();
            searchRecodedDatabaseCombo.setEnabled(item.equals("--Select One--"));
            searchPeptideLengthMinTextField.setEnabled(true);
            searchPeptideLengthMaxTextField.setEnabled(true);
            maxMissedCleavagesCombo.setEnabled(true);
        }
    }//GEN-LAST:event_searchProteinDatabaseComboItemStateChanged

    private void searchAddSpectrumButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchAddSpectrumButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new SpectrumFileChooserFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File("."));
        fc.setMultiSelectionEnabled(true);
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            for(File file : fc.getSelectedFiles()) {
                String path = file.getPath();
                DefaultListModel model = (DefaultListModel)searchSpectrumFileList.getModel();
                if(!model.contains(path)) {
                    model.addElement(path);
                }
            }
        }
        
    }//GEN-LAST:event_searchAddSpectrumButtonActionPerformed

    private void searchRemoveSpectrumButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchRemoveSpectrumButtonActionPerformed
        DefaultListModel model = (DefaultListModel)searchSpectrumFileList.getModel();
        List<String> selections = searchSpectrumFileList.getSelectedValuesList();
        for(String value : selections) {
            model.removeElement(value);
        }
    }//GEN-LAST:event_searchRemoveSpectrumButtonActionPerformed

    private String getSequenceDatabase() throws Exception {
        if(!searchRecodedDatabaseCombo.getSelectedItem().toString().equals("--Select One--")) {
            return "./data/recDb/"+searchRecodedDatabaseCombo.getSelectedItem().toString();
        }
        else if(!searchProteinDatabaseCombo.getSelectedItem().toString().equals("--Select One--")) {
            return "./data/protDb/"+searchProteinDatabaseCombo.getSelectedItem().toString();
        }
        throw new Exception("You must select a sequence database for the search");
    }
    
    private String getOutputDirectory() throws Exception {
        String outputName = searchOutputNameTextField.getText();
        if(outputName == null || outputName.trim().equals("")) {
            throw new Exception("You must enter a directoy name for the output");
        }
        
        File outputDir = new File("./results/"+outputName);
        if(outputDir.exists()) {
            throw new Exception("You specified output directoy already exists. You must delete it or choose a different name");
        }
        
        if(!outputDir.mkdir()) {
            throw new Exception("Failed to create output directory "+outputDir+". Does the results directory exist?");
        }
        
        return outputDir.getPath();
    }
    
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        try {
            /* Copy UI parameters to config object */
            PhosMsgfConfig msgfConfig = new PhosMsgfConfig(getOutputDirectory());
            
            msgfConfig.setThreadCount(THREADS);
            msgfConfig.setSequenceDatabase(getSequenceDatabase());
            msgfConfig.setPrecursorMinCharge(Integer.parseInt(searchPrecursorChargeMinCombo.getSelectedItem().toString()));
            msgfConfig.setPrecursorMaxCharge(Integer.parseInt(searchPrecursorChargeMaxCombo.getSelectedItem().toString()));
            msgfConfig.setSearchDecoyDatabase(1);
            msgfConfig.setPrecursorMassTolerance(
                    Double.parseDouble(searchPrecursorMassTolerance.getText()),
                    searchPrecursorMassToleranceUnitsCombo.getSelectedItem().toString());

            msgfConfig.setIsotopErrorRange(
                    Integer.parseInt(searchIsotopErrorRangeLow.getSelectedItem().toString()), 
                    Integer.parseInt(searchIsotopErrorRangeHigh.getSelectedItem().toString()));

            msgfConfig.setFragmentationMethod(searchFragmentationMethodCombo.getSelectedItem().toString());
            msgfConfig.setMs2DetectorType(searchMs2DetectorTypeCombo.getSelectedItem().toString());
            msgfConfig.setProtocol(searchProtocolCombo.getSelectedItem().toString());
            msgfConfig.setEngine("PhosphoMSGFPlus.jar");
            
            /* If we selected a full protein database, apply remaining UI
             * settings verbatim */
            if(searchProteinDatabaseCombo.isEnabled()) {
                /* Configure remaining search parameters based on the UI */
                msgfConfig.setMinPeptideLength(Integer.parseInt(searchPeptideLengthMinTextField.getText()));
                msgfConfig.setMaxPeptideLength(Integer.parseInt(searchPeptideLengthMaxTextField.getText()));
                msgfConfig.setEnzyme(searchProteaseCombo.getSelectedItem().toString());
                
                /* Configure max missed cleavages if they are anything other 
                 * than "Unlimited". If "Unlimited" the parameter is unset,
                 * which causes MS-GF+ to use default behavior of allowing
                 * unlimited missed clavages */
                if(!maxMissedCleavagesCombo.getSelectedItem().toString().equals("Unlimited"))
                    msgfConfig.setMaxMissedCleavages(Integer.parseInt(maxMissedCleavagesCombo.getSelectedItem().toString()));
            }
            
            /* Otherwise, we are searching a recoded database, so we adjust 
             * some settings that were already applied during the recode */
            else {               
                /* Max/min length were specified during the recode process, so
                 * set a sufficiently wide window here */
                msgfConfig.setMinPeptideLength(1);
                msgfConfig.setMaxPeptideLength(100);
                
                /* Don't digest the recoded protein sequences. They were already
                 * digested during the recode */
                msgfConfig.setEnzyme("no cleavage");
                
                /* The suffix array optimization used in MSGFPlus results in 
                 * a large number of duplicate results when applied to the 
                 * recoded database methid, so the PhosphoMSGFPlus engine 
                 * adds a parameter to disable matches via the suffix array */
                msgfConfig.setPrefixMatchesAllowed(0);
                
                /* Cleving N-terminal methionine was performed during the recode
                 * so diable it (this is a hidden option in MSGFPlus that is 
                 * unhidden in the PhosphoMSGFPlus) */
                msgfConfig.setIgnoreProteinNTermMethionineCleavage(1);
                
                /* So that the correct parameter file is loaded (which we 
                 * believe optimizes scoring for different experimental 
                 * conditions) we specify what enzyme was used for the recode.
                 */
                msgfConfig.setPreDigestEnzyme(searchProteaseCombo.getSelectedItem().toString());
                
                /* We don't specify a value for max missed cleavages, because 
                 * this value was applied during recode, and the default 
                 * behavior for Phospho-MSGF+ is to allow unlimitted missed
                 * cleavages */
            }

            msgfConfig.setNumberOfTolerableTermini(2);
            msgfConfig.setModifications(msgfConfig.getOutputDir()+"/mods.txt");
            msgfConfig.setNumberOfMatchesPerSpectrum(1);
            msgfConfig.setMassOfChargeCarrier(1.00727649);
            msgfConfig.setOutputExtraFeatures(0);
                        
            Object[] spectrumFiles = ((DefaultListModel)searchSpectrumFileList.getModel()).toArray();
            for(Object path : spectrumFiles) {
                msgfConfig.attachSpectrum((String)path);
            }
            
            /* Write mods to file in output directory */
            try(FileWriter fw = new FileWriter(msgfConfig.getOutputDir()+"/mods.txt")) {
                fw.write("NumMods="+searchMaxModsDropDown.getSelectedItem().toString()+"\n");
                ListModel lm = searchModificationsList.getModel();
                for(int i=0;i<lm.getSize();i++) {
                    String element = lm.getElementAt(i).toString();
                    fw.write(element+"\n");
                }
                fw.close();
            }
            
            startButton.setEnabled(false);
            progressBar.setVisible(true);
            (new BgMsgfRunner(null,msgfConfig)).execute();

        }
        catch(Exception e) {
            e.printStackTrace(System.out);
            notifyUser(e.getMessage());
            return;
        }
        
        
    }//GEN-LAST:event_startButtonActionPerformed

    private void helpMenuAboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuAboutItemActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphicalUserInterfaceAbout().setVisible(true);
            }
        });
    }//GEN-LAST:event_helpMenuAboutItemActionPerformed

    private void modificationsAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificationsAddButtonActionPerformed
        modificationsUI.setVisible(true);
        (new BgModificationAdder("a",null)).execute();
    }//GEN-LAST:event_modificationsAddButtonActionPerformed

    private void searchModificationsRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchModificationsRemoveButtonActionPerformed
        int selectedIndex = searchModificationsList.getSelectedIndex();
        DefaultListModel dlm = (DefaultListModel)searchModificationsList.getModel();
        if(selectedIndex != -1) {
            dlm.remove(selectedIndex);
        }
    }//GEN-LAST:event_searchModificationsRemoveButtonActionPerformed

    private void searchModificationsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_searchModificationsListValueChanged
        if(evt.getValueIsAdjusting()) return;
        int selectedIndex = searchModificationsList.getSelectedIndex();
        searchModificationsRemoveButton.setEnabled(selectedIndex != -1);
    }//GEN-LAST:event_searchModificationsListValueChanged

    private void searchModificationsListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchModificationsListFocusGained

    }//GEN-LAST:event_searchModificationsListFocusGained

    private void searchModificationsListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchModificationsListFocusLost

    }//GEN-LAST:event_searchModificationsListFocusLost

    private void editShowTerminalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editShowTerminalMenuItemActionPerformed
        terminalUI.setVisible(true);
    }//GEN-LAST:event_editShowTerminalMenuItemActionPerformed

    private void fileMenuExitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuExitItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_fileMenuExitItemActionPerformed

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
               (new GraphicalUserInterface()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel databasePanel;
    private javax.swing.JButton deleteModificationSiteDatabaseButton;
    private javax.swing.JButton deleteProteinDatabaseButton;
    private javax.swing.JButton deleteRecodedDatabaseButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editShowTerminalMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileMenuExitItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuAboutItem;
    private javax.swing.JProgressBar importModSitesProgressBar;
    private javax.swing.JButton importModificationSiteDatabaseButton;
    private javax.swing.JButton importProteinDatabaseButton;
    private javax.swing.JProgressBar importProteinProgressBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JComboBox maxMissedCleavagesCombo;
    private javax.swing.JList modificationSiteDatabaseList;
    private javax.swing.JButton modificationsAddButton;
    private javax.swing.JPanel phosMSGFPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JList proteinSequenceDatabaseList;
    private javax.swing.JButton recodeButton;
    private javax.swing.JCheckBox recodeDigestOnlyCheckbox;
    private javax.swing.JComboBox recodeMaxModificationsCombo;
    private javax.swing.JTextField recodeMaxPeptideLengthTextField;
    private javax.swing.JTextField recodeMinPeptideLengthTextField;
    private javax.swing.JComboBox recodeMissedCleavagesCombo;
    private javax.swing.JComboBox recodeModificationDatabaseCombo;
    private javax.swing.JTextField recodeOutputFileName;
    private javax.swing.JPanel recodePanel;
    private javax.swing.JProgressBar recodeProgressBar;
    private javax.swing.JComboBox recodeProteaseCombo;
    private javax.swing.JCheckBox recodeProteaseStrictCheckBox;
    private javax.swing.JComboBox recodeProteinDatabaseCombo;
    private javax.swing.JList recodeRecodedDatabaseList;
    private javax.swing.JButton searchAddSpectrumButton;
    private javax.swing.JComboBox<String> searchFragmentationMethodCombo;
    private javax.swing.JComboBox<String> searchIsotopErrorRangeHigh;
    private javax.swing.JComboBox<String> searchIsotopErrorRangeLow;
    private javax.swing.JComboBox searchMaxModsDropDown;
    private javax.swing.JList searchModificationsList;
    private javax.swing.JButton searchModificationsRemoveButton;
    private javax.swing.JComboBox<String> searchMs2DetectorTypeCombo;
    private javax.swing.JTextField searchOutputNameTextField;
    private javax.swing.JTextField searchPeptideLengthMaxTextField;
    private javax.swing.JTextField searchPeptideLengthMinTextField;
    private javax.swing.JComboBox<String> searchPrecursorChargeMaxCombo;
    private javax.swing.JComboBox<String> searchPrecursorChargeMinCombo;
    private javax.swing.JTextField searchPrecursorMassTolerance;
    private javax.swing.JComboBox<String> searchPrecursorMassToleranceUnitsCombo;
    private javax.swing.JComboBox<String> searchProteaseCombo;
    private javax.swing.JComboBox<String> searchProteinDatabaseCombo;
    private javax.swing.JComboBox<String> searchProtocolCombo;
    private javax.swing.JComboBox<String> searchRecodedDatabaseCombo;
    private javax.swing.JButton searchRemoveSpectrumButton;
    private javax.swing.JList<String> searchSpectrumFileList;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables
}
