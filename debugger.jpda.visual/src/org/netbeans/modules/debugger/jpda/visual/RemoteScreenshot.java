/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.debugger.jpda.visual;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.modules.debugger.jpda.JPDADebuggerImpl;
import org.netbeans.modules.debugger.jpda.expr.EvaluatorVisitor;
import org.netbeans.modules.debugger.jpda.expr.InvocationExceptionTranslated;
import org.netbeans.modules.debugger.jpda.models.JPDAThreadImpl;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport.ReadOnly;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Takes screenshot of a remote application.
 * 
 * @author Martin Entlicher
 */
public class RemoteScreenshot {
    
    private static final Logger logger = Logger.getLogger(RemoteScreenshot.class.getName());
    
    private static final String AWTThreadName = "AWT-EventQueue-";  // NOI18N
    
    private static final RemoteScreenshot[] NO_SCREENSHOTS = new RemoteScreenshot[] {};

    private String title;
    private BufferedImage image;
    private ComponentInfo componentInfo;
    
    private RemoteScreenshot(String title, int width, int height, int[] dataArray,
                             ComponentInfo componentInfo) {
        this.title = title;
        final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = bi.getRaster();
        raster.setDataElements(0, 0, width, height, dataArray);
        this.image = bi;
        this.componentInfo = componentInfo;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Image getImage() {
        return image;
    }
    
    public ComponentInfo getComponentInfo() {
        return componentInfo;
    }
    
    public static RemoteScreenshot[] takeCurrent() throws RetrievalException {
        DebuggerEngine engine = DebuggerManager.getDebuggerManager().getCurrentEngine();
        if (engine != null) {
            JPDADebugger debugger = engine.lookupFirst(null, JPDADebugger.class);
            logger.fine("Debugger = "+debugger);
            if (debugger != null) {
                List<JPDAThread> allThreads = debugger.getThreadsCollector().getAllThreads();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Threads = "+allThreads);
                }
                for (JPDAThread t : allThreads) {
                    if (t.getName().startsWith(AWTThreadName)) {
                        //try {
                            return take(t);
                            /*
                        } catch (InvocationException iex) {
                            //ObjectReference exception = iex.exception();
                            Exceptions.printStackTrace(iex);
                            
                            final InvocationExceptionTranslated iextr = new InvocationExceptionTranslated(iex, (JPDADebuggerImpl) debugger);
                            RequestProcessor.getDefault().post(new Runnable() {
                                @Override
                                public void run() {
                                    iextr.getMessage();
                                    iextr.getLocalizedMessage();
                                    iextr.getCause();
                                    iextr.getStackTrace();
                                    Exceptions.printStackTrace(iextr);
                                }
                            }, 100);
                            
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                             */
                        //break;
                    }
                }
            }
        }
        return NO_SCREENSHOTS;
    }
    
