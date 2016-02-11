public enum Rhythm {
	Whole(1.0),
	Half(1.0/2),
	Quarter(1.0/4),
	Eighth(1.0/8),
	Sixteenth(1.0/16);
	
	public double beat;
	Rhythm(double _beat){
	 this.beat=_beat;
	}
	public double dot(){
		return this.beat * 1.5;
	}
}
