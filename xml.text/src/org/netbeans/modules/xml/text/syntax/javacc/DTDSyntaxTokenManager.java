/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.text.syntax.javacc;
import java.io.*;
import org.netbeans.modules.xml.text.syntax.javacc.lib.*;

public class DTDSyntaxTokenManager implements DTDSyntaxConstants {

    //!!! enter proper bridge
    public final class Bridge extends DTDSyntaxTokenManager implements JJSyntaxInterface, JJConstants {
        public Bridge() {
            super(null);
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~ TEXT BASED SHARING START ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private transient String myimage = "";  //contais image of last scanned [partial] token // NOI18N
    private transient String lastImage = ""; // NOI18N
    private transient int id;

    private int lastValidState; //contains last correct state
                                //state may become incorect if EOB is returned
                                //due to buffer end e.g.
                                //(a) <! moves to IN_DECL
                                //(b) <!-- moves to IN_COMMENT
                                //if (a) is followed by EOB that
                                //token manager enters illegal state
    
    /** Return current state of lexan. 
     * There will be probably necessary simple ID mappe among
     * Syntax's state IDs with reserved INIT(-1) and JavaCC DEFAULT(variable often the highest one).
     */
    public final int getState() {
        return curLexState;
    }

    /** Return length of last recognized token. ?? SKIP*/
    public final int getLength() {
        return myimage.length();
    }

    /** Return last token. */
    public final String getImage() {
        return myimage.toString();
    }

    /** Set state info to folowing one. */
    public final void setStateInfo(int[] state) {
        if (states == null) return;
        int[] newstate = new int[states.length];
        System.arraycopy(state,0,newstate,0,states.length);
        lastValidState = state[states.length]; //restore lastValidState
        states = newstate;
    }

    /** return copy of current state. */
    public final int[] getStateInfo() {
        int[] state = new int[states.length + 1];
        System.arraycopy(states,0,state,0,states.length);
        state[states.length] = lastValidState;  //store lastValidState
        return state;
    }


    /** Set input stream to folowing one
     *  and reset initial state.
     */
    public final void init(CharStream input) {
        ReInit((UCode_CharStream)input);
        lastValidState = getState();
    }

    /** Set input stream to folowing one
     *  and set current state.
     */
    public final void init(CharStream in, int state) {
        ReInit((UCode_CharStream)in, state);
        lastValidState = getState();
    }

    /** Syntax would want restore state on buffer boundaries. */
    public final void setState(int state) {
        lastValidState = state;
        SwitchTo(state == -1 ? defaultLexState : state); //fix deleting at document start
    }

    /** Prepare next token from stream. */
    public final void next() {
        try {
            Token tok = getNextToken();
            myimage = tok.image;
            id = tok.kind;
            if (id == EOF) { //??? EOF is visible just at Parser LEVEL
                setState(lastValidState);
                id = Bridge.JJ_EOF;
            }
            lastValidState = getState();

        } catch (TokenMgrError ex) {
            try {
                //is the exception caused by EOF?
                char ch = input_stream.readChar();
                input_stream.backup(1);

                myimage = input_stream.GetImage();
                if (Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                    System.err.println(getClass().toString() + " ERROR:" + getState() + ":'" + ch + "'"); // NOI18N
                id = Bridge.JJ_ERR;

            } catch (IOException eof) {

                myimage = input_stream.GetImage();
                id = Bridge.JJ_EOF;
            }
        }
    }

    /** Return ID of the last recognized token. */
    public int getID() {
        return id;
    }

    /** Return name of the last token. */
    public final String getName() {
        return tokenImage[id];
    }

    /** For testing purposes only. */
    public static void main(String args[]) throws Exception {

        InputStream in;
        int dump = 0;
        int dump2 = 1000;

        System.err.println("Got " + args.length + " arguments."); // NOI18N

        if (args.length != 0) {
            in = new FileInputStream(args[0]);
            if (args.length == 2) { //dump just requested line
                dump = Integer.parseInt(args[1]) - 1;
                dump2 = dump;
                System.err.println("Line to be dumped:" + dump); // NOI18N
            }
        } else  {
            System.err.println("One argument required."); // NOI18N
            return;
        }

        CharStream input = new ASCIICharStream(in, 0, 0);
        Bridge lex = null; //new XMLSyntaxTokenManager(input);

        int i = 25; //token count
        int id;
        int toks = 0;
        long time = System.currentTimeMillis();

        while (i/*--*/>0) {

            lex.next();
            id = lex.getID();

            toks++;
            switch (id) {
            case Bridge.JJ_EOF:
                System.err.println("EOF at " + lex.getState() + " " + lex.getImage()); // NOI18N
                System.err.println("Line: " + input.getLine() ); // NOI18N
                System.err.println("Tokens: " + toks ); // NOI18N
                System.err.println("Time: " + (System.currentTimeMillis() - time) ); // NOI18N
                return;

            default:
                if (dump <= input.getLine() && input.getLine() <= dump2)
                    System.err.println(" " + id + "@" + lex.getState() + ":" + lex.getImage() ); // NOI18N
            }

        }

    }

    //~~~~~~~~~~~~~~~~~~~~~ TEXT BASED SHARING END ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//##########################################################

//!!! enter proper code

  /**
    The analyzer may store information about state in this
    array. These will be used as Syntax state info.
  */
    private int[] states = new int[5];
    private final int IS_COMMENT = 0;
    private final int IS_CREF = 1;
    private final int IS_STRING = 2;
    private final int IS_CHARS = 3;
    private final int IS_PREF = 4;
    private final int jjStopStringLiteralDfa_9(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_9(int pos, long active0)
    {
        return jjMoveNfa_9(jjStopStringLiteralDfa_9(pos, active0), pos + 1);
    }
    private final int jjStopAtPos(int pos, int kind)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        return pos + 1;
    }
    private final int jjStartNfaWithStates_9(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_9(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_9()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 37:
                return jjStopAtPos(0, 54);
            case 91:
                return jjStopAtPos(0, 49);
            default :
                return jjMoveNfa_9(0, 0);
            }
    }
    private final void jjCheckNAdd(int state)
    {
        if (jjrounds[state] != jjround)
            {
                jjstateSet[jjnewStateCnt++] = state;
                jjrounds[state] = jjround;
            }
    }
    private final void jjAddStates(int start, int end)
    {
        do {
            jjstateSet[jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }
    private final void jjCheckNAddTwoStates(int state1, int state2)
    {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }
    private final void jjCheckNAddStates(int start, int end)
    {
        do {
            jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }
    private final void jjCheckNAddStates(int start)
    {
        jjCheckNAdd(jjnextStates[start]);
        jjCheckNAdd(jjnextStates[start + 1]);
    }
    static final long[] jjbitVec0 = {
        0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
    };
    static final long[] jjbitVec2 = {
        0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
    };
    private final int jjMoveNfa_9(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 14;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if ((0x7ff7f18fffff9ffL & l) != 0L)
                                        {
                                            if (kind > 48)
                                                kind = 48;
                                            jjCheckNAdd(12);
                                        }
                                    else if ((0x580080c600000000L & l) != 0L)
                                        {
                                            if (kind > 48)
                                                kind = 48;
                                            jjCheckNAdd(13);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 47)
                                                kind = 47;
                                            jjCheckNAdd(11);
                                        }
                                    break;
                                case 11:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 47;
                                    jjCheckNAdd(11);
                                    break;
                                case 12:
                                    if ((0x7ff7f18fffff9ffL & l) == 0L)
                                        break;
                                    kind = 48;
                                    jjCheckNAdd(12);
                                    break;
                                case 13:
                                    if ((0x580080c600000000L & l) == 0L)
                                        break;
                                    kind = 48;
                                    jjCheckNAdd(13);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if ((0xffffffffd7ffffffL & l) != 0L)
                                        {
                                            if (kind > 48)
                                                kind = 48;
                                            jjCheckNAdd(12);
                                        }
                                    else if (curChar == 93)
                                        {
                                            if (kind > 48)
                                                kind = 48;
                                            jjCheckNAdd(13);
                                        }
                                    if (curChar == 73)
                                        jjAddStates(0, 1);
                                    break;
                                case 1:
                                    if (curChar == 69 && kind > 46)
                                        kind = 46;
                                    break;
                                case 2:
                                    if (curChar == 68)
                                        jjCheckNAdd(1);
                                    break;
                                case 3:
                                    if (curChar == 85)
                                        jjstateSet[jjnewStateCnt++] = 2;
                                    break;
                                case 4:
                                    if (curChar == 76)
                                        jjstateSet[jjnewStateCnt++] = 3;
                                    break;
                                case 5:
                                    if (curChar == 67)
                                        jjstateSet[jjnewStateCnt++] = 4;
                                    break;
                                case 6:
                                    if (curChar == 78)
                                        jjstateSet[jjnewStateCnt++] = 5;
                                    break;
                                case 7:
                                    if (curChar == 82)
                                        jjCheckNAdd(1);
                                    break;
                                case 8:
                                    if (curChar == 79)
                                        jjstateSet[jjnewStateCnt++] = 7;
                                    break;
                                case 9:
                                    if (curChar == 78)
                                        jjstateSet[jjnewStateCnt++] = 8;
                                    break;
                                case 10:
                                    if (curChar == 71)
                                        jjstateSet[jjnewStateCnt++] = 9;
                                    break;
                                case 12:
                                    if ((0xffffffffd7ffffffL & l) == 0L)
                                        break;
                                    if (kind > 48)
                                        kind = 48;
                                    jjCheckNAdd(12);
                                    break;
                                case 13:
                                    if (curChar != 93)
                                        break;
                                    kind = 48;
                                    jjCheckNAdd(13);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                case 12:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 48)
                                        kind = 48;
                                    jjCheckNAdd(12);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 14 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_4(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_4(int pos, long active0)
    {
        return jjMoveNfa_4(jjStopStringLiteralDfa_4(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_4(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_4(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_4()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 39:
                return jjStopAtPos(0, 59);
            default :
                return jjMoveNfa_4(0, 0);
            }
    }
    private final int jjMoveNfa_4(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 1;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if ((0xffffff7ffffffbffL & l) == 0L)
                                        break;
                                    kind = 58;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    kind = 58;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 58)
                                        kind = 58;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjMoveStringLiteralDfa0_5()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 34:
                return jjStopAtPos(0, 60);
            case 39:
                return jjStopAtPos(0, 57);
            default :
                return 1;
            }
    }
    private final int jjMoveStringLiteralDfa0_6()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 34:
                return jjStopAtPos(0, 60);
            case 39:
                return jjStopAtPos(0, 57);
            default :
                return 1;
            }
    }
    private final int jjStopStringLiteralDfa_14(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_14(int pos, long active0)
    {
        return jjMoveNfa_14(jjStopStringLiteralDfa_14(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_14(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_14(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_14()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 63:
                jjmatchedKind = 23;
                return jjMoveStringLiteralDfa1_14(0x400000L);
            default :
                return jjMoveNfa_14(4, 0);
            }
    }
    private final int jjMoveStringLiteralDfa1_14(long active0)
    {
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_14(0, active0);
            return 1;
        }
        switch(curChar)
            {
            case 62:
                if ((active0 & 0x400000L) != 0L)
                    return jjStopAtPos(1, 22);
                break;
            default :
                break;
            }
        return jjStartNfa_14(0, active0);
    }
    private final int jjMoveNfa_14(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 4;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                    if ((0x7ff7f18fffff9ffL & l) != 0L)
                                        {
                                            if (kind > 20)
                                                kind = 20;
                                            jjCheckNAddStates(2, 4);
                                        }
                                    else if ((0x680080e600000000L & l) != 0L)
                                        {
                                            if (kind > 20)
                                                kind = 20;
                                            jjCheckNAddStates(2, 4);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 20)
                                                kind = 20;
                                            jjCheckNAddStates(2, 4);
                                        }
                                    else if (curChar == 60)
                                        {
                                            if (kind > 21)
                                                kind = 21;
                                            jjCheckNAdd(3);
                                        }
                                    break;
                                case 0:
                                    if ((0x7ff7f18fffff9ffL & l) == 0L)
                                        break;
                                    kind = 20;
                                    jjCheckNAddStates(2, 4);
                                    break;
                                case 1:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 20;
                                    jjCheckNAddStates(2, 4);
                                    break;
                                case 2:
                                    if ((0x680080e600000000L & l) == 0L)
                                        break;
                                    kind = 20;
                                    jjCheckNAddStates(2, 4);
                                    break;
                                case 3:
                                    if (curChar != 60)
                                        break;
                                    kind = 21;
                                    jjCheckNAdd(3);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                    if ((0xffffffffd7ffffffL & l) != 0L)
                                        {
                                            if (kind > 20)
                                                kind = 20;
                                            jjCheckNAddStates(2, 4);
                                        }
                                    else if ((0x28000000L & l) != 0L)
                                        {
                                            if (kind > 20)
                                                kind = 20;
                                            jjCheckNAddStates(2, 4);
                                        }
                                    break;
                                case 0:
                                    if ((0xffffffffd7ffffffL & l) == 0L)
                                        break;
                                    kind = 20;
                                    jjCheckNAddStates(2, 4);
                                    break;
                                case 2:
                                    if ((0x28000000L & l) == 0L)
                                        break;
                                    kind = 20;
                                    jjCheckNAddStates(2, 4);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 20)
                                        kind = 20;
                                    jjCheckNAddStates(2, 4);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_3(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_3(int pos, long active0)
    {
        return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_3(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_3(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_3()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 34:
                return jjStopAtPos(0, 62);
            default :
                return jjMoveNfa_3(0, 0);
            }
    }
    private final int jjMoveNfa_3(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 1;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if ((0xfffffffbfffffbffL & l) == 0L)
                                        break;
                                    kind = 61;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    kind = 61;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 61)
                                        kind = 61;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_0(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_0(int pos, long active0)
    {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_0(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_0(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_0()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            default :
                return jjMoveNfa_0(1, 0);
            }
    }
    private final int jjMoveNfa_0(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 4;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if ((0xfffffffffffffbffL & l) != 0L)
                                        {
                                            if (kind > 74)
                                                kind = 74;
                                        }
                                    if ((0x3ff000000000000L & l) != 0L)
                                        {
                                            if (kind > 72)
                                                kind = 72;
                                            jjCheckNAdd(0);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 73)
                                                kind = 73;
                                            jjCheckNAdd(2);
                                        }
                                    else if (curChar == 59)
                                        {
                                            if (kind > 73)
                                                kind = 73;
                                        }
                                    break;
                                case 0:
                                    if ((0x3ff000000000000L & l) == 0L)
                                        break;
                                    if (kind > 72)
                                        kind = 72;
                                    jjCheckNAdd(0);
                                    break;
                                case 2:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    if (kind > 73)
                                        kind = 73;
                                    jjCheckNAdd(2);
                                    break;
                                case 3:
                                    if ((0xfffffffffffffbffL & l) != 0L && kind > 74)
                                        kind = 74;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if (kind > 74)
                                        kind = 74;
                                    if ((0x7e0000007eL & l) != 0L)
                                        {
                                            if (kind > 72)
                                                kind = 72;
                                            jjCheckNAdd(0);
                                        }
                                    break;
                                case 0:
                                    if ((0x7e0000007eL & l) == 0L)
                                        break;
                                    if (kind > 72)
                                        kind = 72;
                                    jjCheckNAdd(0);
                                    break;
                                case 3:
                                    if (kind > 74)
                                        kind = 74;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 74)
                                        kind = 74;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1)
    {
        switch (pos)
            {
            case 0:
                if ((active1 & 0x4L) != 0L)
                    return 2;
                return -1;
            case 1:
                if ((active1 & 0x4L) != 0L)
                    return 3;
                return -1;
            default :
                return -1;
            }
    }
    private final int jjStartNfa_2(int pos, long active0, long active1)
    {
        return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0, active1), pos + 1);
    }
    private final int jjStartNfaWithStates_2(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_2(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_2()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 45:
                return jjMoveStringLiteralDfa1_2(0x4L);
            default :
                return jjMoveNfa_2(4, 0);
            }
    }
    private final int jjMoveStringLiteralDfa1_2(long active1)
    {
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_2(0, 0L, active1);
            return 1;
        }
        switch(curChar)
            {
            case 45:
                return jjMoveStringLiteralDfa2_2(active1, 0x4L);
            default :
                break;
            }
        return jjStartNfa_2(0, 0L, active1);
    }
    private final int jjMoveStringLiteralDfa2_2(long old1, long active1)
    {
        if (((active1 &= old1)) == 0L)
            return jjStartNfa_2(0, 0L, old1); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_2(1, 0L, active1);
            return 2;
        }
        switch(curChar)
            {
            case 62:
                if ((active1 & 0x4L) != 0L)
                    return jjStopAtPos(2, 66);
                break;
            default :
                break;
            }
        return jjStartNfa_2(1, 0L, active1);
    }
    private final int jjMoveNfa_2(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 5;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                    if ((0xffffdffffffffbffL & l) != 0L)
                                        {
                                            if (kind > 64)
                                                kind = 64;
                                            jjCheckNAddTwoStates(0, 1);
                                        }
                                    else if (curChar == 45)
                                        jjstateSet[jjnewStateCnt++] = 2;
                                    if (curChar == 45)
                                        jjCheckNAdd(0);
                                    break;
                                case 2:
                                    if ((0xffffdffffffffbffL & l) != 0L)
                                        {
                                            if (kind > 64)
                                                kind = 64;
                                            jjCheckNAddTwoStates(0, 1);
                                        }
                                    else if (curChar == 45)
                                        jjstateSet[jjnewStateCnt++] = 3;
                                    break;
                                case 0:
                                    if ((0xffffdffffffffbffL & l) == 0L)
                                        break;
                                    if (kind > 64)
                                        kind = 64;
                                    jjCheckNAddTwoStates(0, 1);
                                    break;
                                case 1:
                                    if (curChar == 45)
                                        jjCheckNAdd(0);
                                    break;
                                case 3:
                                    if ((0xbffffffffffffbffL & l) != 0L && kind > 65)
                                        kind = 65;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                case 0:
                                    if (kind > 64)
                                        kind = 64;
                                    jjCheckNAddTwoStates(0, 1);
                                    break;
                                case 2:
                                    if (kind > 64)
                                        kind = 64;
                                    jjCheckNAddTwoStates(0, 1);
                                    break;
                                case 3:
                                    if (kind > 65)
                                        kind = 65;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 64)
                                        kind = 64;
                                    jjCheckNAddTwoStates(0, 1);
                                    break;
                                case 2:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 64)
                                        kind = 64;
                                    jjCheckNAddTwoStates(0, 1);
                                    break;
                                case 3:
                                    if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 65)
                                        kind = 65;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_11(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_11(int pos, long active0)
    {
        return jjMoveNfa_11(jjStopStringLiteralDfa_11(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_11(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_11(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_11()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 37:
                return jjStopAtPos(0, 54);
            case 62:
                return jjStopAtPos(0, 41);
            default :
                return jjMoveNfa_11(4, 0);
            }
    }
    private final int jjMoveNfa_11(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 16;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                    if ((0x3fffe0defffff9ffL & l) != 0L)
                                        {
                                            if (kind > 40)
                                                kind = 40;
                                            jjCheckNAdd(15);
                                        }
                                    else if ((0x80001f0100000200L & l) != 0L)
                                        {
                                            if (kind > 39)
                                                kind = 39;
                                            jjCheckNAdd(14);
                                        }
                                    if (curChar == 35)
                                        jjstateSet[jjnewStateCnt++] = 10;
                                    break;
                                case 11:
                                    if (curChar == 35)
                                        jjstateSet[jjnewStateCnt++] = 10;
                                    break;
                                case 14:
                                    if ((0x80001f0100000200L & l) == 0L)
                                        break;
                                    kind = 39;
                                    jjCheckNAdd(14);
                                    break;
                                case 15:
                                    if ((0x3fffe0defffff9ffL & l) == 0L)
                                        break;
                                    if (kind > 40)
                                        kind = 40;
                                    jjCheckNAdd(15);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                    if ((0xefffffffffffffffL & l) != 0L)
                                        {
                                            if (kind > 40)
                                                kind = 40;
                                            jjCheckNAdd(15);
                                        }
                                    else if (curChar == 124)
                                        {
                                            if (kind > 39)
                                                kind = 39;
                                            jjCheckNAdd(14);
                                        }
                                    if (curChar == 65)
                                        jjstateSet[jjnewStateCnt++] = 12;
                                    else if (curChar == 69)
                                        jjstateSet[jjnewStateCnt++] = 3;
                                    break;
                                case 0:
                                    if (curChar == 89 && kind > 38)
                                        kind = 38;
                                    break;
                                case 1:
                                    if (curChar == 84)
                                        jjCheckNAdd(0);
                                    break;
                                case 2:
                                    if (curChar == 80)
                                        jjstateSet[jjnewStateCnt++] = 1;
                                    break;
                                case 3:
                                    if (curChar == 77)
                                        jjstateSet[jjnewStateCnt++] = 2;
                                    break;
                                case 5:
                                    if (curChar == 65 && kind > 38)
                                        kind = 38;
                                    break;
                                case 6:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 5;
                                    break;
                                case 7:
                                    if (curChar == 65)
                                        jjstateSet[jjnewStateCnt++] = 6;
                                    break;
                                case 8:
                                    if (curChar == 68)
                                        jjstateSet[jjnewStateCnt++] = 7;
                                    break;
                                case 9:
                                    if (curChar == 67)
                                        jjstateSet[jjnewStateCnt++] = 8;
                                    break;
                                case 10:
                                    if (curChar == 80)
                                        jjstateSet[jjnewStateCnt++] = 9;
                                    break;
                                case 12:
                                    if (curChar == 78)
                                        jjCheckNAdd(0);
                                    break;
                                case 13:
                                    if (curChar == 65)
                                        jjstateSet[jjnewStateCnt++] = 12;
                                    break;
                                case 14:
                                    if (curChar != 124)
                                        break;
                                    kind = 39;
                                    jjCheckNAdd(14);
                                    break;
                                case 15:
                                    if ((0xefffffffffffffffL & l) == 0L)
                                        break;
                                    if (kind > 40)
                                        kind = 40;
                                    jjCheckNAdd(15);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 4:
                                case 15:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 40)
                                        kind = 40;
                                    jjCheckNAdd(15);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 16 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_7(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_7(int pos, long active0)
    {
        return jjMoveNfa_7(jjStopStringLiteralDfa_7(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_7(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_7(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_7()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            default :
                return jjMoveNfa_7(1, 0);
            }
    }
    private final int jjMoveNfa_7(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 3;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if ((0xf7fffffefffff9ffL & l) != 0L)
                                        {
                                            if (kind > 55)
                                                kind = 55;
                                            jjCheckNAdd(0);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 56)
                                                kind = 56;
                                            jjCheckNAdd(2);
                                        }
                                    else if (curChar == 59)
                                        {
                                            if (kind > 56)
                                                kind = 56;
                                        }
                                    break;
                                case 0:
                                    if ((0xf7fffffefffff9ffL & l) == 0L)
                                        break;
                                    kind = 55;
                                    jjCheckNAdd(0);
                                    break;
                                case 2:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 56;
                                    jjCheckNAdd(2);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                case 0:
                                    kind = 55;
                                    jjCheckNAdd(0);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 55)
                                        kind = 55;
                                    jjCheckNAdd(0);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 3 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_1(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_1(int pos, long active0)
    {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_1(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_1(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_1()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            default :
                return jjMoveNfa_1(1, 0);
            }
    }
    private final int jjMoveNfa_1(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 4;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if ((0xfffffffffffffbffL & l) != 0L)
                                        {
                                            if (kind > 71)
                                                kind = 71;
                                        }
                                    if ((0x3ff000000000000L & l) != 0L)
                                        {
                                            if (kind > 69)
                                                kind = 69;
                                            jjCheckNAdd(0);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 70)
                                                kind = 70;
                                            jjCheckNAdd(2);
                                        }
                                    else if (curChar == 59)
                                        {
                                            if (kind > 70)
                                                kind = 70;
                                        }
                                    break;
                                case 0:
                                    if ((0x3ff000000000000L & l) == 0L)
                                        break;
                                    if (kind > 69)
                                        kind = 69;
                                    jjCheckNAdd(0);
                                    break;
                                case 2:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    if (kind > 70)
                                        kind = 70;
                                    jjCheckNAdd(2);
                                    break;
                                case 3:
                                    if ((0xfffffffffffffbffL & l) != 0L && kind > 71)
                                        kind = 71;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    kind = 71;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 71)
                                        kind = 71;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_15(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_15(int pos, long active0)
    {
        return jjMoveNfa_15(jjStopStringLiteralDfa_15(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_15(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_15(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_15()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 63:
                jjmatchedKind = 19;
                return jjMoveStringLiteralDfa1_15(0x40000L);
            default :
                return jjMoveNfa_15(6, 0);
            }
    }
    private final int jjMoveStringLiteralDfa1_15(long active0)
    {
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_15(0, active0);
            return 1;
        }
        switch(curChar)
            {
            case 62:
                if ((active0 & 0x40000L) != 0L)
                    return jjStopAtPos(1, 18);
                break;
            default :
                break;
            }
        return jjStartNfa_15(0, active0);
    }
    private final int jjMoveNfa_15(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 27;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 6:
                                    if ((0x5ffffffefffff9ffL & l) != 0L)
                                        {
                                            if (kind > 16)
                                                kind = 16;
                                            jjCheckNAdd(25);
                                        }
                                    else if ((0x2000000100000200L & l) != 0L)
                                        {
                                            if (kind > 17)
                                                kind = 17;
                                            jjCheckNAdd(26);
                                        }
                                    break;
                                case 25:
                                    if ((0x5ffffffefffff9ffL & l) == 0L)
                                        break;
                                    kind = 16;
                                    jjCheckNAdd(25);
                                    break;
                                case 26:
                                    if ((0x2000000100000200L & l) == 0L)
                                        break;
                                    kind = 17;
                                    jjCheckNAdd(26);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 6:
                                    if (kind > 16)
                                        kind = 16;
                                    jjCheckNAdd(25);
                                    if (curChar == 115)
                                        jjstateSet[jjnewStateCnt++] = 23;
                                    else if (curChar == 101)
                                        jjstateSet[jjnewStateCnt++] = 13;
                                    else if (curChar == 118)
                                        jjstateSet[jjnewStateCnt++] = 5;
                                    break;
                                case 0:
                                    if (curChar == 110 && kind > 15)
                                        kind = 15;
                                    break;
                                case 1:
                                    if (curChar == 111)
                                        jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                case 2:
                                    if (curChar == 105)
                                        jjstateSet[jjnewStateCnt++] = 1;
                                    break;
                                case 3:
                                    if (curChar == 115)
                                        jjstateSet[jjnewStateCnt++] = 2;
                                    break;
                                case 4:
                                    if (curChar == 114)
                                        jjstateSet[jjnewStateCnt++] = 3;
                                    break;
                                case 5:
                                    if (curChar == 101)
                                        jjstateSet[jjnewStateCnt++] = 4;
                                    break;
                                case 7:
                                    if (curChar == 103 && kind > 15)
                                        kind = 15;
                                    break;
                                case 8:
                                    if (curChar == 110)
                                        jjstateSet[jjnewStateCnt++] = 7;
                                    break;
                                case 9:
                                    if (curChar == 105)
                                        jjstateSet[jjnewStateCnt++] = 8;
                                    break;
                                case 10:
                                    if (curChar == 100)
                                        jjstateSet[jjnewStateCnt++] = 9;
                                    break;
                                case 11:
                                    if (curChar == 111)
                                        jjstateSet[jjnewStateCnt++] = 10;
                                    break;
                                case 12:
                                    if (curChar == 99)
                                        jjstateSet[jjnewStateCnt++] = 11;
                                    break;
                                case 13:
                                    if (curChar == 110)
                                        jjstateSet[jjnewStateCnt++] = 12;
                                    break;
                                case 14:
                                    if (curChar == 101)
                                        jjstateSet[jjnewStateCnt++] = 13;
                                    break;
                                case 15:
                                    if (curChar == 101 && kind > 15)
                                        kind = 15;
                                    break;
                                case 16:
                                    if (curChar == 110)
                                        jjstateSet[jjnewStateCnt++] = 15;
                                    break;
                                case 17:
                                    if (curChar == 111)
                                        jjstateSet[jjnewStateCnt++] = 16;
                                    break;
                                case 18:
                                    if (curChar == 108)
                                        jjstateSet[jjnewStateCnt++] = 17;
                                    break;
                                case 19:
                                    if (curChar == 97)
                                        jjstateSet[jjnewStateCnt++] = 18;
                                    break;
                                case 20:
                                    if (curChar == 100)
                                        jjstateSet[jjnewStateCnt++] = 19;
                                    break;
                                case 21:
                                    if (curChar == 110)
                                        jjstateSet[jjnewStateCnt++] = 20;
                                    break;
                                case 22:
                                    if (curChar == 97)
                                        jjstateSet[jjnewStateCnt++] = 21;
                                    break;
                                case 23:
                                    if (curChar == 116)
                                        jjstateSet[jjnewStateCnt++] = 22;
                                    break;
                                case 24:
                                    if (curChar == 115)
                                        jjstateSet[jjnewStateCnt++] = 23;
                                    break;
                                case 25:
                                    if (kind > 16)
                                        kind = 16;
                                    jjCheckNAdd(25);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 6:
                                case 25:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 16)
                                        kind = 16;
                                    jjCheckNAdd(25);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 27 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_10(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_10(int pos, long active0)
    {
        return jjMoveNfa_10(jjStopStringLiteralDfa_10(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_10(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_10(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_10()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 34:
                return jjStopAtPos(0, 60);
            case 39:
                return jjStopAtPos(0, 57);
            case 62:
                return jjStopAtPos(0, 45);
            default :
                return jjMoveNfa_10(5, 0);
            }
    }
    private final int jjMoveNfa_10(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 14;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 5:
                                    if ((0xbfffff7afffff9ffL & l) != 0L)
                                        {
                                            if (kind > 43)
                                                kind = 43;
                                            jjCheckNAdd(12);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 44)
                                                kind = 44;
                                            jjCheckNAdd(13);
                                        }
                                    break;
                                case 12:
                                    if ((0xbfffff7afffff9ffL & l) == 0L)
                                        break;
                                    kind = 43;
                                    jjCheckNAdd(12);
                                    break;
                                case 13:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 44;
                                    jjCheckNAdd(13);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 5:
                                    if (kind > 43)
                                        kind = 43;
                                    jjCheckNAdd(12);
                                    if (curChar == 80)
                                        jjstateSet[jjnewStateCnt++] = 10;
                                    else if (curChar == 83)
                                        jjstateSet[jjnewStateCnt++] = 4;
                                    break;
                                case 0:
                                    if (curChar == 77 && kind > 42)
                                        kind = 42;
                                    break;
                                case 1:
                                    if (curChar == 69)
                                        jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                case 2:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 1;
                                    break;
                                case 3:
                                    if (curChar == 83)
                                        jjstateSet[jjnewStateCnt++] = 2;
                                    break;
                                case 4:
                                    if (curChar == 89)
                                        jjstateSet[jjnewStateCnt++] = 3;
                                    break;
                                case 6:
                                    if (curChar == 67 && kind > 42)
                                        kind = 42;
                                    break;
                                case 7:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 6;
                                    break;
                                case 8:
                                    if (curChar == 76)
                                        jjstateSet[jjnewStateCnt++] = 7;
                                    break;
                                case 9:
                                    if (curChar == 66)
                                        jjstateSet[jjnewStateCnt++] = 8;
                                    break;
                                case 10:
                                    if (curChar == 85)
                                        jjstateSet[jjnewStateCnt++] = 9;
                                    break;
                                case 11:
                                    if (curChar == 80)
                                        jjstateSet[jjnewStateCnt++] = 10;
                                    break;
                                case 12:
                                    if (kind > 43)
                                        kind = 43;
                                    jjCheckNAdd(12);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 5:
                                case 12:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 43)
                                        kind = 43;
                                    jjCheckNAdd(12);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 14 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_13(int pos, long active0)
    {
        switch (pos)
            {
            case 0:
                if ((active0 & 0x1f000000L) != 0L)
                    {
                        jjmatchedKind = 29;
                        return 0;
                    }
                return -1;
            case 1:
                if ((active0 & 0x1f000000L) != 0L)
                    {
                        jjmatchedKind = 29;
                        jjmatchedPos = 1;
                        return 0;
                    }
                return -1;
            case 2:
                if ((active0 & 0x1f000000L) != 0L)
                    {
                        jjmatchedKind = 29;
                        jjmatchedPos = 2;
                        return 0;
                    }
                return -1;
            case 3:
                if ((active0 & 0x1f000000L) != 0L)
                    {
                        jjmatchedKind = 29;
                        jjmatchedPos = 3;
                        return 0;
                    }
                return -1;
            case 4:
                if ((active0 & 0x1f000000L) != 0L)
                    {
                        jjmatchedKind = 29;
                        jjmatchedPos = 4;
                        return 0;
                    }
                return -1;
            case 5:
                if ((active0 & 0x1000000L) != 0L)
                    return 0;
                if ((active0 & 0x1e000000L) != 0L)
                    {
                        jjmatchedKind = 29;
                        jjmatchedPos = 5;
                        return 0;
                    }
                return -1;
            case 6:
                if ((active0 & 0xe000000L) != 0L)
                    return 0;
                if ((active0 & 0x10000000L) != 0L)
                    {
                        jjmatchedKind = 29;
                        jjmatchedPos = 6;
                        return 0;
                    }
                return -1;
            default :
                return -1;
            }
    }
    private final int jjStartNfa_13(int pos, long active0)
    {
        return jjMoveNfa_13(jjStopStringLiteralDfa_13(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_13(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_13(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_13()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 37:
                return jjStopAtPos(0, 54);
            case 62:
                return jjStopAtPos(0, 33);
            case 65:
                return jjMoveStringLiteralDfa1_13(0x2000000L);
            case 68:
                return jjMoveStringLiteralDfa1_13(0x4000000L);
            case 69:
                return jjMoveStringLiteralDfa1_13(0x9000000L);
            case 78:
                return jjMoveStringLiteralDfa1_13(0x10000000L);
            case 91:
                return jjStopAtPos(0, 32);
            default :
                return jjMoveNfa_13(3, 0);
            }
    }
    private final int jjMoveStringLiteralDfa1_13(long active0)
    {
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_13(0, active0);
            return 1;
        }
        switch(curChar)
            {
            case 76:
                return jjMoveStringLiteralDfa2_13(active0, 0x8000000L);
            case 78:
                return jjMoveStringLiteralDfa2_13(active0, 0x1000000L);
            case 79:
                return jjMoveStringLiteralDfa2_13(active0, 0x14000000L);
            case 84:
                return jjMoveStringLiteralDfa2_13(active0, 0x2000000L);
            default :
                break;
            }
        return jjStartNfa_13(0, active0);
    }
    private final int jjMoveStringLiteralDfa2_13(long old0, long active0)
    {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_13(0, old0); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_13(1, active0);
            return 2;
        }
        switch(curChar)
            {
            case 67:
                return jjMoveStringLiteralDfa3_13(active0, 0x4000000L);
            case 69:
                return jjMoveStringLiteralDfa3_13(active0, 0x8000000L);
            case 84:
                return jjMoveStringLiteralDfa3_13(active0, 0x13000000L);
            default :
                break;
            }
        return jjStartNfa_13(1, active0);
    }
    private final int jjMoveStringLiteralDfa3_13(long old0, long active0)
    {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_13(1, old0); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_13(2, active0);
            return 3;
        }
        switch(curChar)
            {
            case 65:
                return jjMoveStringLiteralDfa4_13(active0, 0x10000000L);
            case 73:
                return jjMoveStringLiteralDfa4_13(active0, 0x1000000L);
            case 76:
                return jjMoveStringLiteralDfa4_13(active0, 0x2000000L);
            case 77:
                return jjMoveStringLiteralDfa4_13(active0, 0x8000000L);
            case 84:
                return jjMoveStringLiteralDfa4_13(active0, 0x4000000L);
            default :
                break;
            }
        return jjStartNfa_13(2, active0);
    }
    private final int jjMoveStringLiteralDfa4_13(long old0, long active0)
    {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_13(2, old0); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_13(3, active0);
            return 4;
        }
        switch(curChar)
            {
            case 69:
                return jjMoveStringLiteralDfa5_13(active0, 0x8000000L);
            case 73:
                return jjMoveStringLiteralDfa5_13(active0, 0x2000000L);
            case 84:
                return jjMoveStringLiteralDfa5_13(active0, 0x11000000L);
            case 89:
                return jjMoveStringLiteralDfa5_13(active0, 0x4000000L);
            default :
                break;
            }
        return jjStartNfa_13(3, active0);
    }
    private final int jjMoveStringLiteralDfa5_13(long old0, long active0)
    {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_13(3, old0); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_13(4, active0);
            return 5;
        }
        switch(curChar)
            {
            case 73:
                return jjMoveStringLiteralDfa6_13(active0, 0x10000000L);
            case 78:
                return jjMoveStringLiteralDfa6_13(active0, 0x8000000L);
            case 80:
                return jjMoveStringLiteralDfa6_13(active0, 0x4000000L);
            case 83:
                return jjMoveStringLiteralDfa6_13(active0, 0x2000000L);
            case 89:
                if ((active0 & 0x1000000L) != 0L)
                    return jjStartNfaWithStates_13(5, 24, 0);
                break;
            default :
                break;
            }
        return jjStartNfa_13(4, active0);
    }
    private final int jjMoveStringLiteralDfa6_13(long old0, long active0)
    {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_13(4, old0); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_13(5, active0);
            return 6;
        }
        switch(curChar)
            {
            case 69:
                if ((active0 & 0x4000000L) != 0L)
                    return jjStartNfaWithStates_13(6, 26, 0);
                break;
            case 79:
                return jjMoveStringLiteralDfa7_13(active0, 0x10000000L);
            case 84:
                if ((active0 & 0x2000000L) != 0L)
                    return jjStartNfaWithStates_13(6, 25, 0);
                else if ((active0 & 0x8000000L) != 0L)
                    return jjStartNfaWithStates_13(6, 27, 0);
                break;
            default :
                break;
            }
        return jjStartNfa_13(5, active0);
    }
    private final int jjMoveStringLiteralDfa7_13(long old0, long active0)
    {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_13(5, old0); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_13(6, active0);
            return 7;
        }
        switch(curChar)
            {
            case 78:
                if ((active0 & 0x10000000L) != 0L)
                    return jjStartNfaWithStates_13(7, 28, 0);
                break;
            default :
                break;
            }
        return jjStartNfa_13(6, active0);
    }
    private final int jjMoveNfa_13(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 3;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 3:
                                    if ((0x7ff7f18fffff9ffL & l) != 0L)
                                        {
                                            if (kind > 29)
                                                kind = 29;
                                            jjCheckNAdd(0);
                                        }
                                    else if ((0xb800808600000000L & l) != 0L)
                                        {
                                            if (kind > 31)
                                                kind = 31;
                                            jjCheckNAdd(2);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 30)
                                                kind = 30;
                                            jjCheckNAdd(1);
                                        }
                                    break;
                                case 0:
                                    if ((0x7ff7f18fffff9ffL & l) == 0L)
                                        break;
                                    kind = 29;
                                    jjCheckNAdd(0);
                                    break;
                                case 1:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 30;
                                    jjCheckNAdd(1);
                                    break;
                                case 2:
                                    if ((0xb800808600000000L & l) == 0L)
                                        break;
                                    kind = 31;
                                    jjCheckNAdd(2);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 3:
                                    if ((0xffffffffd7ffffffL & l) != 0L)
                                        {
                                            if (kind > 29)
                                                kind = 29;
                                            jjCheckNAdd(0);
                                        }
                                    else if (curChar == 93)
                                        {
                                            if (kind > 31)
                                                kind = 31;
                                            jjCheckNAdd(2);
                                        }
                                    break;
                                case 0:
                                    if ((0xffffffffd7ffffffL & l) == 0L)
                                        break;
                                    kind = 29;
                                    jjCheckNAdd(0);
                                    break;
                                case 2:
                                    if (curChar != 93)
                                        break;
                                    kind = 31;
                                    jjCheckNAdd(2);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 3:
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 29)
                                        kind = 29;
                                    jjCheckNAdd(0);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 3 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_8(int pos, long active0, long active1)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_8(int pos, long active0, long active1)
    {
        return jjMoveNfa_8(jjStopStringLiteralDfa_8(pos, active0, active1), pos + 1);
    }
    private final int jjStartNfaWithStates_8(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_8(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_8()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 34:
                return jjStopAtPos(0, 60);
            case 37:
                return jjStopAtPos(0, 54);
            case 38:
                return jjMoveStringLiteralDfa1_8(0x18L);
            case 39:
                return jjStopAtPos(0, 57);
            case 62:
                return jjStopAtPos(0, 53);
            default :
                return jjMoveNfa_8(1, 0);
            }
    }
    private final int jjMoveStringLiteralDfa1_8(long active1)
    {
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_8(0, 0L, active1);
            return 1;
        }
        switch(curChar)
            {
            case 35:
                if ((active1 & 0x10L) != 0L)
                    {
                        jjmatchedKind = 68;
                        jjmatchedPos = 1;
                    }
                return jjMoveStringLiteralDfa2_8(active1, 0x8L);
            default :
                break;
            }
        return jjStartNfa_8(0, 0L, active1);
    }
    private final int jjMoveStringLiteralDfa2_8(long old1, long active1)
    {
        if (((active1 &= old1)) == 0L)
            return jjStartNfa_8(0, 0L, old1); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_8(1, 0L, active1);
            return 2;
        }
        switch(curChar)
            {
            case 120:
                if ((active1 & 0x8L) != 0L)
                    return jjStopAtPos(2, 67);
                break;
            default :
                break;
            }
        return jjStartNfa_8(1, 0L, active1);
    }
    private final int jjMoveNfa_8(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 61;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if ((0x7ff7f18fffff9ffL & l) != 0L)
                                        {
                                            if (kind > 52)
                                                kind = 52;
                                            jjCheckNAdd(32);
                                        }
                                    else if ((0x2800000200000000L & l) != 0L)
                                        {
                                            if (kind > 52)
                                                kind = 52;
                                            jjCheckNAdd(33);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 52)
                                                kind = 52;
                                            jjCheckNAdd(31);
                                        }
                                    else if (curChar == 60)
                                        {
                                            if (kind > 50)
                                                kind = 50;
                                            jjCheckNAdd(0);
                                        }
                                    if (curChar == 35)
                                        jjAddStates(5, 7);
                                    break;
                                case 0:
                                    if (curChar != 60)
                                        break;
                                    kind = 50;
                                    jjCheckNAdd(0);
                                    break;
                                case 31:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 52;
                                    jjCheckNAdd(31);
                                    break;
                                case 32:
                                    if ((0x7ff7f18fffff9ffL & l) == 0L)
                                        break;
                                    if (kind > 52)
                                        kind = 52;
                                    jjCheckNAdd(32);
                                    break;
                                case 33:
                                    if ((0x2800000200000000L & l) == 0L)
                                        break;
                                    kind = 52;
                                    jjCheckNAdd(33);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                    if ((0xffffffffd7ffffffL & l) != 0L)
                                        {
                                            if (kind > 52)
                                                kind = 52;
                                            jjCheckNAdd(32);
                                        }
                                    else if ((0x28000000L & l) != 0L)
                                        {
                                            if (kind > 52)
                                                kind = 52;
                                            jjCheckNAdd(33);
                                        }
                                    if (curChar == 78)
                                        jjAddStates(8, 9);
                                    else if (curChar == 69)
                                        jjAddStates(10, 11);
                                    else if (curChar == 67)
                                        jjstateSet[jjnewStateCnt++] = 29;
                                    else if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 20;
                                    break;
                                case 2:
                                    if (curChar == 68 && kind > 51)
                                        kind = 51;
                                    break;
                                case 3:
                                case 10:
                                case 16:
                                    if (curChar == 69)
                                        jjCheckNAdd(2);
                                    break;
                                case 4:
                                    if (curChar == 82)
                                        jjstateSet[jjnewStateCnt++] = 3;
                                    break;
                                case 5:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 4;
                                    break;
                                case 6:
                                    if (curChar == 85)
                                        jjstateSet[jjnewStateCnt++] = 5;
                                    break;
                                case 7:
                                    if (curChar == 81)
                                        jjstateSet[jjnewStateCnt++] = 6;
                                    break;
                                case 8:
                                    if (curChar == 69)
                                        jjstateSet[jjnewStateCnt++] = 7;
                                    break;
                                case 9:
                                    if (curChar == 82)
                                        jjstateSet[jjnewStateCnt++] = 8;
                                    break;
                                case 11:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 10;
                                    break;
                                case 12:
                                    if (curChar == 76)
                                        jjstateSet[jjnewStateCnt++] = 11;
                                    break;
                                case 13:
                                    if (curChar == 80)
                                        jjstateSet[jjnewStateCnt++] = 12;
                                    break;
                                case 14:
                                    if (curChar == 77)
                                        jjstateSet[jjnewStateCnt++] = 13;
                                    break;
                                case 15:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 14;
                                    break;
                                case 17:
                                    if (curChar == 88)
                                        jjstateSet[jjnewStateCnt++] = 16;
                                    break;
                                case 18:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 17;
                                    break;
                                case 19:
                                    if (curChar == 70)
                                        jjstateSet[jjnewStateCnt++] = 18;
                                    break;
                                case 20:
                                    if (curChar != 68)
                                        break;
                                    if (kind > 51)
                                        kind = 51;
                                    jjCheckNAddTwoStates(24, 22);
                                    break;
                                case 21:
                                    if (curChar != 70)
                                        break;
                                    if (kind > 51)
                                        kind = 51;
                                    jjCheckNAdd(22);
                                    break;
                                case 22:
                                case 40:
                                    if (curChar == 83 && kind > 51)
                                        kind = 51;
                                    break;
                                case 23:
                                    if (curChar == 69)
                                        jjstateSet[jjnewStateCnt++] = 21;
                                    break;
                                case 24:
                                    if (curChar == 82)
                                        jjstateSet[jjnewStateCnt++] = 23;
                                    break;
                                case 25:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 20;
                                    break;
                                case 26:
                                    if (curChar == 65 && kind > 51)
                                        kind = 51;
                                    break;
                                case 27:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 26;
                                    break;
                                case 28:
                                    if (curChar == 65)
                                        jjstateSet[jjnewStateCnt++] = 27;
                                    break;
                                case 29:
                                    if (curChar == 68)
                                        jjstateSet[jjnewStateCnt++] = 28;
                                    break;
                                case 30:
                                    if (curChar == 67)
                                        jjstateSet[jjnewStateCnt++] = 29;
                                    break;
                                case 32:
                                    if ((0xffffffffd7ffffffL & l) == 0L)
                                        break;
                                    if (kind > 52)
                                        kind = 52;
                                    jjCheckNAdd(32);
                                    break;
                                case 33:
                                    if ((0x28000000L & l) == 0L)
                                        break;
                                    kind = 52;
                                    jjCheckNAdd(33);
                                    break;
                                case 34:
                                    if (curChar == 69)
                                        jjAddStates(10, 11);
                                    break;
                                case 35:
                                    if (curChar == 89 && kind > 51)
                                        kind = 51;
                                    break;
                                case 36:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 35;
                                    break;
                                case 37:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 36;
                                    break;
                                case 38:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 37;
                                    break;
                                case 39:
                                    if (curChar == 78)
                                        jjstateSet[jjnewStateCnt++] = 38;
                                    break;
                                case 41:
                                    if (curChar == 69)
                                        jjstateSet[jjnewStateCnt++] = 40;
                                    break;
                                case 42:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 41;
                                    break;
                                case 43:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 42;
                                    break;
                                case 44:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 43;
                                    break;
                                case 45:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 44;
                                    break;
                                case 46:
                                    if (curChar == 78)
                                        jjstateSet[jjnewStateCnt++] = 45;
                                    break;
                                case 47:
                                    if (curChar == 78)
                                        jjAddStates(8, 9);
                                    break;
                                case 48:
                                    if (curChar != 78)
                                        break;
                                    if (kind > 51)
                                        kind = 51;
                                    jjCheckNAdd(22);
                                    break;
                                case 49:
                                    if (curChar == 69)
                                        jjstateSet[jjnewStateCnt++] = 48;
                                    break;
                                case 50:
                                    if (curChar == 75)
                                        jjstateSet[jjnewStateCnt++] = 49;
                                    break;
                                case 51:
                                    if (curChar == 79)
                                        jjstateSet[jjnewStateCnt++] = 50;
                                    break;
                                case 52:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 51;
                                    break;
                                case 53:
                                    if (curChar == 77)
                                        jjstateSet[jjnewStateCnt++] = 52;
                                    break;
                                case 54:
                                    if (curChar == 78 && kind > 51)
                                        kind = 51;
                                    break;
                                case 55:
                                    if (curChar == 79)
                                        jjstateSet[jjnewStateCnt++] = 54;
                                    break;
                                case 56:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 55;
                                    break;
                                case 57:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 56;
                                    break;
                                case 58:
                                    if (curChar == 65)
                                        jjstateSet[jjnewStateCnt++] = 57;
                                    break;
                                case 59:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 58;
                                    break;
                                case 60:
                                    if (curChar == 79)
                                        jjstateSet[jjnewStateCnt++] = 59;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 1:
                                case 32:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 52)
                                        kind = 52;
                                    jjCheckNAdd(32);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 61 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_17(int pos, long active0, long active1)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_17(int pos, long active0, long active1)
    {
        return jjMoveNfa_17(jjStopStringLiteralDfa_17(pos, active0, active1), pos + 1);
    }
    private final int jjStartNfaWithStates_17(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_17(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_17()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 37:
                return jjStopAtPos(0, 54);
            case 38:
                return jjMoveStringLiteralDfa1_17(0x0L, 0x18L);
            case 60:
                jjmatchedKind = 7;
                return jjMoveStringLiteralDfa1_17(0x8000000000000060L, 0x0L);
            case 93:
                return jjMoveStringLiteralDfa1_17(0x100L, 0x0L);
            default :
                return jjMoveNfa_17(0, 0);
            }
    }
    private final int jjMoveStringLiteralDfa1_17(long active0, long active1)
    {
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_17(0, active0, active1);
            return 1;
        }
        switch(curChar)
            {
            case 33:
                if ((active0 & 0x20L) != 0L)
                    {
                        jjmatchedKind = 5;
                        jjmatchedPos = 1;
                    }
                return jjMoveStringLiteralDfa2_17(active0, 0x8000000000000000L, active1, 0L);
            case 35:
                if ((active1 & 0x10L) != 0L)
                    {
                        jjmatchedKind = 68;
                        jjmatchedPos = 1;
                    }
                return jjMoveStringLiteralDfa2_17(active0, 0L, active1, 0x8L);
            case 63:
                if ((active0 & 0x40L) != 0L)
                    return jjStopAtPos(1, 6);
                break;
            case 93:
                return jjMoveStringLiteralDfa2_17(active0, 0x100L, active1, 0L);
            default :
                break;
            }
        return jjStartNfa_17(0, active0, active1);
    }
    private final int jjMoveStringLiteralDfa2_17(long old0, long active0, long old1, long active1)
    {
        if (((active0 &= old0) | (active1 &= old1)) == 0L)
            return jjStartNfa_17(0, old0, old1); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_17(1, active0, active1);
            return 2;
        }
        switch(curChar)
            {
            case 45:
                return jjMoveStringLiteralDfa3_17(active0, 0x8000000000000000L, active1, 0L);
            case 62:
                if ((active0 & 0x100L) != 0L)
                    return jjStopAtPos(2, 8);
                break;
            case 120:
                if ((active1 & 0x8L) != 0L)
                    return jjStopAtPos(2, 67);
                break;
            default :
                break;
            }
        return jjStartNfa_17(1, active0, active1);
    }
    private final int jjMoveStringLiteralDfa3_17(long old0, long active0, long old1, long active1)
    {
        if (((active0 &= old0) | (active1 &= old1)) == 0L)
            return jjStartNfa_17(1, old0, old1); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_17(2, active0, 0L);
            return 3;
        }
        switch(curChar)
            {
            case 45:
                if ((active0 & 0x8000000000000000L) != 0L)
                    return jjStopAtPos(3, 63);
                break;
            default :
                break;
            }
        return jjStartNfa_17(2, active0, 0L);
    }
    private final int jjMoveNfa_17(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 1;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if ((0xefffff9ffffffbffL & l) == 0L)
                                        break;
                                    kind = 9;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if ((0xffffffffdfffffffL & l) == 0L)
                                        break;
                                    kind = 9;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 9)
                                        kind = 9;
                                    jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_16(int pos, long active0)
    {
        switch (pos)
            {
            case 0:
                if ((active0 & 0x400L) != 0L)
                    {
                        jjmatchedKind = 11;
                        return 0;
                    }
                return -1;
            case 1:
                if ((active0 & 0x400L) != 0L)
                    {
                        jjmatchedKind = 11;
                        jjmatchedPos = 1;
                        return 0;
                    }
                return -1;
            default :
                return -1;
            }
    }
    private final int jjStartNfa_16(int pos, long active0)
    {
        return jjMoveNfa_16(jjStopStringLiteralDfa_16(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_16(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_16(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_16()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 63:
                return jjMoveStringLiteralDfa1_16(0x4000L);
            case 120:
                return jjMoveStringLiteralDfa1_16(0x400L);
            default :
                return jjMoveNfa_16(3, 0);
            }
    }
    private final int jjMoveStringLiteralDfa1_16(long active0)
    {
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_16(0, active0);
            return 1;
        }
        switch(curChar)
            {
            case 62:
                if ((active0 & 0x4000L) != 0L)
                    return jjStopAtPos(1, 14);
                break;
            case 109:
                return jjMoveStringLiteralDfa2_16(active0, 0x400L);
            default :
                break;
            }
        return jjStartNfa_16(0, active0);
    }
    private final int jjMoveStringLiteralDfa2_16(long old0, long active0)
    {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_16(0, old0); 
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) {
            jjStopStringLiteralDfa_16(1, active0);
            return 2;
        }
        switch(curChar)
            {
            case 108:
                if ((active0 & 0x400L) != 0L)
                    return jjStartNfaWithStates_16(2, 10, 0);
                break;
            default :
                break;
            }
        return jjStartNfa_16(1, active0);
    }
    private final int jjMoveNfa_16(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 3;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 3:
                                    if ((0x7ff7f18fffff9ffL & l) != 0L)
                                        {
                                            if (kind > 11)
                                                kind = 11;
                                            jjCheckNAdd(0);
                                        }
                                    else if ((0x780080e600000000L & l) != 0L)
                                        {
                                            if (kind > 12)
                                                kind = 12;
                                            jjCheckNAdd(1);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 13)
                                                kind = 13;
                                            jjCheckNAdd(2);
                                        }
                                    break;
                                case 0:
                                    if ((0x7ff7f18fffff9ffL & l) == 0L)
                                        break;
                                    kind = 11;
                                    jjCheckNAdd(0);
                                    break;
                                case 1:
                                    if ((0x780080e600000000L & l) == 0L)
                                        break;
                                    kind = 12;
                                    jjCheckNAdd(1);
                                    break;
                                case 2:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 13;
                                    jjCheckNAdd(2);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 3:
                                    if ((0xffffffffd7ffffffL & l) != 0L)
                                        {
                                            if (kind > 11)
                                                kind = 11;
                                            jjCheckNAdd(0);
                                        }
                                    else if ((0x28000000L & l) != 0L)
                                        {
                                            if (kind > 12)
                                                kind = 12;
                                            jjCheckNAdd(1);
                                        }
                                    break;
                                case 0:
                                    if ((0xffffffffd7ffffffL & l) == 0L)
                                        break;
                                    kind = 11;
                                    jjCheckNAdd(0);
                                    break;
                                case 1:
                                    if ((0x28000000L & l) == 0L)
                                        break;
                                    kind = 12;
                                    jjCheckNAdd(1);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 3:
                                case 0:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 11)
                                        kind = 11;
                                    jjCheckNAdd(0);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 3 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    private final int jjStopStringLiteralDfa_12(int pos, long active0)
    {
        switch (pos)
            {
            default :
                return -1;
            }
    }
    private final int jjStartNfa_12(int pos, long active0)
    {
        return jjMoveNfa_12(jjStopStringLiteralDfa_12(pos, active0), pos + 1);
    }
    private final int jjStartNfaWithStates_12(int pos, int kind, int state)
    {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try { curChar = input_stream.readChar(); }
        catch(java.io.IOException e) { return pos + 1; }
        return jjMoveNfa_12(state, pos + 1);
    }
    private final int jjMoveStringLiteralDfa0_12()
    {
        switch(curChar)
            {
            case 10:
                return jjStopAtPos(0, 1);
            case 34:
                return jjStopAtPos(0, 60);
            case 37:
                return jjStopAtPos(0, 54);
            case 39:
                return jjStopAtPos(0, 57);
            case 62:
                return jjStopAtPos(0, 37);
            default :
                return jjMoveNfa_12(5, 0);
            }
    }
    private final int jjMoveNfa_12(int startState, int curPos)
    {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 19;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (;;)
            {
                if (++jjround == 0x7fffffff)
                    ReInitRounds();
                if (curChar < 64)
                    {
                        long l = 1L << curChar;
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 5:
                                    if ((0xbfffff5afffff9ffL & l) != 0L)
                                        {
                                            if (kind > 36)
                                                kind = 36;
                                            jjCheckNAdd(18);
                                        }
                                    else if ((0x100000200L & l) != 0L)
                                        {
                                            if (kind > 35)
                                                kind = 35;
                                            jjCheckNAdd(17);
                                        }
                                    break;
                                case 17:
                                    if ((0x100000200L & l) == 0L)
                                        break;
                                    kind = 35;
                                    jjCheckNAdd(17);
                                    break;
                                case 18:
                                    if ((0xbfffff5afffff9ffL & l) == 0L)
                                        break;
                                    kind = 36;
                                    jjCheckNAdd(18);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else if (curChar < 128)
                    {
                        long l = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 5:
                                    if (kind > 36)
                                        kind = 36;
                                    jjCheckNAdd(18);
                                    if (curChar == 80)
                                        jjstateSet[jjnewStateCnt++] = 15;
                                    else if (curChar == 78)
                                        jjstateSet[jjnewStateCnt++] = 9;
                                    else if (curChar == 83)
                                        jjstateSet[jjnewStateCnt++] = 4;
                                    break;
                                case 0:
                                    if (curChar == 77 && kind > 34)
                                        kind = 34;
                                    break;
                                case 1:
                                    if (curChar == 69)
                                        jjstateSet[jjnewStateCnt++] = 0;
                                    break;
                                case 2:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 1;
                                    break;
                                case 3:
                                    if (curChar == 83)
                                        jjstateSet[jjnewStateCnt++] = 2;
                                    break;
                                case 4:
                                    if (curChar == 89)
                                        jjstateSet[jjnewStateCnt++] = 3;
                                    break;
                                case 6:
                                    if (curChar == 65 && kind > 34)
                                        kind = 34;
                                    break;
                                case 7:
                                    if (curChar == 84)
                                        jjstateSet[jjnewStateCnt++] = 6;
                                    break;
                                case 8:
                                    if (curChar == 65)
                                        jjstateSet[jjnewStateCnt++] = 7;
                                    break;
                                case 9:
                                    if (curChar == 68)
                                        jjstateSet[jjnewStateCnt++] = 8;
                                    break;
                                case 10:
                                    if (curChar == 78)
                                        jjstateSet[jjnewStateCnt++] = 9;
                                    break;
                                case 11:
                                    if (curChar == 67 && kind > 34)
                                        kind = 34;
                                    break;
                                case 12:
                                    if (curChar == 73)
                                        jjstateSet[jjnewStateCnt++] = 11;
                                    break;
                                case 13:
                                    if (curChar == 76)
                                        jjstateSet[jjnewStateCnt++] = 12;
                                    break;
                                case 14:
                                    if (curChar == 66)
                                        jjstateSet[jjnewStateCnt++] = 13;
                                    break;
                                case 15:
                                    if (curChar == 85)
                                        jjstateSet[jjnewStateCnt++] = 14;
                                    break;
                                case 16:
                                    if (curChar == 80)
                                        jjstateSet[jjnewStateCnt++] = 15;
                                    break;
                                case 18:
                                    if (kind > 36)
                                        kind = 36;
                                    jjCheckNAdd(18);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                else
                    {
                        int hiByte = (int)(curChar >> 8);
                        int i1 = hiByte >> 6;
                        long l1 = 1L << (hiByte & 077);
                        int i2 = (curChar & 0xff) >> 6;
                        long l2 = 1L << (curChar & 077);
                    MatchLoop: do
                        {
                            switch(jjstateSet[--i])
                                {
                                case 5:
                                case 18:
                                    if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                        break;
                                    if (kind > 36)
                                        kind = 36;
                                    jjCheckNAdd(18);
                                    break;
                                default : break;
                                }
                        } while(i != startsAt);
                    }
                if (kind != 0x7fffffff)
                    {
                        jjmatchedKind = kind;
                        jjmatchedPos = curPos;
                        kind = 0x7fffffff;
                    }
                ++curPos;
                if ((i = jjnewStateCnt) == (startsAt = 19 - (jjnewStateCnt = startsAt)))
                    return curPos;
                try { curChar = input_stream.readChar(); }
                catch(java.io.IOException e) { return curPos; }
            }
    }
    static final int[] jjnextStates = {
        6, 10, 0, 1, 2, 9, 15, 19, 53, 60, 39, 46, 
    };
    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
    {
        switch(hiByte)
            {
            case 0:
                return ((jjbitVec2[i2] & l2) != 0L);
            default : 
                if ((jjbitVec0[i1] & l1) != 0L)
                    return true;
                return false;
            }
    }
    public static final String[] jjstrLiteralImages = {
        "", "\12", null, null, null, "\74\41", "\74\77", "\74", "\135\135\76", null, // NOI18N
        "\170\155\154", null, null, null, "\77\76", null, null, null, "\77\76", "\77", null, null, // NOI18N
        "\77\76", "\77", "\105\116\124\111\124\131", "\101\124\124\114\111\123\124", // NOI18N
        "\104\117\103\124\131\120\105", "\105\114\105\115\105\116\124", "\116\117\124\101\124\111\117\116", null, null, // NOI18N
        null, "\133", "\76", null, null, null, "\76", null, null, null, "\76", null, null, // NOI18N
        null, "\76", null, null, null, "\133", null, null, null, "\76", "\45", null, null, // NOI18N
        "\47", null, "\47", "\42", null, "\42", "\74\41\55\55", null, null, "\55\55\76", // NOI18N
        "\46\43\170", "\46\43", null, null, null, null, null, null, }; // NOI18N
    public static final String[] lexStateNames = {
        "IN_CHREF", // NOI18N
        "IN_CREF", // NOI18N
        "IN_COMMENT", // NOI18N
        "IN_STRING", // NOI18N
        "IN_CHARS", // NOI18N
        "IN_DOCTYPE", // NOI18N
        "IN_TAG_ATTLIST", // NOI18N
        "IN_PREF", // NOI18N
        "IN_ATTLIST", // NOI18N
        "IN_COND", // NOI18N
        "IN_NOTATION", // NOI18N
        "IN_ELEMENT", // NOI18N
        "IN_ENTITY", // NOI18N
        "IN_DECL", // NOI18N
        "IN_PI_CONTENT", // NOI18N
        "IN_XML_DECL", // NOI18N
        "IN_PI", // NOI18N
        "DEFAULT", // NOI18N
    };
    public static final int[] jjnewLexState = {
        -1, -1, -1, -1, -1, 13, 16, -1, -1, -1, 15, -1, -1, 14, 17, -1, -1, -1, 17, -1, -1, -1, 17, -1, 12, 
        8, 5, 11, 10, -1, -1, -1, 9, 17, -1, -1, -1, 17, -1, -1, -1, 17, -1, -1, -1, 17, -1, -1, -1, 17, 
        -1, -1, -1, 17, 7, -1, -1, 4, -1, -1, 3, -1, -1, 2, -1, -1, -1, 0, 1, -1, -1, -1, -1, -1, -1, 
    };
    private UCode_CharStream input_stream;
    private final int[] jjrounds = new int[61];
    private final int[] jjstateSet = new int[122];
    StringBuffer image;
    int jjimageLen;
    int lengthOfMatch;
    protected char curChar;
    public DTDSyntaxTokenManager(UCode_CharStream stream)
    {
        if (UCode_CharStream.staticFlag)
            throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer."); // NOI18N
        input_stream = stream;
    }
    public DTDSyntaxTokenManager(UCode_CharStream stream, int lexState)
    {
        this(stream);
        SwitchTo(lexState);
    }
    public void ReInit(UCode_CharStream stream)
    {
        jjmatchedPos = jjnewStateCnt = 0;
        curLexState = defaultLexState;
        input_stream = stream;
        ReInitRounds();
    }
    private final void ReInitRounds()
    {
        int i;
        jjround = 0x80000001;
        for (i = 61; i-- > 0;)
            jjrounds[i] = 0x80000000;
    }
    public void ReInit(UCode_CharStream stream, int lexState)
    {
        ReInit(stream);
        SwitchTo(lexState);
    }
    public void SwitchTo(int lexState)
    {
        if (lexState >= 18 || lexState < 0)
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE); // NOI18N
        else
            curLexState = lexState;
    }

    private final Token jjFillToken()
    {
        Token t = Token.newToken(jjmatchedKind);
        t.kind = jjmatchedKind;
        String im = jjstrLiteralImages[jjmatchedKind];
        t.image = (im == null) ? input_stream.GetImage() : im;
        t.beginLine = input_stream.getBeginLine();
        t.beginColumn = input_stream.getBeginColumn();
        t.endLine = input_stream.getEndLine();
        t.endColumn = input_stream.getEndColumn();
        return t;
    }

    int curLexState = 17;
    int defaultLexState = 17;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public final Token getNextToken() 
    {
        int kind;
        Token specialToken = null;
        Token matchedToken;
        int curPos = 0;

        EOFLoop :
            for (;;)
                {   
                    try   
                        {     
                            curChar = input_stream.BeginToken();
                        }     
                    catch(java.io.IOException e)
                        {        
                            jjmatchedKind = 0;
                            matchedToken = jjFillToken();
                            return matchedToken;
                        }
                    image = null;
                    jjimageLen = 0;

                    switch(curLexState)
                        {
                        case 0:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_0();
                            break;
                        case 1:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_1();
                            break;
                        case 2:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_2();
                            break;
                        case 3:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_3();
                            break;
                        case 4:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_4();
                            break;
                        case 5:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_5();
                            break;
                        case 6:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_6();
                            break;
                        case 7:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_7();
                            break;
                        case 8:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_8();
                            break;
                        case 9:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_9();
                            break;
                        case 10:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_10();
                            break;
                        case 11:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_11();
                            break;
                        case 12:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_12();
                            break;
                        case 13:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_13();
                            break;
                        case 14:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_14();
                            break;
                        case 15:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_15();
                            break;
                        case 16:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_16();
                            break;
                        case 17:
                            jjmatchedKind = 0x7fffffff;
                            jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_17();
                            break;
                        }
                    if (jjmatchedKind != 0x7fffffff)
                        {
                            if (jjmatchedPos + 1 < curPos)
                                input_stream.backup(curPos - jjmatchedPos - 1);
                            matchedToken = jjFillToken();
                            TokenLexicalActions(matchedToken);
                            if (jjnewLexState[jjmatchedKind] != -1)
                                curLexState = jjnewLexState[jjmatchedKind];
                            return matchedToken;
                        }
                    int error_line = input_stream.getEndLine();
                    int error_column = input_stream.getEndColumn();
                    String error_after = null;
                    boolean EOFSeen = false;
                    try { input_stream.readChar(); input_stream.backup(1); }
                    catch (java.io.IOException e1) {
                        EOFSeen = true;
                        error_after = curPos <= 1 ? "" : input_stream.GetImage(); // NOI18N
                        if (curChar == '\n' || curChar == '\r') {
                            error_line++;
                            error_column = 0;
                        }
                        else
                            error_column++;
                    }
                    if (!EOFSeen) {
                        input_stream.backup(1);
                        error_after = curPos <= 1 ? "" : input_stream.GetImage(); // NOI18N
                    }
                    throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
                }
    }

    final void TokenLexicalActions(Token matchedToken)
    {
        switch(jjmatchedKind)
            {
            case 54 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[54]);
                else
                    image.append(jjstrLiteralImages[54]);
                states[IS_PREF]  = getState();
                break;
            case 56 :
                if (image == null)
                    image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
                else
                    image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
                setState(states[IS_PREF]);
                break;
            case 57 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[57]);
                else
                    image.append(jjstrLiteralImages[57]);
                states[IS_CHARS]  = getState();
                break;
            case 59 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[59]);
                else
                    image.append(jjstrLiteralImages[59]);
                setState(states[IS_CHARS]);
                break;
            case 60 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[60]);
                else
                    image.append(jjstrLiteralImages[60]);
                states[IS_STRING] = getState();
                break;
            case 62 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[62]);
                else
                    image.append(jjstrLiteralImages[62]);
                setState(states[IS_STRING]);
                break;
            case 63 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[63]);
                else
                    image.append(jjstrLiteralImages[63]);
                states[IS_COMMENT] = getState();
                break;
            case 66 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[66]);
                else
                    image.append(jjstrLiteralImages[66]);
                setState(states[IS_COMMENT]);
                break;
            case 67 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[67]);
                else
                    image.append(jjstrLiteralImages[67]);
                states[IS_CREF] = getState();
                break;
            case 68 :
                if (image == null)
                    image = new StringBuffer(jjstrLiteralImages[68]);
                else
                    image.append(jjstrLiteralImages[68]);
                states[IS_CREF] = getState();
                break;
            case 70 :
                if (image == null)
                    image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
                else
                    image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
                setState(states[IS_CREF]);
                break;
            case 73 :
                if (image == null)
                    image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
                else
                    image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
                setState(states[IS_CREF]);
                break;
            default : 
                break;
            }
    }
}
