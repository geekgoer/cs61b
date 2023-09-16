package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHeroLite {
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
//        GuitarString stringA = new GuitarString(CONCERT_A);
//        GuitarString stringC = new GuitarString(CONCERT_C);
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] gtstring = new GuitarString[37];
        for(int i =0 ;i < 37;i++)
            gtstring[i] = new GuitarString(440 * Math.pow(2,(i-24)/12));
        while (true) {

            /* check if the user has typed a key; if so, process it */
            int idx = 0;
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
//                if (key == 'a') {
//                    stringA.pluck();
//                } else if (key == 'c') {
//                    stringC.pluck();
//                }
                idx = keyboard.indexOf(key);
                System.out.println(idx);
                gtstring[idx].pluck();
            }

            /* compute the superposition of samples */
            double t = -1,sample=-1;
            if(t == -1) {
                t = gtstring[idx].sample();
                sample = gtstring[idx].sample();
            }
            else{
                sample = t + gtstring[idx].sample();
                t = gtstring[idx].sample();
            }
            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
//            stringA.tic();
//            stringC.tic();
            gtstring[idx].tic();
        }
    }
}

