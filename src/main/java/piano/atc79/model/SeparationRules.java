package piano.atc79.model;

/**
 * Reglas de separación horizontal/vertical entre pares de aeronaves.
 *
 * <p>Los umbrales están calibrados para la escala visual del radar
 * ({@code RadarCanvas.SCALE = 20} px/NM). El icono de aeronave es un triángulo
 * de ~12 px (0.6 NM) de alto × 8 px (0.4 NM) de ancho.</p>
 *
 * <ul>
 *   <li><b>Colisión:</b> ~50 % de solapamiento visual del icono en radar.
 *       Game over inmediato.</li>
 *   <li><b>TCAS (conflicto):</b> alerta de proximidad. Se dispara antes de
 *       que los iconos se toquen, dando tiempo al jugador para reaccionar.</li>
 * </ul>
 */
public class SeparationRules {

    // -----------------------------------------------------------------
    // Referencia visual
    //   RadarCanvas.SCALE = 20 px/NM
    //   Triángulo de aeronave: (~12 px, ~8 px) = (0.6 NM, 0.4 NM)
    // -----------------------------------------------------------------

    /** Separación vertical mínima para alerta TCAS (estándar 1 000 ft). */
    private static final int MIN_VERTICAL_SEPARATION = 1000;

    /**
     * Distancia horizontal para colisión: 0.25 NM ≈ 5 px.
     * A escala 20 px/NM, dos iconos centrados a 5 px se solapan ~50 %.
     * Suficiente para que el jugador vea dos figuras superpuestas y
     * no sienta que «se atraviesan».
     */
    private static final double COLLISION_HORIZONTAL_THRESHOLD_NM = 0.25;

    /**
     * Distancia vertical para colisión: 100 ft (~30 m).
     * Dos aeronaves separadas 100 ft o menos están efectivamente
     * en la misma altitud a efectos de impacto.
     */
    private static final int COLLISION_VERTICAL_THRESHOLD_FT = 100;

    /**
     * Separación horizontal para alerta TCAS en encuentros.
     * 1.5 NM ≈ 30 px, equivalente a ~2.5 cuerpos de aeronave.
     * Da margen para que el jugador emita un comando de evitación
     * antes de que los iconos lleguen a solaparse.
     */
    private static final double ENCOUNTER_SEPARATION_NM = 1.5;

    /**
     * Dos aeronaves se consideran en «aproximación» cuando sus rumbos
     * difieren más de 90° y menos de 270° (se cruzan o vienen de frente).
     * Fuera de ese rango viajan en dirección aproximadamente paralela.
     */
    private static final int ENCOUNTER_ANGLE_MIN = 90;
    private static final int ENCOUNTER_ANGLE_MAX = 270;

    // ---------------------------------------------------------------
    //  API pública
    // ---------------------------------------------------------------

    /**
     * Comprueba si dos vuelos están en conflicto (violan la separación mínima).
     *
     * <p>El conflicto se usa para alertas TCAS. Requiere proximidad tanto
     * horizontal como vertical. El umbral horizontal depende del tipo de
     * encuentro (aproximación o paralelo).</p>
     *
     * @param first  primer vuelo
     * @param second segundo vuelo
     * @return true si existe conflicto de separación
     */
    public boolean areInConflict(Flight first, Flight second) {
        double hDist = horizontalDistance(first, second);
        double vDist = verticalDistance(first, second);
        return hDist < minHorizontalSeparation(first, second)
                && vDist < MIN_VERTICAL_SEPARATION;
    }

    /**
     * Determina si dos vuelos han colisionado.
     *
     * <p>La colisión se detecta exclusivamente por distancia. A 0.25 NM
     * (5 px) de separación horizontal y 100 ft vertical, dos iconos de
     * aeronave se solapan visiblemente más de la mitad en el radar, con
     * independencia del rumbo relativo.</p>
     *
     * @param first  primer vuelo
     * @param second segundo vuelo
     * @return true si se considera colisión
     */
    public boolean areInCollision(Flight first, Flight second) {
        double hDist = horizontalDistance(first, second);
        double vDist = verticalDistance(first, second);
        return hDist <= COLLISION_HORIZONTAL_THRESHOLD_NM
                && vDist <= COLLISION_VERTICAL_THRESHOLD_FT;
    }

    // ---------------------------------------------------------------
    //  Métodos auxiliares
    // ---------------------------------------------------------------

    /**
     * Distancia horizontal entre dos vuelos (solo plano X, Y).
     */
    private static double horizontalDistance(Flight a, Flight b) {
        return a.getCurrentPosition().distanceTo(b.getCurrentPosition());
    }

    /**
     * Distancia vertical absoluta entre dos vuelos (diferencia de Z en ft).
     */
    private static int verticalDistance(Flight a, Flight b) {
        return Math.abs(a.getCurrentPosition().getZ() - b.getCurrentPosition().getZ());
    }

    /**
     * Distancia mínima de separación horizontal según el tipo de encuentro.
     *
     * <ul>
     *   <li>Si los rumbos son de aproximación (diferencia 90°-270°),
     *       se usa {@link #ENCOUNTER_SEPARATION_NM}.</li>
     *   <li>Si los rumbos son paralelos o divergentes, se aplica la
     *       separación por categoría (LIGHT 3 NM, MEDIUM 4 NM, HEAVY 6 NM).</li>
     * </ul>
     *
     * @return separación mínima en millas náuticas
     */
    private static double minHorizontalSeparation(Flight first, Flight second) {
        int diff = angleDifference(first.getHeading(), second.getHeading());
        return isApproaching(diff)
                ? ENCOUNTER_SEPARATION_NM
                : Math.max(
                        first.getModel().getCategory().getMinSeparationNM(),
                        second.getModel().getCategory().getMinSeparationNM()
                  );
    }

    /**
     * Dos aeronaves se aproximan si la diferencia angular entre sus rumbos
     * está en el rango ({@value #ENCOUNTER_ANGLE_MIN}°, {@value #ENCOUNTER_ANGLE_MAX}°).
     * Eso cubre tráfico de frente, cruzado y convergente.
     */
    private static boolean isApproaching(int angleDiff) {
        return angleDiff > ENCOUNTER_ANGLE_MIN && angleDiff < ENCOUNTER_ANGLE_MAX;
    }

    /**
     * Diferencia angular mínima entre dos rumbos (valor entre 0 y 180).
     * Normaliza el ángulo para obtener siempre el arco más corto.
     *
     * <p>Ejemplo: heading 350° y 10° → diff = 20° (no 340°).
     */
    private static int angleDifference(int heading1, int heading2) {
        int diff = Math.abs(heading1 - heading2) % 360;
        return Math.min(diff, 360 - diff);
    }
}
