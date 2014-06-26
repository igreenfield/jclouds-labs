package org.jclouds.vsphere;

import com.vmware.vim25.AlarmDescription;
import com.vmware.vim25.AlarmExpression;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.ManagedEntity;

import java.rmi.RemoteException;
import java.util.List;

/**
 * CISCO LTD.
 * User: igreenfi
 * Date: 26/06/2014 10:26 AM
 * Package: org.jclouds.vsphere
 */
public interface AlarmManagerApi {
    List<AlarmExpression> getDefaultExpression();

    AlarmDescription getDescription();

    /**
     * @since 4.0
     */
    void acknowledgeAlarm(Alarm alarm, ManagedEntity entity) throws RuntimeFault, RemoteException;

    /**
     * @since 4.0
     */
    boolean areAlarmActionsEnabled(ManagedEntity entity) throws RuntimeFault, RemoteException;

    /**
     * @since 4.0
     */
    void enableAlarmActions(ManagedEntity entity, boolean enabled) throws RuntimeFault, RemoteException;

    Alarm createAlarm(ManagedEntity me, AlarmSpec as) throws InvalidName, DuplicateName, RuntimeFault, RemoteException;

    List<Alarm> getAlarm(ManagedEntity me) throws RuntimeFault, RemoteException;

    List<AlarmState> getAlarmState(ManagedEntity me) throws RuntimeFault, RemoteException;
}
