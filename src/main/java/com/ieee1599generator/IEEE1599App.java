package com.ieee1599generator;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 *
 * @author federica
 */
@Command(mixinStandardHelpOptions = true)
public class IEEE1599App implements Callable<Void> {

    private static final Logger LOGGER = Logger.getLogger(IEEE1599App.class.getName());

    @Option(names = {"--creator"}, description = "document creator name", interactive = true, echo = true)
    private String creator;
    
    private double docVersion = 1.0;

    @Option(names = {"--title"}, defaultValue = "Title", description = "piece title (default: ${DEFAULT-VALUE})")
    private String title;

    @Option(names = {"--author"}, defaultValue = "Author", description = "piece author (default: ${DEFAULT-VALUE})")
    private String author;

    @Option(names = {"--track-length"}, description = "track length in minutes", interactive = true, echo = true)
    private double trackLength;

    @Option(names = {"--bpm"}, description = "time in bpm", interactive = true, echo = true)
    private int bpm;

    @Option(names = {"--metre"}, description = "metre", interactive = true, echo = true)
    private String metre;

    @Option(names = {"--instruments-number"}, description = "number of instruments", interactive = true, echo = true)
    private int instrumentsNumber;

    @Option(names = {"--max-notes-number"}, description = "maximum number of played notes", interactive = true, echo = true)
    private int maxNumberOfPlayedNotes;

    // da mettere interactive
    @Option(names = {"--min-duration"}, split = ",", description = "minimum duration of musical figures", echo = true)
    private int[] minDuration;

    // da mettere interactive
    @Option(names = {"--max-duration"}, split = ",", description = "maximum duration of musical figures", echo = true)
    private int[] maxDuration;

    @Option(names = {"--min-height"}, description = "minimum heigth of musical figures", interactive = true, echo = true)
    private int minHeight;

    @Option(names = {"--max-height"}, description = "maximum heigth of musical figures", interactive = true, echo = true)
    private int maxHeight;

    @Option(names = {"--max-notes-number-chord"}, description = "maximum number of notes in a chord", interactive = true, echo = true)
    private int maxNumberOfNotesInAChord;

    @Option(names = "--irregular-groups", interactive = true, echo = true)
    private boolean areIrregularGroupsPresent;

    @Option(names = {"--min-delay"}, description = "minimum delay in VTU after which the next note will sound", interactive = true, echo = true)
    private int minimumDelay;

    private List<Character> clefs = List.of('G', 'F', 'C');

    private List<Integer> clefsSteps = List.of(2, 4, 6);

    private Map<Integer, String> pitchesMap = new TreeMap<Integer, String>() {
        {
            put(0, "C");
            put(1, "C-sharp");
            put(2, "D");
            put(3, "D-sharp");
            put(4, "E");
            put(5, "F");
            put(6, "F-sharp");
            put(7, "G");
            put(8, "G-sharp");
            put(9, "A");
            put(10, "B-sharp");
            put(11, "B");
        }
    };

    private int octavesNumber;

    @Option(names = {"--seed"}, defaultValue = "1234", description = "seed for random object (default: ${DEFAULT-VALUE})")
    private long seed;

    @Override
    public Void call() throws Exception {
        try {
            Initializer initializer = new Initializer(trackLength, metre, bpm, minDuration, maxDuration);

            Formatter formatter = FormatterBuilder.newBuilder()
                    .seed(seed)
                    .creator(creator)
                    .docVersion(docVersion)
                    .title(title)
                    .author(author)
                    .instrumentsNumber(instrumentsNumber)
                    .maxNumberOfPlayedNotes(maxNumberOfPlayedNotes)
                    .minDuration(minDuration)
                    .minHeight(minHeight)
                    .maxHeight(maxHeight)
                    .maxNumberOfNotesInAChord(maxNumberOfNotesInAChord)
                    .areIrregularGroupsPresent(areIrregularGroupsPresent)
                    .minimumDelay(minimumDelay)
                    .clefs(clefs)
                    .clefsSteps(clefsSteps)
                    .pitchesMap(pitchesMap)
                    .octavesNumber(octavesNumber)
                    .metreInNumbers(initializer.getMetreInNumbers())
                    .measuresNumber(initializer.getMeasuresNumber())
                    .notesMap(initializer.getNotesMap())
                    .maxNumberOfEvents(initializer.getMaxNumberOfEvents())
                    .irregularGroupsMap(initializer.getIrregularGroupsMap())
                    .build();

            Generator generator = new Generator();
            generator.saveXMLFile(formatter.getDocument());

        } catch (Exception e) {
            LOGGER.info("Can cause Exception");
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new IEEE1599App()).execute(args));
    }
}
