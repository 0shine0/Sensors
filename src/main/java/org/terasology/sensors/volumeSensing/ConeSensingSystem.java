package org.terasology.sensors.volumeSensing;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.physics.components.TriggerComponent;
import org.terasology.physics.events.CollideEvent;
import org.terasology.sensors.SensorComponent;

@RegisterSystem
public class ConeSensingSystem extends BaseComponentSystem{
    
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH)
    public void entityDetected(CollideEvent event, EntityRef entity, SensorComponent sensor, TriggerComponent trigger){
        EntityRef sensorParent = sensor.physicalSensor;
        if(sensorParent == null || sensorParent == EntityRef.NULL){
            return;
        }
        
        ConeSensorComponent coneSensor = sensorParent.getComponent(ConeSensorComponent.class);
        if(coneSensor == null){
            return;
        }
        
        VolumeSensorComponent volumeSensor = sensorParent.getComponent(VolumeSensorComponent.class);
        if(volumeSensor == null){
            return;
        }
        
        EntityRef target = event.getOtherEntity();
        
        if(sensorParent.equals(target)){
            return;
        }
        
        LocationComponent loc = sensorParent.getComponent(LocationComponent.class);
        LocationComponent targetLoc = target.getComponent(LocationComponent.class);
        if(loc == null || targetLoc == null){
            return;
        }
        Vector3f sensorPos = loc.getWorldPosition();
        Vector3f targetPos = targetLoc.getWorldPosition();
        
        Vector3f targetDir = targetPos.sub(sensorPos);
        targetDir.normalize();
        
        Vector3f sensorDir = loc.getWorldDirection();
        
        //get the angle between the 2 directions
        Quat4f quatAngle = Quat4f.shortestArcQuat(sensorDir, targetDir);
        float angle = (float)(Math.toDegrees(quatAngle.getAngle()));
        
        if((angle*2) > coneSensor.aperture){
            event.consume();
        }
        
        //rest gets taken care in VolumeSensingSystem
    }

}
