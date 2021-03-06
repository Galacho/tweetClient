package technite.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.luciad.model.transformation.clustering.TLcdCluster;
import com.luciad.shape.ALcdShape;
import com.luciad.view.lightspeed.TLspContext;
import com.luciad.view.lightspeed.layer.TLspPaintState;
import com.luciad.view.lightspeed.style.ALspStyle;
import com.luciad.view.lightspeed.style.ILspWorldElevationStyle;
import com.luciad.view.lightspeed.style.TLspIconStyle;
import com.luciad.view.lightspeed.style.styler.ALspStyleCollector;
import com.luciad.view.lightspeed.style.styler.ALspStyleTargetProvider;
import com.luciad.view.lightspeed.style.styler.ILspStyler;

import samples.gxy.clustering.ClusterIconProvider;
import samples.lightspeed.common.StylerWrapper;



public class TwitterClusterAwareStylerWrapper  extends StylerWrapper {

	  public enum Option {SHOW_CHILDREN, NONE}

	  private final ILspStyler fStyler;
	  private final TLspPaintState fPaintState;
	  private final Option fOption;

	  public TwitterClusterAwareStylerWrapper(ILspStyler aStyler, TLspPaintState aPaintState) {
	    this(aStyler, aPaintState, getOption(aPaintState));
	  }

	  private static Option getOption(TLspPaintState aPaintState) {
	    return aPaintState != TLspPaintState.REGULAR ? Option.SHOW_CHILDREN : Option.NONE;
	  }

	  public TwitterClusterAwareStylerWrapper(ILspStyler aStyler, TLspPaintState aPaintState, Option aOption) {
	    super(aStyler);
	    fStyler = aStyler;
	    fPaintState = aPaintState;
	    fOption = aOption;
	  }

	  @Override
	  public void style(Collection<?> aObjects, ALspStyleCollector aStyleCollector, TLspContext aContext) {
	    for (Object object : aObjects) {
	      if (object instanceof TLcdCluster) {
	        //Style cluster.
	        TLcdCluster<?> cluster = (TLcdCluster<?>) object;
	        Object element = cluster.getComposingElements().iterator().next();
	        List<ALspStyle> stylingOfAnElement = extractAllStyles(Collections.singletonList(element), aContext);
	        List<ALspStyle> clusterStyling = getClusterStyling(cluster, stylingOfAnElement);
	        aStyleCollector.object(cluster)
	                       .geometry(new ALspStyleTargetProvider() {
	                         @Override
	                         public void getStyleTargetsSFCT(Object aObject, TLspContext aContext, List<Object> aResultSFCT) {
	                           aResultSFCT.add(ALcdShape.fromDomainObject(aObject));
	                         }

	                         @Override
	                         public void getEditTargetsSFCT(Object aObject, TLspContext aContext, List<Object> aResultSFCT) {
	                           //You should not be able to edit the cluster.
	                         }
	                       })
	                       .styles(clusterStyling)
	                       .submit();
	        if (showChildren()) {
	          //Style elements of cluster.
	          for (final Object composingElement : cluster.getComposingElements()) {
	            Map<ALspStyleTargetProvider, List<ALspStyle>> styling = extractStyling(Collections.singletonList(composingElement), aContext);
	            for (final Map.Entry<ALspStyleTargetProvider, List<ALspStyle>> entry : styling.entrySet()) {
	              ALspStyleTargetProvider styleTargetProvider;
	              if (entry.getKey() != null) {
	                styleTargetProvider = new ALspStyleTargetProvider() {
	                  @Override
	                  public void getStyleTargetsSFCT(Object aObject, TLspContext aContext, List<Object> aResultSFCT) {
	                    entry.getKey().getStyleTargetsSFCT(composingElement, aContext, aResultSFCT);
	                  }

	                  @Override
	                  public void getEditTargetsSFCT(Object aObject, TLspContext aContext, List<Object> aResultSFCT) {
	                    //We only paint the contents of the cluster.  It is not meant to be edited.
	                  }
	                };
	              } else {
	                styleTargetProvider = new ALspStyleTargetProvider() {
	                  @Override
	                  public void getStyleTargetsSFCT(Object aObject, TLspContext aContext, List<Object> aResultSFCT) {
	                    aResultSFCT.add(composingElement);
	                  }

	                  @Override
	                  public void getEditTargetsSFCT(Object aObject, TLspContext aContext, List<Object> aResultSFCT) {
	                    //We only paint the contents of the cluster.  It is not meant to be edited.
	                  }
	                };
	              }
	              aStyleCollector.object(cluster)
	                             .geometry(styleTargetProvider)
	                             .styles(getElementStyling(entry.getValue()))
	                             .submit();
	            }
	          }
	        }
	      } else {
	        fStyler.style(Collections.singletonList(object), aStyleCollector, aContext);
	      }
	    }
	  }

	  protected boolean showChildren() {
	    return fOption == Option.SHOW_CHILDREN;
	  }

	  protected List<ALspStyle> getElementStyling(List<ALspStyle> aOriginalSelectedElementStyling) {
	    return aOriginalSelectedElementStyling;
	  }

	  private List<ALspStyle> extractAllStyles(Collection<?> aObjects, TLspContext aContext) {
	    InternalStyleCollector styleCollector = new InternalStyleCollector(aObjects);
	    fStyler.style(aObjects, styleCollector, aContext);
	    return styleCollector.getStyles();
	  }

	  private Map<ALspStyleTargetProvider, List<ALspStyle>> extractStyling(Collection<?> aObjects, TLspContext aContext) {
	    InternalStyleCollector styleCollector = new InternalStyleCollector(aObjects);
	    fStyler.style(aObjects, styleCollector, aContext);
	    return styleCollector.getStylesMap();
	  }

	  protected List<ALspStyle> getClusterStyling(TLcdCluster<?> aCluster, List<ALspStyle> aStylingOfAComposingElement) {
	    return Collections.singletonList(createClusterStyle(aCluster));
	  }

	  private ALspStyle createClusterStyle(TLcdCluster aCluster) {
	    ClusterIconProvider iconProvider = new ClusterIconProvider(isSelected());
	    return TLspIconStyle.newBuilder()
	                        .icon(iconProvider.getIcon(aCluster))
	                        .elevationMode(ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
	                        .zOrder(5)
	                        .build();
	  }

	  protected final boolean isSelected() {
	    return fPaintState != TLspPaintState.REGULAR;
	  }

	  private static class InternalStyleCollector extends ALspStyleCollector {

	    private final Map<ALspStyleTargetProvider, List<ALspStyle>> fStyles = new HashMap<>();

	    protected InternalStyleCollector(Collection<?> aObjectsToStyle) {
	      super(aObjectsToStyle);
	    }

	    @Override
	    protected void submitImpl() {
	      ALspStyleTargetProvider styleTargetProvider = super.getStyleTargetProvider();
	      List<ALspStyle> styles = fStyles.get(styleTargetProvider);
	      if (styles == null) {
	        styles = new ArrayList<>();
	      }
	      Set<ALspStyle> temp = new HashSet<>(styles);
	      temp.addAll(super.getStyles());
	      fStyles.put(styleTargetProvider, new ArrayList<>(temp));
	    }

	    @Override
	    public List<ALspStyle> getStyles() {
	      List<ALspStyle> result = new ArrayList<>();
	      for (List<ALspStyle> styles : fStyles.values()) {
	        result.addAll(styles);
	      }
	      return result;
	    }

	    public Map<ALspStyleTargetProvider, List<ALspStyle>> getStylesMap() {
	      return fStyles;
	    }

	  }

	}
