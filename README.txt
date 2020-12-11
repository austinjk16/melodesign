	Thank you for using melodesign, and our associated libraries! 
This user guide will walk you through how to use each of the classes associated with melodesign,
and how you can modify it for your own liking. 
Also, here is a video guide to using the program : https://youtu.be/oOt0edVBnNU

	The main method of melodesign is stored in Melodesign.java. If you run this program, 
it will ask you a variety of questions about the melody you want to produce, including the 
desired root note, mode/scale, number of notes/chords, and the duration of each note in the 
melody (which can be entered as any double). If you misenter any of these answers (meaning 
that the given root note or mode/scale does not exist), it will allow you to re-enter the 
value. Once you have entered each value, it will generate the melody and an associated 
progression. First it will display the melody and chord progression as an ArrayList of 
integers. For the melody, each integer represents the distance from the root 
note and for the progression, each integer represents the position of the chord in the scale.
 The program will play the melody to you, and allow you to download the melody and progression
 as midi files. It will prompt you for the name of the midi files, and if it does not end in 
.mid, it will not save correctly. The files will be available in the same directory that the 
java files are in. You can repeat the program as many times as you want.

	MelodyGenerator is the base algorithm to the program, and is what produces the Lists 
of ints that describe the melody and progression. To create a new melody and chord progression, 
all you have to do is create a new MelodyGenerator object and use the methods getMelody() and 
getProgression(). It is important to note that calling getMelody() and getProgression does NOT 
create a new melody or progression. If you want to create a new melody or progression, you 
will have to make a new MelodyGenerator object, and use getMelody() and getProgression() on 
that new object. getMelody() and getProgression() both return ArrayList<Integer>.

	ToMidi works with the melodies produced in MelodyGenerator and with the jMusic 
libraries to convert the previously made melodies and progressions into midi files. ToMidi 
reads text files to define the possible root notes, modes, and chord changes for those modes. 
To add a new mode, write the name of the mode in modes.txt under the data folder, followed 
by the notes of the mode in integer notation. With that, you also need to go to
chordchanges.txt in the data folder and write the name of the mode exactly the same, with the 
relative major(written as maj), minor (written as min), and diminished chords(written as dim) 
for each note in that mode in order. The main methods in ToMidi are melodyScore and 
progressionScore. Both of these methods accept the same parameters: The key as a String 
(example C#, or Bb or D), the mode as a String, the ArrayList<Integer> of the melody for 
melodyScore or the progression for progressionScore, and the how many beats each note is 
played for, as a double. They are input in that order. Both methods are returned as a Phrase 
object, which is from the jMusic libraries to store musical data. To get the midi files 
downloaded from these, call downloadMidi and input the phrase as a parameter. The ToMidi class 
will also return the Map<String, Integer> of root notes by using the rootNotes() method, and 
the Map<String, ArrayList<Integer>> of modes by using the modes() method.
