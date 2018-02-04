package net.sickill.finishhim;

import net.sickill.finishhim.FinishHimCompletor;

import java.util.HashMap;
import org.gjt.sp.jedit.View;

public class FinishHimExecutor
{
    public HashMap<View, FinishHimCompletor> completors =
        new HashMap<View, FinishHimCompletor>();

    public void execute(View view)
    {
        if (! completors.containsKey(view))
        {
            completors.put(view, new FinishHimCompletor(view));
        }

        completors.get(view).complete();
    }
}