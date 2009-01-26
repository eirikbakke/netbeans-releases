/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.dlight.memory;

import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.dlight.api.indicator.IndicatorMetadata;
import org.netbeans.modules.dlight.api.storage.DataRow;
import org.netbeans.modules.dlight.api.storage.DataTableMetadata;
import org.netbeans.modules.dlight.api.storage.DataTableMetadata.Column;
import org.netbeans.modules.dlight.api.tool.DLightToolConfiguration;
import org.netbeans.modules.dlight.api.visualizer.VisualizerConfiguration;
import org.netbeans.modules.dlight.collector.stdout.api.CLIODCConfiguration;
import org.netbeans.modules.dlight.collector.stdout.api.CLIOParser;
import org.netbeans.modules.dlight.dtrace.collector.DTDCConfiguration;
import org.netbeans.modules.dlight.dtrace.collector.MultipleDTDCConfiguration;
import org.netbeans.modules.dlight.spi.tool.DLightToolConfigurationProvider;
import org.netbeans.modules.dlight.util.Util;
import org.netbeans.modules.dlight.visualizers.api.TableVisualizerConfiguration;


/**
 * 
 * @author Vladimir Kvashin
 */
public final class MemoryToolConfigurationProvider implements DLightToolConfigurationProvider {

    public MemoryToolConfigurationProvider() {
    }

    public DLightToolConfiguration create() {
        final String toolName = "Memory Tool";
        final DLightToolConfiguration toolConfiguration = new DLightToolConfiguration(toolName);
        Column timestampColumn = new Column("timestamp", Long.class, "Timestamp", null);
        Column timeColumn = new Column("kind", Integer.class, "Kind", null);
        Column sizeColumn = new Column("size", Integer.class, "Size", null);
        Column addressColumn = new Column("address", Integer.class, "Address", null);
        Column totalColumn = new Column("total", Integer.class, "Heap size", null);
        Column stackColumn = new Column("stackid", Integer.class, "Stack ID", null);

        List<Column> columns = Arrays.asList(
                timestampColumn,
                timeColumn,
                sizeColumn,
                addressColumn,
                totalColumn,
                stackColumn);

        String scriptFile = Util.copyResource(getClass(), Util.getBasePath(getClass()) + "/resources/mem.d");

        final DataTableMetadata rawTableMetadata = new DataTableMetadata("mem", columns);
        DTDCConfiguration dataCollectorConfiguration = new DTDCConfiguration(scriptFile, Arrays.asList(rawTableMetadata));
        dataCollectorConfiguration.setStackSupportEnabled(true);
        //dataCollectorConfiguration.setIndicatorFiringFactor(1);
        // DTDCConfiguration collectorConfiguration = new DtraceDataAndStackCollector(dataCollectorConfiguration);
        MultipleDTDCConfiguration multipleDTDCConfiguration = new MultipleDTDCConfiguration(dataCollectorConfiguration, "mem:");
        toolConfiguration.addDataCollectorConfiguration(multipleDTDCConfiguration);

        List<Column> indicatorColumns = Arrays.asList(
                totalColumn);
        IndicatorMetadata indicatorMetadata = new IndicatorMetadata(indicatorColumns);
        DataTableMetadata indicatorTableMetadata = new DataTableMetadata("truss", indicatorColumns);

        CLIODCConfiguration clioCollectorConfiguration = new CLIODCConfiguration("/usr/bin/truss", "-d -t '!all' -u 'libc:*alloc,free' -p @PID", 
                new MyCLIOParser(totalColumn), Arrays.asList(indicatorTableMetadata));
        toolConfiguration.addIndicatorDataProviderConfiguration(clioCollectorConfiguration);

//        HashMap<String, Object> configuration = new HashMap<String, Object>();
//        configuration.put("aggregation", "avrg");
//        BarIndicator indicator = new BarIndicator(indicatorMetadata, new BarIndicatorConfig(configuration));
        MemoryIndicatorConfiguration indicator = new MemoryIndicatorConfiguration(indicatorMetadata, "total");
        indicator.setVisualizerConfiguration(getDetails(rawTableMetadata));
        toolConfiguration.addIndicatorConfiguration(indicator);

        return toolConfiguration;
    }

    private VisualizerConfiguration getDetails(DataTableMetadata rawTableMetadata) {

        List<Column> viewColumns = Arrays.asList(
                new Column("func_name", String.class, "Function", null),
                new Column("leak", Long.class, "Leak", null));

        String sql =
            "SELECT func.func_name as func_name, SUM(size) as leak " +
            "FROM mem, node AS node, func, ( " +
            "   SELECT MAX(timestamp) as leak_timestamp FROM mem, ( " +
            "       SELECT address as leak_address, sum(kind*size) AS leak_size FROM mem GROUP BY address HAVING sum(kind*size) > 0 " +
            "   ) WHERE address = leak_address GROUP BY address " +
            ") WHERE timestamp = leak_timestamp " +
            "AND stackid = node.node_id and node.func_id = func.func_id " +
            "GROUP BY node.func_id";

        DataTableMetadata viewTableMetadata = new DataTableMetadata("sync", viewColumns, sql, Arrays.asList(rawTableMetadata));
        return new TableVisualizerConfiguration(viewTableMetadata);
    }

    private class MyCLIOParser implements CLIOParser {

        private List<String> colNames;
        private int allocated;

        public MyCLIOParser(Column totalColumn) {
            colNames = Arrays.asList(totalColumn.getColumnName());
            allocated = 0;
        }

        public DataRow process(String line) {

            if (line == null) {
                return null;
            }
            String l = line.trim();

            // The example of line is:
            //      /1@1:   14.9686 -> libc:malloc(0x1388, 0x2710, 0x88, 0x1)
            //      /1@1:   14.9700 <- libc:malloc() = 0x8064c08
            //      /1@1:   14.9720 -> libc:free(0x8064c08, 0x2710, 0x88, 0x1)
            //      /1@1:   14.9728 <- libc:free() = 0

            String[] tokens = l.split("[ \t()]+"); //NOI18N

            if (tokens.length < 4) {
                return null;
            }
            try {
                if ( "libc:malloc".equals(tokens[4])) { //NOI18N
                    int delta = Integer.parseInt(tokens[6], 16);
                    allocated += delta;
                    return new DataRow(colNames, Arrays.asList(new Integer[] { allocated }));
                }
                else if ( "libc:free".equals(tokens[4])) { //NOI18N
                    int delta = Integer.parseInt(tokens[6], 16);
                    allocated -= delta;
                    return new DataRow(colNames, Arrays.asList(new Integer[] { allocated }));
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            return null;
        }
    }

}
