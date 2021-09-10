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

    @Option(names = {"--creator"}, description = "document creator name")
    private String creator;

    @Option(names = {"--doc-version"}, description = "document version")
    private double docVersion = 1.0;

    @Option(names = {"--title"}, description = "piece title")
    private String title = "Title";

    @Option(names = {"--author"}, description = "piece author")
    private String author = "Author";

    @Option(names = {"--track-length"}, description = "track length in minutes")
    private double trackLength;

    @Option(names = {"--bpm"}, description = "time in bpm")
    private int bpm;

    @Option(names = {"--metre"}, description = "metre")
    private String metre;

    @Option(names = {"--instruments-number"}, description = "number of instruments")
    private int instrumentsNumber;

    @Option(names = {"--max-notes-number"}, description = "maximum number of played notes")
    private int maxNumberOfPlayedNotes;

    @Option(names = {"--min-duration"}, description = "minimum duration of musical figures")
    private int[] minDuration;

    @Option(names = {"--max-duration"}, description = "maximum duration of musical figures")
    private int[] maxDuration;

    @Option(names = {"--min-height"}, description = "minimum heigth of musical figures")
    private int minHeight;

    @Option(names = {"--max-height"}, description = "maximum heigth of musical figures")
    private int maxHeight;

    @Option(names = {"--max-notes-number-chord"}, description = "maximum number of notes in a chord")
    private int maxNumberOfNotesInAChord;

    @Option(names = "-irregular-groups")
    private boolean areIrregularGroupsPresent;

    @Option(names = {"--min-delay"}, description = "minimum delay in VTU after which the next note will sound")
    private int minimumDelay;

    @Option(names = {"--clefs"}, description = "available clefs")
    private List<Character> clefs = List.of('G', 'F', 'C');

    @Option(names = {"--clefs-steps"}, description = "available clefs steps")
    private List<Integer> clefsSteps = List.of(2, 4, 6);

    @Option(names = {"--pitches"}, description = "pitches map")
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

    @Option(names = {"--octaves"}, description = "number of octaves")
    private int octavesNumber = 10;

    @Option(names = {"--seed"}, description = "seed for random object")
    private int seed = 10;

    public static void main(String[] args) {
        new CommandLine(new IEEE1599App()).execute(args);
    }

    @Override
    public Void call() throws Exception {
        try {
            Formatter formatter = new Formatter(new Initializer(this.trackLength, this.metre, this.bpm, this.minDuration, this.maxDuration), this.seed);
            formatter.execute(this.creator, this.docVersion, this.title, this.author, this.instrumentsNumber, this.clefs, this.clefsSteps, 
                    this.minHeight, this.maxHeight, this.maxNumberOfNotesInAChord, this.minimumDelay, this.areIrregularGroupsPresent, 
                    this.octavesNumber, this.pitchesMap);
            Generator generator = new Generator();
            generator.saveXMLFile(formatter.getDocument());
        } catch (Exception e) {
            LOGGER.info("Can cause Exception");
        }
        return null;
    }
}
