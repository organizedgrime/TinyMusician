public class Note {
	Tones tone;
	Rhythm length;
	int volume;
	
	public Note(Tones _tone, Rhythm r, int v){
		this.tone = _tone;
		this.length = r;
		this.volume = v;
	}
}
