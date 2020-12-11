import java.util.*;

// A MelodyGenerator can produce valid melodies and accompanying
// harmonic progressions from one of a set of optional durations.
//   Has a melody - a list of numbers representing the notes of a melody
// which are relative (diatonic) pitches from a starting pitch of 0
// (representing scale degree 1 in whatever key, or "do").
// Ex. in C major, a melody [0 1 4 -1 0] would be C, D, G, B, C.
//   Also has a progression - a list of numbers representing the chords of a
// harmonic progression, using numbers from 1 to 7 representing triad
// chords built on that scale-degree, in root position.
// Ex. in C major, a progression [1 6 2 5 1] would represent
// C triad (CEG), Am (ACE), Dm (DFA), G (GBD), C (CEG).
public class MelodyGenerator {
    
    private final Random random = new Random();
    private final ArrayList<Integer> progression = new ArrayList<Integer>();
    private final ArrayList<Integer> melody = new ArrayList<Integer>();
    // motionOptions is a quota of melodic intervals allowed for a certain length melody
    private final Map<Integer, Integer> motionOptions = new HashMap<Integer, Integer>();

    // progressionRules contains the legal harmonic grammar from one chord to the next
    private static final Map<Integer, Integer[]> progressionRules = new HashMap<>();
    static {
        progressionRules.put(1, new Integer[]{1, 2, 3, 4, 5, 6, 7});
        progressionRules.put(2, new Integer[]{5, 7});
        progressionRules.put(3, new Integer[]{6});
        progressionRules.put(4, new Integer[]{5, 7});
        progressionRules.put(5, new Integer[]{1});
        progressionRules.put(6, new Integer[]{2, 4});
        progressionRules.put(7, new Integer[]{1});
    }

    // Constructs a MelodyGenerator given a duration choice of 6, 8, 9, 12, or 16.
    // If an invalid duration is somehow chosen, throws IllegalArgumentException.
    // Generates the progression and melody for the given duration.
    public MelodyGenerator(int duration) {

        // this exception shouldn't happen, as users are only given limited duration choices
        if (duration != 6 && duration != 8 && duration != 9 && duration != 12 && duration != 16) {
            System.out.println("Something went wrong! Duration must be 6, 8, 9, 12, or 16.");
            throw new IllegalArgumentException();
        }

        // set our desired motionOptions for each possible duration
        if (duration == 6) {
            motionOptions.put(0, 1);
            motionOptions.put(1, 20);
            motionOptions.put(2, 1);
        } else if (duration == 8 || duration == 9) {
            motionOptions.put(0, 2);
            motionOptions.put(1, 20);
            motionOptions.put(2, 2);
        } else if (duration == 12 || duration == 16) {
            motionOptions.put(0, 3);
            motionOptions.put(1, 20);
            motionOptions.put(2, 3);
        }
        motionOptions.put(3, 1);

        generateProgression(duration);
        generateMelody();
    }

    // Generate a valid chord progression for a set duration.
    private void generateProgression(int duration) {
        // continue generating random progressions until we find a valid solution
        do {
            progression.clear();
        } while (!generateProgression(1, duration));
    }

    private boolean generateProgression(int currentChord, int duration) {

        //add the current chord to the progression and decrement the remaining duration
        //the first chord is always a 1 chord by default
        progression.add(currentChord);
        duration--;

        //if we didn't find a good progression for the requested duration, start again
        if (duration < 0) {
            return false;
        }

        //check if we reached the right duration with a 5 -> 1 progression at the cadence
        if (duration == 0 && (progression.get(progression.size()-1) == 1)
                          && (progression.get(progression.size()-2) == 5)) {
            return true;
        }

        //randomly choose a legal next chord from our progression rules
        Integer[] options = progressionRules.get(currentChord);
        return generateProgression(options[random.nextInt(options.length)], duration);

    }

    // Generate a melody that matches our existing progression.
    private void generateMelody() {

        // stop generating notes when we are finished
        if (melody.size() == progression.size()) {
            return;
        }

        // begin with the starting pitch
        if (melody.size() == 0) {
            melody.add(0);

        } else {

            // store our current pitch and our upcoming chord
            int currentPitch = melody.get(melody.size()-1);
            int nextChord = progression.get(melody.size());
            ArrayList<Integer> nextPitchOptions = new ArrayList<Integer>();

            // find the valid in-range octave for each of the 3 possible notes in the next chord
            for (int i = 0; i < 3; i++) {
                int scaleDegree = nextChord - 1 + (2 * i);
                int minPossiblePitch = 0;
                if (currentPitch <= scaleDegree + 3) {
                    minPossiblePitch = currentPitch - 3;
                } else { //if (currentPitch > scaleDegree + 3)
                    minPossiblePitch = currentPitch + 3;
                }
                nextPitchOptions.add(minPossiblePitch - ((minPossiblePitch - scaleDegree) % 7));
            }

            int nextPitchChoice = 0;
            int motion = 0;

            // randomly try picking one of our 3 note options until we find one that is valid
            while (!nextPitchOptions.isEmpty()) {
                // keep track of which note option we are evaluating
                int index = random.nextInt(nextPitchOptions.size());
                nextPitchChoice = nextPitchOptions.get(index);
                // find the interval between our current note and this next note choice
                motion = Math.abs(nextPitchChoice - currentPitch);

                // exit if we found a legal motion, otherwise remove this option from our random options
                if (motionOptions.containsKey(motion)) {
                    break;
                } else {
                    nextPitchOptions.remove(index);
                }
            }        

            // if there are no more legal options, break the rules and use the closest pitch option
            if (!motionOptions.containsKey(motion)) {
                for (int i = 0; i < nextPitchOptions.size(); i++) {
                    motion = Math.abs(nextPitchOptions.get(i) - currentPitch);
                    if (motion <= 2) {
                        nextPitchChoice = nextPitchOptions.get(i);
                    }
                }
            } else { // using a legal random pitch choice, decrement the motion quota as normal
                motionOptions.put(motion, motionOptions.get(motion) - 1);
                if (motionOptions.get(motion) <= 0) {
                    motionOptions.remove(motion);
                }
            }

            // add the pitch
            melody.add(nextPitchChoice);
            
        }    

        // recursively generate the next note
        generateMelody();
    }

    // Returns the progression for this MelodyGenerator.
    public final ArrayList<Integer> getProgression() {
        return progression;
    }

    // Returns the melody for this MelodyGenerator.
    public final ArrayList<Integer> getMelody() {
        return melody;
    }
}