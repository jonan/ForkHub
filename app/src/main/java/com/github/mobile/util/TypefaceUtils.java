/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Helpers for dealing with custom typefaces and measuring text to display
 */
public class TypefaceUtils {

    /*
     * All octicon icons
     */
    public static final String ICON_ALERT = "\uf02d";
    public static final String ICON_ARROW_DOWN = "\uf03f";
    public static final String ICON_ARROW_LEFT = "\uf040";
    public static final String ICON_ARROW_RIGHT = "\uf03e";
    public static final String ICON_ARROW_SMALL_DOWN = "\uf0a0";
    public static final String ICON_ARROW_SMALL_LEFT = "\uf0a1";
    public static final String ICON_ARROW_SMALL_RIGHT = "\uf071";
    public static final String ICON_ARROW_SMALL_UP = "\uf09f";
    public static final String ICON_ARROW_UP = "\uf03d";
    public static final String ICON_BEAKER = "\uf0dd";
    public static final String ICON_BELL = "\uf0de";
    public static final String ICON_BOLD = "\uf0e2";
    public static final String ICON_BOOK = "\uf007";
    public static final String ICON_BOOKMARK = "\uf07b";
    public static final String ICON_BRIEFCASE = "\uf0d3";
    public static final String ICON_BROADCAST = "\uf048";
    public static final String ICON_BROWSER = "\uf0c5";
    public static final String ICON_BUG = "\uf091";
    public static final String ICON_CALENDAR = "\uf068";
    public static final String ICON_CHECK = "\uf03a";
    public static final String ICON_CHECKLIST = "\uf076";
    public static final String ICON_CHEVRON_DOWN = "\uf0a3";
    public static final String ICON_CHEVRON_LEFT = "\uf0a4";
    public static final String ICON_CHEVRON_RIGHT = "\uf078";
    public static final String ICON_CHEVRON_UP = "\uf0a2";
    public static final String ICON_CIRCLE_SLASH = "\uf084";
    public static final String ICON_CIRCUIT_BOARD = "\uf0d6";
    public static final String ICON_CLIPPY = "\uf035";
    public static final String ICON_CLOCK = "\uf046";
    public static final String ICON_CLOUD_DOWNLOAD = "\uf00b";
    public static final String ICON_CLOUD_UPLOAD = "\uf00c";
    public static final String ICON_CODE = "\uf05f";
    public static final String ICON_COLOR_MODE = "\uf065";
    public static final String ICON_COMMENT = "\uf02b";
    public static final String ICON_COMMENT_DISCUSSION = "\uf04f";
    public static final String ICON_CREDIT_CARD = "\uf045";
    public static final String ICON_DASH = "\uf0ca";
    public static final String ICON_DASHBOARD = "\uf07d";
    public static final String ICON_DATABASE = "\uf096";
    public static final String ICON_DESKTOP_DOWNLOAD = "\uf0dc";
    public static final String ICON_DEVICE_CAMERA = "\uf056";
    public static final String ICON_DEVICE_CAMERA_VIDEO = "\uf057";
    public static final String ICON_DEVICE_DESKTOP = "\uf27c";
    public static final String ICON_DEVICE_MOBILE = "\uf038";
    public static final String ICON_DIFF = "\uf04d";
    public static final String ICON_DIFF_ADDED = "\uf06b";
    public static final String ICON_DIFF_IGNORED = "\uf099";
    public static final String ICON_DIFF_MODIFIED = "\uf06d";
    public static final String ICON_DIFF_REMOVED = "\uf06c";
    public static final String ICON_DIFF_RENAMED = "\uf06e";
    public static final String ICON_ELLIPSIS = "\uf09a";
    public static final String ICON_EYE = "\uf04e";
    public static final String ICON_FILE_BINARY = "\uf094";
    public static final String ICON_FILE_CODE = "\uf010";
    public static final String ICON_FILE_DIRECTORY = "\uf016";
    public static final String ICON_FILE_MEDIA = "\uf012";
    public static final String ICON_FILE_PDF = "\uf014";
    public static final String ICON_FILE_SUBMODULE = "\uf017";
    public static final String ICON_FILE_SYMLINK_DIRECTORY = "\uf0b1";
    public static final String ICON_FILE_SYMLINK_FILE = "\uf0b0";
    public static final String ICON_FILE_TEXT = "\uf011";
    public static final String ICON_FILE_ZIP = "\uf013";
    public static final String ICON_FLAME = "\uf0d2";
    public static final String ICON_FOLD = "\uf0cc";
    public static final String ICON_GEAR = "\uf02f";
    public static final String ICON_GIFT = "\uf042";
    public static final String ICON_GIST = "\uf00e";
    public static final String ICON_GIST_SECRET = "\uf08c";
    public static final String ICON_GIT_BRANCH = "\uf020";
    public static final String ICON_GIT_COMMIT = "\uf01f";
    public static final String ICON_GIT_COMPARE = "\uf0ac";
    public static final String ICON_GIT_MERGE = "\uf023";
    public static final String ICON_GIT_PULL_REQUEST = "\uf009";
    public static final String ICON_GLOBE = "\uf0b6";
    public static final String ICON_GRAPH = "\uf043";
    public static final String ICON_HEART = "\u2665";
    public static final String ICON_HISTORY = "\uf07e";
    public static final String ICON_HOME = "\uf08d";
    public static final String ICON_HORIZONTAL_RULE = "\uf070";
    public static final String ICON_HOURGLASS = "\uf09e";
    public static final String ICON_HUBOT = "\uf09d";
    public static final String ICON_INBOX = "\uf0cf";
    public static final String ICON_INFO = "\uf059";
    public static final String ICON_ISSUE_CLOSED = "\uf028";
    public static final String ICON_ISSUE_OPENED = "\uf026";
    public static final String ICON_ITALIC = "\uf0e4";
    public static final String ICON_ISSUE_REOPENED = "\uf027";
    public static final String ICON_JERSEY = "\uf019";
    public static final String ICON_KEY = "\uf049";
    public static final String ICON_KEYBOARD = "\uf00d";
    public static final String ICON_LAW = "\uf0d8";
    public static final String ICON_LIGHT_BULB = "\uf000";
    public static final String ICON_LINK = "\uf05c";
    public static final String ICON_LINK_EXTERNAL = "\uf07f";
    public static final String ICON_LIST_ORDERED = "\uf062";
    public static final String ICON_LIST_UNORDERED = "\uf061";
    public static final String ICON_LOCATION = "\uf060";
    public static final String ICON_LOCK = "\uf06a";
    public static final String ICON_LOGO_GITHUB = "\uf092";
    public static final String ICON_MAIL = "\uf03b";
    public static final String ICON_MAIL_READ = "\uf03c";
    public static final String ICON_MAIL_REPLY = "\uf051";
    public static final String ICON_MARK_GITHUB = "\uf00a";
    public static final String ICON_MARKDOWN = "\uf0c9";
    public static final String ICON_MEGAPHONE = "\uf077";
    public static final String ICON_MENTION = "\uf0be";
    public static final String ICON_MILESTONE = "\uf075";
    public static final String ICON_MIRROR = "\uf024";
    public static final String ICON_MORTAR_BOARD = "\uf0d7";
    public static final String ICON_MUTE = "\uf080";
    public static final String ICON_NO_NEWLINE = "\uf09c";
    public static final String ICON_OCTOFACE = "\uf008";
    public static final String ICON_ORGANIZATION = "\uf037";
    public static final String ICON_PACKAGE = "\uf0c4";
    public static final String ICON_PAINTCAN = "\uf0d1";
    public static final String ICON_PENCIL = "\uf058";
    public static final String ICON_PERSON = "\uf018";
    public static final String ICON_PIN = "\uf041";
    public static final String ICON_PLUG = "\uf0d4";
    public static final String ICON_PLUS = "\uf05d";
    public static final String ICON_PRIMITIVE_DOT = "\uf052";
    public static final String ICON_PRIMITIVE_SQUARE = "\uf053";
    public static final String ICON_PULSE = "\uf085";
    public static final String ICON_QUESTION = "\uf02c";
    public static final String ICON_QUOTE = "\uf063";
    public static final String ICON_RADIO_TOWER = "\uf030";
    public static final String ICON_REPO = "\uf001";
    public static final String ICON_REPO_CLONE = "\uf04c";
    public static final String ICON_REPO_FORCE_PUSH = "\uf04a";
    public static final String ICON_REPO_FORKED = "\uf002";
    public static final String ICON_REPO_PULL = "\uf006";
    public static final String ICON_REPO_PUSH = "\uf005";
    public static final String ICON_ROCKET = "\uf033";
    public static final String ICON_RSS = "\uf034";
    public static final String ICON_RUBY = "\uf047";
    public static final String ICON_SEARCH = "\uf02e";
    public static final String ICON_SERVER = "\uf097";
    public static final String ICON_SETTINGS = "\uf07c";
    public static final String ICON_SHIELD = "\uf0e1";
    public static final String ICON_SIGN_IN = "\uf036";
    public static final String ICON_SIGN_OUT = "\uf032";
    public static final String ICON_SQUIRREL = "\uf0b2";
    public static final String ICON_STAR = "\uf02a";
    public static final String ICON_STOP = "\uf08f";
    public static final String ICON_SYNC = "\uf087";
    public static final String ICON_TAG = "\uf015";
    public static final String ICON_TASKLIST = "\uf0e5";
    public static final String ICON_TELESCOPE = "\uf088";
    public static final String ICON_TERMINAL = "\uf0c8";
    public static final String ICON_TEXT_SIZE = "\uf0e3";
    public static final String ICON_THREE_BARS = "\uf05e";
    public static final String ICON_THUMBSDOWN = "\uf0db";
    public static final String ICON_THUMBSUP = "\uf0da";
    public static final String ICON_TOOLS = "\uf031";
    public static final String ICON_TRASHCAN = "\uf0d0";
    public static final String ICON_TRIANGLE_DOWN = "\uf05b";
    public static final String ICON_TRIANGLE_LEFT = "\uf044";
    public static final String ICON_TRIANGLE_RIGHT = "\uf05a";
    public static final String ICON_TRIANGLE_UP = "\uf0aa";
    public static final String ICON_UNFOLD = "\uf039";
    public static final String ICON_UNMUTE = "\uf0ba";
    public static final String ICON_VERSIONS = "\uf064";
    public static final String ICON_WATCH = "\uf0e0";
    public static final String ICON_X = "\uf081";
    public static final String ICON_ZAP = "\u26A1";

