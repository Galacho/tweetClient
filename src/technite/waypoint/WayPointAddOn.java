package technite.waypoint;

import com.luciad.lucy.addons.lightspeed.ALcyLspFormatAddOn;
import com.luciad.lucy.format.lightspeed.ALcyLspFormat;
import com.luciad.lucy.format.lightspeed.TLcyLspSafeGuardFormatWrapper;
import com.luciad.lucy.util.ALcyTool;
import com.luciad.model.ILcdModel;
import com.luciad.util.ILcdFilter;

public class WayPointAddOn extends ALcyLspFormatAddOn {
	 
	  public WayPointAddOn() {
	    super(ALcyTool.getLongPrefix(WayPointAddOn.class),
	          ALcyTool.getShortPrefix(WayPointAddOn.class));
	  }
	 
	  @Override
	  protected ALcyLspFormat createBaseFormat() {
	    ILcdFilter<ILcdModel> modelFilter =
	        (ILcdFilter<ILcdModel>) aModel -> "CWP".equals(aModel.getModelDescriptor().getTypeName());
	 
	    return new WayPointFormat(getLucyEnv(),
	                              getLongPrefix(),
	                              getShortPrefix(),
	                              getPreferences(),
	                              modelFilter);
	  }
	 
	  @Override
	  protected ALcyLspFormat createFormatWrapper(ALcyLspFormat aBaseFormat) {
	    return new TLcyLspSafeGuardFormatWrapper(aBaseFormat);
	  }
	 
	}