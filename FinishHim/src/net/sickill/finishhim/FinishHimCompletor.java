package net.sickill.finishhim;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;
import org.gjt.sp.util.Log;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class FinishHimCompletor
{
    public View view;
    public String noWordSep;
    public ArrayList<String> wordList;
    public Buffer buffer;
    public JEditTextArea textArea;
    public int caret;
    public String prefix;
    public int prefixLength;
    public int suggestedWordLength;
    public int caretLine;
    public int nextWordIndex;

    public FinishHimCompletor(View view_)
    {
        view = view_;
        noWordSep = "_";
        wordList = new ArrayList<String>();
        caret = 0;
        prefix = "";
        prefixLength = 0;
        suggestedWordLength = 0;
        caretLine = 0;
        nextWordIndex = 0;
    }

    public boolean firstInvocation()
    {
        return view.getInputHandler().getLastActionCount() == 1;
    }

    public void setup()
    {
        log("setup()");
        nextWordIndex = 0;
        buffer = view.getBuffer();
        textArea = view.getTextArea();
        caret = textArea.getCaretPosition();
        caretLine = textArea.getCaretLine();

        if(findPrefix())
        {
            log("found prefix: " + prefix);
            buildWordList();
        }
        else
        {
            log("empty prefix, leaving");
            wordList = new ArrayList<String>();
        }
    }

    public void complete()
    {
        log("complete()");

        if(firstInvocation())
        {
            setup();
        }

        if(!buffer.isEditable())
        {
            textArea.getToolkit().beep();
            return;
        }

        if(wordList.isEmpty())
        {
            textArea.getToolkit().beep();
        }
        else
        {
            String nextWord = wordList.get(nextWordIndex);

            log("    nextWord = '" + nextWord + "'");

            log("        caret = " + caret);
            log("        prefixLength = " + prefixLength);
            log("        suggestedWordLength = " + suggestedWordLength);

            textArea.setSelection(
                new Selection.Range(
                    caret,
                    caret - prefixLength + suggestedWordLength
                )
            );

            String tmp = nextWord.substring(prefixLength);

            log("    tmp = '" + tmp + "'");

            textArea.replaceSelection(nextWord.substring(prefixLength));

            suggestedWordLength = nextWord.length();

            nextWordIndex = (nextWordIndex + 1) % wordList.size();
        }
    }

    public HashSet<Buffer> getVisibleBuffers()
    {
        HashSet<Buffer> buffers = new HashSet<Buffer>();

        JEditVisitorAdapter adapter = new JEditVisitorAdapter(){
            public void visit(EditPane ep)
            {
                Buffer b = ep.getBuffer();

                buffers.add(b);
            }
        };

        jEdit.visit(adapter);

        return buffers;
    }

    public ArrayList<String> getWordsFromString(String s)
    {
        HashSet<String> set = new HashSet<String>();

        set.add(prefix);

        ArrayList<String> filtered = new ArrayList<String>();

        for(String x : s.split("[^\\w" + noWordSep + "]+"))
        {
            if(x.startsWith(prefix) && !set.contains(x))
            {
                set.add(x);
                filtered.add(x);
            }
        }

        return filtered;
    }

    public ArrayList<String> getWordsFromBuffer(Buffer buf, int start, int end)
    {
        return getWordsFromString(buf.getText(start, end - start));
    }

    public ArrayList<String> reverse(ArrayList<String> in)
    {
        int i = 0;
        int j = in.size() - 1;

        if(in.size() == 0) return in;

        while(i < j)
        {
            String tmp = in.get(i);
            in.set(i, in.get(j));
            in.set(j, tmp);

            i += 1;
            j -= 1;
        }

        return in;
    }

    public void buildWordList()
    {
        log("buildWordList()");

        wordList = reverse(getWordsFromBuffer(buffer, 0, caret));

        HashSet<String> set = new HashSet<String>();

        set.addAll(wordList);

        for(String x : getWordsFromBuffer(buffer, caret, buffer.getLength()))
        {
            if(!set.contains(x))
            {
                set.add(x);
                wordList.add(x);
            }
        }

        for(Buffer b : getVisibleBuffers())
        {
            for(String x : getWordsFromBuffer(b, 0, b.getLength()))
            {
                if(!set.contains(x))
                {
                    set.add(x);
                    wordList.add(x);
                }
            }
        }

        for(String x : wordList)
        {
            log("    " + x);
        }
    }

    public boolean findPrefix()
    {
        log("findPrefix()");

        String line = buffer.getLineSegment(caretLine).toString();

        int dot = caret - buffer.getLineStartOffset(caretLine);

        if(dot <= 0) return false;

        char c = line.charAt(dot - 1);

        String str = new String();

        str += c;

        if(!Character.isLetterOrDigit(c) && !noWordSep.contains(str)) return false;

        int wordStartPos = TextUtilities.findWordStart(line, dot - 1, "_");

        String prfx = line.subSequence(wordStartPos, dot).toString();

        if(prfx.length() == 0) return false;

        prefix = prfx.toString();

        prefixLength = prefix.length();

        suggestedWordLength = prefixLength;

        return true;
    }

    public void log(String msg)
    {
        Log.log(Log.DEBUG, this, msg);
    }
}

