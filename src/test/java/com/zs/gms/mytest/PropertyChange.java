package com.zs.gms.mytest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PropertyChange {

    private String demoName;

    PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    public PropertyChange() {
        demoName = "initValue";
    }


    public String getDemoName() {
        return demoName;
    }


    public void setDemoName(String demoName) {
        String oldValue = this.demoName;
        this.demoName = demoName;
        //发布监听事件
        firePropertyChange("demoName", oldValue, demoName);

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        listeners.removePropertyChangeListener(listener);
    }


    /**
     * 触发属性改变的事件
     */
    protected void firePropertyChange(String prop, Object oldValue, Object newValue) {
        listeners.firePropertyChange(prop, oldValue, newValue);
    }

    public static void main(String[] args) {
        PropertyChange beans = new PropertyChange();
        beans.addPropertyChangeListener(new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("OldValue:"+evt.getOldValue());
                System.out.println("NewValue:"+evt.getNewValue());
                System.out.println("tPropertyName:"+evt.getPropertyName());
            }});
        beans.setDemoName("test");
    }
}