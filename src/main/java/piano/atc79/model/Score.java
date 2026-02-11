package piano.atc79.model;

public class Score {
    private int totalPoints;
    private int successfulLandings;
    private int successfulTakesOff;

    public Score() {
        totalPoints = 0;
        successfulLandings = 0;
        successfulTakesOff = 0;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getSuccessfulLandings() {
        return successfulLandings;
    }

    public int getSuccessfulTakesOff() {
        return successfulTakesOff;
    }

    public void addLanding(int points) {
        this.successfulLandings++;
        this.totalPoints += points;
    }

    public void addTakeOff(int points) {
        this.successfulTakesOff++;
        this.totalPoints += points;
    }
}
