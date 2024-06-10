package aguDataSystem.server.http.controllers.models.output.dno

import aguDataSystem.server.domain.company.DNO

/**
 * Represents the output model for a list of [DNO]s
 *
 * @property dnos The list of [DNO]s
 * @property size The size of the list
 */
data class DNOListOutputModel(
    val dnos: List<DNOOutputModel>,
    val size: Int
) {
    constructor(dnos: List<DNO>) : this(
        dnos = dnos.map { DNOOutputModel(it) },
        size = dnos.size
    )
}