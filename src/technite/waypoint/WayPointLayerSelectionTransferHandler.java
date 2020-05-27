package technite.waypoint;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.lucy.datatransfer.ALcyDefaultLayerSelectionTransferHandler;
import com.luciad.model.ILcdModel;
import technite.waypoint.WayPointsModelDecoder;
import com.luciad.shape.ALcdShape;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.ILcdShape;
import com.luciad.shape.shape3D.TLcdLonLatHeightPoint;
import com.luciad.transformation.TLcdGeoReference2GeoReference;
import com.luciad.util.TLcdOutOfBoundsException;
 
final class WayPointLayerSelectionTransferHandler extends ALcyDefaultLayerSelectionTransferHandler<ILcdDataObject> {
 
  private final TLcdGeoReference2GeoReference fTransformer = new TLcdGeoReference2GeoReference();
 
  WayPointLayerSelectionTransferHandler() {
    super(null);
  }
 
  @Override
  protected ILcdDataObject createDomainObjectCopy(ILcdDataObject aDomainObject, ILcdModel aSourceModel, ILcdModel aTargetModel) {
	  System.out.println("WayPointLayerSelectionTransferHandler - createDomainObjectCopy");
	if (aSourceModel.getModelReference().equals(aTargetModel.getModelReference())) {
      //no transformation needed as we only copy between models of the same reference
      ILcdDataObject copy = WayPointsModelDecoder.WAYPOINT_TYPE.newInstance();
      copy.setValue("identifier", aDomainObject.getValue("identifier"));
      copy.setValue("location", new TLcdLonLatHeightPoint((TLcdLonLatHeightPoint) ALcdShape.fromDomainObject(aDomainObject)));
      return copy;
    }
    return null;
  }
 
  @Override
  protected ILcdDataObject createDomainObjectForShape(ILcdShape aShape, ILcdModel aSourceModel, ILcdModel aTargetModel) {
    if (aShape instanceof ILcdPoint) {
    	System.out.println("WayPointLayerSelectionTransferHandler - createDomainObjectForShape");
      fTransformer.setSourceReference(aSourceModel.getModelReference());
      fTransformer.setDestinationReference(aTargetModel.getModelReference());
 
      ILcdDataObject wayPoint = WayPointsModelDecoder.WAYPOINT_TYPE.newInstance();
      wayPoint.setValue("identifier", "WayPoint (no name)");
      TLcdLonLatHeightPoint location = new TLcdLonLatHeightPoint(0, 0, 0);
      wayPoint.setValue("location", location);
      try {
        fTransformer.sourcePoint2destinationSFCT((ILcdPoint) aShape, location);
        return wayPoint;
      } catch (TLcdOutOfBoundsException aE) {
        getLogListener().fail("Could not copy shape from source to destination reference");
        return null;
      }
    }
    getLogListener().fail("Only point shapes can be pasted into a way points layer.");
    return null;
  }
 
  @Override
  protected ILcdShape createShapeCopy(ILcdShape aShape, ILcdModel aSourceModel) {
	  System.out.println("WayPointLayerSelectionTransferHandler - createShapeCopy");
    ILcdPoint originalLocation = (ILcdPoint) aShape;
    TLcdLonLatHeightPoint copy = new TLcdLonLatHeightPoint();
    copy.move3D(originalLocation);
    return copy;
  }
}
