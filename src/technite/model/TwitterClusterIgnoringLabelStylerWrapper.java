package technite.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.luciad.model.transformation.clustering.TLcdCluster;
import com.luciad.view.lightspeed.TLspContext;
import com.luciad.view.lightspeed.style.styler.ALspLabelStyleCollector;
import com.luciad.view.lightspeed.style.styler.ALspStyleCollector;
import com.luciad.view.lightspeed.style.styler.ILspCustomizableStyler;
import com.luciad.view.lightspeed.style.styler.ILspStyleChangeListener;
import com.luciad.view.lightspeed.style.styler.ILspStyler;
import com.luciad.view.lightspeed.style.styler.TLspCustomizableStyle;

import samples.lightspeed.common.LabelStylerWrapper;


public class TwitterClusterIgnoringLabelStylerWrapper extends LabelStylerWrapper implements ILspCustomizableStyler {

	  private final ILspStyler fDelegate;

	  public TwitterClusterIgnoringLabelStylerWrapper(ILspStyler aDelegate) {
	    super(aDelegate);
	    System.out.println("TwitterClusterIgnoringLabelStylerWrapper - construtor");
	    fDelegate = aDelegate;
	  }

	  @Override
	  public void style(Collection<?> aObjects, ALspLabelStyleCollector aStyleCollector, TLspContext aContext) {
	    List<Object> nonClusteredObjects = new ArrayList<>();
	    System.out.println("TwitterClusterIgnoringLabelStylerWrapper - style");
	    for (Object object : aObjects) {
	      if (!(object instanceof TLcdCluster)) {
	    	  System.out.println("TwitterClusterIgnoringLabelStylerWrapper - style - "+object.toString());
	        nonClusteredObjects.add(object);
	      }
	    }
	    super.style(nonClusteredObjects, aStyleCollector, aContext);
	  }

	  @Override
	  public Collection<TLspCustomizableStyle> getStyles() {
	    if (fDelegate instanceof ILspCustomizableStyler) {
	      return ((ILspCustomizableStyler) fDelegate).getStyles();
	    }
	    return Collections.emptySet();
	  }

	@Override
	public void addStyleChangeListener(ILspStyleChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeStyleChangeListener(ILspStyleChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void style(Collection<?> arg0, ALspStyleCollector arg1, TLspContext arg2) {
		// TODO Auto-generated method stub
		
	}
	}
