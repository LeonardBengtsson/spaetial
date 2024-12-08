package spaetial.util.sound;

public final class MusicUtil {
    private MusicUtil() {}

    private static final double ONE_TWELFTH = 1d / 12;

    /**
     * Maps the scale degrees of a major musical key to how many half-steps above the tonic they are
     */
    private static final int[] MAJOR_TO_CHROMATIC_MAP = new int[] { 0, 2, 4, 5, 7, 9, 11 };

    /**
     * Maps the scale degrees of a (natural) minor musical key to how many half-steps above the tonic they are
     */
    private static final int[] MINOR_TO_CHROMATIC_MAP = new int[] { 0, 2, 3, 5, 7, 8, 10 };

    /**
     * @param referencePitch The pitch (in Hz) of the "tonic" of the key
     * @param halfSteps      A note, specified in the distance in half steps from the reference note
     * @return The pitch (in Hz) of the note
     */
    public static double pitchChromatic(double referencePitch, int halfSteps) {
        return referencePitch * Math.pow(2, halfSteps * ONE_TWELFTH);
    }

    /**
     * @param tonicPitch The pitch of the tonic of the key
     * @param degree     The degree of a note in the major key starting on the specified tonic, minus 1. That is, Degree V -> {@code degree = 4} and so on
     * @return The pitch of the note
     */
    public static double pitchMajorScale(double tonicPitch, int degree) {
        int octave = degree / 7;
        return tonicPitch * Math.pow(2, MAJOR_TO_CHROMATIC_MAP[degree % 7] * ONE_TWELFTH + octave);
    }

    /**
     * @param tonicPitch The pitch of the tonic of the key
     * @param degree     The degree of a note in the (natural) minor key starting on the specified tonic, minus 1. That is, Degree iii -> {@code degree = 2} and so on
     * @return The pitch of the note
     */
    public static double pitchMinorScale(double tonicPitch, int degree) {
        int octave = degree / 7;
        return tonicPitch * Math.pow(2, MINOR_TO_CHROMATIC_MAP[degree % 7] * ONE_TWELFTH + octave);
    }
}
