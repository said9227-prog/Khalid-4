/**
 * Arabic Reshaper & Bidirectional text connector for 3D Text Geometry
 * Khalid Super 3D Cinematic Studio
 */
(function (global) {
    var ArabicShaper = {
        // Arabic character form mappings (Isolated, Initial, Medial, Final)
        arabicMap: {
            0x0621: [0xFE80, 0xFE80, 0xFE80, 0xFE80], // HAMZA
            0x0622: [0xFE81, 0xFE81, 0xFE82, 0xFE82], // ALEF WITH MADDA ABOVE
            0x0623: [0xFE83, 0xFE83, 0xFE84, 0xFE84], // ALEF WITH HAMZA ABOVE
            0x0624: [0xFE85, 0xFE85, 0xFE86, 0xFE86], // WAW WITH HAMZA ABOVE
            0x0625: [0xFE87, 0xFE87, 0xFE88, 0xFE88], // ALEF WITH HAMZA BELOW
            0x0626: [0xFE89, 0xFE8B, 0xFE8C, 0xFE8A], // YEH WITH HAMZA ABOVE
            0x0627: [0xFE8D, 0xFE8D, 0xFE8E, 0xFE8E], // ALEF
            0x0628: [0xFE8F, 0xFE91, 0xFE92, 0xFE90], // BEH
            0x0629: [0xFE93, 0xFE93, 0xFE94, 0xFE94], // TEH MARBUTA
            0x062A: [0xFE95, 0xFE97, 0xFE98, 0xFE96], // TEH
            0x062B: [0xFE99, 0xFE9B, 0xFE9C, 0xFE9A], // THEH
            0x062C: [0xFE9D, 0xFE9F, 0xFEA0, 0xFE9E], // JEEM
            0x062D: [0xFEA1, 0xFEA3, 0xFEA4, 0xFEA2], // HAH
            0x062E: [0xFEA5, 0xFEA7, 0xFEA8, 0xFEA6], // KHAH
            0x062F: [0xFEA9, 0xFEA9, 0xFEAA, 0xFEAA], // DAL
            0x0630: [0xFEAB, 0xFEAB, 0xFEAC, 0xFEAC], // THAL
            0x0631: [0xFEAD, 0xFEAD, 0xFEAE, 0xFEAE], // REH
            0x0632: [0xFEAF, 0xFEAF, 0xFEB0, 0xFEB0], // ZAIN
            0x0633: [0xFEB1, 0xFEB3, 0xFEB4, 0xFEB2], // SEEN
            0x0634: [0xFEB5, 0xFEB7, 0xFEB8, 0xFEB6], // SHEEN
            0x0635: [0xFEB9, 0xFEBB, 0xFEBC, 0xFEBA], // SAD
            0x0636: [0xFEBD, 0xFEBF, 0xFEC0, 0xFEBE], // DAD
            0x0637: [0xFEC1, 0xFEC3, 0xFEC4, 0xFEC2], // TAH
            0x0638: [0xFEC5, 0xFEC7, 0xFEC8, 0xFEC6], // ZAH
            0x0639: [0xFEC9, 0xFECB, 0xFECC, 0xFECA], // AIN
            0x063A: [0xFECD, 0xFECF, 0xFED0, 0xFECE], // GHAIN
            0x0641: [0xFED1, 0xFED3, 0xFED4, 0xFED2], // FEH
            0x0642: [0xFED5, 0xFED7, 0xFED8, 0xFED6], // QAF
            0x0643: [0xFED9, 0xFEDB, 0xFEDC, 0xFEDA], // KAF
            0x0644: [0xFEDD, 0xFEDF, 0xFEE0, 0xFEDE], // LAM
            0x0645: [0xFEE1, 0xFEE3, 0xFEE4, 0xFEE2], // MEEM
            0x0646: [0xFEE5, 0xFEE7, 0xFEE8, 0xFEE6], // NOON
            0x0647: [0xFEE9, 0xFEEB, 0xFEEC, 0xFEEA], // HEH
            0x0648: [0xFEED, 0xFEED, 0xFEEE, 0xFEEE], // WAW
            0x0649: [0xFEEF, 0xFEEF, 0xFEF0, 0xFEF0], // ALEF MAKSURA
            0x064A: [0xFEF1, 0xFEF3, 0xFEF4, 0xFEF2], // YEH
            0x067E: [0xFB56, 0xFB58, 0xFB59, 0xFB57], // PEH
            0x0686: [0xFB7A, 0xFB7C, 0xFB7D, 0xFB7B], // TCHEH
            0x0698: [0xFB8A, 0xFB8A, 0xFB8B, 0xFB8B], // JEH
            0x06AF: [0xFB92, 0xFB94, 0xFB95, 0xFB93]  // GAF
        },

        // Characters that disconnect on the left (don't connect to next character)
        nonConnectingRight: [
            0x0621, 0x0622, 0x0623, 0x0624, 0x0625, 0x0627, 0x062F, 0x0630, 0x0631,
            0x0632, 0x0648, 0x0649, 0x067E, 0x0698
        ],

        isArabic: function(text) {
            return /[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF\uFB50-\uFDFF\uFE70-\uFEFF]/.test(text);
        },

        reshape: function(text) {
            if (!text) return "";
            var chars = [];
            for (var i = 0; i < text.length; i++) {
                chars.push(text.charCodeAt(i));
            }

            var reshaped = [];
            for (var i = 0; i < chars.length; i++) {
                var code = chars[i];
                var map = this.arabicMap[code];

                if (!map) {
                    reshaped.push(String.fromCharCode(code));
                    continue;
                }

                var prevCode = i > 0 ? chars[i - 1] : null;
                var nextCode = i < chars.length - 1 ? chars[i + 1] : null;

                var prevConnects = prevCode && this.arabicMap[prevCode] && this.nonConnectingRight.indexOf(prevCode) === -1;
                var nextConnects = nextCode && this.arabicMap[nextCode];

                var formIndex = 0; // Isolated
                if (prevConnects && nextConnects) {
                    formIndex = 2; // Medial
                } else if (prevConnects) {
                    formIndex = 3; // Final
                } else if (nextConnects) {
                    formIndex = 1; // Initial
                }

                reshaped.push(String.fromCharCode(map[formIndex]));
            }

            // Reverse for 3D extrusion order if text is Arabic RTL
            if (this.isArabic(text)) {
                return reshaped.reverse().join("");
            }
            return reshaped.join("");
        }
    };

    global.ArabicShaper = ArabicShaper;
})(window);
