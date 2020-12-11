import jm.JMC;
import jm.music.data.*;
import jm.util.*;
import jm.midi.*;
import java.util.*;
import java.io.*;

// A ToMidi class converts melodies and chord progressions into midi files

public class ToMidi implements JMC {
    // A map of all the modes available
    private Map<String, ArrayList<Integer>> modes;

    // A map of all the root notes
    private Map<String, Integer> rootNotes;
    
    // A map of all the chord changes per mode
    private Map<String, ArrayList<String>> chordChanges;
    
    // A map of the basic chord rules
    private Map<String, int[]> chordRules;

    // The desired octave you want the root note of the melody to start on
    public static final int MELODY_OCTAVE = 6;

    // The desired octave you want the root note of the progression to start on
    public static final int PROGRESSION_OCTAVE = 4;

    // Creates a new ToMidi object
    public ToMidi() throws FileNotFoundException {
        this.modes = createModes("data\\modes.txt");
        this.rootNotes = createRootNotes("data\\notes.txt");
        this.chordChanges = createChordChanges("data\\chordchanges.txt");
        this.chordRules = new HashMap<String, int[]>();
        chordRules.put("maj", new int[]{0, 4, 7});
        chordRules.put("min", new int[]{0, 3, 7}); 
        chordRules.put("dim", new int[]{0, 3, 6});
    }

    // Behavior : Creates a melody score out of the given key, mode and melody.
    // Parameters : String key : The root note of the melody
    // String mode : The mode of the desired melody
    // ArrayList<Integer> melody : The distance of each note in the melody from the root note.
    // double rv : how many beats you would like each note to last.
    // Exceptions : Will not produce a melody if the key or mode does not exist. Root notes should
    // be written in a capital letter (followed by a b or # for flat or sharp if desired).
    // Return : Returns a Score object of the melody, that can be converted to midi.
    public Phrase melodyScore(String key, String mode, ArrayList<Integer> melody, double rv) {
        Phrase phr = new Phrase();
        if (!rootNotes.containsKey(key) || !modes.containsKey(mode.toLowerCase())) {   
            System.out.println("Something went wrong! Root node or mode does not exist");
            throw new IllegalArgumentException();
        }
        int[] scale = createScale(MELODY_OCTAVE, rootNotes.get(key), modes.get(mode.toLowerCase()));
        for (int i = 0; i < melody.size(); i++) {
            phr.addNote(new Note(melodyHelper(melody, scale, i), rv));
        }
        return phr;
    }

    // Behavior : Creates a progression score out of the given key, mode and progression.
    // Parameters : String key : The root note of the melody
    // String mode : The mode of the desired melody
    // ArrayList<Integer> melody : The distance of each note in the melody from the root note.
    // Exceptions : Will not produce a melody if the key or mode does not exist. Root notes should
    // be written in a capital letter (followed by a b or # for flat or sharp if desired).
    // Return : Returns a Score object of the melody, that can be converted to midi.
    public Phrase progressionScore(String key, String mode, ArrayList<Integer> progression, double rv) {
        Phrase phr = new Phrase();
        if (!rootNotes.containsKey(key) || !modes.containsKey(mode.toLowerCase())) {   
            System.out.println("Something went wrong! Root node or mode does not exist");
            throw new IllegalArgumentException();
        }
        ArrayList<String> targetChords = chordChanges.get(mode);
        int[] scale = createScale(PROGRESSION_OCTAVE, rootNotes.get(key), modes.get(mode.toLowerCase()));
        
        for (int i = 0; i < progression.size(); i++) {
            int target = progression.get(i) - 1;
            int baseNote = scale[target];
            int[] currentChord = chordRules.get(targetChords.get(target));
            int[] completeChord = new int[]{baseNote + currentChord[0], baseNote + currentChord[1],
                                            baseNote + currentChord[2]};
            phr.addChord(completeChord, rv);
        }
        return phr;
         
    }
    
    // Takes the phrases that were made with progressionScore or melodyScore, and downloads it
    // locally to the local directory as a MIDI file.
    public void downloadMidi(Phrase phr) {
        Write.midi(phr);
    }

    // Finds the numerical value of each note as a distance form C-2.
    private int melodyHelper(ArrayList<Integer> melody, int[] scale, int i) {
        int target = melody.get(i);
        int track = 0;
        while (target < 0) {
            target += scale.length - 1;
            track--;     
        }
        while (target >= scale.length - 1) {
            target -= scale.length - 1;
            track++;
        }
        return scale[target] + track * 12;
    }

    // Creates the scale out of the given mode and root note.
    private int[] createScale(int octave, int rootNote, ArrayList<Integer> targetMode) {
        int[] scale = new int[targetMode.size()];
        for (int i = 0; i < targetMode.size(); i++) {
            scale[i] = (rootNote + 12 * octave) + targetMode.get(i);
        }
        return scale;
    }

    // Behavior : Initializes the map of modes that the user can choose from.
    // Parameters : String file : The file that stores the list of modes.
    // Exceptions : Will throw a FileNotFoundException if the file does not exist.
    private Map<String, ArrayList<Integer>> createModes(String file) throws FileNotFoundException {
        Map<String, ArrayList<Integer>> tempModes = new HashMap<String, ArrayList<Integer>>();
        Scanner modeScan = new Scanner(new File(file));
        while (modeScan.hasNextLine()) {
            Scanner line = new Scanner(modeScan.nextLine());
            String name = line.next();
            ArrayList<Integer> notes = new ArrayList<Integer>();
            while (line.hasNextInt()) {
                notes.add(line.nextInt());
            }
            tempModes.put(name, notes);
        }
        return tempModes;
    }

    // Behavior : Initializes the map of root notes that the user can choose from.
    // Parameters : String file : The file that stores the list of root notes.
    // Exceptions : Will throw a FileNotFoundException if the file does not exist.
    private Map<String, Integer> createRootNotes(String file) throws FileNotFoundException {
        Map<String, Integer> tempNotes = new HashMap<String, Integer>();
        Scanner noteScan = new Scanner(new File(file));
        while (noteScan.hasNextLine()) {
            Scanner line = new Scanner(noteScan.nextLine());
            String note = line.next();
            int value = line.nextInt();
            tempNotes.put(note, value);
        }
        return tempNotes;
    }
    
    // Behavior : Initializes the map of the chords in each mode
    // Parameters : String file : The file that stores the list of modes changes.
    // Exceptions : Will throw a FileNotFoundException if the file does not exist.
    private Map<String, ArrayList<String>> createChordChanges(String file) throws FileNotFoundException {
        Map<String, ArrayList<String>> tempChords = new HashMap<String, ArrayList<String>>();
        Scanner modeScan = new Scanner(new File(file));
        while (modeScan.hasNextLine()) {
            Scanner line = new Scanner(modeScan.nextLine());
            String name = line.next();
            ArrayList<String> changes = new ArrayList<String>();
            while (line.hasNext()) {
                changes.add(line.next());
            }
            tempChords.put(name, changes);
        }
        return tempChords;
    }
    // returns the map of the root notes.
    public Map<String, Integer> rootNotes() {
        return this.rootNotes;
    }
    
    // Returns the map of modes.
    public Map<String, ArrayList<Integer>> modes() {
        return this.modes;
    }
}
