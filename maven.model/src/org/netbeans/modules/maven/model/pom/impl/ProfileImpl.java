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
package org.netbeans.modules.maven.model.pom.impl;

import java.util.Collections;
import org.w3c.dom.Element;
import org.netbeans.modules.maven.model.pom.*;	
import org.netbeans.modules.maven.model.pom.POMComponentVisitor;	

/**
 *
 * @author mkleint
 */
public class ProfileImpl extends IdPOMComponentImpl implements Profile {

    public ProfileImpl(POMModel model, Element element) {
        super(model, element);
    }
    
    public ProfileImpl(POMModel model) {
        this(model, createElementNS(model, model.getPOMQNames().PROFILE));
    }

    // attributes

    // child elements
    public Activation getActivation() {
        return getChild(Activation.class);
    }

    public void setActivation(Activation activation) {
        java.util.List<Class<? extends POMComponent>> empty = Collections.emptyList();
        setChild(Activation.class, getModel().getPOMQNames().ACTIVATION.getName(), activation, empty);
    }

    public BuildBase getBuildBase() {
        return getChild(BuildBase.class);
    }

    public void setBuildBase(BuildBase buildBase) {
        java.util.List<Class<? extends POMComponent>> empty = Collections.emptyList();
        setChild(BuildBase.class, getModel().getPOMQNames().BUILD.getName(), buildBase, empty);
    }

    public java.util.List<Repository> getRepositories() {
        ModelList<Repository> childs = getChild(RepositoryImpl.RepoList.class);
        if (childs != null) {
            return childs.getListChildren();
        }
        return null;
    }

    public void addRepository(Repository repo) {
        ModelList<Repository> childs = getChild(RepositoryImpl.RepoList.class);
        if (childs == null) {
            setChild(RepositoryImpl.RepoList.class,
                    getModel().getPOMQNames().REPOSITORIES.getName(),
                    getModel().getFactory().create(this, getModel().getPOMQNames().REPOSITORIES.getQName()),
                    Collections.EMPTY_LIST);
            childs = getChild(RepositoryImpl.RepoList.class);
            assert childs != null;
        }
        childs.addListChild(repo);
    }

    public void removeRepository(Repository repo) {
        ModelList<Repository> childs = getChild(RepositoryImpl.RepoList.class);
        if (childs != null) {
            childs.removeListChild(repo);
        }
    }

    public java.util.List<Repository> getPluginRepositories() {
        ModelList<Repository> childs = getChild(RepositoryImpl.PluginRepoList.class);
        if (childs != null) {
            return childs.getListChildren();
        }
        return null;
    }

    public void addPluginRepository(Repository repo) {
        ModelList<Repository> childs = getChild(RepositoryImpl.PluginRepoList.class);
        if (childs == null) {
            setChild(RepositoryImpl.PluginRepoList.class,
                    getModel().getPOMQNames().PLUGINREPOSITORIES.getName(),
                    getModel().getFactory().create(this, getModel().getPOMQNames().PLUGINREPOSITORIES.getQName()),
                    Collections.EMPTY_LIST);
            childs = getChild(RepositoryImpl.PluginRepoList.class);
            assert childs != null;
        }
        childs.addListChild(repo);
    }

    public void removePluginRepository(Repository repo) {
        ModelList<Repository> childs = getChild(RepositoryImpl.PluginRepoList.class);
        if (childs != null) {
            childs.removeListChild(repo);
        }
    }

    public java.util.List<Dependency> getDependencies() {
        ModelList<Dependency> childs = getChild(DependencyImpl.List.class);
        if (childs != null) {
            return childs.getListChildren();
        }
        return null;
    }

    public void addDependency(Dependency dep) {
        ModelList<Dependency> childs = getChild(DependencyImpl.List.class);
        if (childs == null) {
            setChild(DependencyImpl.List.class,
                    getModel().getPOMQNames().DEPENDENCIES.getName(),
                    getModel().getFactory().create(this, getModel().getPOMQNames().DEPENDENCIES.getQName()),
                    Collections.EMPTY_LIST);
            childs = getChild(DependencyImpl.List.class);
            assert childs != null;
        }
        childs.addListChild(dep);
    }

    public void removeDependency(Dependency dep) {
        ModelList<Dependency> childs = getChild(DependencyImpl.List.class);
        if (childs != null) {
            childs.removeListChild(dep);
        }
    }

    public Reporting getReporting() {
        return getChild(Reporting.class);
    }

    public void setReporting(Reporting reporting) {
        java.util.List<Class<? extends POMComponent>> empty = Collections.emptyList();
        setChild(Reporting.class, getModel().getPOMQNames().REPORTING.getName(), reporting, empty);
    }

    public DependencyManagement getDependencyManagement() {
        return getChild(DependencyManagement.class);
    }

    public void setDependencyManagement(DependencyManagement dependencyManagement) {
        java.util.List<Class<? extends POMComponent>> empty = Collections.emptyList();
        setChild(DependencyManagement.class, getModel().getPOMQNames().DEPENDENCYMANAGEMENT.getName(), dependencyManagement, empty);
    }

    public DistributionManagement getDistributionManagement() {
        return getChild(DistributionManagement.class);
    }

    public void setDistributionManagement(DistributionManagement distributionManagement) {
        java.util.List<Class<? extends POMComponent>> empty = Collections.emptyList();
        setChild(DistributionManagement.class, getModel().getPOMQNames().DISTRIBUTIONMANAGEMENT.getName(), distributionManagement, empty);
    }

    public void accept(POMComponentVisitor visitor) {
        visitor.visit(this);
    }

    public Properties getProperties() {
        return getChild(Properties.class);
    }

    public void setProperties(Properties props) {
        java.util.List<Class<? extends POMComponent>> empty = Collections.emptyList();
        setChild(Reporting.class, getModel().getPOMQNames().PROPERTIES.getName(), props, empty);
    }

    public java.util.List<String> getModules() {
        java.util.List<StringList> lists = getChildren(StringList.class);
        for (StringList list : lists) {
            if (getModel().getPOMQNames().MODULES.getName().equals(list.getPeer().getNodeName())) {
                return list.getListChildren();
            }
        }
        return null;
    }

    public void addModule(String module) {
        java.util.List<StringList> lists = getChildren(StringList.class);
        for (StringList list : lists) {
            if (getModel().getPOMQNames().MODULES.getName().equals(list.getPeer().getNodeName())) {
                list.addListChild(module);
                return;
            }
        }
        setChild(StringListImpl.class,
                 getModel().getPOMQNames().MODULES.getName(),
                 getModel().getFactory().create(this, getModel().getPOMQNames().MODULES.getQName()),
                 Collections.EMPTY_LIST);
        lists = getChildren(StringList.class);
        for (StringList list : lists) {
            if (getModel().getPOMQNames().MODULES.getName().equals(list.getPeer().getNodeName())) {
                list.addListChild(module);
                return;
            }
        }
    }

    public void removeModule(String module) {
        java.util.List<StringList> lists = getChildren(StringList.class);
        for (StringList list : lists) {
            if (getModel().getPOMQNames().MODULES.getName().equals(list.getPeer().getNodeName())) {
                list.removeListChild(module);
                return;
            }
        }
    }

    public static class List extends ListImpl<Profile> {
        public List(POMModel model, Element element) {
            super(model, element, model.getPOMQNames().PROFILE, Profile.class);
        }

        public List(POMModel model) {
            this(model, createElementNS(model, model.getPOMQNames().PROFILES));
        }
    }


}