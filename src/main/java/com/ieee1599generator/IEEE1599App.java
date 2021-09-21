package com.ieee1599generator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Contains the main method to run the app
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
@Command(mixinStandardHelpOptions = true)
public class IEEE1599App implements Callable<Void> {

    private static final Logger logger = LogManager.getLogger(IEEE1599App.class.getName());

    @Option(names = {"--creator"}, description = "document creator name")
    private String creator;

    @Option(names = {"--title"}, defaultValue = "Title", description = "piece title (default: ${DEFAULT-VALUE})")
    private String title;

    @Option(names = {"--author"}, defaultValue = "Author", description = "piece author (default: ${DEFAULT-VALUE})")
    private String author;

    @Option(names = {"--track-length"}, description = "track length in seconds")
    private long trackLength;

    @Option(names = {"--bpm"}, description = "time in bpm")
    private int bpm;

    @Option(names = {"--metre"}, description = "metre (string composed by <first number>:<second number>)")
    private String metre;

    @Option(names = {"--instruments-number"}, description = "number of instruments")
    private int instrumentsNumber;

    @ArgGroup(exclusive = false, multiplicity = "1..*")
    List<InstrumentParams> instrumentsParams;

    static class InstrumentParams {

        @Option(names = "--max-notes-number", description = "maximum number of played notes")
        int maxNumberOfPlayedNotes;
        @Option(names = "--min-duration", split = "/", description = "minimum duration of musical figures (array composed by <numerator>,<denominator>)")
        int[] minDuration;
        @Option(names = "--max-duration", split = "/", description = "maximum duration of musical figures (array composed by <numerator>,<denominator>)")
        int[] maxDuration;
        @Option(names = "--min-height", description = "minimum heigth of musical figures (<Anglo-Saxon note name>_<possible accidental (sharp, sharp_and_a_half, demisharp, double_sharp, flat, flat_and_a_half, demiflat, double_flat)><octave number>")
        String minHeight;
        @Option(names = "--max-height", description = "maximum heigth of musical figures (<Anglo-Saxon note name>_<possible accidental (sharp, sharp_and_a_half, demisharp, double_sharp, flat, flat_and_a_half, demiflat, double_flat)><octave number>")
        String maxHeight;
        @Option(names = "--max-notes-number-chord", description = "maximum number of notes in a chord")
        int maxNumberOfNotesInAChord;
        @Option(names = "--irregular-groups", description = "presence of irregular groups (true/false value)")
        boolean areIrregularGroupsPresent;
        @Option(names = "--min-delay", description = "minimum delay in VTU after which the next note will sound")
        int minimumDelay;
    }

    @Option(names = {"--seed"}, defaultValue = "1234", description = "seed for random object (default: ${DEFAULT-VALUE})")
    private long seed;

    private final float docVersion = 1.0f;    // document version

    private final List<Character> clefs = List.of('G', 'F', 'C');

    private final List<Integer> clefsSteps = List.of(2, 4, 6);

    private final Map<String, Float> accidentalMap = new HashMap<String, Float>() {
        {
            put("natural", 0f);
            put("sharp", 1.0f);
            put("sharp_and_a_half", 0.75f);
            put("demisharp", 0.25f);
            put("double_sharp", 2.0f);
            put("flat", -1.0f);
            put("flat_and_a_half", -0.75f);
            put("demiflat", -0.25f);
            put("double_flat", -2.0f);
        }
    };

