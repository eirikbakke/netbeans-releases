/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.indexing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.cnd.indexing.api.CndTextIndexKey;
import org.netbeans.modules.cnd.indexing.spi.TextIndexLayer;
import org.netbeans.modules.cnd.repository.impl.spi.LayerDescriptor;
import org.netbeans.modules.cnd.repository.impl.spi.LayeringSupport;
import org.netbeans.modules.cnd.repository.impl.spi.UnitsConverter;

/**
 *
 * @author akrasny
 */
public final class TextIndexStorage {

    private final List<TextIndexLayer> layers;
    private final LayeringSupport layeringSupport;

    TextIndexStorage(LayeringSupport layeringSupport) {
        this.layeringSupport = layeringSupport;
        layers = Collections.unmodifiableList(createLayers(layeringSupport.getLayerDescriptors()));
    }

    public void put(final CndTextIndexKey indexKey, final Set<CharSequence> ids) {
        for (TextIndexLayer layer : layers) {
            if (layer.getDescriptor().isWritable()) {
                CndTextIndexKey layerKey = toLayerKey(layer.getDescriptor(), indexKey);
                layer.put(layerKey, ids);
            }
        }
    }

    public List<CndTextIndexKey> query(final CharSequence text) {
        List<CndTextIndexKey> result = new ArrayList<CndTextIndexKey>();
        for (TextIndexLayer layer : layers) {
            Collection<CndTextIndexKey> data = layer.query(text);
            if (data != null) {
                for (CndTextIndexKey key : data) {
                    result.add(toClientKey(layer.getDescriptor(), key));
                }
            }
        }
        return result;
    }

    public void remove(final CndTextIndexKey indexKey) {
        for (TextIndexLayer layer : layers) {
            if (layer.getDescriptor().isWritable()) {
                CndTextIndexKey layerKey = toLayerKey(layer.getDescriptor(), indexKey);
                layer.remove(layerKey);
            }
        }
    }

    private List<TextIndexLayer> createLayers(final List<LayerDescriptor> layerDescriptors) {
        List<TextIndexLayer> result = new ArrayList<TextIndexLayer>();

        for (LayerDescriptor layerDescriptor : layerDescriptors) {
            TextIndexLayer layer = TextLayerProvider.getLayer(layerDescriptor);
            if (layer != null) {
                result.add(layer);
            }
        }

        return result;
    }

    private CndTextIndexKey toLayerKey(LayerDescriptor layerDescriptor, CndTextIndexKey clientKey) {
        int clientUnitID = clientKey.getUnitId();
        UnitsConverter writeUnitsConverter = layeringSupport.getWriteUnitsConverter(layerDescriptor);
        return new CndTextIndexKey(writeUnitsConverter.clientToLayer(clientUnitID), clientKey.getFileNameIndex());
    }

    private CndTextIndexKey toClientKey(LayerDescriptor layerDescriptor, CndTextIndexKey layerKey) {
        int layerUnitID = layerKey.getUnitId();
        UnitsConverter readUnitsConverter = layeringSupport.getReadUnitsConverter(layerDescriptor);
        int clientUnitID = readUnitsConverter.layerToClient(layerUnitID);
        return new CndTextIndexKey(clientUnitID, layerKey.getFileNameIndex());
    }

    void shutdown() {
        for (TextIndexLayer layer : layers) {
            if (layer.getDescriptor().isWritable()) {
                layer.shutdown();
            }
        }
    }
}
