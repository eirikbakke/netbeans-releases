/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.db.metadata.model.spi;

import java.util.Collection;
import java.util.Collections;
import org.netbeans.modules.db.metadata.model.MetadataAccessor;
import org.netbeans.modules.db.metadata.model.api.Catalog;
import org.netbeans.modules.db.metadata.model.api.Function;
import org.netbeans.modules.db.metadata.model.api.Procedure;
import org.netbeans.modules.db.metadata.model.api.Schema;
import org.netbeans.modules.db.metadata.model.api.Table;
import org.netbeans.modules.db.metadata.model.api.View;

/**
 *
 * @author Andrei Badea
 */
public abstract class SchemaImplementation {

    private Schema schema;

    public final Schema getSchema() {
        if (schema == null) {
            schema = MetadataAccessor.getDefault().createSchema(this);
        }
        return schema;
    }

    public abstract Catalog getParent();

    public abstract String getName();

    public abstract View getView(String name);

    public abstract Collection<View> getViews();

    public abstract Procedure getProcedure(String name);

    public abstract Collection<Procedure> getProcedures();

    /**
     * @since db.metadata.model/1.0
     */
    public Function getFunction(String name) {
        return null;
    }

    /**
     * @since db.metadata.model/1.0
     */
    public Collection<Function> getFunctions() {
        return Collections.emptyList();
    }

    public abstract boolean isDefault();

    public abstract boolean isSynthetic();

    public abstract Collection<Table> getTables();

    public abstract Table getTable(String name);

    public abstract void refresh();
}
