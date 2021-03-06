package technite.waypoint;

import java.util.UUID;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.lucy.map.action.lightspeed.TLcyLspCreateLayerAction;
import com.luciad.lucy.map.lightspeed.ILcyLspMapComponent;
import com.luciad.lucy.map.lightspeed.controller.ALcyLspCreateControllerModel;
import technite.waypoint.WayPointsModelDecoder;
import com.luciad.shape.shape3D.TLcdLonLatHeightPoint;
import com.luciad.view.lightspeed.ILspView;
import com.luciad.view.lightspeed.layer.ILspLayer;
 
final class WayPointCreateControllerModel extends ALcyLspCreateControllerModel {
 
  WayPointCreateControllerModel(ILcyLspMapComponent aMapComponent, TLcyLspCreateLayerAction aCreateLayerAction) {
    super(aMapComponent, aCreateLayerAction);
    System.out.println("WayPointCreateControllerModel - construtor");
  }
 
  @Override
  public Object create(ILspView aView, ILspLayer aLayer) {
    //Create the waypoint domain object, and initiate it with dummy values
	  String uuid = UUID.randomUUID().toString().replace("-", "");
	  System.out.println("WayPointCreateControllerModel - create - "+uuid);
    ILcdDataObject wayPoint = WayPointsModelDecoder.WAYPOINT_TYPE.newInstance();
    wayPoint.setValue("identifier", "WayPoint ("+uuid+")");
    wayPoint.setValue("location", new TLcdLonLatHeightPoint(0, 0, 0));
    return wayPoint;
  }
}
