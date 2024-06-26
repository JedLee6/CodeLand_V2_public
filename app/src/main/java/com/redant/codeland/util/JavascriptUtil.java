/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redant.codeland.util;

/**
 * Helper functions for dealing with Javascript.
 */
public class JavascriptUtil {
    /**
     * Creates a double quoted Javascript string, escaping backslashes, forward slashes, single
     * quotes, double quotes, and escape characters.
     */
    public static String makeJsString(String str) {
        // TODO(#17): More complete character escaping: unicode characters to hex, octal, or \\u.
        String escapedStr = str.replace("\\", "\\\\")  // Must escape backslashes first.
                .replace("</", "<\\/")  // See: http://stackoverflow.com/a/6117915
                .replace("\'", "\\\'")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + escapedStr + "\"";
    }

    private JavascriptUtil() {} // Do not instantiate.
}
