package services;

import helper.wikiTextParsing.UnityUITuple;
import helper.wikiTextParsing.UnityUIElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static helper.RegexMatchHelper.allMatches;

/**
 * This parses a wikitext article and builds a datastructure for unity clients to use in the UI.
 */
public class WikiTextToUnityParserService {

    /**
     * Generates a Token string to put into the text
     *
     * @param uiElement
     * @return
     */
    private static String toTokenString(UnityUIElement uiElement, String content) {
        return String.format("|||UnityUIElementStart|||%s|||%s|||UnityUIElementEnd|||", uiElement.toString(), content);
    }

    private static String toTokenString(String uiElement, String content) {
        var elementUI = UnityUIElement.P;
        switch (uiElement) {
            case "H6":
                elementUI = UnityUIElement.H6;
                break;
            case "H5":
                elementUI = UnityUIElement.H5;
                break;
            case "H4":
                elementUI = UnityUIElement.H4;
                break;
            case "H3":
                elementUI = UnityUIElement.H3;
                break;
            case "H2":
                elementUI = UnityUIElement.H2;
                break;
            case "Hr":
                elementUI = UnityUIElement.Hr;
                break;
            case "Image":
                elementUI = UnityUIElement.Image;
                break;
        }
        return toTokenString(elementUI, content);
    }

    /**
     * Creates a wiki image url from a filename
     *
     * @param fileName
     * @return
     */
    private static String toWikiImageUrl(String fileName) {
        return String.format("https://commons.wikimedia.org/w/thumb.php?f=%s&w=600", fileName);
    }

    /**
     * Takes a wikitext and parses it into a list of UnityUITuple.
     *
     * @param wikiText
     * @return
     */
    public static List<UnityUITuple> ParseWikiText(String wikiText) {
        var tokenTuple = new ArrayList<UnityUITuple>();

        // Tokenize the text
        var tokenizedText = Tokenize(wikiText);

        // Lets iterate through the tokenized text and build the UnityUITuples from them
        for (String rawToken : tokenizedText) {
            // If this param is longer than 0, that means we dont just have a text. Otherwise its just a p
            var splited = rawToken.split("\\|\\|\\|");
            if (splited.length == 1) {
                // There are sometimes text which are just line breaks. We dont need them in the UI.
                if(rawToken.equals("\\n") || rawToken.equals("\\n\\n")) continue;
                tokenTuple.add(new UnityUITuple(UnityUIElement.P, rawToken));
                continue;
            }
            // Else, we gotta look what kinda token we got, parse and add it.
            tokenTuple.add(new UnityUITuple(UnityUIElement.valueOf(splited[0]), splited[1]));
        }

        return tokenTuple;
    }

    /**
     * Tokenizes the wikitext. See also here:
     * https://www.mediawiki.org/wiki/Help:Formatting/de
     *
     * @param wikiText
     * @return
     */
    private static List<String> Tokenize(String wikiText) {

        // DONT CHANGE THE ORDER FOR NOW!
        // Add any expressions we want to parse here
        wikiText = MatchAndReplaceShortDescription(wikiText);
        wikiText = MatchAndReplaceExtras(wikiText);
        wikiText = MatchAndReplaceLocalLinks(wikiText);
        wikiText = MatchAndReplaceKursivAndBold(wikiText);
        wikiText = MatchAndReplaceBold(wikiText);
        wikiText = MatchAndReplaceKursiv(wikiText);
        wikiText = MatchAndReplaceNoFormat(wikiText);
        wikiText = MatchAndReplaceLevel(wikiText);
        wikiText = MatchAndReplaceComments(wikiText);
        wikiText = MatchAndReplaceRefs(wikiText);

        wikiText = Cleanup(wikiText);

        // Ik ik looks shit, but we simply split at the above designed start and end
        return Arrays.stream(wikiText
                .split("\\|\\|\\|UnityUIElementStart\\|\\|\\||\\|\\|\\|UnityUIElementEnd\\|\\|\\|")).toList();
    }

    /**
     * There are some leftover symbols like ]] since the regex isnt 100% solid. Lets just clean them up
     *
     * @return
     */
    private static String Cleanup(String wikiText) {
        return wikiText.replaceAll("\\[\\[", "")
                .replaceAll("\\]\\]", "");
    }

    /**
     * Replaces <ref></ref> with empty fields. We dont handle that right now...
     *
     * @return
     */
    private static String MatchAndReplaceRefs(String wikiText) {
        var pattern = "<ref(.*?)/>";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group();
            wikiText = wikiText.replace(matchedText, "");
        }

