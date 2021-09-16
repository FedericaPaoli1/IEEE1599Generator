package com.ieee1599generator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * IEEE1599App is the class containing the main method to run the app
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
@Command(mixinStandardHelpOptions = true)
public class IEEE1599App implements Callable<Void> {

    private static final Logger LOGGER = Logger.getLogger(IEEE1599App.class.getName());

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
        @Option(names = "--min-height", description = "minimum heigth of musical figures (<Anglo-Saxon note name><possible accidental (sharp, sharp_and_a_half, demisharp, double_sharp, flat, flat_and_a_half, demiflat, double_flat)>")
        String minHeight;
        @Option(names = "--max-height", description = "maximum heigth of musical figures (<Anglo-Saxon note name><possible accidental (sharp, sharp_and_a_half, demisharp, double_sharp, flat, flat_and_a_half, demiflat, double_flat)>")
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

    private float docVersion = 1.0f;    // document version

    private List<Character> clefs = List.of('G', 'F', 'C');

    private List<Integer> clefsSteps = List.of(2, 4, 6);

    private Map<String, Float> accidentalMap = new HashMap<String, Float>() {
        {
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

    private Map<Float, List<String>> allNotesMap = new HashMap<Float, List<String>>() {
        {
            put(0f, List.of("C", "D-double_flat", "B-sharp"));
            put(0.25f, List.of("C-demisharp", "D-sharp_and_a_half"));
            put(0.75f, List.of("C-sharp_and_a_half", "B-demisharp"));
            put(1f, List.of("C-sharp", "D-flat", "B-double_sharp"));
            put(1.25f, List.of("D-flat_and_a_half"));
            put(1.75f, List.of("D-demiflat"));
            put(2f, List.of("D", "E-double_flat", "C-double_sharp"));
            put(2.25f, List.of("D-demisharp"));
            put(2.75f, List.of("D-sharp_and_a_half"));
            put(3f, List.of("D-sharp", "E-flat", "F-double_flat"));
            put(3.25f, List.of("E-flat_and_a_half"));
            put(3.75f, List.of("E-demiflat"));
            put(4f, List.of("E", "F-flat", "D-double_sharp"));
            put(4.25f, List.of("E_demisharp", "F-flat_and_a_half"));
            put(4.75f, List.of("E_sharp_and_a_half", "F-demiflat"));
            put(5f, List.of("F", "G_double_flat", "E-sharp"));
            put(5.25f, List.of("F_demisharp"));
            put(5.75f, List.of("F_sharp_and_a_half"));
            put(6f, List.of("F_sharp", "G_flat", "E-double_sharp"));
            put(6.25f, List.of("G_flat_and_a_half"));
            put(6.75f, List.of("G-demiflat"));
            put(7f, List.of("G", "A-double_flat", "F-sharp"));
            put(7.25f, List.of("G-demisharp"));
            put(7.75f, List.of("G-sharp_and_a_half"));
            put(8f, List.of("G-sharp", "A-flat"));
            put(8.25f, List.of("A-flat_and_a_half"));
            put(8.75f, List.of("A-demiflat"));
            put(9f, List.of("A", "B-double_flat", "G-double_sharp"));
            put(9.25f, List.of("A-demisharp"));
            put(9.75f, List.of("A-sharp_and_a_half"));
            put(10f, List.of("A-sharp", "B-flat", "C-double_flat"));
            put(10.25f, List.of("B-flat_and_a_half", "C-demiflat"));
            put(10.75f, List.of("B-demiflat","C-flat_and_a_half"));
            put(11f, List.of("B", "C-flat", "A-double_sharp"));
        }
    };

    private int octavesNumber = 10;

    /**
     * <p>
     * call is the overriding method that permits to call the methods needed to
     * start the app and create the document in IEEE1599 format.
     * </p>
     *
     * @throws Exception
     */
    @Override
    public Void call() throws Exception {
        try {
            if (instrumentsParams.size() != instrumentsNumber) {
                throw new IllegalArgumentException("Number of instrumentsParams different from number of instruments, "
                        + "please try again by entering the same number of instrumentsParams for as many instruments as there are.");
            }

            Generator generator = new Generator();
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
                    .octavesNumber(octavesNumber)
                    .metreInNumbers(initializer.getMetreInNumbers())
                    .measuresNumber(initializer.getMeasuresNumber())
                    .build();

            formatter.format();
            generator.saveXMLFile(formatter.getDocument());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in call method");
            //System.out.println(e);
            //e.printStackTrace();
        }
        return null;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new IEEE1599App()).execute(args));
    }
}
