package technite.waypoint;

import com.luciad.lucy.ILcyLucyEnv;
import technite.waypoint.WayPointModelAddOn;
import com.luciad.lucy.gui.formatbar.ALcyFormatBar;
import com.luciad.lucy.gui.formatbar.ALcyFormatBarFactory;
import com.luciad.lucy.map.ILcyGenericMapComponent;
import com.luciad.lucy.map.lightspeed.TLcyLspMapComponent;
import com.luciad.lucy.util.properties.ALcyProperties;
import com.luciad.view.ILcdLayer;
import com.luciad.view.ILcdView;
 
class WayPointFormatBarFactory extends ALcyFormatBarFactory {
 
  static String TOOLBAR_ID = "wayPointsToolBar";
 
  private final ILcyLucyEnv fLucyEnv;
  private final ALcyProperties fProperties;
  private final String fShortPrefix;
 
  WayPointFormatBarFactory(ILcyLucyEnv aLucyEnv, ALcyProperties aProperties, String aShortPrefix) {
    fLucyEnv = aLucyEnv;
    fProperties = aProperties;
    fShortPrefix = aShortPrefix;
  }
 
  @Override
  public boolean canCreateFormatBar(ILcdView aView, ILcdLayer aLayer) {
    // TLcyLspSafeGuardFormatWrapper already checks the layer
    return findLspMapComponent(aView) != null;
  }
 
  @Override
  public ALcyFormatBar createFormatBar(ILcdView aView, ILcdLayer aLayer) {
    WayPointModelAddOn wayPointsModelAddOn = fLucyEnv.retrieveAddOnByClass(WayPointModelAddOn.class);
    return new WayPointFormatBar(findLspMapComponent(aView),
                                 fProperties,
                                 fShortPrefix,
                                 wayPointsModelAddOn.getFormat().getDefaultModelDescriptorFactories()[0],
                                 fLucyEnv);
  }
 
  private TLcyLspMapComponent findLspMapComponent(ILcdView aView) {
    ILcyGenericMapComponent mapComponent = fLucyEnv.getCombinedMapManager().findMapComponent(aView);
    return mapComponent instanceof TLcyLspMapComponent ? (TLcyLspMapComponent) mapComponent : null;
  }
}
