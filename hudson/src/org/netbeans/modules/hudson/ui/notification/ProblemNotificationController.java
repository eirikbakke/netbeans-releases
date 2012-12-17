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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.hudson.ui.notification;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.netbeans.modules.hudson.api.HudsonJob;
import org.netbeans.modules.hudson.api.HudsonJob.Color;
import org.netbeans.modules.hudson.impl.HudsonInstanceImpl;

public class ProblemNotificationController {

    private static final Logger LOG = Logger.getLogger(ProblemNotificationController.class.getName());

    private final HudsonInstanceImpl instance;
    private final Set<ProblemNotification> notifications = new HashSet<ProblemNotification>();

    public ProblemNotificationController(HudsonInstanceImpl instance) {
        this.instance = instance;
    }

    public synchronized void updateNotifications() {
        LOG.log(Level.FINE, "Updating notifications for {0}", instance);
        Preferences prefs = instance.prefs().node("notifications"); // NOI18N
        for (HudsonJob job : instance.getJobs()) {
            if (!job.isSalient()) {
                LOG.log(Level.FINER, "{0} is not being watched", job);
                continue;
            }
            int build = job.getLastCompletedBuild();
            if (prefs.getInt(job.getName(), 0) >= build) {
                LOG.log(Level.FINER, "{0} was already notified", job);
                continue;
            }
            ProblemNotification n;
            Color color = job.getColor();
            LOG.log(Level.FINER, "{0} has status {1}", new Object[] {job, color});
            switch (color) {
            case red:
            case red_anime:
                n = new ProblemNotification(job, build, true);
                break;
            case yellow:
            case yellow_anime:
                n = new ProblemNotification(job, build, false);
                break;
            case blue:
            case blue_anime:
                removeFormerNotifications(job, null);
                n = null;
                break;
            default:
                n = null;
            }
            if (n != null && notifications.add(n)) {
                prefs.putInt(job.getName(), build);
                n.add();
                removeFormerNotifications(job, n);
            }
        }
    }

    private void removeFormerNotifications(HudsonJob job, ProblemNotification except) {
        Iterator<ProblemNotification> i = notifications.iterator();
        while (i.hasNext()) {
            ProblemNotification former = i.next();
            if (former.job.getName().equals(job.getName()) && !former.equals(except)) {
                former.remove();
                i.remove();
            }
        }
    }

    public synchronized void clearNotifications() {
        for (ProblemNotification problemNotification : notifications) {
            problemNotification.remove();
        }
        notifications.clear();
    }
}
