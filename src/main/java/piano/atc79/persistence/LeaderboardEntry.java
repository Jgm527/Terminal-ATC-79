package piano.atc79.persistence;

/**
 * DTO que representa una entrada en el leaderboard.
 */
public class LeaderboardEntry {
    private final String alias;
    private final int score;
    private final int landings;
    private final int streakMax;
    private final int durationSeconds;
    private final String gameOverCause;
    private final String completedAt;

    public LeaderboardEntry(String alias, int score, int landings,
                            int streakMax, int durationSeconds,
                            String gameOverCause, String completedAt) {
        this.alias = alias;
        this.score = score;
        this.landings = landings;
        this.streakMax = streakMax;
        this.durationSeconds = durationSeconds;
        this.gameOverCause = gameOverCause;
        this.completedAt = completedAt;
    }

    public String getAlias() { return alias; }
    public int getScore() { return score; }
    public int getLandings() { return landings; }
    public int getStreakMax() { return streakMax; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getGameOverCause() { return gameOverCause; }
    public String getCompletedAt() { return completedAt; }
}