    public static RemoteScreenshot[] take(JPDAThread t) throws RetrievalException {//throws ClassNotLoadedException, IncompatibleThreadStateException, InvalidTypeException, InvocationException {
        //RemoteScreenshot[] screenshots = NO_SCREENSHOTS;
        List<RemoteScreenshot> screenshots = new ArrayList<RemoteScreenshot>();
        boolean methodInvoking = false;
        Lock threadLock = ((JPDAThreadImpl) t).accessLock.writeLock();
        threadLock.lock();
        try {
            
            logger.fine("RemoteScreenshot.take("+t+"), is suspended = "+t.isSuspended());
            if (!t.isSuspended()) {
                threadLock.unlock();
                threadLock = null;
                t = RemoteServices.makeAWTThreadStopOnEvent(t);
                threadLock = ((JPDAThreadImpl) t).accessLock.writeLock();
                threadLock.lock();
            }
            logger.fine("  after remote service BP hit: "+t+" is suspended = "+t.isSuspended());
            if (t.isSuspended()) {
                /*
                 * Run following code in the target VM:
                   Window[] windows = Window.getWindows();
                   for (Window w : windows) {
                       if (!w.isVisible()) {
                           continue;
                       }
                       Dimension d = w.getSize();
                       BufferedImage bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
                       Graphics g = bi.createGraphics();
                       w.paint(g);
                       Raster raster = bi.getData();
                       Object data = raster.getDataElements(0, 0, d.width, d.height, null);
                   }
                 */
                
                ThreadReference tawt = ((JPDAThreadImpl) t).getThreadReference();
                VirtualMachine vm = tawt.virtualMachine();
                ClassType windowClass = RemoteServices.getClass(vm, "java.awt.Window");
                if (windowClass == null) {
                    logger.fine("No Window");
                    return NO_SCREENSHOTS;
                }
                
                Method getWindows = null;//windowClass.concreteMethodByName("getOwnerlessWindows", "()[Ljava/awt/Window;");
                if (getWindows == null) {
                    getWindows = windowClass.concreteMethodByName("getWindows", "()[Ljava/awt/Window;");
                    if (getWindows == null) {
                        logger.fine("No getWindows() method!");
                        String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "java.awt.Window.getWindows()");
                        throw new RetrievalException(msg);
                    }
                }
                
                methodInvoking = true;
                ((JPDAThreadImpl) t).notifyMethodInvoking();
                
                ArrayReference windowsArray = (ArrayReference) ((ClassType) windowClass).invokeMethod(tawt, getWindows, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                List<Value> windows = windowsArray.getValues();
                logger.fine("Have "+windows.size()+" window(s).");
                
                Method isVisible = windowClass.concreteMethodByName("isVisible", "()Z");
                if (isVisible == null) {
                    logger.fine("No isVisible() method!");
                    String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "java.awt.Window.isVisible()");
                    throw new RetrievalException(msg);
                }
                Method getOwner = windowClass.concreteMethodByName("getOwner", "()Ljava/awt/Window;");
                if (getOwner == null) {
                    logger.fine("No getOwner() method!");
                    String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "java.awt.Window.getOwner()");
                    throw new RetrievalException(msg);
                }
                Method getSize = windowClass.concreteMethodByName("getSize", "()Ljava/awt/Dimension;");
                if (getSize == null) {
                    logger.fine("No getSize() method!");
                    String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "java.awt.Window.getSize()");
                    throw new RetrievalException(msg);
                }
                ClassType dimensionClass = RemoteServices.getClass(vm, "java.awt.Dimension");
                if (dimensionClass == null) {
                    logger.fine("No Dimension");
                    String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingClass", "java.awt.Dimension");
                    throw new RetrievalException(msg);
                }
                ClassType bufferedImageClass = RemoteServices.getClass(vm, "java.awt.image.BufferedImage");
                if (bufferedImageClass == null) {
                    logger.fine("No BufferedImage class.");
                    String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingClass", "java.awt.image.BufferedImage");
                    throw new RetrievalException(msg);
                }
                Method bufferedImageConstructor = bufferedImageClass.concreteMethodByName("<init>", "(III)V");
                Method createGraphics = bufferedImageClass.concreteMethodByName("createGraphics", "()Ljava/awt/Graphics2D;");
                if (createGraphics == null) {
                    logger.fine("createGraphics() method is not found!");
                    String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "java.awt.image.BufferedImage.createGraphics()");
                    throw new RetrievalException(msg);
                }
                
                ClassType frameClass = RemoteServices.getClass(vm, "java.awt.Frame");
                Method getFrameTitle = null;
                if (frameClass != null) {
                    getFrameTitle = frameClass.concreteMethodByName("getTitle", "()Ljava/lang/String;");
                }
                ClassType dialogClass = RemoteServices.getClass(vm, "java.awt.Dialog");
                Method getDialogTitle = null;
                if (dialogClass != null) {
                    getDialogTitle = dialogClass.concreteMethodByName("getTitle", "()Ljava/lang/String;");
                }
                
                for (Value windowValue : windows) {
                    ObjectReference window = (ObjectReference) windowValue;
                    //dumpHierarchy(window);
                    
                    BooleanValue visible = (BooleanValue) window.invokeMethod(tawt, isVisible, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    if (!visible.value()) {
                        // Ignore windows that are not visible.
                        // TODO: mark them as not visible.
                        //continue;
                    }
                    Object owner = window.invokeMethod(tawt, getOwner, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    if (owner != null) {
                        // An owned window
                        //continue;
                    }
                    
                    ObjectReference sizeDimension = (ObjectReference) window.invokeMethod(tawt, getSize, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    Field field = dimensionClass.fieldByName("width");
                    IntegerValue widthValue = (IntegerValue) sizeDimension.getValue(field);
                    int width = widthValue.value();
                    field = dimensionClass.fieldByName("height");
                    IntegerValue heightValue = (IntegerValue) sizeDimension.getValue(field);
                    int height = heightValue.value();
                    logger.fine("The size is "+width+" x "+height+"");
                    
                    List<? extends Value> args = Arrays.asList(widthValue, heightValue, vm.mirrorOf(BufferedImage.TYPE_INT_ARGB));
                    ObjectReference bufferedImage = bufferedImageClass.newInstance(tawt, bufferedImageConstructor, args, ObjectReference.INVOKE_SINGLE_THREADED);
                    ObjectReference graphics = (ObjectReference) bufferedImage.invokeMethod(tawt, createGraphics, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    
                    
                    Method paint = windowClass.concreteMethodByName("paint", "(Ljava/awt/Graphics;)V");
                    window.invokeMethod(tawt, paint, Arrays.asList(graphics), ObjectReference.INVOKE_SINGLE_THREADED);
                    
                    /*
                    // getPeer() - java.awt.peer.ComponentPeer, ComponentPeer.paint()
                    Method getPeer = windowClass.concreteMethodByName("getPeer", "()Ljava/awt/peer/ComponentPeer;");
                    ObjectReference peer = (ObjectReference) window.invokeMethod(tawt, getPeer, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    Method paint = ((ClassType) peer.referenceType()).concreteMethodByName("paint", "(Ljava/awt/Graphics;)V");
                    peer.invokeMethod(tawt, paint, Arrays.asList(graphics), ObjectReference.INVOKE_SINGLE_THREADED);
                    - paints nothing! */
                    
                    Method getData = bufferedImageClass.concreteMethodByName("getData", "()Ljava/awt/image/Raster;");
                    ObjectReference raster = (ObjectReference) bufferedImage.invokeMethod(tawt, getData, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                    
                    Method getDataElements = ((ClassType) raster.referenceType()).concreteMethodByName("getDataElements", "(IIIILjava/lang/Object;)Ljava/lang/Object;");
                    IntegerValue zero = vm.mirrorOf(0);
                    ArrayReference data = (ArrayReference) raster.invokeMethod(tawt, getDataElements, Arrays.asList(zero, zero, widthValue, heightValue, null), ObjectReference.INVOKE_SINGLE_THREADED);
                    
                    logger.fine("Image data length = "+data.length());
                    
                    List<Value> dataValues = data.getValues();
                    int[] dataArray = new int[data.length()];
                    int i = 0;
                    for (Value v : dataValues) {
                        dataArray[i++] = ((IntegerValue) v).value();
                    }
                    
                    String title = null;
                    if (frameClass != null && EvaluatorVisitor.instanceOf(window.referenceType(), frameClass)) {
                        Value v = window.invokeMethod(tawt, getFrameTitle, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                        if (v instanceof StringReference) {
                            StringReference sr = (StringReference) v;
                            title = sr.value();
                        }
                    }
                    if (dialogClass != null && EvaluatorVisitor.instanceOf(window.referenceType(), dialogClass)) {
                        Value v = window.invokeMethod(tawt, getDialogTitle, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                        if (v instanceof StringReference) {
                            StringReference sr = (StringReference) v;
                            title = sr.value();
                        }
                    }
                    
                    ClassType containerClass = RemoteServices.getClass(vm, "java.awt.Container");
                    ComponentInfo componentInfo = retrieveComponentTree((JPDAThreadImpl) t, containerClass, window);
                    
                    screenshots.add(new RemoteScreenshot(title, width, height, dataArray, componentInfo));
                }
            }
        } catch (PropertyVetoException pvex) {
            // Can not invoke methods
            throw new RetrievalException(pvex.getMessage(), pvex);
        } catch (InvocationException iex) {
            //Exceptions.printStackTrace(iex);

            final InvocationExceptionTranslated iextr = new InvocationExceptionTranslated(iex, ((JPDAThreadImpl) t).getDebugger());
            // Initialize the translated exception:
            iextr.setPreferredThread((JPDAThreadImpl) t);
            iextr.getMessage();
            iextr.getLocalizedMessage();
            iextr.getCause();
            iextr.getStackTrace();
            throw new RetrievalException(iex.getMessage(), iextr);
        } catch (InvalidTypeException itex) {
            throw new RetrievalException(itex.getMessage(), itex);
        } catch (ClassNotLoadedException cnlex) {
            return NO_SCREENSHOTS;
        } catch (IncompatibleThreadStateException itsex) {
            throw new RetrievalException(itsex.getMessage(), itsex);
        } finally {
            if (methodInvoking) {
                ((JPDAThreadImpl) t).notifyMethodInvokeDone();
            }
            if (threadLock != null) {
                threadLock.unlock();
            }
        }
        return screenshots.toArray(new RemoteScreenshot[] {});
    }
    
    private static ComponentInfo retrieveComponentTree(JPDAThreadImpl t,
                                                       ClassType containerClass, ObjectReference window)
                                                       throws InvalidTypeException, ClassNotLoadedException,
                                                              IncompatibleThreadStateException, InvocationException,
                                                              RetrievalException {
        
        ThreadReference tawt = t.getThreadReference();
        VirtualMachine vm = tawt.virtualMachine();
        ClassType componentClass = RemoteServices.getClass(vm, "java.awt.Component");
        Method getBounds = componentClass.concreteMethodByName("getBounds", "()Ljava/awt/Rectangle;");
        Method getComponents = containerClass.concreteMethodByName("getComponents", "()[Ljava/awt/Component;");
        if (getComponents == null) {
            logger.fine("No getComponents() method!");
            String msg = NbBundle.getMessage(RemoteScreenshot.class, "MSG_ScreenshotNotTaken_MissingMethod", "java.awt.Container.getComponents()");
            throw new RetrievalException(msg);
        }
        ComponentInfo ci = new ComponentInfo(t);
        retrieveComponents(ci, t, vm, componentClass, containerClass, window, getComponents, getBounds,
                           Integer.MIN_VALUE, Integer.MIN_VALUE, ((JPDAThreadImpl) t).getDebugger());
        ci.bounds.x = 0; // Move to the origin, we do not care where it's on the screen.
        ci.bounds.y = 0;
        return ci;
    }
    
    private static void retrieveComponents(final ComponentInfo ci, JPDAThreadImpl t, VirtualMachine vm,
                                           ClassType componentClass, ClassType containerClass, ObjectReference component,
                                           Method getComponents, Method getBounds,
                                           int shiftx, int shifty, JPDADebuggerImpl debugger)
                                           throws InvalidTypeException, ClassNotLoadedException,
                                                  IncompatibleThreadStateException, InvocationException,
                                                  RetrievalException {
        
        ThreadReference tawt = t.getThreadReference();
        ObjectReference rectangle = (ObjectReference) component.invokeMethod(tawt, getBounds, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
        ClassType rectangleClass = RemoteServices.getClass(vm, "java.awt.Rectangle");
        Field fx = rectangleClass.fieldByName("x");
        Field fy = rectangleClass.fieldByName("y");
        Field fwidth = rectangleClass.fieldByName("width");
        Field fheight = rectangleClass.fieldByName("height");
        Map<Field, Value> rvalues = rectangle.getValues(Arrays.asList(new Field[] {fx, fy, fwidth, fheight}));
        Rectangle r = new Rectangle();
        r.x = ((IntegerValue) rvalues.get(fx)).value();
        r.y = ((IntegerValue) rvalues.get(fy)).value();
        r.width = ((IntegerValue) rvalues.get(fwidth)).value();
        r.height = ((IntegerValue) rvalues.get(fheight)).value();
        ci.bounds = r;
        if (shiftx == Integer.MIN_VALUE && shifty == Integer.MIN_VALUE) {
            shiftx = shifty = 0; // Do not shift the window as such
        } else {
            shiftx += r.x;
            shifty += r.y;
            ci.windowBounds = new Rectangle(shiftx, shifty, r.width, r.height);
        }
        Method getName = componentClass.concreteMethodByName("getName", "()Ljava/lang/String;");
        StringReference name = (StringReference) component.invokeMethod(tawt, getName, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
        if (name != null) {
            ci.name = name.value();
        }
        ci.component = component;
        ci.type = component.referenceType().name();
        logger.fine("  Component '"+ci.name+"' class='"+ci.type+"' bounds = "+r);
        
        ci.addPropertySet(new PropertySet("main", "Main", "The main properties") {
            @Override
            public Property<?>[] getProperties() {
                return new Property[] {
                    new ReadOnly("name", String.class, "Component Name", "The name of the component") {
                        @Override
                        public Object getValue() throws IllegalAccessException, InvocationTargetException {
                            return ci.getName();
                        }
                    },
                    new ReadOnly("type", String.class, "Component Type", "The type of the component") {
                        @Override
                        public Object getValue() throws IllegalAccessException, InvocationTargetException {
                            return ci.getType();
                        }
                    },
                    new ReadOnly("bounds", String.class, "Component Bounds", "The bounds of the component in the window.") {
                        @Override
                        public Object getValue() throws IllegalAccessException, InvocationTargetException {
                            Rectangle r = ci.getWindowBounds();
                            return "[x=" + r.x + ",y=" + r.y + ",width=" + r.width + ",height=" + r.height + "]";
                        }
                    },
                };
            }
        });
        addProperties(ci, t, vm, componentClass, component, debugger);
        
        if (isInstanceOfClass((ClassType) component.referenceType(), containerClass)) {
            ArrayReference componentsArray = (ArrayReference) component.invokeMethod(tawt, getComponents, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            List<Value> components = componentsArray.getValues();
            logger.fine("Have "+components.size()+" component(s).");
            if (components.size() > 0) {
                ComponentInfo[] cis = new ComponentInfo[components.size()];
                int i = 0;
                for(Value cv : components) {
                    cis[i] = new ComponentInfo(t);
                    ObjectReference c = (ObjectReference) cv;
                    retrieveComponents(cis[i], t, vm, componentClass, containerClass, c, getComponents, getBounds,
                                       shiftx, shifty, debugger);
                    i++;
                }
                ci.setSubComponents(cis);
            }
        }
    }
    
    private static void addProperties(ComponentInfo ci, JPDAThreadImpl t, VirtualMachine vm,
                                      ClassType componentClass, ObjectReference component,
                                      JPDADebuggerImpl debugger) {
        // TODO: Try to find out the BeanInfo of the class
        List<Method> allMethods = componentClass.allMethods();
        //System.err.println("Have "+allMethods.size()+" methods.");
        Map<String, Method> methodsByName = new HashMap<String, Method>(allMethods.size());
        for (Method m : allMethods) {
            String name = m.name();
            if ((name.startsWith("get") || name.startsWith("set")) && name.length() > 3 ||
                 name.startsWith("is") && name.length() > 2) {
                if ((name.startsWith("get") || name.startsWith("is")) && m.argumentTypeNames().size() == 0 ||
                    name.startsWith("set") && m.argumentTypeNames().size() == 1 && "void".equals(m.returnTypeName())) {

                    methodsByName.put(name, m);
                }
            }
        }
        Map<String, Property> sortedProperties = new TreeMap<String, Property>();
        //final List<Property> properties = new ArrayList<Property>();
        for (String name : methodsByName.keySet()) {
            //System.err.println("  Have method '"+name+"'...");
            if (name.startsWith("set")) {
                continue;
            }
            String property;
            if (name.startsWith("is")) {
                property = Character.toLowerCase(name.charAt(2)) + name.substring(3);
            } else { // startsWith("get"):
                property = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            }
            Property p = new ComponentProperty(property, methodsByName.get(name), methodsByName.get("set" + name.substring(3)),
                                               ci, component, t, debugger);
            sortedProperties.put(property, p);
            //System.err.println("    => property '"+property+"', p = "+p);
        }
        final Property[] properties = sortedProperties.values().toArray(new Property[] {});
        ci.addPropertySet(
            new PropertySet("Properties", "Properties", "All component properties") {

                @Override
                public Property<?>[] getProperties() {
                    return properties;//.toArray(new Property[] {});
                }
            });
    }
    
    private static class ComponentProperty extends Node.Property {
        
        private String propertyName;
        private Method getter;
        private Method setter;
        private ComponentInfo ci;
        private ObjectReference component;
        private JPDAThreadImpl t;
        private ThreadReference tawt;
        private JPDADebuggerImpl debugger;
        private String value;
        private final Object valueLock = new Object();
        private final String valueCalculating = new String("calculating");
        
        ComponentProperty(String propertyName, Method getter, Method setter,
                          ComponentInfo ci, ObjectReference component,
                          JPDAThreadImpl t, JPDADebuggerImpl debugger) {
            super(String.class);
            this.propertyName = propertyName;
            this.getter = getter;
            this.setter = setter;
            this.ci = ci;
            this.component = component;
            this.t = t;
            this.tawt = t.getThreadReference();
            this.debugger = debugger;
        }

        @Override
        public String getName() {
            return propertyName;
        }
        
        @Override
        public String getDisplayName() {
            return propertyName;
        }
        
        @Override
        public boolean canRead() {
            return getter != null;
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            synchronized (valueLock) {
                if (value == null) {
                    value = valueCalculating;
                    debugger.getRequestProcessor().post(new Runnable() {
                        @Override
                        public void run() {
                            String v = getValueLazy();
                            synchronized (valueLock) {
                                value = v;
                            }
                            ci.firePropertyChange(propertyName, null, v);
                        }
                    });
                }
                return value;
            }
        }
        
        private String getValueLazy() {
            Lock l = t.accessLock.writeLock();
            l.lock();
            try {
                Value v = component.invokeMethod(tawt, getter, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                if (v instanceof StringReference) {
                    return ((StringReference) v).value();
                }
                if (v instanceof ObjectReference) {
                    Type t = v.type();
                    if (t instanceof ClassType) {
                        Method toStringMethod = ((ClassType) t).concreteMethodByName("toString", "()Ljava/lang/String;");
                        v = ((ObjectReference) v).invokeMethod(tawt, toStringMethod, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
                        if (v instanceof StringReference) {
                            return ((StringReference) v).value();
                        }
                    }
                }
                return String.valueOf(v);
            } catch (InvalidTypeException ex) {
                Exceptions.printStackTrace(ex);
                return ex.getMessage();
            } catch (ClassNotLoadedException ex) {
                Exceptions.printStackTrace(ex);
                return ex.getMessage();
            } catch (IncompatibleThreadStateException ex) {
                Exceptions.printStackTrace(ex);
                return ex.getMessage();
            } catch (final InvocationException ex) {
                final InvocationExceptionTranslated iextr = new InvocationExceptionTranslated(ex, debugger);
                iextr.setPreferredThread(t);
                /*
                RequestProcessor.getDefault().post(new Runnable() {
                    @Override
                    public void run() {
                        iextr.getMessage();
                        iextr.getLocalizedMessage();
                        iextr.getCause();
                        iextr.getStackTrace();
                        Exceptions.printStackTrace(iextr);
                        Exceptions.printStackTrace(ex);
                    }
                }, 100);
                 */
                //Exceptions.printStackTrace(iextr);
                //Exceptions.printStackTrace(ex);
                return iextr.getMessage();
            } finally {
                l.unlock();
            }
        }

        @Override
        public boolean canWrite() {
            return setter != null;
        }

        @Override
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    /*
    private static ComponentInfo[] retrieveComponents(ThreadReference tawt, VirtualMachine vm,
                                                      ClassType containerClass, ObjectReference window,
                                                      Method getBounds)
                                                      throws InvalidTypeException, ClassNotLoadedException,
                                                             IncompatibleThreadStateException, InvocationException,
                                                             RetrievalException {
        Method getComponents = containerClass.concreteMethodByName("getComponents", "()[Ljava/awt/Component;");
        if (getComponents == null) {
            logger.severe("No getComponents() method!");
            return new ComponentInfo[] {};
        }
        ArrayReference componentsArray = (ArrayReference) window.invokeMethod(tawt, getComponents, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
        List<Value> components = componentsArray.getValues();
        logger.severe("Have "+components.size()+" component(s).");
        
        ComponentInfo[] cis = new ComponentInfo[components.size()];
        int i = 0;
        for(Value cv : components) {
            cis[i] = new ComponentInfo();
            ObjectReference c = (ObjectReference) cv;
            ObjectReference rectangle = (ObjectReference) c.invokeMethod(tawt, getBounds, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED);
            ClassType rectangleClass = getClass(vm, "java.awt.Rectangle");
            Field fx = rectangleClass.fieldByName("x");
            Field fy = rectangleClass.fieldByName("y");
            Field fwidth = rectangleClass.fieldByName("width");
            Field fheight = rectangleClass.fieldByName("height");
            Map<Field, Value> rvalues = rectangle.getValues(Arrays.asList(new Field[] {fx, fy, fwidth, fheight}));
            Rectangle r = new Rectangle();
            r.x = ((IntegerValue) rvalues.get(fx)).value();
            r.y = ((IntegerValue) rvalues.get(fy)).value();
            r.width = ((IntegerValue) rvalues.get(fwidth)).value();
            r.height = ((IntegerValue) rvalues.get(fheight)).value();
            cis[i].bounds = r;
            logger.severe("  Component "+i+": bounds = "+r);
            
            if (isInstanceOfClass((ClassType) c.referenceType(), containerClass)) {
                cis[i].subComponents = retrieveComponents(tawt, vm, containerClass, c, getBounds);
            }
            
            i++;
        }
        return cis;
    }
    */
    
    private static boolean isInstanceOfClass(ClassType c1, ClassType c2) {
        if (c1.equals(c2)) {
            return true;
        }
        c1 = c1.superclass();
        if (c1 == null) {
            return false;
        }
        return isInstanceOfClass(c1, c2);
    }
    
    public static class ComponentInfo {
        
        private static final ComponentInfo[] NO_SUBCOMPONENTS = new ComponentInfo[] {};
        
        //private ComponentInfo parent;
        private Rectangle bounds;
        private Rectangle windowBounds;
        private String name;
        private String type;
        private ComponentInfo[] subComponents;
        private List<PropertySet> propertySets = new ArrayList<PropertySet>();
        private PropertyChangeSupport pchs = new PropertyChangeSupport(this);
        
        private JPDAThreadImpl thread;
        private ObjectReference component;
        
        public ComponentInfo(JPDAThreadImpl t) {
            this.thread = t;
        }
        
        public JPDAThreadImpl getAWTThread() {
            return thread;
        }
        
        public ObjectReference getComponent() {
            return component;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDisplayName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        public Rectangle getBounds() {
            return bounds;
        }
        
        public Rectangle getWindowBounds() {
            if (windowBounds == null) {
                return bounds;
            } else {
                return windowBounds;
            }
        }
        
        void addPropertySet(PropertySet ps) {
            propertySets.add(ps);
        }
        
        public PropertySet[] getPropertySets() {
            return propertySets.toArray(new PropertySet[] {});
        }
        
        void setSubComponents(ComponentInfo[] subComponents) {
            this.subComponents = subComponents;
            /*
            int sx = getWindowBounds().x;
            int sy = getWindowBounds().y;
            for (int i = 0; i < subComponents.length; i++) {
                subComponents[i].parent = this;
                if (sx != 0 || sy != 0) {
                    Rectangle b = subComponents[i].bounds;
                    subComponents[i].windowBounds = new Rectangle(sx + b.x, sy + b.y, b.width, b.height);
                }
            }
             */
        }
        
        public ComponentInfo[] getSubComponents() {
            if (subComponents == null) {
                return NO_SUBCOMPONENTS;
            } else {
                return subComponents;
            }
        }
        
        /** The component info or <code>null</code> */
        public ComponentInfo findAt(int x, int y) {
            if (!bounds.contains(x, y)) {
                return null;
            }
            x -= bounds.x;
            y -= bounds.y;
            ComponentInfo ci = this;
            if (subComponents != null) {
                for (int i = 0; i < subComponents.length; i++) {
                    Rectangle sb = subComponents[i].bounds;
                    if (sb.contains(x, y)) {
                        ComponentInfo tci = subComponents[i].findAt(x, y);
                        if (tci.bounds.width < ci.bounds.width || tci.bounds.height < ci.bounds.height) {
                            ci = tci;
                        }
                    }
                }
            }
            return ci;
        }

        public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
            pchs.addPropertyChangeListener(propertyChangeListener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
            pchs.removePropertyChangeListener(propertyChangeListener);
        }
        
        protected void firePropertyChange(String name, Object o, Object n) {
            pchs.firePropertyChange(name, o, n);
        }
    }
    
    public static class RetrievalException extends Exception {
        
        public RetrievalException(String message) {
            super(message);
        }
        
        public RetrievalException(String message, Throwable cause) {
            super(message, cause);
        }
        
    }
    
}
