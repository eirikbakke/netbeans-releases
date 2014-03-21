/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.cnd.analysis.api;

import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Alexander Simon
 */
public final class AuditPreferences {
    public static final Preferences AUDIT_PREFERENCES_ROOT = NbPreferences.root().node("com/sun/tools/ide/analysis"); // NOI18N
    private final Preferences preferences;


    public AuditPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public Preferences getPreferences() {
        return preferences;
    }
    
    public String get(String audit, String key) {
        String old = preferences.get(audit, ""); //NOI18N
        StringTokenizer st = new StringTokenizer(old,";"); //NOI18N
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            int i = token.indexOf('='); //NOI18N
            if (i > 0) {
                String rv = token.substring(0,i);
                if (key.equals(rv)) {
                    return token.substring(i+1);
                }
            }
        }
        return ""; //NOI18N
    }

    public void put(String audit, String key, String value) {
        String old = preferences.get(audit, ""); //NOI18N
        StringBuilder buf = new StringBuilder();
        StringTokenizer st = new StringTokenizer(old,";"); //NOI18N
        boolean found = false;
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            int i = token.indexOf('='); //NOI18N
            if (i > 0) {
                String rv = token.substring(0,i);
                if (buf.length() > 0) {
                        buf.append(';'); //NOI18N
                }
                if (key.equals(rv)) {
                    buf.append(key);
                    buf.append('='); //NOI18N
                    buf.append(value);
                    found = true;
                } else {
                    buf.append(token);
                }
            }
        }
        if (!found) {
            if (buf.length() > 0) {
                    buf.append(';'); //NOI18N
            }
            buf.append(key);
            buf.append('='); //NOI18N
            buf.append(value);
        }
        preferences.put(audit, buf.toString());
    }

    @Override
    public String toString() {
        return preferences.toString();
    }
}