    private static Typeface OCTICONS;

    /**
     * Find the maximum number of digits in the given numbers
     *
     * @param numbers
     * @return max digits
     */
    public static int getMaxDigits(int... numbers) {
        int max = 1;
        for (int number : numbers)
            max = Math.max(max, (int) Math.log10(number) + 1);
        return max;
    }

    /**
     * Get width of number of digits
     *
     * @param view
     * @param numberOfDigits
     * @return number width
     */
    public static int getWidth(TextView view, int numberOfDigits) {
        Paint paint = new Paint();
        paint.setTypeface(view.getTypeface());
        paint.setTextSize(view.getTextSize());
        char[] text = new char[numberOfDigits];
        Arrays.fill(text, '0');
        return Math.round(paint.measureText(text, 0, text.length));
    }

    /**
     * Get octicons typeface
     *
     * @param context
     * @return octicons typeface
     */
    public static Typeface getOcticons(final Context context) {
        if (OCTICONS == null)
            OCTICONS = getTypeface(context, "octicons.ttf");
        return OCTICONS;
    }

    /**
     * Set octicons typeface on given text view(s)
     *
     * @param textViews
     */
    public static void setOcticons(final TextView... textViews) {
        if (textViews == null || textViews.length == 0)
            return;

        Typeface typeface = getOcticons(textViews[0].getContext());
        for (TextView textView : textViews)
            textView.setTypeface(typeface);
    }

    /**
     * Get typeface with name
     *
     * @param context
     * @param name
     * @return typeface
     */
    public static Typeface getTypeface(final Context context, final String name) {
        return Typeface.createFromAsset(context.getAssets(), name);
    }
}
