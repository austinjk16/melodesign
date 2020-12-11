import jm.JMC;
import jm.music.data.*;
import jm.util.*;
import jm.midi.*;
import java.util.*;
import java.io.*;
import jm.audio.*;

public class Melodesign implements JMC {

    public static void main(String[] args) throws FileNotFoundException {
        Boolean cont = true;
        System.out.println("Welcome to Melodesign, this program will create a melody for you!");
        while(cont) {
            Scanner console = new Scanner(System.in);
            System.out.println("Input the duration you want the melody to be.");
            System.out.println("Duration must be 6, 8, 9, 12, or 16.");
            int duration = console.nextInt();
            boolean result = check(duration);
            while(!result) {
                System.out.println("Duration must be 6, 8, 9, 12, or 16.");
                duration = console.nextInt();
                result = check(duration);
            }
            
            MelodyGenerator melodyGenerator = new MelodyGenerator(duration);
            ToMidi toMidi = new ToMidi();
            ArrayList<Integer> melody = melodyGenerator.getMelody();
            ArrayList<Integer> progression = melodyGenerator.getProgression();
            
            System.out.println("Input the desired root note of the melody");
            System.out.println("Must be written with a capital letter, followed by a #, b " +
                                                                   " or nothing (ex: C#)");
            String rootNote = console.next();
            while (!toMidi.rootNotes().containsKey(rootNote)) {
                System.out.println("Try again, root note must be written with a capital " +
                                           "letter, followed by a #, b or nothing (ex: C#)");
                rootNote = console.next();
            }
            System.out.println("Input the desired mode/scale of the melody");
            String mode = console.next();
            while (!toMidi.modes().containsKey(mode)) {
                System.out.println("Try again, mode/scale not found in modes.txt");
                mode = console.next();
            } 
            System.out.println("Input how many beats long you would like each note to be");
            double rythym = console.nextDouble();

            Phrase melodyScore = toMidi.melodyScore(rootNote, mode, melody, rythym);
            Phrase progressionScore = toMidi.progressionScore(rootNote, mode, progression, rythym); 

            System.out.println("melody: " + melodyGenerator.getMelody());
            System.out.println("progression: " + melodyGenerator.getProgression());
            
            System.out.println("Playing melody...");
            Score score = new Score("Melody Score");
            score.addPart(new Part(melodyScore));
            Play.midi(score);
            
            System.out.println("Would you like to download the midi files to the local directory? (y/n)");
            String saveAnswer = console.next();
            if (saveAnswer.equals("y")) {
                System.out.println("Input the desired name of the melody midi file (ending in .mid)");
                Write.midi(melodyScore, console.next());
                System.out.println("Input the desired name of the progression midi file (ending in .mid)");
                Write.midi(progressionScore, console.next());
            }
      
            System.out.println("Would you like to run again? (y/n)");
            String yesOrNo = console.next();
            if(yesOrNo.equals("n")) {
                cont = false;
            }
        }
        System.out.println("Thank you for using Melodesign!");
    }

    // This method checks whether the duration entered is 6, 8, 9, 12, or 16.
    private static boolean check(int duration) {
        if(duration == 6 || duration == 8 || duration == 9 || duration == 12 || duration == 16) {
            return true;
        }
        return false;
    }
}        
        
        
        
        
        
        
        
        
        
        
        
        
        
      
        
        
        
   