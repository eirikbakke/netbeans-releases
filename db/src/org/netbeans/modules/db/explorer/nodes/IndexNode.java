/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.db.explorer.nodes;

import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

import org.openide.nodes.Children;
import org.openide.nodes.NodeTransfer;
import org.openide.nodes.Node;
import org.openide.util.datatransfer.PasteType;

import org.netbeans.lib.ddl.*;
import org.netbeans.lib.ddl.impl.*;
import org.netbeans.modules.db.*;
import org.netbeans.modules.db.explorer.*;
import org.netbeans.modules.db.explorer.infos.*;

public class IndexNode extends DatabaseNode {
    /*
    	public void setName(String newname)
    	{
    		try {
    			DatabaseNodeInfo info = getInfo();
    			String table = (String)info.get(DatabaseNode.TABLE);
    			Specification spec = (Specification)info.getSpecification();
    			RenameColumn cmd = spec.createCommandRenameColumn(table);
    			cmd.renameColumn(info.getName(), newname);
    			cmd.execute();
    			super.setName(newname);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    */

    protected void createPasteTypes(Transferable t, List s)
    {
        super.createPasteTypes(t, s);
        Node node = NodeTransfer.node(t, NodeTransfer.MOVE);
        if (node != null) {
            ColumnNodeInfo nfo = (ColumnNodeInfo)node.getCookie(ColumnNodeInfo.class);
            if (nfo != null) s.add(new IndexPasteType((ColumnNodeInfo)nfo, null));
        }
    }

    class IndexPasteType extends PasteType
    {
        /** transferred info */
        private DatabaseNodeInfo info;

        /** the node to destroy or null */
        private Node node;

        /** Constructs new TablePasteType for the specific type of operation paste.
        */
        public IndexPasteType(ColumnNodeInfo info, Node node)
        {
            this.info = info;
            this.node = node;
        }

        /* @return Human presentable name of this paste type. */
        public String getName() {
            return bundle.getString("IndexPasteTypeName"); //NOI18N
        }

        /** Performs the paste action.
        * @return Transferable which should be inserted into the clipboard after
        *         paste action. It can be null, which means that clipboard content
        *         should stay the same.
        */
        public Transferable paste() throws IOException {
            IndexNodeInfo destinfo = (IndexNodeInfo)getInfo();
            
            if (info != null) {
                Connection con;
                DatabaseMetaData dmd;
                Specification spec;
                String catalog;

                try {
                    con = info.getConnection();
                    dmd = info.getSpecification().getMetaData();
                    spec = (Specification)info.getSpecification();
                    catalog = (String)info.get(DatabaseNode.CATALOG);
                    boolean jdbcOdbcBridge = (((java.sql.DriverManager.getDriver(dmd.getURL()) instanceof sun.jdbc.odbc.JdbcOdbcDriver) && (!dmd.getDatabaseProductName().trim().equals("DB2/NT"))) ? true : false); //NOI18N

                    DriverSpecification drvSpec = info.getDriverSpecification();
                    drvSpec.getIndexInfo(catalog, dmd, info.getTable(), false, false);
                    if (drvSpec.rs != null) {
                        String index = destinfo.getName();
                        HashSet ixrm = new HashSet();

                        while (drvSpec.rs.next()) {
                            String ixname = drvSpec.rs.getString("INDEX_NAME"); //NOI18N
                            String colname = drvSpec.rs.getString("COLUMN_NAME"); //NOI18N
                            if (ixname.equals(index)) ixrm.add(colname);
                        }
                        drvSpec.rs.close();

                        if (ixrm.contains(info.getName())) {
                            String message = MessageFormat.format(bundle.getString("EXC_IndexContainsColumn"), new String[] {index, info.getName()}); // NOI18N
                            throw new IOException(message);
                        }

                        CreateIndex icmd = spec.createCommandCreateIndex(info.getTable());
                        icmd.setIndexName(destinfo.getName());
                        Iterator enu = ixrm.iterator();
                        while (enu.hasNext()) {
                            icmd.specifyColumn((String)enu.next());
                        }

                        icmd.specifyColumn(info.getName());
                        spec.createCommandDropIndex(index).execute();
                        icmd.execute();

                        drvSpec.getIndexInfo(catalog, dmd, destinfo.getTable(), false, false);
                        if (drvSpec.rs != null) {
                            while (drvSpec.rs.next()) {
                                if (jdbcOdbcBridge) drvSpec.rsTemp.next();
                                String ixname = drvSpec.rs.getString("INDEX_NAME"); //NOI18N
                                String colname = drvSpec.rs.getString("COLUMN_NAME"); //NOI18N
                                if (ixname.equals(index) && colname.equals(info.getName())) {
                                    IndexNodeInfo ixinfo;
                                    if (jdbcOdbcBridge)
                                        ixinfo = (IndexNodeInfo)DatabaseNodeInfo.createNodeInfo(destinfo, DatabaseNode.INDEX, drvSpec.rsTemp);
                                    else
                                        ixinfo = (IndexNodeInfo)DatabaseNodeInfo.createNodeInfo(destinfo, DatabaseNode.INDEX, drvSpec.rs);

                                    if (ixinfo != null) {
                                        ((DatabaseNodeChildren)destinfo.getNode().getChildren()).createSubnode(ixinfo,true);
                                    } else throw new Exception(bundle.getString("EXC_UnableToCreateIndexNodeInfo")); //NOI18N
                                }
                            }
                            drvSpec.rs.close();
                            if (jdbcOdbcBridge) drvSpec.rsTemp.close();
                        }
                    }
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                }

            } else
                throw new IOException(bundle.getString("EXC_CannotFindIndexOwnerInformation"));
            
            return null;
        }
    }
}
