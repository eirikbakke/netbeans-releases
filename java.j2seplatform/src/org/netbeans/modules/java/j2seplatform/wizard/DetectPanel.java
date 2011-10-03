/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.java.j2seplatform.wizard;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.java.j2seplatform.platformdefinition.J2SEPlatformImpl;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.HelpCtx;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.ChangeSupport;
import org.openide.util.Utilities;

/**
 * This Panel launches autoconfiguration during the New J2SE Platform sequence.
 * The UI views properties of the platform, reacts to the end of detection by
 * updating itself. It triggers the detection task when the button is pressed.
 * The inner class WizardPanel acts as a controller, reacts to the UI completness
 * (jdk name filled in) and autoconfig result (passed successfully) - and manages
 * Next/Finish button (valid state) according to those.
 *
 * @author Svata Dedic
 */
public class DetectPanel extends javax.swing.JPanel {

    private static final int COLS = 30;
    private static final RequestProcessor RP = new RequestProcessor(DetectPanel.class.getName(), 1, false, false);

    private NewJ2SEPlatform primaryPlatform;
    private final ChangeSupport cs = new ChangeSupport(this);
    
    private static final String HTTP = "http";              //NOI18N
    private static final String HTTPS = "https";            //NOI18N
    private static final String FILE = "file";              //NOI18N
    private static final String INNER_SEPARATOR = "!/";     //NOI18N
    private static final String PATH_SEPARATOR = ";";       //NOI18N

    /**
     * Creates a detect panel
     * start the task and update on its completion
     * @param primaryPlatform the platform being customized.
     */
    public DetectPanel(NewJ2SEPlatform primaryPlatform) {
        initComponents();
        postInitComponents ();
        putClientProperty(WizardDescriptor.PROP_CONTENT_DATA,
            new String[] {
                NbBundle.getMessage(DetectPanel.class,"TITLE_PlatformName"),
        });
        this.primaryPlatform = primaryPlatform;
        this.setName (NbBundle.getMessage(DetectPanel.class,"TITLE_PlatformName"));
    }

    @Override
    public void addNotify() {
        super.addNotify();        
    }    

