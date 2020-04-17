package technite.waypoint;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
 
import com.luciad.datamodel.ILcdDataObject;
import com.luciad.io.TLcdFileOutputStreamFactory;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelEncoder;
import com.luciad.model.TLcdModelDescriptor;
import com.luciad.shape.shape3D.TLcdLonLatHeightPoint;
import com.luciad.util.concurrent.TLcdLockUtil;
 
public class WayPointsModelEncoder implements ILcdModelEncoder {
 
  @Override
  public String getDisplayName() {
    return "Way Points";
  }
 
  @Override
  public boolean canSave(ILcdModel aModel) {
    return canExport(aModel, aModel.getModelDescriptor().getSourceName());
  }
 
  @Override
  public void save(ILcdModel aModel) throws IllegalArgumentException, IOException {
    export(aModel, aModel.getModelDescriptor().getSourceName());
  }
 
  @Override
  public boolean canExport(ILcdModel aModel, String aDestinationName) {
    return isWaypointModel(aModel) && isValidDestinationName(aDestinationName);
  }
 
  private boolean isWaypointModel(ILcdModel aModel) {
    return "CWP".equals(aModel.getModelDescriptor().getTypeName());
  }
 
  private boolean isValidDestinationName(String aTargetName) {
    return aTargetName != null && aTargetName.endsWith(".cwp");
  }
 
  @Override
  public void export(ILcdModel aModel, String aDestinationName) throws IllegalArgumentException, IOException {
    if (!canExport(aModel, aDestinationName)) {
      throw new IllegalArgumentException(String.format("Cannot export %s to %s", aModel.getModelDescriptor().getDisplayName(), aDestinationName));
    }
    //Loop over all elements, and write them to the file one by one
    TLcdFileOutputStreamFactory outputStreamFactory = new TLcdFileOutputStreamFactory();
    try (OutputStream os = outputStreamFactory.createOutputStream(aDestinationName);
         PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)))) {
      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.readLock(aModel)) {
        Enumeration elements = aModel.elements();
        while (elements.hasMoreElements()) {
          ILcdDataObject wayPoint = (ILcdDataObject) elements.nextElement();
          writeRecord(wayPoint, writer);
        }
      }
    }
 
    //Update the source name on the model to reflect the latest location
    try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(aModel)) {
      TLcdModelDescriptor waypointModelDescriptor = (TLcdModelDescriptor) aModel.getModelDescriptor();
      waypointModelDescriptor.setSourceName(aDestinationName);
    }
 
  }
 
  private void writeRecord(ILcdDataObject aWayPoint, PrintWriter aWriter) throws IOException {
    aWriter.println(aWayPoint.getValue("identifier"));
 
    TLcdLonLatHeightPoint point =
        (TLcdLonLatHeightPoint) aWayPoint.getValue("location");
 
    double lon = point.getX();
    double lat = point.getY();
    double height = point.getZ();
 
    aWriter.print(lon);
    aWriter.print(" ");
 
    aWriter.print(lat);
    aWriter.print(" ");
 
    aWriter.print(height);
 
    aWriter.println();
  }
 
}
