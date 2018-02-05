package net.sickill.finishhim;

import net.sickill.finishhim.FinishHimCompletor;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import java.util.HashMap;


public class FinishHimExecutor
{
    private static HashMap<View, FinishHimCompletor> map =
        new HashMap<View, FinishHimCompletor>();

    public static void execute(View view)
    {
        if( ! map.containsKey(view))
        {
            map.put(view, new FinishHimCompletor(view));
        }

        map.get(view).complete();
    }
}