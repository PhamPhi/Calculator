package com.techie.calculator.app;

/**
 * Copyright (C) 2011 Techie DB Inc. All rights reserved.
 * <p/>
 * Software Development Division,
 * Digital Media & Communications Business, Techie DB Inc.
 * <p/>
 * This software and its documentation are confidential and proprietary information of Techie DB Inc. No part of the software and documents may be
 * copied, reproduced, transmitted, translated, or reduced to any electronic medium or machine-readable form without the prior written consent of
 * Techie DB. Techie DB makes no representations with respect to the contents, and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject to change without notice.
 * <p/>
 * History
 * 2014.04/04/2014.04      larry.pham          Initialize version
 */
public class Constant {

    public static final String INFINITY_UNICODE= "\u221e";
    public static final String MARKER_EVALUATE_ON_RESUME= "?";
    public static final String INFINITY= "Infinity";
    public static final String NAN= "NaN";

    public static final char MINUS= '\u2212';

    public static final int DELETE_MODE_BACKSPACE=  0;
    public static final int DELETE_MODE_CLEAR= 1;


    public static final String ATTR_MAX_DIGITS= "maxDigits";
    public static final int DEFAULT_MAX_DIGITS= 10;
    public static final char[] ACCEPTED_CHARS= "012345678.+-*/\u2212\u00d7\u00f7()!%^".toCharArray();
    public static final char[] OPERATOR_CHARS= "+\u2212\u00d7\u00f7/*".toCharArray();
    public static final int ANIM_DURATION = 500;
    public enum Scroll{
        UP, DOWN, NONE
    }
    // Operations and functions symbols
    public static final char[] ORIGINALS = { '-',   '*',   '/'};
    public static final char[] REPLACEMENTS= {'\u2212', '\u00d7', '\u00f7'};

}
