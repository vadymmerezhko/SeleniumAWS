package org.example.utils;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public final class TextUtils {

    private static final AtomicReference<StanfordCoreNLP> pipeline = new AtomicReference<>();
    private static final AtomicBoolean initializing = new AtomicBoolean(false);

    // Expanded map of irregular singular nouns to their plural forms
    private static final ConcurrentMap<String, String> irregularNouns = new ConcurrentHashMap<>();

    static {
        irregularNouns.put("man", "men");
        irregularNouns.put("woman", "women");
        irregularNouns.put("child", "children");
        irregularNouns.put("tooth", "teeth");
        irregularNouns.put("foot", "feet");
        irregularNouns.put("mouse", "mice");
        irregularNouns.put("person", "people");
        irregularNouns.put("goose", "geese");
        irregularNouns.put("cactus", "cacti");
        irregularNouns.put("focus", "foci");
        irregularNouns.put("fungus", "fungi");
        irregularNouns.put("nucleus", "nuclei");
        irregularNouns.put("syllabus", "syllabi");
        irregularNouns.put("analysis", "analyses");
        irregularNouns.put("diagnosis", "diagnoses");
        irregularNouns.put("oasis", "oases");
        irregularNouns.put("thesis", "theses");
        irregularNouns.put("crisis", "crises");
        irregularNouns.put("phenomenon", "phenomena");
        irregularNouns.put("criterion", "criteria");
        irregularNouns.put("datum", "data");
        irregularNouns.put("leaf", "leaves");
        irregularNouns.put("life", "lives");
        irregularNouns.put("knife", "knives");
        irregularNouns.put("wife", "wives");
        irregularNouns.put("elf", "elves");
        irregularNouns.put("loaf", "loaves");
        irregularNouns.put("half", "halves");
        irregularNouns.put("self", "selves");
        irregularNouns.put("shelf", "shelves");
        irregularNouns.put("ox", "oxen");
        irregularNouns.put("axis", "axes");
        irregularNouns.put("appendix", "appendices");
        irregularNouns.put("matrix", "matrices");
        irregularNouns.put("index", "indices");
        irregularNouns.put("vertex", "vertices");

        initializePipelineAsync();
    }

    /**
     * Initializes StanfordCoreNLP pipeline instance.
     */
    public static synchronized void initializePipelineAsync() {
        if (!initializing.get()) {
            CompletableFuture.runAsync(() -> {
                if (pipeline.get() == null) {
                    Properties props = new Properties();
                    props.setProperty("annotators", "tokenize,ssplit,lemma");
                    props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

                    // Initialize the pipeline
                    pipeline.set(new StanfordCoreNLP(props));
                    Sentence sentence = new Sentence("men");
                    sentence.lemmas();
                    System.out.println("Async TextUtils initialized in the background.");
                }
            });
            initializing.set(true);
        }
    }

    /**
     * Returns the number of keyword in the string.
     * Therows exception if string is null or keyword is null or empty.
     * @param string The string.
     * @param keyword The keyword.
     * @return The number of keywords.
     */
    public static int getNumberOfKeywordsInString(String string, String keyword) {
        DataValidationUtils.validateNotNull(string, "string");
        DataValidationUtils.validateNotEmpty(keyword, "keyword");

        if (keyword == null || string == null || string.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;

        while ((index = string.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    /**
     * Converts plural English word to singular one.
     * @param plural The plural noun.
     * @return The singular noun.
     */
    public static String pluralToSingular(String plural) {
        DataValidationUtils.validateNotBlank(plural, "plural");

        try {
            if (pipeline.get() == null) {
                initializePipelineAsync();
            }
            // Create a sentence with the word to lemmatize
            Sentence sentence = new Sentence(plural);

            // Get the lemmas (singular form of the words)
            List<String> lemmas = sentence.lemmas();

            // Return the lemma (should be singular form of the word)
            if (!lemmas.isEmpty()) {
                // Get the first lemma (which will be singular if it's a noun)
                String singular = lemmas.get(0);
                log.debug("Plural '{}' converted to singular '{}'.", plural, singular);
                return singular;
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Invalid noun input: %s", plural));
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert plural '%s' noun to singular one.", plural), e);
        }
    }

    /**
     * Converts a singular English word to its plural form.
     * @param singular The singular noun.
     * @return The plural noun.
     */
    // TODO - add unit tests
    public static String singularToPlural(String singular) {
        DataValidationUtils.validateNotBlank(singular, "singular");

        try {
            String plural;
            // Check if the singular noun is irregular
            if (irregularNouns.containsKey(singular.toLowerCase())) {
                plural = irregularNouns.get(singular.toLowerCase());
            }
            else {
                // StanfordCoreNLP can be used for POS tagging,
                // but manual rules handle pluralization
                Sentence sentence = new Sentence(singular);

                // Apply basic English pluralization rules
                if (singular.endsWith("y") && !isVowelBeforeY(singular)) {
                    plural = singular.substring(0, singular.length() - 1) + "ies";
                }
                else if (singular.endsWith("s") || singular.endsWith("sh") || singular.endsWith("ch") || singular.endsWith("x") || singular.endsWith("z")) {
                    plural = singular + "es";
                }
                else if (singular.endsWith("f") || singular.endsWith("fe")) {
                    plural = singular.replaceAll("(f|fe)$", "ves");
                }
                else {
                    plural = singular + "s";
                }
            }
            log.debug("Singular '{}' converted to plural '{}'.", plural, singular);
            return plural;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert singular '%s' noun to plural.", singular), e);
        }
    }

    /**
     * Splits a multiline string into individual lines, handling all types of newline characters:
     * "\n" (Unix/Linux), "\r\n" (Windows), and "\r" (older MacOS).
     * @param multilineString The multiline string to split.
     * @return An array of strings, each representing a line, including empty or blank lines.
     */
    public static String[] splitMultilineString(String multilineString) {
        DataValidationUtils.validateNotNull(multilineString, "multilineString");

        // Return the original string as a single-element array if it's empty
        if (multilineString.isEmpty()) {
            return new String[] { multilineString };
        }
        // Normalize multiline string - replace carriage return and new line with one new line character
        String normilizedString = multilineString.replace("\n\r", "\n");
        // Use "\\R" with -1 to retain all line breaks and empty lines
        String[] lines = normilizedString.split("\\R", -1);
        log.debug("""
        Multiline string was split into an array of strings.
        Original string:
        {}
        Split lines:
        {}
        """, multilineString, lines);
        return lines;
    }

    // Utility method to check if the letter before 'y' is a vowel
    private static boolean isVowelBeforeY(String word) {
        String vowels = "aeiou";
        char beforeY = word.charAt(word.length() - 2);
        return vowels.indexOf(beforeY) != -1;
    }
}