    private void postInitComponents () {        
        final DocumentListener docListener = new DocumentListener () {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cs.fireChange();
            }
        };
        this.jdkName.getDocument().addDocumentListener(docListener);
        this.javadoc.getDocument().addDocumentListener(docListener);
        this.sources.getDocument().addDocumentListener(docListener);
        this.progressLabel.setVisible(false);
        this.progressPanel.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        jdkName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        javadoc = new javax.swing.JTextField();
        sources = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        progressLabel = new javax.swing.JLabel();
        progressPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jLabel3.setLabelFor(jdkName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, NbBundle.getBundle(DetectPanel.class).getString("LBL_DetailsPanel_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(jdkName, gridBagConstraints);
        jdkName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_PlatformName")); // NOI18N

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(sources);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("TXT_Sources")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel4.setLabelFor(javadoc);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, NbBundle.getBundle(DetectPanel.class).getString("TXT_JavaDoc")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        javadoc.setColumns(COLS);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        jPanel1.add(javadoc, gridBagConstraints);
        javadoc.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_PlatformJavadoc")); // NOI18N

        sources.setColumns(COLS);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(sources, gridBagConstraints);
        sources.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_PlatformSources")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("LBL_BrowseSources")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectSources(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(jButton1, gridBagConstraints);
        jButton1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_SelectSources")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("LBL_BrowseJavadoc")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectJavadoc(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        jPanel1.add(jButton2, gridBagConstraints);
        jButton2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_SelectJavadoc")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(24, 0, 0, 0);
        add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        progressLabel.setLabelFor(progressPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/wizard/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(progressLabel, bundle.getString("TXT_PlatfromDetectProgress")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 6, 0);
        add(progressLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(progressPanel, gridBagConstraints);

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_DetectPanel")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void selectJavadoc(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectJavadoc
        String newValue = this.browse(this.javadoc.getText(),NbBundle.getMessage(DetectPanel.class,"TXT_SelectJavadoc"));
        if (newValue != null) {
            this.javadoc.setText(newValue);
        }
        
    }//GEN-LAST:event_selectJavadoc

    private void selectSources(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectSources
        String newValue = this.browse(this.sources.getText(),NbBundle.getMessage(DetectPanel.class,"TXT_SelectSources"));
        if (newValue != null) {
            this.sources.setText(newValue);
        }
    }//GEN-LAST:event_selectSources
    
    public final synchronized void addChangeListener (ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public final synchronized void removeChangeListener (ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    public String getPlatformName() {
	    return jdkName.getText().trim();
    }
    
    List<? extends PathResourceImplementation> getSources (boolean dieOnError) {
        final String val = this.sources.getText();
        final List<PathResourceImplementation> result = new ArrayList<PathResourceImplementation>();
        if (val.trim().length()>0) {
            final File f = new File (val);
            if (f.exists()) {
                try {
                    URL url = f.toURI().toURL();
                    if (FileUtil.isArchiveFile(url)) {
                        url = FileUtil.getArchiveRoot(url);
                        FileObject fo = URLMapper.findFileObject(url);
                        if (fo != null) {
                            fo = fo.getFileObject("src");   //NOI18N
                            if (fo != null) {
                                url = fo.getURL();
                            }
                        }
                        result.add (ClassPathSupport.createResource(url));
                    } else {
                        result.add (ClassPathSupport.createResource(url));
                    }
                } catch (IllegalArgumentException mue) {
                    if (dieOnError) {
                        throw new IllegalStateException();
                    }
                } catch (MalformedURLException mue) {
                    if (dieOnError) {
                        throw new IllegalStateException();
                    }
                } catch (FileStateInvalidException e) {
                    if (dieOnError) {
                        throw new IllegalStateException();
                    }
                }
            } else if (dieOnError) {
                throw new IllegalStateException();
            }
        }
        return result;
    }

    void setSources (String sources) {
        this.sources.setText (sources == null ? "" : sources);      //NOI18N
    }

    List<URL> getJavadoc (boolean dieOnError) {
        final String val = this.javadoc.getText();
        final List<URL> result = new ArrayList<URL>();
        final StringTokenizer tk = new StringTokenizer(val,PATH_SEPARATOR);
        while (tk.hasMoreTokens()) {
            try {
                final String token =  tk.nextToken().trim();
                if (token.startsWith(HTTP) || token.startsWith(HTTPS)) {
                    //Http(s) URL add directly
                    result.add(new URI(token).toURL());
                } else {
                    //File or folder
                    //1st) /jdk/docs/
                    //2nd) /jdk/docs.zip
                    //3rd) /jdk/docs.zip!/docs/api/
                    int index = token.lastIndexOf(INNER_SEPARATOR);
                    if (index > 0) {
                        final String outherPath = token.substring(0, index);
                        final String innerPath = index+2 == token.length() ? "" : token.substring(index+2);
                        final File f = new File (outherPath);
                        if (f.exists()) {
                            result.add (new URL (FileUtil.getArchiveRoot(f.toURI().toURL()).toExternalForm() + innerPath));
                        } else if (dieOnError) {
                            throw new IllegalStateException();
                        }
                    } else {
                        final File f = new File(token);
                        final URL url = FileUtil.urlForArchiveOrDir(f);
                        if (url != null) {
                            result.add(url);
                        } else if (dieOnError) {
                            throw new IllegalStateException();
                        }
                    }
                }
            } catch (MalformedURLException e) {
                if (dieOnError) {
                    throw new IllegalStateException();
                }
            } catch (URISyntaxException e) {
                if (dieOnError) {
                    throw new IllegalStateException();
                }
            }
        }
        return result;
    }

    void setJavadoc (final List<URL> jdoc) {
        //Create "human" representation of URLs
        final StringBuilder result = new StringBuilder();
        for (final URL jdocRoot : jdoc) {
            try {
                final String extUrl = jdocRoot.toExternalForm(); 
                URL url = FileUtil.getArchiveFile(jdocRoot);
                String relative;
                String userName;
                if (url == null) {
                    url = jdocRoot;
                    relative = "";  //NOI18N
                } else {                
                    int index = extUrl.lastIndexOf(INNER_SEPARATOR);
                    relative = index < 0 ? "" : extUrl.substring(index);    //NOI18N
                }
                final String protocol = url.getProtocol();
                if (FILE.equals(protocol)){ //NOI18N
                    userName = new File(url.toURI()).getAbsolutePath() + relative;
                } else if (HTTP.equals(protocol) ||HTTPS.equals(protocol)) {
                    userName = extUrl;
                } else {
                    //Other protocols are unsupported
                    continue;
                }
                if (result.length() > 0) {
                    result.append(PATH_SEPARATOR);
                }
                result.append(userName);
            } catch (URISyntaxException e) {
                Exceptions.printStackTrace(e);
            }
        }
        this.javadoc.setText(result.toString());
    }

    /**
     * Updates static information from the detected platform's properties
     */
    void updateData() {
        Map<String,String> m = primaryPlatform.getSystemProperties();
        // if the name is empty, fill something in:
        if ("".equals(jdkName.getText())) {
            jdkName.setEditable(true);
            jdkName.setText(getInitialName (m));
            this.jdkName.selectAll();
        }
    }


    private static String getInitialName (Map<String,String> m) {        
        String vmVersion = m.get("java.specification.version");        //NOI18N
        StringBuilder result = new StringBuilder(NbBundle.getMessage(DetectPanel.class,"TXT_DetectPanel_Java"));        
        if (vmVersion != null) {
            result.append (vmVersion);
        }
        return result.toString();
    }
    
    
    private String browse (String oldValue, String title) {
        JFileChooser chooser = new JFileChooser ();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (Utilities.isMac()) {
            //New JDKs and JREs are bundled into package, allow JFileChooser to navigate in
            chooser.putClientProperty("JFileChooser.packageIsTraversable", "always");   //NOI18N
        }
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter (new FileFilter () {
            @Override
            public boolean accept(File f) {
                return (f.exists() && f.canRead() && (f.isDirectory() || (f.getName().endsWith(".zip") || f.getName().endsWith(".jar"))));
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(DetectPanel.class,"TXT_ZipFilter");
            }
        });
        File f = new File (oldValue);
        chooser.setSelectedFile(f);
        chooser.setDialogTitle (title);
        if (chooser.showOpenDialog (this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField javadoc;
    private javax.swing.JTextField jdkName;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JTextField sources;
    // End of variables declaration//GEN-END:variables

    /**
     * Controller for the outer class: manages wizard panel's valid state
     * according to the user's input and detection state.
     */
    static class WizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, TaskListener, ChangeListener {
        private DetectPanel         component;
        private RequestProcessor.Task task;
        private final J2SEWizardIterator  iterator;
        private final ChangeSupport cs = new ChangeSupport(this);
        private boolean             detected;
        private boolean             valid;
        private boolean             firstPass=true;
        private WizardDescriptor    wiz;
        private ProgressHandle      progressHandle;

        WizardPanel(J2SEWizardIterator iterator) {            
	    this.iterator = iterator;
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        @Override
        public java.awt.Component getComponent() {
            if (component == null) {
                final NewJ2SEPlatform primaryPlatform = this.iterator.getPlatform();
                final NewJ2SEPlatform secondaryPlatform = this.iterator.getSecondaryPlatform();
                component = new DetectPanel(primaryPlatform);
                component.addChangeListener (this);
                task = RP.create(
                    new Runnable() {
                        @Override
                        public void run() {
                            primaryPlatform.run();
                            if (secondaryPlatform != null) {
                                secondaryPlatform.run();
                            }
                        }
                });
                task.addTaskListener(this);
            }
            return component;
        }

        void setValid(boolean v) {
            if (v == valid)
                return;
            valid = v;
            cs.fireChange();
        }

        @Override
        public HelpCtx getHelp() {
            return new HelpCtx (DetectPanel.class);
        }

        @Override
        public boolean isValid() {
            return valid;
        }

        @Override
        public void readSettings(WizardDescriptor settings) {           
            this.wiz = settings;
            JavaPlatform platform = this.iterator.getPlatform();
            String srcPath = null;
            ClassPath src = platform.getSourceFolders();
            if (src.entries().size()>0) {
                URL folderRoot = src.entries().get(0).getURL();
                if ("jar".equals(folderRoot.getProtocol())) {   //NOI18N
                    folderRoot = FileUtil.getArchiveFile (folderRoot);
                }
                srcPath = new File(URI.create(folderRoot.toExternalForm())).getAbsolutePath();
            }
            else if (firstPass) {
                for (FileObject folder : platform.getInstallFolders()) {
                    File base = FileUtil.toFile(folder);
                    if (base!=null) {
                        File f = new File (base,"src.zip"); //NOI18N
                        if (f.canRead()) {
                            srcPath = f.getAbsolutePath();
                        }
                        else {
                            f = new File (base,"src.jar");  //NOI18N
                            if (f.canRead()) {
                                srcPath = f.getAbsolutePath();
                            }
                        }
                    }
                }                
                firstPass = false;
            }
            this.component.setSources (srcPath);
            this.component.jdkName.setEditable(false);
            this.component.progressPanel.setVisible (true);
            this.component.progressLabel.setVisible (true);
            
            this.progressHandle = ProgressHandleFactory.createHandle(NbBundle.getMessage(DetectPanel.class,"TXT_PlatfromDetectProgress"));
            this.component.progressPanel.removeAll();
            this.component.progressPanel.setLayout (new GridBagLayout ());
            GridBagConstraints c = new GridBagConstraints ();
            c.gridx = c.gridy = GridBagConstraints.RELATIVE;
            c.gridheight = c.gridwidth = GridBagConstraints.REMAINDER;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            JComponent pc = ProgressHandleFactory.createProgressComponent(this.progressHandle);
            ((GridBagLayout)this.component.progressPanel.getLayout ()).setConstraints(pc,c);
            this.component.progressPanel.add (pc);
            this.progressHandle.start ();
            task.schedule(0);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

	/**
	 Updates the Platform's display name with the one the user
	 has entered. Stores user-customized display name into the Platform.
	 */
        @Override
        public void storeSettings(WizardDescriptor settings) {
            if (isValid()) {                                
                String name = component.getPlatformName();                
                final List<? extends PathResourceImplementation> src = this.component.getSources(false);
                final List<URL> jdoc = this.component.getJavadoc(false);
                NewJ2SEPlatform platform = this.iterator.getPlatform();
                platform.setDisplayName (name);
                platform.setAntName (createAntName (name));
                platform.setSourceFolders (ClassPathSupport.createClassPath(src));
                if (!jdoc.isEmpty()) {
                    platform.setJavadocFolders(jdoc);
                }
                
                platform = this.iterator.getSecondaryPlatform();
                if (platform != null) {
                    name = NbBundle.getMessage(DetectPanel.class,"FMT_64BIT", name);
                    platform.setDisplayName (name);
                    platform.setAntName (createAntName(name));
                    platform.setSourceFolders (ClassPathSupport.createClassPath(src));
                    if (!jdoc.isEmpty()) {
                        platform.setJavadocFolders(jdoc);
                    }
                }                                
            }
        }

        /**
         * Revalidates the Wizard Panel
         */
        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run () {
                    JavaPlatform platform = iterator.getPlatform();
                    List<URL> jdoc = platform.getJavadocFolders();
                    if (jdoc.isEmpty()) {                            
                        jdoc = J2SEPlatformImpl.defaultJavadoc(platform);                            
                    }
                    component.setJavadoc(jdoc);
                    component.updateData ();
                    component.jdkName.setEditable(true);
                    assert progressHandle != null;
                    progressHandle.finish ();
                    component.progressPanel.setVisible (false);
                    component.progressLabel.setVisible (false);                    
                    detected = iterator.getPlatform().isValid();                    
                    checkValid ();
                }
            });            
        }


        @Override
        public void stateChanged(ChangeEvent e) {
             this.checkValid();
        }

        private void checkValid () {
            this.wiz.putProperty( WizardDescriptor.PROP_ERROR_MESSAGE, ""); //NOI18N
            final String name = this.component.getPlatformName ();
            boolean vld = detected;
            if (!vld) {
                this.wiz.putProperty( WizardDescriptor.PROP_ERROR_MESSAGE,NbBundle.getMessage(DetectPanel.class,"ERROR_NoSDKRegistry"));         //NOI18N
            }
            if (vld && name.length() == 0) {
                vld = false;
                this.wiz.putProperty( WizardDescriptor.PROP_ERROR_MESSAGE,NbBundle.getMessage(DetectPanel.class,"ERROR_InvalidDisplayName"));    //NOI18N
            }
            if (vld) {
                for (JavaPlatform platform : JavaPlatformManager.getDefault().getInstalledPlatforms()) {
                    if (name.equals (platform.getDisplayName())) {
                        vld = false;
                        this.wiz.putProperty( WizardDescriptor.PROP_ERROR_MESSAGE,NbBundle.getMessage(DetectPanel.class,"ERROR_UsedDisplayName"));    //NOI18N
                        break;
                    }
                }
            }
            if (vld) {
                try {
                    component.getSources(true);
                } catch (IllegalStateException ise) {
                    this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,NbBundle.getMessage(DetectPanel.class,"ERROR_Sources"));
                    vld = false;
                }
            }
            if (vld) {
                try {
                    component.getJavadoc(true);
                } catch (IllegalStateException ise) {
                    this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,NbBundle.getMessage(DetectPanel.class,"ERROR_Javadoc"));
                    vld = false;
                }
            }
            setValid(vld);
        }

        private static String createAntName (String name) {
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException ();
            }                        
            String antName = PropertyUtils.getUsablePropertyName(name);            
            if (platformExists (antName)) {
                String baseName = antName;
                int index = 1;
                antName = baseName + Integer.toString (index);
                while (platformExists (antName)) {
                    index ++;
                    antName = baseName + Integer.toString (index);
                }
            }
            return antName;
        }
        
        private static boolean platformExists (String antName) {
            JavaPlatformManager mgr = JavaPlatformManager.getDefault();
            JavaPlatform[] platforms = mgr.getInstalledPlatforms();
            for (int i=0; i < platforms.length; i++) {
                if (platforms[i] instanceof J2SEPlatformImpl) {
                    String val = ((J2SEPlatformImpl)platforms[i]).getAntName();
                    if (antName.equals(val)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
    }    
}
