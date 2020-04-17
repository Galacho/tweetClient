package technite.waypoint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
 
import javax.swing.JLabel;
 
import com.luciad.datamodel.ILcdDataObject;
import com.luciad.lucy.ILcyLucyEnv;
import com.luciad.lucy.gui.TLcyTwoColumnLayoutBuilder;
import com.luciad.lucy.gui.customizer.ALcyDomainObjectCustomizerPanel;
import com.luciad.lucy.util.context.TLcyDomainObjectContext;
import com.luciad.model.ILcdModel;
import com.luciad.shape.ALcdShape;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.shape3D.TLcdLonLatHeightPoint;
import com.luciad.util.ALcdWeakPropertyChangeListener;
import com.luciad.util.ILcdFilter;
import com.luciad.util.concurrent.TLcdLockUtil;
 
import samples.lucy.text.StringFormat;
import samples.lucy.util.ValidatingTextField;
 
public class WayPointCustomizerPanel extends ALcyDomainObjectCustomizerPanel {
 
  private static final ILcdFilter WAYPOINT_DOMAIN_OBJECT_FILTER = new ILcdFilter() {
    @Override
    public boolean accept(Object aObject) {
      if (aObject instanceof TLcyDomainObjectContext) {
        TLcyDomainObjectContext context = (TLcyDomainObjectContext) aObject;
        //Check if the model of the domain object is a waypoint model
        return "CWP".equals(context.getModel().getModelDescriptor().getTypeName());
      }
      return false;
    }
  };
 
  private ValidatingTextField fIdentifierField;
  private ValidatingTextField fLocationField;
  private ValidatingTextField fHeightField;
 
 
  /**
   * This boolean field is used to avoid loops:
   * <ul>
   *   <li>When the user updates the waypoint on the map,
   *   the customizer panel will update the contents of the text fields.</li>
   *   <li>The listener attached to the text fields would detect this change,
   *   and indicate that the user made a change in the text fields (which isn't the case).</li>
   * </ul>
   * This boolean is used to indicate when the customizer panel itself is updating the text fields,
   * allowing the text field listener to distinguish between user-made changes
   * and changes made by the panel itself.
   */
  boolean fUpdatingUI = false;
 
  public WayPointCustomizerPanel(ILcyLucyEnv aLucyEnv) {
    super(WAYPOINT_DOMAIN_OBJECT_FILTER, "Way points");
    //Create the text fields and add them to this panel
    initUI(aLucyEnv);
 
    //Create a listener to detect changes made by the user in the text fields
    PropertyChangeListener textFieldListener = evt -> {
      if (!fUpdatingUI) {
        setChangesPending(true);
      }
    };
 
    //Install the listener on the text fields
    fIdentifierField.addPropertyChangeListener("value", textFieldListener);
    fLocationField.addPropertyChangeListener("value", textFieldListener);
    fHeightField.addPropertyChangeListener("value", textFieldListener);
    aLucyEnv.addPropertyChangeListener(new PointFormatListener(this));
    aLucyEnv.addPropertyChangeListener(new AltitudeFormatListener(this));
  }
 
  private void initUI(ILcyLucyEnv aLucyEnv) {
    fIdentifierField = new ValidatingTextField(new StringFormat(), aLucyEnv);
    fLocationField = new ValidatingTextField(aLucyEnv.getDefaultLonLatPointFormat(), aLucyEnv);
    fHeightField = new ValidatingTextField(aLucyEnv.getDefaultAltitudeFormat(), aLucyEnv);
 
    TLcyTwoColumnLayoutBuilder.newBuilder()
                              .addTitledSeparator("Way point")
                              .row()
                              .columnOne(new JLabel("Identifier"), fIdentifierField)
                              .build()
                              .row()
                              .columnOne(new JLabel("Location"), fLocationField)
                              .build()
                              .row()
                              .columnOne(new JLabel("Height"), fHeightField)
                              .build()
                              .populate(this);
  }
 
