package aguDataSystem.server.http.controllers.models.output.provider

import aguDataSystem.server.domain.measure.GasMeasure

/**
 * Output model for GasMeasureList
 *
 * @param gasMeasures The list of GasMeasureOutputModel
 * @param size The size of the list
 */
data class GasMeasureListOutputModel(
	val gasMeasures: List<GasMeasureOutputModel>,
	val size: Int
) {
	constructor(gasMeasures: List<GasMeasure>) : this(
		gasMeasures = gasMeasures.map { measure -> GasMeasureOutputModel(measure) },
		size = gasMeasures.size
	)
}