    private final Map<Float, List<String>> allNotesMap = new HashMap<Float, List<String>>() {
        {
            put(0f, List.of("C", "D_double_flat", "B_sharp"));
            put(0.25f, List.of("C_demisharp", "D_sharp_and_a_half"));
            put(0.75f, List.of("C_sharp_and_a_half", "B_demisharp"));
            put(1f, List.of("C_sharp", "D_flat", "B_double_sharp"));
            put(1.25f, List.of("D_flat_and_a_half"));
            put(1.75f, List.of("D_demiflat"));
            put(2f, List.of("D", "E_double_flat", "C_double_sharp"));
            put(2.25f, List.of("D_demisharp"));
            put(2.75f, List.of("D_sharp_and_a_half"));
            put(3f, List.of("D_sharp", "E_flat", "F_double_flat"));
            put(3.25f, List.of("E_flat_and_a_half"));
            put(3.75f, List.of("E_demiflat"));
            put(4f, List.of("E", "F_flat", "D_double_sharp"));
            put(4.25f, List.of("E_demisharp", "F_flat_and_a_half"));
            put(4.75f, List.of("E_sharp_and_a_half", "F_demiflat"));
            put(5f, List.of("F", "G_double_flat", "E_sharp"));
            put(5.25f, List.of("F_demisharp"));
            put(5.75f, List.of("F_sharp_and_a_half"));
            put(6f, List.of("F_sharp", "G_flat", "E_double_sharp"));
            put(6.25f, List.of("G_flat_and_a_half"));
            put(6.75f, List.of("G_demiflat"));
            put(7f, List.of("G", "A_double_flat", "F_sharp"));
            put(7.25f, List.of("G_demisharp"));
            put(7.75f, List.of("G_sharp_and_a_half"));
            put(8f, List.of("G_sharp", "A_flat"));
            put(8.25f, List.of("A_flat_and_a_half"));
            put(8.75f, List.of("A_demiflat"));
            put(9f, List.of("A", "B_double_flat", "G_double_sharp"));
            put(9.25f, List.of("A_demisharp"));
            put(9.75f, List.of("A_sharp_and_a_half"));
            put(10f, List.of("A_sharp", "B_flat", "C_double_flat"));
            put(10.25f, List.of("B_flat_and_a_half", "C_demiflat"));
            put(10.75f, List.of("B_demiflat", "C_flat_and_a_half"));
            put(11f, List.of("B", "C_flat", "A_double_sharp"));
        }
    };

    /**
     * <p>
     * permits to call the methods needed to start the app and create the
     * document in IEEE1599 format.
     * </p>
     *
     * @throws Exception if there is an exception that causes the end of the program
     */
    @Override
    public Void call() throws Exception {
        try {
            if (instrumentsParams.size() != instrumentsNumber) {
                throw new IllegalArgumentException(" number of instrumentsParams different from number of instruments, "
                        + "please try again by entering the same number of instrumentsParams for as many instruments as there are");
            }

            Initializer initializer = new Initializer(trackLength, metre, bpm);

            for (int i = 0; i < instrumentsNumber; i++) {
                InstrumentParams instrumentParams = instrumentsParams.get(i);
                initializer.initializeInstrumentsParams(instrumentParams.maxNumberOfPlayedNotes, instrumentParams.minDuration, instrumentParams.maxDuration, instrumentParams.minHeight, instrumentParams.maxHeight, instrumentParams.maxNumberOfNotesInAChord, instrumentParams.areIrregularGroupsPresent, instrumentParams.minimumDelay);
            }

            Formatter formatter = FormatterBuilder.newBuilder()
                    .seed(seed)
                    .creator(creator)
                    .docVersion(docVersion)
                    .title(title)
                    .author(author)
                    .instrumentsNumber(instrumentsNumber)
                    .instruments(initializer.getInstruments())
                    .clefs(clefs)
                    .clefsSteps(clefsSteps)
                    .accidentalMap(accidentalMap)
                    .allNotesMap(allNotesMap)
                    .metreInNumbers(initializer.getMetreInNumbers())
                    .measuresNumber(initializer.getMeasuresNumber())
                    .irregularGroupsMap(initializer.getIrregularGroupsMap())
                    .build();

            formatter.format();
            FormatterUtils.saveXMLFile(formatter.getDocument());
        } catch (IllegalArgumentException | NoSuchElementException | ParserConfigurationException | TransformerException exceptionThrown) {
            IEEE1599App.logger.error(exceptionThrown.getClass() + ":" + exceptionThrown.getMessage());
        } catch (Exception exception) {
            IEEE1599App.logger.error("The program terminated due to the exception " + exception.getClass());
        }
        return null;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new IEEE1599App()).execute(args));
    }
}
