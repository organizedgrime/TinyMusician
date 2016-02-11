import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import javax.sound.midi.MidiChannel;

public class MidiTest {
	static ArrayList<Note> newMeasure;
	static int key = (int) (Math.random() * 100);
	static Random rand = new Random(key);
	static Rhythm times[] = new Rhythm[] { Rhythm.Whole, Rhythm.Half, Rhythm.Quarter, Rhythm.Eighth, Rhythm.Sixteenth };
	static Tones major[] = new Tones[] { Tones.C, Tones.D, Tones.E, Tones.F, Tones.G, Tones.A, Tones.B };
	static Tones minor[] = new Tones[] { Tones.C, Tones.D, Tones.DS, Tones.F, Tones.G, Tones.GS, Tones.AS };
	Tones dominant[] = new Tones[] { Tones.C, Tones.D, Tones.E, Tones.F, Tones.G, Tones.A, Tones.AS };

	static int[] weights = new int[major.length];

	static int volume = 127, /* between 0 and 127 */ beatsPerMeasure = 4, standardNote = 4;
	static double duration;

	public static void musicLoop() {
		// time signature top + booty
		System.out.println(key);

		// midi setup
		int channel = 0; // 0 = piano

		// in milliseconds
		int base = 60;// base note
		try {
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();

			MidiChannel[] channels = synth.getChannels();
			channels[channel].noteOn(60, 0); // C note
			Thread.sleep(20);
			channels[channel].noteOff(60, 0);
			// flush long note out

			// generate motifs
			ArrayList<Note> motifs[] = new ArrayList[4];
			for (int i = 0; i < motifs.length; i++) {
				motifs[i] = measureGen((int) (rand.nextDouble() * 3) + 1, 4, Rhythm.Half);
			}

			for (int i = 0; i < weights.length; i++) {// set all values of
														// weights to 1
				weights[i] = 1;
			}
			ArrayList<Note> measure;
			for (;;) {// plays music forever
				if (rand.nextDouble() < .5) {
					measure = measureGen(standardNote, 4, Rhythm.Whole);
					// System.out.println("NOT");
				} else {
					// System.out.println("MOTIF");
					ArrayList<Note> m = motifs[(int) (rand.nextDouble() * motifs.length)];
					measure = mutate(m);
				}
				channels[channel].noteOn(base - Tones.O.value + measure.get(0).tone.value, volume);
				for (int i = 0; i < measure.size(); i++) {// MEASURE
					channels[channel].noteOn(base + measure.get(i).tone.value, measure.get(i).volume); // C
																										// note
					videoController.readNote(measure.get(i).tone.value, measure.get(i).length.beat * duration,
							measure.get(0).tone.value);

					// System.out.println(duration);
					Thread.sleep((long) (measure.get(i).length.beat * duration));

					channels[channel].noteOff(base + measure.get(i).tone.value);
				}
				channels[channel].noteOff(base - Tones.O.value + measure.get(0).tone.value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Note> measureGen(int counts, int rhythmIndex, Rhythm biggest) {
		ArrayList<Note> measure = new ArrayList<Note>();
		measure.add(new Note(Tones.C, biggest, 90));
		if (counts < Math.pow(2, rhythmIndex) - 1) {
			for (int i = 0; i < counts; i++) {
				int j = randomNotX(measure, times[rhythmIndex]);
				// j is the index of splitter,
				measure.get(j);
				newMeasure = new ArrayList<Note>();
				for (int k = 0; k < measure.size(); k++) {// TODO:Fix and use
															// .set not recreate
															// whole thing
					if (k != j) {
						newMeasure.add(measure.get(k));
					} else {
						if (Arrays.asList(times).indexOf(measure.get(j).length) != 4) {
							int[] rando = randomNote();
							int firstIndex = rando[(int) (rand.nextDouble() * rando.length)];
							weights[firstIndex]++;
							int secondIndex = rando[(int) (rand.nextDouble() * rando.length)];
							weights[secondIndex]++;
							newMeasure.add(new Note(major[firstIndex],
									times[Arrays.asList(times).indexOf(measure.get(j).length) + 1],
									new int[] { 0, volume, volume, volume, volume, volume, volume,
											volume }[(int) (rand.nextDouble() * 8)]));

							newMeasure.add(new Note(major[secondIndex],
									times[Arrays.asList(times).indexOf(measure.get(j).length) + 1],
									new int[] { 0, volume, volume, volume, volume, volume, volume,
											volume }[(int) (rand.nextDouble() * 8)]));
						}
					}
				}
				measure = newMeasure;
			}
		}
		return measure;
	}

	public static double BPMtoMPB(int bpm, int beatspermeasure) { // bpm to
																	// milliseconds
																	// per beat
		return 60000.0 * beatspermeasure / bpm;
	}

	public static ArrayList<Note> mutate(ArrayList<Note> n) {
		int r = (int) (rand.nextDouble() * 1);
		ArrayList<Note> newnotes = (ArrayList<Note>) n.clone();
		switch (r) {
		case 0:
			int rnote = (int) (rand.nextDouble() * n.size());
			newnotes.set(rnote, new Note(major[(int) (rand.nextDouble() * major.length)], n.get(rnote).length, 80));
			break;
		}
		return newnotes;
	}

	public static int randomNotX(ArrayList<Note> o, Rhythm not) {
		int finalCountdown = (int) (rand.nextDouble() * o.size());
		int count = 0;
		int i = 0;
		for (; count < finalCountdown; i = (i + 1) % o.size()) {
			if (o.get(i).length != not) {
				count++;
			}
			if (count == finalCountdown) {
				break;
			}
		}
		return i;
	}

	public static int[] randomNote() {
		int sum = IntStream.of(weights).sum();
		int modifiableWeights[] = weights.clone();
		int fin[] = new int[4];
		for (int i = 0; i < 4; i++) {
			double choice = rand.nextDouble();
			double cWeight = 0;
			for (int j = 0; j < modifiableWeights.length; j++) {
				if (cWeight > choice) {
					modifiableWeights[j] = 0;
					fin[i] = j;
					break;
				}
				cWeight += ((double) modifiableWeights[j]) / sum;
			}
		}
		return fin;
	}

	public static ArrayList<Note> complimentary(ArrayList<Note> original) {
		// System.out.println(newMeasure);
		// newMeasure
		newMeasure = new ArrayList<Note>();

		return null;
	}
}