package technite.waypoint;

import com.luciad.lucy.addons.ALcyFormatAddOn;
import com.luciad.lucy.format.ALcyFormat;
import com.luciad.lucy.format.TLcySafeGuardFormatWrapper;
import com.luciad.lucy.util.ALcyTool;

public class WayPointModelAddOn extends ALcyFormatAddOn {
	 
	  public WayPointModelAddOn() {
	    super(ALcyTool.getLongPrefix(WayPointModelAddOn.class),
	          ALcyTool.getShortPrefix(WayPointModelAddOn.class));
	    System.out.println("WayPointModelAddOn");
	  }
	 
	  @Override
	  protected ALcyFormat createBaseFormat() {
		  
		System.out.println("WayPointModelAddOn - createBaseFormat");
	    return new WayPointModelFormat(getLucyEnv(),
	                                   getLongPrefix(),
	                                   getShortPrefix(),
	                                   getPreferences());
	  }
	 
	  @Override
	  protected ALcyFormat createFormatWrapper(ALcyFormat aBaseFormat) {
		  System.out.println("WayPointModelAddOn - createFormatWrapper");
	    return new TLcySafeGuardFormatWrapper(aBaseFormat);
	  }
	}
