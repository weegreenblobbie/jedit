package net.sickill.finishhim;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import net.sickill.finishhim.FinishHimExecutor;

public class FinishHimPlugin extends EditPlugin {

    private static FinishHimExecutor exec = new FinishHimExecutor();

    public void start()
    {
        Log.log(Log.DEBUG, this, "start()");

        exec.execute(jEdit.getActiveView());
    }
}