  @Override
  protected void updateCustomizerPanelFromObject(boolean aPanelEditable) {
    fIdentifierField.setEditable(aPanelEditable);
    fLocationField.setEditable(aPanelEditable);
    fHeightField.setEditable(aPanelEditable);
 
    boolean old = fUpdatingUI;
    try {
      //Switch the flag to indicate we are currently updating the panel
      //and the changes to the text fields are not made by the user
      fUpdatingUI = true;
      ILcdDataObject waypoint = (ILcdDataObject) getDomainObject();
      if (waypoint != null) {
        //Take a read lock so that we can safely read the values from the domain object
        try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.readLock(getModel())) {
          fIdentifierField.setValue(waypoint.getValue("identifier"));
          TLcdLonLatHeightPoint location = (TLcdLonLatHeightPoint) ALcdShape.fromDomainObject(waypoint);
          fLocationField.setValue(location);
          fHeightField.setValue(location.getZ());
        }
      } else {
        fIdentifierField.setValue("");
        fLocationField.setValue(null);
        fHeightField.setValue(0);
      }
    } finally {
      //Restore the state of the flag
      fUpdatingUI = old;
    }
  }
 
  @Override
  protected boolean applyChangesImpl() {
    ILcdDataObject waypoint = (ILcdDataObject) getDomainObject();
    if (waypoint != null) {
      ILcdModel model = getModel();
      //Changing a model element requires a write lock
      try (TLcdLockUtil.Lock autoUnlock = TLcdLockUtil.writeLock(model)) {
        waypoint.setValue("identifier", fIdentifierField.getValue());
        TLcdLonLatHeightPoint location = (TLcdLonLatHeightPoint) ALcdShape.fromDomainObject(waypoint);
        ILcdPoint updatedLocation = (ILcdPoint) fLocationField.getValue();
        double height = (double) fHeightField.getValue();
        location.move3D(updatedLocation.getX(), updatedLocation.getY(), height);
        model.elementChanged(waypoint, ILcdModel.FIRE_LATER);
      } finally {
        model.fireCollectedModelChanges();
      }
    }
    return true;
  }
 
  private void updatePointFormat(Format aPointFormat) {
    boolean old = fUpdatingUI;
    try {
      fUpdatingUI = true;
      fLocationField.setFormat(aPointFormat, fLocationField.getValue());
    } finally {
      fUpdatingUI = old;
    }
  }
 
  private void updateAltitudeFormat(Format aAltitudeFormat) {
    boolean old = fUpdatingUI;
    try {
      fUpdatingUI = true;
      fHeightField.setFormat(aAltitudeFormat, fHeightField.getValue());
    } finally {
      fUpdatingUI = old;
    }
  }
 
 
  /**
   * The location field should be formatted using the point format exposed on the Lucy back-end.
   * When this format changes, the UI must be updated.
   */
  private static class PointFormatListener extends
                                           ALcdWeakPropertyChangeListener<WayPointCustomizerPanel> {
 
    private PointFormatListener(WayPointCustomizerPanel aObjectToModify) {
      super(aObjectToModify);
    }
 
    @Override
    protected void propertyChangeImpl(WayPointCustomizerPanel aWayPointCustomizerPanel, PropertyChangeEvent aPropertyChangeEvent) {
      String propertyName = aPropertyChangeEvent.getPropertyName();
      if ("defaultLonLatPointFormat".equals(propertyName)) {
        aWayPointCustomizerPanel.updatePointFormat((Format) aPropertyChangeEvent.getNewValue());
      }
    }
  }
 
  /**
   * The altitude field should be formatted using the altitude format exposed on the Lucy back-end.
   * When this format changes, the UI must be updated
   */
  private static class AltitudeFormatListener extends ALcdWeakPropertyChangeListener<WayPointCustomizerPanel> {
    private AltitudeFormatListener(WayPointCustomizerPanel aObjectToModify) {
      super(aObjectToModify);
    }
 
    @Override
    protected void propertyChangeImpl(WayPointCustomizerPanel aWayPointCustomizerPanel, PropertyChangeEvent aPropertyChangeEvent) {
      String propertyName = aPropertyChangeEvent.getPropertyName();
      if ("defaultAltitudeFormat".equals(propertyName) || "defaultUserAltitudeUnit".equals(propertyName)) {
        aWayPointCustomizerPanel.updateAltitudeFormat(((ILcyLucyEnv) aPropertyChangeEvent.getSource()).getDefaultAltitudeFormat());
      }
    }
  }
 
}