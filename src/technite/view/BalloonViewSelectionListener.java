package technite.view;


import com.luciad.util.ILcdSelectionListener;
import com.luciad.util.TLcdSelectionChangedEvent;
import com.luciad.view.*;
import com.luciad.view.swing.ALcdBalloonDescriptor;
import com.luciad.view.swing.ALcdBalloonManager;
import com.luciad.view.swing.TLcdModelElementBalloonDescriptor;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

/**
 * Listener which listens to selection changes and when a single object is selected, it is set on
 * the balloon manager.
 *
 * Typical use:
 * <pre><code class="java">
 * BalloonViewSelectionListener balloonListener = new BalloonViewSelectionListener( view, balloonManager );
 * view.getRootNode().addHierarchySelectionListener( balloonListener );
 * view.getRootNode().addHierarchyPropertyChangeListener( balloonListener );
 * view.getRootNode().addHierarchyLayeredListener( balloonListener );
 * </code></pre>
 */
public class BalloonViewSelectionListener implements ILcdSelectionListener, ILcdLayeredListener,
                                                     PropertyChangeListener {
  /**
   * Use a delay to show the balloon
   */
  private final Timer fTimer;
  private static final int TIMER_DELAY = 150;

  private final ILcdView fView;
  private final ALcdBalloonManager fBalloonManager;

  private ALcdBalloonDescriptor fLastDescriptor;

  /**
   * Creates a new <code>ViewSelectionBalloonListener</code> instance.
   * @param aView the view that contains the elements that are related to this <code>ILcdSelectionListener</code>
   * @param aBalloonManager a balloon manager to notify when an element on the view has been set.
   */
  public BalloonViewSelectionListener(ILcdView aView, ALcdBalloonManager aBalloonManager) {
    if (aView == null || aBalloonManager == null) {
      throw new IllegalArgumentException("Balloon selection listener: view and balloon manager cannot be null");
    }
    fBalloonManager = aBalloonManager;
    fView = aView;

    fTimer = new Timer(TIMER_DELAY, new TimerListener());
    fTimer.setRepeats(false);
  }

  /**
   * Triggers an update for the current balloon when a change in selection is triggered.
   * @param aSelectionEvent a <code>TLcdSelectionChangedEvent</code> detailing the changes
   */
  @Override
  public void selectionChanged(TLcdSelectionChangedEvent aSelectionEvent) {
    notifyUpdate();
  }

  /**
   *  Triggers an update for the current balloon when a layer was added or removed.
   * @param e a <code>TLcdLayeredEvent</code>
   */
  @Override
  public void layeredStateChanged(TLcdLayeredEvent e) {
    if (e.getID() == TLcdLayeredEvent.LAYER_REMOVED || e.getID() == TLcdLayeredEvent.LAYER_ADDED) {
      notifyUpdate();
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if ("visible".equals(evt.getPropertyName())) {
      notifyUpdate();
    }
  }

  /**
   * Method that is called when the state of the balloon should be updated. It will execute the
   * update with a small delay, allowing new calls to this method to delay the update.
   */
  private void notifyUpdate() {
    fLastDescriptor = fBalloonManager.getBalloonDescriptor();
    if (fTimer.isRunning()) {
      fTimer.restart();
    } else {
      fTimer.start();
    }
  }

  /**
   * Shows a balloon if only one object is selected on the view.
   */
  private class TimerListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent aActionEvent) {
      if (!isNewDescriptorSet(fBalloonManager)) {
        TLcdDomainObjectContext selectedObject = retrieveSingleSelectedObject();
        if (selectedObject != null && selectedObject.getLayer().isVisible()) {
          fBalloonManager.setBalloonDescriptor(new TLcdModelElementBalloonDescriptor(selectedObject));
        } else {
          fBalloonManager.setBalloonDescriptor(null);
        }
      }
      fLastDescriptor = null;
    }

    private boolean isNewDescriptorSet(ALcdBalloonManager aBalloonManager) {
      ALcdBalloonDescriptor descriptor = aBalloonManager.getBalloonDescriptor();
      if (fLastDescriptor == null) {
        return !(descriptor == null);
      } else {
        return descriptor == null || fLastDescriptor != descriptor;
      }
    }

    private TLcdDomainObjectContext retrieveSingleSelectedObject() {
      Object selectedObject = null;
      ILcdLayer selectedLayer = null;

      if (fView instanceof ILcdLayered) {
        for (ILcdLayer layer : ((ILcdLayered) fView).getLayers()) {
          Enumeration enumeration = layer.selectedObjects();
          while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();
            if (selectedObject == null) {
              selectedLayer = layer;
              selectedObject = object;
            } else {
              // multiple selected objects found
              return null;
            }
          }
        }
      }
      return selectedObject == null ? null : new TLcdDomainObjectContext(selectedObject, selectedLayer.getModel(), selectedLayer, fView);
    }
  }
}

