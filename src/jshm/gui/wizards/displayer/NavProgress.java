package jshm.gui.wizards.displayer;

import java.awt.Container;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import jshm.gui.components.ProgressLogPanel;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.modules.wizard.InstructionsPanelImpl;
import org.netbeans.modules.wizard.NbBridge;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.WizardPage;

/**6
 * Show progress bar for deferred results, with a label showing percent done and progress bar.
 * 
 * <p>
 * <b><i><font color="red">This class is NOT AN API CLASS.  There is no
 * commitment that it will remain backward compatible or even exist in the
 * future.  The API of this library is in the packages <code>org.netbeans.api.wizard</code>
 * and <code>org.netbeans.spi.wizard</code></font></i></b>.

 * @author stanley@stanleyknutson.com
 */
@SuppressWarnings("unused")
public class NavProgress implements ResultProgressHandle
{
    private static final Logger logger =
        Logger.getLogger(NavProgress.class.getName());

    WizardDisplayerImpl parent;

    String              failMessage = null;
    
    boolean             isUseBusy = false;
    
    Container   ipanel = null;
    
    boolean             isInitialized = false;
    
    /** isRunning is true until finished or failed is called */
    boolean             isRunning = true;
    
    ProgressLogPanel progPanel = new ProgressLogPanel();
    
    NavProgress(WizardDisplayerImpl impl, boolean useBusy)
    {
        this.parent = impl;
        isUseBusy = useBusy;
    }
    
    public void addProgressComponents (Container panel)
    {
    	// not used
    }
    
    public ProgressLogPanel getProgPanel() {
    	return progPanel;
    }

    public void setProgress(final String description, final int currentStep, final int totalSteps)
    {
        progPanel.setProgress(description, currentStep, totalSteps);
    }

    public void setProgress(final int currentStep, final int totalSteps)
    {
    	progPanel.setProgress(currentStep, totalSteps);
    }

    public void setBusy (final String description)
    {
    	progPanel.setBusy(description);
    }
    
    private void invoke(Runnable r)
    {
        if (EventQueue.isDispatchThread())
        {
            r.run();
        }
        else
        {
            try
            {
                EventQueue.invokeAndWait(r);
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
                logger.severe("Error invoking operation " + ex.getClass().getName() + " " + ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                logger.severe("Error invoking operation " + ex.getClass().getName() + " " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void finished(final Object o)
    {
        isRunning = false;
        
        Runnable r = new Runnable()
        {
            public void run()
            {
                if (o instanceof Summary)
                {
                    Summary summary = (Summary) o;
                    parent.handleSummary(summary);
                    parent.setWizardResult(summary.getResult());
                }
                else if (parent.getDeferredResult() != null)
                {
                    parent.setWizardResult(o);

                    // handle result based on which button was pushed
                    parent.getButtonManager().deferredResultFinished(o);
                }
            }
        };
        invoke(r);
    }

    public void failed(final String message, final boolean canGoBack)
    {
        failMessage = message;
        isRunning = false;

        Runnable r = new Runnable()
        {
            public void run()
            {
                // cheap word wrap
                JLabel comp = new JLabel("<html><body>" + message); // NOI18N
                comp.setBorder(new EmptyBorder(5, 5, 5, 5));
                parent.setCurrentWizardPanel(comp);
                parent.getTtlLabel().setText(
                                             NbBridge
                                                 .getString("org/netbeans/api/wizard/Bundle", // NOI18N
                                                            WizardDisplayer.class, "Failed")); // NOI18N
                NavButtonManager bm = parent.getButtonManager();
                bm.deferredResultFailed(canGoBack);
            }
        };
        invoke(r);
    }

    public boolean isRunning()
    {
        return isRunning;
    }
}