        pattern = "<ref>(.*?)</ref>";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group();
            wikiText = wikiText.replace(matchedText, "");
        }

        wikiText = wikiText.replace("</ref>", "");

        return wikiText;
    }

    /**
     * Replaces {{}} with empty fields. We dont handle that right now...
     *
     * @return
     */
    private static String MatchAndReplaceShortDescription(String wikiText) {
        var pattern = "\\{\\{Short description(.*?)\\}\\}";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group();
            wikiText = wikiText.replace(matchedText, "");
        }

        return wikiText;
    }

    /**
     * Replaces {{}} with empty fields. We dont handle that right now...
     *
     * @return
     */
    private static String MatchAndReplaceExtras(String wikiText) {
        // See also: https://itecnote.com/tecnote/regular-expression-to-match-balanced-parentheses/
        // We have to use recursion here
        // Edit: This takes way too long, but works. But for now, lets just do this
        var pattern = "\\{\\{(.*?)\\}\\}";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: {{bold and kursiv}}
            // This will be like: bold and kursiv
            wikiText = wikiText.replace(matchedText,"");
        }
        return wikiText;
        //return wikiText.replaceAll("\\{\\{", "(").replaceAll("\\}\\}", ")");
        /*
        var pattern = "\\{\\{(?:[^)(]+|\\{\\{(?:[^)(]+|\\{\\{[^)(]*\\}\\})*\\}\\})*\\}\\}";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: {{bold and kursiv}}
            // This will be like: bold and kursiv
            wikiText = wikiText.replace(matchedText,"");
        }

        return wikiText;*/
    }

    /**
     * Replaces <nowiki></nowiki> with empty fields. We dont handle that right now...
     *
     * @return
     */
    private static String MatchAndReplaceComments(String wikiText) {
        var pattern = "<!--(.*?)-->";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: <!--bold and kursiv-->
            // This will be like: bold and kursiv
            wikiText = wikiText.replace(matchedText, "");
        }

        return wikiText;
    }

    /**
     * Replaces == Ebene 2 == or === Ebene 3 === or ==== Ebene 4 ==== and so on with tokens
     *
     * @return
     */
    private static String MatchAndReplaceLevel(String wikiText) {
        // There are 5 levels. Level 6 to Level 2
        for (int i = 6; i > 1; i--) {
            var pattern = "(.*?)";
            pattern = "=".repeat(i) + pattern + "=".repeat(i);
            // Get all matches and iterate through them. Handle them
            for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
                var matchedText = match.group();
                // This will be like: bold and kursiv
                var content = matchedText
                        .replaceAll("=".repeat(i), "");
                wikiText = wikiText.replace(matchedText,
                        toTokenString(("H" + i), content));
            }
        }

        return wikiText;
    }

    /**
     * Replaces <nowiki></nowiki> with empty fields. We dont handle that right now...
     *
     * @return
     */
    private static String MatchAndReplaceNoFormat(String wikiText) {
        var pattern = "<nowiki>(.*?)</nowiki>";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: <nowiki>>bold and kursiv</nowiki>
            // This will be like: bold and kursiv
            var content = matchedText
                    .replaceAll("<nowiki>", "")
                    .replaceAll("</nowiki>", "");
            wikiText = wikiText.replace(matchedText, content);
        }

        return wikiText;
    }

    /**
     * Replaces '''''bold and kursiv''''' with classic html tags <i><b></b></i>
     *
     * @return
     */
    private static String MatchAndReplaceKursivAndBold(String wikiText) {
        var pattern = "'''''(.*?)'''''";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: '''''bold and kursiv'''''
            // This will be like: bold and kursiv
            var content = matchedText
                    .replaceAll("'''''", "");
            wikiText = wikiText.replace(matchedText,
                    "<i><b>" + content + "</b></i>");
        }

        return wikiText;
    }

    /**
     * Replaces '''bold''' with classic html tags <b></b>>
     *
     * @return
     */
    private static String MatchAndReplaceBold(String wikiText) {
        var pattern = "'''(.*?)'''";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: '''bold'''
            // This will be like: bold
            var content = matchedText
                    .replaceAll("'''", "");
            wikiText = wikiText.replace(matchedText,
                    "<b>" + content + "</b>");
        }

        return wikiText;
    }

    /**
     * Replaces ''bold'' with classic html tags <i></i>>
     *
     * @return
     */
    private static String MatchAndReplaceKursiv(String wikiText) {
        var pattern = "''(.*?)''";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: ''bold''
            // This will be like: bold
            var content = matchedText
                    .replaceAll("''", "");
            wikiText = wikiText.replace(matchedText,
                    "<i>" + content + "</i>");
        }

        return wikiText;
    }

    /**
     * First, lets match the [[..]] structures. They are mostly references to other wikipages
     * We ignore [[Augsburg]] and [[Inhalt|Quelle]] for now
     * But we try to parse the [[File:name.jpg]] eventually.
     * Doesnt work with [[Inhalt]]]]
     *
     * @param wikiText
     * @return
     */
    private static String MatchAndReplaceLocalLinks(String wikiText) {
        var pattern = "\\[\\[(.*?)\\]\\]";
        // Get all matches and iterate through them. Handle them
        for (MatchResult match : allMatches(Pattern.compile(pattern), wikiText)) {
            var matchedText = match.group(); // This is e.g.: [[chamber music|chamber]]
            // This will be like: chamber music|chamber
            var content = matchedText
                    .replaceAll("\\[\\[", "")
                    .replaceAll("\\]\\]", "");

            // Now handle the different token types. Here we only want to estract the file images
            // and delete all cites
            if (content.startsWith("File:")) {
                // [[File:Wolfgang Amadeus Mozart Signature.svg|frameless]]
                var splited = content.split("\\|");
                var imageName = splited[0].replace("File:", "");
                wikiText = wikiText.replace(matchedText,
                        toTokenString(UnityUIElement.Image, toWikiImageUrl(imageName)));
            } else {
                // Lets check if we got a city or something. We dont want those...
                var splited = content.split("\\|");
                if (splited.length > 1) wikiText = wikiText.replace(matchedText, "");
                else wikiText = wikiText.replace(matchedText, content); // At least replace the [[ ]]
            }
        }

        return wikiText;
    }

}